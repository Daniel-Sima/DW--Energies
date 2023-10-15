package production.aleatory;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>SolarPanelExternalControlCI</code> declares the
 * signatures of services used by the household energy manager to manage
 * the power production of the solar panels.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevelProduction() <= getMaxPowerLevelProduction()}
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface SolarPanelExternalControlCI 
extends		RequiredCI,
			OfferedCI,
			SolarPanelExternalControlI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerLevelProduction() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevelProduction() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/