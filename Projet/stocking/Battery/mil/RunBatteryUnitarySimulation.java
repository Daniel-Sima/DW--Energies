package stocking.Battery.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.mil.CookingPlateUserModel;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.Fridge.mil.FridgeElectricityModel;
import equipments.Fridge.mil.FridgeTemperatureModel;
import equipments.Fridge.mil.FridgeUnitTesterModel;
import equipments.Fridge.mil.InternalTemperatureModel;
import equipments.Fridge.mil.events.SetPowerFridge;
import equipments.Fridge.mil.events.SwitchOffFridge;
import equipments.Fridge.mil.events.SwitchOnFridge;
import equipments.Lamp.mil.LampElectricityModel;
import equipments.Lamp.mil.LampUserModel;
import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import equipments.AirConditioning.mil.AirConditioningElectricityModel;
import equipments.AirConditioning.mil.AirConditioningTemperatureModel;
import equipments.AirConditioning.mil.AirConditioningUnitTesterModel;
import equipments.AirConditioning.mil.ExternalTemperatureModel;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.CookingPlate.mil.CookingPlateElectricityModel;
//import equipments.CookingPlate.mil.CookingPlateUserModel;
//import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
//import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
//import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
//import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.meter.mil.ElectricMeterElectricityModel;
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
import production.aleatory.SolarPanel.mil.SolarPanelUnitTesterModel;
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
			atomicModelDescriptors.put(
					SolarPanelUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							SolarPanelUnitTesterModel.class,
							SolarPanelUnitTesterModel.URI,
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
			atomicModelDescriptors.put(
					PetrolGeneratorUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							PetrolGeneratorUnitTesterModel.class,
							PetrolGeneratorUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));

			// Electric Meter Model
			atomicModelDescriptors.put(
					ElectricMeterElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							ElectricMeterElectricityModel.class,
							ElectricMeterElectricityModel.URI,
							TimeUnit.HOURS,
							null));

			// Cooking Plate
			atomicModelDescriptors.put(
					CookingPlateElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							CookingPlateElectricityModel.class,
							CookingPlateElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					CookingPlateUserModel.URI,
					AtomicModelDescriptor.create(
							CookingPlateUserModel.class,
							CookingPlateUserModel.URI,
							TimeUnit.HOURS,
							null));
			
			// Lamp
			atomicModelDescriptors.put(
					LampElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							LampElectricityModel.class,
							LampElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					LampUserModel.URI,
					AtomicModelDescriptor.create(
							LampUserModel.class,
							LampUserModel.URI,
							TimeUnit.HOURS,
							null));
			
			// Air Conditioning
			atomicModelDescriptors.put(
					AirConditioningElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							AirConditioningElectricityModel.class,
							AirConditioningElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					AirConditioningTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							AirConditioningTemperatureModel.class,
							AirConditioningTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.URI,
							TimeUnit.HOURS,
							null));
			atomicModelDescriptors.put(
					AirConditioningUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							AirConditioningUnitTesterModel.class,
							AirConditioningUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));
			
			// Fridge
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
			atomicModelDescriptors.put(
					FridgeUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							FridgeUnitTesterModel.class,
							FridgeUnitTesterModel.URI,
							TimeUnit.HOURS,
							null));

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
			submodels.add(SolarPanelUnitTesterModel.URI);
			// Petrol Generator
			submodels.add(PetrolGeneratorElectricityModel.URI);
			submodels.add(PetrolGeneratorUnitTesterModel.URI);
			// Electric Meter
			submodels.add(ElectricMeterElectricityModel.URI);
			// Cooking Plate
			submodels.add(CookingPlateElectricityModel.URI);
			submodels.add(CookingPlateUserModel.URI);
			// Lamp
			submodels.add(LampElectricityModel.URI);
			submodels.add(LampUserModel.URI);
			// Air Conditioning
			submodels.add(AirConditioningElectricityModel.URI);
			submodels.add(AirConditioningTemperatureModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(AirConditioningUnitTesterModel.URI);
			// Fridge
			submodels.add(FridgeElectricityModel.URI);
			submodels.add(FridgeTemperatureModel.URI);
			submodels.add(InternalTemperatureModel.URI);
			submodels.add(FridgeUnitTesterModel.URI);
			

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
					connections.put(
							new EventSource(CookingPlateUserModel.URI, SwitchOnCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.URI,
											SwitchOnCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.URI, SwitchOffCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.URI,
											SwitchOffCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.URI, IncreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.URI,
											IncreaseCookingPlate.class)
							});
					connections.put(
							new EventSource(CookingPlateUserModel.URI, DecreaseCookingPlate.class),
							new EventSink[] {
									new EventSink(CookingPlateElectricityModel.URI,
											DecreaseCookingPlate.class)
							});
					
					// Lamp
					connections.put(
							new EventSource(LampUserModel.URI, SwitchOnLamp.class),
							new EventSink[] {
									new EventSink(LampElectricityModel.URI,
												  SwitchOnLamp.class)
							});
					connections.put(
							new EventSource(LampUserModel.URI, SwitchOffLamp.class),
							new EventSink[] {
									new EventSink(LampElectricityModel.URI,
												  SwitchOffLamp.class)
							});
					
					connections.put(
							new EventSource(LampUserModel.URI, IncreaseLamp.class),
							new EventSink[] {
									new EventSink(LampElectricityModel.URI,
												  IncreaseLamp.class)
							});
					connections.put(
							new EventSource(LampUserModel.URI, DecreaseLamp.class),
							new EventSink[] {
									new EventSink(LampElectricityModel.URI,
												  DecreaseLamp.class)
							});
					
					// Air Conditioning
					connections.put(
							new EventSource(AirConditioningUnitTesterModel.URI,
									SetPowerAirConditioning.class),
							new EventSink[] {
									new EventSink(AirConditioningElectricityModel.URI,
											SetPowerAirConditioning.class)
							});
					connections.put(
							new EventSource(AirConditioningUnitTesterModel.URI,
									SwitchOnAirConditioning.class),
							new EventSink[] {
									new EventSink(AirConditioningElectricityModel.URI,
											SwitchOnAirConditioning.class)
							});
					connections.put(
							new EventSource(AirConditioningUnitTesterModel.URI,
									SwitchOffAirConditioning.class),
							new EventSink[] {
									new EventSink(AirConditioningElectricityModel.URI,
											SwitchOffAirConditioning.class),
									new EventSink(AirConditioningTemperatureModel.URI,
											SwitchOffAirConditioning.class)
							});
					connections.put(
							new EventSource(AirConditioningUnitTesterModel.URI, Cool.class),
							new EventSink[] {
									new EventSink(AirConditioningElectricityModel.URI,
											Cool.class),
									new EventSink(AirConditioningTemperatureModel.URI,
											Cool.class)
							});
					connections.put(
							new EventSource(AirConditioningUnitTesterModel.URI, DoNotCool.class),
							new EventSink[] {
									new EventSink(AirConditioningElectricityModel.URI,
											DoNotCool.class),
									new EventSink(AirConditioningTemperatureModel.URI,
											DoNotCool.class)
							});
					
					// Fridge 
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
							new EventSource(FridgeUnitTesterModel.URI, equipments.Fridge.mil.events.Cool.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											equipments.Fridge.mil.events.Cool.class),
									new EventSink(FridgeTemperatureModel.URI,
											equipments.Fridge.mil.events.Cool.class)
							});
					connections.put(
							new EventSource(FridgeUnitTesterModel.URI, equipments.Fridge.mil.events.DoNotCool.class),
							new EventSink[] {
									new EventSink(FridgeElectricityModel.URI,
											equipments.Fridge.mil.events.DoNotCool.class),
									new EventSink(FridgeTemperatureModel.URI,
											equipments.Fridge.mil.events.DoNotCool.class)
							});


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
							
							// Air Conditioning
							bindings.put(new VariableSource("externalTemperature",
									Double.class,
									ExternalTemperatureModel.URI),
									new VariableSink[] {
											new VariableSink("externalTemperature",
													Double.class,
													AirConditioningTemperatureModel.URI)
							});
							bindings.put(new VariableSource("currentCoolingPower",
									Double.class,
									AirConditioningElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentCoolingPower",
													Double.class,
													AirConditioningTemperatureModel.URI)
							});
							
							// Fridge 
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

							// bindings between models and the Electric Meter model
							bindings.put(
									new VariableSource("currentPowerProducedSolarPanel",
											Double.class,
											SolarPanelElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentPowerProducedSolarPanel",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentPowerProducedPetrolGenerator",
											Double.class,
											PetrolGeneratorElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentPowerProducedPetrolGenerator",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentTotalPowerProduced",
											Double.class,
											ElectricMeterElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentTotalPowerProduced",
													Double.class,
													BatteryElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentTotalPowerConsumed",
											Double.class,
											ElectricMeterElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentTotalPowerConsumed",
													Double.class,
													BatteryElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											CookingPlateElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentCookingPlateIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											LampElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentLampIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											AirConditioningElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentAirConditioningIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});
							bindings.put(
									new VariableSource("currentIntensity",
											Double.class,
											FridgeElectricityModel.URI),
									new VariableSink[] {
											new VariableSink("currentFridgeIntensity",
													Double.class,
													ElectricMeterElectricityModel.URI)
									});

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
							se.doStandAloneSimulation(0.0, 12);
							System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
