package equipments.Fridge;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import equipments.Fridge.connections.FridgeExternalControlInboundPort;
import equipments.Fridge.connections.FridgeInternalControlInboundPort;
import equipments.Fridge.connections.FridgeUserInboundPort;
import equipments.Fridge.measures.FridgeSensorData;
import equipments.HEM.registration.RegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.Heater.HeaterState;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterCompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import utils.ExecutionType;
import utils.Measure;
import utils.MeasurementUnit;
import equipments.HEM.registration.RegistrationConnector;
import equipments.HEM.registration.RegistrationInboundPort;
import equipments.HEM.HEM_descriptors;
// -----------------------------------------------------------------------------
/**
 * The class <code>Fridge</code> a Fridge component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * TODO
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
@OfferedInterfaces(offered={FridgeUserCI.class, FridgeInternalControlCI.class, FridgeExternalControlCI.class,
		FridgeSensorDataCI.FridgeSensorOfferedPullCI.class, FridgeActuatorCI.class})
@RequiredInterfaces(required={RegistrationOutboundPort.class, RegistrationConnector.class, RegistrationInboundPort.class,
		DataOfferedCI.PushCI.class, ClocksServerCI.class})
public class			Fridge
extends		AbstractCyPhyComponent
implements	FridgeUserImplI,
FridgeUserAndControlI,
FridgeInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>FridgeState</code> describes the operation
	 * states of the Fridge.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 */
	protected static enum	FridgeState
	{
		/** Fridge is on.													*/
		ON,
		/** Cooler is cooling.												*/
		COOLER_COOLING,
		/** Freezer is cooling.												*/
		FREEZER_COOLING,
		/** Both freezer and cooler are cooling								*/
		BOTH_COOLING,
		/** Fridge is off.													*/
		OFF
	}

	/**
	 * The enumeration <code>FridgeCompartment</code> describes the 
	 * compartment types of the Fridge.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 */
	protected static enum	FridgeCompartment
	{
		/** Fridge compartment is COOLER.													*/
		COOLER,
		/** Fridge compartment is FREEZER.												*/
		FREEZER
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the Fridge inbound port used in tests.						*/
	public static final String REFLECTION_INBOUND_PORT_URI = "Fridge-RIP-URI";
	/** max power level of the Fridge, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;
	/** registration required boolean 										*/
	protected boolean registrationRequired = true;

	/** URI of the Fridge port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
			"Fridge-USER-INBOUND-PORT-URI";
	/** URI of the Fridge port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
			"Fridge-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the Fridge port for external control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
			"Fridge-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the Registration outbound port.								*/
	public static final String 		REGISTRATION_OUTBOUND_PORT = 
			"REGISTRATION-OUTBOUND-PORT-URI";
	public static final String		SENSOR_INBOUND_PORT_URI =
			"HEATER-SENSOR-INBOUND-PORT-URI";
	public static final String		ACTUATOR_INBOUND_PORT_URI =
			"HEATER-ACTUATOR-INBOUND-PORT-URI";

	public static String Uri = "FRIDGE-URI";


	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_COOLER_TEMPERATURE = -5.0;
	public static final double		FAKE_CURRENT_FREEZER_TEMPERATURE = 4.0;

	protected static final double STANDARD_TARGET_COOLER_TEMPERATURE = 4.0;
	protected static final double STANDARD_TARGET_FREEZER_TEMPERATURE = -18.0;
	public static final MeasurementUnit	TEMPERATURE_UNIT =
			MeasurementUnit.CELSIUS;

	/** current state (on, off) of the Fridge.								*/
	protected FridgeState		currentState;
	/** current compartment (cooler, freeze) of the Fridge.								*/
	protected FridgeCompartment	currentCompartment;
	/**	current power level of the Fridge.									*/
	protected double			currentPowerLevel;
	/** inbound port offering the <code>FridgeUserCI</code> interface.		*/
	protected FridgeUserInboundPort	fip;
	/** inbound port offering the <code>FridgeInternalControlCI</code>
	 *  interface.															*/
	protected FridgeInternalControlInboundPort	ficip;
	/** inbound port offering the <code>FridgeExternalControlCI</code>
	 *  interface.															*/
	protected FridgeExternalControlInboundPort	fecip;
	/** outbound port offering <code>RegistrationCI</code>
	 *  interface															*/														
	protected RegistrationOutboundPort	regop;
	/** target temperature for the cooler compartment.	*/
	protected double			targetCoolerTemperature;
	/** target temperature for the freezer compartment.	*/
	protected double			targetFreezerTemperature;
	/** Connector descriptor file path **/
	protected String			path2xmlDescriptor;

	// Sensors/actuators

	/** the inbound port through which the sensors are called.				*/
	protected FridgeSensorDataInboundPort	sensorInboundPort;
	/** the inbound port through which the actuators are called.			*/
	protected HeaterActuatorInboundPort		actuatorInboundPort;

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
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected Fridge() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
				EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
				ACTUATOR_INBOUND_PORT_URI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param registrationRequired					required param.
	 * @throws Exception							<i>to do</i>.
	 */
	protected Fridge(
			String fridgeUserInboundPortURI,
			String fridgeInternalControlInboundPortURI,
			String fridgeExternalControlInboundPortURI,
			String fridgeSensorInboundPortURI,
			String fridgeActuatorInboundPortURI
			) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, fridgeUserInboundPortURI,
				fridgeInternalControlInboundPortURI,
				fridgeExternalControlInboundPortURI,
				fridgeSensorInboundPortURI,
				fridgeActuatorInboundPortURI,
				ExecutionType.STANDARD, null, null, 0.0, null);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param registrationRequired					required param.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise() 
			throws Exception
	{
		this.currentState = FridgeState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetCoolerTemperature = STANDARD_TARGET_COOLER_TEMPERATURE;
		this.targetFreezerTemperature = STANDARD_TARGET_FREEZER_TEMPERATURE;

		this.fip = new FridgeUserInboundPort(USER_INBOUND_PORT_URI, this);
		this.fip.publishPort();

		this.ficip = new FridgeInternalControlInboundPort(
				INTERNAL_CONTROL_INBOUND_PORT_URI, this);
		this.ficip.publishPort();

		this.fecip = new FridgeExternalControlInboundPort(
				EXTERNAL_CONTROL_INBOUND_PORT_URI, this);
		this.fecip.publishPort();


		if (VERBOSE) {
			this.tracer.get().setTitle("Fridge component");
			this.tracer.get().setRelativePosition(3, 2);
			this.toggleTracing();		
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override 
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			if(VERBOSE)
				this.traceMessage("Connexion des ports\n\n");

			if(registrationRequired) {
				this.regop = new RegistrationOutboundPort(REGISTRATION_OUTBOUND_PORT, this);
				this.regop.publishPort();
				this.doPortConnection(regop.getPortURI(), HEM_descriptors.URI_REGISTRATION_INBOUND_PORT, 
						RegistrationConnector.class.getCanonicalName());
				if(VERBOSE)
					this.traceMessage("Inscription of Fridge\n\n");
			}

		} catch(Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		if(VERBOSE)
			this.traceMessage("Test if the fridge is registered\n\n");
		if(!this.registered())
			this.traceMessage("Fridge unregistered\n\n");
		super.execute();
	}

	@Override 
	public synchronized void finalise() throws Exception {
		if(VERBOSE) 
			this.traceMessage("Disconection of ports bounds\n\n");

		if(this.registrationRequired) {
			this.doPortDisconnection(this.regop.getPortURI());
		}

		super.finalise();
	}


	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			if(VERBOSE)
				this.traceMessage("Disconection of fridge ports\n\n");

			this.fip.unpublishPort();
			this.ficip.unpublishPort();
			this.fecip.unpublishPort();
			if(this.registrationRequired)
				this.regop.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Fridge.FridgeUserImplI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its state: " +
					this.currentState + ".\n");
		}
		this.traceMessage(""+(this.currentState == FridgeState.ON ||
				this.currentState == FridgeState.COOLER_COOLING ||
				this.currentState == FridgeState.FREEZER_COOLING
				)+"\n");

		return this.currentState == FridgeState.ON ||
				this.currentState == FridgeState.COOLER_COOLING || 
				this.currentState == FridgeState.FREEZER_COOLING;
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge switches ON.\n");
		}

		assert	!(this.currentState == FridgeState.ON) : new PreconditionException("!(this.currentState == FridgeState.ON)");

		this.register();
		this.currentState = FridgeState.ON;

		assert	 this.currentState == FridgeState.ON : new PostconditionException("this.currentState == FridgeState.ON");
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge switches OFF.\n");
		}

		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");

		this.currentState = FridgeState.OFF;

		assert	 !(this.currentState == FridgeState.ON) : new PostconditionException("!(this.currentState == FridgeState.ON)");

		this.unregister();
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#setTargetFreezerTemperature(double targetFreezer)
	 */
	@Override
	public void	setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets a new target freezer temperature: " + targetFreezer + "°.\n");
		}

		assert	targetFreezer >= -20.0 && targetFreezer <= 0.0 :
			new PreconditionException("target >= -20.0 && target <= 0.0");

		this.targetFreezerTemperature = targetFreezer;

		assert	this.targetFreezerTemperature == targetFreezer :
			new PostconditionException("this.targetFreezerTemperature == target");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetFreezerTemperature()
	 */
	@Override
	public double getTargetFreezerTemperature() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its freezer target temperature " + this.targetFreezerTemperature + "°.\n");
		}

		double ret = this.targetFreezerTemperature;

		assert	ret >= -20.0 && ret <= 0.0 :
			new PostconditionException("return >= -20.0 && return <= 0.0");

		return ret;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentFreezeTemperature()
	 */
	@Override
	public double getCurrentFreezerTemperature() throws Exception
	{
		assert	this.currentState == FridgeState.ON  : new PreconditionException("this.currentState == FridgeState.ON");

		// Temporary implementation; would need a temperature sensor.
		double currentFreezerTemperature = FAKE_CURRENT_FREEZER_TEMPERATURE;
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns the current freezer temperature " + currentFreezerTemperature + "°.\n");
		}

		return  currentFreezerTemperature;
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#setTargetCoolerTemperature(double targetCooler)
	 */
	@Override
	public void	setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets a new target cooler temperature: " + targetCooler + "°.\n");
		}

		assert	targetCooler >= 0.0 && targetCooler <= 15.0 :
			new PreconditionException("target >= 0.0 && target <= 15.0");

		this.targetCoolerTemperature = targetCooler;

		assert	this.targetCoolerTemperature == targetCooler :
			new PostconditionException("this.targetCoolerTemperature == target");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetCoolerTemperature()
	 */
	@Override
	public double getTargetCoolerTemperature() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its cooler target temperature " + this.targetCoolerTemperature + "°.\n");
		}

		double ret = this.targetCoolerTemperature;

		assert	ret >= 0.0 && ret <= 15.0 :
			new PostconditionException("return >= 0.0 && return <= 15.0");

		return ret;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");

		// Temporary implementation; would need a temperature sensor.
		double currentCoolerTemperature = FAKE_CURRENT_COOLER_TEMPERATURE;
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns the current cooler temperature " + currentCoolerTemperature + "°.\n");
		}

		return  currentCoolerTemperature;
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#cooling()
	 */
	@Override
	public boolean		coolingCooler() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Cooler returns its cooling status: " + 
					(this.currentState == FridgeState.COOLER_COOLING) + ".\n");
		}

		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");

		return this.currentState == FridgeState.COOLER_COOLING;
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#cooling()
	 */
	@Override
	public boolean		coolingFreezer() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Freezer returns its cooling status: " + 
					(this.currentState == FridgeState.FREEZER_COOLING) + ".\n");
		}

		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");

		return this.currentState == FridgeState.FREEZER_COOLING;
	}


	/**
	 * @see equipments.Fridge.FridgeInternalControlI#startCoolingCooler()
	 */
	@Override
	public void			startCoolingCooler() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge cooler starts cooling.\n");
		}
		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");
		assert	!(this.currentState == FridgeState.COOLER_COOLING) : new PreconditionException("!(this.currentState == FridgeState.COOLER_COOLING)");

		this.currentState = FridgeState.COOLER_COOLING;

		assert	this.currentState == FridgeState.COOLER_COOLING : new PostconditionException("this.currentState == FridgeState.COOLER_COOLING");
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#stopCoolingCooler()
	 */
	@Override
	public void			stopCoolingCooler() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge cooler stops cooling.\n");
		}
		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");
		assert	this.currentState == FridgeState.COOLER_COOLING  : new PreconditionException("this.currentState == FridgeState.COOLER_COOLING ");

		this.currentState = FridgeState.ON;

		assert	!(this.currentState == FridgeState.COOLER_COOLING ) : new PostconditionException("!(this.currentState == FridgeState.COOLER_COOLING )");
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#startCoolingCooler()
	 */
	@Override
	public void			startCoolingFreezer() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge freezer starts cooling.\n");
		}
		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");
		assert	!(this.currentState == FridgeState.FREEZER_COOLING) : new PreconditionException("!(this.currentState == FridgeState.FREEZER_COOLING)");

		this.currentState = FridgeState.FREEZER_COOLING;

		assert	this.currentState == FridgeState.FREEZER_COOLING : new PostconditionException("this.currentState == FridgeState.FREEZER_COOLING");
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#stopCoolingFreezer()
	 */
	@Override
	public void			stopCoolingFreezer() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge Freezer stops cooling.\\n");
		}
		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");
		assert	this.currentState == FridgeState.FREEZER_COOLING : new PreconditionException("coolingFreezer()this.currentState == FridgeState.FREEZER_COOLING");

		this.currentState = FridgeState.ON;

		assert	!(this.currentState == FridgeState.FREEZER_COOLING) : new PostconditionException("!(this.currentState == FridgeState.FREEZER_COOLING)");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its max power level " + 
					MAX_POWER_LEVEL + "W.\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
			throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets its power level to " + 
					powerLevel + "W.\n");
		}

		assert	this.currentState == FridgeState.ON : new PreconditionException("this.currentState == FridgeState.ON");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= MAX_POWER_LEVEL) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}

		assert	powerLevel > MAX_POWER_LEVEL ||
		this.currentPowerLevel == powerLevel :
			new PostconditionException(
					"powerLevel > MAX_POWER_LEVEL || "
							+ "this.currentPowerLevel == powerLevel");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its current power level " + 
					this.currentPowerLevel + "W.\n");
		}

		assert	this.currentState == FridgeState.ON : new PreconditionException("on()");

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= MAX_POWER_LEVEL :
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

	/**
	 * 		Registering and unregistering
	 */

	/**
	 * test registration
	 * @return true if already registered else false
	 * @throws Exception
	 */
	public boolean registered() throws Exception {
		return this.regop.registered(Uri);
	}

	/**
	 * registers equipment
	 * @return true if register else false
	 * @throws Exception
	 */
	public boolean register() throws Exception {
		return this.regop.register(Uri, INTERNAL_CONTROL_INBOUND_PORT_URI, this.path2xmlDescriptor);
	}

	/**
	 * unregisters equipment
	 * @throws Exception
	 */
	public void unregister() throws Exception {
		this.regop.unregister(Uri);
	}

	// -------------------------------------------------------------------------
	// Component sensors
	// -------------------------------------------------------------------------

	/**
	 * return the cooling status of the Fridge as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the cooling status of the Fridge as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public FridgeSensorData<Measure<Boolean>> coolingPullSensor() {
		return new FridgeSensorData<Measure<Boolean>>(
				new Measure<Boolean>(this.coolingCooler()));
	}

	/***********************************************************************************/
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
	public FridgeSensorData<Measure<Double>>  targetTemperaturePullSensor() {
		return new FridgeSensorData<Measure<Double>>(
				new Measure<Double>(this.targetCoolerTemperature,
						MeasurementUnit.CELSIUS));
	}

	/***********************************************************************************/
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
	public FridgeSensorData<Measure<Double>> currentTemperaturePullSensor() {
		return new FridgeSensorData<Measure<Double>>(
				new Measure<Double>(this.targetCoolerTemperature,
						MeasurementUnit.CELSIUS));
	}

	/***********************************************************************************/
	/**
	 * if the Fridge is not off, perform one push and schedule the next.
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
	public void startTemperaturesPushSensor(long controlPeriod, TimeUnit tu) {
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

	/***********************************************************************************/
	/**
	 * if the Fridge is not off, perform one push and schedule the next.
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
	protected void	temperaturesPushSensorTask(long actualControlPeriod)
			throws Exception
	{
		assert	actualControlPeriod > 0 :
			new PreconditionException("actualControlPeriod > 0");

		if (this.currentState != FridgeState.OFF) {
			this.traceMessage("Heater performs a new temperatures push.\n");
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

	/***********************************************************************************/
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
	protected void temperaturesPushSensor() throws Exception {
		this.sensorInboundPort.send(
				new FridgeSensorData<FridgeCompoundMeasure>(
						new FridgeCompoundMeasure(
								this.targetTemperaturePullSensor().getMeasure(),
								this.currentTemperaturePullSensor().getMeasure())));
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
