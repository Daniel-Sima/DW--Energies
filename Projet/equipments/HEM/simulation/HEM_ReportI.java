package equipments.HEM.simulation;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>HEM_ReportI</code> defines the common behaviours of
 * simulation report objects in the household management system example.
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
 * <p>Created on : 2023-10-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface HEM_ReportI {
	/**
	 * produce a printout of the report as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code indent != null}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @param indent	indentation as a string of blank characters.
	 * @return			a printout of the report as a string.
	 */
	public String printout(String indent);

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
