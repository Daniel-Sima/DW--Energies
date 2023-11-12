package equipments.Fridge.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import equipments.Fridge.mil.events.FridgeEventI;
import equipments.Fridge.mil.events.Cool;
import equipments.Fridge.mil.events.DoNotCool;
import equipments.Fridge.mil.events.SwitchOffFridge;
import equipments.HEM.simulation.HEM_ReportI;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
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
 * The class <code>FridgeTemperatureModel</code> defines a simulation model
 * for the temperature inside the fridge.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model is implemented as an atomic HIOA model. A differential equation
 * defines the temperature variation over time. 
 * </p>
 * <ol>
 * <li>the temperature outside the fridge where the coefficient
 *   applied to the difference between the outside temperature and the
 *   current temperature models the thermal insulation of the walls
 *   ({@code INSULATION_TRANSFER_CONSTANT});</li>
 * <li>the temperature of the AirConditionnin when it cools where the coefficient
 *   applied to the difference between the Fridge temperature
 *   ({@code STANDARD_COOLING_TEMP}) and the current temperature models the
 *   cool diffusion over the fridge
 *   ({@code COOLING_TRANSFER_CONSTANT}); the cooling diffusion is not constant
 *   but rather proportional to the current power level of the Fridge.</li>
 * </ol>
 * <p>
 * The resulting differential equation is integrated using the Euler method
 * with a predefined integration step. The initial state of the model is
 * a state not cooling and the initial temperature given by
 * {@code INITIAL_TEMPERATURE}.
 * </p>
 * <p>
 * Whether the current temperature evolves under the influence of the outside
 * temperature only or also the cooling temperature depends upon the state,
 * which in turn is modified through the reception of imported events
 * {@code Cool} and {@code DoNotCool}. The internal temperature is imported
 * from another model simulating the environment. The current temperature is
 * exported to be used by other models.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOffAirConditionning},
 *   {@code CoolCooler},
 *   {@code CoolFreezer},
 *   {@code DoNotCoolCooler},
 *   {@code DoNotCoolFreezer}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code internalTemperature}, type = {@code Double}</li>
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
 * invariant	{@code STEP > 0.0}
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@ModelExternalEvents(imported = {SwitchOffFridge.class,
		Cool.class,
		DoNotCool.class})
