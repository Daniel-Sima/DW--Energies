package equipments.HEM;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>AdjustableOutboundPort</code> implements an
 * outbound port for the {@code AdjustableCI} component
 * interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariants
 * </pre>
 * 
 * <p>Created on : 2023-10-16</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class AdjustableOutboundPort 
extends AbstractOutboundPort
implements AdjustableCI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public AdjustableOutboundPort(ComponentI owner) throws Exception {
		super(AdjustableCI.class, owner);
	}

	/***********************************************************************************/
	/**
	 * create a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public AdjustableOutboundPort(String uri, ComponentI owner) 
			throws Exception {
		super(uri, AdjustableCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int maxMode() throws Exception {
		int ret = ((AdjustableCI)this.getConnector()).maxMode();
		assert ret > 0;
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean upMode() throws Exception {
		int oldMode = this.currentMode();
		assert	oldMode < this.maxMode();
		boolean ret = ((AdjustableCI)this.getConnector()).upMode();
		assert	this.currentMode() > oldMode;
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean downMode() throws Exception {
		int oldMode = this.currentMode();
		assert	oldMode > 1;
		boolean ret = ((AdjustableCI)this.getConnector()).downMode();
		System.out.println("ret: "+ret);
		assert	this.currentMode() < oldMode;
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean setMode(int modeIndex) throws Exception {
		assert	modeIndex > 0 && modeIndex <= this.maxMode();
		boolean ret = ((AdjustableCI)this.getConnector()).setMode(modeIndex);
		assert	this.currentMode() == modeIndex;
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int currentMode() throws Exception {
		int ret = ((AdjustableCI)this.getConnector()).currentMode();
		assert	ret > 0 && ret <= this.maxMode();
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean suspended() throws Exception {
		return ((AdjustableCI)this.getConnector()).suspended();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean suspend() throws Exception {
		assert	!this.suspended();
		boolean ret = ((AdjustableCI)this.getConnector()).suspend();
		assert	!ret || this.suspended();
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean resume() throws Exception {
		assert	this.suspended();
		boolean ret = ((AdjustableCI)this.getConnector()).resume();
		assert	!ret || !this.suspended();
		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double emergency() throws Exception {
		assert	this.suspended();
		double ret = ((AdjustableCI)this.getConnector()).emergency();
		assert	ret >= 0.0 && ret <= 1.0;
		return ret;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/