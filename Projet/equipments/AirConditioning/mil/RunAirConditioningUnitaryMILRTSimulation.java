package equipments.AirConditioning.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunHairDryerUnitaryMILRTSimulation</code>
 *
 * <p><strong>Description</strong></p>
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
 * <p>Created on : 2023-11-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunAirConditioningUnitaryMILRTSimulation
{
	public static final double			ACCELERATION_FACTOR = 1800.0;

	public static void main(String[] args)
	{
		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					AirConditioningStateModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							AirConditioningStateModel.class,
							AirConditioningStateModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			// the air conditioning models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					AirConditioningElectricityModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							AirConditioningElectricityModel.class,
							AirConditioningElectricityModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					AirConditioningTemperatureModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							AirConditioningTemperatureModel.class,
							AirConditioningTemperatureModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.MIL_RT_URI,
					RTAtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));
			// the air conditioning unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					AirConditioningUnitTesterModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							AirConditioningUnitTesterModel.class,
							AirConditioningUnitTesterModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							ACCELERATION_FACTOR));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(AirConditioningStateModel.MIL_RT_URI);
			submodels.add(AirConditioningElectricityModel.MIL_RT_URI);
			submodels.add(AirConditioningTemperatureModel.MIL_RT_URI);
			submodels.add(ExternalTemperatureModel.MIL_RT_URI);
			submodels.add(AirConditioningUnitTesterModel.MIL_RT_URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
									SetPowerAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_RT_URI,
										  SetPowerAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
									SwitchOnAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_RT_URI,
										  SwitchOnAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
									SwitchOffAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_RT_URI,
										  SwitchOffAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
									Cool.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_RT_URI,
										  Cool.class)
					});
			connections.put(
					new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
									DoNotCool.class),
					new EventSink[] {
							new EventSink(AirConditioningStateModel.MIL_RT_URI,
										  DoNotCool.class)
					});

			connections.put(
					new EventSource(AirConditioningStateModel.MIL_RT_URI,
									SetPowerAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
										  SetPowerAirConditioning.class),
							new EventSink(AirConditioningTemperatureModel.MIL_RT_URI,
										  SetPowerAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_RT_URI,
									SwitchOnAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
										  SwitchOnAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_RT_URI,
									SwitchOffAirConditioning.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
										  SwitchOffAirConditioning.class),
							new EventSink(AirConditioningTemperatureModel.MIL_RT_URI,
										  SwitchOffAirConditioning.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_RT_URI, Cool.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
										  Cool.class),
							new EventSink(AirConditioningTemperatureModel.MIL_RT_URI,
										  Cool.class)
					});
			connections.put(
					new EventSource(AirConditioningStateModel.MIL_RT_URI,
									DoNotCool.class),
					new EventSink[] {
							new EventSink(AirConditioningElectricityModel.MIL_RT_URI,
										  DoNotCool.class),
							new EventSink(AirConditioningTemperatureModel.MIL_RT_URI,
										  DoNotCool.class)
					});

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource(
									"externalTemperature",
									Double.class,
									ExternalTemperatureModel.MIL_RT_URI),
						 new VariableSink[] {
								 new VariableSink(
										 "externalTemperature",
										 Double.class,
										 AirConditioningTemperatureModel.MIL_RT_URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					AirConditioningCoupledModel.MIL_RT_URI,
					new RTCoupledHIOA_Descriptor(
							AirConditioningCoupledModel.class,
							AirConditioningCoupledModel.MIL_RT_URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings,
							ACCELERATION_FACTOR));

			// simulation architecture
			ArchitectureI architecture =
					new RTArchitecture(
							AirConditioningCoupledModel.MIL_RT_URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			long start = System.currentTimeMillis() + 100;
			double simulationDuration = 24.1;
			se.startRTSimulation(start, 0.0, simulationDuration);
			long sleepTime =
				(long)(TimeUnit.HOURS.toMillis(1) *
								(simulationDuration/ACCELERATION_FACTOR));
			Thread.sleep(sleepTime + 10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