@ModelImportedVariable(name = "internalTemperature", type = Double.class)
@ModelImportedVariable(name = "currentCoolingPower", type = Double.class)
@ModelExportedVariable(name = "fridgeTemperature", type = Double.class)
public class FridgeTemperatureModel 
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
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>State</code> defines the state in which the
	 * Fridge can be from the temperature perspective.
	 *
	 */
	public static enum State {
		/** Fridge is not cooling.									*/
		NOT_COOLING,
		/** Fridge is on and cooling.						*/
		COOLING,
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// The following variables should be considered constant but can be changed
	// before the first model instance is created to adapt the simulation
	// scenario.

	/** URI for a model; works when only one instance is created.			*/
	public static String URI = FridgeTemperatureModel.class. getSimpleName();
	/** temperature of the fridge when the simulation begins.			*/
	public static double INITIAL_TEMPERATURE = 10.005;
	/** fridge wall insulation heat transfer constant in the differential equation.*/
	protected static double INSULATION_TRANSFER_CONSTANT = 20;
	/** cooling transfer constant in the differential equation when the
	 *  cooling power is maximal.											*/
	protected static double MIN_COOLING_TRANSFER_CONSTANT = 30.0;
	/** temperature of the cooling in the Fridge.					*/
	protected static double STANDARD_COOLING_TEMP = -150.0; // TODO AR
	/** update tolerance for the temperature <i>i.e.</i>, shortest elapsed
	 *  time since the last update under which the temperature is not
	 *  changed by the update to avoid too large computation errors.		*/
	protected static double TEMPERATURE_UPDATE_TOLERANCE = 0.0001;
	/** the minimal power under which the temperature derivative must be 0.	*/
	protected static double POWER_HEAT_TRANSFER_TOLERANCE = 0.0001;
	/** integration step for the differential equation(assumed in hours).	*/
	protected static double	STEP = 60.0/3600.0;	// 60 seconds

	/** current state of the Fridge.								*/
	protected State currentState = State.NOT_COOLING;

	// Simulation run variables

	/** integration step as a duration, including the time unit.			*/
	protected final Duration integrationStep;
	/** accumulator to compute the mean internal temperature for the
	 *  simulation report.													*/
	protected double temperatureAcc;
	/** the simulation time of start used to compute the mean temperature.	*/
	protected Time start;
	/** the mean temperature over the simulation duration for the simulation
	 *  report.																*/
	protected double meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current internal temperature in Celsius.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>	internalTemperature;
	/** the current cooling power between 0 and
	 *  {@code FridgeElectricityModel.MAX_COOLING_POWER}.			*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>	currentCoolingPower;
	/** current temperature in the fridge.									*/
	@InternalVariable(type = Double.class)
	protected final DerivableValue<Double> currentTemperature = new DerivableValue<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>FridgeTemperatureModel</code> instance.
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
	public FridgeTemperatureModel(
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
	 * set the state of the Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void setState(State s) {
		this.currentState = s;
	}

	/***********************************************************************************/
	/**
	 * return the state of the Fridge.
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
	public State getState() {
		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * compute the current heat transfer constant given the current cooling
	 * power of the Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the current heat transfer constant.
	 */
	protected double currentHeatTransfertConstant() {
		// the following formula is just a mathematical trick to get a heat
		// transfer constant that grows as the power gets lower, hence the
		// derivative given by the differential equation will be lower when
		// the power gets **higher**, what is physically awaited.
		double c = MIN_COOLING_TRANSFER_CONSTANT * FridgeElectricityModel.MAX_COOLING_POWER;
		return c/this.currentCoolingPower.getValue();
	}

	/***********************************************************************************/
	/**
	 * compute the current derivative of the fridge temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current temperature of the fridge.
	 * @return			the current derivative.
	 */
	protected double computeDerivatives(Double current) {
		double currentTempDerivative = 0.0;
		if (this.currentState == State.COOLING) {
			// the cooling contribution: temperature difference between the
			// cooling temperature and the fridge temperature divided by the
			// heat transfer constant taking into account the size of the
			// fridge
			if (this.currentCoolingPower.getValue() > POWER_HEAT_TRANSFER_TOLERANCE) {
				currentTempDerivative = (STANDARD_COOLING_TEMP - current)/this.currentHeatTransfertConstant();
			}
		}

		// the heating contribution: difference between the internal temperature
		// and the temperature of the fridge divided by the insulation transfer
		// constant taking into account the surface of the walls.
		Time t = this.getCurrentStateTime();
		currentTempDerivative += (this.internalTemperature.evaluateAt(t) - current)
								/INSULATION_TRANSFER_CONSTANT;
		
		return currentTempDerivative;
	}

	/***********************************************************************************/
	/**
	 * compute the current temperature given that a duration of {@code deltaT}
	 * has elapsed since the last update.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code deltaT >= 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param deltaT	the duration of the step since the last update.
	 * @return			the new temperature in celsius.
	 */
	protected double computeNewTemperature(double deltaT) {
		Time t = this.currentTemperature.getTime();
		double oldTemp = this.currentTemperature.evaluateAt(t);
		double newTemp;

		if (deltaT > TEMPERATURE_UPDATE_TOLERANCE) {
			// update the fridge temperature using the Euler integration of the
			// differential equation
			double derivative = this.currentTemperature.getFirstDerivative();
			newTemp = oldTemp + derivative*deltaT;
		} else {
			newTemp = oldTemp;
		}

		// accumulate the temperature*time to compute the mean temperature
		this.temperatureAcc += ((oldTemp + newTemp)/2.0) * deltaT;
		return newTemp;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.temperatureAcc = 0.0;
		this.start = initialTime;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");

		super.initialiseState(initialTime);
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
		int justInitialised = 0;
		int notInitialisedYet = 0;

		// Only one variable must be initialised, the current temperature, and
		// it depends upon only one variable, the internal temperature.
		if (!this.currentTemperature.isInitialised() &&
				this.internalTemperature.isInitialised()) {
			// If the current temperature is not initialised yet but the
			// internal temperature is, then initialise the current temperature
			// and say one more variable is initialised at this execution.
			double derivative = this.computeDerivatives(INITIAL_TEMPERATURE);
			this.currentTemperature.initialise(INITIAL_TEMPERATURE, derivative);
			justInitialised++;
		} else if (!this.currentTemperature.isInitialised()) {
			// If the internal temperature is not initialised and the current
			// temperature either, then say one more variable has not been
			// initialised yet at this execution, forcing another execution
			// to reach the fix point.
			notInitialisedYet++;
		}

		return new Pair<>(justInitialised, notInitialisedYet);
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
		return this.integrationStep;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		// First, update the temperature (i.e., the value of the continuous
		// variable) until the current time.
		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		// Next, compute the new derivative
		double newDerivative = this.computeDerivatives(newTemp);
		// Finally, set the new temperature value and derivative
		this.currentTemperature.setNewValue(
				newTemp,
				newDerivative,
				new Time(this.getCurrentStateTime().getSimulatedTime(),
						this.getSimulatedTimeUnit()));

		// Tracing
		String mark = this.currentState == State.COOLING ? "is COOLING" : "is NOT COOLING";
		StringBuffer message = new StringBuffer();
		message.append((Math.round(this.currentTemperature.getValue() *100.0) / 100.0) + "° in the fridge and ");
		message.append(mark);
		message.append(" at " + this.currentTemperature.getTime());
		message.append('\n' + ANSI_RESET);
		this.logMessage(message.toString());

		super.userDefinedInternalTransition(elapsedTime);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the Fridge model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof FridgeEventI;

		StringBuffer sb = new StringBuffer("executing the internal event: ");
		sb.append(ce.eventAsString());
		sb.append(".\n");
		this.logMessage(sb.toString());

		// First, update the temperature (i.e., the value of the continuous
		// variable) until the current time.
		double newTemp =
				this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		// Then, update the current state of the Fridge.
		ce.executeOn(this);
		// Next, compute the new derivative
		double newDerivative = this.computeDerivatives(newTemp);
		// Finally, set the new temperature value and derivative
		this.currentTemperature.setNewValue(
				newTemp,
				newDerivative,
				new Time(this.getCurrentStateTime().getSimulatedTime()
						+ elapsedTime.getSimulatedDuration(),
						this.getSimulatedTimeUnit()));

		super.userDefinedExternalTransition(elapsedTime);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		this.meanTemperature =
				this.temperatureAcc/
				endTime.subtract(this.start).getSimulatedDuration();

		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>FridgeTemperatureReport</code> implements the
	 * simulation report for the <code>FridgeTemperatureModel</code>.
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
	 */
	public static class FridgeTemperatureReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double meanTemperature;

		/***********************************************************************************/
		public FridgeTemperatureReport(
				String modelURI,
				double meanTemperature
				)
		{
			super();
			this.modelURI = modelURI;
			this.meanTemperature = meanTemperature;
		}

		/***********************************************************************************/
		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		/***********************************************************************************/
		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("mean temperature = ");
			ret.append(this.meanTemperature);
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
		return new FridgeTemperatureReport(URI, this.meanTemperature);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

