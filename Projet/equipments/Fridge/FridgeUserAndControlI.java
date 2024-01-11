package equipments.Fridge;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AirConditioningUserAndControlI</code> declares the signatures of
 * the methods corresponding both to actions performed by users
 * and by the controllers on the fridge.
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
public interface		FridgeUserAndControlI
{
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
