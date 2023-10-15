package production.intermittent.PetrolGenerator;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>PetrolGeneratorExternalControlCI</code> declares the
 * signatures of services used by the household energy manager to manage the petrol
 * generator .
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevel() <= getMaxPowerLevel()}
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface PetrolGeneratorExternalControlCI 
extends RequiredCI, OfferedCI, PetrolGeneratorExternalControlI {
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getMaxPowerProductionLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getMaxPetrolLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getCurrentPetrolLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void	switchOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void	switchOff() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void	fillFuelTank(double liters) throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean on() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/