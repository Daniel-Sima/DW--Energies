package equipments.CookingPlate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

import equipments.CookingPlate.CookingPlateImplementationI.CookingPlateState;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e1.CVMIntegrationTest;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

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
extends AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected CookingPlateOutboundPort  cookingPlateOutboundPort;
	protected String					cookingPlateInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	protected CookingPlateTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, CookingPlate.INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	protected CookingPlateTester(boolean isUnitTest, String cookingPlateOutboundPort)
			throws Exception {
		super(1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(cookingPlateOutboundPort);
	}

	/***********************************************************************************/
	protected CookingPlateTester(boolean isUnitTest,
			String cookingPlateOutboundPort, 
			String reflectionInboundPortURI
			) throws Exception {
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(cookingPlateOutboundPort);
	}

	/***********************************************************************************/
	protected void initialise(String cookingPlateInboundPortURI) throws Exception {
		this.cookingPlateInboundPortURI = cookingPlateInboundPortURI;
		this.cookingPlateOutboundPort = new CookingPlateOutboundPort(this);
		this.cookingPlateOutboundPort.publishPort();

		this.tracer.get().setTitle("Cooking plate tester component");
		this.tracer.get().setRelativePosition(0, 0);
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
			assertEquals(50, this.cookingPlateOutboundPort.getTemperature());
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
			assertEquals(50, this.cookingPlateOutboundPort.getTemperature());
		} catch (Exception e) {
			assertTrue(false);
		}
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
		this.logMessage("testSetLowHigh()... ");
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
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertThrows(ExecutionException.class,
					() -> this.cookingPlateOutboundPort.increaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.cookingPlateOutboundPort.decreaseMode();
			assertEquals(CookingPlateState.ON, this.cookingPlateOutboundPort.getState());
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

	/***********************************************************************************/
	protected void runAllTests() {
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
	public synchronized void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(
					this.cookingPlateOutboundPort.getPortURI(),
					cookingPlateInboundPortURI,
					CookingPlateConnector.class.getCanonicalName());
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
		if (!this.isUnitTest) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();

			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

			System.out.println("Cooking Plate Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
							CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Cooking Plate Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Cooking Plate Tester waits to perform tests");

			this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		System.out.println("Cooking Plate Tester ends");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception
	{
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

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/