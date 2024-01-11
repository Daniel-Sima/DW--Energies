package equipments.CookingPlate;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateOutboundPort</code> implements an outbound port for
 * the <code>CookingPlateUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CookingPlateOutboundPort 
extends		AbstractOutboundPort
implements	CookingPlateUserCI{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
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
	public CookingPlateOutboundPort(ComponentI owner) throws Exception {
		super(CookingPlateUserCI.class, owner);
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
	public CookingPlateOutboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, CookingPlateUserCI.class, owner);
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/**
	 * @see 
	 */
	@Override
	public CookingPlateState getState() throws Exception {
		return ((CookingPlateUserCI)this.getConnector()).getState();
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public int getTemperature() throws Exception {
		return ((CookingPlateUserCI)this.getConnector()).getTemperature();
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void turnOn() throws Exception {
		((CookingPlateUserCI)this.getConnector()).turnOn();
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void turnOff() throws Exception {
		((CookingPlateUserCI)this.getConnector()).turnOff();	
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void increaseMode() throws Exception {
		((CookingPlateUserCI)this.getConnector()).increaseMode();
		
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void decreaseMode() throws Exception {
		((CookingPlateUserCI)this.getConnector()).decreaseMode();
		
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void printSeparator(String title) throws Exception {
		((CookingPlateUserCI)this.getConnector()).printSeparator(title);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/