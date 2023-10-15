package production.intermittent.PetrolGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.Heater;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorExternalControlInboundPort;
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorInternalControlInboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/** TODO Voir si UserI necessaire ?
 * The class <code>PetrolGenerator</code> is a petrol generator component.
 *
 * <p><strong>Small size</strong> petrol generator, 2kW/h.</p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentPowerLevelProduction >= 0.0 && currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@OfferedInterfaces(offered={PetrolGeneratorExternalControlCI.class, PetrolGeneratorInternalControlCI.class})
public class PetrolGenerator 
extends		AbstractComponent
implements PetrolGeneratorExternalControlI, PetrolGeneratorInternalControlI{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * The enumeration <code>GeneratorState</code> describes the operation
	 * states of the heater.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2023-10-15</p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 */
	protected static enum GeneratorState {
		/** petrol generator is on.											*/
		ON,
		/** petrol generator  is heating.									*/
		PRODUCING,
		/** petrol generator  is off.										*/
		OFF
	}
	/***********************************************************************************/

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/** max power level production of the petrol generator, in W/h.				    */
	protected static final double MAX_POWER_LEVEL_PRODUCTION = 2000.0; // 2 kW/h
	/** max fuel tank level of the petrol generator, in L.				    		*/
	protected static final double MAX_FUEL_TANK_LEVEL = 5.0; // 5L

	/** URI of the petrol generator port for internal control.						*/
	public static final String INTERNAL_CONTROL_INBOUND_PORT_URI =
			"PETROL-GENERATOR-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the petrol generator port for external control.						*/
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI =			
			"PETROL-GENERATOR-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.										*/
	public static final boolean		VERBOSE = true;

	/** current state (on, off) of the petrol generator.							*/
	protected GeneratorState currentState;
	/**	current power level produced by the petrol generator since its on.			*/
	protected double currentPowerLevelProduction;
	/**	current fuel tank level of the petrol generator.							*/
	protected double currentFuelTankLevel;
	/** inbound port offering the <code>PetrolGeneratorInternalControlCI</code>
	 *  interface.											  						*/
	protected PetrolGeneratorInternalControlInboundPort	petrolGeneratorInternalControlInboundPort; 
	/** inbound port offering the <code>PetrolGeneratorExternalControlCI</code>
	 *  interface.																	*/
	protected PetrolGeneratorExternalControlInboundPort	petrolGeneratorExternalControlInboundPort;
	/***********************************************************************************/

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a new heater.
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
	protected PetrolGenerator() throws Exception{
		this(INTERNAL_CONTROL_INBOUND_PORT_URI, EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code petrolGeneratorInternalControlInboundPortURI != null && !petrolGeneratorInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code petrolGeneratorExternalControlInboundPortURI != null && !petrolGeneratorExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param petrolGeneratorInternalControlInboundPortURI	URI of the inbound port to call the petrol generator component for internal control.
	 * @param petrolGeneratorExternalControlInboundPortURI	URI of the inbound port to call the petrol generato component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected PetrolGenerator(
			String petrolGeneratorInternalControlInboundPortURI,
			String petrolGeneratorExternalControlInboundPortURI
			) throws Exception
	{
		super(1, 0);
		this.initialise(petrolGeneratorInternalControlInboundPortURI, petrolGeneratorExternalControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code petrolGeneratorInternalControlInboundPortURI != null && !petrolGeneratorInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code petrolGeneratorExternalControlInboundPortURI != null && !petrolGeneratorExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param petrolGeneratorInternalControlInboundPortURI	URI of the inbound port to call the petrol generator component for internal control.
	 * @param petrolGeneratorExternalControlInboundPortURI	URI of the inbound port to call the petrol generator component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected PetrolGenerator(
			String reflectionInboundPortURI,
			String petrolGeneratorInternalControlInboundPortURI,
			String petrolGeneratorExternalControlInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(petrolGeneratorInternalControlInboundPortURI, petrolGeneratorExternalControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code petrolGeneratorInternalControlInboundPortURI != null && !petrolGeneratorInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code petrolGeneratorExternalControlInboundPortURI != null && !petrolGeneratorExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param petrolGeneratorInternalControlInboundPortURI	URI of the inbound port to call the petrol generator component for internal control.
	 * @param petrolGeneratorExternalControlInboundPortURI	URI of the inbound port to call the petrol generator component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String petrolGeneratorInternalControlInboundPortURI,
			String petrolGeneratorExternalControlInboundPortURI
			) throws Exception
	{
		assert	petrolGeneratorInternalControlInboundPortURI != null && !petrolGeneratorInternalControlInboundPortURI.isEmpty();
		assert	petrolGeneratorExternalControlInboundPortURI != null && !petrolGeneratorExternalControlInboundPortURI.isEmpty();

		this.currentState = GeneratorState.OFF;
		this.currentPowerLevelProduction = 0;
		this.currentFuelTankLevel = 5.0; // full at the beginning

		this.petrolGeneratorInternalControlInboundPort = new PetrolGeneratorInternalControlInboundPort(
				petrolGeneratorInternalControlInboundPortURI, this);
		this.petrolGeneratorInternalControlInboundPort.publishPort();

		this.petrolGeneratorExternalControlInboundPort = new PetrolGeneratorExternalControlInboundPort(
				petrolGeneratorExternalControlInboundPortURI, this);
		this.petrolGeneratorExternalControlInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Petrol Generator component");
			this.tracer.get().setRelativePosition(1, 1);
			this.toggleTracing();		
		}
	}

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.petrolGeneratorInternalControlInboundPort.unpublishPort();
			this.petrolGeneratorExternalControlInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean isProducing() throws Exception {
		if (PetrolGenerator.VERBOSE) {
			this.traceMessage("Petrol Generator returns its producing status: " + 
					(this.currentState == GeneratorState.PRODUCING) + ".\n");
		}

		assert (this.currentState == GeneratorState.ON) || (this.currentState == GeneratorState.PRODUCING) : new PreconditionException("(this.currentState == GeneratorState.ON) || (this.currentState == GeneratorState.PRODUCING)");

		return this.currentState == GeneratorState.PRODUCING;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void startProducing() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator starts producing.\n");
		}
		
		assert (this.currentState == GeneratorState.ON) : new PreconditionException("(this.currentState == GeneratorState.ON)");

		this.currentState = GeneratorState.PRODUCING;

		assert (this.currentState == GeneratorState.PRODUCING) : new PostconditionException("this.currentState == GeneratorState.PRODUCING");
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void stopProducing() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator stops producing.\n");
		}
		assert	(this.currentState == GeneratorState.PRODUCING) : new PreconditionException("(this.currentState == GeneratorState.PRODUCING) ");

		this.currentState = GeneratorState.ON;

		assert	!(this.currentState == GeneratorState.PRODUCING)  : new PostconditionException("!(this.currentState == GeneratorState.PRODUCING) ");

	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerProductionLevel() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator returns its max power production level " + 
					MAX_POWER_LEVEL_PRODUCTION + ".\n");
		}

		return MAX_POWER_LEVEL_PRODUCTION;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator returns its current power production level " + 
					this.currentPowerLevelProduction + ".\n");
		}

		double ret = this.currentPowerLevelProduction;

		assert	ret >= 0.0 && ret <= MAX_POWER_LEVEL_PRODUCTION :
			new PostconditionException(
					"return >= 0.0 && return <= MAX_POWER_LEVEL_PRODUCTION");

		return this.currentPowerLevelProduction;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPetrolLevel() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator returns its max fuel tank level " + 
					MAX_FUEL_TANK_LEVEL + ".\n");
		}

		return MAX_FUEL_TANK_LEVEL;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPetrolLevel() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator returns its current fuel tank level " + 
					this.currentFuelTankLevel + "L.\n");
		}

		double ret = this.currentFuelTankLevel;

		assert	ret >= 0.0 && ret <= MAX_FUEL_TANK_LEVEL :
			new PostconditionException(
					"return >= 0.0 && return <= getMaxPetrolLevel()");

		return this.currentFuelTankLevel;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOn() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator switches on.\n");
		}

		assert !(this.currentState == GeneratorState.ON): new PreconditionException("!(this.currentState == GeneratorState.ON)");

		this.currentState = GeneratorState.ON;

		assert (this.currentState == GeneratorState.ON) : new PostconditionException("(this.currentState == GeneratorState.ON)");
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOff() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator switches off.\n");
		}

		assert	(this.currentState == GeneratorState.ON) : new PreconditionException("this.currentState == GeneratorState.ON");

		this.currentState = GeneratorState.OFF;

		assert	 !(this.currentState == GeneratorState.ON) : new PostconditionException("!(this.currentState == GeneratorState.ON)");
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void fillFuelTank(double liters) throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator fills its petrol level with " + 
					liters + "L.\n");
		}

		assert	liters >= 0.0 : new PreconditionException("liters >= 0.0");

		if (this.currentFuelTankLevel+liters <= MAX_FUEL_TANK_LEVEL) {
			this.currentFuelTankLevel = getCurrentPetrolLevel()+liters;
		} else {
			this.currentFuelTankLevel = MAX_FUEL_TANK_LEVEL;
		}
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean on() throws Exception {
		if (Heater.VERBOSE) {
			this.traceMessage("Petrol Generator returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == GeneratorState.ON ||
									this.currentState == GeneratorState.PRODUCING;
	}
	
	/***********************************************************************************/
	// TODO fonction qui calcule combien de W on a produit depuis startProducing()
	// TODO fonction qui consomme les L d'essence quand le generateur est produit
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/



