package stocking.Battery.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.mil.CookingPlateElectricityModel;
import equipments.CookingPlate.mil.CookingPlateUserModel;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
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
import production.aleatory.SolarPanel.mil.ExternalWeatherModel;
import production.aleatory.SolarPanel.mil.SolarPanelElectricityModel;
import production.intermittent.PetrolGenerator.mil.PetrolGeneratorElectricityModel;
import production.intermittent.PetrolGenerator.mil.PetrolGeneratorUnitTesterModel;
import production.intermittent.PetrolGenerator.mil.events.DoNotProduce;
import production.intermittent.PetrolGenerator.mil.events.FillFuelTank;
import production.intermittent.PetrolGenerator.mil.events.Producing;
import production.intermittent.PetrolGenerator.mil.events.SwitchOffPetrolGenerator;
import production.intermittent.PetrolGenerator.mil.events.SwitchOnPetrolGenerator;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>RunBatteryUnitarySimulation</code> creates a simulator
 * for the Battery and then runs a typical simulation of a day.
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
 * <p>Created on : 2023-11-12</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class RunBatteryUnitarySimulation {
	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
					new HashMap<>();

			// the heater models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			// Battery
			atomicModelDescriptors.put(
					BatteryElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							BatteryElectricityModel.class,
							BatteryElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			// Solar Panel
			atomicModelDescriptors.put(
					SolarPanelElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							SolarPanelElectricityModel.class,
							SolarPanelElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					ExternalWeatherModel.URI,
					AtomicHIOA_Descriptor.create(
							ExternalWeatherModel.class,
							ExternalWeatherModel.URI,
							TimeUnit.HOURS,
							null));
			// Petrol Generator
			atomicModelDescriptors.put(
					PetrolGeneratorElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							PetrolGeneratorElectricityModel.class,
							PetrolGeneratorElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			// Cooking Plate
//			atomicModelDescriptors.put(
//					CookingPlateElectricityModel.URI,
//					AtomicHIOA_Descriptor.create(
//							CookingPlateElectricityModel.class,
//							CookingPlateElectricityModel.URI,
//							TimeUnit.HOURS,
//							null));

			// the heater unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			// Battery
			atomicModelDescriptors.put(
					BatteryUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							BatteryUnitTesterModel.class,
							BatteryUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));
			// Petrol Generator
			atomicModelDescriptors.put(
					PetrolGeneratorUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							PetrolGeneratorUnitTesterModel.class,
							PetrolGeneratorUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));
			// Cooking Plate
//			atomicModelDescriptors.put(
//					CookingPlateUserModel.URI,
//					AtomicModelDescriptor.create(
//							CookingPlateUserModel.class,
//							CookingPlateUserModel.URI,
//							TimeUnit.HOURS,
//							null));



			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
					new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			// Battery 
			submodels.add(BatteryElectricityModel.URI);
			submodels.add(BatteryUnitTesterModel.URI);
			// Solar Panel
			submodels.add(SolarPanelElectricityModel.URI);
			submodels.add(ExternalWeatherModel.URI);
			// Petrol Generator
			submodels.add(PetrolGeneratorElectricityModel.URI);
			submodels.add(PetrolGeneratorUnitTesterModel.URI);
			// Cooking Plate
//			submodels.add(CookingPlateElectricityModel.URI);
//			submodels.add(CookingPlateUserModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
					new HashMap<EventSource,EventSink[]>();

					// Petrol Generator
					connections.put(
							new EventSource(PetrolGeneratorUnitTesterModel.URI,
									FillFuelTank.class),
							new EventSink[] {
									new EventSink(PetrolGeneratorElectricityModel.URI,
											FillFuelTank.class)
							});
					connections.put(
							new EventSource(PetrolGeneratorUnitTesterModel.URI,
									SwitchOnPetrolGenerator.class),
							new EventSink[] {
									new EventSink(PetrolGeneratorElectricityModel.URI,
											SwitchOnPetrolGenerator.class)
							});
					connections.put(
							new EventSource(PetrolGeneratorUnitTesterModel.URI,
									SwitchOffPetrolGenerator.class),
							new EventSink[] {
									new EventSink(PetrolGeneratorElectricityModel.URI,
											SwitchOffPetrolGenerator.class)
							});
					connections.put(
							new EventSource(PetrolGeneratorUnitTesterModel.URI, Producing.class),
							new EventSink[] {
									new EventSink(PetrolGeneratorElectricityModel.URI,
											Producing.class),
							});
					connections.put(
							new EventSource(PetrolGeneratorUnitTesterModel.URI, DoNotProduce.class),
							new EventSink[] {
									new EventSink(PetrolGeneratorElectricityModel.URI,
											DoNotProduce.class)
							});

					// Cooking plate
//					connections.put(
//							new EventSource(CookingPlateUserModel.URI, SwitchOnCookingPlate.class),
//							new EventSink[] {
//									new EventSink(CookingPlateElectricityModel.URI,
//											SwitchOnCookingPlate.class)
//							});
//					connections.put(
//							new EventSource(CookingPlateUserModel.URI, SwitchOffCookingPlate.class),
//							new EventSink[] {
//									new EventSink(CookingPlateElectricityModel.URI,
//											SwitchOffCookingPlate.class)
//							});
//					connections.put(
//							new EventSource(CookingPlateUserModel.URI, IncreaseCookingPlate.class),
//							new EventSink[] {
//									new EventSink(CookingPlateElectricityModel.URI,
//											IncreaseCookingPlate.class)
//							});
//					connections.put(
//							new EventSource(CookingPlateUserModel.URI, DecreaseCookingPlate.class),
//							new EventSink[] {
//									new EventSink(CookingPlateElectricityModel.URI,
//											DecreaseCookingPlate.class)
//							});

					// variable bindings between exporting and importing models
					Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

							// Solar Panel
							bindings.put(new VariableSource("externalSolarIrradiance",
									Double.class,
									ExternalWeatherModel.URI),
									new VariableSink[] {
											new VariableSink("externalSolarIrradiance",
													Double.class,
													SolarPanelElectricityModel.URI)
							});

							bindings.put(new VariableSource("currentPowerProducedSolarPanel",
									Double.class,
									SolarPanelElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentPowerProducedSolarPanel", 
													Double.class,
													BatteryElectricityModel.URI)
							});

							// Petrol Generator
							bindings.put(new VariableSource("currentPowerProducedPetrolGenerator",
									Double.class,
									PetrolGeneratorElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentPowerProducedPetrolGenerator", 
													Double.class,
													BatteryElectricityModel.URI)
							});
							
							// Cooking Plate
//							bindings.put(new VariableSource("currentPowerConsumed",
//									Double.class,
//									CookingPlateElectricityModel.URI),
//									new VariableSink[] {
//											new VariableSink("currentPowerConsumedCookingPlate", 
//													Double.class,
//													BatteryElectricityModel.URI)
//							});

							// coupled model descriptor
							coupledModelDescriptors.put(
									BatteryCoupledModel.URI,
									new CoupledHIOA_Descriptor(
											BatteryCoupledModel.class,
											BatteryCoupledModel.URI,
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
											BatteryCoupledModel.URI,
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
							se.doStandAloneSimulation(0.0, 6.0);
							System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
