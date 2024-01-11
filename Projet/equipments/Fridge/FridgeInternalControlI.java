package equipments.Fridge;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AirConditioningInternalControlI</code> defines the signatures of
 * the services offered by the air conditioning to its thermostat controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public interface		FridgeInternalControlI
extends		FridgeUserAndControlI
{
	/**
	 * return true if Freezer is currently cooling.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if freezer is currently cooling.
	 * @throws Exception	<i>to do</i>.
	 */
	boolean coolingFreezer() throws Exception;
	
	/**
	 * start cooling freezer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code coolingFreezer()}
	 * post	{@code !coolingFreezer()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	void startCoolingFreezer() throws Exception;
	
	/**
	 * stop cooling freezer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code coolingFreezer()}
	 * post	{@code !coolingFreezer()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	void stopCoolingFreezer() throws Exception;
	
	/**
	 * return true if Cooler is currently cooling.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if cooler is currently cooling.
	 * @throws Exception	<i>to do</i>.
	 */
	boolean coolingCooler() throws Exception;
	
	/**
	 * start cooling cooler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code coolingCooler()}
	 * post	{@code !coolingCooler()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	void startCoolingCooler() throws Exception;
	
	/**
	 * stop cooling Cooler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code coolingCooler()}
	 * post	{@code !coolingCooler()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	void stopCoolingCooler() throws Exception;
	
	/***********************************************************************************/
	/**
	 * This functions prints a separator for better visualization of the traces.
	 */
	public void printSeparator(String title) throws Exception;
	
}
// -----------------------------------------------------------------------------
