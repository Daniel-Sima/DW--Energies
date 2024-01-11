package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningActuatorCI;
import equipments.AirConditioning.AirConditioningInternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class 	AirConditioningActuatorInboundPort
extends 	AbstractInboundPort
implements 	AirConditioningActuatorCI
{

	private static final long serialVersionUID = 1L;

	public AirConditioningActuatorInboundPort(ComponentI owner)
	throws Exception {
		super(AirConditioningActuatorCI.class, owner);
		assert owner instanceof AirConditioningInternalControlI :
			new PreconditionException("owner insanceof AirConditioningInternalControlI");
	}
	
	public AirConditioningActuatorInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AirConditioningActuatorCI.class, owner);
			assert	owner instanceof AirConditioningInternalControlI :
				new PreconditionException(
						"owner instanceof AirConditioningInternalControlI");
		}
	
	/**
	 * @see equipments.AirConditioning.AirCondtioningActuatorCI#startCooling()
	 */
	@Override
	public void startCooling() throws Exception {
		this.getOwner().handleRequest(
			o -> {	((AirConditioningInternalControlI)o).startCooling();
					return null;
				 });

	}

	/**
	 * @see equipments.AirConditioning.AirCondtioningActuatorCI#stopCooling()
	 */
	@Override
	public void stopCooling() throws Exception {
		this.getOwner().handleRequest(
			o -> {	((AirConditioningInternalControlI)o).stopCooling();
					return null;
				 });
	}

}
