package equipments.CookingPlate;

/**
* The interface <code>CookingPlateOperationI</code> declares operations that
* simulation models must implement to have events associated with the models
* execute on them.
*
* <p><strong>Description</strong></p>
* 
* <p>Created on : 2024-01-10</p>
* 
* @author walte
*/
public interface		CookingPlateOperationI
{
	/**
	 * turn on the CookingPlate
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			turnOn();

	/**
	 * turn off the CookingPlate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			turnOff();

	/**
	 * increases CookingPlate mode;
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 */
	public void 		increaseMode() ;
	
	/**
	 * decreases CookingPlate mode;
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 */
	public void 		decreaseMode() ;
}