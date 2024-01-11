package production.aleatory.SolarPanel;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>SolarPanelMeteoControlCI</code> declares the
 * signatures of services used for the simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface SolarPanelMeteoControlCI
extends		RequiredCI,
OfferedCI,
SolarPanelMeteoControlI{
	/***********************************************************************************/
	/**
	 * @see
	 */
	public void setPowerLevelProduction(double percentage) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/