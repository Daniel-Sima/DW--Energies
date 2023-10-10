package equipments.CookingPlate;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlate</code> implements the cooking plate component.
 *
 * <p><strong>Description TODO </strong></p>
 * 
 * <p> 
 * The cooking plate is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. However, it will connect later to the
 * electric panel to take its (simulated) electricity consumption into account.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentState != null}
 * invariant	{@code currentMode != null}
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
@OfferedInterfaces(offered={CookingPlateUserCI.class})
public class CookingPlate
extends AbstractComponent
implements CookingPlateImplementationI{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the cooking plate inbound port used in tests.					*/
	public static final String	INBOUND_PORT_URI = "COOKING-PLATE-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean VERBOSE = true;
	public static final CookingPlateState INITIAL_STATE = CookingPlateState.OFF;
	public static final int INITIAL_MODE = CookingPlateMode[0];

	/** current state (on, off) of the cooking plate.							*/
	protected CookingPlateState currentState;
	/** current mode of operation (1 to 7) of the cooking plate.			*/
	protected int currentMode;

	/** inbound port offering the <code>CookingPlateUserCI</code> interface.		*/
	protected CookingPlateInboundPort cookingPlateInboudPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a cooking plate component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == CookingPlateMode.MODE_1}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected CookingPlate()
			throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a cooking plate component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null}
	 * pre	{@code !cookingPlateInboundPortURI.isEmpty()}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == CookingPlateState.MODE_1}
	 * </pre>
	 * 
	 * @param cookingPlateInboundPortURI URI of the cooking plate inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected CookingPlate(String cookingPlateInboundPortURI)
			throws Exception
	{
		super(1, 0);
		this.initialise(cookingPlateInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create a cooking plate component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null}
	 * pre	{@code !cookingPlateInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == CookingPlateState.LOW}
	 * </pre>
	 *
	 * @param cookingPlateInboundPortURI	URI of the cooking plate inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected CookingPlate(String cookingPlateInboundPortURI, String reflectionInboundPortURI) 
			throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(cookingPlateInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * initialise the cooking plate component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null}
	 * pre	{@code !cookingPlateInboundPortURI.isEmpty()}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == CookingPlateMode.Mode_1}
	 * </pre>
	 * 
	 * @param cookingPlateInboundPortURI	URI of the cooking plate inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String cookingPlateInboundPortURI)
			throws Exception {
		assert	cookingPlateInboundPortURI != null : new PreconditionException("cookingPlateInboundPortURI != null");
		assert	!cookingPlateInboundPortURI.isEmpty() : new PreconditionException("!cookingPlateInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.cookingPlateInboudPort  = new CookingPlateInboundPort(cookingPlateInboundPortURI, this);
		this.cookingPlateInboudPort.publishPort();

		if (CookingPlate.VERBOSE) {
			this.tracer.get().setTitle("Cooking plate component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.cookingPlateInboudPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/**
	 * @see
	 */
	@Override
	public CookingPlateState getState() throws Exception {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate returns its state : " + this.currentState + ".\n");
		}

		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int getMode() throws Exception {
		if (CookingPlate.VERBOSE) { 
			this.traceMessage("Cooking plate returns its mode : " + this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOn() throws Exception {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is turned on.\n");
		}

		assert this.getState() == CookingPlateState.OFF : new PreconditionException("getState() == CookingPlateState.OFF");

		this.currentState = CookingPlateState.ON;
		this.currentMode = CookingPlateMode[0];
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOff() throws Exception {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is turned off.\n");
		}

		assert this.getState() == CookingPlateState.ON : new PreconditionException("getState() == CookingPlateState.ON");

		this.currentState = CookingPlateState.OFF;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void increaseMode() throws Exception {
		assert	this.getState() == CookingPlateState.ON :
			new PreconditionException("getState() == CookingPlateState.ON");

		int nextMode = this.currentMode == 6 ? 6 : this.currentMode+1; 
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is increasing its mode from "+this.currentMode+" to "+nextMode+".\n");
		}

		this.currentMode = CookingPlateMode[nextMode]; 
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void decreaseMode() throws Exception {
		assert	this.getState() == CookingPlateState.ON : new PreconditionException("getState() == CookingPlateState.ON");

		int nextMode = this.currentMode == 0 ? 0 : this.currentMode-1; 
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is decreasing its mode from "+this.currentMode+" to "+nextMode+".\n");
		}

		this.currentMode = CookingPlateMode[nextMode];
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
