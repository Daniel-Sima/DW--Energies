package production.aleatory;

import fr.sorbonne_u.components.connectors.AbstractConnector;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelMeteoControlConnector</code> implements a
 * connector for the {@code SolarPanelMeteoControlCI} component interface.
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
public class SolarPanelMeteoControlConnector 
extends		AbstractConnector
implements SolarPanelMeteoControlCI{
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setPowerLevelProduction(double percentage) throws Exception {
		((SolarPanelMeteoControlCI)this.offering).setPowerLevelProduction(percentage);	
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/