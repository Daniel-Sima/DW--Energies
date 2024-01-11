package equipments.AirConditioning.measures;

import utils.MeasureI;
import utils.SensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;

public class			AirConditioningSensorData<T extends MeasureI>
extends		SensorData<T>
implements	DataOfferedCI.DataI,
			DataRequiredCI.DataI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a air conditioning sensor data from the given measure, which can be
	 * compound.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param m						a measure on some air conditioning property.
	 */
	public			AirConditioningSensorData(T m)
	{
		super(m);
	}
}
