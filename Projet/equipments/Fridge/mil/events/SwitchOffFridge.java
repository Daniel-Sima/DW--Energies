package equipments.Fridge.mil.events;

import equipments.Fridge.mil.FridgeElectricityModel;
import equipments.Fridge.mil.FridgeTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SwitchOffFridge</code> defines the simulation event of the
 * Fridge being switched off.
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
public class SwitchOffFridge extends ES_Event
implements FridgeEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffFridge</code> event.
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
	public SwitchOffFridge(Time timeOfOccurrence) {
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
		// if many Fridge events occur at the same time, the
		// SwitchOffFridge one will be executed after all others.
		return false;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert model instanceof FridgeElectricityModel ||
		model instanceof FridgeTemperatureModel;

		if (model instanceof FridgeElectricityModel) {
			FridgeElectricityModel m = (FridgeElectricityModel)model;
			assert	m.getState() != FridgeElectricityModel.FridgeState.ON :
				new AssertionError(
						"model not in the right state, should not be "
								+ "FridgeElectricityModel.FridgeState.ON but is "
								+ m.getState());
			m.setState(FridgeElectricityModel.FridgeState.OFF,
					this.getTimeOfOccurrence());
		} else {
			FridgeTemperatureModel m = (FridgeTemperatureModel)model;
			m.setState(FridgeTemperatureModel.State.NOT_COOLING);
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
