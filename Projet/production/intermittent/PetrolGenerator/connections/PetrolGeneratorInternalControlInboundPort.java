package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import production.intermittent.PetrolGenerator.PetrolGeneratorInternalControlCI;
import production.intermittent.PetrolGenerator.PetrolGeneratorInternalControlI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorInternalControlInboundPort</code> implements an
 * inbound port for the component interface {@code PetrolGeneratorInternalControlCI}.
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
public class PetrolGeneratorInternalControlInboundPort 
extends		AbstractInboundPort
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
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof PetrolGeneratorInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public PetrolGeneratorInternalControlInboundPort(ComponentI owner)
			throws Exception {
		super(PetrolGeneratorInternalControlCI.class, owner);
		assert owner instanceof PetrolGeneratorInternalControlI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof PetrolGeneratorInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public PetrolGeneratorInternalControlInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, PetrolGeneratorInternalControlCI.class, owner);
		assert owner instanceof PetrolGeneratorInternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean isProducing() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorInternalControlI)o).isProducing());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void startProducing() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((PetrolGeneratorInternalControlI)o).startProducing();
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void stopProducing() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((PetrolGeneratorInternalControlI)o).stopProducing();
					return null;
				});
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/