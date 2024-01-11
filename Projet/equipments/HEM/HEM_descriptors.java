package equipments.HEM;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.connections.AirConditioningUserOutboundPort;
import equipments.Fridge.Fridge;
import equipments.HEM.registration.RegistrationI;
import equipments.HEM.registration.RegistrationInboundPort;
import equipments.meter.ElectricMeter;
import equipments.meter.ElectricMeterCI;
import equipments.meter.ElectricMeterConnector;
import equipments.meter.ElectricMeterOutboundPort;
import equipments.config.ConnectorGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.bases.RegistrationCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

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
@OfferedInterfaces(offered = {RegistrationCI.class})
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class, ClocksServerCI.class, RegistrationInboundPort.class})
public class HEM_descriptors 
extends AbstractComponent
implements RegistrationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final boolean VERBOSE = true;
	public static final String URI_ELECTRIC_METER_PORT = "ELECTRIC-METER-PORT-URI";
	public static final String URI_AIR_CONDITIONING_PORT = "AIR-CONDITIONING-PORT-URI";
	public static final String URI_FRIDGE_PORT = "FRIDGE-PORT-URI";
	public static final String URI_REGISTRATION_INBOUND_PORT = "REGISTRATION-INBOUND-PORT-URI";
	
	
	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort clocksServerOutboundPort;
	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort electricMeterOutboundPort;
	/** port to connect this inbound registration.							*/
	protected RegistrationInboundPort registrationInboundPort;
	
	/** port to connect to the Air Conditioning.							*/
	protected AdjustableOutboundPort adjustableOutboundPortAirConditioning; 
	/** port to connect the Fridge 											*/
	protected AdjustableOutboundPort adjustableOutboundPortFridge;
	
	/** hashmap of the AdjustableOutboundPort of each equipment				*/
	public HashMap<String, AdjustableOutboundPort> registeredUriEquipments;
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
	 * @throws Exception 
	 *
	 */
	protected HEM_descriptors() throws Exception {
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.initialise();	
	}
	
	protected void initialise() throws Exception {
		registeredUriEquipments = new HashMap<String, AdjustableOutboundPort>();
		electricMeterOutboundPort = new ElectricMeterOutboundPort(URI_ELECTRIC_METER_PORT, this);
		registrationInboundPort = new RegistrationInboundPort(URI_REGISTRATION_INBOUND_PORT, this);
		
		registrationInboundPort.publishPort();
		
		if(VERBOSE) {
			this.tracer.get().setTitle("Home Energy Manager component");
			this.tracer.get().setRelativePosition(0, 0);
			this.toggleTracing();
			this.traceMessage("\n");
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
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

			this.electricMeterOutboundPort = new ElectricMeterOutboundPort(this);
			this.electricMeterOutboundPort.publishPort();
			this.doPortConnection(
					this.electricMeterOutboundPort.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

				
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
		long startTimeInNanos =
				TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()
						+ CVMIntegrationTest.DELAY_TO_START_IN_MILLIS);
		Instant startInstant = Instant.parse("2023-09-20T15:00:00.00Z");
		System.out.println("HEM creates the clock");
		AcceleratedClock ac =
				this.clocksServerOutboundPort.createClock(
						CVMIntegrationTest.TEST_CLOCK_URI,
						startTimeInNanos,
						startInstant,
						1.0);
		System.out.println("HEM has created the clock");

		// TODO pq deconnecte deja ici ?
		this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
		this.clocksServerOutboundPort.unpublishPort();
		System.out.println("HEM has disconnected from the clocks server");

		// simplified integration testing for meter.
		this.traceMessage("Electric meter current consumption? " +
				this.electricMeterOutboundPort.getCurrentConsumption() + "\n");
		this.traceMessage("Electric meter current production? " +
				this.electricMeterOutboundPort.getCurrentProduction() + "\n");

		System.out.println("HEM waits until start");
		// Test for the Air Conditioning
		Instant airConditioningTestStart = Instant.parse("2023-09-20T15:00:05.00Z");
		ac.waitUntilStart();
		long delay = ac.nanoDelayUntilInstant(airConditioningTestStart);
		System.out.println("HEM schedules the Air Conditioning test");

		// This is to avoid mixing the 'this' of the task object with the 'this'
		// representing the component object in the code of the next methods run
		AbstractComponent o = this;
		
		//this.adjustableOutboundPortAirConditioning = registeredUriEquipments.get(URI_AIR_CONDITIONING_PORT);
		
		this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
//							o.traceMessage("------------- Air Conditioning -------------\n");
//							o.traceMessage("Air Conditioning maxMode index? " +
//									adjustableOutboundPortAirConditioning.maxMode() + "\n");
//							o.traceMessage("Air Conditioning current mode index? " +
//									adjustableOutboundPortAirConditioning.currentMode() + "\n");
//							o.traceMessage("Air Conditioning going down one mode? " +
//									adjustableOutboundPortAirConditioning.downMode() + "\n");
//							o.traceMessage("Air Conditioning current mode is? " +
//									adjustableOutboundPortAirConditioning.currentMode() + "\n");
//							o.traceMessage("Air Conditioning going up one mode? " +
//									adjustableOutboundPortAirConditioning.upMode() + "\n");
//							o.traceMessage("Air Conditioning current mode is? " +
//									adjustableOutboundPortAirConditioning.currentMode() + "\n");
//							o.traceMessage("Air Conditioning setting current mode? " +
//									adjustableOutboundPortAirConditioning.setMode(2) + "\n");
//							o.traceMessage("Air Conditioning current mode is? " +
//									adjustableOutboundPortAirConditioning.currentMode() + "\n");
//							o.traceMessage("Air Conditioning is suspended? " +
//									adjustableOutboundPortAirConditioning.suspended() + "\n");
//							o.traceMessage("Air Conditioning suspends? " +
//									adjustableOutboundPortAirConditioning.suspend() + "\n");
//							o.traceMessage("Air Conditioning is suspended? " +
//									adjustableOutboundPortAirConditioning.suspended() + "\n");
//							o.traceMessage("Air Conditioning emergency? " +
//									adjustableOutboundPortAirConditioning.emergency() + "\n");
//							Thread.sleep(1000);
//							o.traceMessage("Air Conditioning emergency? " +
//									adjustableOutboundPortAirConditioning.emergency() + "\n");
//							o.traceMessage("Air Conditioning resumes? " +
//									adjustableOutboundPortAirConditioning.resume() + "\n");
//							o.traceMessage("Air Conditioning is suspended? " +
//									adjustableOutboundPortAirConditioning.suspended() + "\n");
//							o.traceMessage("Air Conditioning current mode is? " +
//									adjustableOutboundPortAirConditioning.currentMode() + "\n");
							
							o.traceMessage("------------- Fridge  -------------\n");
							o.traceMessage("Fridge maxMode index? " +
									registeredUriEquipments.get("FRIDGE-URI").maxMode() + "\n");
							o.traceMessage("Fridge current mode index? " +									registeredUriEquipments.get("FRIDGE-URI").currentMode() + "\n");
							o.traceMessage("Fridge going down one mode? " +
									registeredUriEquipments.get("FRIDGE-URI").downMode() + "\n");
							o.traceMessage("Fridge current mode is? " +
									registeredUriEquipments.get("FRIDGE-URI").currentMode() + "\n");
//							o.traceMessage("Fridge going up one mode? " +
//									registeredUriEquipments.get("FRIDGE-URI").upMode() + "\n");
//							o.traceMessage("Fridge current mode is? " +
//									registeredUriEquipments.get("FRIDGE-URI").currentMode() + "\n");
//							o.traceMessage("Fridge setting current mode? " +
//									registeredUriEquipments.get("FRIDGE-URI").setMode(2) + "\n");
//							o.traceMessage("Fridge current mode is? " +
//									registeredUriEquipments.get("FRIDGE-URI").currentMode() + "\n");
//							o.traceMessage("Fridge is suspended? " +
//									registeredUriEquipments.get("FRIDGE-URI").suspended() + "\n");
//							o.traceMessage("Fridge suspends? " +
//									registeredUriEquipments.get("FRIDGE-URI").suspend() + "\n");
//							o.traceMessage("Fridge is suspended? " +
//									registeredUriEquipments.get("FRIDGE-URI").suspended() + "\n");
//							o.traceMessage("Fridge emergency? " +
//									registeredUriEquipments.get("FRIDGE-URI").emergency() + "\n");
//							Thread.sleep(1000);
//							o.traceMessage("Fridge emergency? " +
//									registeredUriEquipments.get("FRIDGE-URI").emergency() + "\n");
//							o.traceMessage("Fridge resumes? " +
//									registeredUriEquipments.get("FRIDGE-URI").resume() + "\n");
//							o.traceMessage("Fridge is suspended? " +
//									registeredUriEquipments.get("FRIDGE-URI").suspended() + "\n");
//							o.traceMessage("Fridge current mode is? " +
//									registeredUriEquipments.get("FRIDGE-URI").currentMode() + "\n");
							
							System.out.println("HEM test ends");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.electricMeterOutboundPort.getPortURI());
		
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
			
			this.registrationInboundPort.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		
		super.shutdown();
	}

	/**
	 * Test if uid registered
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean registered(String uid) throws Exception {
		if(VERBOSE)
			this.traceMessage("Inscription verification for "+ uid + "\n");
		if(this.registeredUriEquipments.containsKey(uid))
			return true;
		return false;
	}

	/**
	 * Create connector and register equipment
	 * @param uid
	 * @param controlPortURI
	 * @param path2xmlControlAdapter
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		if(VERBOSE)
			this.traceMessage(uid+" inscription.\n\n");
		if(registered(uid))
			return false;
		
		AdjustableOutboundPort ao = new AdjustableOutboundPort(this);
		ao.publishPort();
		this.registeredUriEquipments.put(uid, ao);
		// Créez une instance de la classe chargée en mémoire
		Class <?> classConnector = ConnectorGenerator.generate(path2xmlControlAdapter);
        Object connectorCI = null;
		try {
			connectorCI = classConnector.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		this.doPortConnection(
				ao.getPortURI(),
				controlPortURI,
				connectorCI.getClass().getCanonicalName());
					
		return false;
	}

	/**
	 * Unregister equipment
	 * @param uid
	 * @throws Exception
	 */
	@Override
	public void unregister(String uid) throws Exception {
		if(VERBOSE)
			this.traceMessage("Unsubscription of "+uid+"\n\n");
		if(registered(uid)) {
			this.doPortDisconnection(this.registeredUriEquipments.get(uid).getPortURI());
			this.registeredUriEquipments.get(uid).unpublishPort();
			this.registeredUriEquipments.remove(uid);
		}
	}


}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/