package fr.sorbonne_u.components.hem2023e3.equipments.meter;

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
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterInboundPort;
import fr.sorbonne_u.components.hem2023e3.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023e3.equipments.meter.mil.MILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023e3.equipments.meter.sil.SILSimulationArchitectures;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.hem2023e3.utils.MeasurementUnit;
import fr.sorbonne_u.components.hem2023e3.utils.SensorData;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.util.concurrent.atomic.AtomicReference;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeter</code> implements a simplified electric meter
 * component.
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
@OfferedInterfaces(offered={ElectricMeterCI.class})
@RequiredInterfaces(required = {ClocksServerCI.class})
public class			ElectricMeter
extends		AbstractCyPhyComponent
implements	ElectricMeterImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the voltage at which the meter is operating.						*/
	public static final Measure<Double>	ELECTRIC_METER_VOLTAGE =
							new Measure<Double>(220.0, MeasurementUnit.VOLTS);
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String	REFLECTION_INBOUND_PORT_URI =
													"ELECTRIC-METER-RIP-URI";	
	/** URI of the electric meter inbound port used in tests.				*/
	public static final String	ELECTRIC_METER_INBOUND_PORT_URI =
															"ELECTRIC-METER";
	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;

	/** inbound port offering the <code>ElectricMeterCI</code> interface.	*/
	protected ElectricMeterInboundPort	emip;

	/** current total electric power consumption measured at the electric
	 *  meter in amperes.												 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerConsumption;
	/** current total electric power production measured at the electric
	 *  meter in watts.													 	*/
	protected AtomicReference<SensorData<Measure<Double>>>
											currentPowerProduction;
	
	// Execution/Simulation

	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort	clocksServerOutboundPort;
	/** accelerated clock, in integration and SIL simulation tests.			*/
	protected AcceleratedClock			clock;
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

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ELECTRIC_METER_INBOUND_PORT_URI != null && !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected			ElectricMeter() throws Exception
	{
		this(ELECTRIC_METER_INBOUND_PORT_URI);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String electricMeterInboundPortURI
		) throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, electricMeterInboundPortURI,
			 ExecutionType.STANDARD, null, null, 0.0);
	}

	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection innbound port of the component.
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param currentExecutionType		current execution type for the next run.
	 * @param simArchitectureURI		URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param localSimulatorURI			URI of the local simulator to be used in the simulation architecture.
	 * @param accFactor					acceleration factor for the simulation.
	 * @throws Exception					<i>to do</i>.
	 */
	protected			ElectricMeter(
		String reflectionInboundPortURI,
		String electricMeterInboundPortURI,
		ExecutionType currentExecutionType,
		String simArchitectureURI,
		String localSimulatorURI,
		double accFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, 2, 0);

		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");
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

		this.initialise(electricMeterInboundPortURI);
	}

	/**
	 * initialise an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected void		initialise(String electricMeterInboundPortURI)
	throws Exception
	{
		assert	electricMeterInboundPortURI != null &&
										!electricMeterInboundPortURI.isEmpty() :
				new PreconditionException(
						"electricMeterInboundPortURI != null && "
						+ "!electricMeterInboundPortURI.isEmpty()");

		this.emip =
				new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.emip.publishPort();

		this.currentPowerConsumption =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES)));
		this.currentPowerProduction =
			new AtomicReference<>(
					new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.WATTS)));

		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			Architecture architecture =
					MILSimulationArchitectures.
									createElectricMeterMILArchitecture();
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case MIL_RT_SIMULATION:
			architecture =
					MILSimulationArchitectures.
						createElectricMeterMILRTArchitecture(this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
						put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		case SIL_SIMULATION:
			architecture =
					SILSimulationArchitectures.
						createElectricMeterSILArchitecture(this.accFactor);
			assert	architecture.getRootModelURI().equals(this.localSimulatorURI) :
					new AssertionError(
							"local simulator " + this.localSimulatorURI
							+ " does not exist!");
			this.addLocalSimulatorArchitecture(architecture);
			this.architecturesURIs2localSimulatorURIS.
					put(this.simArchitectureURI, this.localSimulatorURI);
			break;
		default:
		}

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(1, 0);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power					electric power consumption in amperes.
	 */
	public void			setCurrentPowerConsumption(Measure<Double> power)
	{
		
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.AMPERES) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.AMPERES)");

		double old;
		double i;

		if (VERBOSE) {
			old = ((Measure<Double>)this.currentPowerConsumption.get().
														getMeasure()).getData();
		}
		SensorData<Measure<Double>> sd = null;
		if (this.currentExecutionType.isSIL()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock, 
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerConsumption.set(sd);

		if (VERBOSE) {
			i = power.getData();
			if (Math.abs(old - i) > 0.000001) {
				this.traceMessage(
					"Electric meter sets its current consumption at "
					+ this.currentPowerConsumption.get().getMeasure() + ".\n");
			}
		}
	}

	/**
	 * set the current power consumption, a method that is meant to be called
	 * only by the simulator in SIL runs, otherwise a hardware sensor would be
	 * used in standard executions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code power != null}
	 * pre	{@code power.getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param power					electric power production in watts.
	 */
	public void			setCurrentPowerProduction(Measure<Double> power)
	{
		assert	power != null : new PreconditionException("power != null");
		assert	power.getMeasurementUnit().equals(MeasurementUnit.WATTS) :
				new PreconditionException(
						"power.getMeasurementUnit().equals("
						+ "MeasurementUnit.WATTS)");

		SensorData<Measure<Double>> sd = null;
		if (this.currentExecutionType.isSIL()) {
			// in SIL simulation, an accelerated clock is used as time reference
			// for measurements and sensor data time stamps
			sd = new SensorData<>(this.clock,
								  new Measure<Double>(
										  this.clock,
										  power.getData(),
										  power.getMeasurementUnit()));
		} else {
			sd = new SensorData<>(power);
		}
		this.currentPowerProduction.set(sd);

		if (VERBOSE) {
			this.traceMessage(
					"Electric meter sets its current production at "
					+ this.currentPowerProduction.get().getMeasure() + ".\n");
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
				AtomicSimulatorPlugin asp = new AtomicSimulatorPlugin();
				String uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				Architecture architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				asp.setPluginURI(uri);
				asp.setSimulationArchitecture(architecture);
				this.installPlugin(asp);
				break;
			case MIL_RT_SIMULATION:
				RTAtomicSimulatorPlugin rtasp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				rtasp.setPluginURI(architecture.getRootModelURI());
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
			case SIL_SIMULATION:
				rtasp = new RTAtomicSimulatorPlugin();
				uri = this.architecturesURIs2localSimulatorURIS.
												get(this.simArchitectureURI);
				architecture =
					(Architecture) this.localSimulatorsArchitectures.get(uri);
				rtasp.setPluginURI(uri);
				rtasp.setSimulationArchitecture(architecture);
				this.installPlugin(rtasp);
				break;
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
		// In the electric meter, the accelerated clock is only used to get
		// properly time stamped sensor data in simulated time instants.
		this.clock = null;
		if (this.currentExecutionType.isIntegrationTest() ||
											this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("ElectricMeter gets the clock.");
			this.clock =
				this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("ElectricMeter got the clock " + this.clock);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.emip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterImplementationI#getCurrentConsumption()
	 */
	@Override
	public SensorData<Measure<Double>>	getCurrentConsumption() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage(
					"Electric meter returns its current consumption.\n");
		}

		SensorData<Measure<Double>> ret = null;
		if (this.currentExecutionType.isSIL()) {
			ret = this.currentPowerConsumption.get();
		} else {
			if (this.clock != null) {
				ret = new SensorData<>(
						this.clock,
						new Measure<Double>(this.clock,
											0.0,
											MeasurementUnit.AMPERES));
			} else {
				ret = new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.AMPERES));
			}
		}

		assert	ret != null : new PostconditionException("return != null");
		assert	ret.getMeasure().isScalar() :
				new PostconditionException("return.getMeasure().isScalar()");
		assert	((Measure<?>)ret.getMeasure()).getData() instanceof Double :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getData() "
						+ "instanceof Double");
		assert	((Measure<Double>)ret.getMeasure()).getData() >= 0.0 :
				new PostconditionException(
						"((Measure<Double>)return.getMeasure())."
						+ "getData() >= 0.0");
		assert	((Measure<?>)ret.getMeasure()).getMeasurementUnit().
											equals(MeasurementUnit.AMPERES) :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getMeasurementUnit()."
						+ "equals(MeasurementUnit.AMPERES)");

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterImplementationI#getCurrentProduction()
	 */
	@Override
	public SensorData<Measure<Double>>	getCurrentProduction() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Electric meter returns its current production.\n");
		}

		SensorData<Measure<Double>> ret = null;
		if (this.currentExecutionType.isSIL()) {
			ret = this.currentPowerProduction.get();
		} else {
			if (this.clock != null) {
				ret = new SensorData<>(
						this.clock,
						new Measure<Double>(this.clock,
											0.0,
											MeasurementUnit.WATTS));
			} else {
				ret = new SensorData<>(
							new Measure<Double>(0.0, MeasurementUnit.WATTS));
			}
		}

		assert	ret != null : new PostconditionException("return != null");
		assert	ret.getMeasure().isScalar() :
				new PostconditionException("return.getMeasure().isScalar()");
		assert	((Measure<?>)ret.getMeasure()).getData() instanceof Double :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getData() "
						+ "instanceof Double");
		assert	((Measure<Double>)ret.getMeasure()).getData() >= 0.0 :
				new PostconditionException(
						"((Measure<Double>)return.getMeasure())."
						+ "getData() >= 0.0");
		assert	((Measure<?>)ret.getMeasure()).getMeasurementUnit().
											equals(MeasurementUnit.WATTS) :
				new PostconditionException(
						"((Measure<?>)return.getMeasure()).getMeasurementUnit()."
						+ "equals(MeasurementUnit.WATTS)");

		return ret;
	}
}
// -----------------------------------------------------------------------------
