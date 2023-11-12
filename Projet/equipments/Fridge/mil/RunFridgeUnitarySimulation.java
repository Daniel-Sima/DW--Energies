package equipments.Fridge.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.Fridge.mil.events.Cool;
import equipments.Fridge.mil.events.DoNotCool;
import equipments.Fridge.mil.events.SetPowerFridge;
import equipments.Fridge.mil.events.SwitchOffFridge;
import equipments.Fridge.mil.events.SwitchOnFridge;
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

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>RunFridgeUnitarySimulation</code> creates a simulator
 * for the Fridge and then runs a typical simulation.
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
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class RunFridgeUnitarySimulation {
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			// the Fridge models simulating its electricity consumption, its
			// temperatures and the internal temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					FridgeElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FridgeElectricityModel.class,
							FridgeElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					FridgeTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							FridgeTemperatureModel.class,
							FridgeTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					InternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							InternalTemperatureModel.class,
							InternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			// the Fridge unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					FridgeUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							FridgeUnitTesterModel.class,
							FridgeUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(FridgeElectricityModel.URI);
			submodels.add(FridgeTemperatureModel.URI);
			submodels.add(InternalTemperatureModel.URI);
			submodels.add(FridgeUnitTesterModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>();

					connections.put(
							new EventSource(FridgeUnitTesterModel.URI,
									SetPowerFridge.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											SetPowerFridge.class)
							});
					connections.put(
							new EventSource(FridgeUnitTesterModel.URI,
									SwitchOnFridge.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											SwitchOnFridge.class)
							});
					connections.put(
							new EventSource(FridgeUnitTesterModel.URI,
									SwitchOffFridge.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											SwitchOffFridge.class),
									new EventSink(FridgeTemperatureModel.URI,
											SwitchOffFridge.class)
							});
					connections.put(
							new EventSource(FridgeUnitTesterModel.URI, Cool.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											Cool.class),
									new EventSink(FridgeTemperatureModel.URI,
											Cool.class)
							});
					connections.put(
							new EventSource(FridgeUnitTesterModel.URI, DoNotCool.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											DoNotCool.class),
									new EventSink(FridgeTemperatureModel.URI,
											DoNotCool.class)
							});

					// variable bindings between exporting and importing models
					Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

							bindings.put(new VariableSource("internalTemperature",
									Double.class,
									InternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("internalTemperature",
													Double.class,
													FridgeTemperatureModel.URI)
							});
							bindings.put(new VariableSource("currentCoolingPower",
									Double.class,
									FridgeElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentCoolingPower",
													Double.class,
													FridgeTemperatureModel.URI)
							});

							// coupled model descriptor
							coupledModelDescriptors.put(
									FridgeCoupledModel.URI,
									new CoupledHIOA_Descriptor(
											FridgeCoupledModel.class,
											FridgeCoupledModel.URI,
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
											FridgeCoupledModel.URI,
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
							se.doStandAloneSimulation(0.0, 10.0);
							System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

