package production.intermittent.PetrolGenerator;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>PetrolGeneratorInternalControlI</code> defines the signatures of
 * the services offered by the petrol generator to its sensor controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface PetrolGeneratorInternalControlI {
	/***********************************************************************************/
	/**
	 * return true if the petrol generator is currently producing power.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the petrol generator is currently producing power.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean isProducing() throws Exception;
	
	/***********************************************************************************/
	/**
	 * start producing power.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code !isProducing()}
	 * post	{@code isProducing()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	startProducing() throws Exception;
	
	/***********************************************************************************/
	/**
	 * stop producing power.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code isProducing()}
	 * post	{@code !isProducing()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	stopProducing() throws Exception;

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/