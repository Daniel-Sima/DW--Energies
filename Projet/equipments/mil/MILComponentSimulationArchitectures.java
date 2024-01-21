package equipments.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.mil.AirConditioningCoupledModel;
import equipments.AirConditioning.mil.AirConditioningElectricityModel;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.Lamp.Lamp;
import equipments.Lamp.LampUser;
import equipments.Lamp.mil.LampStateModel;
import equipments.Lamp.mil.LampUserModel;
import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import equipments.meter.ElectricMeter;
import equipments.meter.mil.ElectricMeterCoupledModel;
import equipments.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import global.GlobalCoordinator;
import global.GlobalSupervisor;

public abstract class MILComponentSimulationArchitectures {

	/**
	 * create the global MIL component simulation architecture for the HEM
	 * application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of the component model architecture to be created.
	 * @return					the global MIL simulation  architecture for the HEM application.
	 * @throws Exception		<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture createMILComponentSimulationArchitectures(
				String architectureURI
	) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
									new HashMap<>();

		atomicModelDescriptors.put(
				LampStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						LampStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						TimeUnit.HOURS,
						Lamp.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				LampUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						LampUserModel.MIL_URI,
						null,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						TimeUnit.HOURS,
						LampUser.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				AirConditioningCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						AirConditioningCoupledModel.MIL_URI,
						null,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerAirConditioning.class,
							SwitchOnAirConditioning.class,
							SwitchOffAirConditioning.class,
							Cool.class,
							DoNotCool.class},
						TimeUnit.HOURS,
						AirConditioning.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class,
							SetPowerAirConditioning.class,
							SwitchOnAirConditioning.class,
							SwitchOffAirConditioning.class,
							Cool.class,
							DoNotCool.class
							},
						null,
						TimeUnit.HOURS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));
		
		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();
		
		// TODO
		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(LampStateModel.MIL_URI);
		submodels.add(LampUserModel.MIL_URI);
		submodels.add(AirConditioningCoupledModel.MIL_URI);
		submodels.add(ElectricMeterCoupledModel.MIL_URI);
		

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
									
		// first, the events going from the lamp to the electric meter
		
	    // LAMP connections
		connections.put(
				new EventSource(LampUserModel.MIL_URI,
								SwitchOnLamp.class),
				new EventSink[] {
					new EventSink(LampStateModel.MIL_URI,
								  SwitchOnLamp.class)
				});
			connections.put(
				new EventSource(LampUserModel.MIL_URI,
								SwitchOffLamp.class),
				new EventSink[] {
					new EventSink(LampStateModel.MIL_URI,
								  SwitchOffLamp.class)
				});
			connections.put(
				new EventSource(LampUserModel.MIL_URI,
								DecreaseLamp.class),
					new EventSink[] {
					new EventSink(LampStateModel.MIL_URI,
							DecreaseLamp.class)
				});
			connections.put(
				new EventSource(LampUserModel.MIL_URI,
								IncreaseLamp.class),
				new EventSink[] {
					new EventSink(LampStateModel.MIL_URI,
							IncreaseLamp.class)
				});

			connections.put(
				new EventSource(LampStateModel.MIL_URI,
								SwitchOnLamp.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  SwitchOnLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_URI,
								SwitchOffLamp.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  SwitchOffLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_URI,
						DecreaseLamp.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
							DecreaseLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_URI,
						IncreaseLamp.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
							IncreaseLamp.class)
				});
									
		// AIR CONDITIONING conditions
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_URI,
								SetPowerAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  SetPowerAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_URI,
								SwitchOnAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  SwitchOnAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_URI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  SwitchOffAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_URI,
								Cool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  Cool.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_URI,
								DoNotCool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_URI,
								  DoNotCool.class)
				});
		
		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.MIL_URI,
				ComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.MIL_URI,
						submodels,
						null,
						null,
						connections,
						null,
						GlobalCoordinator.REFLECTION_INBOUND_PORT_URI,
						CoordinatorPlugin.class,
						null));

		ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
	
	/**
	 * create the global MIL real time component simulation architecture for the
	 * HEM application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI of the component model architecture to be created.
	 * @param accelerationFactor	acceleration factor for this run.
	 * @return						the global MIL real time simulation  architecture for the HEM application.
	 * @throws Exception			<i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static ComponentModelArchitecture
									createMILRTComponentSimulationArchitectures(
		String architectureURI,
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				LampStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						LampStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						TimeUnit.HOURS,
						Lamp.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				LampUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						LampUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						TimeUnit.HOURS,
						LampUser.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				AirConditioningCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						AirConditioningCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerAirConditioning.class,
							SwitchOnAirConditioning.class,
							SwitchOffAirConditioning.class,
							Cool.class,
							DoNotCool.class},
						null,
						TimeUnit.HOURS,
						AirConditioning.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnLamp.class,
							SwitchOffLamp.class,
							DecreaseLamp.class,
							IncreaseLamp.class},
						(Class<? extends EventI>[]) new Class<?>[]{},
						TimeUnit.HOURS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(LampStateModel.MIL_RT_URI);
		submodels.add(LampUserModel.MIL_RT_URI);
		submodels.add(AirConditioningElectricityModel.MIL_RT_URI);
		submodels.add(ElectricMeterElectricityModel.MIL_RT_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
		connections.put(
			new EventSource(LampUserModel.MIL_RT_URI,
							SwitchOnLamp.class),
			new EventSink[] {
				new EventSink(LampStateModel.MIL_RT_URI,
							  SwitchOnLamp.class)
			});
		connections.put(
			new EventSource(LampUserModel.MIL_RT_URI,
							SwitchOffLamp.class),
			new EventSink[] {
				new EventSink(LampStateModel.MIL_RT_URI,
							  SwitchOffLamp.class)
			});
		connections.put(
			new EventSource(LampUserModel.MIL_RT_URI,
							DecreaseLamp.class),
				new EventSink[] {
				new EventSink(LampStateModel.MIL_RT_URI,
							  DecreaseLamp.class)
			});
		connections.put(
			new EventSource(LampUserModel.MIL_RT_URI,
							IncreaseLamp.class),
			new EventSink[] {
				new EventSink(LampStateModel.MIL_RT_URI,
							  IncreaseLamp.class)
			});
//
		connections.put(
			new EventSource(LampStateModel.MIL_RT_URI,
							SwitchOnLamp.class),
			new EventSink[] {
				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
							  SwitchOnLamp.class)
			});
		connections.put(
			new EventSource(LampStateModel.MIL_RT_URI,
							SwitchOffLamp.class),
			new EventSink[] {
				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
							  SwitchOffLamp.class)
			});
		connections.put(
			new EventSource(LampStateModel.MIL_RT_URI,
							DecreaseLamp.class),
			new EventSink[] {
				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
							  DecreaseLamp.class)
			});
		connections.put(
			new EventSource(LampStateModel.MIL_RT_URI,
							IncreaseLamp.class),
			new EventSink[] {
				new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
							  IncreaseLamp.class)
			});

		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_RT_URI,
								SetPowerAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
								  SetPowerAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_RT_URI,
								SwitchOnAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
								  SwitchOnAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_RT_URI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
								  SwitchOffAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_RT_URI,
								Cool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
								  Cool.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.MIL_RT_URI,
								DoNotCool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
								  DoNotCool.class)
				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.MIL_RT_URI,
				RTComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.MIL_RT_URI,
						submodels,
						null,
						null,
						connections,
						null,
						GlobalCoordinator.REFLECTION_INBOUND_PORT_URI,
						CoordinatorPlugin.class,
						null,
						accelerationFactor));

		ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						GlobalSupervisor.MIL_RT_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
