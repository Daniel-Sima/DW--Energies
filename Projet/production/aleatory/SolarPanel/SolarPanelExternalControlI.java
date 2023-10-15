package production.aleatory.SolarPanel;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>SolarPanelExternalControlI</code> declares the
 * signatures of service implementations accessible to the external controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevelProduction() <= getMaxPowerLevelProduction()}
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface SolarPanelExternalControlI {
	/***********************************************************************************/
	/**
	 * return the maximum power production of the solar panel in watts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum power production of the solar panel in watts.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getMaxPowerLevelProduction() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current power level production of the solar panel in watts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return >= 0.0 && return <= getMaxPowerLevelProduction()}
	 * </pre>
	 *
	 * @return				the current power level production of the solar panel.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getCurrentPowerLevelProduction() throws Exception;

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
