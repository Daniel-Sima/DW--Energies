package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import production.intermittent.PetrolGenerator.PetrolGeneratorInternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorInternalControlConnector</code> implements a connector
 * for the {@code PetrolGeneratorInternalControlCI} component interface.
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
public class PetrolGeneratorInternalControlConnector 
extends		AbstractConnector
implements PetrolGeneratorInternalControlCI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean isProducing() throws Exception {
		return ((PetrolGeneratorInternalControlCI)this.offering).isProducing();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void startProducing() throws Exception {
		((PetrolGeneratorInternalControlCI)this.offering).startProducing();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void stopProducing() throws Exception {
		((PetrolGeneratorInternalControlCI)this.offering).stopProducing();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/