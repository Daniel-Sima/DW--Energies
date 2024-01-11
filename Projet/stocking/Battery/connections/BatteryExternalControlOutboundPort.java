package stocking.Battery.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import stocking.Battery.Battery.BatteryState;
import stocking.Battery.BatteryExternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>BatteryExternalOutboundPort</code> implements an
 * outbound port for the {@code BatteryExternalControlCI} component interface.
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
public class BatteryExternalControlOutboundPort
extends		AbstractOutboundPort
implements BatteryExternalControlCI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public BatteryExternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(BatteryExternalControlCI.class, owner);
	}

	/***********************************************************************************/
	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public BatteryExternalControlOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, BatteryExternalControlCI.class, owner);
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
		return ((BatteryExternalControlCI)this.getConnector()).
				getMaxPowerCapacity();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((BatteryExternalControlCI)this.getConnector()).
				getCurrentPowerLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public BatteryState getBatteryState() throws Exception {
		return ((BatteryExternalControlCI)this.getConnector()).
				getBatteryState();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setBatteryState(BatteryState newState) throws Exception {
		((BatteryExternalControlCI)this.getConnector()).setBatteryState(newState);

	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void addPowerBattery(double powerValue) throws Exception {
		((BatteryExternalControlCI)this.getConnector()).addPowerBattery(powerValue);
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void pullPowerBattery(double powerValue) throws Exception {
		((BatteryExternalControlCI)this.getConnector()).pullPowerBattery(powerValue);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/