package equipments.Lamp.mil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.HEM.simulation.HEM_ReportI;
import equipments.Lamp.LampOperationI;
import equipments.Lamp.mil.events.AbstractLampEvent;
import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
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

// -----------------------------------------------------------------------------
/**
 * The class <code>LampElectricityModel</code> defines a MIL model
 * of the electricity consumption of a lamps.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The lamp can be switched on and off, and when switched on, it can be
 * either in a low mode, with lower electricity consumption, or a high mode,
 * with a higher electricity consumption.
 * </p>
 * <p>
 * The electricity consumption is represented as a variable of type double that
 * has to be exported towards the electric meter MIL model in order to be summed
 * up to get the global electricity consumption of the house.
 * </p>
 * <p>
 * To model the user actions, four events are defined to be imported and the
 * external transitions upon the reception of these events force the lamp
 * electricity model in the corresponding mode with the corresponding
 * electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOffLamp},
 *   {@code SwitchOnLamp},
 *   {@code IncreaseLamp},
 *   {@code DecreaseLamp}
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
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@ModelExternalEvents(imported = {SwitchOffLamp.class,
		 					     SwitchOnLamp.class,
								 IncreaseLamp.class,
								 DecreaseLamp.class})
@ModelExportedVariable(name = "currentLampIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			LampElectricityModel
extends		AtomicHIOA
implements LampOperationI
{
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
	// Inner classes and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>LampState</code> describes the discrete states or
	 * modes of the lamp.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * The lamp can be <code>OFF</code> or on, and then it is either in
	 * <code>LOW</code> mode (20%) or in
	 * <code>MEDIUM</code> mode (50%) or in
	 * <code>HIGH</code> mode (100%).
	 * 
	 * <p>Created on : 2019-10-10</p>
	 * 
	 * @author	<a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum LampState {
		OFF,
		/** low mode is less light and less consuming.							*/
		LOW,			
		/** medium mode is 50% light and consuming normally.					*/
		MEDIUM,	
		/** high mode is brighter and more consuming.							*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	private static final long		serialVersionUID = 1L;

	/** URI for an instance model in MIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	MIL_URI = LampElectricityModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as  only one instance is created.								*/
	public static final String	MIL_RT_URI = LampElectricityModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *   only one instance is created.										*/
	public static final String	SIL_URI = LampElectricityModel.class.
													getSimpleName() + "-SIL";

	/** energy consumption (in Watts) of the Lamp depending the mode.		*/
	public static HashMap<LampState, Double> LampEnergyConsumption = new HashMap<LampState, Double>() {{
		put(LampState.LOW,65.0); 
		put(LampState.MEDIUM,110.0);
		put(LampState.HIGH,230.0);}
	};


	/** nominal tension (in Volts) of the lamp.						*/
	public static double TENSION = 220; // Volts

	/** current LampState (OFF, LOW, HIGH) of the lamp.					*/
	protected LampState					currentState = LampState.OFF;
	/** true when the electricity consumption of the lamp has changed
	 *  after executing an external event; the external event changes the
	 *  value of <code>currentState</code> and then an internal transition
	 *  will be triggered by putting through in this variable which will
	 *  update the variable <code>currentIntensity</code>.					*/
	protected boolean				consumptionHasChanged = false;

	/** total consumption of the lamp during the simulation in kwh.	*/
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity in amperes; intensity is power/tension.			*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a lamp MIL model instance.
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
	public				LampElectricityModel(
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
	 * @see equipments.Lamp.mil.LampOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == LampElectricityModel.LampState.OFF) {
			// then put it in the state LOW
			this.currentState = LampElectricityModel.LampState.LOW;
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}

	/**
	 * @see equipments.Lamp.mil.LampOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		// a SwitchOff event can be executed when the state of the hair
		// dryer model is *not* in the state OFF
		if (this.currentState != LampElectricityModel.LampState.OFF) {
			// then put it in the state OFF
			this.currentState = LampElectricityModel.LampState.OFF;
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}

	/**
	 * @see equipments.Lamp.mil.LampOperationI#increaseMode()
	 */
	@Override
	public void			increaseMode()
	{
		// a IncreaseLamp event can be executed when the state of the lamp
		// model is *not* in the state OFF
		if (this.currentState != LampElectricityModel.LampState.OFF) {
			// then put it in the next state
			switch (this.currentState) {
				case LOW : this.currentState = LampElectricityModel.LampState.MEDIUM;
							break;
				case MEDIUM : this.currentState = LampElectricityModel.LampState.HIGH;
							break;
				default : break;
			}
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}
	
	/**
	 * @see equipments.Lamp.mil.LampOperationI#decreaseMode()
	 */
	@Override
	public void			decreaseMode()
	{
		// a DecreaseLamp event can be executed when the state of the lamp
		// model is *not* in the LampState OFF
		if (this.currentState != LampElectricityModel.LampState.OFF) {
			// then put it in the next state
			switch (this.currentState) {
				case MEDIUM : this.currentState = LampElectricityModel.LampState.LOW;
							break;
				case HIGH : this.currentState = LampElectricityModel.LampState.MEDIUM;
							break;
				default : break;
			}
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			this.toggleConsumptionHasChanged();
		}
	}

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
	public void			toggleConsumptionHasChanged()
	{
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
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		// initially the lamp is off and its electricity consumption is
		// not about to change.
		this.currentState = LampState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		// initially, the lamp is off, so its consumption is zero.
		this.currentIntensity.initialise(0.0);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// the model does not export events.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
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

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// set the current electricity consumption from the current state
		Time t = this.getCurrentStateTime();
		switch (this.currentState)
		{
			case OFF : 	this.currentIntensity.setNewValue(0.0, t); break;
			case LOW : 	this.currentIntensity.
							setNewValue(LampEnergyConsumption.get(LampState.LOW)/TENSION, t);
						break;
			case MEDIUM : this.currentIntensity.
							setNewValue(LampEnergyConsumption.get(LampState.MEDIUM)/TENSION, t);
						break;
			case HIGH : this.currentIntensity.
							setNewValue(LampEnergyConsumption.get(LampState.HIGH)/TENSION, t);
		}

		// Tracing
		StringBuffer message =
				new StringBuffer(ANSI_RED_BACKGROUND +"Current consumption ");
		message.append((Math.round(this.currentIntensity.getValue()* 100.0) / 100.0) );
		message.append(" Amperes (Total: " + 
		(Math.round(this.totalConsumption * 100.0) / 100.0) + " Wh" + ") at ");
		message.append(this.currentIntensity.getTime());
		message.append(".\n" + ANSI_RESET);
		this.logMessage(message.toString());
	}

	/**
	 * The method is overridden to take into account the electricity consumption
	 * of the Lamp when it is switched on and off and when its mode is
	 * changed.
	 * @param elapsedTime	time duration of the external event.
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of currently received external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the current lamp model, there must be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// compute the total consumption (in Wh) for the simulation report.
		this.totalConsumption +=
				Electricity.computeConsumption(
									elapsedTime,
									TENSION*this.currentIntensity.getValue());

		// Tracing
		StringBuffer message =
				new StringBuffer(ANSI_BLACK_BACKGROUND + "Executes an external transition ");
		message.append(ce.toString());
		message.append(")\n" + ANSI_RESET);
		this.logMessage(message.toString());

		assert	ce instanceof AbstractLampEvent;
		// events have a method execute on to perform their effect on this
		// model
		ce.executeOn(this);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
									d,
									TENSION*this.currentIntensity.getValue());

		this.logMessage("simulation ends.\n");	
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code LOW_MODE_CONSUMPTION}.				*/
	public static final String		LOW_MODE_CONSUMPTION_RPNAME =
												"LOW_MODE_CONSUMPTION";
	/** run parameter name for {@code MEDIUM_MODE_CONSUMPTION}.				*/
	public static final String		MEDIUM_MODE_CONSUMPTION_RPNAME =
												"MEDIUM_MODE_CONSUMPTION";
	/** run parameter name for {@code HIGH_MODE_CONSUMPTION}.				*/
	public static final String		HIGH_MODE_CONSUMPTION_RPNAME =
												"HIGH_MODE_CONSUMPTION";
	/** run parameter name for {@code TENSION}.								*/
	public static final String		TENSION_RPNAME = "TENSION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		// this gets the reference on the owner component which is required
		// to have simulation models able to make the component perform some
		// operations or tasks or to get the value of variables held by the
		// component when necessary.
		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			// by the following, all of the logging will appear in the owner
			// component logger
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}

		String lowName =
			ModelI.createRunParameterName(getURI(),
										  LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			LampEnergyConsumption.put(LampState.LOW,(double) simParams.get(lowName));
		}
		String mediumName =
			ModelI.createRunParameterName(getURI(),
										  MEDIUM_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(mediumName)) {
			LampEnergyConsumption.put(LampState.MEDIUM,(double) simParams.get(mediumName));
		}
		String highName =
			ModelI.createRunParameterName(getURI(),
										  HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			LampEnergyConsumption.put(LampState.HIGH,(double) simParams.get(highName));
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
	 * The class <code>LampElectricityReport</code> implements the
	 * simulation report for the <code>LampElectricityModel</code>.
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
	 * <p>Created on : 2023-09-29</p>
	 * 
	 * @author	<a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static class		LampElectricityReport
	implements	SimulationReportI, HEM_ReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in Wh

		public				LampElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String		getModelURI()
		{
			return null;
		}

		@Override
		public String		printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
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

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new LampElectricityReport(this.getURI(), this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
