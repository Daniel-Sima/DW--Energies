package equipments.AirConditioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import equipments.AirConditioning.connections.AirConditioningExternalControlConnector;
import equipments.AirConditioning.connections.AirConditioningExternalControlOutboundPort;
import equipments.AirConditioning.connections.AirConditioningInternalControlConnector;
import equipments.AirConditioning.connections.AirConditioningInternalControlOutboundPort;
import equipments.AirConditioning.connections.AirConditioningUserConnector;
import equipments.AirConditioning.connections.AirConditioningUserOutboundPort;

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
 * The class <code>AirConditioningTester</code> implements a component performing 
 * tests for the class <code>AirConditioning</code> as a BCM component.
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
@RequiredInterfaces(required={AirConditioningUserCI.class,
							  AirConditioningInternalControlCI.class,
							  AirConditioningExternalControlCI.class,
							  ClocksServerCI.class})
public class			AirConditioningTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean		isUnitTest;
	/** URI of the user component interface inbound port.					*/
	protected String			AirConditioningUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			AirConditioningInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			AirConditioningExternalControlInboundPortURI;

	/** user component interface inbound port.								*/
	protected AirConditioningUserOutboundPort			acop;
	/** internal control component interface inbound port.					*/
	protected AirConditioningInternalControlOutboundPort	acicop;
	/** external control component interface inbound port.					*/
	protected AirConditioningExternalControlOutboundPort	acecop;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a AirConditioning test component.
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
	protected			AirConditioningTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 AirConditioning.USER_INBOUND_PORT_URI,
			 AirConditioning.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 AirConditioning.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a AirConditioning test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param AirConditioningUserInboundPortURI				URI of the user component interface inbound port.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioningTester(
		boolean isUnitTest,
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(AirConditioningUserInboundPortURI,
				AirConditioningInternalControlInboundPortURI,
				AirConditioningExternalControlInboundPortURI);
	}

	/**
	 * create a AirConditioning test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param isUnitTest							true if the component must perform unit tests, otherwise it executes integration tests actions.
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param AirConditioningUserInboundPortURI				URI of the user component interface inbound port.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioningTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(AirConditioningUserInboundPortURI,
						AirConditioningInternalControlInboundPortURI,
						AirConditioningExternalControlInboundPortURI);
	}

	/**
	 * initialise a AirConditioning test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param AirConditioningUserInboundPortURI				URI of the user component interface inbound port.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		this.AirConditioningUserInboundPortURI = AirConditioningUserInboundPortURI;
		this.acop = new AirConditioningUserOutboundPort(this);
		this.acop.publishPort();
		this.AirConditioningInternalControlInboundPortURI =
									AirConditioningInternalControlInboundPortURI;
		this.acicop = new AirConditioningInternalControlOutboundPort(this);
		this.acicop.publishPort();
		this.AirConditioningExternalControlInboundPortURI =
									AirConditioningExternalControlInboundPortURI;
		this.acecop = new AirConditioningExternalControlOutboundPort(this);
		this.acecop.publishPort();

		this.tracer.get().setTitle("AirConditioning tester component");
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

	protected void		testTargetTemperature()
	{
		this.traceMessage("testTargetTemperature()...\n");
		try {
			this.acop.setTargetTemperature(10.0);
			assertEquals(10.0, this.acop.getTargetTemperature());
			this.acop.setTargetTemperature(AirConditioning.STANDARD_TARGET_TEMPERATURE);
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testTargetTemperature() done.\n");

	}

	protected void		testCurrentTemperature()
	{
		this.traceMessage("testCurrentTemperature()...\n");
		try {
			this.acop.switchOn();
			assertEquals(AirConditioning.FAKE_CURRENT_TEMPERATURE,
						 this.acop.getCurrentTemperature());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testCurrentTemperature() done.\n");
	}

	protected void		testPowerLevel()
	{
		this.traceMessage("testPowerLevel()...\n");
		try {
			assertEquals(AirConditioning.MAX_POWER_LEVEL,
						 this.acop.getMaxPowerLevel());
			this.acop.switchOn();
			this.acop.setCurrentPowerLevel(AirConditioning.MAX_POWER_LEVEL/2.0);
			assertEquals(AirConditioning.MAX_POWER_LEVEL/2.0,
						 this.acop.getCurrentPowerLevel());
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testPowerLevel() done.\n");
	}

	protected void		testInternalControl()
	{
		this.traceMessage("testInternalControl()...\n");
		try {
			assertEquals(AirConditioning.STANDARD_TARGET_TEMPERATURE,
						 this.acicop.getTargetTemperature());
			this.acop.switchOn();
			assertEquals(true, this.acop.on());
			assertEquals(AirConditioning.FAKE_CURRENT_TEMPERATURE,
						 this.acicop.getCurrentTemperature());
			this.acicop.startCooling();
			assertEquals(true, this.acicop.cooling());
			this.acicop.stopCooling();
			assertEquals(false, this.acicop.cooling());
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...testInternalControl() done.\n");
	}

	protected void		testExternalControl()
	{
		this.traceMessage("testExternalControl()...\n");
		try {
			assertEquals(AirConditioning.MAX_POWER_LEVEL,
						 this.acecop.getMaxPowerLevel());
			this.acecop.setCurrentPowerLevel(AirConditioning.MAX_POWER_LEVEL/2.0);
			assertEquals(AirConditioning.MAX_POWER_LEVEL/2.0,
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
		this.testTargetTemperature();
		this.testCurrentTemperature();
		this.testPowerLevel();
		this.testInternalControl();
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
					this.AirConditioningUserInboundPortURI,
					AirConditioningUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.acicop.getPortURI(),
					AirConditioningInternalControlInboundPortURI,
					AirConditioningInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.acecop.getPortURI(),
					AirConditioningExternalControlInboundPortURI,
					AirConditioningExternalControlConnector.class.getCanonicalName());
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
			System.out.println("AirConditioning tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
										CVMIntegrationTest.TEST_CLOCK_URI);
			System.out.println("AirConditioning tester waits until start");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();

			Instant AirConditioningSwitchOn = Instant.parse("2023-09-20T15:00:02.00Z");
			Instant AirConditioningSwitchOff = Instant.parse("2023-09-20T15:00:08.00Z");
			ac.waitUntilStart();
			System.out.println("AirConditioning tester schedules switch on and off");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(AirConditioningSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(AirConditioningSwitchOff);

			// This is to avoid mixing the 'this' of the task object with the 'this'
			// representing the component object in the code of the next methods run
			AbstractComponent o = this;

			// schedule the switch on AirConditioning
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("AirConditioning switches on.\n");
								acop.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			// schedule the switch off AirConditioning
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("AirConditioning switches off.\n");
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
