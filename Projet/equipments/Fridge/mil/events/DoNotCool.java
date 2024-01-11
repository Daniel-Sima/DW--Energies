package equipments.Fridge.mil.events;

import equipments.Fridge.mil.FridgeElectricityModel;
import equipments.Fridge.mil.FridgeTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
//-----------------------------------------------------------------------------
/**
 * The class <code>DoNotCool</code> defines the simulation event of the
 * Fridge stopping to cool.
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
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class DoNotCool 
extends Event
implements FridgeEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>DoNotCool</code> event.
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
	public DoNotCool(Time timeOfOccurrence) {
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
		// if many Fridge events occur at the same time, the DoNotCool one
		// will be executed first except for SwitchOnFridge ones.
		if (e instanceof SwitchOnFridge) {
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
		assert	model instanceof FridgeElectricityModel ||
		model instanceof FridgeTemperatureModel;

		if (model instanceof FridgeElectricityModel) {
			FridgeElectricityModel m = (FridgeElectricityModel)model;
			assert	m.getState() == FridgeElectricityModel.FridgeState.COOLING:
				new AssertionError(
						"model not in the right state, should be "
								+ "FridgeElectricityModel.FridgeState.COOLING but is "
								+ m.getState());
			m.setState(FridgeElectricityModel.FridgeState.ON,
					this.getTimeOfOccurrence());
		} else {
			FridgeTemperatureModel m = (FridgeTemperatureModel)model;
			assert	m.getState() == FridgeTemperatureModel.State.COOLING:
				new AssertionError(
						"model not in the right state, should be "
								+ "FridgeTemperatureModel.State.COOLING but is "
								+ m.getState());
			m.setState(FridgeTemperatureModel.State.NOT_COOLING);
		}
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

