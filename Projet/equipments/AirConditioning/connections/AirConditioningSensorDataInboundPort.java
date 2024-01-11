package equipments.AirConditioning.connections;

import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.AirConditioningInternalControlI;
import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI;
import equipments.AirConditioning.measures.AirConditioningCompoundMeasure;
import equipments.AirConditioning.measures.AirConditioningSensorData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.Measure;

public class AirConditioningSensorDataInboundPort
extends 	AbstractDataInboundPort
implements 	AirConditioningSensorOfferedPullCI 
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public AirConditioningSensorDataInboundPort(ComponentI owner)
	throws Exception 
	{
		super(AirConditioningSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);
		
		assert	owner instanceof AirConditioningInternalControlI :
			new PreconditionException(
					"owner instanceof AirConditioningInternalControlI");
	}
	
	public	AirConditioningSensorDataInboundPort(
			String uri, 
			ComponentI owner
			) 
	throws Exception
	{
		super(uri, AirConditioningSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof AirConditioningInternalControlI :
			new PreconditionException(
					"owner instanceof AirConditioningInternalControlI");
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public AirConditioningSensorData<Measure<Boolean>> coolingPullSensor()
	throws Exception 
	{
		return this.getOwner().handleRequest(
				o -> ((AirConditioning)o).coolingPullSensor());
	}

	@Override
	public AirConditioningSensorData<Measure<Double>> targetTemperaturePullSensor()
	throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((AirConditioning)o).targetTemperaturePullSensor());
	}

	@Override
	public AirConditioningSensorData<Measure<Double>> currentTemperaturePullSensor() 
	throws Exception 
	{
		return this.getOwner().handleRequest(
				o -> ((AirConditioning)o).currentTemperaturePullSensor());
	}

	@Override
	public void startTemperaturesPushSensor(long controlPeriod, TimeUnit tu) 
	throws Exception 
	{
		this.getOwner().handleRequest(
				o -> {	((AirConditioning)o).startTemperaturesPushSensor(controlPeriod, tu);
						return null;
					});
	}

	@Override
	public DataOfferedCI.DataI 	get() throws Exception 
	{
		return new AirConditioningSensorData<AirConditioningCompoundMeasure>(
				new AirConditioningCompoundMeasure(
						this.targetTemperaturePullSensor().getMeasure(),
						this.currentTemperaturePullSensor().getMeasure()));
	}

}
