package stocking.Battery.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import stocking.Battery.Battery.BatteryState;
import stocking.Battery.BatteryExternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>BatteryExternalConnector</code> implements a
 * connector for the {@code BatteryExternalControlCI} component interface.
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
public class BatteryExternalConnector 
extends		AbstractConnector
implements BatteryExternalControlCI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerCapacity() throws Exception {
		return ((BatteryExternalControlCI)this.offering).getMaxPowerCapacity();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((BatteryExternalControlCI)this.offering).getCurrentPowerLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public BatteryState getBatteryState() throws Exception {
		return ((BatteryExternalControlCI)this.offering).getBatteryState();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setBatteryState(BatteryState newState) throws Exception {
		((BatteryExternalControlCI)this.offering).setBatteryState(newState);	
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void addPowerBattery(double powerValue) throws Exception {
		((BatteryExternalControlCI)this.offering).addPowerBattery(powerValue);		
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void pullPowerBattery(double powerValue) throws Exception {
		((BatteryExternalControlCI)this.offering).pullPowerBattery(powerValue);		
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
