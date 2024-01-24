package equipments.HEM;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import equipments.meter.ElectricMeter;
import equipments.meter.ElectricMeterCI;
import equipments.meter.ElectricMeterConnector;
import equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import global.CVMGlobalTest;
import utils.ExecutionType;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-16</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class, ClocksServerCI.class})
public class HEM 
extends AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort electricMeterOutboundPort;
	/** port to connect to the Air Conditioning.							*/
	//	protected AdjustableOutboundPort adjustableOutboundPortAirConditioning; 
	/** port to connect the Fridge 											*/
	//	protected AdjustableOutboundPort adjustableOutboundPortFridge;

	/** period of the HEM control loop.										*/
	protected final long PERIOD_IN_SECONDS = 60L;

	// Execution/Simulation

	/** port to connect to the clocks server.								*/
	protected final ExecutionType currentExecutionType;
	/** current type of execution.											*/
	protected ClocksServerOutboundPort clocksServerOutboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected HEM(ExecutionType currentExecutionType) {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.currentExecutionType = currentExecutionType;

		this.tracer.get().setTitle("Home Energy Manager component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * perform once the control and then schedule another task to continue,
	 * unless the end instant has been reached; following this approach, the
	 * decisions to be made by the HEM could be introduced in this method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * pre	{@code end != null}
	 * pre	{@code ac != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	the instant at which the current execution of the control must be scheduled.
	 * @param end		the instant at which the control loop must stop.
	 * @param ac		the accelerated clock used as time reference to interpret the instants.
	 */
	protected void loop(Instant current, Instant end, AcceleratedClock ac) {
		// For each action, compute the waiting time for this action
		// using the above instant and the clock, and then schedule the
		// task that will perform the action at the appropriate time.
		long delayInNanos = ac.nanoDelayUntilInstant(current);
		Instant next = current.plusSeconds(PERIOD_IN_SECONDS);
		if (next.compareTo(end) < 0) {
			this.scheduleTask(
					o -> {
						try	{
							o.traceMessage(
									"Electric meter current consumption: " +
											electricMeterOutboundPort.getCurrentConsumption() + "\n");
//							o.traceMessage(
//									"Electric meter current production: " +
//											electricMeterOutboundPort.getCurrentProduction() + "\n");
							loop(next, end, ac);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}, delayInNanos, TimeUnit.NANOSECONDS);
		}
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
			this.electricMeterOutboundPort = new ElectricMeterOutboundPort(this);
			this.electricMeterOutboundPort.publishPort();
			this.doPortConnection(
					this.electricMeterOutboundPort.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

			//			this.adjustableOutboundPortAirConditioning = new AdjustableOutboundPort(this);
			//			this.adjustableOutboundPortAirConditioning.publishPort();
			//			this.doPortConnection(
			//					this.adjustableOutboundPortAirConditioning.getPortURI(),
			//					AirConditioning.EXTERNAL_CONTROL_INBOUND_PORT_URI,
			//					AirConditioningConnector.class.getCanonicalName());
			//
			//			this.adjustableOutboundPortFridge = new AdjustableOutboundPort(this);
			//			this.adjustableOutboundPortFridge.publishPort();
			//			this.doPortConnection(
			//					this.adjustableOutboundPortFridge.getPortURI(),
			//					Fridge.EXTERNAL_CONTROL_INBOUND_PORT_URI,
			//					FridgeConnector.class.getCanonicalName());
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
		// First, get the clock and wait until the start time that it specifies.
		AcceleratedClock ac = null;

		if (this.currentExecutionType.isIntegrationTest() ||
				this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

			System.out.println("HEM creates the clock");
			ac = this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("HEM waits until start time.");
			ac.waitUntilStart();
		}

		this.logMessage("HEM starts.");

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, execute the control loop until the end of
			// simulation time.
			long delayUntilEndInSeconds =
					(long) (TimeUnit.HOURS.toSeconds(1)
							* CVMGlobalTest.SIMULATION_DURATION);
			Instant startInstant = ac.getStartInstant();
			Instant endInstant =
					startInstant.plusSeconds(delayUntilEndInSeconds);
			// delay until the first call to the electric meter
			long delayInSecondsOfSimulatedTime = 600L;
			Instant first =
					startInstant.plusSeconds(delayInSecondsOfSimulatedTime);

			this.logMessage("HEM schedules the SIL integration test.");
			this.loop(first, endInstant, ac);
		} else if (this.currentExecutionType.isIntegrationTest()) {
			// Integration test for the meter and Air Conditionning and Fridge
			Instant meterTest = ac.getStartInstant().plusSeconds(60L);
			long delay = ac.nanoDelayUntilInstant(meterTest);
			this.logMessage("HEM schedules the meter integration test in "
					+ delay + " " + TimeUnit.NANOSECONDS);

			// This is to avoid mixing the 'this' of the task object with the
			// 'this' representing the component object in the code of the next
			// methods run
			AbstractComponent o = this;

			// For the electric meter, simply perform two calls to test the
			// sensor methods.
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage(
										"Electric meter current consumption: " +
												electricMeterOutboundPort.getCurrentConsumption() + "\n");
								o.traceMessage(
										"Electric meter current production: " +
												electricMeterOutboundPort.getCurrentProduction() + "\n");
								o.traceMessage("HEM meter test ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);

			// Test for the Air Conditioning
			Instant airConditioningTestStart = ac.getStartInstant().plusSeconds(30L);
			delay = ac.nanoDelayUntilInstant(airConditioningTestStart);
			this.logMessage("HEM schedules the heater first call in "
					+ delay + " " + TimeUnit.NANOSECONDS);
			// schedule air conditioning methods tests
//			this.scheduleTaskOnComponent(
//					new AbstractComponent.AbstractTask() {
//						@Override
//						public void run() {
//							try {
//								o.traceMessage("------------- Air Conditioning -------------\n");
//								o.traceMessage("Air Conditioning maxMode index? " +
//										adjustableOutboundPortAirConditioning.maxMode() + "\n");
//								o.traceMessage("Air Conditioning current mode index? " +
//										adjustableOutboundPortAirConditioning.currentMode() + "\n");
//								o.traceMessage("Air Conditioning going down one mode? " +
//										adjustableOutboundPortAirConditioning.downMode() + "\n");
//								o.traceMessage("Air Conditioning current mode is? " +
//										adjustableOutboundPortAirConditioning.currentMode() + "\n");
//								o.traceMessage("Air Conditioning going up one mode? " +
//										adjustableOutboundPortAirConditioning.upMode() + "\n");
//								o.traceMessage("Air Conditioning current mode is? " +
//										adjustableOutboundPortAirConditioning.currentMode() + "\n");
//								o.traceMessage("Air Conditioning setting current mode? " +
//										adjustableOutboundPortAirConditioning.setMode(2) + "\n");
//								o.traceMessage("Air Conditioning current mode is? " +
//										adjustableOutboundPortAirConditioning.currentMode() + "\n");
//								o.traceMessage("Air Conditioning is suspended? " +
//										adjustableOutboundPortAirConditioning.suspended() + "\n");
//								o.traceMessage("Air Conditioning suspends? " +
//										adjustableOutboundPortAirConditioning.suspend() + "\n");
//								o.traceMessage("Air Conditioning is suspended? " +
//										adjustableOutboundPortAirConditioning.suspended() + "\n");
//								o.traceMessage("Air Conditioning emergency? " +
//										adjustableOutboundPortAirConditioning.emergency() + "\n");
//								Thread.sleep(1000);
//								o.traceMessage("Air Conditioning emergency? " +
//										adjustableOutboundPortAirConditioning.emergency() + "\n");
//								o.traceMessage("Air Conditioning resumes? " +
//										adjustableOutboundPortAirConditioning.resume() + "\n");
//								o.traceMessage("Air Conditioning is suspended? " +
//										adjustableOutboundPortAirConditioning.suspended() + "\n");
//								o.traceMessage("Air Conditioning current mode is? " +
//										adjustableOutboundPortAirConditioning.currentMode() + "\n");
//
//								o.traceMessage("------------- Fridge  -------------\n");
//								o.traceMessage("Fridge maxMode index? " +
//										adjustableOutboundPortFridge.maxMode() + "\n");
//								o.traceMessage("Fridge current mode index? " +
//										adjustableOutboundPortFridge.currentMode() + "\n");
//								o.traceMessage("Fridge going down one mode? " +
//										adjustableOutboundPortFridge.downMode() + "\n");
//								o.traceMessage("Fridge current mode is? " +
//										adjustableOutboundPortFridge.currentMode() + "\n");
//								o.traceMessage("Fridge going up one mode? " +
//										adjustableOutboundPortFridge.upMode() + "\n");
//								o.traceMessage("Fridge current mode is? " +
//										adjustableOutboundPortFridge.currentMode() + "\n");
//								o.traceMessage("Fridge setting current mode? " +
//										adjustableOutboundPortFridge.setMode(2) + "\n");
//								o.traceMessage("Fridge current mode is? " +
//										adjustableOutboundPortFridge.currentMode() + "\n");
//								o.traceMessage("Fridge is suspended? " +
//										adjustableOutboundPortFridge.suspended() + "\n");
//								o.traceMessage("Fridge suspends? " +
//										adjustableOutboundPortFridge.suspend() + "\n");
//								o.traceMessage("Fridge is suspended? " +
//										adjustableOutboundPortFridge.suspended() + "\n");
//								o.traceMessage("Fridge emergency? " +
//										adjustableOutboundPortFridge.emergency() + "\n");
//								Thread.sleep(1000);
//								o.traceMessage("Fridge emergency? " +
//										adjustableOutboundPortFridge.emergency() + "\n");
//								o.traceMessage("Fridge resumes? " +
//										adjustableOutboundPortFridge.resume() + "\n");
//								o.traceMessage("Fridge is suspended? " +
//										adjustableOutboundPortFridge.suspended() + "\n");
//								o.traceMessage("Fridge current mode is? " +
//										adjustableOutboundPortFridge.currentMode() + "\n");
//
//								System.out.println("HEM test ends");
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}, delay, TimeUnit.NANOSECONDS);
		}


	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
//		this.doPortDisconnection(this.adjustableOutboundPortAirConditioning.getPortURI());
//		this.doPortDisconnection(this.adjustableOutboundPortFridge.getPortURI());

		super.finalise();
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.electricMeterOutboundPort.unpublishPort();
//			this.adjustableOutboundPortAirConditioning.unpublishPort();
//			this.adjustableOutboundPortFridge.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/