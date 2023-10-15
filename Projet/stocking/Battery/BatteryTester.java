package stocking.Battery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;

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
import stocking.Battery.Battery.BatteryState;
import stocking.Battery.connections.BatteryExternalConnector;
import stocking.Battery.connections.BatteryExternalControlOutboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>PetrolGeneratorTester</code> implements a component performing 
 * tests for the class <code>PetrolGenerator</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@RequiredInterfaces(required={BatteryExternalControlCI.class,
		ClocksServerCI.class})
public class BatteryTester 
extends	AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean isUnitTest;
	/** URI of the external control component interface inbound port.		*/
	protected String batteryExternalControlInboundPortURI;

	/** external control component interface inbound port.					*/
	protected BatteryExternalControlOutboundPort batteryExternalControlOutboundPort;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a Battery test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest	true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @throws Exception	<i>to do</i>.
	 */
	protected BatteryTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, Battery.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a Battery test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param batteryExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected BatteryTester(
			boolean isUnitTest,
			String batteryExternalControlInboundPortURI
			) throws Exception {
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(batteryExternalControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create a Battery test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest								true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI					URI of the reflection inbound port of the component.
	 * @param batteryExternalControlInboundPortURI		URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected BatteryTester(
			boolean isUnitTest,
			String reflectionInboundPortURI,
			String batteryExternalControlInboundPortURI
			) throws Exception{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(batteryExternalControlInboundPortURI);	
	}

	/***********************************************************************************/
	/**
	 * initialise a Battery test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param batteryExternalControlInboundPortURI	    URI of the internal control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String batteryExternalControlInboundPortURI
			) throws Exception{

		this.batteryExternalControlInboundPortURI =
				batteryExternalControlInboundPortURI;
		this.batteryExternalControlOutboundPort = new BatteryExternalControlOutboundPort(this);
		this.batteryExternalControlOutboundPort.publishPort();

		this.tracer.get().setTitle("Petrol Generator tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	protected void tests()
	{
		this.traceMessage("tests...\n");
		try {
			assertEquals(5000, this.batteryExternalControlOutboundPort.getMaxPowerCapacity());
			
			assertEquals(BatteryState.CONSUMING, this.batteryExternalControlOutboundPort.getBatteryState());
			assertEquals(0, this.batteryExternalControlOutboundPort.getCurrentPowerLevel());
			
			this.batteryExternalControlOutboundPort.addPowerBattery(500);
			assertEquals(500, this.batteryExternalControlOutboundPort.getCurrentPowerLevel());
			
			this.batteryExternalControlOutboundPort.setBatteryState(BatteryState.PRODUCING);
			assertEquals(BatteryState.PRODUCING, this.batteryExternalControlOutboundPort.getBatteryState());
			assertEquals(500, this.batteryExternalControlOutboundPort.getCurrentPowerLevel());
			
			this.batteryExternalControlOutboundPort.pullPowerBattery(150);
			assertEquals(350, this.batteryExternalControlOutboundPort.getCurrentPowerLevel());
			
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		
		try {
			assertThrows(ExecutionException.class,
					() -> this.batteryExternalControlOutboundPort.pullPowerBattery(500));
		} catch (Exception e) {
			assertTrue(false);
		}

		this.traceMessage("...tests() done.\n");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException{
		super.start();

		try {
			this.doPortConnection(
					this.batteryExternalControlOutboundPort.getPortURI(),
					batteryExternalControlInboundPortURI,
					BatteryExternalConnector.class.getCanonicalName());

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

			System.out.println("Battery Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
							CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Battery Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Battery Tester waits to perform tests");

			this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.tests();
		System.out.println("Battery Tester ends");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception
	{
		this.doPortDisconnection(this.batteryExternalControlOutboundPort.getPortURI());
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
			this.batteryExternalControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/