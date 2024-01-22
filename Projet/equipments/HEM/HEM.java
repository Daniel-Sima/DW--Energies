package equipments.HEM;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.connections.AirConditioningSensorDataConnector;
import equipments.AirConditioning.connections.AirConditioningSensorDataOutboundPort;
import equipments.AirConditioning.measures.AirConditioningCompoundMeasure;
import equipments.AirConditioning.measures.AirConditioningSensorData;
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
import utils.Measure;
import utils.SensorData;

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
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
								ClocksServerCI.class})
public class 		HEM 
extends AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	/** URI of the sensor inbound port on the {@code ThermostatedAirConditiong}.	*/
	protected String								sensorIBP_URI;
	
	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort 	electricMeterOutboundPort;
	/** port to connect to the Air Conditioning.							*/
	protected AdjustableOutboundPort 		adjustableOutboundPortAirConditioning; 
	/** port to connect the Fridge 											*/
	protected AdjustableOutboundPort 		adjustableOutboundPortFridge;
	/** sensor data outbound port connected to the {@code AirConditioning}.	*/
	protected AirConditioningSensorDataOutboundPort			sensorOutboundPort;
	
	/** period of the HEM control loop.										*/
	protected final long					PERIOD_IN_SECONDS = 60L;


    /** Température minimale pour laquelle le HEM doit 
	 * 	suspendre AirConditioning.											*/
    private static final double SUSPEND_TEMP = 16.0;
	/** Température maximale pour laquelle le HEM doit
	 * 	resume AirConditioning.												*/
	private static final double RESUME_TEMP = 21.0;
	/** Consommation maximale pour laquelle le HEM doit 
	 *	suspendre AirConditioning.											*/
	private static final double CONSUMPTION_THRESHOLD = 600.0;

	/** Restart flag														*/
	private boolean restart = true;

	/** flag to print or not debug messages.								*/
	private static final boolean DEBUG = false;

	/** total consumption of the household.									*/
	private double totalConsumption = 0.0;
	
	// Execution / Simulation
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort 		clocksServerOutboundPort;
	/** current type of execution.											*/
	protected final ExecutionType			currentExecutionType;
	
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
	protected HEM(
			ExecutionType currentExecutionType) {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);
		
		this.currentExecutionType = currentExecutionType;
		this.sensorIBP_URI = AirConditioning.SENSOR_INBOUND_PORT_URI_HEM;

		try {
			this.sensorOutboundPort =
					new AirConditioningSensorDataOutboundPort(this);
			this.sensorOutboundPort.publishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	protected void		loop(Instant current, Instant end, AcceleratedClock ac)
	{
		// For each action, compute the waiting time for this action
		// using the above instant and the clock, and then schedule the
		// task that will perform the action at the appropriate time.
		long delayInNanos = ac.nanoDelayUntilInstant(current);
		Instant next = current.plusSeconds(PERIOD_IN_SECONDS);
		if (next.compareTo(end) < 0) {
			this.scheduleTask(
				o -> {
					try	{
						// Appeler la méthode de décision pour la consommation
						evaluateConsumptionAndTakeAction();
						o.traceMessage(
								"Electric meter current consumption: " +
								electricMeterOutboundPort.getCurrentConsumption()
														 .getMeasure()
														 .getData() + "\n");
						o.traceMessage(
								"Electric meter total consumption: " +
								totalConsumption + "\n");

						// Appeler la méthode de décision pour la température
						evaluateTemperatureAndTakeAction();

//						o.traceMessage(
//								"Electric meter current production: " +
//								electricMeterOutboundPort.getCurrentProduction() + "\n");
						loop(next, end, ac);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}, delayInNanos, TimeUnit.NANOSECONDS);
		}
	}

	private void evaluateTemperatureAndTakeAction() {
		try {
			@SuppressWarnings("unchecked")
			AirConditioningSensorData<AirConditioningCompoundMeasure> td =
						(AirConditioningSensorData<AirConditioningCompoundMeasure>)
										this.sensorOutboundPort.request();
			if(DEBUG) {
				this.traceMessage(td + "\n");	
			}

			double currentTemp = td.getMeasure().getCurrentTemperature();

			if(currentTemp < SUSPEND_TEMP) {
				adjustableOutboundPortAirConditioning.suspend();
			}
			else if (currentTemp > RESUME_TEMP) {
				adjustableOutboundPortAirConditioning.resume();
				restart = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void evaluateConsumptionAndTakeAction() {
        try {
            SensorData<Measure<Double>> currentConsumption = electricMeterOutboundPort.getCurrentConsumption();
            totalConsumption += currentConsumption.getMeasure().getData();
            if (totalConsumption > CONSUMPTION_THRESHOLD && restart) {
                // Le taux de consommation est trop élevé, suspendre le climatiseur
                adjustableOutboundPortAirConditioning.suspend();
                // Vous pouvez également notifier d'autres composants ou prendre d'autres actions nécessaires.
            }
        } catch (Exception e) {
            e.printStackTrace();
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

			this.adjustableOutboundPortAirConditioning = new AdjustableOutboundPort(this);
			this.adjustableOutboundPortAirConditioning.publishPort();
			this.doPortConnection(
					this.adjustableOutboundPortAirConditioning.getPortURI(),
					AirConditioning.EXTERNAL_CONTROL_INBOUND_PORT_URI,
					AirConditioningConnector.class.getCanonicalName());

			System.out.println("HEM sensor outbound port uri : " + this.sensorOutboundPort.getPortURI());
			this.doPortConnection(
					this.sensorOutboundPort.getPortURI(),
					sensorIBP_URI,
					AirConditioningSensorDataConnector.class.getCanonicalName());
			
			
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
		System.out.println("HEM execute");
		AcceleratedClock ac = null;
		if (this.currentExecutionType.isIntegrationTest() ||
											this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("HEM gets the clock");
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
			
		} 
		else if (this.currentExecutionType.isIntegrationTest()) {
			System.out.println("is integration test");
			// Integration test for the meter and the heater
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
//							o.traceMessage(
//									"Electric meter current production: " +
//									electricMeterOutboundPort.getCurrentProduction() + "\n");
							o.traceMessage("HEM meter test ends.\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);

			// For the heater, perform a series of call that will also test the
			// adjustable interface.
			Instant airConditioning1 = ac.getStartInstant().plusSeconds(30L);
			delay = ac.nanoDelayUntilInstant(airConditioning1);
			this.logMessage("HEM schedules the air conditioning first call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM air conditioning first call begins.\n");
								o.traceMessage("AirConditioning maxMode index? " +
										adjustableOutboundPortAirConditioning.maxMode() + "\n");
								o.traceMessage("AirConditioning current mode index? " +
										adjustableOutboundPortAirConditioning.currentMode() + "\n");
								o.traceMessage("AirConditioning going down one mode? " +
										adjustableOutboundPortAirConditioning.downMode() + "\n");
								o.traceMessage("AirConditioning current mode is? " +
										adjustableOutboundPortAirConditioning.currentMode() + "\n");
								o.traceMessage("AirConditioning going up one mode? " +
										adjustableOutboundPortAirConditioning.upMode() + "\n");
								o.traceMessage("AirConditioning current mode is? " +
										adjustableOutboundPortAirConditioning.currentMode() + "\n");
								o.traceMessage("AirConditioning setting current mode? " +
										adjustableOutboundPortAirConditioning.setMode(2) + "\n");
								o.traceMessage("AirConditioning current mode is? " +
										adjustableOutboundPortAirConditioning.currentMode() + "\n");
								o.traceMessage("AirConditioning is suspended? " +
										adjustableOutboundPortAirConditioning.suspended() + "\n");
								o.traceMessage("HEM air conditioning first call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);
			
			Instant airConditioning2 = ac.getStartInstant().plusSeconds(120L);
			delay = ac.nanoDelayUntilInstant(airConditioning2);
			this.logMessage("HEM schedules the air conditioning second call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM air conditioning second call begins.\n");
								o.traceMessage("AirConditioning suspends? " +
										adjustableOutboundPortAirConditioning.suspend() + "\n");
								o.traceMessage("AirConditioning is suspended? " +
										adjustableOutboundPortAirConditioning.suspended() + "\n");
								o.traceMessage("AirConditioning emergency? " +
										adjustableOutboundPortAirConditioning.emergency() + "\n");
								o.traceMessage("HEM air conditioning second call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);
			
			Instant airConditioning3 = ac.getStartInstant().plusSeconds(240L);
			delay = ac.nanoDelayUntilInstant(airConditioning3);
			this.logMessage("HEM schedules the air conditioning third call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM air conditioning third call begins.\n");
								o.traceMessage("AirConditioning emergency? " +
										adjustableOutboundPortAirConditioning.emergency() + "\n");
								o.traceMessage("AirConditioning resumes? " +
										adjustableOutboundPortAirConditioning.resume() + "\n");
								o.traceMessage("AirConditioning is suspended? " +
										adjustableOutboundPortAirConditioning.suspended() + "\n");
								o.traceMessage("AirConditioning current mode is? " +
										adjustableOutboundPortAirConditioning.currentMode() + "\n");
								o.traceMessage("HEM air conditioning third call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.logMessage("HEM ends.");
		this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
		this.doPortDisconnection(this.sensorOutboundPort.getPortURI());
		this.doPortDisconnection(this.adjustableOutboundPortAirConditioning.getPortURI());
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
			this.adjustableOutboundPortAirConditioning.unpublishPort();
			this.sensorOutboundPort.unpublishPort();
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