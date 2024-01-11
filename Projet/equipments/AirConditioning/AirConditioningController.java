package equipments.AirConditioning;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import equipments.AirConditioning.AirConditioningController;
import equipments.AirConditioning.AirConditioning.AirConditioningState;
import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI;
import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI;
import equipments.AirConditioning.connections.AirConditioningActuatorConnector;
import equipments.AirConditioning.connections.AirConditioningActuatorOutboundPort;
import equipments.AirConditioning.connections.AirConditioningSensorDataConnector;
import equipments.AirConditioning.connections.AirConditioningSensorDataOutboundPort;
import equipments.AirConditioning.measures.AirConditioningCompoundMeasure;
import equipments.AirConditioning.measures.AirConditioningMeasureI;
import equipments.AirConditioning.measures.AirConditioningSensorData;
import equipments.AirConditioning.measures.AirConditioningStateMeasure;
import utils.ExecutionType;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

//-----------------------------------------------------------------------------
@RequiredInterfaces(required={AirConditioningSensorRequiredPullCI.class,
							  AirConditioningActuatorCI.class,
							  ClocksServerCI.class})
@OfferedInterfaces(offered={AirConditioningSensorOfferedPullCI.class})
//-----------------------------------------------------------------------------
public class 	AirConditioningController
extends 	AbstractComponent
implements 	AirConditioningPushImplementationI{

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
	public static final double	STANDARD_HYSTERESIS = 0.8;
	/** standard control period in seconds.									*/
	public static final double	STANDARD_CONTROL_PERIOD = 60.0;

	/** URI of the sensor inbound port on the {@code ThermostatedAirConditiong}.	*/
	protected String								sensorIBP_URI;
	/** URI of the actuator inbound port on the {@code ThermostatedAirConditioning}.	*/
	protected String								actuatorIBPURI;
	/** sensor data outbound port connected to the {@code AirConditioning}.	*/
	protected AirConditioningSensorDataOutboundPort			sensorOutboundPort;
	/** actuator outbound port connected to the {@code AirConditioning}.	*/
	protected AirConditioningActuatorOutboundPort			actuatorOutboundPort;

	/** the actual hysteresis used in the control loop.						*/
	protected double								hysteresis;
	/* user set control period in seconds.									*/
	protected double								controlPeriod;
	protected final ControlMode						controlMode;
	/* actual control period, either in pure real time (not under test)
	 * or in accelerated time (under test), expressed in nanoseconds;
	 * used for scheduling the control task.								*/
	protected long									actualControlPeriod;
	/** the current state of the AirConditioning as perceived through the sensor
	 *  data received from the {@code ThermostatedAirConditioning}.					*/
	protected AirConditioningState							currentState;
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

	protected 	AirConditioningController(
			String sensorIBP_URI,
			String actuatorIBP_URI
			) throws Exception
		{
			this(sensorIBP_URI, actuatorIBP_URI,
				 STANDARD_HYSTERESIS, STANDARD_CONTROL_PERIOD,
				 ControlMode.PULL);
		}

	protected			AirConditioningController(
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
	
	protected			AirConditioningController(
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
					new AirConditioningSensorDataOutboundPort(this);
			this.sensorOutboundPort.publishPort();
			this.actuatorOutboundPort =
					new AirConditioningActuatorOutboundPort(this);
			this.actuatorOutboundPort.publishPort();

			if (VERBOSE || DEBUG) {
				this.tracer.get().setTitle("AirConditioning controller component");
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
					AirConditioningSensorDataConnector.class.getCanonicalName());
			this.doPortConnection(
					this.actuatorOutboundPort.getPortURI(),
					this.actuatorIBPURI,
					AirConditioningActuatorConnector.class.getCanonicalName());

			synchronized (this.stateLock) {
				this.currentState = AirConditioningState.OFF;
			}
			
			this.traceMessage("AirConditioning controller starts.\n");
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
		this.traceMessage("AirConditioning controller ends.\n");
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

	@SuppressWarnings("unchecked")
	@Override
	public void receiveDataFromAirConditioning(DataRequiredCI.DataI sd) 
	{
		assert	sd != null : new PreconditionException("sd != null");
		if (DEBUG) {
			this.traceMessage("receives air conditioning sensor data: " + sd + ".\n");
		}
		
		AirConditioningMeasureI acm =
				((AirConditioningSensorData<AirConditioningMeasureI>)sd).getMeasure();
		
		if (acm.isStateMeasure()) {
			AirConditioningState acsd = ((AirConditioningStateMeasure)acm).getData();
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
				AirConditioningState oldState = this.currentState;
				this.currentState = acsd;
				if (acsd != AirConditioningState.OFF && oldState == AirConditioningState.OFF ) {
					if (this.controlMode == ControlMode.PULL) {
						this.traceMessage("start pull control.\n");
						// if a state change has been detected from OFF to ON,
						// schedule a first execution of the control loop, which
						// in turn will schedule its next execution if needed
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										((AirConditioningController)
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
			assert	acm.isTemperatureMeasures();
			// in push control mode, execute one push control step
			this.pushControlLoop((AirConditioningCompoundMeasure)acm);
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
	 * @param acm	most recent temperatures measure from the heater.
	 */
	protected void pushControlLoop(AirConditioningCompoundMeasure acm)
	{
		try {
			// execute the control only of the heater is still ON
			AirConditioningState s = AirConditioningState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != AirConditioningState.OFF) {
				if (DEBUG) {
					this.traceMessage(acm.toString() + "\n");
				}
				double current = acm.getCurrentTemperature();
				double target = acm.getTargetTemperature();
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
		AirConditioningState s
		) throws Exception
	{	
		if (current > target + this.hysteresis) {
			// the current is too hot, start cooling
			if (s != AirConditioningState.COOLING) {
				// tracing
				StringBuffer sb =
						new StringBuffer("start cooling with ");
				sb.append(current);
				sb.append(" > ");
				sb.append(target);
				sb.append(" - ");
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
				// actuate
				this.actuatorOutboundPort.startCooling();;
			}
		} else if (current < target - this.hysteresis) {
			// the current room temperature is low enough, stop cooling
			if (s == AirConditioningState.COOLING) {
				// tracing
				StringBuffer sb =
						new StringBuffer("stop cooling with ");
				sb.append(current);
				sb.append(" < ");
				sb.append(target);
				sb.append(" + ");
				sb.append(this.hysteresis);
				sb.append(" at ");
				sb.append(this.clock.get().currentInstant());
				sb.append(".\n");
				this.traceMessage(sb.toString());
				// actuate
				this.actuatorOutboundPort.stopCooling();;
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
			// execute the control as long as the air conditioning is ON
			AirConditioningState s = AirConditioningState.OFF;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			
			if (s != AirConditioningState.OFF) {
				// get the temperature data from the air conditioning
				@SuppressWarnings("unchecked")
				AirConditioningSensorData<AirConditioningCompoundMeasure> td =
						(AirConditioningSensorData<AirConditioningCompoundMeasure>)
										this.sensorOutboundPort.request();

				if (DEBUG) {
					this.traceMessage(td + "\n");
				}

				double current = td.getMeasure().getCurrentTemperature();
				double target = td.getMeasure().getTargetTemperature();
				System.out.println("currentTemp: "+ current + " | targetTemp: "+ target + this.hysteresis);
				this.oneControlStep(current, target, s);

				// schedule the next execution of the loop
				this.scheduleTask(
						o -> ((AirConditioningController)o).pullControLoop(),
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
