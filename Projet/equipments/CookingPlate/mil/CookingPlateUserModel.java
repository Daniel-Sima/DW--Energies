package equipments.CookingPlate.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateUserModel</code> defines a very simple user
 * model for the Cooking Plate.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is meant to illustrate how to program MIL models simulating user
 * actions by sending events to other models.
 * </p>
 * <p>
 * Here, we use an event scheduling atomic model to output events at random
 * time intervals in a predefined cycle to test all of the different modes in
 * the Cooking Plate. Note that the exported events are indeed subclasses of
 * event scheduling events {@code ES_Event}. Hence, this example also shows
 * how to program this type of event scheduling simulation models.
 * </p>
 * <p>
 * Event scheduling models are constructed around an event list that contains
 * events scheduled to be executed at future time in the simulation time. The
 * class {@code AtomicES_Model} hence defines the main methods to execute
 * the transitions. Internal transitions simply occurs at the time of the next
 * event in the event list and then, if the event is internal, execute that
 * event on the model (by calling the method {@code executeOn} defined on the
 * event. If the next event is external, it must be emitted towards other
 * models. This is performed by the method {@code AtomicES_Model#output}
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnCookingPlate},
 *   {@code SwitchOffCookingPlate},
 *   {@code IncreaseCookingPlate},
 *   {@code DecreaseCookingPlate}</li>
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(exported = {SwitchOnCookingPlate.class,
		SwitchOffCookingPlate.class,
		IncreaseCookingPlate.class,
		DecreaseCookingPlate.class})
public class CookingPlateUserModel 
extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String	URI = CookingPlateUserModel.class.getSimpleName();

	/** time interval between event outputs in hours.						*/
	protected static double STEP_MEAN_DURATION = 60.0/3600.0;
	/** time interval between Cooking Plate usages in hours.				*/
	protected static double	DELAY_MEAN_DURATION = 4.0;
	
	/** Times left to increase/decrease */
	protected static int timesLeft = 0;
	/** Try to increase boolean */
	protected static boolean increase = true;
	
	
	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a Cooking Plate user MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do.</i>
	 */
	public CookingPlateUserModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * generate the next event in the test scenario; current implementation
	 * cycles through {@code SwitchOnCookingPlate}, {@code IncreaseCookingPlate},
	 * {@code DecreaseCookingPlate} and {@code SwitchOffCookingPlate} in this order
	 * at a random time interval following a gaussian distribution with
	 * mean {@code STEP_MEAN_DURATION} and standard deviation
	 * {@code STEP_MEAN_DURATION/2.0}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code eventList.peek() != null}
	 * post	{@code eventList.peek() != null}
	 * </pre>
	 *
	 */
	protected void generateNextEvent() {
		EventI current = this.eventList.peek();
		// compute the next event type given the current event
		ES_EventI nextEvent = null;
		if (current instanceof SwitchOffCookingPlate) {
			// compute the time of occurrence for the next Cooking Plate usage
			Time t2 = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			nextEvent = new SwitchOnCookingPlate(t2);
		} else {
			// compute the time of occurrence for the next event
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			if (current instanceof SwitchOnCookingPlate) {
				nextEvent = new IncreaseCookingPlate(t);
			} else if ((current instanceof IncreaseCookingPlate) && (increase)){
				nextEvent = new IncreaseCookingPlate(t);
				timesLeft++;
				increase = timesLeft == 6 ? false : true;
			} else if ((current instanceof IncreaseCookingPlate) && (!increase)) {
				nextEvent = new DecreaseCookingPlate(t);
			} else if ((current instanceof DecreaseCookingPlate) && (!increase)) {
				nextEvent = new DecreaseCookingPlate(t);
				timesLeft--;
				increase = timesLeft == 0 ? true : false;
			} else if ((current instanceof DecreaseCookingPlate) && (increase)) {
				nextEvent = new SwitchOffCookingPlate(t);
			}
		}
		// schedule the event to be executed by this model
		this.scheduleEvent(nextEvent);
	}

	/***********************************************************************************/
	/**
	 * compute the time of the next event, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time computeTimeOfNextEvent(Time from) {
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
				STEP_MEAN_DURATION/2.0),
				0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	/***********************************************************************************/
	/**
	 * compute the time of the next Cooking Plate usage, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time computeTimeOfNextUsage(Time from) {
		assert	from != null;

		// generate randomly the next time interval but force it to be
		// greater than 0 by returning at least 0.1 
		double delay = Math.max(this.rg.nextGaussian(DELAY_MEAN_DURATION,
				DELAY_MEAN_DURATION/10.0),
				0.1);
		// compute the new time by adding the delay to from
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void	initialiseState(Time initialTime) {
		super.initialiseState(initialTime);

		this.rg.reSeedSecure();

		// compute the time of occurrence for the first event
		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		// schedule the first event
		this.scheduleEvent(new SwitchOnCookingPlate(t));
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// generate and schedule the next event
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
		// this will extract the next event from the event list and emit it
		return super.output();
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------
	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String MEAN_DELAY_RPNAME = "STEP_MEAN_DURATION";

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(
			Map<String, Serializable> simParams
			) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String stepName =
				ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) {
			STEP_MEAN_DURATION = (double) simParams.get(stepName);
		}
		String delayName =
				ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) {
			DELAY_MEAN_DURATION = (double) simParams.get(delayName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
