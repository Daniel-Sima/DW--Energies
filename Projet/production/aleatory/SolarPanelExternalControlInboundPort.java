package production.aleatory;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelExternalControlInboundPort</code>
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
public class SolarPanelExternalControlInboundPort 
extends		AbstractInboundPort
implements	SolarPanelExternalControlCI{
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
	 * pre	{@code owner instanceof SolarPanelExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public SolarPanelExternalControlInboundPort(ComponentI owner) throws Exception{
		super(SolarPanelExternalControlCI.class, owner);
		assert	owner instanceof SolarPanelExternalControlI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof SolarPanelExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public SolarPanelExternalControlInboundPort(String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri,SolarPanelExternalControlCI.class, owner);
		assert	owner instanceof SolarPanelExternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerLevelProduction() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPanelExternalControlI)o).getMaxPowerLevelProduction());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevelProduction() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((SolarPanelExternalControlI)o).getCurrentPowerLevelProduction());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/