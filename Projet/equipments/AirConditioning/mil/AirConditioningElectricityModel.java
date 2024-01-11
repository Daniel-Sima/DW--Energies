package equipments.AirConditioning.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.mil.AirConditioningStateModel.State;
import equipments.AirConditioning.mil.events.AirConditioningEventI;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.HEM.simulation.HEM_ReportI;
import utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
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

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>AirConditioningElectricityModel</code> defines a simulation model
 * for the electricity consumption of the AirConditioning.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The electric power consumption (in amperes) depends upon the state 'AirConditioningState' 
 * and the current power level i.e., {@code AirConditioningState.OFF => consumption == 0.0},
 * {@code AirConditioningState.ON => consumption == NOT_COOLING_POWER} and
 * {@code AirConditioningState.COOLING => consumption >= NOT_COOLING_POWER && consumption <= MAX_COOLING_POWER}).
 * The state of the AirConditioning is modified by the reception of external events
 * ({@code SwitchOnAirConditioning}, {@code SwitchOffAirConditioning}, {@code Cool} and
 * {@code DoNotCool}). The power level is set through the external event
 * {@code SetPowerAirConditioning} that has a parameter defining the required power
 * level. The electric power consumption is stored in the exported variable
 * {@code currentIntensity}.
 * </p>
 * <p>
 * Initially, the mode is in state {@code AirConditioningState.OFF} and the electric power
 * consumption at 0.0.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnAirConditioning},
 *   {@code SwitchOffAirConditioning},
 *   {@code SetPowerAirConditioning},
 *   {@code Cool},
 *   {@code DoNotCool}</li>
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
 * invariant	{@code NOT_COOLING_POWER >= 0.0}
 * invariant	{@code MAX_COOLING_POWER > NOT_COOLING_POWER}
 * invariant	{@code TENSION > 0.0}
 * </pre>
 * 
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@ModelExternalEvents(imported = {SwitchOnAirConditioning.class,
								SwitchOffAirConditioning.class,
								SetPowerAirConditioning.class,
								Cool.class,
								DoNotCool.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
public class 		AirConditioningElectricityModel 
extends 		AtomicHIOA 
implements		AirConditioningOperationI
{
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
	/** URI for a MIL model; works when only one instance is created.		*/
	public static final String	MIL_URI = AirConditioningElectricityModel.class.
												getSimpleName() + "-MIL";
	/** URI for a MIL real time model; works when only one instance is
	*  created.															*/
	public static final String	MIL_RT_URI = AirConditioningElectricityModel.class.
												getSimpleName() + "-MIL-RT";
	/** URI for a SIL model; works when only one instance is created.		*/
	public static final String	SIL_URI = AirConditioningElectricityModel.class.
												getSimpleName() + "-SIL";
	
	/** power of the AirConditioning in watts.										*/
	public static double 		NOT_COOLING_POWER = 5.0;
	/** max power of the AirConditioning in watts.									*/
	public static double 		MAX_COOLING_POWER = 2000.0;
	/** nominal tension (in Volts) of the AirConditioning.							*/
	public static double 		TENSION = 220.0;
	/** initial power at which the air conditioning is set.							*/
	public static double		INITIAL_COOLING_POWER = MAX_COOLING_POWER;

	/** current state of the AirConditioning.												*/
	protected State currentState = State.OFF;
	/** true when the electricity consumption of the AirConditioning has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.							*/
	protected boolean 			consumptionHasChanged = false;

	/** total consumption of the AirConditioning during the simulation in kwh.		*/
	protected double			totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------
	
	/** current intensity in amperes; intensity is power/tension.					*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double> 		currentIntensity = 
												new Value<Double>(this);
	/** the current AirConditioning power between 0 and
	 *  {@code AirConditioningElectricityModel.MAX_COOLING_POWER}.					*/
	@InternalVariable(type = Double.class)
	protected final Value<Double> 		currentCoolingPower = 
												new Value<Double>(this);
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a AirConditioning MIL model instance.
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
	public 		AirConditioningElectricityModel(
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
	 * set the state of the AirConditioning.
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
	public void 		setState(State s) {
		State old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.consumptionHasChanged = true;					
		}
	}

	/***********************************************************************************/
	/**
	 * return the state of the AirConditioning.
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
	public State		 getState() {
		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * set the current cooling power of the AirConditioning to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_COOLING_POWER}
	 * post	{@code getCurrentPowerLevel() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the AirConditioning.
	 * @param t			time at which the new power is set.
	 */
	public void setCurrentCoolingPower(double newPower, Time t) {
		assert	newPower >= 0.0 &&
				newPower <= AirConditioningElectricityModel.MAX_COOLING_POWER :
			new AssertionError(
					"Precondition violation: newPower >= 0.0 && "
							+ "newPower <= AirConditioningElectricityModel.MAX_COOLING_POWER,"
							+ " but newPower = " + newPower);

		double oldPower = this.currentCoolingPower.getValue();
		this.currentCoolingPower.setNewValue(newPower, t);
		if (newPower != oldPower) {
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
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.currentState = State.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

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
		if (!this.currentIntensity.isInitialised() ||
									!this.currentCoolingPower.isInitialised()) {
			// initially, the AirConditioning is off, so its consumption is zero.
			this.currentIntensity.initialise(0.0);
			this.currentCoolingPower.initialise(INITIAL_COOLING_POWER);
			System.out.println(this.currentCoolingPower);
			StringBuffer sb = new StringBuffer("new consumption: ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" amperes at ");
			sb.append(this.currentIntensity.getTime());
			sb.append(" seconds.\n");
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
		if (this.consumptionHasChanged) {
			// When the consumption has changed, an immediate (delay = 0.0)
			// internal transition must be made to update the electricity
			// consumption.
			this.consumptionHasChanged = false;
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
		if (this.currentState == State.ON) {
			this.currentIntensity.setNewValue(
					AirConditioningElectricityModel.NOT_COOLING_POWER/
					AirConditioningElectricityModel.TENSION,
					t);
		} else if (this.currentState == State.COOLING) {
			this.currentIntensity.setNewValue(
					this.currentCoolingPower.getValue()/
					AirConditioningElectricityModel.TENSION,
					t);
		} else {
			assert this.currentState == State.OFF;
			this.currentIntensity.setNewValue(0.0, t);
		}

		StringBuffer sb = new StringBuffer("new consumption: ");
		sb.append((Math.round(this.currentIntensity.getValue() * 100.0) / 100.0));
		sb.append(" Amperes at ");
		sb.append(this.currentIntensity.getTime());
		sb.append(" seconds.\n");
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
		// and for the AirConditioning model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert ce instanceof AirConditioningEventI;

		// compute the total consumption for the simulation report.
		this.totalConsumption +=
				Electricity.computeConsumption(
						elapsedTime,
						TENSION*this.currentIntensity.getValue());

		StringBuffer sb = new StringBuffer(ANSI_BLACK_BACKGROUND + "Execute the external event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n" + ANSI_RESET);
		this.logMessage(sb.toString());

		// the next call will update the current state of the AirConditioning and if
		// this state has changed, it put the boolean consumptionHasChanged
		// at true, which in turn will trigger an immediate internal transition
		// to update the current intensity of the AirConditioning electricity
		// consumption.
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

		this.logMessage("simulation ends.\n");
		this.logMessage(new AirConditioningElectricityReport(MIL_URI, (Math.round(this.totalConsumption * 100.0) / 100.0)*1000.0).printout("-"));
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** power of the AirConditioning in watts.										*/
	public static final String	NOT_COOLING_POWER_RUNPNAME = "NOT_COOLING_POWER";
	/** power of the AirConditioning in watts.										*/
	public static final String	MAX_COOLING_POWER_RUNPNAME = "MAX_COOLING_POWER";
	/** nominal tension (in Volts) of the AirConditioning.							*/
	public static final String	TENSION_RUNPNAME = "TENSION";

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(
			Map<String, Object> simParams
			) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String notCoolingName = ModelI.createRunParameterName(getURI(), NOT_COOLING_POWER_RUNPNAME);
		if (simParams.containsKey(notCoolingName)) {
			NOT_COOLING_POWER = (double) simParams.get(notCoolingName);
		}
		String coolingName = ModelI.createRunParameterName(getURI(), MAX_COOLING_POWER_RUNPNAME);
		if (simParams.containsKey(coolingName)) {
			MAX_COOLING_POWER = (double) simParams.get(coolingName);
		}

		String tensionName = ModelI.createRunParameterName(getURI(), TENSION_RUNPNAME);
		if (simParams.containsKey(tensionName)) {
			TENSION = (double) simParams.get(tensionName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>AirConditioningElectricityReport</code> implements the
	 * simulation report for the <code>AirConditioningElectricityModel</code>.
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
	public static class AirConditioningElectricityReport
	implements	SimulationReportI, HEM_ReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption; // in kwh

		/***********************************************************************************/
		public AirConditioningElectricityReport(
				String modelURI,
				double totalConsumption
				) {
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
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
			ret.append("total consumption in Wh = ");
			ret.append(this.totalConsumption);
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
		return new AirConditioningElectricityReport(this.getURI(), this.totalConsumption);
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
