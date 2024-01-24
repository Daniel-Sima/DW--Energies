package equipments.CookingPlate.mil;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>CookingPlateOperationI</code> declares operations that
 * simulation models must implement to have events associated with the models
 * execute on them.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2024-01-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface CookingPlateOperationI {
	
	/**
	 * Turn on the Cooking Plate
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void turnOn();
	
	/***********************************************************************************/
	/**
	 * Turn off Cooking Plate
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void turnOff();
	
	/***********************************************************************************/
	/**
	 * Increase the mode of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void increaseMode();
	
	/***********************************************************************************/
	/**
	 * Decrease the mode of the Cooking Plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void decreaseMode();
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

