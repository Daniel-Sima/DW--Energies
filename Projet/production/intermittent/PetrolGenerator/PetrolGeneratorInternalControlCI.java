package production.intermittent.PetrolGenerator;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>PetrolGeneratorInternalControlCI</code> declares the
 * signatures of services used by the sensor to control the power produced by the
 * petrol generator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface PetrolGeneratorInternalControlCI 
extends		OfferedCI,
RequiredCI, PetrolGeneratorInternalControlI{
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean isProducing() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void	startProducing() throws Exception;

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void	stopProducing() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/