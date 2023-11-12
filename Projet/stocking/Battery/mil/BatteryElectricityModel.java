package stocking.Battery.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.HEM.simulation.HEM_ReportI;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>BatteryElectricityModel</code> defines a simulation model
 * for the electricity production/consumption of the Battery.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power production (in Wats) depends upon the production components 
 * (SolarPanel and PetrolGenerator). The consumption depends upon the others components
 * such as CookingPlate, Lamp, AirConditioniong and Fridge.
 * .
 * </p>
 * <p>
 * Initially, the electric power stored is at 0.0 Wh.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code totalPowerStored}, type = {@code Double}</li> // TODO others?
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
 * </pre>
 * 
 * <p>Created on : 2023-11-12</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelImportedVariable(name = "currentPowerProducedSolarPanel", type = Double.class)
 @ModelImportedVariable(name = "currentPowerProducedPetrolGenerator", type = Double.class)
@ModelExportedVariable(name = "totalPowerStored", type = Double.class)
public class BatteryElectricityModel 
extends	AtomicHIOA {
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
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>BatteryState</code> describes the operation
	 * states of the Battery.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 */
	public static enum BatteryState {
		/** Battery is producing (destocking energy).						*/
		PRODUCING,
		/** Battery is consuming (stocking energy).							*/
		CONSUMING,
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String URI = BatteryElectricityModel.class.getSimpleName();

	/** max power produced in Watts.										*/
	public static double MAX_POWER_CAPACITY = 5000.0;

	/** nominal tension (in Volts).											*/
	public static double TENSION = 220.0;

	/** integration step as a duration, including the time unit.			*/
	protected final Duration integrationStep;
	/** integration step for the differential equation(assumed in hours).	*/
	protected static double	STEP = (10* 60.0)/3600.0;	// 60 seconds * 10 = 10 min

	/** current state of the Battery.										*/
	protected BatteryState currentState = BatteryState.CONSUMING;

	/** boolean that informs if the state has changed 						*/
	protected boolean stateHasChanged = false;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the total power stores in the battery power produced 				*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> totalPowerStored = new Value<Double>(this);

	/** current power produced by the SolarPanel in Wh.						*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>	 currentPowerProducedSolarPanel; 
	
	/** current power produced by the PetrolGenerator in Wh.				*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>	 currentPowerProducedPetrolGenerator; 
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater MIL model instance.
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
	public BatteryElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the state of the Battery.
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
	public void	setState(BatteryState s, Time t) {
		BatteryState old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.stateHasChanged = true;					
		}
	}

	/***********************************************************************************/
	/**
	 * return the state of the Battery.
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
	public BatteryState getState() {
		return this.currentState;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean useFixpointInitialiseVariables(){
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		if (!this.totalPowerStored.isInitialised()) {
			// initially, the Solar Panel starts with 0 production.
			this.totalPowerStored.initialise(0.0);

			StringBuffer sb = new StringBuffer("New total power stored: ");
			sb.append(this.totalPowerStored.getValue());
			sb.append(" Wh at ");
			sb.append(this.totalPowerStored.getTime());
			sb.append("\n");
			this.logMessage(sb.toString());
			return new Pair<>(1, 0); // TODO AR
		} else {
			return new Pair<>(0, 0);
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output()
	{
		return null;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration	timeAdvance() {
		return this.integrationStep;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		// adding power stored from the Solar Panel
		this.totalPowerStored.setNewValue(currentPowerProducedSolarPanel.getValue().doubleValue() + 
				this.totalPowerStored.getValue().doubleValue(), this.totalPowerStored.getTime()); 
		// adding power stored from the Petrol Generator
		this.totalPowerStored.setNewValue(currentPowerProducedPetrolGenerator.getValue().doubleValue() + 
				this.totalPowerStored.getValue().doubleValue(), this.totalPowerStored.getTime()); 
		
		// Tracing
		StringBuffer message1 = new StringBuffer();	
		message1.append(ANSI_BLUE_BACKGROUND + "Current power stored from the Solar Panel: ");
		message1.append((Math.round(currentPowerProducedSolarPanel.getValue().doubleValue()* 100.0) / 100.0) + " Wh");
		message1.append(" at " + this.currentPowerProducedSolarPanel.getTime());
		message1.append("\n" + ANSI_RESET);
		this.logMessage(message1.toString());
		
		StringBuffer message2 = new StringBuffer();	
		message2.append(ANSI_BLUE_BACKGROUND + "Current power stored from the Petrol Generator: ");
		message2.append((Math.round(currentPowerProducedPetrolGenerator.getValue().doubleValue()* 100.0) / 100.0) + " Wh");
		message2.append(" at " + this.currentPowerProducedPetrolGenerator.getTime());
		message2.append("\n" + ANSI_RESET);
		this.logMessage(message2.toString());

		StringBuffer message = new StringBuffer();	
		message.append(ANSI_BLUE_BACKGROUND + "Total power stored: ");
		message.append((Math.round(this.totalPowerStored.getValue().doubleValue() * 100.0) / 100.0) + " Wh");
		message.append(" at " + this.currentPowerProducedSolarPanel.getTime());
		message.append("\n" + ANSI_RESET);
		this.logMessage(message.toString());

		// reset the energy produced after the battery has stored it
		this.currentPowerProducedPetrolGenerator.setNewValue(0.0, this.currentPowerProducedPetrolGenerator.getTime());

		super.userDefinedInternalTransition(elapsedTime);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	endSimulation(Time endTime) {
		this.logMessage("simulation ends.\n");
		this.logMessage(new BatteryElectricityReport(URI, Math.round(this.totalPowerStored.getValue().doubleValue() * 100.0) / 100.0).printout("-"));
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------
	/** maximum power proudced by Solar Panel in watts.										*/
	public static final String	MAX_POWER_CAPACITY_RUNPNAME = "MAX_POWER_CAPACITY";
	/** nominal tension (in Volts).															*/
	public static final String	TENSION_RUNPNAME = "TENSION";

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

		String storingName =
				ModelI.createRunParameterName(getURI(), MAX_POWER_CAPACITY_RUNPNAME);
		if (simParams.containsKey(storingName)) {
			MAX_POWER_CAPACITY = (double) simParams.get(storingName);
		}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RUNPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>BatteryElectricityReport</code> implements the
	 * simulation report for the <code>BatteryElectricityModel</code>.
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
	 */
	public static class BatteryElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalStored; // in kwh



		/***********************************************************************************/
		public BatteryElectricityReport(
				String modelURI,
				double totalStored
				)
		{
			super();
			this.modelURI = modelURI;
			this.totalStored = totalStored;
		}

		/***********************************************************************************/
		@Override
		public String getModelURI() {
			return this.modelURI;
		}

		/***********************************************************************************/
		@Override
		public String printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("\n---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total stored in Wh = ");
			ret.append(this.totalStored);
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
		return new BatteryElectricityReport(URI, this.totalPowerStored.getValue().doubleValue());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
