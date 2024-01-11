package fr.sorbonne_u.components.hem2023e3.equipments.heater;

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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterExternalControlCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterExternalControlInboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterInternalControlInboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.connections.HeaterUserInboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterActuatorInboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.connections.HeaterSensorDataInboundPort;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterCompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterStateMeasure;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.HeaterStateModel;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.HeaterTemperatureModel;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SetPowerHeater.PowerValue;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.hem2023e3.utils.MeasurementUnit;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>Heater</code> a heater component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The component is relatively complex as it attempts to show in one component
 * several possibilities, like the capability to create several local simulation
 * architectures, the definition of a well-structured sensor and actuator
 * interfaces as well as the capability to run in different modes (unit test,
 * integration test, MIL simulations and SIL simulations) each with their own
 * test scenario.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code targetTemperature >= -50.0 && targetTemperature <= 50.0}
 * invariant	{@code currentPowerLevel >= 0.0 && currentPowerLevel <= MAX_POWER_LEVEL}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={HeaterUserCI.class, HeaterInternalControlCI.class,
							HeaterExternalControlCI.class,
							HeaterSensorDataCI.HeaterSensorOfferedPullCI.class,
							HeaterActuatorCI.class})
@RequiredInterfaces(required={DataOfferedCI.PushCI.class, ClocksServerCI.class})
public class			Heater
extends		AbstractCyPhyComponent
implements	HeaterUserImplI,
			HeaterInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>HeaterState</code> describes the operation
	 * states of the heater.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum		HeaterState
	{
		/** heater is on.													*/
		ON,
		/** heater is heating.												*/
		HEATING,
		/** heater is off.													*/
		OFF
	}

	/**
	 * The enumeration <code>HeaterSensorMeasures</code> describes the measures
	 * that the heater component performs.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * TODO: not used yet!
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-11-28</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum		HeaterSensorMeasures
	{
		/** heating status: a boolean sensor where true means currently
		 *  heating, false currently not heating.							*/
		HEATING_STATUS,
		/** the current target temperature.									*/
		TARGET_TEMPERATURE,
		/** the current room temperature.									*/
		CURRENT_TEMPERATURE,
		/** both the target and the current temperatures.					*/
		COMPOUND_TEMPERATURES
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"Heater-RIP-URI";	
	/** max power level of the heater, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;
	/** standard target temperature for the heater.							*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 19.0;
	public static final MeasurementUnit	TEMPERATURE_UNIT =
														MeasurementUnit.CELSIUS;

	/** URI of the heater port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"HEATER-USER-INBOUND-PORT-URI";
	/** URI of the heater port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"HEATER-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the heater port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"HEATER-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String		SENSOR_INBOUND_PORT_URI =
											"HEATER-SENSOR-INBOUND-PORT-URI";
	public static final String		ACTUATOR_INBOUND_PORT_URI =
											"HEATER-ACTUATOR-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 10.0;

	/** current state (on, off) of the heater.								*/
	protected HeaterState			currentState;
	/**	current power level of the heater.									*/
	protected double				currentPowerLevel;
	/** inbound port offering the <code>HeaterUserCI</code> interface.		*/
	protected HeaterUserInboundPort	hip;
	/** inbound port offering the <code>HeaterInternalControlCI</code>
	 *  interface.															*/
	protected HeaterInternalControlInboundPort	hicip;
	/** inbound port offering the <code>HeaterExternalControlCI</code>
	 *  interface.															*/
	protected HeaterExternalControlInboundPort	hecip;
	/** target temperature for the heating.	*/
	protected double				targetTemperature;

	// Sensors/actuators

	/** the inbound port through which the sensors are called.				*/
	protected HeaterSensorDataInboundPort	sensorInboundPort;
	/** the inbound port through which the actuators are called.			*/
	protected HeaterActuatorInboundPort		actuatorInboundPort;

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
	protected static final String		CURRENT_TEMPERATURE_NAME =
														"currentTemperature";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !on()}
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected			Heater() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
			 ACTUATOR_INBOUND_PORT_URI);
	}

	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterSensorInboundPortURI != null && !heaterSensorInboundPortURI.isEmpty()}
	 * pre	{@code heaterActuatorInboundPortURI != null && !heaterActuatorInboundPortURI.isEmpty()}
	 * post	{@code !on()}
	 * </pre>
	 * 
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
	 * @param heaterSensorInboundPortURI			URI of the inbound port to call the heater component sensors.
	 * @param heaterActuatorInboundPortURI			URI of the inbound port to call the heater component actuators.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Heater(
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI,
		String heaterSensorInboundPortURI,
		String heaterActuatorInboundPortURI
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, heaterUserInboundPortURI,
			 heaterInternalControlInboundPortURI,
			 heaterExternalControlInboundPortURI,
			 heaterSensorInboundPortURI,
			 heaterActuatorInboundPortURI,
			 ExecutionType.STANDARD, null, null, 0.0, null);
	}

	/**
	 * create a new heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterSensorInboundPortURI != null && !heaterSensorInboundPortURI.isEmpty()}
	 * pre	{@code heaterActuatorInboundPortURI != null && !heaterActuatorInboundPortURI.isEmpty()}
	 * pre	{@code currentExecutionType != null}
	 * pre	{@code !currentExecutionType.isSimulated() || (simArchitectureURI != null && !simArchitectureURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSimulated() || (localSimulatorURI != null && !localSimulatorURI.isEmpty())}
	 * pre	{@code !currentExecutionType.isSIL() || accFactor > 0.0}
	 * post	{@code !on()}
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
	 * @param heaterSensorInboundPortURI			URI of the inbound port to call the heater component sensors.
	 * @param heaterActuatorInboundPortURI			URI of the inbound port to call the heater component actuators.
	 * @param currentExecutionType					current execution type for the next run.
	 * @param simArchitectureURI					URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI						URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor								acceleration factor for the simulation.
	 * @param clockURI								URI of the clock to be used to synchronise the test scenarios and the simulation.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Heater(
		String reflectionInboundPortURI,
		String heaterUserInboundPortURI,
		String heaterInternalControlInboundPortURI,
		String heaterExternalControlInboundPortURI,
		String heaterSensorInboundPortURI,
		String heaterActuatorInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor,
		String clockURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);

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

		this.initialise(heaterUserInboundPortURI,
						heaterInternalControlInboundPortURI,
						heaterExternalControlInboundPortURI,
						heaterSensorInboundPortURI,
						heaterActuatorInboundPortURI);
	}

	/**
	 * create a new thermostated heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code heaterUserInboundPortURI != null && !heaterUserInboundPortURI.isEmpty()}
	 * pre	{@code heaterInternalControlInboundPortURI != null && !heaterInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code heaterExternalControlInboundPortURI != null && !heaterExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param heaterUserInboundPortURI				URI of the inbound port to call the heater component for user interactions.
	 * @param heaterInternalControlInboundPortURI	URI of the inbound port to call the heater component for internal control.
	 * @param heaterExternalControlInboundPortURI	URI of the inbound port to call the heater component for external control.
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

		this.currentState = HeaterState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.hip = new HeaterUserInboundPort(heaterUserInboundPortURI, this);
		this.hip.publishPort();
		this.hicip = new HeaterInternalControlInboundPort(
									heaterInternalControlInboundPortURI, this);
		this.hicip.publishPort();
		this.hecip = new HeaterExternalControlInboundPort(
									heaterExternalControlInboundPortURI, this);
		this.hecip.publishPort();
		this.sensorInboundPort = new HeaterSensorDataInboundPort(
									heaterSensorInboundPortURI, this);
		this.sensorInboundPort.publishPort();
		this.actuatorInboundPort = new HeaterActuatorInboundPort(
									heaterActuatorInboundPortURI, this);
		this.actuatorInboundPort.publishPort();

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.createHeaterMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
		case SIL_SIMULATION:
			architecture =
					MILSimulationArchitectures.
						createHeaterRTArchitecture(
								this.currentExecutionType,
								this.accFactor);
		assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
				new AssertionError(
						"local simulator " + this.localSimulatorURI
						+ " does not exist!");
		this.addLocalSimulatorArchitecture(architecture);
		this.architecturesURIs2localSimulatorURIS.
				put(this.simArchitectureURI, this.localSimulatorURI);
		break;
		case STANDARD:
		case INTEGRATION_TEST:
		default:
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Heater component");
			this.tracer.get().setRelativePosition(1, 2);
			this.toggleTracing();		
		}
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
			switch (this.currentExecutionType) {
			case MIL_SIMULATION:
				this.asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				this.asp.setPluginURI(uri);
				this.asp.setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case MIL_RT_SIMULATION:
				this.asp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case SIL_SIMULATION:
				// For SIL simulations, we use the ModelStateAccessI protocol
				// to provide the access to the current temperature computed
				// by the HeaterTemperatureModel.
				this.asp = new RTAtomicSimulatorPlugin() {
					private static final long serialVersionUID = 1L;
					/**
					 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String, java.lang.String)
					 */
					@Override
					public Object	getModelStateValue(
						String modelURI,
						String name
						) throws Exception
					{
						assert	modelURI.equals(HeaterTemperatureModel.SIL_URI);
						assert	name.equals(CURRENT_TEMPERATURE_NAME);
						return ((HeaterTemperatureModel)
										this.atomicSimulators.get(modelURI).
												getSimulatedModel()).
														getCurrentTemperature();
					}
				};
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
						(Architecture) this.localSimulatorsArchitectures.get(uri);
				((RTAtomicSimulatorPlugin)this.asp).setPluginURI(uri);
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				break;
			case STANDARD:
			case INTEGRATION_TEST:
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}		
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (!this.currentExecutionType.isStandard()) {
			this.clockServerOBP = new ClocksServerOutboundPort(this);
			this.clockServerOBP.publishPort();
			this.doPortConnection(
					this.clockServerOBP.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			AcceleratedClock clock =
					this.clockServerOBP.getClock(this.clockURI);
			this.doPortDisconnection(this.clockServerOBP.getPortURI());
			this.clockServerOBP.unpublishPort();
			this.clock.complete(clock);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hip.unpublishPort();
			this.hicip.unpublishPort();
			this.hecip.unpublishPort();
			this.sensorInboundPort.unpublishPort();
			this.actuatorInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater#on() current state: " +
											this.currentState + ".\n");
		}
		return this.currentState == HeaterState.ON ||
									this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		assert	!this.on() : new PreconditionException("!on()");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater switches on.\n");
		}

		this.currentState = HeaterState.ON;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												HeaterStateModel.SIL_URI,
												t -> new SwitchOnHeater(t));
		}

		this.sensorInboundPort.send(
				new HeaterSensorData<HeaterStateMeasure>(
						new HeaterStateMeasure(this.currentState)));

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater switches off.\n");
		}

		this.currentState = HeaterState.OFF;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												HeaterStateModel.SIL_URI,
												t -> new SwitchOffHeater(t));
		}

		this.sensorInboundPort.send(
				new HeaterSensorData<HeaterStateMeasure>(
						new HeaterStateMeasure(this.currentState)));

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		assert	target >= -50.0 && target <= 50.0 :
				new PreconditionException("target >= -50.0 && target <= 50.0");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater sets a new target "
										+ "temperature: " + target + ".\n");
		}

		this.targetTemperature = target;

		assert	this.getTargetTemperature() == target :
				new PostconditionException(
						"getTargetTemperature() == target");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndControlI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		double ret = this.targetTemperature;

		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its target"
											+ " temperature " + ret + ".\n");
		}

		assert	ret >= -50.0 && ret <= 50.0 :
				new PostconditionException(
						"return.getData() >= -50.0 && return.getData() <= 50.0");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndControlI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, the value is got from the
			// HeaterTemperatureModel through the ModelStateAccessI interface
			currentTemperature = 
					(double)((RTAtomicSimulatorPlugin)this.asp).
										getModelStateValue(
												HeaterTemperatureModel.SIL_URI,
												CURRENT_TEMPERATURE_NAME);
		}

		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return currentTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its heating status " + 
						(this.currentState == HeaterState.HEATING) + ".\n");
		}

		return this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater starts heating.\n");
		}

		this.currentState = HeaterState.HEATING;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												HeaterStateModel.SIL_URI,
												t -> new Heat(t));
		}

		this.sensorInboundPort.send(
				new HeaterSensorData<HeaterStateMeasure>(
						new HeaterStateMeasure(this.currentState)));

		assert	this.heating() : new PostconditionException("heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater stops heating.\n");
		}

		this.currentState = HeaterState.ON;

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												HeaterStateModel.SIL_URI,
												t -> new DoNotHeat(t));
		}

		this.sensorInboundPort.send(
				new HeaterSensorData<HeaterStateMeasure>(
						new HeaterStateMeasure(this.currentState)));

		assert	!this.heating() : new PostconditionException("!heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (Heater.VERBOSE) {
			this.traceMessage("Heater sets its power level to " + 
														powerLevel + ".\n");
		}

		if (powerLevel <= getMaxPowerLevel()) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, an operation done in the component code
			// must be reflected in the simulation; to do so, the component
			// code triggers an external event sent to the HairDryerStateModel
			// to make it change its state to on.
			PowerValue pv = new PowerValue(this.currentPowerLevel);
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												HeaterStateModel.SIL_URI,
												t -> new SetPowerHeater(t, pv));
		}

		assert	powerLevel > getMaxPowerLevel() ||
										getCurrentPowerLevel() == powerLevel :
				new PostconditionException(
						"powerLevel > getMaxPowerLevel() || "
						+ "getCurrentPowerLevel() == powerLevel");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double ret = this.currentPowerLevel;

		if (Heater.VERBOSE) {
			this.traceMessage("Heater returns its current power level " + 
																ret + ".\n");
		}

		assert	ret >= 0.0 && ret <= getMaxPowerLevel() :
				new PostconditionException(
							"return >= 0.0 && return <= getMaxPowerLevel()");

		return this.currentPowerLevel;
	}

	// -------------------------------------------------------------------------
	// Component sensors
	// -------------------------------------------------------------------------

	/**
	 * return				the heating status of the heater as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the heating status of the heater as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public HeaterSensorData<Measure<Boolean>>	heatingPullSensor()
	throws Exception
	{
		return new HeaterSensorData<Measure<Boolean>>(
										new Measure<Boolean>(this.heating()));
	}

	/**
	 * return the target temperature as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the target temperature as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public HeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return new HeaterSensorData<Measure<Double>>(
						new Measure<Double>(this.getTargetTemperature(),
											MeasurementUnit.CELSIUS));
	}

	/**
	 * return the current temperature as a sensor data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature as a sensor data.
	 * @throws Exception	<i>to do</i>.
	 */
	public HeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return new HeaterSensorData<Measure<Double>>(
						new Measure<Double>(this.getCurrentTemperature(),
											MeasurementUnit.CELSIUS));
	}

	/**
	 * start a sequence of temperatures pushes with the given period.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code controlPeriod > 0}
	 * pre	{@code tu != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param controlPeriod	period at which the pushes must be made.
	 * @param tu			time unit in which {@code controlPeriod} is expressed.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		AcceleratedClock ac = this.clock.get();
		// the accelerated period is in nanoseconds, hence first convert
		// the period to nanoseconds, perform the division and then
		// convert to long (hence providing a better precision than
		// first dividing and then converting to nanoseconds...)
		long actualControlPeriod =
			(long)((controlPeriod * tu.toNanos(1))/ac.getAccelerationFactor());
		// sanity checking, the standard Java scheduler has a
		// precision no less than 10 milliseconds...
		if (actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
			System.out.println(
					"Warning: accelerated control period is "
							+ "too small ("
							+ actualControlPeriod +
							"), unexpected scheduling problems may"
							+ " occur!");
		}
		this.temperaturesPushSensorTask(actualControlPeriod);
	}

	/**
	 * if the heater is not off, perform one push and schedule the next.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code actualControlPeriod > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param actualControlPeriod	period at which the push sensor must be triggered.
	 * @throws Exception			<i>to do</i>.
	 */
	protected void		temperaturesPushSensorTask(long actualControlPeriod)
	throws Exception
	{
		assert	actualControlPeriod > 0 :
				new PreconditionException("actualControlPeriod > 0");

		if (this.currentState != HeaterState.OFF) {
			this.traceMessage("Heater performs a new temperatures push.\n");
			this.temperaturesPushSensor();
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								temperaturesPushSensorTask(actualControlPeriod);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					actualControlPeriod,
					TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * sends the compound measure of the target and the current temperatures
	 * through the push sensor interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		temperaturesPushSensor() throws Exception
	{
		this.sensorInboundPort.send(
					new HeaterSensorData<HeaterCompoundMeasure>(
						new HeaterCompoundMeasure(
							this.targetTemperaturePullSensor().getMeasure(),
							this.currentTemperaturePullSensor().getMeasure())));
	}
}
// -----------------------------------------------------------------------------
