package stocking.Battery;

import stocking.Battery.Battery.BatteryState;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/** 
 * The interface <code>BatteryExternalControlI</code> declares the
 * signatures of service implementations accessible to the external controller.
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
public interface BatteryExternalControlI {
	/***********************************************************************************/
	/**
	 * return the maximum power capacity of the battery kW/h.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum power capacity of the battery kW/h.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getMaxPowerCapacity() throws Exception;
	/***********************************************************************************/
	/**
	 * return the power level in the battery, in kW/h.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return >= 0.0 && return <= getMaxPowerProductionLevel()}
	 * </pre>
	 *
	 * @return				the current power level in the battery, in kW/h.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getCurrentPowerLevel() throws Exception;
	/***********************************************************************************/
	/**
	 * return the state of the battery 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				<code>PRODUCING</code> or <code>CONSUMING</code> 
	 * @throws Exception	<i>to do</i>.
	 */
	public BatteryState getBatteryState() throws Exception;
	/***********************************************************************************/
	/**
	 * setting the state of the battery 
	 * <code>PRODUCING</code> or <code>CONSUMING</code> 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void setBatteryState(BatteryState newState) throws Exception;
	/***********************************************************************************/
	/**
	 * adding power to the battery to stock energy.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getBatteryState() == CONSUMING}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void addPowerBattery(double powerValue) throws Exception;
	/***********************************************************************************/
	/**
	 * pull power from the battery, needed somewhere else in the house.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getBatteryState() == PRODUCING}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void pullPowerBattery(double powerValue) throws Exception;

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/