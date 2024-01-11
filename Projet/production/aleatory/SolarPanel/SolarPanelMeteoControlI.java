package production.aleatory.SolarPanel;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>SolarPanelMeteoControI</code> declares 
 * signatures of service implementations accessible based on weather.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface SolarPanelMeteoControlI {
	/***********************************************************************************/
	/**
	 * return the power level production of the solar panel in watts based on the 
	 * sunshine percentage.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void setPowerLevelProduction(double percentage) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/