package equipments.AirConditioning.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunAirConditioningUnitarySimulation</code> creates a simulator
 * for the air conditioning and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class shows how to use simulation model descriptors to create the
 * description of a simulation architecture and then create an instance of this
 * architecture by instantiating and connecting the models. Note how models
 * are described by atomic model descriptors and coupled model descriptors and
 * then the connections between coupled models and their submodels as well as
 * exported events and variables to imported ones are described by different
 * maps. In this example, only connections of events and bindings of variables
 * between models within this architecture are necessary, but when creating
 * coupled models, they can also import and export events and variables
 * consumed and produced by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}.
 * </p>
 * <p>
 * The descriptors and maps can be viewed as kinds of nodes in the abstract
 * syntax tree of an architectural language that does not have a concrete
 * syntax yet.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunAirConditioningUnitaryMILSimulation
{
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					AirConditioningStateModel.MIL_URI,
					AtomicModelDescriptor.create(
							AirConditioningStateModel.class,
							AirConditioningStateModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			// the air conditioning models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					AirConditioningElectricityModel.MIL_URI,
					AtomicHIOA_Descriptor.create(
							AirConditioningElectricityModel.class,
							AirConditioningElectricityModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					AirConditioningTemperatureModel.MIL_URI,
					AtomicHIOA_Descriptor.create(
							AirConditioningTemperatureModel.class,
							AirConditioningTemperatureModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.MIL_URI,
					AtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			// the air conditioning unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					AirConditioningUnitTesterModel.MIL_URI,
					AtomicModelDescriptor.create(
							AirConditioningUnitTesterModel.class,
							AirConditioningUnitTesterModel.MIL_URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(AirConditioningStateModel.MIL_URI);
			submodels.add(AirConditioningElectricityModel.MIL_URI);
			submodels.add(AirConditioningTemperatureModel.MIL_URI);
			submodels.add(ExternalTemperatureModel.MIL_URI);
			submodels.add(AirConditioningUnitTesterModel.MIL_URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_URI,
							SetPowerAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_URI,
										  SetPowerAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_URI,
									SwitchOnAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_URI,
										  SwitchOnAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_URI,
									SwitchOffAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_URI,
										  SwitchOffAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_URI,
									Cool.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_URI,
										  Cool.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_URI,
									DoNotCool.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_URI,
										  DoNotCool.class)
					});

			connections.put(
					new EventSource(AirConditioningStateModel.MIL_URI,
									SetPowerAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_URI,
										  SetPowerAirConditioning.class),
							new EventSink(AirConditioningTemperatureModel.MIL_URI,
										  SetPowerAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_URI,
									SwitchOnAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_URI,
										  SwitchOnAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_URI,
									SwitchOffAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_URI,
										  SwitchOffAirConditioning.class),
							new EventSink(AirConditioningTemperatureModel.MIL_URI,
										  SwitchOffAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_URI, Cool.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_URI,
										  Cool.class),
							new EventSink(AirConditioningTemperatureModel.MIL_URI,
										  Cool.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_URI, DoNotCool.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_URI,
										  DoNotCool.class),
							new EventSink(AirConditioningTemperatureModel.MIL_URI,
										  DoNotCool.class)
					});

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("externalTemperature",
											Double.class,
											ExternalTemperatureModel.MIL_URI),
						 new VariableSink[] {
								 new VariableSink("externalTemperature",
										 		  Double.class,
										 		  AirConditioningTemperatureModel.MIL_URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					AirConditioningCoupledModel.MIL_URI,
					new CoupledHIOA_Descriptor(
							AirConditioningCoupledModel.class,
							AirConditioningCoupledModel.MIL_URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							AirConditioningCoupledModel.MIL_URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			se.doStandAloneSimulation(1.0, 3.0);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
