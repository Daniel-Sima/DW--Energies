package production.intermittent.PetrolGenerator.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.HEM.simulation.HEM_ReportI;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import production.intermittent.PetrolGenerator.mil.events.DoNotProduce;
import production.intermittent.PetrolGenerator.mil.events.FillFuelTank;
import production.intermittent.PetrolGenerator.mil.events.PetrolGeneratorEventI;
import production.intermittent.PetrolGenerator.mil.events.Producing;
import production.intermittent.PetrolGenerator.mil.events.SwitchOffPetrolGenerator;
import production.intermittent.PetrolGenerator.mil.events.SwitchOnPetrolGenerator;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorElectricityModel</code> defines a simulation model
 * for the electricity production of the PetrolGenerator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power production (in Wh) depends upon the petrol quantity (5L) and 
 * its production capacity (2kWh).
 * </p>
 * 
 * <p>
 * Initially, the mode is in state {@code State.OFF} and the electric power
 * production is 0.0 Wh.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnPetrolGenerator},
 *   {@code SwitchOffPetrolGenerator},
 *   {@code Producing},
 *   {@code DoNotProduce},
 *   {@code FillFuelTank}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentPowerProducedPetrolGenerator}, type = {@code Double}</li> // TODO total?
 * </ul>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>	
 * invariant	{@code NOT_PRODUCING_POWER == 0.0}
 * invariant	{@code MAX_PRODUCING_POWER > NOT_PRODUCING_POWER}
 * invariant	{@code MAX_FUEL_TANK_LEVEL == 5}
 * </pre>
 * 
 * <p>Created on : 2023-11-12</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelExternalEvents(imported = {SwitchOnPetrolGenerator.class,
		SwitchOffPetrolGenerator.class,
		Producing.class,
		DoNotProduce.class,
		FillFuelTank.class})
