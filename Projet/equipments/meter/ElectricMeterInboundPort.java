package equipments.meter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import utils.Measure;
import utils.SensorData;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ElectricMeterInboundPort</code> implements an inbound port
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
public class ElectricMeterInboundPort 
extends	AbstractInboundPort
implements ElectricMeterCI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	public ElectricMeterInboundPort(ComponentI owner) throws Exception {
		super(ElectricMeterCI.class, owner);
	}

	/***********************************************************************************/
	public ElectricMeterInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ElectricMeterCI.class, owner);
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public SensorData<Measure<Double>> getCurrentConsumption() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((ElectricMeterImplementationI)o).getCurrentConsumption());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public SensorData<Measure<Double>> getCurrentProduction() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((ElectricMeterImplementationI)o).getCurrentProduction());
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/