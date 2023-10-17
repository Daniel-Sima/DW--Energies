package equipments.CookingPlate;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>CookingPlateImplementationI</code> defines the signatures
 * of services service implemented by the cooking plate component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Black-box Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no invariant
 * </pre>
 * 
 * <p>
 * Created on : 2023-10-10
 * </p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public interface CookingPlateImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------
	/**
	 * The enumeration <code>CookingPlateState</code> describes the operation states
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum CookingPlateState {
		/** cooking plate is on. */
		ON,
		/** cooking plate is off. */
		OFF
	}

	/***********************************************************************************/
	/**
	 * Array of <code>CookingPlateMode</code> describes the operation modes
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * The cooking plate has 8 modes, from 0 to 7, from the coldest to the hottest temperature.
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static int[] CookingPlateTemperature =	new int[] {0, 50, 80, 120, 160, 200, 250, 300};
	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the cooking plate.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public CookingPlateState getState() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current operation temperature of the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the cooking plate.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public int getTemperature() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn on the cooking plate, put in the mode 0 (50°).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == CookingPlate.OFF}
	 * post	{@code getMode() == CookingPlateMode.MODE_1}
	 * post	{@code getState() == CookingPlateMode.ON}
	 * </pre>
	 *
	 * @throws Exception <i>TODO</i>.
	 */
	public void turnOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn off the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == CookingPlateState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOff() throws Exception;
	
	/***********************************************************************************/
	/**
	 * increase the cooking plate MODE.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == CookingPlateState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	increaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * decrease the cooking plate MODE
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == CookingPlateState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	decreaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * This functions prints a separator for better visualization of the traces.
	 */
	public void printSeparator(String title) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
