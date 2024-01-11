package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import production.intermittent.PetrolGenerator.PetrolGeneratorExternalControlCI;
import production.intermittent.PetrolGenerator.PetrolGeneratorExternalControlI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorExternalControlInboundPort</code>
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
 * <p>Created on : 2023-09-19</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class PetrolGeneratorExternalControlInboundPort 
extends		AbstractInboundPort
implements PetrolGeneratorExternalControlCI{
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
	 * pre	{@code owner instanceof PetrolGeneratorExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public PetrolGeneratorExternalControlInboundPort(ComponentI owner)
			throws Exception
	{
		super(PetrolGeneratorExternalControlCI.class, owner);
		assert	owner instanceof PetrolGeneratorExternalControlI;
	}

	/***********************************************************************************/
	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof PetrolGeneratorExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public PetrolGeneratorExternalControlInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
	{
		super(uri,PetrolGeneratorExternalControlCI.class, owner);
		assert	owner instanceof PetrolGeneratorExternalControlI;
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
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorExternalControlI)o).getMaxPowerProductionLevel());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorExternalControlI)o).getCurrentPowerLevel());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPetrolLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorExternalControlI)o).getMaxPetrolLevel());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPetrolLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorExternalControlI)o).getCurrentPetrolLevel());
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((PetrolGeneratorExternalControlI)o).switchOn();
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((PetrolGeneratorExternalControlI)o).switchOff();
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void fillFuelTank(double liters) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((PetrolGeneratorExternalControlI)o).fillFuelTank(liters);
					return null;
				});
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean on() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((PetrolGeneratorExternalControlI)o).on());
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
