package production.aleatory.SolarPanel.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import production.aleatory.SolarPanel.SolarPanelMeteoControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelMeteoOutboundPort</code> implements an
 * outbound port for the {@code SolarPanelMeteoControlCI} component interface.
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
public class SolarPanelMeteoControlOutboundPort 
extends		AbstractOutboundPort
implements SolarPanelMeteoControlCI{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public SolarPanelMeteoControlOutboundPort(ComponentI owner) throws Exception{
		super(SolarPanelMeteoControlCI.class, owner);
	}

	/***********************************************************************************/
	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public SolarPanelMeteoControlOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception{
		super(uri, SolarPanelMeteoControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setPowerLevelProduction(double percentage) throws Exception {
		((SolarPanelMeteoControlCI)this.getConnector()).setPowerLevelProduction(percentage);
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/