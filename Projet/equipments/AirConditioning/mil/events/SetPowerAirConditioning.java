package equipments.AirConditioning.mil.events;

import equipments.AirConditioning.mil.AirConditioningElectricityModel;
import equipments.AirConditioning.mil.AirConditioningOperationI;
import equipments.AirConditioning.mil.AirConditioningStateModel.State;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SetPowerAirConditioning</code> defines the simulation event of the
 * AirConditioning power being set to some level (in watts).
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
public class SetPowerAirConditioning 
extends ES_Event
implements AirConditioningEventI {
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PowerValue</code> represent a power value to be passed
	 * as an {@code EventInformationI} when creating a {@code SetPowerAirConditioning}
	 * event.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code power >= 0.0 && power <= AirConditioningElectricityModel.MAX_COOLING_POWER}
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
	 */
	public static class	PowerValue
	implements	EventInformationI {
		private static final long serialVersionUID = 1L;
		/* a power in watts.												*/
		protected final double	power;

		/***********************************************************************************/
		/**
		 * create an instance of {@code PowerValue}.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code power >= 0.0 && power <= AirConditioningElectricityModel.MAX_COOLING_POWER}
		 * post	{@code getPower() == power}
		 * </pre>
		 *
		 * @param power	the power in watts to put in this container.
		 */
		public PowerValue(double power) {
			super();

			assert	power >= 0.0 &&
					power <= AirConditioningElectricityModel.MAX_COOLING_POWER:
						new AssertionError(
								"Precondition violation: power >= 0.0 && "
										+ "power <= AirConditioningElectricityModel.MAX_COOLING_POWER,"
										+ " but power = " + power);

			this.power = power;
		}

		/***********************************************************************************/
		/**
		 * return the power value in watts.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code power >= 0.0 && power <= AirConditioningElectricityModel.MAX_COOLING_POWER}
		 * </pre>
		 *
		 * @return	the power value in watts.
		 */
		public double getPower()	{ return this.power; }

		/***********************************************************************************/
		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.power);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the power value to be set on the AirConditioning when the event will be
	 *  executed.															*/
	protected final PowerValue	powerValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a {@code SetPowerAirConditioning} event which content is a
	 * {@code PowerValue}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code content instanceof PowerValue}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time at which the event must be executed in simulated time.
	 * @param content			the power value to be set on the AirConditioning when the event will be executed.
	 */
	public SetPowerAirConditioning(
			Time timeOfOccurrence,
			EventInformationI content
			)
	{
		super(timeOfOccurrence, content);
		
		assert content instanceof PowerValue :
			new AssertionError(
					"Precondition violation: event content is not a "
							+ "PowerValue " + content);

		this.powerValue = (PowerValue) content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		// if many AirConditioning events occur at the same time, the SetPowerAirConditioning one
		// will be executed first except for SwitchOnAirConditioning ones.
		if (e instanceof SwitchOnAirConditioning) {
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
		assert	model instanceof AirConditioningOperationI;

		AirConditioningOperationI ac = (AirConditioningOperationI)model;
		assert	ac.getState() == State.COOLING :
			new AssertionError(
					"model not in the right state, should be "
							+ "State.COOLING but is " + ac.getState());
		ac.setCurrentCoolingPower(this.powerValue.getPower(),
									this.getTimeOfOccurrence());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
