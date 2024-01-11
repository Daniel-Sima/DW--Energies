package equipments.CookingPlate;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>CookingPlateUserCI</code> defines the services a
 * cooking plate component offers and that can be required from it.
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
public interface CookingPlateUserCI 
extends OfferedCI, RequiredCI, CookingPlateImplementationI{
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public CookingPlateState getState() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int	getTemperature() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void	turnOff() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void	increaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void	decreaseMode() throws Exception;

	/***********************************************************************************/
	/**
	 * @see
	 */
	public void printSeparator(String title) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
