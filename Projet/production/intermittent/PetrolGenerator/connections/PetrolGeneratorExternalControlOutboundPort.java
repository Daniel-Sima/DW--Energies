package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import production.intermittent.PetrolGenerator.PetrolGeneratorExternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorExternalControlOutboundPort</code> implements an
 * outbound port for the {@code PetrolGeneratorExternalControlCI} component interface.
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
public class PetrolGeneratorExternalControlOutboundPort 
extends		AbstractOutboundPort
implements PetrolGeneratorExternalControlCI {
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
	public PetrolGeneratorExternalControlOutboundPort(ComponentI owner)
			throws Exception
	{
		super(PetrolGeneratorExternalControlCI.class, owner);
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
	public PetrolGeneratorExternalControlOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri, PetrolGeneratorExternalControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerProductionLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.getConnector()).
				getMaxPowerProductionLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.getConnector()).
				getCurrentPowerLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPetrolLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.getConnector()).
				getMaxPetrolLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPetrolLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.getConnector()).
				getCurrentPetrolLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOn() throws Exception {
		((PetrolGeneratorExternalControlCI)this.getConnector()).switchOn();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOff() throws Exception {
		((PetrolGeneratorExternalControlCI)this.getConnector()).switchOff();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void fillFuelTank(double liters) throws Exception {
		((PetrolGeneratorExternalControlCI)this.getConnector()).fillFuelTank(liters);
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean on() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.getConnector()).on();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/