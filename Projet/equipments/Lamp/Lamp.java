package equipments.Lamp;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;

import java.util.HashMap;

import equipments.Lamp.Lamp;
import equipments.Lamp.mil.MILSimulationArchitectures;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.ExecutionType;

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
public class Lamp 
extends AbstractCyPhyComponent 
implements LampImplementationI 
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the lamp inbound port used in tests.					*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
														"LAMP-RIP-URI";	
	/** URI of lamp inbound port used in tests. */
	public static final String INBOUND_PORT_URI = "LAMP-INBOUND-PORT-URI";

	/** when true, methods trace their actions. */
	public static boolean VERBOSE = true;
	public static final LampState INITIAL_STATE = LampState.OFF;
	public static final LampMode INITIAL_MODE = LampMode.MODE_1;

	/** current state (on, off) of the cooking plate. */
	protected LampState currentState;
	/** current mode of operation (1 to 3) of the cooking plate. */
	protected LampMode currentMode;
	/** map helping finding the percentage of the current mode*/
	protected HashMap<LampMode, Integer> findMode;
	/** inbound port offering the <code>LampCI</code> interface.		*/
	protected LampInboundPort lip;
	
	// Execution/Simulation

	/** plug-in holding the local simulation architecture and simulators.	*/
	protected AtomicSimulatorPlugin		asp;
	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String				simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String				localSimulatorURI;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;

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
		this(INBOUND_PORT_URI);
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
	protected 		Lamp(String lampInboundPortURI)
    throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, lampInboundPortURI,
			ExecutionType.STANDARD, null, null, 0.0);
	}

	/**
	 * create a lamp component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code lampInboundPortURI != null}
	 * pre	{@code !lampInboundPortURI.isEmpty()}
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
			String reflectionInboundPortURI,
			String lampInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		assert	lampInboundPortURI != null &&
											!lampInboundPortURI.isEmpty() :
				new PreconditionException(
						"lampInboundPortURI != null && "
						+ "!lampInboundPortURI.isEmpty()");
		assert	currentExecutionType != null :
				new PreconditionException("currentExecutionType != null");
		assert	!currentExecutionType.isSimulated() ||
								(simArchitectureURI != null &&
											!simArchitectureURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(simArchitectureURI != null && "
						+ "!simArchitectureURI.isEmpty())");
		assert	!currentExecutionType.isSimulated() ||
								(localSimulatorURI != null &&
												!localSimulatorURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(localSimulatorURI != null && "
						+ "!localSimulatorURI.isEmpty())");
		assert	!currentExecutionType.isSIL() || accFactor > 0.0 :
				new PreconditionException(
						"!currentExecutionType.isSIL() || accFactor > 0.0");

		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.localSimulatorURI = localSimulatorURI;
		this.accFactor = accFactor;	
		
		if(this.currentExecutionType.isIntegrationTest()) {
			Lamp.VERBOSE = true;
		}
		
		this.initialise(lampInboundPortURI);
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
		assert LampInboundPortURI != null : 
			new PreconditionException("LampInboundPortURI != null");
		assert !LampInboundPortURI.isEmpty() : 
			new PreconditionException("!LampInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.lip = new LampInboundPort(LampInboundPortURI, this);
		this.lip.publishPort();
		
		this.findMode = new HashMap<>();
		this.findMode.put(LampMode.MODE_1, 10);
		this.findMode.put(LampMode.MODE_2, 50);
		this.findMode.put(LampMode.MODE_3, 100);
		
		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
				MILSimulationArchitectures.createLampMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
		case SIL_SIMULATION:
			architecture =
				MILSimulationArchitectures.
							createLampRTArchitecture(
									this.currentExecutionType,
									this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
					put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case STANDARD:
		case INTEGRATION_TEST:
		default:
		}

		if (Lamp.VERBOSE) {
			this.tracer.get().setTitle("Lamp component");
			this.tracer.get().setRelativePosition(3, 1);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				this.asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				this.asp.setPluginURI(uri);
				this.asp.setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case MIL_RT_SIMULATION:
			case SIL_SIMULATION:
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case STANDARD:
			case INTEGRATION_TEST:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
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
													this.currentMode + " (" + findMode.get(this.currentMode)+"%).\n");
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

		assert	this.currentState == LampState.OFF :
				new PreconditionException("this.currentState == LampState.OFF");

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

		assert	this.currentState == LampState.ON :
				new PreconditionException("this.currentStat == LampState.ON");

		this.currentState = LampState.OFF;
	}

	@Override
	public void increaseMode() throws Exception 
	{
		assert	this.currentMode != LampMode.MODE_3 : new PreconditionException("this.currentMode != LampMode.MODE_3");
		
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp mode is increasing mode.\n");
		}
		
		switch (this.currentMode) {
		case MODE_1:
			assert	this.currentMode == LampMode.MODE_1 :
				new PreconditionException("this.currentMode== LampMode.MODE_1");
			this.currentMode = LampMode.MODE_2;
			break;
		case MODE_2:
			assert	this.currentMode == LampMode.MODE_2 :
				new PreconditionException("this.currentMode == LampMode.MODE_2");
			this.currentMode = LampMode.MODE_3;
			break;
		default:
			break;
		}
	}

	@Override
	public void decreaseMode() throws Exception 
	{
		assert	this.currentMode != LampMode.MODE_1 :
			new PreconditionException("getMode() != LampMode.MODE_1");
		
		if (Lamp.VERBOSE) {
			this.traceMessage("Lamp mode is decrease.\n");
		}
		
		assert	this.currentState == LampState.ON :
			new PreconditionException("getState() == LampState.ON");
		
		switch (this.currentMode) {
		case MODE_2:
			assert	this.currentMode == LampMode.MODE_2 :
				new PreconditionException("getMode() == LampMode.MODE_2");
			this.currentMode = LampMode.MODE_1;
			break;
		case MODE_3:
			assert	this.currentMode == LampMode.MODE_3 :
				new PreconditionException("getMode() == LampMode.MODE_3");
			this.currentMode = LampMode.MODE_2;
			break;
		default:
			break;
		}
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	public void printSeparator(String title) throws Exception {
		this.traceMessage("**********"+ title +"**********\n");
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
