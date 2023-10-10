package equipments.Lamp;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import equipments.Lamp.Lamp;
import fr.sorbonne_u.exceptions.PreconditionException;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>Lamp</code> implements the cooking plate component.
 *
 * <p>
 * <strong>Description TODO </strong>
 * </p>
 * 
 * <p>
 * The cooking plate is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. However, it will connect later to the
 * electric panel to take its (simulated) electricity consumption into account.
 * </p>
 * 
 * <p>
 * <strong>White-box Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * currentState != null
 * }
 * invariant	{@code
 * currentMode != null
 * }
 * </pre>
 * 
 * <p>
 * <strong>Black-box Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * Created on : 2023-10-10
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered = { LampUserCI.class })
public class Lamp extends AbstractComponent implements LampImplementationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of lamp inbound port used in tests. */
	public static final String INBOUND_PORT_URI = "COOKING-PLATE-INBOUND-PORT-URI";

	/** when true, methods trace their actions. */
	public static final boolean VERBOSE = true;
	public static final LampState INITIAL_STATE = LampState.OFF;
	public static final LampMode INITIAL_MODE = LampMode.MODE_1;

	/** current state (on, off) of the cooking plate. */
	protected LampState currentState;
	/** current mode of operation (1 to 3) of the cooking plate. */
	protected LampMode currentMode;
	/** inbound port offering the <code>LampCI</code> interface.		*/
	protected LampInboundPort lip;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a lamp component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == LampState.OFF}
	 * post	{@code getMode() == LampMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected Lamp()
	throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	/**
	 * create a lamp component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code LampInboundPortURI != null}
	 * pre	{@code !LampInboundPortURI.isEmpty()}
	 * post	{@code getState() == LampState.OFF}
	 * post	{@code getMode() == LampMode.LOW}
	 * </pre>
	 * 
	 * @param lampInboundPortURI	URI of the lamp inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected Lamp(String lampInboundPortURI)
    throws Exception
	{
		super(1, 0);
		this.initialise(lampInboundPortURI);
	}

	/**
	 * create a lamp component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code LampInboundPortURI != null}
	 * pre	{@code !LampInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null}
	 * post	{@code getState() == LampState.OFF}
	 * post	{@code getMode() == LampMode.LOW}
	 * </pre>
	 *
	 * @param LampInboundPortURI	URI of the lamp inbound port.
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @throws Exception				<i>to do</i>.
	 */
	protected Lamp(
			String LampInboundPortURI,
			String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(LampInboundPortURI);
	}

	/**
	 * initialise the lamp component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * LampInboundPortURI != null
	 * }
	 * pre	{@code
	 * !LampInboundPortURI.isEmpty()
	 * }
	 * post	{@code
	 * getState() == LampState.OFF
	 * }
	 * post	{@code
	 * getMode() == LampMode.LOW
	 * }
	 * </pre>
	 * 
	 * @param LampInboundPortURI URI of the lamp inbound port.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(String LampInboundPortURI) throws Exception {
		assert LampInboundPortURI != null : new PreconditionException("LampInboundPortURI != null");
		assert !LampInboundPortURI.isEmpty() : new PreconditionException("!LampInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.lip = new LampInboundPort(LampInboundPortURI, this);
		this.lip.publishPort();

		if (Lamp.VERBOSE) {
			this.tracer.get().setTitle("lamp component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.lip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampImplementationI#getState()
	 */
	@Override
	public LampState	getState() throws Exception
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampImplementationI#getMode()
	 */
	@Override
	public LampMode	getMode() throws Exception
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp is turned on.\n");
		}

		assert	this.getState() == LampState.OFF :
				new PreconditionException("getState() == LampState.OFF");

		this.currentState = LampState.ON;
		this.currentMode = LampMode.MODE_1;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp is turned off.\n");
		}

		assert	this.getState() == LampState.ON :
				new PreconditionException("getState() == LampState.ON");

		this.currentState = LampState.OFF;
	}

	@Override
	public void increaseMode() throws Exception 
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp mode is increase.\n");
		}
		
		switch (this.currentMode) {
		case MODE_1:
			assert	this.getMode() == LampMode.MODE_1 :
				new PreconditionException("getMode() == LampMode.MODE_1");
			this.currentMode = LampMode.MODE_2;
			break;
		case MODE_2:
			assert	this.getMode() == LampMode.MODE_2 :
				new PreconditionException("getMode() == LampMode.MODE_2");
			this.currentMode = LampMode.MODE_3;
			break;
		default:
			break;
		}
	}

	@Override
	public void decreaseMode() throws Exception 
	{
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp mode is decrease.\n");
		}
		
		assert	this.getState() == LampState.ON :
			new PreconditionException("getState() == LampState.ON");
		
		switch (this.currentMode) {
		case MODE_2:
			assert	this.getMode() == LampMode.MODE_2 :
				new PreconditionException("getMode() == LampMode.MODE_2");
			this.currentMode = LampMode.MODE_1;
			break;
		case MODE_3:
			assert	this.getMode() == LampMode.MODE_3 :
				new PreconditionException("getMode() == LampMode.MODE_3");
			this.currentMode = LampMode.MODE_2;
			break;
		default:
			break;
		}
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
