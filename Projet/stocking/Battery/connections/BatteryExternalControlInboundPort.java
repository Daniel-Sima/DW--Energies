package stocking.Battery.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import stocking.Battery.Battery.BatteryState;
import stocking.Battery.BatteryExternalControlCI;
import stocking.Battery.BatteryExternalControlI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>BatteryExternalControlInboundPort</code> implements an
 * inbound port for the component interface {@code BatteryExternalControlCI}.
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
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class BatteryExternalControlInboundPort 
extends		AbstractInboundPort
implements BatteryExternalControlCI{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof BatteryExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public BatteryExternalControlInboundPort(ComponentI owner)
			throws Exception {
		super(BatteryExternalControlCI.class, owner);
		assert owner instanceof BatteryExternalControlI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof BatteryExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public BatteryExternalControlInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, BatteryExternalControlCI.class, owner);
		assert owner instanceof BatteryExternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerCapacity() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryExternalControlI)o).getMaxPowerCapacity());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryExternalControlI)o).getCurrentPowerLevel());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public BatteryState getBatteryState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((BatteryExternalControlI)o).getBatteryState());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setBatteryState(BatteryState newState) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((BatteryExternalControlI)o).setBatteryState(newState);
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void addPowerBattery(double powerValue) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((BatteryExternalControlI)o).addPowerBattery(powerValue);
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void pullPowerBattery(double powerValue) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((BatteryExternalControlI)o).pullPowerBattery(powerValue);
					return null;
				});
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/