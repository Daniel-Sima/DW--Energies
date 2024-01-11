package equipments.CookingPlate;

import fr.sorbonne_u.components.connectors.AbstractConnector;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateConnector</code> implements a connector for
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
 * <p>Created on : 2023-09-19</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CookingPlateConnector 
extends		AbstractConnector
implements	CookingPlateUserCI{
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public CookingPlateState getState() throws Exception {
		return ((CookingPlateUserCI)this.offering).getState();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int getTemperature() throws Exception {
		return ((CookingPlateUserCI)this.offering).getTemperature();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOn() throws Exception {
		((CookingPlateUserCI)this.offering).turnOn();	
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOff() throws Exception {
		((CookingPlateUserCI)this.offering).turnOff();		
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void increaseMode() throws Exception {
		((CookingPlateUserCI)this.offering).increaseMode();	
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void decreaseMode() throws Exception {
		((CookingPlateUserCI)this.offering).decreaseMode();	
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void printSeparator(String title) throws Exception {
		((CookingPlateUserCI)this.offering).printSeparator(title);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
