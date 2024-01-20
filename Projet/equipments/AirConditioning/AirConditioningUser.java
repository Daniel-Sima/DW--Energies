package equipments.AirConditioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.connections.AirConditioningActuatorOutboundPort;
import equipments.AirConditioning.connections.AirConditioningExternalControlConnector;
import equipments.AirConditioning.connections.AirConditioningExternalControlOutboundPort;
import equipments.AirConditioning.connections.AirConditioningInternalControlConnector;
import equipments.AirConditioning.connections.AirConditioningInternalControlOutboundPort;
import equipments.AirConditioning.connections.AirConditioningSensorDataConnector;
import equipments.AirConditioning.connections.AirConditioningSensorDataOutboundPort;
import equipments.AirConditioning.connections.AirConditioningUserConnector;
import equipments.AirConditioning.connections.AirConditioningUserOutboundPort;
import global.CVMGlobalTest;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI;
import fr.sorbonne_u.exceptions.PreconditionException;
import utils.ExecutionType;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@RequiredInterfaces(required={AirConditioningUserCI.class,
							  AirConditioningInternalControlCI.class,
							  AirConditioningExternalControlCI.class,
							  ClocksServerCI.class,
							  AirConditioningSensorDataCI.AirConditioningSensorRequiredPullCI.class,
							  AirConditioningActuatorCI.class})
