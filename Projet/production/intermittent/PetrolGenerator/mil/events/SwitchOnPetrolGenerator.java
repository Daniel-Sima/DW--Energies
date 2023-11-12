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
 * The class <code>SwitchOnPetrolGenerator</code> defines the simulation event of the
 * Petrol Generator being switched on.
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
public class SwitchOnPetrolGenerator 
extends ES_Event
implements PetrolGeneratorEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>SwitchOnPetrolGenerator</code> event.
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
	public SwitchOnPetrolGenerator(Time timeOfOccurrence) {
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
		// if many Petrol Generator events occur at the same time, the
		// SwitchOnPetrolGenerator one will be executed first.
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void	executeOn(AtomicModelI model) {
		assert	model instanceof PetrolGeneratorElectricityModel;

		PetrolGeneratorElectricityModel m = (PetrolGeneratorElectricityModel)model;
		assert	m.getState() == PetrolGeneratorElectricityModel.GeneratorState.OFF :
			new AssertionError(
					"model not in the right state, should be "
							+ "PetrolGeneratorElectricityModel.GeneratorState.OFF but is "
							+ m.getState());
		m.setState(PetrolGeneratorElectricityModel.GeneratorState.ON,
				this.getTimeOfOccurrence());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
