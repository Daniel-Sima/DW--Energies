package equipments.CookingPlate;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateInboundPort</code> implements an inbound port for
 * the <code>CookingPlateUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CookingPlateInboundPort
extends		AbstractInboundPort
implements CookingPlateUserCI{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/***********************************************************************************/
	/**
	 * create an inbound port instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof CookingPlateImplementationI}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public CookingPlateInboundPort (ComponentI owner) throws Exception
	{
		super(CookingPlateUserCI.class, owner);
		assert	owner instanceof CookingPlateImplementationI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof CookingPlateImplementationI}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public CookingPlateInboundPort(String uri, ComponentI owner)
			throws Exception
	{
		super(uri, CookingPlateUserCI.class, owner);
		assert	owner instanceof CookingPlateImplementationI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * @see TODO
	 */
	@Override
	public CookingPlateState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((CookingPlateImplementationI)o).getState());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int getTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((CookingPlateImplementationI)o).getTemperature());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOn() throws Exception {
		 this.getOwner().handleRequest(
				o -> {
					((CookingPlateImplementationI)o).turnOn();
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOff() throws Exception {
		 this.getOwner().handleRequest(
					o -> {
						((CookingPlateImplementationI)o).turnOff();
						return null;
					});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void increaseMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((CookingPlateImplementationI)o).increaseMode();
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void decreaseMode() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((CookingPlateImplementationI)o).decreaseMode();
					return null;
				});
	}
	
	/***********************************************************************************/
	/**
	 * 
	 */
	@Override
	public void printSeparator(String title) throws Exception {
		this.getOwner().handleRequest(
				o -> { ((CookingPlateImplementationI)o).printSeparator(title);
						return null;
				});
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/