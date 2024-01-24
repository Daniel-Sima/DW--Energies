package equipments.meter;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import utils.Measure;
import utils.SensorData;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ElectricMeterCI</code> defines the services offered by and
 * that can be required from an electric meter component.
 *
 * <p><strong>Description</strong></p>
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
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface ElectricMeterCI 
extends	ElectricMeterImplementationI, RequiredCI, OfferedCI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	public SensorData<Measure<Double>> getCurrentConsumption() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	public SensorData<Measure<Double>> getCurrentProduction() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/