package equipments.Fridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import equipments.Fridge.connections.FridgeExternalControlConnector;
import equipments.Fridge.connections.FridgeExternalControlOutboundPort;
import equipments.Fridge.connections.FridgeInternalControlConnector;
import equipments.Fridge.connections.FridgeInternalControlOutboundPort;
import equipments.Fridge.connections.FridgeUserConnector;
import equipments.Fridge.connections.FridgeUserOutboundPort;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
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

// -----------------------------------------------------------------------------
/**
 * The class <code>FridgeTester</code> implements a component performing 
 * tests for the class <code>Fridge</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@RequiredInterfaces(required={FridgeUserCI.class,
							  FridgeInternalControlCI.class,
							  FridgeExternalControlCI.class,
							  ClocksServerCI.class})
public class			FridgeTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean		isUnitTest;
	/** URI of the user component interface inbound port.					*/
	protected String			FridgeUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			FridgeInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			FridgeExternalControlInboundPortURI;

	/** user component interface inbound port.								*/
	protected FridgeUserOutboundPort			acop;
	/** internal control component interface outbound port.					*/
	protected FridgeInternalControlOutboundPort	acicop;
	/** external control component interface outbound port.					*/
	protected FridgeExternalControlOutboundPort	acecop;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Fridge test component.
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
	protected			FridgeTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 Fridge.USER_INBOUND_PORT_URI,
			 Fridge.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 Fridge.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a Fridge test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param FridgeUserInboundPortURI				URI of the user component interface inbound port.
	 * @param FridgeInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param FridgeExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			FridgeTester(
		boolean isUnitTest,
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(FridgeUserInboundPortURI,
				FridgeInternalControlInboundPortURI,
				FridgeExternalControlInboundPortURI);
	}

	/**
	 * create a Fridge test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param FridgeUserInboundPortURI				URI of the user component interface inbound port.
	 * @param FridgeInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param FridgeExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			FridgeTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(FridgeUserInboundPortURI,
						FridgeInternalControlInboundPortURI,
						FridgeExternalControlInboundPortURI);
	}

	/**
	 * initialise a Fridge test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param FridgeUserInboundPortURI				URI of the user component interface inbound port.
	 * @param FridgeInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param FridgeExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		this.FridgeUserInboundPortURI = FridgeUserInboundPortURI;
		this.acop = new FridgeUserOutboundPort(this);
		this.acop.publishPort();
		this.FridgeInternalControlInboundPortURI =
									FridgeInternalControlInboundPortURI;
		this.acicop = new FridgeInternalControlOutboundPort(this);
		this.acicop.publishPort();
		this.FridgeExternalControlInboundPortURI =
									FridgeExternalControlInboundPortURI;
		this.acecop = new FridgeExternalControlOutboundPort(this);
		this.acecop.publishPort();

		this.tracer.get().setTitle("Fridge tester component");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testSwitchOnSwitchOff()
	{
		this.traceMessage("testSwitchOnSwitchOff...\n");
		try {
			this.acop.switchOn();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		try {
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testSwitchOnSwitchOff() done.\n");
	}

	protected void		testOn()
	{
		this.traceMessage("testOn()...\n");
		try {
			assertEquals(false, this.acop.on());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		try {
			this.acop.switchOn();
			assertEquals(true, this.acop.on());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testOn() done.\n");
	}

	protected void		testTargetCoolerTemperature()
	{
		this.traceMessage("testTargetCoolerTemperature()...\n");
		try {
			this.acop.setTargetCoolerTemperature(10.0);
			assertEquals(10.0, this.acop.getTargetCoolerTemperature());
			this.acop.setTargetCoolerTemperature(Fridge.STANDARD_TARGET_COOLER_TEMPERATURE);
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testTargetTemperature() done.\n");

	}

	protected void		testCurrentCoolerTemperature()
	{
		this.traceMessage("testCurrentCoolerTemperature()...\n");
		try {
			this.acop.switchOn();
			assertEquals(Fridge.FAKE_CURRENT_COOLER_TEMPERATURE,
						 this.acop.getCurrentCoolerTemperature());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testCurrentCoolerTemperature() done.\n");
	}
	
	protected void		testTargetFreezerTemperature()
	{
		this.traceMessage("testTargetFreezerTemperature()...\n");
		try {
			this.acop.setTargetFreezerTemperature(-15.0);
			assertEquals(-15.0, this.acop.getTargetFreezerTemperature());
			this.acop.setTargetFreezerTemperature(Fridge.STANDARD_TARGET_FREEZER_TEMPERATURE);
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testTargetTemperature() done.\n");

	}

	protected void		testCurrentFreezerTemperature()
	{
		this.traceMessage("testCurrentFreezerTemperature()...\n");
		try {
			this.acop.switchOn();
			assertEquals(Fridge.FAKE_CURRENT_FREEZER_TEMPERATURE,
						 this.acop.getCurrentFreezerTemperature());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testCurrentFreezerTemperature() done.\n");
	}

	protected void		testPowerLevel()
	{
		this.traceMessage("testPowerLevel()...\n");
		try {
			assertEquals(Fridge.MAX_POWER_LEVEL,
						 this.acop.getMaxPowerLevel());
			this.acop.switchOn();
			this.acop.setCurrentPowerLevel(Fridge.MAX_POWER_LEVEL/2.0);
			assertEquals(Fridge.MAX_POWER_LEVEL/2.0,
						 this.acop.getCurrentPowerLevel());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testPowerLevel() done.\n");
	}

	protected void		testInternalControlForCooler()
	{
		this.traceMessage("testInternalControlForCooler()...\n");
		try {
			this.acicop.getTargetCoolerTemperature();
			this.acop.switchOn();
			assertEquals(true, this.acop.on());
			assertEquals(Fridge.FAKE_CURRENT_COOLER_TEMPERATURE,
						 this.acicop.getCurrentCoolerTemperature());
			this.acicop.setTargetCoolerTemperature(3.0);
			assertEquals(3.0, this.acicop.getTargetCoolerTemperature());
			this.acicop.startCooling();
			assertEquals(true, this.acicop.cooling());
			this.acicop.stopCooling();
			assertEquals(false, this.acicop.cooling());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...testInternalControlForCooler() done.\n");
	}
	
	protected void		testInternalControlForFreezer()
	{
		this.traceMessage("testInternalControlForFreezer()...\n");
		try {
			this.acicop.getTargetFreezerTemperature();
			this.acop.switchOn();
			assertEquals(true, this.acop.on());
			assertEquals(Fridge.FAKE_CURRENT_FREEZER_TEMPERATURE,
						 this.acicop.getCurrentFreezerTemperature());
			this.acicop.setTargetFreezerTemperature(-20.0);
			assertEquals(-20.0, this.acicop.getTargetFreezerTemperature());
			this.acicop.startCooling();
			assertEquals(true, this.acicop.cooling());
			this.acicop.stopCooling();
			assertEquals(false, this.acicop.cooling());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...testInternalControlForFreezer() done.\n");
	}


	protected void		testExternalControl()
	{
		this.traceMessage("testExternalControl()...\n");
		try {
			assertEquals(Fridge.MAX_POWER_LEVEL,
						 this.acecop.getMaxPowerLevel());
			this.acecop.setCurrentPowerLevel(Fridge.MAX_POWER_LEVEL/2.0);
			assertEquals(Fridge.MAX_POWER_LEVEL/2.0,
						 this.acecop.getCurrentPowerLevel());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testExternalControl() done.\n");
	}

	protected void		runAllTests()
	{
		this.testSwitchOnSwitchOff();
		this.testOn();
		this.testTargetCoolerTemperature();
		this.testCurrentCoolerTemperature();
		this.testTargetFreezerTemperature();
		this.testCurrentFreezerTemperature();
		this.testPowerLevel();
		this.testInternalControlForCooler();
		this.testInternalControlForFreezer();
		this.testExternalControl();
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
			this.doPortConnection(
					this.acop.getPortURI(),
					this.FridgeUserInboundPortURI,
					FridgeUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.acicop.getPortURI(),
					FridgeInternalControlInboundPortURI,
					FridgeInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.acecop.getPortURI(),
					FridgeExternalControlInboundPortURI,
					FridgeExternalControlConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.isUnitTest) {
			this.runAllTests();
		} else {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			System.out.println("Fridge tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
										CVMIntegrationTest.TEST_CLOCK_URI);
			System.out.println("Fridge tester waits until start");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();

			Instant FridgeSwitchOn = Instant.parse("2023-09-20T15:00:02.00Z");
			Instant FridgeSwitchOff = Instant.parse("2023-09-20T15:00:08.00Z");
			ac.waitUntilStart();
			System.out.println("Fridge tester schedules switch on and off");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(FridgeSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(FridgeSwitchOff);

			// This is to avoid mixing the 'this' of the task object with the 'this'
			// representing the component object in the code of the next methods run
			AbstractComponent o = this;

			// schedule the switch on Fridge
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Fridge switches on.\n");
								acop.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			// schedule the switch off Fridge
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Fridge switches off.\n");
								acop.switchOff();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.acop.getPortURI());
		this.doPortDisconnection(this.acicop.getPortURI());
		this.doPortDisconnection(this.acecop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.acop.unpublishPort();
			this.acicop.unpublishPort();
			this.acecop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
