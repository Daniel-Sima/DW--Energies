package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import production.intermittent.PetrolGenerator.PetrolGeneratorInternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorInternalControlOutboundPort</code> implements an
 * outbound port for the {@code PetrolGeneratorInternalControlCI} component interface.
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
public class PetrolGeneratorInternalControlOutboundPort 
extends		AbstractOutboundPort
implements PetrolGeneratorInternalControlCI {
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
	public PetrolGeneratorInternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(PetrolGeneratorInternalControlCI.class, owner);
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
	public PetrolGeneratorInternalControlOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, PetrolGeneratorInternalControlCI.class, owner);
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean isProducing() throws Exception {
		return ((PetrolGeneratorInternalControlCI)this.getConnector()).isProducing();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void startProducing() throws Exception {
		((PetrolGeneratorInternalControlCI)this.getConnector()).startProducing();
		
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void stopProducing() throws Exception {
		((PetrolGeneratorInternalControlCI)this.getConnector()).stopProducing();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
