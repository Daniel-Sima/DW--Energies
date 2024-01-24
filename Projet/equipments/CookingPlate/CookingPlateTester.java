package equipments.CookingPlate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.CookingPlateImplementationI.CookingPlateState;
import equipments.CookingPlate.mil.CookingPlateOperationI;
import equipments.CookingPlate.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import global.CVMGlobalTest;
import utils.ExecutionType;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateTester</code> implements a component performing
 * tests for the class <code>CookingPlate</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@RequiredInterfaces(required = {CookingPlateUserCI.class, ClocksServerCI.class})
public class CookingPlateTester 
extends AbstractCyPhyComponent 
implements CookingPlateOperationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard reflection, inbound port URI for the {@code CookingPlateTester}
	 *  component.															*/
	public static final String REFLECTION_INBOUND_PORT_URI = "COOKING-PLATE-USER-RIP-URI";
	/** when true, operations are traced.									*/
	public static boolean VERBOSE = true ;

	/** outbound port to connect to the {@code CookingPlate} component.		*/
	protected CookingPlateOutboundPort  cookingPlateOutboundPort;
	/** service inbound port URI of the {@code CookingPlate} component.		*/
	protected String cookingPlateInboundPortURI;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String localSimulatorURI;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double accFactor;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort clocksServerOutboundPort;
	/** clock used for time-triggered synchronisation in test actions.		*/
	protected AcceleratedClock acceleratedClock;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * Create a Cooking Plate component with the standard URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected CookingPlateTester() throws Exception {
		this(CookingPlate.INBOUND_PORT_URI, ExecutionType.STANDARD);
	}

	/***********************************************************************************/
	/**
	 * create a Cooking Plate component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentExecutionType.isStandard() || currentExecutionType.isUnitTest()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param cookingPlateInboundPortURI	URI of the Cooking Plate inbound port.
	 * @param currentExecutionType			current execution type for the next run.
	 * @throws Exception					<i>to do</i>.
	 */
	protected CookingPlateTester(String cookingPlateInboundPortURI, ExecutionType currentExecutionType)
			throws Exception {
		this(REFLECTION_INBOUND_PORT_URI, cookingPlateInboundPortURI,
				currentExecutionType, null, null, 0.0);

		assert	currentExecutionType.isStandard() ||
		currentExecutionType.isIntegrationTest() :
			new PreconditionException(
					"currentExecutionType.isStandard() || "
							+ "currentExecutionType.isUnitTest()");
	}

	/***********************************************************************************/
	/**
	 * create a Cooking Plate component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code cookingPlateInboundPortURI != null && !cookingPlateInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection inbound port of the component.
	 * @param cookingPlateInboundPortURI	URI of the Cooking Plate inbound port.
	 * @param currentExecutionType			current execution type for the next run.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI				URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor						acceleration factor for the simulation.
	 * @throws Exception				<i>to do</i>.
	 */
	protected CookingPlateTester(
			String reflectionInboundPortURI,
			String cookingPlateInboundPortURI,
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			String localSimulatorURI,
			double accFactor
			) throws Exception {

		super(reflectionInboundPortURI, 2, 1);

		assert	cookingPlateInboundPortURI != null &&
				!cookingPlateInboundPortURI.isEmpty() :
					new PreconditionException(
							"cookingPlateInboundPortURI != null && "
									+ "!cookingPlateInboundPortURI.isEmpty()");
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

		this.initialise(cookingPlateInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * initialise the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param cookingPlateInboundPortURI	URI of the Cooking Plate inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void initialise(String cookingPlateInboundPortURI) throws Exception {
		this.cookingPlateInboundPortURI = cookingPlateInboundPortURI;
		this.cookingPlateOutboundPort = new CookingPlateOutboundPort(this);
		this.cookingPlateOutboundPort.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
			MILSimulationArchitectures.createCookingPlateUserMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
				new AssertionError(
						"local simulator " + this.localSimulatorURI
						+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
			put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
			architecture =
			MILSimulationArchitectures.
			createCookingPlateUserMILRTArchitecture(
					this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
				new AssertionError(
						"local simulator " + this.localSimulatorURI
						+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
			put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		default:
		}		


		this.tracer.get().setTitle("Cooking Plate tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------
	public void testGetState() {
		this.logMessage("testGetState()... ");
		try {
			assertEquals(CookingPlateState.OFF, this.cookingPlateOutboundPort.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	/***********************************************************************************/
	public void	testGetMode()
	{
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(0, this.cookingPlateOutboundPort.getTemperature());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	/***********************************************************************************/
	public void testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(CookingPlateState.OFF, this.cookingPlateOutboundPort.getState());
			this.cookingPlateOutboundPort.turnOn();
			assertEquals(CookingPlateState.ON, this.cookingPlateOutboundPort.getState());
			assertEquals(0, this.cookingPlateOutboundPort.getTemperature());
		} catch (Exception e) {
			assertTrue(false);
		}
		//		 PreconditionException raised with message this.currentState == CookingPlateState.OFF!
		try {
			assertThrows(ExecutionException.class,
					() -> this.cookingPlateOutboundPort.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.cookingPlateOutboundPort.turnOff();
			assertEquals(CookingPlateState.OFF, this.cookingPlateOutboundPort.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message currentState == CookingPlateState.ON!
		try {
			assertThrows(ExecutionException.class,
					() -> this.cookingPlateOutboundPort.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	/***********************************************************************************/
	public void	testIncreaseDecrease() {
		this.logMessage("testIncreaseDecrease()... ");
		try {
			this.cookingPlateOutboundPort.turnOn();
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlateState.ON, this.cookingPlateOutboundPort.getState());
			assertEquals(CookingPlate.CookingPlateTemperature[1], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[2], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[3], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[4], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[5], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[6], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.increaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[7], this.cookingPlateOutboundPort.getTemperature());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message this.currentMode < 7!
		try {
			assertThrows(ExecutionException.class,
					() -> this.cookingPlateOutboundPort.increaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlateState.ON, this.cookingPlateOutboundPort.getState());
			assertEquals(CookingPlate.CookingPlateTemperature[6], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[5], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[4], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[3], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[2], this.cookingPlateOutboundPort.getTemperature());
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[1], this.cookingPlateOutboundPort.getTemperature());	
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlate.CookingPlateTemperature[0], this.cookingPlateOutboundPort.getTemperature());	
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message this.currentMode > 0!
		try {
			assertThrows(ExecutionException.class,
					() -> this.cookingPlateOutboundPort.decreaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.cookingPlateOutboundPort.turnOff();
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	/**
	 * @throws Exception *********************************************************************************/
	protected void runAllTests() throws Exception {
		this.cookingPlateOutboundPort.printSeparator(" testGetState() ");
		this.testGetState();
		this.cookingPlateOutboundPort.printSeparator(" testGetMode() ");
		this.testGetMode();
		this.cookingPlateOutboundPort.printSeparator(" testTurnOnOff() ");
		this.testTurnOnOff();
		this.cookingPlateOutboundPort.printSeparator(" testIncreaseDecrease() ");
		this.testIncreaseDecrease();
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
			this.doPortConnection(
					this.cookingPlateOutboundPort.getPortURI(),
					cookingPlateInboundPortURI,
					CookingPlateConnector.class.getCanonicalName());
			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				AtomicSimulatorPlugin asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
						get(this.simArchitectureURI);
				Architecture architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				asp.setPluginURI(uri);
				asp.setSimulationArchitecture(architecture);
				this.installPlugin(asp);
				break;
			case MIL_RT_SIMULATION:
				RTAtomicSimulatorPlugin rtasp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
						get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				rtasp.setPluginURI(uri);
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		this.logMessage("Cooking Plate executes.");
		if (this.currentExecutionType.isIntegrationTest() ||
				this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();

			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

			this.logMessage("Cooking Plate Tester gets the clock");

			this.acceleratedClock =
					this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());

			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("Cooking Plate Tester waits until start time.");

			this.acceleratedClock.waitUntilStart();
			this.logMessage("Cooking Plate Tester starts.");
			if (this.currentExecutionType.isIntegrationTest()) {
				this.logMessage("Cooking Plate Tester begins to perform unit tests.");
				this.runAllTests();
				this.logMessage("Cooking Plate Tester unit tests end.");
			} else {
				this.logMessage("Cooking Plate Tester begins to perform SIL scenario.");
				this.silTestScenario();
				this.logMessage("Cooking Plate Tester SIL scenario end.");				
			}
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.cookingPlateOutboundPort.getPortURI());
		super.finalise();
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.cookingPlateOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal testing method triggered by the SIL simulator
	// -------------------------------------------------------------------------

	/**
	 * @see 
	 */
	@Override
	public void turnOn() {
		if (VERBOSE) {
			this.logMessage("CookingPlateTester#turnOn().");
		}
		try {
			this.cookingPlateOutboundPort.turnOn();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void turnOff() {
		if (VERBOSE) {
			this.logMessage("CookingPlateTester#turnOff().");
		}
		try {
			this.cookingPlateOutboundPort.turnOff();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void increaseMode() {
		if (VERBOSE) {
			this.logMessage("CookingPlateTester#increaseMode().");
		}
		try {
			this.cookingPlateOutboundPort.increaseMode();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public void decreaseMode() {
		if (VERBOSE) {
			this.logMessage("CookingPlateTester#decreaseMode().");
		}
		try {
			this.cookingPlateOutboundPort.decreaseMode();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// SIL test scenarios
	// -------------------------------------------------------------------------

	protected void silTestScenario() {
		// Define the instants of the different actions in the scenario.
		Instant startInstant = Instant.parse(CVMGlobalTest.START_INSTANT);
		Instant switchOn = startInstant.plusSeconds(3600L);
		Instant increaseMode = startInstant.plusSeconds(3800L);
		Instant decreaseMode = startInstant.plusSeconds(5600L);
		Instant switchOff = startInstant.plusSeconds(7400L);

		long addTime = 200L;

		// For each action, compute the waiting time for this action using the
		// above instant and the clock, and then schedule the task that will
		// perform the action at the appropriate time.
		long delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOn);
		this.logMessage(
				"CookingPlate#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
				+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOn);
		this.scheduleTask(
				o -> ((CookingPlateTester)o).turnOn(),
				delayInNanos, TimeUnit.NANOSECONDS);

		for (int i=1; i<=8; i++) {
			delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(increaseMode);
			this.logMessage(
					"CookingPlate#silTestScenario waits for " + delayInNanos
					+ " " + TimeUnit.NANOSECONDS + " i.e., "
					+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
					+ " " + TimeUnit.MILLISECONDS
					+ " to reach " + increaseMode);
			this.scheduleTask(
					o -> ((CookingPlateTester)o).increaseMode(),
					delayInNanos, TimeUnit.NANOSECONDS);
			increaseMode = startInstant.plusSeconds(3800L + addTime*i);
		}

		for (int i=1; i<=8; i++) {
			delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(decreaseMode);
			this.logMessage(
					"CookingPlate#silTestScenario waits for " + delayInNanos
					+ " " + TimeUnit.NANOSECONDS + " i.e., "
					+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
					+ " " + TimeUnit.MILLISECONDS
					+ " to reach " + decreaseMode);
			this.scheduleTask(
					o -> ((CookingPlateTester)o).decreaseMode(),
					delayInNanos, TimeUnit.NANOSECONDS);
			decreaseMode = startInstant.plusSeconds(5600L + addTime*i);
		}
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOff);
		this.logMessage(
				"CookingPlate#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
				+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOff);
		this.scheduleTask(
				o -> ((CookingPlateTester)o).turnOff(),
				delayInNanos, TimeUnit.NANOSECONDS);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/