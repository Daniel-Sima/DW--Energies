package equipments.Lamp;

/**
* The interface <code>LampOperationI</code> declares operations that
* simulation models must implement to have events associated with the models
* execute on them.
*
* <p><strong>Description</strong></p>
* 
* <p>Created on : 2024-01-10</p>
* 
* @author walte
*/
public interface		LampOperationI
{
	/**
	 * turn on the lamp
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
	 * turn off the lamp.
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
	 * increases lamp mode;
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
	 * decreases lamp mode;
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