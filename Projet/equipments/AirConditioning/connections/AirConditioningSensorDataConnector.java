package equipments.AirConditioning.connections;

import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI;
import equipments.AirConditioning.measures.AirConditioningSensorData;
import fr.sorbonne_u.components.connectors.DataConnector;
import equipments.AirConditioning.AirConditioningSensorDataCI;
import utils.Measure;

public class AirConditioningSensorDataConnector 
extends 	DataConnector 
implements 	AirConditioningSensorRequiredPullCI 
{

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#coolingPullSensor()
	 */
	@Override
	public AirConditioningSensorData<Measure<Boolean>>	coolingPullSensor()
	throws Exception
	{
		return ((AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI)this.offering).coolingPullSensor();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#targetTemperaturePullSensor()
	 */
	@Override
	public AirConditioningSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return ((AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI)this.offering).targetTemperaturePullSensor();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#currentTemperaturePullSensor()
	 */
	@Override
	public AirConditioningSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return ((AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI)this.offering).currentTemperaturePullSensor();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#startTemperaturesPushSensor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		((AirConditioningSensorDataCI.AirConditioningSensorOfferedPullCI)this.offering).
								startTemperaturesPushSensor(controlPeriod, tu);
	}

}
