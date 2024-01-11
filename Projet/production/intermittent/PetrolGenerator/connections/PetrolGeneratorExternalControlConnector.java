package production.intermittent.PetrolGenerator.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import production.intermittent.PetrolGenerator.PetrolGeneratorExternalControlCI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorExternalControlConnector</code> implements a
 * connector for the {@code PetrolGeneratorExternalControlCI} component interface.
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
public class PetrolGeneratorExternalControlConnector 
extends		AbstractConnector
implements PetrolGeneratorExternalControlCI {
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerProductionLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.offering).getMaxPowerProductionLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.offering).getCurrentPowerLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPetrolLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.offering).getMaxPetrolLevel();
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPetrolLevel() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.offering).getCurrentPetrolLevel();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOn() throws Exception {
		((PetrolGeneratorExternalControlCI)this.offering).switchOn();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void switchOff() throws Exception {
		((PetrolGeneratorExternalControlCI)this.offering).switchOff();	
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void fillFuelTank(double liters) throws Exception {
		((PetrolGeneratorExternalControlCI)this.offering).fillFuelTank(liters);		
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public boolean on() throws Exception {
		return ((PetrolGeneratorExternalControlCI)this.offering).on();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/