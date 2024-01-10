package fr.sorbonne_u.components.hem2023e3.equipments.heater;

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
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterExternalControlConnector;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterInternalControlConnector;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterUserConnector;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterUserOutboundPort;
import fr.sorbonne_u.components.hem2023e3.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterActuatorConnector;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterActuatorOutboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterSensorDataConnector;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterSensorDataOutboundPort;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterTester</code> implements a component performing 
 * tests for the class <code>Heater</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In unit test runs, the component performs a series of tests on the heater
 * component. In integration test and SIL simulation runs, it simply switch
 * on and off the heater, leaving to the other components (the heater controller
 * and the HEM) the responsibility to test the heater component services and
 * internal behaviour.
 * </p>
 *
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@RequiredInterfaces(required={HeaterUserCI.class,
							  HeaterInternalControlCI.class,
							  HeaterExternalControlCI.class,
							  ClocksServerCI.class,
							  HeaterSensorDataCI.HeaterSensorRequiredPullCI.class,
							  HeaterActuatorCI.class})
@OfferedInterfaces(offered={DataRequiredCI.PushCI.class})
public class			HeaterUser
extends		AbstractComponent
implements	HeaterPushImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the user component interface inbound port.					*/
	protected String			heaterUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			heaterInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			heaterExternalControlInboundPortURI;
	/** URI of the heater sensor inbound port.								*/
	protected String			heaterSensorInboundPortURI;
	/** URI of the heater actuator inbound port.							*/
	protected String			heaterActuatorInboundPortURI;

	/** user component interface inbound port.								*/
	protected HeaterUserOutboundPort			hop;
	/** internal control component interface inbound port.					*/
	protected HeaterInternalControlOutboundPort	hicop;
	/** external control component interface inbound port.					*/
	protected HeaterExternalControlOutboundPort	hecop;
	/** a sensor outbound port to show a well structured sensor interface.	*/
	protected HeaterSensorDataOutboundPort		hsobp;
	/** an actuator outbound port to show a well structured actuator
	 *  interface.															*/
	protected HeaterActuatorOutboundPort		haobp;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String				clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected AcceleratedClock			clock;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater user component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterSensorInboundPortURI != null && !heaterSensorInboundPortURI.isEmpty()}
	 * pre	{@code heaterActuatorInboundPortURI != null && !heaterActuatorInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the user component interface inbound port.
	 * @param heaterInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param heaterExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param heaterSensorInboundPortURI			URI of the inbound port to call the heater component sensors.
	 * @param heaterActuatorInboundPortURI			URI of the inbound port to call the heater component actuators.
	 * @param currentExecutionType					current execution type for the next run.
	 * @param clockURI								URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			HeaterUser(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI,
		String heaterSensorInboundPortURI,
		String heaterActuatorInboundPortURI,
		ExecutionType currentExecutionType,
		String clockURI
		) throws Exception
	{
		super(1, 1);

		this.currentExecutionType = currentExecutionType;
		this.clockURI = clockURI;

		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI,
						heaterSensorInboundPortURI,
						heaterActuatorInboundPortURI);
	}

	/**
	 * initialise a heater test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterSensorInboundPortURI != null && !heaterSensorInboundPortURI.isEmpty()}
	 * pre	{@code heaterActuatorInboundPortURI != null && !heaterActuatorInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the user component interface inbound port.
	 * @param heaterInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param heaterExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param heaterSensorInboundPortURI			URI of the inbound port to call the heater component sensors.
	 * @param heaterActuatorInboundPortURI			URI of the inbound port to call the heater component actuators.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI,
		String heaterSensorInboundPortURI,
		String heaterActuatorInboundPortURI
		) throws Exception
	{
		assert	heaterUserInboundPortURI != null &&
										!heaterUserInboundPortURI.isEmpty() :
				new PreconditionException(
						"heaterUserInboundPortURI != null && "
						+ "!heaterUserInboundPortURI.isEmpty()");
		assert	heaterInternalControlInboundPortURI != null &&
								!heaterInternalControlInboundPortURI.isEmpty() :
		new PreconditionException(
				"heaterInternalControlInboundPortURI != null && "
				+ "!heaterInternalControlInboundPortURI.isEmpty()");
		assert	heaterExternalControlInboundPortURI != null &&
								!heaterExternalControlInboundPortURI.isEmpty() :
		new PreconditionException(
				"heaterExternalControlInboundPortURI != null && "
				+ "!heaterExternalControlInboundPortURI.isEmpty()");
		assert	heaterSensorInboundPortURI != null &&
										!heaterSensorInboundPortURI.isEmpty() :
				new PreconditionException(
						"heaterSensorInboundPortURI != null &&"
						+ "!heaterSensorInboundPortURI.isEmpty()");
		assert	heaterActuatorInboundPortURI != null &&
									!heaterActuatorInboundPortURI.isEmpty() :
				new PreconditionException(
						"heaterActuatorInboundPortURI != null && "
						+ "!heaterActuatorInboundPortURI.isEmpty()");

		this.heaterUserInboundPortURI = heaterUserInboundPortURI;
		this.hop = new HeaterUserOutboundPort(this);
		this.hop.publishPort();

		this.heaterInternalControlInboundPortURI =
									heaterInternalControlInboundPortURI;
		this.heaterExternalControlInboundPortURI =
				heaterExternalControlInboundPortURI;
		this.heaterSensorInboundPortURI = heaterSensorInboundPortURI;
		this.heaterActuatorInboundPortURI = heaterActuatorInboundPortURI;

		if (this.currentExecutionType.isUnitTest()) {
			this.hicop = new HeaterInternalControlOutboundPort(this);
			this.hicop.publishPort();
			this.hecop = new HeaterExternalControlOutboundPort(this);
			this.hecop.publishPort();
			this.hsobp = new HeaterSensorDataOutboundPort(this);
			this.hsobp.publishPort();
			this.haobp = new HeaterActuatorOutboundPort(this);
			this.haobp.publishPort();
		}

		this.tracer.get().setTitle("Heater user component");
		this.tracer.get().setRelativePosition(3, 2);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testSwitchOnSwitchOff()
	{
		this.traceMessage("testSwitchOnSwitchOff...\n");
		try {
			this.hop.switchOn();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		try {
			this.hop.switchOff();
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
			assertEquals(false, this.hop.on());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		try {
			this.hop.switchOn();
			assertEquals(true, this.hop.on());
			this.hop.switchOff();
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
			this.hop.setTargetTemperature(10.0);
			assertEquals(10.0, this.hop.getTargetTemperature());
			this.hop.setTargetTemperature(Heater.STANDARD_TARGET_TEMPERATURE);
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
			this.hop.switchOn();
			assertEquals(Heater.FAKE_CURRENT_TEMPERATURE,
						 this.hop.getCurrentTemperature());
			this.hop.switchOff();
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
			assertEquals(Heater.MAX_POWER_LEVEL,
						 this.hop.getMaxPowerLevel());
			this.hop.switchOn();
			this.hop.setCurrentPowerLevel(Heater.MAX_POWER_LEVEL/2.0);
			assertEquals(Heater.MAX_POWER_LEVEL/2.0,
						 this.hop.getCurrentPowerLevel());
			this.hop.switchOff();
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
			assertEquals(Heater.STANDARD_TARGET_TEMPERATURE,
						 this.hicop.getTargetTemperature());
			this.hop.switchOn();
			assertEquals(true, this.hop.on());
			assertEquals(Heater.FAKE_CURRENT_TEMPERATURE,
						 this.hicop.getCurrentTemperature());
			this.hicop.startHeating();
			assertEquals(true, this.hicop.heating());
			this.hicop.stopHeating();
			assertEquals(false, this.hicop.heating());
			this.hop.switchOff();
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
			assertEquals(Heater.MAX_POWER_LEVEL,
						 this.hecop.getMaxPowerLevel());
			this.hecop.setCurrentPowerLevel(Heater.MAX_POWER_LEVEL/2.0);
			assertEquals(Heater.MAX_POWER_LEVEL/2.0,
						 this.hecop.getCurrentPowerLevel());
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
		this.traceMessage("...testExternalControl() done.\n");
	}

	protected void		testPullSensors()
	{
		this.traceMessage("testPullSensors()...\n");
		try {
			this.traceMessage(this.hsobp.targetTemperaturePullSensor() + ".\n");
			this.hop.switchOn();
			this.traceMessage(this.hsobp.currentTemperaturePullSensor() + ".\n");
			this.traceMessage(this.hsobp.heatingPullSensor() + ".\n");
			this.hop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
	}

	protected void		testActuators()
	{
		this.traceMessage("testInternalControl()...\n");
		try {
			this.hop.switchOn();
			assertEquals(true, this.hop.on());
			this.haobp.startHeating();
			assertEquals(true, this.hicop.heating());
			this.haobp.stopHeating();
			assertEquals(false, this.hicop.heating());
			this.hop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n");
			assertTrue(false);
		}
		this.traceMessage("...testInternalControl() done.\n");
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
		this.testPullSensors();
		this.testActuators();
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
					this.hop.getPortURI(),
					this.heaterUserInboundPortURI,
					HeaterUserConnector.class.getCanonicalName());
			if (this.currentExecutionType.isUnitTest()) {
				this.doPortConnection(
						this.hicop.getPortURI(),
						heaterInternalControlInboundPortURI,
						HeaterInternalControlConnector.class.getCanonicalName());
				this.doPortConnection(
						this.hecop.getPortURI(),
						heaterExternalControlInboundPortURI,
						HeaterExternalControlConnector.class.getCanonicalName());
				this.doPortConnection(
						this.hsobp.getPortURI(),
						this.heaterSensorInboundPortURI,
						HeaterSensorDataConnector.class.getCanonicalName());
				this.doPortConnection(
						this.haobp.getPortURI(),
						this.heaterActuatorInboundPortURI,
						HeaterActuatorConnector.class.getCanonicalName());
			}
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
		this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
		this.clocksServerOutboundPort.publishPort();
		this.doPortConnection(
				this.clocksServerOutboundPort.getPortURI(),
				ClocksServer.STANDARD_INBOUNDPORT_URI,
				ClocksServerConnector.class.getCanonicalName());
		this.logMessage("Heater tester gets the clock.");
		this.clock =
				this.clocksServerOutboundPort.getClock(this.clockURI);
		Instant startInstant = this.clock.getStartInstant();
		this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
		this.clocksServerOutboundPort.unpublishPort();
		this.logMessage("Heater user waits until start.");
		this.clock.waitUntilStart();

		// This is to avoid mixing the 'this' of the task object with the
		// 'this' representing the component object in the code of the next
		// methods run
		HeaterUser ht = this;

		if (this.currentExecutionType.isUnitTest()) {
			Instant startTests = startInstant.plusSeconds(1L);
			long delayToTestsStart =
								this.clock.nanoDelayUntilInstant(startTests);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								ht.logMessage("Heater tester starts the tests.");
								ht.runAllTests();
								ht.logMessage("Heater tester tests end.");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToTestsStart, TimeUnit.NANOSECONDS);
		} else if (this.currentExecutionType.isIntegrationTest()) {
			Instant switchOnInstant = startInstant.plusSeconds(60L);
			Instant switchOffInstant = startInstant.plusSeconds(600L);
			long delayToSwitchOn =
					this.clock.nanoDelayUntilInstant(switchOnInstant);
			HeaterUserOutboundPort o = this.hop;
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								ht.traceMessage(
									"Heater user switches the heater on\n.");
								o.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			long delayToSwitchOff =
					this.clock.nanoDelayUntilInstant(switchOffInstant);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								ht.traceMessage(
									"Heater user switches the heater off\n.");
								o.switchOff();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		} else if (this.currentExecutionType.isSIL()) {
			// switch on after one hour
			Instant switchOnInstant = startInstant.plusSeconds(3600L);
			// switch off one hour before the end of the simulation (in
			// simulated time)
			double switchOffInSimulatedTime =
					CVMGlobalTest.SIMULATION_DURATION - 1.0;
			long delayInSeconds = (long)(switchOffInSimulatedTime * 3600.0);
			Instant switchOffInstant = startInstant.plusSeconds(delayInSeconds);
			long delayToSwitchOn =
					this.clock.nanoDelayUntilInstant(switchOnInstant);
			HeaterUserOutboundPort o = this.hop;
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								ht.traceMessage(
									"Heater user switches the heater on\n.");
								o.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			long delayToSwitchOff =
					this.clock.nanoDelayUntilInstant(switchOffInstant);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								ht.traceMessage(
									"Heater user switches the heater off\n.");
								o.switchOff();
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
		this.logMessage("Heater user ends.");
		this.doPortDisconnection(this.hop.getPortURI());
		if (this.currentExecutionType.isUnitTest()) {
			this.doPortDisconnection(this.hicop.getPortURI());
			this.doPortDisconnection(this.hecop.getPortURI());
			this.doPortDisconnection(this.hsobp.getPortURI());
			this.doPortDisconnection(this.haobp.getPortURI());
		}

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hop.unpublishPort();
			if (this.currentExecutionType.isUnitTest()) {
				this.hicop.unpublishPort();
				this.hecop.unpublishPort();
				this.hsobp.unpublishPort();
				this.haobp.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterPushImplementationI#receiveDataFromHeater(fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI)
	 */
	@Override
	public void			receiveDataFromHeater(DataI sd)
	{
		this.logMessage("HeaterUser receives " + sd.toString());
	}
}
// -----------------------------------------------------------------------------
