package equipments.Fridge.measures;

import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import utils.MeasureI;
import utils.SensorData;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/* The class <code>FridgeSensorData</code> implements the sensor data
 * sent when calling the heater sensors.
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
 * <p>Created on : 2024-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class FridgeSensorData <T extends MeasureI> 
extends		SensorData<T>
implements	DataOfferedCI.DataI,
DataRequiredCI.DataI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Fridge sensor data from the given measure, which can be
	 * compound.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param m		a measure on some Fridge property.
	 */
	public FridgeSensorData(T m) {
		super(m);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

