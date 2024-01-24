package global;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>GlobalCoordinator</code>
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
 * <p>Created on : 2024-10-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class GlobalCoordinator 
extends AbstractCyPhyComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String	REFLECTION_INBOUND_PORT_URI =
			"GLOBAL-COORDINATOR";

	/**
	 * crate the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected GlobalCoordinator() {
		// 2 threads are required to take charge of the simulation main
		// coordination and the calls from submodels when they need to perform
		// externam events and notify their parent coupled models to do so.
		super(REFLECTION_INBOUND_PORT_URI, 2, 0);

		this.tracer.get().setTitle("Global coordinator");
		this.tracer.get().setRelativePosition(0, 2);
		this.toggleTracing();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
