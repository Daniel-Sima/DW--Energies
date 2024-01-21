package production.aleatory.SolarPanel;

import java.util.concurrent.CompletableFuture;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.MILSimulationArchitectures;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import production.aleatory.SolarPanel.connections.SolarPanelExternalControlInboundPort;
import production.aleatory.SolarPanel.connections.SolarPanelMeteoControlInboundPort;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import utils.ExecutionType;
import utils.Measure;
import utils.MeasurementUnit;

/***********************************************************************************/
/**
 * The class <code>SolarPanel</code> is a solar panel component.
 *
 * <p><strong>Always on, not producing if the weather is bad. Size: 0.5m^2.</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentPowerLevelProduction >= 0.0 && currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@OfferedInterfaces(offered={SolarPanelExternalControlCI.class, SolarPanelMeteoControlCI.class})
@RequiredInterfaces(required={ClocksServerCI.class})
public class SolarPanel 
extends	AbstractCyPhyComponent  
implements 	SolarPanelExternalControlI, 
			SolarPanelMeteoControlI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"SolarPanel-RIP-URI";	
	
	/** max power level production of the solar panel, in watts.			*/
	protected final double MAX_POWER_LEVEL_PRODUCTION = 400.0;

	/** URI of the solar panel port for external control.				    */
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI =
			"SOLAR-PANEL-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the solar panel port for meteo control.				    */
	public static final String METEO_CONTROL_INBOUND_PORT_URI =
			"SOLAR-PANEL-METEO-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean VERBOSE = true;

	/**	current power level production of the solar panel.					*/
	protected double currentPowerLevelProduction;
	/** inbound port offering the <code>SolarPanelExternalControlCI</code>
	 *  interface.															*/
	protected SolarPanelExternalControlInboundPort solarPanelExternalControlInbound;
	/** inbound port offerinr the <code>SolarPanelMeteoControlCI</code> inteface */
	protected SolarPanelMeteoControlInboundPort solarPanelMeteoControlInboundPort;
	
	// Execution/Simulation

	/** outbound port to connect to the centralised clock server.			*/
	protected ClocksServerOutboundPort	clockServerOBP;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String				clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected final CompletableFuture<AcceleratedClock>	clock;

	/** plug-in holding the local simulation architecture and simulators.	*/
	protected AtomicSimulatorPlugin		asp;
	/** current type of execution.											*/
	protected final ExecutionType		currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String				simArchitectureURI;
	/** URI of the local simulator used to compose the global simulation
	 *  architecture.														*/
	protected final String				localSimulatorURI;
	/** acceleration factor to be used when running the real time
	 *  simulation.															*/
	protected double					accFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected SolarPanel() throws Exception {
		this(EXTERNAL_CONTROL_INBOUND_PORT_URI, METEO_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanel(
		String solarPanelExternalControlInboundPortURI, 
		String solarPanelMeteoControlInboundPort
		) throws Exception
	{
		this(
			REFLECTION_INBOUND_PORT_URI,
			solarPanelExternalControlInboundPortURI, 
			solarPanelMeteoControlInboundPort,
			ExecutionType.STANDARD, null, null, 0.0, null);
	}
	/***********************************************************************************/
	/**
	 * create a new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @param currentExecutionType					current execution type for the next run.
	 * @param simArchitectureURI					URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI						URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor								acceleration factor for the simulation.
	 * @param clockURI								URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanel(
		String reflectionInboundPortURI, 
		String solarPanelExternalControlInboundPortURI, 
		String solarPanelMeteoControlInboundPort,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor,
		String clockURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		
		assert	currentExecutionType != null :
				new PreconditionException("currentExecutionType != null");
		assert	!currentExecutionType.isSimulated() ||
							(simArchitectureURI != null &&
										!simArchitectureURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(simArchitectureURI != null && "
						+ "!simArchitectureURI.isEmpty())");
		assert	!currentExecutionType.isSimulated() ||
							(localSimulatorURI != null &&
											!localSimulatorURI.isEmpty()) :
				new PreconditionException(
						"currentExecutionType.isSimulated() ||  "
						+ "(localSimulatorURI != null && "
						+ "!localSimulatorURI.isEmpty())");
		assert	!currentExecutionType.isSIL() || accFactor > 0.0 :
				new PreconditionException(
						"!currentExecutionType.isSIL() || accFactor > 0.0");

		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.localSimulatorURI = localSimulatorURI;
		this.accFactor = accFactor;
		this.clockURI = clockURI;
		this.clock = new CompletableFuture<AcceleratedClock>();
				
		this.initialise(solarPanelExternalControlInboundPortURI, 
						solarPanelMeteoControlInboundPort);
	}

	/***********************************************************************************/
	/**
	 * initialisation of the new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String solarPanelExternalControlInboundPortURI,
			String solarPanelMeteoControlInboundPort
			) throws Exception
	{
		assert	solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty();

		this.currentPowerLevelProduction = 0;

		this.solarPanelExternalControlInbound = new SolarPanelExternalControlInboundPort(
				solarPanelExternalControlInboundPortURI, this);
		this.solarPanelExternalControlInbound.publishPort();
		
		this.solarPanelMeteoControlInboundPort = new SolarPanelMeteoControlInboundPort(solarPanelMeteoControlInboundPort, this);
		this.solarPanelMeteoControlInboundPort.publishPort();

		// switch (this.currentExecutionType) {
		// case MIL_SIMULATION:
		// 	Architecture architecture =
		// 			MILSimulationArchitectures.createHeaterMILArchitecture();
		// 	assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
		// 			new AssertionError(
		// 					"local simulator " + this.localSimulatorURI
		// 					+ " does not exist!");
		// 	this.addLocalSimulatorArchitecture(architecture);
		// 	this.architecturesURIs2localSimulatorURIS.
		// 				put(this.simArchitectureURI, this.localSimulatorURI);
		// 	break;
		// case MIL_RT_SIMULATION:
		// case SIL_SIMULATION:
		// 	architecture =
		// 			MILSimulationArchitectures.
		// 				createSolarPanelRTArchitecture(
		// 						this.currentExecutionType,
		// 						this.accFactor);
		// assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
		// 		new AssertionError(
		// 				"local simulator " + this.localSimulatorURI
		// 				+ " does not exist!");
		// this.addLocalSimulatorArchitecture(architecture);
		// this.architecturesURIs2localSimulatorURIS.
		// 		put(this.simArchitectureURI, this.localSimulatorURI);
		// break;
		// case STANDARD:
		// case INTEGRATION_TEST:
		// default:
		// }

		if (VERBOSE) {
			this.tracer.get().setTitle("Solar panel component");
			this.tracer.get().setRelativePosition(1, 3);
			this.toggleTracing();		
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.solarPanelExternalControlInbound.unpublishPort();
			this.solarPanelMeteoControlInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerLevelProduction() throws Exception {
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel max power production level production is "+ this.MAX_POWER_LEVEL_PRODUCTION + ".\n");
		}
		return MAX_POWER_LEVEL_PRODUCTION;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevelProduction() throws Exception {
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel current power production level production is "+ this.currentPowerLevelProduction + ".\n");
		}
		return currentPowerLevelProduction;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setPowerLevelProduction(double percentage) throws Exception {
		assert	percentage >= 0 && percentage <= 100 : new PreconditionException("percentage > 0 && percentage < 100");
		
		// TODO mby to change
		this.currentPowerLevelProduction = 4 * percentage; 
		
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel power production level changed to "+ this.currentPowerLevelProduction + ".\n");
		}
		
		assert	this.currentPowerLevelProduction >= 0 && this.currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION : new PreconditionException("this.currentPowerLevelProduction >= 0 && this.currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION ");
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/