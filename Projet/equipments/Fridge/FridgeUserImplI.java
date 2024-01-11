package equipments.Fridge;

// -----------------------------------------------------------------------------
/**
 * The interface <code>FridgeConditioningUserImplI</code> declares the signature of the
 * methods corresponding to actions performed by users on the fridge: switching
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
	 * switch on the fridge.
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
	 * switch off the fridge.
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
	
	/***********************************************************************************/
	/**
	 * return the target temperature of the cooler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the target temperature of the cooler.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public double			getTargetCoolerTemperature()
	throws Exception;

	/**
	 * return the current temperature of the cooler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature of the cooler.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public double			getCurrentCoolerTemperature()
	throws Exception;
	
	/**
	 * set the target cooler temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code targetCooler >= 0.0 && targetCooler <= 15.0}
	 * post	{@code targetCooler == setTargetCoolerTemperature()}
	 * </pre>
	 *
	 * @param targetCooler		the new target cooler temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetCoolerTemperature(double targetCooler)
	throws Exception;
	
	/**
	 * return the target temperature of the Freezer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the target temperature of the Freezer.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public double			getTargetFreezerTemperature()
	throws Exception;

	/**
	 * return the current temperature of the freezer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature of the Freezer.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public double			getCurrentFreezerTemperature()
	throws Exception;
	
	/**
	 * set the target freezer temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code targetFreezer >= -20.0 && targetFreezer <= 0.0}
	 * post	{@code targetFreezer == setTargetFreezerTemperature()}
	 * </pre>
	 *
	 * @param targetFreezer		the new target freezer temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetFreezerTemperature(double targetFreezer)
	throws Exception;
	
}
// -----------------------------------------------------------------------------