@OfferedInterfaces(offered={DataRequiredCI.PushCI.class})
public class 	AirConditioningUser
extends	 	AbstractComponent
implements 	AirConditioningPushImplementationI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	// --- URI of ports --- 
	/** URI of the user component interface inbound port.					*/
	protected String			airConditioningUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			airConditioningInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			airConditioningExternalControlInboundPortURI;
	/** URI of the airConditioning sensor inbound port.								*/
	protected String			airConditioningSensorInboundPortURI;
	/** URI of the airConditioning actuator inbound port.							*/
	protected String			airConditioningActuatorInboundPortURI;

	// --- Ports ---
	/** user component interface inbound port.								*/
	protected AirConditioningUserOutboundPort			acop;
	/** internal control component interface inbound port.					*/
	protected AirConditioningInternalControlOutboundPort	acicop;
	/** external control component interface inbound port.					*/
	protected AirConditioningExternalControlOutboundPort	acecop;
	/** a sensor outbound port to show a well structured sensor interface.	*/
	protected AirConditioningSensorDataOutboundPort		acsobp;
	/** an actuator outbound port to show a well structured actuator
	 *  interface.															*/
	protected AirConditioningActuatorOutboundPort		acaobp;
	
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

	protected	AirConditioningUser (
			String airConditioningUserInboundPortURI,
			String airConditioningInternalControlInboundPortURI,
			String airConditioningExternalControlInboundPortURI,
			String airConditioningSensorInboundPortURI,
			String airConditioningActuatorInboundPortURI,
			ExecutionType currentExecutionType,
			String clockURI) throws Exception
	{
		super(1,1);
		
		this.currentExecutionType = currentExecutionType;
		this.clockURI = clockURI;
		
		this.initialise(airConditioningUserInboundPortURI,
				airConditioningInternalControlInboundPortURI,
				airConditioningExternalControlInboundPortURI,
				airConditioningSensorInboundPortURI,
				airConditioningActuatorInboundPortURI);
	}
	
	private void 	initialise(
			String airConditioningUserInboundPortURI,
			String airConditioningInternalControlInboundPortURI,
			String airConditioningExternalControlInboundPortURI,
			String airConditioningSensorInboundPortURI,
			String airConditioningActuatorInboundPortURI) 
	throws Exception
	{
		// ---	 ASSERTS	---
		assert	airConditioningUserInboundPortURI != null &&
				!airConditioningUserInboundPortURI.isEmpty() :
		new PreconditionException(
		"airConditioningUserInboundPortURI != null && "
		+ "!airConditioningUserInboundPortURI.isEmpty()");
		assert	airConditioningInternalControlInboundPortURI != null &&
				!airConditioningInternalControlInboundPortURI.isEmpty() :
		new PreconditionException(
		"airConditioningInternalControlInboundPortURI != null && "
		+ "!airConditioningInternalControlInboundPortURI.isEmpty()");
		assert	airConditioningExternalControlInboundPortURI != null &&
				!airConditioningExternalControlInboundPortURI.isEmpty() :
		new PreconditionException(
		"airConditioningExternalControlInboundPortURI != null && "
		+ "!airConditioningExternalControlInboundPortURI.isEmpty()");
		assert	airConditioningSensorInboundPortURI != null &&
						!airConditioningSensorInboundPortURI.isEmpty() :
		new PreconditionException(
		"airConditioningSensorInboundPortURI != null &&"
		+ "!airConditioningSensorInboundPortURI.isEmpty()");
		assert	airConditioningActuatorInboundPortURI != null &&
					!airConditioningActuatorInboundPortURI.isEmpty() :
		new PreconditionException(
		"airConditioningActuatorInboundPortURI != null && "
		+ "!airConditioningActuatorInboundPortURI.isEmpty()");

		
		//	---		Publish Ports	---
		
		this.airConditioningUserInboundPortURI = airConditioningUserInboundPortURI;
		this.acop = new AirConditioningUserOutboundPort(this);
		this.acop.publishPort();

		this.airConditioningInternalControlInboundPortURI =
									airConditioningInternalControlInboundPortURI;
		this.airConditioningExternalControlInboundPortURI =
				airConditioningExternalControlInboundPortURI;
		this.airConditioningSensorInboundPortURI = airConditioningSensorInboundPortURI;
		this.airConditioningActuatorInboundPortURI = airConditioningActuatorInboundPortURI;

		if (this.currentExecutionType.isUnitTest()) {
			this.acicop = new AirConditioningInternalControlOutboundPort(this);
			this.acicop.publishPort();
			this.acecop = new AirConditioningExternalControlOutboundPort(this);
			this.acecop.publishPort();
			this.acsobp = new AirConditioningSensorDataOutboundPort(this);
			this.acsobp.publishPort();
			this.acaobp = new AirConditioningActuatorOutboundPort(this);
			this.acaobp.publishPort();
		}

		this.tracer.get().setTitle("airConditioning user component");
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
			this.acop.switchOff();
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
	
	protected void		testPullSensors()
	{
		this.traceMessage("testPullSensors()...\n");
		try {
			this.traceMessage(this.acsobp.targetTemperaturePullSensor() + ".\n");
			this.acop.switchOn();
			this.traceMessage(this.acsobp.currentTemperaturePullSensor() + ".\n");
			this.traceMessage(this.acsobp.coolingPullSensor() + ".\n");
			this.acop.switchOff();
		} catch (Exception e) {
			this.traceMessage("...KO.\n" + e);
			assertTrue(false);
		}
	}

	protected void		testActuators()
	{
		this.traceMessage("testInternalControl()...\n");
		try {
			this.acop.switchOn();
			assertEquals(true, this.acop.on());
			this.acaobp.startCooling();
			assertEquals(true, this.acicop.cooling());
			this.acaobp.stopCooling();
			assertEquals(false, this.acicop.cooling());
			this.acop.switchOff();
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
	public synchronized void	start() 
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.acop.getPortURI(),
					this.airConditioningUserInboundPortURI,
					AirConditioningUserConnector.class.getCanonicalName());
			if (this.currentExecutionType.isUnitTest()) {
				this.doPortConnection(
						this.acicop.getPortURI(),
						airConditioningInternalControlInboundPortURI,
						AirConditioningInternalControlConnector.class.getCanonicalName());
				this.doPortConnection(
						this.acecop.getPortURI(),
						airConditioningExternalControlInboundPortURI,
						AirConditioningExternalControlConnector.class.getCanonicalName());
				this.doPortConnection(
						this.acsobp.getPortURI(),
						this.airConditioningSensorInboundPortURI,
						AirConditioningSensorDataConnector.class.getCanonicalName());
				this.doPortConnection(
						this.acaobp.getPortURI(),
						this.airConditioningActuatorInboundPortURI,
						AirConditioningActuatorCI.class.getCanonicalName());
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
		this.logMessage("Air conditioning tester gets the clock.");
		this.clock =
				this.clocksServerOutboundPort.getClock(this.clockURI);
		Instant startInstant = this.clock.getStartInstant();
		this.doPortDisconnection(
						this.clocksServerOutboundPort.getPortURI());
		this.clocksServerOutboundPort.unpublishPort();
		this.logMessage("Air conditioning user waits until start.");
		this.clock.waitUntilStart();

		// This is to avoid mixing the 'this' of the task object with the
		// 'this' representing the component object in the code of the next
		// methods run
		AirConditioningUser act = this;

		if (this.currentExecutionType.isUnitTest()) {
			Instant startTests = startInstant.plusSeconds(1L);
			long delayToTestsStart =
								this.clock.nanoDelayUntilInstant(startTests);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								act.logMessage("Air conditioning tester starts the tests.");
								act.runAllTests();
								act.logMessage("Air conditioning tester tests end.");
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
			
			AirConditioningUserOutboundPort o = this.acop;
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								act.traceMessage(
									"User switches the air conditioning on\n.");
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
								act.traceMessage(
									"User switches the air conditioning off\n.");
								o.switchOff();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		} else if (this.currentExecutionType.isSIL()) {
			// switch on after one 0.5 hour
			Instant switchOnInstant = startInstant.plusSeconds(1800L);
			long delayToSwitchOn =
					this.clock.nanoDelayUntilInstant(switchOnInstant);
			// switch off one hour before the end of the simulation (in
			// simulated time)
			double switchOffInSimulatedTime =
					CVMGlobalTest.SIMULATION_DURATION - 1.0;
			long delayInSeconds = (long)(switchOffInSimulatedTime * 3600.0);
			Instant switchOffInstant = startInstant.plusSeconds(delayInSeconds);

			AirConditioningUserOutboundPort o = this.acop;
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								act.traceMessage(
									"User switches the air conditioning on\n.");
								o.switchOn();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);
			
			// long delayToSwitchOff =
			// 		this.clock.nanoDelayUntilInstant(switchOffInstant);
			// this.scheduleTaskOnComponent(
			// 		new AbstractComponent.AbstractTask() {
			// 			@Override
			// 			public void run() {
			// 				try {
			// 					act.traceMessage(
			// 						"User switches the air conditioning off\n.");
			// 					o.switchOff();
			// 				} catch (Exception e) {
			// 					e.printStackTrace();
			// 				}
			// 			}
			// 		}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.logMessage("Air conditioning user ends.");
		this.doPortDisconnection(this.acop.getPortURI());
		if (this.currentExecutionType.isUnitTest()) {
			this.doPortDisconnection(this.acicop.getPortURI());
			this.doPortDisconnection(this.acecop.getPortURI());
			this.doPortDisconnection(this.acsobp.getPortURI());
			this.doPortDisconnection(this.acaobp.getPortURI());
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
			this.acop.unpublishPort();
			if (this.currentExecutionType.isUnitTest()) {
				this.acicop.unpublishPort();
				this.acecop.unpublishPort();
				this.acsobp.unpublishPort();
				this.acaobp.unpublishPort();
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
	 * @see equipments.AirConditioning.AirConditioningPushImplementationI#receiveDataFromAirConditioning(fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI)
	 */
	@Override
	public void			receiveDataFromAirConditioning(DataI sd)
	{
		this.logMessage("AirConditioningUser receives " + sd.toString());
	}

}
