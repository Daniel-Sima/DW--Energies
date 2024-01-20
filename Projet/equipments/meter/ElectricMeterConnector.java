package equipments.meter;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import utils.Measure;
import utils.SensorData;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
//-----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterConnector</code> implements a connector
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
 * <p>Created on : 2023-10-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author  <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class ElectricMeterConnector 
extends	AbstractConnector
implements ElectricMeterCI{
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public SensorData<Measure<Double>> getCurrentConsumption() throws Exception {
		return ((ElectricMeterCI)this.offering).getCurrentConsumption();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public SensorData<Measure<Double>> getCurrentProduction() throws Exception {
		return ((ElectricMeterCI)this.offering).getCurrentProduction();
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
