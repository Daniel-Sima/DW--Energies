package equipments.AirConditioning.connections;

import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioningController;
import equipments.AirConditioning.AirConditioningSensorDataCI;
import equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI;
import equipments.AirConditioning.measures.AirConditioningSensorData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import utils.Measure;

public class AirConditioningSensorDataOutboundPort
extends 	AbstractDataOutboundPort
implements 	AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI 
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

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
	public				AirConditioningSensorDataOutboundPort(ComponentI owner)
	throws Exception
	{
		super(AirConditioningSensorRequiredPullCI.class,
			  DataRequiredCI.PushCI.class, owner);
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
	public				AirConditioningSensorDataOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI.class,
			  DataRequiredCI.PushCI.class, owner);
	}

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
		return ((AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI)
							this.getConnector()).coolingPullSensor();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#targetTemperaturePullSensor()
	 */
	@Override
	public AirConditioningSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return ((AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI)
							this.getConnector()).targetTemperaturePullSensor();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningSensorDataCI.AirConditioningSensorCI#currentTemperaturePullSensor()
	 */
	@Override
	public AirConditioningSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return ((AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI)
							this.getConnector()).currentTemperaturePullSensor();
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
		((AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI)this.getConnector()).
								startTemperaturesPushSensor(controlPeriod, tu);
	}

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataRequiredCI.PushCI#receive(fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI)
	 */
	@Override
	public void			receive(DataRequiredCI.DataI d) throws Exception
	{
		this.getOwner().runTask(
			o -> ((AirConditioningController)o).receiveDataFromAirConditioning(d));
	}
}
