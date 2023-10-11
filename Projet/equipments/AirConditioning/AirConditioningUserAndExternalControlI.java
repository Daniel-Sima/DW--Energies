package equipments.AirConditioning;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AirConditioningUserAndExternalControlI</code> declares the
 * signatures of service implementations accessible both to the user and
 * to the external controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevel() <= getMaxPowerLevel()}
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public interface		AirConditioningUserAndExternalControlI
{
	/**
	 * return the maximum power of the air conditioning in watts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum power of the air conditioning in watts.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getMaxPowerLevel() throws Exception;

	/**
	 * set the power level of the air conditioning; if
	 * {@code powerLevel > getMaxPowerLevel()} then set the power level to the
	 * maximum.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code powerLevel >= 0.0}
	 * post	{@code powerLevel > getMaxPowerLevel() || getCurrentPowerLevel() == powerLevel}
	 * </pre>
	 *
	 * @param powerLevel	the powerLevel to be set.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception;

	/**
	 * return the current power level of the air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return >= 0.0 && return <= getMaxPowerLevel()}
	 * </pre>
	 *
	 * @return				the current power level of the air conditioning.
	 * @throws Exception	<i>to do</i>.
	 */
	public double		getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
