package equipments.Lamp;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e1.CVMIntegrationTest;
import equipments.Lamp.LampImplementationI.LampMode;
import equipments.Lamp.LampImplementationI.LampState;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.ExecutionException;

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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required = {LampUserCI.class, ClocksServerCI.class})
public class			LampTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final boolean				isUnitTest;
	protected LampOutboundPort		lop;
	protected String					LampInboundPortURI;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			LampTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, Lamp.INBOUND_PORT_URI);
	}

	protected			LampTester(
		boolean isUnitTest,
		String LampInboundPortURI
		) throws Exception
	{
		super(1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(LampInboundPortURI);
	}

	protected			LampTester(
		boolean isUnitTest,
		String LampInboundPortURI,
		String reflectionInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(LampInboundPortURI);
	}

	protected void		initialise(
		String LampInboundPortURI
		) throws Exception
	{
		this.LampInboundPortURI = LampInboundPortURI;
		this.lop = new LampOutboundPort(this);
		this.lop.publishPort();

		this.tracer.get().setTitle("Lamp tester component");
		this.tracer.get().setRelativePosition(2, 1);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	public void	testGetState()
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

	public void	testGetMode()
	{
		this.logMessage("testGetMode()... ");
		try {
			assertEquals(LampMode.MODE_1, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void	testTurnOnOff()
	{
		this.logMessage("testTurnOnOff()... ");
		try {
			assertEquals(LampState.OFF, this.lop.getState());
			this.lop.turnOn();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MODE_1, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
//		try {
//			assertThrows(ExecutionException.class,
//						 () -> this.lop.turnOn());
//		} catch (Exception e) {
//			assertTrue(false);
//		}
		try {
			this.lop.turnOff();
			assertEquals(LampState.OFF, this.lop.getState());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message this.currentStat == LampState.ON!
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.turnOff());
		} catch (Exception e) {
			assertTrue(false);
		}
		this.logMessage("...done.");
	}

	public void	testIncreaseDecreaseMode()
	{
		this.logMessage("testIncreaseDecreaseMode()... ");
		try {
			this.lop.turnOn();
			assertEquals(LampState.ON, this.lop.getState());
			this.lop.increaseMode();
			assertEquals(LampMode.MODE_2, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.increaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MODE_3, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message this.currentMode != LampMode.MODE_3!
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.increaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message this.currentMode != LampMode.MODE_3!
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.increaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			this.lop.decreaseMode();
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MODE_2, this.lop.getMode());
			this.lop.decreaseMode();
			assertEquals(LampMode.MODE_1, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message getMode() != LampMode.MODE_1!
		try {
			assertThrows(ExecutionException.class,
						 () -> this.lop.decreaseMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		try {
			assertEquals(LampState.ON, this.lop.getState());
			assertEquals(LampMode.MODE_1, this.lop.getMode());
		} catch (Exception e) {
			assertTrue(false);
		}
		// PreconditionException raised with message getMode() != LampMode.MODE_1!
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

	protected void runAllTests() throws Exception
	{
		this.lop.printSeparator(" testGetState() ");
		this.testGetState();
		this.lop.printSeparator(" testGetMode() ");
		this.testGetMode(); 
		this.lop.printSeparator(" testTurnOnOff() ");
		this.testTurnOnOff();
		this.lop.printSeparator(" testIncreaseDecreaseMode() ");
		this.testIncreaseDecreaseMode();
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
							this.lop.getPortURI(),
							LampInboundPortURI,
							LampConnector.class.getCanonicalName());
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
		if (!this.isUnitTest) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			System.out.println("Lamp Tester gets the clock");
			AcceleratedClock ac =
					this.clocksServerOutboundPort.getClock(
										CVMIntegrationTest.TEST_CLOCK_URI);

			System.out.println("Lamp Tester waits until start");
			ac.waitUntilStart();
			System.out.println("Lamp Tester waits to perform tests");
			this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			Thread.sleep(3000);
		}
		this.runAllTests();
		System.out.println("Lamp Tester ends");
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
}
// -----------------------------------------------------------------------------
