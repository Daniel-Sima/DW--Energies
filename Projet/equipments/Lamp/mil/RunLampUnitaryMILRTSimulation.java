package equipments.Lamp.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunLampUnitaryMILRTSimulation</code> tests the MIL
 * real time simulation architecture for the lamp by executing the
 * simulation in a stand alone way.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * @author walte
 */
public class			RunLampUnitaryMILRTSimulation
{
	public static final double			ACCELERATION_FACTOR = 3600.0;

	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// the hair dyer model simulating its electricity consumption, an
			// atomic HIOA model hence we use an AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					LampElectricityModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							LampElectricityModel.class,
							LampElectricityModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			// for atomic models, we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					LampStateModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							LampStateModel.class,
							LampStateModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			atomicModelDescriptors.put(
					LampUserModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							LampUserModel.class,
							LampUserModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(LampElectricityModel.MIL_RT_URI);
			submodels.add(LampStateModel.MIL_RT_URI);
			submodels.add(LampUserModel.MIL_RT_URI);

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
								IncreaseLamp.class),
				new EventSink[] {
					new EventSink(LampStateModel.MIL_RT_URI,
								  IncreaseLamp.class)
				});
			connections.put(
				new EventSource(LampUserModel.MIL_RT_URI,
								DecreaseLamp.class),
				new EventSink[] {
					new EventSink(LampStateModel.MIL_RT_URI,
								  DecreaseLamp.class)
				});

			connections.put(
				new EventSource(LampStateModel.MIL_RT_URI,
								SwitchOnLamp.class),
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_RT_URI,
								  SwitchOnLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_RT_URI,
								SwitchOffLamp.class),
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_RT_URI,
								  SwitchOffLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_RT_URI,
								IncreaseLamp.class),
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_RT_URI,
								  IncreaseLamp.class)
				});
			connections.put(
				new EventSource(LampStateModel.MIL_RT_URI,
								DecreaseLamp.class),
				new EventSink[] {
					new EventSink(LampElectricityModel.MIL_RT_URI,
								  DecreaseLamp.class)
				});

			// coupled model descriptor
			coupledModelDescriptors.put(
					LampCoupledModel.MIL_RT_URI,
					new RTCoupledModelDescriptor(
							LampCoupledModel.class,
							LampCoupledModel.MIL_RT_URI,
							submodels,
							null,
							null,
							connections,
							null,
							ACCELERATION_FACTOR));

			// simulation architecture
			ArchitectureI architecture =
					new RTArchitecture(
							LampCoupledModel.MIL_RT_URI,
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
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			long start = System.currentTimeMillis() + 100;
			double simulationDuration = 24.0;
			se.startRTSimulation(start, 0.0, simulationDuration);
			long executionDuration =					
				new Double(TimeUnit.HOURS.toMillis(1)*
									(simulationDuration/ACCELERATION_FACTOR)).
																	longValue();
			Thread.sleep(executionDuration + 2000L);
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
