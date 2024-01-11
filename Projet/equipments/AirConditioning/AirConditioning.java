package equipments.AirConditioning;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import equipments.AirConditioning.connections.AirConditioningExternalControlInboundPort;
import equipments.AirConditioning.connections.AirConditioningInternalControlInboundPort;
import equipments.AirConditioning.connections.AirConditioningUserInboundPort;
import equipments.AirConditioning.measures.AirConditioningCompoundMeasure;
import equipments.AirConditioning.measures.AirConditioningSensorData;
import equipments.AirConditioning.measures.AirConditioningStateMeasure;
import equipments.AirConditioning.mil.AirConditioningStateModel;
import equipments.AirConditioning.mil.AirConditioningTemperatureModel;
import equipments.AirConditioning.mil.MILSimulationArchitectures;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning.PowerValue;
import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI;
import equipments.AirConditioning.connections.AirConditioningActuatorInboundPort;
import equipments.AirConditioning.connections.AirConditioningSensorDataInboundPort;
import utils.Measure;
import utils.MeasurementUnit;
import utils.ExecutionType;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>AirConditioning</code> is a AirConditioning component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code targetTemperature >= -20.0 && targetTemperature <= 50.0}
 * invariant	{@code currentPowerLevel >= 0.0 && currentPowerLevel <= MAX_POWER_LEVEL}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@OfferedInterfaces(offered={AirConditioningUserCI.class, AirConditioningInternalControlCI.class,
							AirConditioningExternalControlCI.class,
							AirConditioningSensorOfferedPullCI.class,
							AirConditioningActuatorCI.class})
