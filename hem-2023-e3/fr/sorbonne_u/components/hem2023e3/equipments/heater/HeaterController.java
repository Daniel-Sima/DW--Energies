package fr.sorbonne_u.components.hem2023e3.equipments.heater;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.Heater.HeaterState;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorRequiredPullCI;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorOfferedPullCI;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterActuatorConnector;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterActuatorOutboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterSensorDataConnector;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterSensorDataOutboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterCompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterMeasureI;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterStateMeasure;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ThermostatedHeaterController</code> implements a controller
 * component for the thermostated heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The controller is a simple fixed period threshold-based controller with
 * hysteresis. It has two control modes. In a pull mode, it calls the pull
 * sensors of the heater to get the target and current temperatures. In the
 * push mode, it sets the period for the heater to push the temperatures data
 * towards it and perform once its control decision upon each reception. It
 * also uses a push pattern to receive changes in the state of the heater. For
 * example, when the heater is switched on, it sends a state data telling the
 * controller that it is now on so that the controller can begins its control
 * until the heater is switched off.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code hysteresis > 0.0}
 * invariant	{@code controlPeriod > 0}
 * invariant	{@code accFactor > 0.0}
 * invariant	{@code !isUnderTest || clockURI != null && !clockURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code STANDARD_HYSTERESIS > 0.0}
 * invariant	{@code STANDARD_CONTROL_PERIOD > 0}
 * </pre>
 * 
 * <p>Created on : 2022-10-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@RequiredInterfaces(required={HeaterSensorRequiredPullCI.class,
							  HeaterActuatorCI.class,
							  ClocksServerCI.class})
