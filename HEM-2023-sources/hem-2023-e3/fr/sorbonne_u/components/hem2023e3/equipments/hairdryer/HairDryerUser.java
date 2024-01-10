package fr.sorbonne_u.components.hem2023e3.equipments.hairdryer;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerConnector;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerMode;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerState;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023e3.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerOutboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerUserCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HairDryerTester</code> implements a component performing
 * tests for the class <code>HairDryer</code> as a BCM component.
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {HairDryerUserCI.class, ClocksServerCI.class})
public class			HairDryerUser
extends		AbstractCyPhyComponent
implements	HairDryerOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** standard reflection, inbound port URI for the {@code HairDryerUser}
	 *  component.															*/
	public static final String			REFLECTION_INBOUND_PORT_URI =
													"HAIR-DRYER-USER-RIP-URI";
	/** when true, operations are traced.									*/
	public static boolean				VERBOSE = true ;

	/** outbound port to connect to the {@code HairDryer} component.		*/
	protected HairDryerOutboundPort		hdop;
	/** service inbound port URI of the {@code HairDryer} component.		*/
	protected String					hairDryerInboundPortURI;

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
	 * create a hair dryer component with the standard URIs and execution types.
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
	protected			HairDryerUser() throws Exception
	{
		this(HairDryer.INBOUND_PORT_URI, ExecutionType.STANDARD);
	}

	/**
	 * create a hair dryer component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code currentExecutionType.isStandard() || currentExecutionType.isUnitTest()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			HairDryerUser(
		String hairDryerInboundPortURI,
		ExecutionType currentExecutionType
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, hairDryerInboundPortURI,
			 currentExecutionType, null, null, 0.0);

		assert	currentExecutionType.isStandard() ||
										currentExecutionType.isIntegrationTest() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "currentExecutionType.isUnitTest()");
	}

	/**
	 * create a hair dryer component with the given URIs and execution types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null && !hairDryerInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the component.
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI			URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor					acceleration factor for the simulation.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			HairDryerUser(
		String reflectionInboundPortURI,
		String hairDryerInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 1);

		assert	hairDryerInboundPortURI != null &&
										!hairDryerInboundPortURI.isEmpty() :
				new PreconditionException(
						"hairDryerInboundPortURI != null && "
						+ "!hairDryerInboundPortURI.isEmpty()");
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

		this.initialise(hairDryerInboundPortURI);
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
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(
		String hairDryerInboundPortURI
		) throws Exception
	{
		this.hairDryerInboundPortURI = hairDryerInboundPortURI;
		this.hdop = new HairDryerOutboundPort(this);
		this.hdop.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
								createHairDryerUserMILArchitecture();
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
							createHairDryerUserMILRTArchitecture(
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

		this.tracer.get().setTitle("Hair dryer user component");
		this.tracer.get().setRelativePosition(2, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal testing method triggered by the SIL simulator
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (VERBOSE) {
			this.logMessage("HairDryerUser#turnOn().");
		}
		try {
			this.hdop.turnOn();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		if (VERBOSE) {
			this.logMessage("HairDryerUser#turnOff().");
		}
		try {
			this.hdop.turnOff();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#setHigh()
	 */
	@Override
	public void			setHigh()
	{
		if (VERBOSE) {
			this.logMessage("HairDryerUser#setHigh().");
		}
		try {
			this.hdop.setHigh();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.hairdryer.mil.HairDryerOperationI#setLow()
	 */
	@Override
	public void			setLow()
	{
		if (VERBOSE) {
			this.logMessage("HairDryerUser#setLow().");
		}
		try {
			this.hdop.setLow();
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
			assertEquals(HairDryerState.OFF, this.hdop.getState());
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
			assertEquals(HairDryerMode.LOW, this.hdop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void			testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(HairDryerState.OFF, this.hdop.getState());
			this.hdop.turnOn();
			assertEquals(HairDryerState.ON, this.hdop.getState());
			assertEquals(HairDryerMode.LOW, this.hdop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.hdop.turnOn());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.hdop.turnOff();
			assertEquals(HairDryerState.OFF, this.hdop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.hdop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void			testSetLowHigh()
	{
		this.logMessage("testSetLowHigh()... ");
		try {
			this.hdop.turnOn();
			this.hdop.setHigh();
			assertEquals(HairDryerState.ON, this.hdop.getState());
			assertEquals(HairDryerMode.HIGH, this.hdop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.hdop.setHigh());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.hdop.setLow();
			assertEquals(HairDryerState.ON, this.hdop.getState());
			assertEquals(HairDryerMode.LOW, this.hdop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
						 () -> this.hdop.setLow());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.hdop.turnOff();
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
		this.testSetLowHigh();
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
			this.doPortConnection(
					this.hdop.getPortURI(),
					this.hairDryerInboundPortURI,
					HairDryerConnector.class.getCanonicalName());

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
		this.logMessage("HairDryerUser executes.");
		if (this.currentExecutionType.isIntegrationTest() ||
										this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("HairDryerUser gets the clock.");
			this.acceleratedClock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(
								this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("HairDryerUser waits until start time.");
			this.acceleratedClock.waitUntilStart();
			this.logMessage("HairDryerUser starts.");
			if (this.currentExecutionType.isIntegrationTest()) {
				this.logMessage("HairDryerUser begins to perform unit tests.");
				this.runAllTests();
				this.logMessage("HairDryerUser unit tests end.");
			} else {
				this.logMessage("HairDryerUser begins to perform SIL scenario.");
				this.silTestScenario();
				this.logMessage("HairDryerUser SIL scenario end.");				
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.hdop.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hdop.unpublishPort();
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
		Instant switchOn = startInstant.plusSeconds(3600L);
		Instant setHigh = startInstant.plusSeconds(3800L);
		Instant setLow = startInstant.plusSeconds(4500L);
		Instant switchOff = startInstant.plusSeconds(5000L);

		// For each action, compute the waiting time for this action using the
		// above instant and the clock, and then schedule the rask that will
		// perform the action at the appropriate time.
		long delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOn);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOn);
		this.scheduleTask(
				o -> ((HairDryerUser)o).turnOn(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setHigh);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setHigh);
		this.scheduleTask(
				o -> ((HairDryerUser)o).setHigh(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(setLow);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + setLow);
		this.scheduleTask(
				o -> ((HairDryerUser)o).setLow(),
				delayInNanos, TimeUnit.NANOSECONDS);
		delayInNanos = this.acceleratedClock.nanoDelayUntilInstant(switchOff);
		this.logMessage(
				"HairDryer#silTestScenario waits for " + delayInNanos
				+ " " + TimeUnit.NANOSECONDS + " i.e., "
				+ TimeUnit.NANOSECONDS.toMillis(delayInNanos)
												+ " " + TimeUnit.MILLISECONDS
				+ " to reach " + switchOff);
		this.scheduleTask(
				o -> ((HairDryerUser)o).turnOff(),
				delayInNanos, TimeUnit.NANOSECONDS);
	}
}
// -----------------------------------------------------------------------------
