package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningActuatorCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AirConditioningActuatorOutboundPort 
extends 	AbstractOutboundPort 
implements 	AirConditioningActuatorCI 
{

	private static final long serialVersionUID = 1L;

	/**
	 * create the outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				AirConditioningActuatorOutboundPort(ComponentI owner)
	throws Exception
	{
		super(AirConditioningActuatorCI.class, owner);
	}

	/**
	 * create the outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				AirConditioningActuatorOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AirConditioningActuatorCI.class, owner);
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningActuatorCI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		((AirConditioningActuatorCI)this.getConnector()).startCooling();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningActuatorCI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		((AirConditioningActuatorCI)this.getConnector()).stopCooling();
	}
}
