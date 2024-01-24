package equipments.meter;

import utils.Measure;
import utils.SensorData;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>ElectricMeterImplementationI</code> defines the services
 * implemented by an electric meter component.
 *
 * <p><strong>Description</strong></p>
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
public interface ElectricMeterImplementationI {
	/***********************************************************************************/
	/**
	 * return the current total electric consumption in W.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= 0.0}
	 * </pre>
	 *
	 * @return				the current total electric consumption in W.
	 * @throws Exception	<i>to do</i>.
	 */
	public SensorData<Measure<Double>> getCurrentConsumption() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current total electric power production in W.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= 0.0}
	 * </pre>
	 *
	 * @return				the current total electric power production in W.
	 * @throws Exception	<i>to do</i>.
	 */
	public SensorData<Measure<Double>> getCurrentProduction() throws Exception;

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/



