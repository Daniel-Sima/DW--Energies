package equipments.Fridge;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AirConditioningUserImplI</code> declares the signature of the
 * methods corresponding to actions performed by users on the air conditioning: switching
 * on and off, setting the target temperature, etc.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public interface FridgeUserImplI
extends		FridgeUserAndControlI,
			FridgeUserAndExternalControlI
{
	/**
	 * return true if the air conditioner is currently running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the air conditioner is currently running.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		on() throws Exception;

	/**
	 * switch on the air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !on()}
	 * post	{@code on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOn() throws Exception;

	/**
	 * switch off the air conditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code !on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOff() throws Exception;

	/**
	 * set the target cooler temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code target >= -50.0 && target <= 50.0}
	 * post	{@code target == getTargetCoolerTemperature()}
	 * </pre>
	 *
	 * @param target		the new target cooler temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetCoolerTemperature(double targetCooler)
	throws Exception;
	
	/**
	 * set the target freezer temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code targetFreezer >= -50.0 && targetFreezer <= 50.0}
	 * post	{@code targetFreezer == getTargetFreezerTemperature()}
	 * </pre>
	 *
	 * @param targetFreezer		the new target freezer temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetFreezeTemperature(double targetFreezer)
	throws Exception;
}
// -----------------------------------------------------------------------------
