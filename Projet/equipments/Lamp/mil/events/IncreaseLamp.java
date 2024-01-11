package equipments.Lamp.mil.events;

import equipments.Lamp.mil.LampElectricityModel;
import equipments.Lamp.mil.LampElectricityModel.State;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>IncreaseLamp</code> defines the simulation event of the
 * Cooking Plate being increased to high temperature mode.
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
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class IncreaseLamp 
extends AbstractLampEvent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a <code>IncreaseLamp</code> event.
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
	public IncreaseLamp(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		// if many Cooking Plate events occur at the same time, the
		// IncreaseLamp one will be executed after SwitchOnLamp
		// and DecreaseLamp ones but before SwitchOffLamp.
		if (e instanceof SwitchOnLamp || e instanceof DecreaseLamp) {
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
	public void executeOn(AtomicModelI model) {
		assert model instanceof LampElectricityModel;

		LampElectricityModel m = (LampElectricityModel)model;

		// a Increase event can only be executed when the state of Lamp
		// model is in the state LOW or MEDIUM
		if (m.getState() == LampElectricityModel.State.LOW) {
			// then put it in the state MEDIUM
			m.setState(State.MEDIUM);
			m.toggleConsumptionHasChanged(); 
		} else if (m.getState() == LampElectricityModel.State.MEDIUM) {
			// then put it in the state HIGH
			m.setState(State.HIGH); 
			m.toggleConsumptionHasChanged();
		}
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
