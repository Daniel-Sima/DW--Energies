package equipments.CookingPlate;

import equipments.CookingPlate.mil.CookingPlateStateModel;
import equipments.CookingPlate.mil.MILSimulationArchitectures;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.ExecutionType;

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
 * <p>Created on : 2024-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@OfferedInterfaces(offered={CookingPlateUserCI.class})
public class CookingPlate
extends AbstractCyPhyComponent
implements CookingPlateImplementationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the Cooking Plate inbound port used in tests.							*/
	public static final String REFLECTION_INBOUND_PORT_URI = "COOKING-PLATE-RIP-URI";	

	/** URI of the cooking plate inbound port used in tests.						*/
	public static final String	INBOUND_PORT_URI = "COOKING-PLATE-INBOUND-PORT-URI";

	/** when true, methods trace their actions.										*/
	public static boolean VERBOSE = true;
	public static final CookingPlateState INITIAL_STATE = CookingPlateState.OFF;
	public static final int INITIAL_TEMPERATURE = CookingPlateTemperature[0];

	/** current state (on, off) of the cooking plate.								*/
	protected CookingPlateState currentState;
	/** current mode of operation (0 (0°), 1 (50°) to 7 (300°)) of the cooking plate.			*/
	protected int currentMode;
	/** number modes of the Cooking Plate */
	protected int MAX_MODES;

	/** inbound port offering the <code>CookingPlateUserCI</code> interface.		*/
	protected CookingPlateInboundPort cookingPlateInboudPort; 

	// Execution/Simulation

	/** plug-in holding the local simulation architecture and simulators.			*/
	protected AtomicSimulatorPlugin asp;
	/** current type of execution.													*/
	protected final ExecutionType currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.						*/
	protected final String simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.																*/
	protected final String localSimulatorURI;
	/** acceleration factor to be used when running the real time simulation.		*/
	protected double accFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * Create a Cooking Plate component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code INBOUND_PORT_URI != null}
	 * pre	{@code !INBOUND_PORT_URI.isEmpty()}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == 0}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected CookingPlate() throws Exception {
		this(INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * Create a Cooking Plate component for standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null}
	 * pre	{@code !cookingPlateInboundPortURI.isEmpty()}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == 0}
	 * </pre>
	 * 
	 * @param cookingPlateInboundPortURI URI of the cooking plate inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected CookingPlate(String cookingPlateInboundPortURI) throws Exception {
		this(REFLECTION_INBOUND_PORT_URI, cookingPlateInboundPortURI,
				ExecutionType.STANDARD, null, null, 0.0);
	}

	/***********************************************************************************/
	/**
	 * Create a Cooking Plate component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null  && !cookingPlateInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * post	{@code getState() == CookingPlateState.OFF}
	 * post	{@code getMode() == 0}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection innbound port of the component.
	 * @param cookingPlateInboundPortURI	URI of the cooking plate inbound port.
	 * @param currentExecutionType			current execution type for the next run.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI				URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor						acceleration factor for the simulation.
	 * @throws Exception				<i>to do</i>.
	 */
	protected CookingPlate(String reflectionInboundPortURI,
			String cookingPlateInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception 
	{

		super(reflectionInboundPortURI, 1, 0);

		assert cookingPlateInboundPortURI != null &&
				!cookingPlateInboundPortURI.isEmpty() : new PreconditionException( 
						"cookingPlateInboundPortURI != null && "
								+ "!cookingPlateInboundPortURI.isEmpty()");	

		assert currentExecutionType != null :
			new PreconditionException("currentExecutionType != null");
		assert !currentExecutionType.isSimulated() || (simArchitectureURI != null &&
				!simArchitectureURI.isEmpty()) :
					new PreconditionException(
							"currentExecutionType.isSimulated() ||  "
									+ "(simArchitectureURI != null && "
									+ "!simArchitectureURI.isEmpty())");
		assert !currentExecutionType.isSimulated() ||
		(localSimulatorURI != null && !localSimulatorURI.isEmpty()) :
			new PreconditionException(
					"currentExecutionType.isSimulated() ||  "
							+ "(localSimulatorURI != null && "
							+ "!localSimulatorURI.isEmpty())");

		assert !currentExecutionType.isSIL() || accFactor > 0.0 :
			new PreconditionException(
					"!currentExecutionType.isSIL() || accFactor > 0.0");

		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.localSimulatorURI = localSimulatorURI;
		this.accFactor = accFactor;	

		if (this.currentExecutionType.isIntegrationTest()) {
			CookingPlate.VERBOSE = true;
		}

		this.initialise(cookingPlateInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * Initialize the Cooking Plate component.
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
	 * @throws Exception					<i>to do</i>.
	 */
	protected void initialise(String cookingPlateInboundPortURI)
			throws Exception {
		assert	cookingPlateInboundPortURI != null : new PreconditionException("cookingPlateInboundPortURI != null");
		assert	!cookingPlateInboundPortURI.isEmpty() : new PreconditionException("!cookingPlateInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = 0; // 0°
		this.MAX_MODES = 7;
		this.cookingPlateInboudPort  = new CookingPlateInboundPort(cookingPlateInboundPortURI, this);
		this.cookingPlateInboudPort.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
			MILSimulationArchitectures.createCookingPlateMILArchitecture();
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
			MILSimulationArchitectures.createCookingPlateRTArchitecture(
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

		if (CookingPlate.VERBOSE) {
			this.tracer.get().setTitle("Cooking Plate component");
			this.tracer.get().setRelativePosition(1, 1);
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
	public synchronized void start() throws ComponentStartException {
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

	/***********************************************************************************/
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
			this.traceMessage("Cooking Plate returns its state : " + this.currentState + ".\n");
		}

		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * Current mode getter.
	 * 
	 * @return number of the current mode.
	 */
	public int getMode() {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking Plate returns its mode : " + this.currentMode + ".\n");
		}
		return this.currentMode;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public int getTemperature() throws Exception {
		if (CookingPlate.VERBOSE) { 
			this.traceMessage("Cooking Plate returns its temperature : " + CookingPlateTemperature[this.currentMode] + "° corresponding mode n°"+this.currentMode+".\n");
		}

		return CookingPlateTemperature[this.currentMode];
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOn() throws Exception {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking Plate is turned ON.\n");
		}

		assert this.currentState == CookingPlateState.OFF : new PreconditionException("this.currentState == CookingPlateState.OFF");

		this.currentState = CookingPlateState.ON;
		this.currentMode = 0;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the CookingPlateStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					CookingPlateStateModel.SIL_URI,
					t -> new SwitchOnCookingPlate(t));
		}
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void turnOff() throws Exception {
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking Plate is turned OFF.\n");
		}

		assert this.currentState == CookingPlateState.ON : new PreconditionException("currentState == CookingPlateState.ON");

		this.currentState = CookingPlateState.OFF;
		this.currentMode = 0;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the CookingPlateStateModel
			// to make it change its state to off.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					CookingPlateStateModel.SIL_URI,
					t -> new SwitchOffCookingPlate(t));
		}
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void increaseMode() throws Exception {
		assert	this.currentState == CookingPlateState.ON :
			new PreconditionException("this.currentState == CookingPlateState.ON");

		assert this.currentMode < MAX_MODES: new PreconditionException("this.currentMode < "+MAX_MODES);

		int nextMode = this.currentMode+1; 
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is increasing its mode from n°"+this.currentMode+" ("+CookingPlateTemperature[this.currentMode]+"°) to n°"+nextMode+" ("+CookingPlateTemperature[nextMode]+"°)"+"\n");
		}

		this.currentMode = nextMode; 

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the CookingPlateStateModel
			// to make it change its mode to high.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					CookingPlateStateModel.SIL_URI,
					t -> new IncreaseCookingPlate(t));
		}
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void decreaseMode() throws Exception {
		assert	this.currentState == CookingPlateState.ON : new PreconditionException("currentState == CookingPlateState.ON");

		assert this.currentMode > 0: new PreconditionException("this.currentMode > 0");

		int nextMode = this.currentMode-1; 
		if (CookingPlate.VERBOSE) {
			this.traceMessage("Cooking plate is decrasing its mode from n°"+this.currentMode+" ("+CookingPlateTemperature[this.currentMode]+"°) to n°"+nextMode+" ("+CookingPlateTemperature[nextMode]+"°)"+"\n");
		}

		this.currentMode = nextMode;
		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the CookingPlateStateModel
			// to make it change its mode to low.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					CookingPlateStateModel.SIL_URI,
					t -> new DecreaseCookingPlate(t));
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
