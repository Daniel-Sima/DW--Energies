package production.intermittent.PetrolGenerator;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/** 
 * The interface <code>PetrolGeneratorUserAndExternalControlI</code> declares the
 * signatures of service implementations accessible to the external controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code getCurrentPowerLevel() <= getMaxPowerLevel()}
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface PetrolGeneratorExternalControlI {
	/***********************************************************************************/
	/**
	 * return the maximum power production level of the petrol generator in W/h.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum power production of the petrol generator in W/h.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getMaxPowerProductionLevel() throws Exception;

	/***********************************************************************************/
	/**
	 * return the power level produced since the petrol generator
	 * is turned on, in watts/h.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return >= 0.0 && return <= getMaxPowerProductionLevel()}
	 * </pre>
	 *
	 * @return				the current power level produced by the petrol generator in W/h.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getCurrentPowerLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the maximum level of the fuel tank of the petrol generator in L.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0.0}
	 * </pre>
	 *
	 * @return				the maximum level of the fuel tank in L.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getMaxPetrolLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current level of the fuel tank of the petrol generator in L.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return >= 0.0 && return <= getMaxPowerProductionLevel()}
	 * </pre>
	 *
	 * @return				the current level of the fuel tank in L.
	 * @throws Exception	<i>to do</i>.
	 */
	public double getCurrentPetrolLevel() throws Exception;
	
	/***********************************************************************************/
	/**
	 * switch on the petrol generator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !on()}
	 * post	{@code on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	switchOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * switch off the petrol generator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code !on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	switchOff() throws Exception;
	
	
	/***********************************************************************************/
	/** TODO ici dans External ?
	 * fill the fuel tank of the petrol generator
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code liters >= 0.0}
	 * </pre>
	 *
	 * @param powerLevel	the liters to be added to the fuel tank.
	 * @throws Exception	<i>to do</i>.
	 */
	public void	fillFuelTank(double liters)
	throws Exception;
	
	/***********************************************************************************/
	/**
	 * return true if the petrol generator is currently running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the petrol generator is currently running.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean on() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/