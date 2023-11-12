package equipments.AirConditioning.mil.events;

import equipments.AirConditioning.mil.AirConditioningElectricityModel;
import equipments.AirConditioning.mil.AirConditioningTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SwitchOffAirConditioning</code> defines the simulation event of the
 * AirConditioning being switched off.
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
public class SwitchOffAirConditioning extends ES_Event
implements AirConditioningEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffAirConditioning</code> event.
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
	public SwitchOffAirConditioning(Time timeOfOccurrence) {
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
		// if many AirConditioning events occur at the same time, the
		// SwitchOffAirConditioning one will be executed after all others.
		return false;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert model instanceof AirConditioningElectricityModel ||
		model instanceof AirConditioningTemperatureModel;

		if (model instanceof AirConditioningElectricityModel) {
			AirConditioningElectricityModel m = (AirConditioningElectricityModel)model;
			assert	m.getState() != AirConditioningElectricityModel.AirConditioningState.ON :
				new AssertionError(
						"model not in the right state, should not be "
								+ "AirConditioningElectricityModel.AirConditioningState.ON but is "
								+ m.getState());
			m.setState(AirConditioningElectricityModel.AirConditioningState.OFF,
					this.getTimeOfOccurrence());
		} else {
			AirConditioningTemperatureModel m = (AirConditioningTemperatureModel)model;
			m.setState(AirConditioningTemperatureModel.State.NOT_COOLING);
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
