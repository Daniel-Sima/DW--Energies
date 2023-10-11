package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningInternalControlCI;
import equipments.AirConditioning.AirConditioningInternalControlI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>AirConditioningInternalControlInboundPort</code> implements an
 * inbound port for the component interface {@code AirConditioningInternalControlCI}.
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
public class AirConditioningInternalControlInboundPort
extends		AbstractInboundPort
implements	AirConditioningInternalControlCI
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
	 * pre	{@code owner instanceof AirConditioningInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				AirConditioningInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(AirConditioningInternalControlCI.class, owner);
		assert owner instanceof AirConditioningInternalControlI;
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof AirConditioningInternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				AirConditioningInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AirConditioningInternalControlCI.class, owner);
		assert owner instanceof AirConditioningInternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlCI#cooling()
	 */
	@Override
	public boolean		cooling() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((AirConditioningInternalControlI)o).cooling());
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((AirConditioningInternalControlI)o).
														getTargetTemperature());
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((AirConditioningInternalControlI)o).
														getCurrentTemperature());
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlCI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((AirConditioningInternalControlI)o).
																startCooling();
										return null;
								});
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlCI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((AirConditioningInternalControlI)o).
																stopCooling();
										return null;
								});
	}
}
// -----------------------------------------------------------------------------
