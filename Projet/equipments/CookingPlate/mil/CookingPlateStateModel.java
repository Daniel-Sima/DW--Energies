package equipments.CookingPlate.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.CookingPlate;
import equipments.CookingPlate.CookingPlateOperationI;
import equipments.CookingPlate.mil.CookingPlateElectricityModel.CookingPlateState;
import equipments.CookingPlate.mil.events.AbstractCookingPlateEvent;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>CookingPlateStateModel</code> defines a simulation model
 * tracking the state changes on a CookingPlate.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model receives event from the CookingPlate component (corresponding to
 * calls to operations on the CookingPlate in this component), keeps track of
 * the current state of the CookingPlate in the simulation and then emits the
 * received events again towards another model simulating the electricity
 * consumption of the CookingPlate given its current operating state (switched
 * on/off, high/low power mode).
 * </p>
 * <p>
 * This model becomes necessary in a SIL simulation of the household energy
 * management system because the electricity model must be put in the electric
 * meter component to share variables with other electricity models so this
 * state model will serve as a bridge between the models put in the CookingPlate
 * component and its electricity model put in the electric meter component.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnCookingPlate},
 *   {@code SwitchOffCookingPlate},
 *   {@code SetLowCookingPlate},
 *   {@code SetHighCookingPlate}</li>
 * <li>Exported events:
 *   {@code SwitchOnCookingPlate},
 *   {@code SwitchOffCookingPlate},
 *   {@code SetLowCookingPlate},
 *   {@code SetHighCookingPlate}</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: none</li>
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
 * <p>Created on : 2021-10-04</p>
 * 
 * @author walte  
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {SwitchOnCookingPlate.class,SwitchOffCookingPlate.class,
				DecreaseCookingPlate.class,IncreaseCookingPlate.class},
	exported = {SwitchOnCookingPlate.class,SwitchOffCookingPlate.class,
				DecreaseCookingPlate.class,IncreaseCookingPlate.class}
	)
// -----------------------------------------------------------------------------
public class			CookingPlateStateModel
extends		AtomicModel
implements	CookingPlateOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model in MIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	MIL_URI = CookingPlateStateModel.class.
													getSimpleName() + "-MIL";
	/** URI for an instance model in MIL real time simulations; works as
	 *  long as only one instance is created.								*/
	public static final String	MIL_RT_URI = CookingPlateStateModel.class.
													getSimpleName() + "-MIL_RT";
	/** URI for an instance model in SIL simulations; works as long as
	 *  only one instance is created.										*/
	public static final String	SIL_URI = CookingPlateStateModel.class.
													getSimpleName() + "-SIL";

	/** current state of the CookingPlate.									*/
	protected CookingPlateState						currentState;
	/** current mode of the CookingPlate.									*/
	protected int									currentMode;
	/** last received event or null if none.								*/
	protected AbstractCookingPlateEvent			lastReceived;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a CookingPlate state model instance.
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
	 * @throws Exception		<i>to do</i>.
	 */
	public				CookingPlateStateModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		// set the logger to a standard simulation logger
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.CookingPlate.mil.CookingPlateOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == CookingPlateElectricityModel.CookingPlateState.OFF) {
			// then put it in the state LOW
			this.currentState = CookingPlateElectricityModel.CookingPlateState.ON;
			this.currentMode = 0;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.CookingPlate.mil.CookingPlateOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		// a SwitchOff event can be executed when the state of the hair
		// dryer model is *not* in the state OFF
		if (this.currentState != CookingPlateElectricityModel.CookingPlateState.OFF) {
			// then put it in the state OFF
			this.currentState = CookingPlateElectricityModel.CookingPlateState.OFF;
			this.currentMode = 0;
		}
	}

	/**
	 * @see equipments.CookingPlate.mil.CookingPlateOperationI#increaseMode()
	 */
	@Override
	public void			increaseMode()
	{
		if (this.currentState == CookingPlateElectricityModel.CookingPlateState.ON) {
			if (this.currentMode < CookingPlate.MAX_MODES) {
				this.currentMode++;
			}
		}
	}

	/**
	 * @see equipments.CookingPlate.mil.CookingPlateOperationI#decreaseMode()
	 */
	@Override
	public void			decreaseMode()
	{
		// a SetLow event can only be executed when the state of the hair
		// dryer model is in the state HIGH
		if (this.currentState == CookingPlateElectricityModel.CookingPlateState.ON) {
			if (this.currentMode > 0) {
				this.currentMode--;
			}
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentState = CookingPlateState.OFF;

		this.getSimulationEngine() .toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		assert	this.lastReceived != null;

		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}


	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null) {
			// trigger an immediate internal transition
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// wait until the next external event that will trigger an internal
			// transition
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the CookingPlate model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		// this will trigger an internal transition by the fact that
		// lastReceived will not be null; the internal transition does nothing
		// on the model state except to put lastReceived to null again, but
		// this will also trigger output and the sending of the event to
		// the electricity model to also change its state
		this.lastReceived = (AbstractCookingPlateEvent) currentEvents.get(0);

		// tracing
		StringBuffer message = new StringBuffer(this.uri);
		message.append(" executes the external event ");
		message.append(this.lastReceived);
		message.append('\n');
		this.logMessage(message.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
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
	}
}
// -----------------------------------------------------------------------------
