package equipments.AirConditioning.mil.events;

import equipments.AirConditioning.mil.AirConditioningOperationI;
import equipments.AirConditioning.mil.AirConditioningStateModel.State;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>Cool</code> defines the simulation event of the AirConditioning
 * starting to cool.
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
public class Cool 
extends Event
implements AirConditioningEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>Cool</code> event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public Cool(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		// if many AirConditioning events occur at the same time, the Cool one will be
		// executed after SwitchOnAirConditioning and DoNotCool ones but before
		// SwitchOffAirConditioning.
		if (e instanceof SwitchOnAirConditioning || e instanceof DoNotCool) {
			return false;
		} else {
			return true;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void 	executeOn(AtomicModelI model) 
	{
		assert	model instanceof AirConditioningOperationI;

		AirConditioningOperationI ac = (AirConditioningOperationI)model;
		assert	ac.getState() == State.ON:
				new AssertionError(
							"model " + model.getClass().getSimpleName()
							+ " not in the right state, should be "
							+ "AirConditioningElectricityModel.State.ON but is "
							+ ac.getState());
		ac.setState(State.COOLING);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

