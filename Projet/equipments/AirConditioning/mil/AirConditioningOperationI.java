package equipments.AirConditioning.mil;

import equipments.AirConditioning.mil.AirConditioningStateModel.State;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public interface 	AirConditioningOperationI 
{
	/**
	 * return the state of the air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public State		getState();

	/**
	 * set the state of the air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(State s);

	/**
	 * set the current cooling power of the air conditioning to {@code newPower}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_COOLING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the air conditioning.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentCoolingPower(double newPower, Time t);
}
