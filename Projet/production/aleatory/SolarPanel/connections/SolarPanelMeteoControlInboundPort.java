package production.aleatory.SolarPanel.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import production.aleatory.SolarPanel.SolarPanelMeteoControlCI;
import production.aleatory.SolarPanel.SolarPanelMeteoControlI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelMeteoControlInboundPort</code>
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
public class SolarPanelMeteoControlInboundPort 
extends		AbstractInboundPort
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
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof SolarPanelMeteoControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public SolarPanelMeteoControlInboundPort(ComponentI owner) throws Exception{
		super(SolarPanelMeteoControlCI.class, owner);
		assert	owner instanceof SolarPanelMeteoControlI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof SolarPanelMeteoControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public SolarPanelMeteoControlInboundPort(String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri,SolarPanelMeteoControlCI.class, owner);
		assert	owner instanceof SolarPanelMeteoControlI;
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
		this.getOwner().handleRequest(
				o -> {
					((SolarPanelMeteoControlI)o).setPowerLevelProduction(percentage);
					return null;
				});

	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/