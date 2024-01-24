package equipments.Fridge.connections;

import java.util.concurrent.TimeUnit;

import equipments.Fridge.Fridge;
import equipments.Fridge.FridgeInternalControlI;
import equipments.Fridge.FridgeSensorDataCI;
import equipments.Fridge.measures.FridgeCompoundMeasure;
import equipments.Fridge.measures.FridgeSensorData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterCompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataOfferedCI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.Measure;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>FridgeSensorDataInboundPort</code>
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
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class FridgeSensorDataInboundPort 
extends AbstractDataInboundPort
implements FridgeSensorDataCI.FridgeSensorOfferedPullCI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof FridgeInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FridgeSensorDataInboundPort(ComponentI owner)
			throws Exception {
		super(FridgeSensorDataCI.FridgeSensorOfferedPullCI.class,
				DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof FridgeInternalControlI :
			new PreconditionException(
					"owner instanceof FridgeInternalControlI");
	}

	/***********************************************************************************/
	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof FridgeInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FridgeSensorDataInboundPort(
			String uri, 
			ComponentI owner
			) throws Exception
	{
		super(uri, FridgeSensorDataCI.FridgeSensorOfferedPullCI.class,
				DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof FridgeInternalControlI :
			new PreconditionException(
					"owner instanceof FridgeInternalControlI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see
	 */
	@Override
	public FridgeSensorData<Measure<Boolean>> coolingPullSensor()
			throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((Fridge)o).coolingPullSensor());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public FridgeSensorData<Measure<Double>> targetTemperaturePullSensor() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((Fridge)o).targetTemperaturePullSensor());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public FridgeSensorData<Measure<Double>> currentTemperaturePullSensor() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((Fridge)o).currentTemperaturePullSensor());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void startTemperaturesPushSensor(long controlPeriod, TimeUnit tu) throws Exception {
		this.getOwner().handleRequest(
				o -> { ((Fridge)o).startTemperaturesPushSensor(controlPeriod, tu);
				return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public DataOfferedCI.DataI get() throws Exception {
		return new FridgeSensorData<FridgeCompoundMeasure>(
				new FridgeCompoundMeasure(
						this.targetTemperaturePullSensor().getMeasure(),
						this.currentTemperaturePullSensor().getMeasure()));
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

