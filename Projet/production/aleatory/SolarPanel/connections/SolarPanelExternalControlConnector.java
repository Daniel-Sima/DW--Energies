package production.aleatory.SolarPanel.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import production.aleatory.SolarPanel.SolarPanelExternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelExternalControlConnector</code> implements a
 * connector for the {@code SolarPanelExternalControlCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
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
public class SolarPanelExternalControlConnector 
extends		AbstractConnector
implements SolarPanelExternalControlCI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerLevelProduction() throws Exception {
		return ((SolarPanelExternalControlCI)this.offering).getMaxPowerLevelProduction();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevelProduction() throws Exception {
		return ((SolarPanelExternalControlCI)this.offering).getCurrentPowerLevelProduction();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/