package equipments.Lamp;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>LampUserCI</code> defines the services a
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
public interface LampUserCI 
extends OfferedCI, RequiredCI, LampImplementationI{
	
	// TODO voir pour les @see
	@Override
	public LampState getState() throws Exception;
	
	@Override
	public LampMode	getMode() throws Exception;
	
	@Override
	public void turnOn() throws Exception;
	
	@Override
	public void	turnOff() throws Exception;
	
	@Override
	public void	increaseMode() throws Exception;
	
	@Override
	public void	decreaseMode() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
