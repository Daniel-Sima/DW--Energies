package equipments.AirConditioning;

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
public interface		AirConditioningInternalControlI
extends		AirConditioningUserAndControlI
{
	/**
	 * return true if the air conditioning is currently heating.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the air conditioning is currently cooling.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		cooling() throws Exception;

	/**
	 * start cooling.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code !cooling()}
	 * post	{@code cooling()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startCooling() throws Exception;

	/**
	 * stop cooling.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code cooling()}
	 * post	{@code !cooling()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			stopCooling() throws Exception;
	
	/***********************************************************************************/
	/**
	 * This functions prints a separator for better visualization of the traces.
	 */
	public void printSeparator(String title) throws Exception;
}
// -----------------------------------------------------------------------------
