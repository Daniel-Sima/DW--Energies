package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningActuatorCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * 
 * @author walte
 *
 */
public class AirConditioningActuatorConnector 
extends		AbstractConnector
implements	AirConditioningActuatorCI
{
	/**
	 * @see equipments.AirConditioning.AirConditioningActuatorCI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		((AirConditioningActuatorCI)this.offering).startCooling();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningActuatorCI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		((AirConditioningActuatorCI)this.offering).stopCooling();
	}
}
