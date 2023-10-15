package production.aleatory.SolarPanel;

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
import production.aleatory.SolarPanel.connections.SolarPanelExternalControlConnector;
import production.aleatory.SolarPanel.connections.SolarPanelExternalControlOutboundPort;
import production.aleatory.SolarPanel.connections.SolarPanelMeteoControlConnector;
import production.aleatory.SolarPanel.connections.SolarPanelMeteoControlOutboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanelTester</code> implements a component performing 
 * tests for the class <code>SolarPanel</code> as a BCM component.
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
@RequiredInterfaces(required={SolarPanelExternalControlCI.class,
		SolarPanelMeteoControlCI.class,
		ClocksServerCI.class})
public class SolarPanelTester 
extends	AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean isUnitTest;
	/** URI of the external control component interface inbound port.		*/
	protected String solarPanelExternalControlInboundPortURI;
	/** URI of the meteo control component interface inbound port.		*/
	protected String solarPanelMeteoControlInboundPortURI;
	/** port to connect to the clocks server.								*/

	/** external control component interface inbound port.					*/
	protected SolarPanelExternalControlOutboundPort	solarPanelExternalControlOutboundPort;
	/** meteo control component interface inbound port.					*/
	protected SolarPanelMeteoControlOutboundPort	solarPanelMeteoControlOutboundPort;
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a solar panel test component.
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
	protected SolarPanelTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, SolarPanel.EXTERNAL_CONTROL_INBOUND_PORT_URI, SolarPanel.METEO_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a solar panel test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarExternalControlInboundPortURI != null && !solarExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param solarPanelExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param solarPanelMeteoControlInboundPort  	URI of the meteo control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanelTester(
			boolean isUnitTest,
			String solarPanelExternalControlInboundPortURI,
			String solarPanelMeteoControlInboundPort
			) throws Exception {
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(solarPanelExternalControlInboundPortURI,
				solarPanelMeteoControlInboundPort);
	}

	/***********************************************************************************/
	/**
	 * create a solar panel test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest								true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI					URI of the reflection inbound port of the component.
	 * @param solarPanelExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param solarPanelMeteoControlInboundPortURI  	URI of the meteo control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanelTester(
			boolean isUnitTest,
			String reflectionInboundPortURI,
			String solarPanelExternalControlInboundPortURI,
			String solarPanelMeteoControlInboundPortURI
			) throws Exception{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(solarPanelExternalControlInboundPortURI,
				solarPanelMeteoControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * initialise a solar panel test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPortURI != null && !solarPanelMeteoControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param solarPanelExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param solarPanelMeteoControlInboundPortURI	    URI of the meteo control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
			String solarPanelExternalControlInboundPortURI,
			String solarPanelMeteoControlInboundPort
			) throws Exception{

		this.solarPanelExternalControlInboundPortURI =
				solarPanelExternalControlInboundPortURI;
		this.solarPanelExternalControlOutboundPort = new SolarPanelExternalControlOutboundPort(this);
		this.solarPanelExternalControlOutboundPort.publishPort();

		this.solarPanelMeteoControlInboundPortURI = solarPanelMeteoControlInboundPort;
		this.solarPanelMeteoControlOutboundPort = new SolarPanelMeteoControlOutboundPort(this);
		this.solarPanelMeteoControlOutboundPort.publishPort();

		this.tracer.get().setTitle("Solar Panel tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	protected void testPowerLevel()
	{
		this.traceMessage("testPowerLevel...\n");
		try {
			assertEquals(400, this.solarPanelExternalControlOutboundPort.getMaxPowerLevelProduction());
			assertEquals(0, this.solarPanelExternalControlOutboundPort.getCurrentPowerLevelProduction());
			this.solarPanelMeteoControlOutboundPort.setPowerLevelProduction(1);
			assertEquals(4, this.solarPanelExternalControlOutboundPort.getCurrentPowerLevelProduction());
			this.solarPanelMeteoControlOutboundPort.setPowerLevelProduction(50);
			assertEquals(200, this.solarPanelExternalControlOutboundPort.getCurrentPowerLevelProduction());
			this.solarPanelMeteoControlOutboundPort.setPowerLevelProduction(100);
			assertEquals(400, this.solarPanelExternalControlOutboundPort.getCurrentPowerLevelProduction());
			this.solarPanelMeteoControlOutboundPort.setPowerLevelProduction(0);
			assertEquals(0, this.solarPanelExternalControlOutboundPort.getCurrentPowerLevelProduction());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		
		try {
			assertThrows(ExecutionException.class,
					() -> this.solarPanelMeteoControlOutboundPort.setPowerLevelProduction(200));
		} catch (Exception e) {
			assertTrue(false);
		}
		
		this.traceMessage("...testPowerLevel() done.\n");
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
					this.solarPanelExternalControlOutboundPort.getPortURI(),
					solarPanelExternalControlInboundPortURI,
					SolarPanelExternalControlConnector.class.getCanonicalName());

			this.doPortConnection(
					this.solarPanelMeteoControlOutboundPort.getPortURI(),
					solarPanelMeteoControlInboundPortURI,
					SolarPanelMeteoControlConnector.class.getCanonicalName());
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

			System.out.println("Solar Panel Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
							CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Solar Panel Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Solar Panel Tester waits to perform tests");

			this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.testPowerLevel();
		System.out.println("Solar Panel Tester ends");
	}
	
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception
	{
		this.doPortDisconnection(this.solarPanelExternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(this.solarPanelMeteoControlOutboundPort.getPortURI());
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
			this.solarPanelExternalControlOutboundPort.unpublishPort();
			this.solarPanelMeteoControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/