@RequiredInterfaces(required = {DataOfferedCI.PushCI.class, ClocksServerCI.class})
public class			AirConditioning
extends		AbstractCyPhyComponent
implements	AirConditioningUserImplI,
			AirConditioningInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>AirConditioningState</code> describes the operation
	 * states of the AirConditioning.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum AirConditioningState
	{
		/** AirConditioning is on.													*/
		ON,
		/** AirConditioning is cooling.												*/
		COOLING,
		/** AirConditioning is off.													*/
		OFF
	}
	
	public static enum AirConditioningSensorMeasures
	{
		/** cooling status: true if currently cooling, else false **/
		COOLING_STATUS,
		/** current target temperature **/
		TARGET_TEMPERATURE,
		/** current room temperate **/
		CURRENT_TEMPERATURE,
		/** somme of current and target temperatures **/
		COMPOUND_TEMPERATURES
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** max power level of the AirConditioning, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 1200.0;
	/** standard target temperature for the AirConditioning.						*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 19.0;

	// --- PORTS URI ---
	/** URI of the air conditioning inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
											"AirConditioning-RIP-URI";	
	/** URI of the AirConditioning port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
										"AIRCONDITIONING-USER-INBOUND-PORT-URI";
	/** URI of the AirConditioning port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
										"AIRCONDITIONING-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the AirConditioning port for external control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
										"AIRCONDITIONING-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String		SENSOR_INBOUND_PORT_URI =
										"AIRCONDITIONING-SENSOR-INBOUND-PORT-URI";
	public static final String		ACTUATOR_INBOUND_PORT_URI =
										"AIRCONDITIONING-ACTUATOR-INBOUND-PORT-URI";
	// ----------------
	
	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 10.0;

	/** current state (on, off) of the AirConditioning.								*/
	protected AirConditioningState						currentState;
	/**	current power level of the AirConditioning.									*/
	protected double									currentPowerLevel;
	/** inbound port offering the <code>AirConditioningUserCI</code> interface.		*/
	protected AirConditioningUserInboundPort			acip;
	/** inbound port offering the <code>AirConditioningInternalControlCI</code>
	 *  interface.															*/
	protected AirConditioningInternalControlInboundPort	acicip;
	/** inbound port offering the <code>AirConditioningExternalControlCI</code>
	 *  interface.															*/
	protected AirConditioningExternalControlInboundPort	acecip;
	/** target temperature for the cooling.	*/
	protected double									targetTemperature;
	
	// Sensors/actuators
	/** the inbound port through which the sensors are called.				*/
	protected AirConditioningSensorDataInboundPort	sensorInboundPort;
	/** the inbound port through which the actuators are called.			*/
	protected AirConditioningActuatorInboundPort	actuatorInboundPort;

	// Execution/Simulation
	/** outbound port to connect to the centralised clock server.			*/
	protected ClocksServerOutboundPort	clockServerOBP;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String				clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected final CompletableFuture<AcceleratedClock>	clock;

	/** plug-in holding the local simulation architecture and simulators.	*/
	protected AtomicSimulatorPlugin		asp;
	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String				simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String				localSimulatorURI;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;
	protected static final String		CURRENT_TEMPERATURE_NAME =
															"currentTemperature";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !on()}
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected			AirConditioning() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
			 ACTUATOR_INBOUND_PORT_URI);
	}
	
	/**
	 * create a new AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioning(
		String airConditioningUserInboundPortURI,
		String airConditioningInternalControlInboundPortURI,
		String airConditioningExternalControlInboundPortURI,
		String airConditioningSensorInboundPortURI,
		String airConditioningActuatorInboundPortURI
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, airConditioningUserInboundPortURI,
				airConditioningInternalControlInboundPortURI,
				airConditioningExternalControlInboundPortURI,
				airConditioningSensorInboundPortURI,
				airConditioningActuatorInboundPortURI,
				ExecutionType.STANDARD, null, null, 0.0, null);
	}

	/**
	 * create a new AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioning(
		String reflectionInboundPortURI,
		String airConditioningUserInboundPortURI,
		String airConditioningInternalControlInboundPortURI,
		String airConditioningExternalControlInboundPortURI,
		String airConditioningSensorInboundPortURI,
		String airConditioningActuatorInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor,
		String clockURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		
		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.localSimulatorURI = localSimulatorURI;
		this.accFactor = accFactor;
		this.clockURI = clockURI;
		this.clock = new CompletableFuture<AcceleratedClock>();
		
		this.initialise(airConditioningUserInboundPortURI,
						airConditioningInternalControlInboundPortURI,
						airConditioningExternalControlInboundPortURI,
						airConditioningSensorInboundPortURI,
						airConditioningActuatorInboundPortURI);
	}

	/**
	 * create a new thermostated AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String airConditioningUserInboundPortURI,
		String airConditioningInternalControlInboundPortURI,
		String airConditioningExternalControlInboundPortURI,
		String airConditioningSensorInboundPortURI,
		String airConditioningActuatorInboundPortURI
		) throws Exception
	{
		assert	airConditioningUserInboundPortURI != null && !airConditioningUserInboundPortURI.isEmpty();
		assert	airConditioningInternalControlInboundPortURI != null && !airConditioningInternalControlInboundPortURI.isEmpty();
		assert	airConditioningExternalControlInboundPortURI != null && !airConditioningExternalControlInboundPortURI.isEmpty();

		this.currentState = AirConditioningState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.acip = new AirConditioningUserInboundPort(airConditioningUserInboundPortURI, this);
		this.acip.publishPort();
		this.acicip = new AirConditioningInternalControlInboundPort(
									airConditioningInternalControlInboundPortURI, this);
		this.acicip.publishPort();
		this.acecip = new AirConditioningExternalControlInboundPort(
									airConditioningExternalControlInboundPortURI, this);
		this.acecip.publishPort();
		this.sensorInboundPort = new AirConditioningSensorDataInboundPort(
						airConditioningSensorInboundPortURI, this);
		this.sensorInboundPort.publishPort();
		this.actuatorInboundPort = new AirConditioningActuatorInboundPort(
						airConditioningActuatorInboundPortURI, this);
		this.actuatorInboundPort.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.createAirConditioningMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			 
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
		case SIL_SIMULATION:
			
			architecture =
					MILSimulationArchitectures.
						createAirConditioningRTArchitecture(
								this.currentExecutionType,
								this.accFactor);
		assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
				new AssertionError(
						"local simulator " + this.localSimulatorURI
						+ " does not exist!");
		
		this.addLocalSimulatorArchitecture(architecture);
		this.architecturesURIs2localSimulatorURIS.
				put(this.simArchitectureURI, this.localSimulatorURI);
		break;
		case STANDARD:
		case INTEGRATION_TEST:
		default:
		}
		
		if (VERBOSE) {
			this.tracer.get().setTitle("AirConditioning component");
			this.tracer.get().setRelativePosition(1, 2);
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
			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				this.asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				this.asp.setPluginURI(uri);
				this.asp.setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case MIL_RT_SIMULATION:
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case SIL_SIMULATION:
				// For SIL simulations, we use the ModelStateAccessI protocol
				// to provide the access to the current temperature computed
				// by the AirConditioningTemperatureModel.
				this.asp = new RTAtomicSimulatorPlugin() {
					private static final long serialVersionUID = 1L;
					/**
					 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
					 */
					@Override
					public Object	getModelStateValue(
						String modelURI,
						String name
						) throws Exception
					{
						assert	modelURI.equals(AirConditioningTemperatureModel.SIL_URI);
						assert	name.equals(CURRENT_TEMPERATURE_NAME);
						return ((AirConditioningTemperatureModel)
										this.atomicSimulators.get(modelURI).
												getSimulatedModel()).
														getCurrentTemperature();
					}
				};
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case STANDARD:
			case INTEGRATION_TEST:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
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
			this.clock.complete(clock);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.acip.unpublishPort();
			this.acicip.unpublishPort();
			this.acecip.unpublishPort();
			this.sensorInboundPort.unpublishPort();
			this.actuatorInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its state: " +
											this.currentState + ".\n");
		}
		
		return this.currentState == AirConditioningState.ON ||
									this.currentState == AirConditioningState.COOLING;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		assert	!this.on() : new PreconditionException("!on()");

		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning switches on.\n");
		}

		this.currentState = AirConditioningState.ON;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the LampStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												AirConditioningStateModel.SIL_URI,
												t -> new SwitchOnAirConditioning(t));
		}

		this.sensorInboundPort.send(
				new AirConditioningSensorData<AirConditioningStateMeasure>(
						new AirConditioningStateMeasure(this.currentState)));

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		if (AirConditioning.VERBOSE) {
			this.traceMessage("Heater switches off.\n");
		}

		this.currentState = AirConditioningState.OFF;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the LampStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												AirConditioningStateModel.SIL_URI,
												t -> new SwitchOffAirConditioning(t));
		}

		this.sensorInboundPort.send(
				new AirConditioningSensorData<AirConditioningStateMeasure>(
						new AirConditioningStateMeasure(this.currentState)));

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		assert	target >= -50.0 && target <= 50.0 :
				new PreconditionException("target >= -50.0 && target <= 50.0");
		
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning sets a new target temperature: " + target + "°.\n");
		}

		this.targetTemperature = target;

		assert	this.targetTemperature == target :
				new PostconditionException("getTargetTemperature() == target");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its target temperature " + this.targetTemperature + "°.\n");
		}

		double ret = this.targetTemperature;

		assert	ret >= -50.0 && ret <= 50.0 :
				new PostconditionException("return >= -50.0 && return <= 50.0");

		return ret;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		assert this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, the value is got from the
			// AirConditioningTemperatureModel through the ModelStateAccessI interface
			currentTemperature = 
					(double)((RTAtomicSimulatorPlugin)this.asp).
										getModelStateValue(
												AirConditioningTemperatureModel.SIL_URI,
												CURRENT_TEMPERATURE_NAME);
		}
		
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns the current temperature " + currentTemperature + "°.\n");
		}

		return  currentTemperature;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#cooling()
	 */
	@Override
	public boolean		cooling() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");
		
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its cooling status: " + 
						(this.currentState == AirConditioningState.COOLING) + ".\n");
		}

		return this.currentState == AirConditioningState.COOLING;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");
		assert	!this.cooling() : new PreconditionException("!cooling()");

		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning starts cooling.\n");
		}
		
		this.currentState = AirConditioningState.COOLING;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the LampStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												AirConditioningStateModel.SIL_URI,
												t -> new Cool(t));
		}
		

		this.sensorInboundPort.send(
				new AirConditioningSensorData<AirConditioningStateMeasure>(
						new AirConditioningStateMeasure(this.currentState)));

		assert	this.cooling() : new PostconditionException("cooling()");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning stops cooling.\n");
		}
		assert	this.currentState == AirConditioningState.COOLING : new PreconditionException("this.currentState == AirConditioningState.COOLING");

		
		this.currentState = AirConditioningState.ON;
		
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the LampStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												AirConditioningStateModel.SIL_URI,
												t -> new DoNotCool(t));
		}

		this.sensorInboundPort.send(
				new AirConditioningSensorData<AirConditioningStateMeasure>(
						new AirConditioningStateMeasure(this.currentState)));


		assert	!(this.cooling()) : new PostconditionException("!cooling()");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its max power level " + 
					MAX_POWER_LEVEL + "W.\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning sets its power level to " + 
														powerLevel + "W.\n");
		}

		assert	this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= MAX_POWER_LEVEL) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}
		
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			PowerValue pv = new PowerValue(this.currentPowerLevel);
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												AirConditioningStateModel.SIL_URI,
												t -> new SetPowerAirConditioning(t, pv));
		}

		assert	powerLevel > MAX_POWER_LEVEL || this.currentPowerLevel == powerLevel :
				new PostconditionException(
						"powerLevel > MAX_POWER_LEVEL || "
						+ "this.currentPowerLevel  == powerLevel");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its current power level " + 
					this.currentPowerLevel + "W.\n");
		}

		assert	this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= getMaxPowerLevel() :
				new PostconditionException(
							"return >= 0.0 && return <= MAX_POWER_LEVEL");

		return this.currentPowerLevel;
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	public void printSeparator(String title) throws Exception {
		this.traceMessage("**********"+ title +"**********\n");
	}
	
	
	// -------------------------------------------------------------------------
	// Component sensors
	// -------------------------------------------------------------------------

	/**
	 * return the cooling status of the air conditioning as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the cooling status of the air conditioning as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public AirConditioningSensorData<Measure<Boolean>>	coolingPullSensor()
	throws Exception
	{
		return new AirConditioningSensorData<Measure<Boolean>>(
					new Measure<Boolean>(this.cooling()));
	}

	/**
	 * return the target temperature as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the target temperature as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public AirConditioningSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return new AirConditioningSensorData<Measure<Double>>(
						new Measure<Double>(this.getTargetTemperature(),
											MeasurementUnit.CELSIUS));
	}

	/**
	 * return the current temperature as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public AirConditioningSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return new AirConditioningSensorData<Measure<Double>>(
						new Measure<Double>(this.getCurrentTemperature(),
											MeasurementUnit.CELSIUS));
	}

	/**
	 * start a sequence of temperatures pushes with the given period.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code controlPeriod > 0}
	 * pre	{@code tu != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param controlPeriod	period at which the pushes must be made.
	 * @param tu			time unit in which {@code controlPeriod} is expressed.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		AcceleratedClock ac = this.clock.get();
		// the accelerated period is in nanoseconds, hence first convert
		// the period to nanoseconds, perform the division and then
		// convert to long (hence providing a better precision than
		// first dividing and then converting to nanoseconds...)
		long actualControlPeriod =
			(long)((controlPeriod * tu.toNanos(1))/ac.getAccelerationFactor());
		// sanity checking, the standard Java scheduler has a
		// precision no less than 10 milliseconds...
		if (actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
			System.out.println(
					"Warning: accelerated control period is "
							+ "too small ("
							+ actualControlPeriod +
							"), unexpected scheduling problems may"
							+ " occur!");
		}
		this.temperaturesPushSensorTask(actualControlPeriod);
	}

	/**
	 * if the AirConditioning is not off, perform one push and schedule the next.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code actualControlPeriod > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param actualControlPeriod	period at which the push sensor must be triggered.
	 * @throws Exception			<i>to do</i>.
	 */
	protected void		temperaturesPushSensorTask(long actualControlPeriod)
	throws Exception
	{
		assert	actualControlPeriod > 0 :
				new PreconditionException("actualControlPeriod > 0");

		if (this.currentState != AirConditioningState.OFF) {
			this.traceMessage("AirConditioning performs a new temperatures push.\n");
			this.temperaturesPushSensor();
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								temperaturesPushSensorTask(actualControlPeriod);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					actualControlPeriod,
					TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * sends the compound measure of the target and the current temperatures
	 * through the push sensor interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		temperaturesPushSensor() throws Exception
	{
		this.sensorInboundPort.send(
					new AirConditioningSensorData<AirConditioningCompoundMeasure>(
						new AirConditioningCompoundMeasure(
							this.targetTemperaturePullSensor().getMeasure(),
							this.currentTemperaturePullSensor().getMeasure())));
	}
}
// -----------------------------------------------------------------------------
