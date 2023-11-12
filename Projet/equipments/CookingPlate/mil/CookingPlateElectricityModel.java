package equipments.CookingPlate.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.mil.events.AbstractCookingPlateEvent;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.HEM.simulation.HEM_ReportI;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import utils.Electricity;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateElectricityModel</code> defines a MIL model
 * of the electricity consumption of a Cooking Plate.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The Cooking Plate can be switched on and off, and when switched on, it can be
 * either in 1 (50°) to 7 (300°) mode, with electricity consumption growing up with
 * the mode.
 * </p>
 * <p>
 * The electricity consumption is represented as a variable of type double that
 * has to be exported towards the electric meter MIL model in order to be summed
 * up to get the global electricity consumption of the house.
 * </p>
 * <p>
 * To model the user actions, four events are defined to be imported and the
 * external transitions upon the reception of these events force the Cooking Plate
 * electricity model in the corresponding mode with the corresponding
 * electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnCookingPlate},
 *   {@code SwitchOffCookingPlate},
 *   {@code IncreaseCookingPlate},
 *   {@code DecreaseCookingPlate}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
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
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-08</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelExternalEvents(imported = {SwitchOnCookingPlate.class,
		SwitchOffCookingPlate.class,
		IncreaseCookingPlate.class,
		DecreaseCookingPlate.class
})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
public class CookingPlateElectricityModel 
extends	AtomicHIOA {
	// -------------------------------------------------------------------------
	// Color for prints
	// -------------------------------------------------------------------------

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
	 * The enumeration <code>CookingPlateState</code> describes the operation states
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum CookingPlateState {
		/** cooking plate is on. */
		ON,
		/** cooking plate is off. */
		OFF
	}

	/***********************************************************************************/
	/**
	 * Array of <code>CookingPlateMode</code> describes the operation modes
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * The cooking plate has 8 modes, from 0 to 7, from the coldest to the hottest temperature.
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static int[] CookingPlateTemperature = new int[] {0, 50, 80, 120, 160, 200, 250, 300};

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** URI for an instance model; works as long as only one instance is
	 *  created.																	*/
	public static final String URI = CookingPlateElectricityModel.class.getSimpleName();

	/** energy consumption (in Watts) of the Cooking Plate depending the mode.		*/
	public static double[] CookingPlateEnergyConsumption = new double[] {4.0, 500.0, 800.0, 1000.0, 1200.0, 1500.0, 1800.0, 2000.0};

	/** nominal tension (in Volts) in Europe										*/
	public static double TENSION = 220.0; // Volts

	/** current mode of operation (1 (50°) to 7 (300°)) of the cooking plate.			*/
	protected int currentMode = 0; // turned off 

	/** current state (ON, OFF) of the Cooking Plate.								*/
	protected CookingPlateState	currentState = CookingPlateState.OFF;

	/** true when the electricity consumption of the Cooking Plate has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.							*/
	protected boolean consumptionHasChanged = false;

	/** total consumption of the Cooking Plate during the simulation in kwh.		*/
	protected double totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------
	/** current intensity in amperes; intensity is power/tension.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a Cooking PlateMIL model instance.
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
	public CookingPlateElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * set the state of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code getState() == s}
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void setState(CookingPlateState s) {
		this.currentState = s;
	}

	/***********************************************************************************/
	/**
	 * set the mode of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code getState() == s}
	 * </pre>
	 *
	 * @param m		the new mode.
	 */
	public void setMode(int m) {
		this.currentMode = m;
	}

	/***********************************************************************************/
	/**
	 * return the state of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the state of the Cooking Plate.
	 */
	public CookingPlateState getState() {
		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * return the mode of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the mode of the Cooking Plate.
	 */
	public int getMode() {
		return this.currentMode;
	}

	/***********************************************************************************/
	/**
	 * toggle the value of the state of the model telling whether the
	 * electricity consumption level has just changed or not; when it changes
	 * after receiving an external event, an immediate internal transition
	 * is triggered to update the level of electricity consumption.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void	toggleConsumptionHasChanged() {
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	initialiseState(Time startTime) {
		super.initialiseState(startTime);

		// initially the Cooking Plate is off and its electricity consumption is
		// not about to change.
		this.currentState = CookingPlateState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void initialiseVariables() {
		super.initialiseVariables();

		// initially, the Cooking Plate is off, so its consumption is zero.
		this.currentIntensity.initialise(0.0);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// the model does not export events.
		return null;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration	timeAdvance() {
		// to trigger an internal transition after an external transition, the
		// variable consumptionHasChanged is set to true, hence when it is true
		// return a zero delay otherwise return an infinite delay (no internal
		// transition expected)
		if (this.consumptionHasChanged) {
			// after triggering the internal transition, toggle the boolean
			// to prepare for the next internal transition.
			this.toggleConsumptionHasChanged();
			return new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
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

		// set the current electricity consumption from the current state
		Time t = this.getCurrentStateTime();
		switch (this.currentState) {
		case OFF : this.currentIntensity.setNewValue(0.0, t); break;
		case ON : switch (this.currentMode) {
		case 0 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[0]/TENSION, t); break;			
		case 1 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[1]/TENSION, t); break;
		case 2 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[2]/TENSION, t); break;
		case 3 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[3]/TENSION, t); break;
		case 4 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[4]/TENSION, t); break;
		case 5 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[5]/TENSION, t); break;
		case 6 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[6]/TENSION, t); break;
		case 7 : this.currentIntensity.setNewValue(CookingPlateEnergyConsumption[7]/TENSION, t); break;
		}
		}
		// Tracing
		StringBuffer message = new StringBuffer(ANSI_GREY_BACKGROUND + "Current consumption ");
		message.append(Math.round(this.currentIntensity.getValue() * 100.0) / 100.0);
		message.append(" Amperes at " + this.currentIntensity.getTime() + ".\n" + ANSI_RESET);
		this.logMessage(message.toString());
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */ // TODO AR
	@Override
	public void	userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current Cooking Plate model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// compute the total consumption (in kwh) for the simulation report.
		this.totalConsumption +=
				Electricity.computeConsumption(
						elapsedTime,
						TENSION*this.currentIntensity.getValue());

		// Tracing
		StringBuffer message =
				new StringBuffer(ANSI_BLACK_BACKGROUND + "Execute the external event: " + ce.toString() + ")\n" + ANSI_RESET);
		this.logMessage(message.toString());

		assert	ce instanceof AbstractCookingPlateEvent;
		// events have a method execute on to perform their effect on this
		// model
		ce.executeOn(this);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
						d,
						TENSION*this.currentIntensity.getValue());

		this.logMessage("\n" + (new CookingPlateElectricityReport(URI, Math.round(this.totalConsumption * 100.0)/100.0)).printout("-"));
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------
	/** run parameter name for {@code MODE_CONSUMPTION}.				*/
	public static final String MODE_CONSUMPTION_RPNAME = URI + ":MODE_CONSUMPTION";
	/** run parameter name for {@code TENSION}.								*/
	public static final String TENSION_RPNAME = URI + ":TENSION";

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(
			Map<String, Serializable> simParams
			) throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);

		// Setting for each mode
		for (int i=0; i<CookingPlateTemperature.length; i++) {
			String modeName = ModelI.createRunParameterName(getURI(), "" + i + MODE_CONSUMPTION_RPNAME);
			System.out.println("==> "+"" + i + MODE_CONSUMPTION_RPNAME);
			if (simParams.containsKey(modeName)) {
				CookingPlateTemperature[i] = (int) simParams.get(modeName);
			}
		}

		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------
	/**
	 * The class <code>CookingPlateElectricityReport</code> implements the
	 * simulation report for the <code>CookingPlateElectricityModel</code>.
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
	 * <p>Created on : 2023-11-11</p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 */
	public static class CookingPlateElectricityReport
	implements	SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption; // in kwh

		/***********************************************************************************/
		public CookingPlateElectricityReport(
				String modelURI,
				double totalConsumption
				)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		/***********************************************************************************/
		@Override
		public String getModelURI() {
			return modelURI;
		}

		/***********************************************************************************/
		@Override
		public String printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kWh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() {
		return new CookingPlateElectricityReport(URI, this.totalConsumption);
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
