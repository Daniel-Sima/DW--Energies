package production.aleatory.SolarPanel.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.HEM.simulation.HEM_ReportI;
//import utils.Electricity;
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
 * The class <code>SolarPanelElectricityModel</code> defines a simulation model
 * for the electricity production of the SOlar Panel.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power production (in Wats) depends upon the sunshine (solar radiation) 
 * of the day.
 * </p>
 * <p>
 * Initially, the electric power production is at 0.0 W.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentPowerProducedSolarPanel}, type = {@code Double}</li> // TODO total?
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
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelImportedVariable(name = "externalSolarIrradiance", type = Double.class)
@ModelExportedVariable(name = "currentPowerProducedSolarPanel", type = Double.class) // TODO change name
public class SolarPanelElectricityModel 
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
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = SolarPanelElectricityModel.class.getSimpleName();

	/** minimum power produced in watts.									*/
	public static double NOT_PRODUCING_POWER = 0.0;
	/** max power produced in watts.										*/
	public static double MAX_PRODUCING_POWER = 400.0;

	/** total production of the Solar Panel during the simulation in kWh.		*/
	protected double totalProduction;

	/** nominal tension (in Volts).												*/
	public static double TENSION = 220.0;

	/** integration step as a duration, including the time unit.				*/
	protected final Duration integrationStep;
	/** integration step for the differential equation(assumed in hours).		*/
	protected static double	STEP = 60.0/3600.0;	// 60 seconds 

	/** size of the solar panel in m^2 */
	protected final double SIZE_SOLAR_PANEL = 2; 
	
	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current power produced  Wh										*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentPowerProducedSolarPanel = new Value<Double>(this);

	/** current external solar irradiance in W/m^2.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>	 externalSolarIrradiance;

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
	public SolarPanelElectricityModel(
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
	 * return the total power produced of the Solar Panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	total power produced.
	 */
	public double getTotalPowerProduced(){
		return this.totalProduction;
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

		this.totalProduction = 0.0;

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
		if (!this.currentPowerProducedSolarPanel.isInitialised()) {
			// initially, the Solar Panel starts with 0 production.
			this.currentPowerProducedSolarPanel.initialise(0.0);

			StringBuffer sb = new StringBuffer("new production: ");
			sb.append(this.currentPowerProducedSolarPanel.getValue());
			sb.append(" amperes at ");
			sb.append(this.currentPowerProducedSolarPanel.getTime());
			sb.append(" seconds.\n");
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
		// Formula: total power (Wh) = solar irradiance (W/m^2) * surface (m^2) * period(h)
		double currentPowerProduction = (this.externalSolarIrradiance.getValue().doubleValue()) * SIZE_SOLAR_PANEL;
		this.totalProduction += currentPowerProduction*elapsedTime.getSimulatedDuration();
		this.currentPowerProducedSolarPanel.setNewValue(currentPowerProduction*elapsedTime.getSimulatedDuration(), this.externalSolarIrradiance.getTime());

		// Tracing
		StringBuffer message1 = new StringBuffer();	
		message1.append(ANSI_BLUE_BACKGROUND + "Current power production: ");
		message1.append((Math.round(currentPowerProduction*elapsedTime.getSimulatedDuration() * 100.0) / 100.0) + " Wh");
		message1.append(" at " + this.externalSolarIrradiance.getTime());
		message1.append("\n" + ANSI_RESET);
		this.logMessage(message1.toString());
		
		StringBuffer message = new StringBuffer();	
		message.append(ANSI_BLUE_BACKGROUND + "Total power production: ");
		message.append((Math.round(totalProduction * 100.0) / 100.0) + " Wh");
		message.append(" at " + this.externalSolarIrradiance.getTime());
		message.append("\n" + ANSI_RESET);
		this.logMessage(message.toString());


		super.userDefinedInternalTransition(elapsedTime);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	endSimulation(Time endTime) {
//		Duration d = endTime.subtract(this.getCurrentStateTime());
//		this.totalProduction +=
//				Electricity.computeConsumption(
//						d,
//						TENSION*this.currentPowerProducedSolarPanel.getValue());

		this.logMessage("simulation ends.\n");
		this.logMessage(new SolarPanelElectricityReport(URI, Math.round(this.totalProduction * 100.0) / 100.0).printout("-"));
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** minimum power produced by the Solar Panel in watts.									*/
	public static final String	NOT_PRODUCING_POWER_RUNPNAME = "NOT_PRODUCING_POWER";
	/** maximum power proudced by Solar Panel in watts.										*/
	public static final String	MAX_PRODUCING_POWER_RUNPNAME = "MAX_PRODUCING_POWER";
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

		String notProducingName =
				ModelI.createRunParameterName(getURI(), NOT_PRODUCING_POWER_RUNPNAME);
		if (simParams.containsKey(notProducingName)) {
			NOT_PRODUCING_POWER = (double) simParams.get(notProducingName);
		}
		String producingName =
				ModelI.createRunParameterName(getURI(), MAX_PRODUCING_POWER_RUNPNAME);
		if (simParams.containsKey(producingName)) {
			MAX_PRODUCING_POWER = (double) simParams.get(producingName);
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
	 * The class <code>SolarPanelElectricityReport</code> implements the
	 * simulation report for the <code>SolarPanelElectricityModel</code>.
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
	public static class SolarPanelElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalProduction; // in kwh


		/***********************************************************************************/
		public SolarPanelElectricityReport(
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
		return new SolarPanelElectricityReport(URI, this.totalProduction);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
