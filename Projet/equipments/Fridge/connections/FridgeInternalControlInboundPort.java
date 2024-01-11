package equipments.Fridge.connections;

import equipments.Fridge.FridgeInternalControlCI;
import equipments.Fridge.FridgeInternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FridgeInternalControlInboundPort</code> implements an
 * inbound port for the component interface {@code FridgeInternalControlCI}.
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
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class FridgeInternalControlInboundPort
extends		AbstractInboundPort
implements	FridgeInternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof FridgeInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				FridgeInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(FridgeInternalControlCI.class, owner);
		assert owner instanceof FridgeInternalControlI;
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof FridgeInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				FridgeInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FridgeInternalControlCI.class, owner);
		assert owner instanceof FridgeInternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void		setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		this.getOwner().handleRequest(
								o -> { ((FridgeInternalControlI)o).
														setTargetCoolerTemperature(targetCooler);
										return null;
								});
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).
														getTargetCoolerTemperature());
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).
														getCurrentCoolerTemperature());
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void		setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		this.getOwner().handleRequest(
								o -> { ((FridgeInternalControlI)o).
														setTargetFreezerTemperature(targetFreezer);
										return null;
								});
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).
														getTargetFreezerTemperature());
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).
														getCurrentFreezerTemperature());
	}


	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#coolingCooler()
	 */
	@Override
	public boolean		coolingCooler() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).coolingCooler());
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingCooler()
	 */
	@Override
	public void			startCoolingCooler() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((FridgeInternalControlI)o).
																startCoolingCooler();
										return null;
								});
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingCooler()
	 */
	@Override
	public void			stopCoolingCooler() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((FridgeInternalControlI)o).
																stopCoolingCooler();
										return null;
								});
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#coolingFreezer()
	 */
	@Override
	public boolean		coolingFreezer() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FridgeInternalControlI)o).coolingFreezer());
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingFreezer()
	 */
	@Override
	public void			startCoolingFreezer() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((FridgeInternalControlI)o).
																startCoolingFreezer();
										return null;
								});
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingFreezer()
	 */
	@Override
	public void			stopCoolingFreezer() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((FridgeInternalControlI)o).
																stopCoolingFreezer();
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
				o -> { ((FridgeInternalControlI)o).printSeparator(title);
						return null;
				});
	}
}
// -----------------------------------------------------------------------------
