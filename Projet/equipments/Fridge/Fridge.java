package equipments.Fridge;

import equipments.Fridge.connections.FridgeExternalControlInboundPort;
import equipments.Fridge.connections.FridgeInternalControlInboundPort;
import equipments.Fridge.connections.FridgeUserInboundPort;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Fridge</code> a Fridge component.
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
@OfferedInterfaces(offered={FridgeUserCI.class, FridgeInternalControlCI.class,
							FridgeExternalControlCI.class})
public class			Fridge
extends		AbstractComponent
implements	FridgeUserImplI,
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
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum	FridgeState
	{
		/** Fridge is on.													*/
		ON,
		/** Cooler is cooling.												*/
		COOLER_COOLING,
		/** Freezer is cooling.												*/
		FREEZER_COOLING,
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
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
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

	/** max power level of the Fridge, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;

	/** URI of the Fridge port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"Fridge-USER-INBOUND-PORT-URI";
	/** URI of the Fridge port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"Fridge-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the Fridge port for external control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"Fridge-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_COOLER_TEMPERATURE = -5.0;
	public static final double		FAKE_CURRENT_FREEZER_TEMPERATURE = 4.0;

	protected static final double STANDARD_TARGET_COOLER_TEMPERATURE = 4.0;
	protected static final double STANDARD_TARGET_FREEZER_TEMPERATURE = -18.0;

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
	/** target temperature for the cooler compartment.	*/
	protected double			targetCoolerTemperature;
	/** target temperature for the freezer compartment.	*/
	protected double			targetFreezerTemperature;

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
	protected			Fridge() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Fridge(
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(FridgeUserInboundPortURI,
						FridgeInternalControlInboundPortURI,
						FridgeExternalControlInboundPortURI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Fridge(
		String reflectionInboundPortURI,
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(FridgeUserInboundPortURI,
						FridgeInternalControlInboundPortURI,
						FridgeExternalControlInboundPortURI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		assert	FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty();
		assert	FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty();
		assert	FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty();

		this.currentState = FridgeState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetCoolerTemperature = STANDARD_TARGET_COOLER_TEMPERATURE;
		this.targetFreezerTemperature = STANDARD_TARGET_FREEZER_TEMPERATURE;

		this.fip = new FridgeUserInboundPort(FridgeUserInboundPortURI, this);
		this.fip.publishPort();
		this.ficip = new FridgeInternalControlInboundPort(
									FridgeInternalControlInboundPortURI, this);
		this.ficip.publishPort();
		this.fecip = new FridgeExternalControlInboundPort(
									FridgeExternalControlInboundPortURI, this);
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

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fip.unpublishPort();
			this.ficip.unpublishPort();
			this.fecip.unpublishPort();
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
				new PreconditionException("target >= -25.0 && target <= 0.0");

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
				new PostconditionException("return >= -25.0 && return <= 0.0");

		return ret;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentFreezeTemperature()
	 */
	@Override
	public double getCurrentFreezerTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

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
}
// -----------------------------------------------------------------------------
