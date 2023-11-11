package equipments.CookingPlate.mil.events;

import equipments.CookingPlate.mil.CookingPlateElectricityModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>DecreaseCookingPlate</code> defines the simulation event of the
 * Cooking Plate being decreased to a lower temperature mode.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class DecreaseCookingPlate 
extends AbstractCookingPlateEvent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>DecreaseCookingPlate</code> event.
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
	public DecreaseCookingPlate(Time timeOfOccurrence) {
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
		// if many Cooking Plates events occur at the same time, the
		// DecreaseCookingPlate one will be executed first except for
		// SwitchOnCookingPlate ones.
		if (e instanceof SwitchOnCookingPlate) {
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
		assert	model instanceof CookingPlateElectricityModel;

		CookingPlateElectricityModel m = (CookingPlateElectricityModel)model;
		// a Decrease event can only be executed when the state of the 
		// Cooking Plate model is in the state ON
		if (m.getState() == CookingPlateElectricityModel.CookingPlateState.ON) {
			// then put it in the state LOW
			m.setMode(m.getMode() - 1); // max 8 modes // TODO AV
			// trigger an internal transition by toggling the electricity
			// consumption changed boolean to true
			m.toggleConsumptionHasChanged();
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
