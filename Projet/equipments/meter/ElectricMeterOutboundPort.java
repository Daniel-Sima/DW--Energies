package equipments.meter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ElectricMeterOutboundPort</code> implements an outbound port
 * for the {@code ElectricMeterCI} component interface.
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
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class ElectricMeterOutboundPort 
extends	AbstractOutboundPort
implements ElectricMeterCI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	public ElectricMeterOutboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterCI.class, owner);
	}

	/***********************************************************************************/
	public ElectricMeterOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentConsumption() throws Exception {
		return ((ElectricMeterCI)this.getConnector()).getCurrentConsumption();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentProduction() throws Exception {
		return ((ElectricMeterCI)this.getConnector()).getCurrentProduction();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/