package production.intermittent.PetrolGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorExternalControlConnector;
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorExternalControlOutboundPort;
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorInternalControlConnector;
import production.intermittent.PetrolGenerator.connections.PetrolGeneratorInternalControlOutboundPort;

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
@RequiredInterfaces(required={PetrolGeneratorExternalControlCI.class,
		PetrolGeneratorInternalControlCI.class,
		ClocksServerCI.class})
public class PetrolGeneratorTester 
extends	AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean isUnitTest;
	/** URI of the external control component interface inbound port.		*/
	protected String petrolGeneratorExternalControlInboundPortURI;
	/** URI of the meteo control component interface inbound port.		*/
	protected String petrolGeneratorInternalControlInboundPortURI;
	/** port to connect to the clocks server.								*/

	/** external control component interface inbound port.					*/
	protected PetrolGeneratorExternalControlOutboundPort petrolGeneratorExternalControlOutboundPort;
	/** internal control component interface inbound port.					*/
	protected PetrolGeneratorInternalControlOutboundPort petrolGeneratorInternalControlOutboundPort;
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a Petrol Generator test component.
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
	protected PetrolGeneratorTester(boolean isUnitTest) throws Exception {
		this(isUnitTest, PetrolGenerator.EXTERNAL_CONTROL_INBOUND_PORT_URI, PetrolGenerator.INTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a Petrol Generator test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code petrolGeneratorExternalControlOutboundPort != null && !petrolGeneratorExternalControlOutboundPort.isEmpty()}
	 * pre	{@code petrolGeneratorInternalControlOutboundPort != null && !petrolGeneratorInternalControlOutboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param petrolGeneratorExternalControlOutboundPort	URI of the external control component interface inbound port.
	 * @param petrolGeneratorInternalControlOutboundPort  	URI of the internal control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected PetrolGeneratorTester(
			boolean isUnitTest,
			String petrolGeneratorExternalControlOutboundPort,
			String petrolGeneratorInternalControlOutboundPort
			) throws Exception {
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(petrolGeneratorExternalControlOutboundPort,
				petrolGeneratorInternalControlOutboundPort);
	}

	/***********************************************************************************/
	/**
	 * create a Petrol Generator test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code petrolGeneratorExternalControlOutboundPort != null && !petrolGeneratorExternalControlOutboundPort.isEmpty()}
	 * pre	{@code petrolGeneratorInternalControlOutboundPort != null && !petrolGeneratorInternalControlOutboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param petrolGeneratorExternalControlOutboundPort	URI of the external control component interface inbound port.
	 * @param petrolGeneratorInternalControlOutboundPort  	URI of the internal control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected PetrolGeneratorTester(
			boolean isUnitTest,
			String reflectionInboundPortURI,
			String petrolGeneratorExternalControlOutboundPort,
			String petrolGeneratorInternalControlOutboundPort
			) throws Exception{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(petrolGeneratorExternalControlOutboundPort,
				petrolGeneratorInternalControlOutboundPort);
	}


	/***********************************************************************************/
	/**
	 * initialise a Petrol Generator test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code petrolGeneratorExternalControlInboundPortURI != null && !petrolGeneratorExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code petrolGeneratorInternalControlInboundPortURI != null && !petrolGeneratorInternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param petrolGeneratorExternalControlInboundPortURI	    URI of the external control component interface inbound port.
	 * @param petrolGeneratorInternalControlInboundPortURI	    URI of the internal control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
			String petrolGeneratorExternalControlInboundPortURI,
			String petrolGeneratorInternalControlInboundPortURI
			) throws Exception{

		this.petrolGeneratorExternalControlInboundPortURI =
				petrolGeneratorExternalControlInboundPortURI;
		this.petrolGeneratorExternalControlOutboundPort = new PetrolGeneratorExternalControlOutboundPort(this);
		this.petrolGeneratorExternalControlOutboundPort.publishPort();

		this.petrolGeneratorInternalControlInboundPortURI = petrolGeneratorInternalControlInboundPortURI;
		this.petrolGeneratorInternalControlOutboundPort = new PetrolGeneratorInternalControlOutboundPort(this);
		this.petrolGeneratorInternalControlOutboundPort.publishPort();

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
		this.traceMessage("testPowerLevel...\n");
		try {
			assertFalse(this.petrolGeneratorExternalControlOutboundPort.on());
			
			this.petrolGeneratorExternalControlOutboundPort.switchOn();
			assertTrue(this.petrolGeneratorExternalControlOutboundPort.on());
			
			assertFalse(this.petrolGeneratorInternalControlOutboundPort.isProducing());
			this.petrolGeneratorInternalControlOutboundPort.startProducing();
			assertTrue(this.petrolGeneratorInternalControlOutboundPort.isProducing());
			
			assertEquals(5, this.petrolGeneratorExternalControlOutboundPort.getMaxPetrolLevel());
			assertEquals(5, this.petrolGeneratorExternalControlOutboundPort.getCurrentPetrolLevel());
			
			assertEquals(2000, this.petrolGeneratorExternalControlOutboundPort.getMaxPowerProductionLevel());
			assertEquals(0, this.petrolGeneratorExternalControlOutboundPort.getCurrentPowerLevel());
			
			this.petrolGeneratorExternalControlOutboundPort.fillFuelTank(10);
			assertEquals(5, this.petrolGeneratorExternalControlOutboundPort.getCurrentPetrolLevel());
			
			this.petrolGeneratorInternalControlOutboundPort.stopProducing();
			assertFalse(this.petrolGeneratorInternalControlOutboundPort.isProducing());
			
			this.petrolGeneratorExternalControlOutboundPort.switchOff();
			assertFalse(this.petrolGeneratorExternalControlOutboundPort.on());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
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
					this.petrolGeneratorExternalControlOutboundPort.getPortURI(),
					petrolGeneratorExternalControlInboundPortURI,
					PetrolGeneratorExternalControlConnector.class.getCanonicalName());

			this.doPortConnection(
					this.petrolGeneratorInternalControlOutboundPort.getPortURI(),
					petrolGeneratorInternalControlInboundPortURI,
					PetrolGeneratorInternalControlConnector.class.getCanonicalName());
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

			System.out.println("Petrol Generator Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
							CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Petrol Generator  Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Petrol Generator  Tester waits to perform tests");

			this.doPortDisconnection(
					this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.tests();
		System.out.println("Petrol Generator  Tester ends");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception
	{
		this.doPortDisconnection(this.petrolGeneratorExternalControlOutboundPort.getPortURI());
		this.doPortDisconnection(this.petrolGeneratorInternalControlOutboundPort.getPortURI());
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
			this.petrolGeneratorExternalControlOutboundPort.unpublishPort();
			this.petrolGeneratorInternalControlOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/