@ModelExportedVariable(name = "currentPowerProducedPetrolGenerator", type = Double.class)
@ModelExportedVariable(name = "currentFuelTankLevel", type = Double.class)
public class PetrolGeneratorElectricityModel 
extends AtomicHIOA {

	// Declaring ANSI_RESET so that we can reset the color 
	public static final String ANSI_RESET = "\u001B[0m"; 
	// Declaring colors
	public static final String ANSI_CYAN = "\u001B[36m"; 

	// Declaring the background color 
	public static final String ANSI_RED_BACKGROUND  = "\u001B[41m"; 
	public static final String ANSI_BLACK_BACKGROUND  = "\033[40m"; 
	public static final String ANSI_GREY_BACKGROUND  = "\033[0;100m"; 
	public static final String ANSI_BLUE_BACKGROUND  = "\u001B[44m"; 

	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------
	/**
	 * The enumeration <code>GeneratorState</code> describes the operation
	 * states of the Petrol Generator.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2023-10-15</p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 */
	public static enum GeneratorState {
		/** petrol generator is on.											*/
		ON,
		/** petrol generator is producing.									*/
		PRODUCING,
		/** petrol generator  is off.										*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String URI = PetrolGeneratorElectricityModel.class.getSimpleName();

	/** power produced by the PetrolGenerator while not PRODUCING in Wh.	*/
	public static double NOT_PRODUCING_POWER = 0.0;
	/** power produced in 1h by the PetrolGenerator while PRODUCING in Wh.		*/
	public static double MAX_PRODUCING_POWER = 2000.0;
	/** max fuel tank level of the petrol generator, in L.				    */
	protected static final double MAX_FUEL_TANK_LEVEL = 5.0; // 5L
	/** fuel consumed in 1h of producing */
	protected static final double MAX_CONSOMATION_FUEL = 1.9; // 1.9L
	
	/** current state of the PetrolGenerator.								*/
	protected GeneratorState currentState = GeneratorState.OFF;
	/** true when the electricity production of the PetrolGenerator has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentPowerProducedPetrolGenerator</code>.				*/
	protected boolean productionHasChanged = false;

	/** total production of the PetrolGenerator during the simulation in Wh.*/
	protected double totalProduction;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current power produced between 0 and
	 *  {@code PetrolGeneratorElectricityModel.MAX_PRODUCING_POWER}.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentPowerProducedPetrolGenerator = new Value<Double>(this);

	/** the current level of fuel in the fuel tank */
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentFuelTankLevel = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a PetrolGenerator MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public PetrolGeneratorElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the PetrolGenerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 * @param t		time at which the state {@code s} is set.
	 */
	public void setState(GeneratorState s, Time t) {
		GeneratorState old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.productionHasChanged = true;					
		}
	}

	/***********************************************************************************/
	/**
	 * return the state of the PetrolGenerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public GeneratorState getState() {
		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * add fuel to the tank of the PetrolGenerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newQuantity >= 0.0}
	 * </pre>
	 *
	 * @param newQuantity	the new feul quantity in L to be added in the PetrolGenerator.
	 * @param t				time at which the new fuel quantity is added.
	 */
	public void addFuel(double newQuantity, Time t) {
		assert newQuantity >= 0.0 :
			new AssertionError(
					"Precondition violation: newQuantity >= 0.0"
							+ " but newQuantity = " + newQuantity);


		double oldVal = this.currentFuelTankLevel.getValue();
		if ((PetrolGeneratorElectricityModel.MAX_FUEL_TANK_LEVEL - this.currentFuelTankLevel.getValue().doubleValue()) <= newQuantity) {
			this.currentFuelTankLevel.setNewValue(PetrolGeneratorElectricityModel.MAX_FUEL_TANK_LEVEL, t); // fill
		} else {
			this.currentFuelTankLevel.setNewValue(this.currentFuelTankLevel.getValue().doubleValue() + newQuantity, t);
		}
		if (this.currentFuelTankLevel.getValue() != oldVal) {
			this.productionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.currentState = GeneratorState.OFF;
		this.productionHasChanged = false;
		this.totalProduction = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean useFixpointInitialiseVariables() {
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		if (!this.currentPowerProducedPetrolGenerator.isInitialised() ||
				!this.currentFuelTankLevel.isInitialised()) {
			// initially, the PetrolGenerator is off, so its consumption is zero.
			this.currentPowerProducedPetrolGenerator.initialise(0.0);
			// Initially filled
			this.currentFuelTankLevel.initialise(MAX_FUEL_TANK_LEVEL); 

			StringBuffer sb = new StringBuffer(ANSI_BLUE_BACKGROUND + "New production: ");
			sb.append(this.currentPowerProducedPetrolGenerator.getValue());
			sb.append(" Wh at ");
			sb.append(this.currentPowerProducedPetrolGenerator.getTime());
			sb.append("\n" + ANSI_RESET);
			this.logMessage(sb.toString());
			return new Pair<>(2, 0);
		} else {
			return new Pair<>(0, 0);
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		return null;
	}


	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		if (this.productionHasChanged) {
			// When the production has changed, an immediate (delay = 0.0)
			// internal transition must be made to update the electricity
			// production.
			this.productionHasChanged = false;
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// As long as the state does not change, no internal transition
			// is made (delay = infinity).
			return Duration.INFINITY;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		if (this.currentState == GeneratorState.ON) {
			this.currentPowerProducedPetrolGenerator.setNewValue(
					PetrolGeneratorElectricityModel.NOT_PRODUCING_POWER, t);
		} else if (this.currentState == GeneratorState.PRODUCING) {
			this.currentPowerProducedPetrolGenerator.setNewValue(
					MAX_PRODUCING_POWER, t); // 2000Wh  // TODO AR qd pas par heure
			
		} else {
			assert this.currentState == GeneratorState.OFF;
			this.currentPowerProducedPetrolGenerator.setNewValue(0.0, t);
		}

		StringBuffer sb = new StringBuffer(ANSI_BLUE_BACKGROUND + "Current production: ");
		sb.append(this.currentPowerProducedPetrolGenerator.getValue());
		sb.append(" Wh at ");
		sb.append(this.currentPowerProducedPetrolGenerator.getTime());
		sb.append("\n" + ANSI_RESET);
		this.logMessage(sb.toString());
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the PetrolGenerator model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof PetrolGeneratorEventI;

		// compute the total consumption for the simulation report.
		this.totalProduction += this.currentPowerProducedPetrolGenerator.getValue(); 
		// TODO AV qd on produit sur une periode plus courte (pas juste 1h)
		if (this.currentPowerProducedPetrolGenerator.getValue()  > 0) {
			this.currentFuelTankLevel.setNewValue(this.currentFuelTankLevel.getValue().doubleValue()-MAX_CONSOMATION_FUEL, this.getCurrentStateTime());
		}


		
		StringBuffer st = new StringBuffer(ANSI_BLUE_BACKGROUND + "Total production: ");
		st.append(this.totalProduction);
		st.append(" Wh at ");
		st.append(this.currentPowerProducedPetrolGenerator.getTime());
		st.append("\n" + ANSI_RESET);
		this.logMessage(st.toString());
		
		StringBuffer stt = new StringBuffer(ANSI_GREY_BACKGROUND + "Total fuel level: ");
		stt.append(Math.round(this.currentFuelTankLevel.getValue() * 100.0) / 100.0);
		stt.append(" L at ");
		stt.append(this.currentFuelTankLevel.getTime());
		stt.append("\n" + ANSI_RESET);
		this.logMessage(stt.toString());


		StringBuffer sb = new StringBuffer(ANSI_BLACK_BACKGROUND + "Execute the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n" + ANSI_RESET);
		this.logMessage(sb.toString());

		// the next call will update the current state of the PetrolGenerator and if
		// this state has changed, it put the boolean productionHasChanged
		// at true, which in turn will trigger an immediate internal transition
		// to update the current power produced of the PetrolGenerator electricity
		// production.
		ce.executeOn(this);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		this.totalProduction += this.currentPowerProducedPetrolGenerator.getValue(); // TODO AR si ici
		
		this.logMessage((new PetrolGeneratorElectricityReport(URI, this.totalProduction).printout("-e")));

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** power produced by the PetrolGenerator in Wh while only ON.										*/
	public static final String	NOT_PRODUCTING_POWER_RUNPNAME = "NOT_PRODUCING_POWER";
	/** power produced by the PetrolGenerator  in Wh while PRODUCING.										*/
	public static final String	MAX_PRODUCING_POWER_RUNPNAME = "MAX_PRODUCING_POWER";

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void	setSimulationRunParameters(
			Map<String, Serializable> simParams
			) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String notProducingName =
				ModelI.createRunParameterName(getURI(), NOT_PRODUCTING_POWER_RUNPNAME);
		if (simParams.containsKey(notProducingName)) {
			NOT_PRODUCING_POWER = (double) simParams.get(notProducingName);
		}
		String producingName =
				ModelI.createRunParameterName(getURI(), MAX_PRODUCING_POWER_RUNPNAME);
		if (simParams.containsKey(producingName)) {
			MAX_PRODUCING_POWER = (double) simParams.get(producingName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PetrolGeneratorElectricityReport</code> implements the
	 * simulation report for the <code>PetrolGeneratorElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * 
	 */
	public static class PetrolGeneratorElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalProduction; // in kwh

		/***********************************************************************************/
		public PetrolGeneratorElectricityReport(
				String modelURI,
				double totalProduction
				)
		{
			super();
			this.modelURI = modelURI;
			this.totalProduction = totalProduction;
		}

		/***********************************************************************************/
		@Override
		public String getModelURI() {
			return this.modelURI;
		}

		/***********************************************************************************/
		@Override
		public String printout(String indent) {
			StringBuffer ret = new StringBuffer(indent);
			ret.append("\n---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total production in Wh = ");
			ret.append(this.totalProduction);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}		
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() {
		return new PetrolGeneratorElectricityReport(URI, this.totalProduction);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
