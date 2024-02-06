package equipments.Lamp;

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
import utils.ExecutionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import global.CVMGlobalTest;
import equipments.Lamp.LampImplementationI.LampMode;
import equipments.Lamp.LampImplementationI.LampState;
import equipments.Lamp.mil.MILSimulationArchitectures;

// -----------------------------------------------------------------------------
/**
 * The class <code>LampTester</code> implements a component performing
 * tests for the class <code>Lamp</code> as a BCM component.
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
 * @author	<a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@RequiredInterfaces(required = {LampUserCI.class, ClocksServerCI.class})
public class			LampUser
extends		AbstractCyPhyComponent
implements	LampOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard reflection, inbound port URI for the {@code LampUser}
	 *  component.															*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
													"LAMP-USER-RIP-URI";
	/** when true, operations are traced.									*/
	public static boolean				VERBOSE = true ;

	/** outbound port to connect to the {@code Lamp} component.		*/
protected LampOutboundPort				lop;
	/** service inbound port URI of the {@code Lamp} component.		*/
	protected String					lampInboundPortURI;

	// Execution/Simulation

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
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	/** clock used for time-triggered synchronisation in test actions.		*/
	protected AcceleratedClock			acceleratedClock;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a lamp component with the standard URIs and execution types.
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
	protected			LampUser() throws Exception
	{
		this(Lamp.INBOUND_PORT_URI, ExecutionType.STANDARD);
	}

	/**
	 * create a lamp component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentExecutionType.isStandard() || currentExecutionType.isUnitTest()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param lampInboundPortURI	URI of the lamp inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			LampUser(
		String lampInboundPortURI,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, lampInboundPortURI,
			 currentExecutionType, null, null, 0.0);

		assert	currentExecutionType.isStandard() ||
										currentExecutionType.isIntegrationTest() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "currentExecutionType.isUnitTest()");
	}

	/**
	 * create a lamp component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code lampInboundPortURI != null && !lampInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param lampInboundPortURI		URI of the lamp inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI			URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor					acceleration factor for the simulation.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			LampUser(
		String reflectionInboundPortURI,
		String lampInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 1);

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

		this.initialise(lampInboundPortURI);
	}

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
	 * @param lampInboundPortURI	URI of the lamp inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String lampInboundPortURI
		) throws Exception
	{
		this.lampInboundPortURI = lampInboundPortURI;
		this.lop = new LampOutboundPort(this);
		this.lop.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
								createLampUserMILArchitecture();
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
							createLampUserMILRTArchitecture(
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

		this.tracer.get().setTitle("Lamp user component");
		this.tracer.get().setRelativePosition(2, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal testing method triggered by the SIL simulator
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Lamp.mil.LampOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (VERBOSE) {
			this.logMessage("LampUser#turnOn().");
		}
		try {
			this.lop.turnOn();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see equipments.lamp.mil.LampOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		if (VERBOSE) {
			this.logMessage("LampUser#turnOff().");
		}
		try {
			this.lop.turnOff();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see equipments.Lamp.mil.LampOperationI#decreaseMode()
	 */
	@Override
	public void			increaseMode()
	{
		if (VERBOSE) {
			this.logMessage("LampUser#increaseMode().");
		}
		try {
			this.lop.increaseMode();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see equipments.Lamp.mil.LampOperationI#decreaseMode()
	 */
	@Override
	public void			decreaseMode()
	{
		if (VERBOSE) {
			this.logMessage("LampUser#decreaseMode().");
		}
		try {
			this.lop.decreaseMode();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Component internal tests
	// -------------------------------------------------------------------------

	public void			testGetState()
	{
		this.logMessage("testGetState()... ");
		try {
			assertEquals(LampState.OFF, this.lop.getState());
		} catch (Exception e) {
			this.logMessage("...KO.");
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void			testGetMode()
	{
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(LampMode.LOW, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void			testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(LampState.OFF, this.lop.getState());
			this.lop.turnOn();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.LOW, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.turnOff();
			assertEquals(LampState.OFF, this.lop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void			testIncreaseDecrease()
	{
		this.logMessage("testIncreaseDecrease()... ");
		try {
			this.lop.turnOn();
			this.lop.increaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MEDIUM, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.increaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.HIGH, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.increaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.decreaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MEDIUM, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.decreaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.LOW, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.decreaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.turnOff();
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	protected void			runAllTests()
	{
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testIncreaseDecrease();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			System.out.println("LampUser starts.");
			System.out.println("LampUser connects to the CookingPlate component.");
			System.out.println(this.lop.getPortURI());
			System.out.println(lampInboundPortURI);
			this.doPortConnection(
					this.lop.getPortURI(),
					this.lampInboundPortURI,
					LampConnector.class.getCanonicalName());

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

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception
	{
		this.logMessage("LampUser executes.");
		if (this.currentExecutionType.isIntegrationTest() ||
										this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			
			this.logMessage("LampUser gets the clock.");
			this.acceleratedClock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(
								this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("LampUser waits until start time.");
			this.acceleratedClock.waitUntilStart();
			this.logMessage("LampUser starts.");
			
			if (this.currentExecutionType.isIntegrationTest()) {
				this.logMessage("LampUser begins to perform unit tests.");
				this.runAllTests();
				this.logMessage("LampUser unit tests end.");
			} else {
				this.logMessage("LampUser begins to perform SIL scenario.");
				this.silTestScenario();
				this.logMessage("LampUser SIL scenario end.");				
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.lop.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.lop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// SIL test scenarios
	// -------------------------------------------------------------------------

	protected void				silTestScenario()
	{
		// Define the instants of the different actions in the scenario.
		Instant startInstant = Instant.parse(CVMGlobalTest.START_INSTANT);
		Instant switchOn = startInstant.plusSeconds(1200L);
		Instant increaseToMedium = startInstant.plusSeconds(2400L);
		Instant increaseToHigh = startInstant.plusSeconds(3600L);
		Instant decreaseToMedium = startInstant.plusSeconds(4800L);
		Instant decreaseToLow = startInstant.plusSeconds(6000L);
		Instant switchOff = startInstant.plusSeconds(7200L);

		// For each action, compute the waiting time for this action using the
		// above instant and the clock, and then schedule the rask that will
		// perform the action at the appropriate time.
		long delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOn);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOn);
		this.scheduleTask(
				o -> ((LampUser)o).turnOn(),
				delayInNanos, TimeUnit.NANOSECONDS);
		
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(increaseToMedium);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + increaseToMedium);
		this.scheduleTask(
				o -> ((LampUser)o).increaseMode(),
				delayInNanos, TimeUnit.NANOSECONDS);
		
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(increaseToHigh);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + increaseToHigh);
		this.scheduleTask(
				o -> ((LampUser)o).increaseMode(),
				delayInNanos, TimeUnit.NANOSECONDS);
		
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(decreaseToMedium);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + decreaseToMedium);
		this.scheduleTask(
				o -> ((LampUser)o).decreaseMode(),
				delayInNanos, TimeUnit.NANOSECONDS);

		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(decreaseToLow);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + decreaseToLow);
		this.scheduleTask(
				o -> ((LampUser)o).decreaseMode(),
				delayInNanos, TimeUnit.NANOSECONDS);
		
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOff);
		this.logMessage(
				"Lamp#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOff);
		this.scheduleTask(
				o -> ((LampUser)o).turnOff(),
				delayInNanos, TimeUnit.NANOSECONDS);
	}
}
// -----------------------------------------------------------------------------
