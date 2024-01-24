package equipments.CookingPlate.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>RunCookingPlateUnitaryMILSimulation</code> is the main class used
 * to run simulations on the example models of the Cooking Plate in isolation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class shows how to use simulation model descriptors to create the
 * description of a simulation architecture and then create an instance of this
 * architecture by instantiating and connecting the models. Note how models
 * are described by atomic model descriptors and coupled model descriptors and
 * then the connections between coupled models and their submodels as well as
 * exported events to imported ones are described by different maps. In this
 * example, only connections between models within this architecture are
 * necessary, but when creating coupled models, they can also import and export
 * events consumed and produced by their submodels.
 * </p>
 * <p>
 * The architecture object is the root of this description and it provides
 * the method {@code constructSimulator} that instantiate the models and
 * connect them. This method returns the reference on the simulator attached
 * to the root coupled model in the architecture instance, which is then used
 * to perform simulation runs by calling the method
 * {@code doStandAloneSimulation}
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
 */
public class RunCookingPlateUnitaryMILSimulation {
	public static void	main(String[] args) {
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			// the Cooking Plate model simulating its electricity consumption, an
			// atomic HIOA model hence we use an AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					CookingPlateElectricityModel.MIL_URI,
					AtomicHIOA_Descriptor.create(
							CookingPlateElectricityModel.class,
							CookingPlateElectricityModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			
			// for atomic model, we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					CookingPlateUserModel.MIL_URI,
					AtomicModelDescriptor.create(
							CookingPlateUserModel.class,
							CookingPlateUserModel.MIL_URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					CookingPlateStateModel.MIL_URI,
					AtomicModelDescriptor.create(
							CookingPlateStateModel.class,
							CookingPlateStateModel.MIL_URI,
							TimeUnit.HOURS,
							null));

			
			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(CookingPlateElectricityModel.MIL_URI);
			submodels.add(CookingPlateStateModel.MIL_URI);
			submodels.add(CookingPlateUserModel.MIL_URI);

			// event exchanging connections between exporting and importing models
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>();
					
					// Cooking Plate
					connections.put(
							new EventSource(CookingPlateUserModel.MIL_URI, SwitchOnCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateStateModel.MIL_URI,
											SwitchOnCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.MIL_URI, SwitchOffCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateStateModel.MIL_URI,
											SwitchOffCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.MIL_URI, IncreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateStateModel.MIL_URI,
											IncreaseCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.MIL_URI, DecreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateStateModel.MIL_URI,
											DecreaseCookingPlate.class)
							});

					connections.put(
							new EventSource(CookingPlateStateModel.MIL_URI, SwitchOnCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.MIL_URI,
											SwitchOnCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateStateModel.MIL_URI, SwitchOffCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.MIL_URI,
											SwitchOffCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateStateModel.MIL_URI, IncreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.MIL_URI,
											IncreaseCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateStateModel.MIL_URI, DecreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.MIL_URI,
											DecreaseCookingPlate.class)
							});

					// coupled model descriptor
					coupledModelDescriptors.put(
							CookingPlateCoupledModel.MIL_URI,
							new CoupledModelDescriptor(
									CookingPlateCoupledModel.class,
									CookingPlateCoupledModel.MIL_URI,
									submodels,
									null,
									null,
									connections,
									null));

					// simulation architecture
					ArchitectureI architecture =
							new Architecture(
									CookingPlateCoupledModel.MIL_URI,
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
					se.doStandAloneSimulation(0.0, 24.0);
					System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

