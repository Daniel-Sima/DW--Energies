package production.intermittent.PetrolGenerator.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import production.intermittent.PetrolGenerator.mil.PetrolGeneratorElectricityModel;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>FillFuelTank</code> defines the simulation event of the
 * PetrolGenerator fuel level being filled to some level (in L).
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
public class FillFuelTank 
extends ES_Event
implements PetrolGeneratorEventI {
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PowerValue</code> represent a power value to be passed
	 * as an {@code EventInformationI} when creating a {@code FillFuelTank}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code fuelLevel >= 0.0}
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 */
	public static class	FuelLevelValue
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		/* a power in watts.												*/
		protected final double fuelLevel;

		/***********************************************************************************/
		/**
		 * create an instance of {@code FuelLevelValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code fuelLevel >= 0.0}
		 * post	{@code getFuelLevel() == fuelLevel}
		 * </pre>
		 *
		 * @param fuelLevel	the fuel level in L to put in this container.
		 */
		public FuelLevelValue(double fuelLevel) {
			super();

			assert	fuelLevel >= 0.0:
				new AssertionError(
						"Precondition violation: power >= 0.0 && "
								+ " but power = " + fuelLevel);

			this.fuelLevel = fuelLevel;
		}

		/***********************************************************************************/
		/**
		 * return the fuel level value in L.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return >= 0.0}
		 * </pre>
		 *
		 * @return	the fuel level value in L.
		 */
		public double getFuelLevel() { return this.fuelLevel; }

		/***********************************************************************************/
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.fuelLevel);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the fuel level value to be add on the fuel tank of the PetrolGenerator
	 *  when the event will be executed.											*/
	protected final FuelLevelValue fuelValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code FillFuelTank} event which content is a
	 * {@code FuelLevelValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code content instanceof FuelLevelValue}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the fuel value to be added in the fuel tank when the event will be executed.
	 */
	public FillFuelTank(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);

		assert	content instanceof FuelLevelValue :
			new AssertionError(
					"Precondition violation: event content is not a "
							+ "FuelLevelValue " + content);

		this.fuelValue = (FuelLevelValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		// if many PetrolGenerator events occur at the same time, the FillFuelTank one
		// will be executed first except for SwitchOnPetrolGenerator ones.
		if (e instanceof SwitchOnPetrolGenerator) {
			return true;
		} else {
			return false;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void executeOn(AtomicModelI model) {
		assert	model instanceof PetrolGeneratorElectricityModel;

		PetrolGeneratorElectricityModel m = (PetrolGeneratorElectricityModel)model;
		
		m.addFuel(this.fuelValue.getFuelLevel(),this.getTimeOfOccurrence());
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