@OfferedInterfaces(offered={HeaterSensorOfferedPullCI.class})
//-----------------------------------------------------------------------------
public class			HeaterController
extends		AbstractComponent
implements	HeaterPushImplementationI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	public static enum	ControlMode {
		PULL,
		PUSH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, some methods trace their actions.						*/
	protected static boolean		VERBOSE = true;
	/** when true, some methods trace their actions.						*/
	public static boolean			DEBUG = true;

	/** the standard hysteresis used by the controller.						*/
	public static final double	STANDARD_HYSTERESIS = 0.1;
	/** standard control period in seconds.									*/
	public static final double	STANDARD_CONTROL_PERIOD = 60.0;

	/** URI of the sensor inbound port on the {@code ThermostatedHeater}.	*/
	protected String								sensorIBP_URI;
	/** URI of the actuator inbound port on the {@code ThermostatedHeater}.	*/
	protected String								actuatorIBPURI;
	/** sensor data outbound port connected to the {@code Heater}.			*/
	protected HeaterSensorDataOutboundPort			sensorOutboundPort;
	/** actuator outbound port connected to the {@code Heater}.				*/
	protected HeaterActuatorOutboundPort			actuatorOutboundPort;

	/** the actual hysteresis used in the control loop.						*/
	protected double								hysteresis;
	/* user set control period in seconds.									*/
	protected double								controlPeriod;
	protected final ControlMode						controlMode;
	/* actual control period, either in pure real time (not under test)
	 * or in accelerated time (under test), expressed in nanoseconds;
	 * used for scheduling the control task.								*/
	protected long									actualControlPeriod;
	/** the current state of the heater as perceived through the sensor
	 *  data received from the {@code ThermostatedHeater}.					*/
	protected HeaterState							currentState;
	/** lock controlling the access to {@code currentState}.				*/
	protected final Object							stateLock;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType					currentExecutionType;
	/** outbound port to connect to the centralised clock server.			*/
	protected ClocksServerOutboundPort				clockServerOBP;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String							clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected final CompletableFuture<AcceleratedClock>	clock;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI		URI of the heater sensor inbound port.
	 * @param actuatorIBP_URI	URI of the heater actuator inbound port.
	 * @throws Exception		<i>to do</i>.
	 */
	protected			HeaterController(
		String sensorIBP_URI,
		String actuatorIBP_URI
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI,
			 STANDARD_HYSTERESIS, STANDARD_CONTROL_PERIOD,
			 ControlMode.PULL);
	}

	/**
	 * create the controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * pre	{@code hysteresis > 0.0}
	 * pre	{@code controlPeriod > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI		URI of the heater sensor inbound port.
	 * @param actuatorIBP_URI	URI of the heater actuator inbound port.
	 * @param hysteresis		control hysteresis around the target temperature.
	 * @param controlPeriod		control period in seconds.
	 * @param controlMode		control mode: {@code PULL} or {@code PUSH}.
	 * @throws Exception 		<i>to do</i>.
	 */
	protected			HeaterController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI, hysteresis, controlPeriod,
			 ControlMode.PULL, ExecutionType.STANDARD, null);
	}

	/**
	 * create the controller component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sensorIBP_URI != null && !sensorIBP_URI.isEmpty()}
	 * pre	{@code actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()}
	 * pre	{@code hysteresis > 0.0}
	 * pre	{@code controlPeriod > 0}
	 * pre	{@code !currentExecutionType.isUnitTest()}
	 * pre	{@code currentExecutionType.isStandard() || clockURI != null && !clockURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sensorIBP_URI			URI of the heater sensor inbound port.
	 * @param actuatorIBP_URI		URI of the heater actuator inbound port.
	 * @param hysteresis			control hysteresis around the target temperature.
	 * @param controlPeriod			control period in seconds.
	 * @param controlMode			control mode: {@code PULL} or {@code PUSH}.
	 * @param currentExecutionType	current execution type for the next run.
	 * @param clockURI				URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception 			<i>to do</i>.
	 */
	protected			HeaterController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode,
		ExecutionType currentExecutionType,
		String clockURI
		) throws Exception
	{
		// two standard threads in case the thread that runs the method execute
		// can be prevented to run by the thread running receiveRunningState
		// the schedulable thread pool is used to run the control task
		super(2, 1);

		assert	sensorIBP_URI != null && !sensorIBP_URI.isEmpty() :
				new PreconditionException(
						"sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
		assert	actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty() :
				new PreconditionException(
					"actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()");
		assert	hysteresis > 0.0 :
				new PreconditionException("hysteresis > 0.0");
		assert	controlPeriod > 0 :
				new PreconditionException("controlPeriod > 0");
		assert	!currentExecutionType.isUnitTest() :
				new PreconditionException("!currentExecutionType.isUnitTest()");
		assert	currentExecutionType.isStandard() ||
									clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "clockURI != null && !clockURI.isEmpty()");

		this.sensorIBP_URI = sensorIBP_URI;
		this.actuatorIBPURI = actuatorIBP_URI;
		this.hysteresis = hysteresis;
		this.controlPeriod = controlPeriod;
		this.controlMode = controlMode;
		this.currentExecutionType = currentExecutionType;
		this.clockURI = clockURI;
		this.clock = new CompletableFuture<>();

		// just a common initialisation; if the run is in test mode, the
		// acceleration factor will be taken into account at start time
		// first convert to nanoseconds before casting to get a better
		// precision for fractional control periods
		this.actualControlPeriod =
				(long) (this.controlPeriod * TimeUnit.SECONDS.toNanos(1));
		this.stateLock = new Object();

		this.sensorOutboundPort =
				new HeaterSensorDataOutboundPort(this);
		this.sensorOutboundPort.publishPort();
		this.actuatorOutboundPort =
				new HeaterActuatorOutboundPort(this);
		this.actuatorOutboundPort.publishPort();

		if (VERBOSE || DEBUG) {
			this.tracer.get().setTitle("Heater controller component");
			this.tracer.get().setRelativePosition(2, 2);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.sensorOutboundPort.getPortURI(),
					sensorIBP_URI,
					HeaterSensorDataConnector.class.getCanonicalName());
			this.doPortConnection(
					this.actuatorOutboundPort.getPortURI(),
					this.actuatorIBPURI,
					HeaterActuatorConnector.class.getCanonicalName());

			synchronized (this.stateLock) {
				this.currentState = HeaterState.OFF;
			}
			
			this.traceMessage("Heater controller starts.\n");
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (!this.currentExecutionType.isStandard()) {
			this.clockServerOBP = new ClocksServerOutboundPort(this);
			this.clockServerOBP.publishPort();
			this.doPortConnection(
					this.clockServerOBP.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			AcceleratedClock clock =
					this.clockServerOBP.getClock(this.clockURI);
			this.doPortDisconnection(this.clockServerOBP.getPortURI());
			this.clockServerOBP.unpublishPort();
			// the accelerated period is in nanoseconds, hence first convert
			// the period to nanoseconds, perform the division and then
			// convert to long (hence providing a better precision than
			// first dividing and then converting to nanoseconds...)
			this.actualControlPeriod =
				(long)((this.controlPeriod * TimeUnit.SECONDS.toNanos(1))/
												clock.getAccelerationFactor());
			// release readers so that we make sure that actualControlPeriod
			// has also been properly set before
			this.clock.complete(clock);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.traceMessage("Heater controller ends.\n");
		this.doPortDisconnection(this.sensorOutboundPort.getPortURI());
		this.doPortDisconnection(this.actuatorOutboundPort.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.sensorOutboundPort.unpublishPort();
			this.actuatorOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * receive and process the state data coming from the heater component,
	 * starting the control loop if the state has changed from {@code OFF} to
	 * {@code ON}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sd		state data received from the heater component.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void			receiveDataFromHeater(DataRequiredCI.DataI sd)
	{
		assert	sd != null : new PreconditionException("sd != null");
		if (DEBUG) {
			this.traceMessage("receives heater sensor data: " + sd + ".\n");
		}

		HeaterMeasureI hm =
				((HeaterSensorData<HeaterMeasureI>)sd).getMeasure();
		if (hm.isStateMeasure()) {
			HeaterState hsd = ((HeaterStateMeasure)hm).getData();
			if (this.clock != null) {
				try {
					// make sure that the clock and the accelerated control
					// period have been initialised
					this.clock.get();
					// sanity checking, the standard Java scheduler has a
					// precision no less than 10 milliseconds...
					if (this.actualControlPeriod <
							TimeUnit.MILLISECONDS.toNanos(10)) {
						System.out.println(
								"Warning: accelerated control period is "
										+ "too small ("
										+ this.actualControlPeriod +
										"), unexpected scheduling problems may"
										+ " occur!");
					}
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}

			// the current state is always updated, but only in the case
			// when the heater is switched on that the controller begins to
			// perform the temperature control
			synchronized (this.stateLock) {
				HeaterState oldState = this.currentState;
				this.currentState = hsd;
				if (hsd != HeaterState.OFF && oldState == HeaterState.OFF ) {
					if (this.controlMode == ControlMode.PULL) {
						this.traceMessage("start pull control.\n");
						// if a state change has been detected from OFF to ON,
						// schedule a first execution of the control loop, which
						// in turn will schedule its next execution if needed
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										((HeaterController)
												this.getTaskOwner()).
															pullControLoop();
										}
									},
								this.actualControlPeriod, 
								TimeUnit.NANOSECONDS);
					} else {
						this.traceMessage("start push control.\n");
						long cp =
							(long) (TimeUnit.SECONDS.toMillis(1)
														* this.controlPeriod);
						try {
							this.sensorOutboundPort.
									startTemperaturesPushSensor(
													cp, TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}
			}
		} else {
			assert	hm.isTemperatureMeasures();
			// in push control mode, execute one push control step
			this.pushControLoop((HeaterCompoundMeasure)hm);
		}
	}

	/**
	 * implement the push control loop.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hcm	most recent temperatures measure from the heater.
	 */
	protected void		pushControLoop(HeaterCompoundMeasure hcm)
	{
		try {
			// execute the control only of the heater is still ON
			HeaterState s = HeaterState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != HeaterState.OFF) {
				if (DEBUG) {
					this.traceMessage(hcm.toString() + "\n");
				}
				double current = hcm.getCurrentTemperature();
				double target = hcm.getTargetTemperature();
				this.oneControlStep(current, target, s);
			} else {
				// when the heater is OFF, exit the control loop
				this.traceMessage("control is off.\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	protected void		oneControlStep(
		double current,
		double target,
		HeaterState s
		) throws Exception
	{
		if (current < target - this.hysteresis) {
			// the current room temperature is too low, start heating
			if (HeaterState.HEATING != s) {
				// tracing
				StringBuffer sb =
						new StringBuffer("start heating with ");
				sb.append(current);
				sb.append(" < ");
				sb.append(target);
				sb.append(" - ");
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
				// actuate
				this.actuatorOutboundPort.startHeating();;
			}
		} else if (current > target + this.hysteresis) {
			// the current room temperature is high enough, stop heating
			if (HeaterState.HEATING == s) {
				// tracing
				StringBuffer sb =
						new StringBuffer("stop heating with ");
				sb.append(current);
				sb.append(" > ");
				sb.append(target);
				sb.append(" + ");
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
				// actuate
				this.actuatorOutboundPort.stopHeating();;
			}
		}
	}

	/**
	 * implement the push control loop.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		pullControLoop()
	{
		this.traceMessage("executes a new pull control step.\n");
		try {
			// execute the control as long as the heater is ON
			HeaterState s = HeaterState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != HeaterState.OFF) {
				// get the temperature data from the heater
				@SuppressWarnings("unchecked")
				HeaterSensorData<HeaterCompoundMeasure> td =
						(HeaterSensorData<HeaterCompoundMeasure>)
										this.sensorOutboundPort.request();

				if (DEBUG) {
					this.traceMessage(td + "\n");
				}

				double current = td.getMeasure().getCurrentTemperature();
				double target = td.getMeasure().getTargetTemperature();
				this.oneControlStep(current, target, s);

				// schedule the next execution of the loop
				this.scheduleTask(
						o -> ((HeaterController)o).pullControLoop(),
						this.actualControlPeriod, 
						TimeUnit.NANOSECONDS);
			} else {
				// when the heater is OFF, exit the control loop
				this.traceMessage("exit the control.\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
