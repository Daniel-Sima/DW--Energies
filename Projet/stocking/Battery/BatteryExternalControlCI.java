package stocking.Battery;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import stocking.Battery.Battery.BatteryState;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The component interface <code>BatteryExternalControlCI</code> declares the
 * signatures of services used by the household energy manager to manage use of
 * the batteries.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevel() <= getMaxPowerCapacity()}
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface BatteryExternalControlCI 
extends RequiredCI, OfferedCI, BatteryExternalControlI {
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getMaxPowerCapacity() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public BatteryState getBatteryState() throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void setBatteryState(BatteryState newState) throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void addPowerBattery(double powerValue) throws Exception;
	
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void pullPowerBattery(double powerValue) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/