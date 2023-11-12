package production.intermittent.PetrolGenerator.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import production.intermittent.PetrolGenerator.mil.PetrolGeneratorElectricityModel;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SwitchOffPetrolGenerator</code> defines the simulation event of the
 * PetrolGenerator being switched off.
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
 * <p>Created on : 2023-11-12</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class SwitchOffPetrolGenerator 
extends ES_Event
implements PetrolGeneratorEventI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * create a <code>SwitchOffPetrolGenerator</code> event.
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
	public SwitchOffPetrolGenerator(Time timeOfOccurrence) {
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
		// if many PetrolGenerator events occur at the same time, the
		// SwitchOffPetrolGenerator one will be executed after all others.
		return false;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert	model instanceof PetrolGeneratorElectricityModel;

		PetrolGeneratorElectricityModel m = (PetrolGeneratorElectricityModel)model;
		assert	m.getState() != PetrolGeneratorElectricityModel.GeneratorState.ON :
			new AssertionError(
					"model not in the right state, should not be "
							+ "PetrolGeneratorElectricityModel.GeneratorState.ON but is "
							+ m.getState());
		m.setState(PetrolGeneratorElectricityModel.GeneratorState.OFF,
				this.getTimeOfOccurrence());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
