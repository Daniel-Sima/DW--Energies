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
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.HIOA_Composer;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import utils.ExecutionType;

public abstract class MILSimulationArchitectures 
{

	public static Architecture createAirConditioningMILArchitecture()
	throws Exception
	{
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
				new EventSource(AirConditioningUnitTesterModel.MIL_URI, Cool.class),
				new EventSink[] {
						new EventSink(AirConditioningStateModel.MIL_URI, Cool.class)
				});
		connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_URI, DoNotCool.class),
				new EventSink[] {
						new EventSink(AirConditioningStateModel.MIL_URI, DoNotCool.class)
				});

		connections.put(
				new EventSource(AirConditioningStateModel.MIL_URI,
								SetPowerAirConditioning.class),
				new EventSink[] {
						new EventSink(AirConditioningTemperatureModel.MIL_URI,
									  SetPowerAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningStateModel.MIL_URI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
						new EventSink(AirConditioningTemperatureModel.MIL_URI,
									  SwitchOffAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningStateModel.MIL_URI, Cool.class),
				new EventSink[] {
						new EventSink(AirConditioningTemperatureModel.MIL_URI,
									  Cool.class)
				});
		connections.put(
				new EventSource(AirConditioningStateModel.MIL_URI, DoNotCool.class),
				new EventSink[] {
						new EventSink(AirConditioningTemperatureModel.MIL_URI,
									  DoNotCool.class)
				});					

		Map<Class<? extends EventI>, ReexportedEvent> reexported =
				new HashMap<>();

		reexported.put(SetPowerAirConditioning.class,
				   new ReexportedEvent(AirConditioningStateModel.MIL_URI,
						   			   SetPowerAirConditioning.class));
		reexported.put(SwitchOnAirConditioning.class,
					   new ReexportedEvent(AirConditioningStateModel.MIL_URI,
							   			   SwitchOnAirConditioning.class));
		reexported.put(SwitchOffAirConditioning.class,
					   new ReexportedEvent(AirConditioningStateModel.MIL_URI,
							   			   SwitchOffAirConditioning.class));
		reexported.put(Cool.class,
					   new ReexportedEvent(AirConditioningStateModel.MIL_URI,
							   			   Cool.class));
		reexported.put(DoNotCool.class,
					   new ReexportedEvent(AirConditioningStateModel.MIL_URI,
							   			   DoNotCool.class));

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
						reexported,
						connections,
						null,
						null,
						null,
						bindings));

		// simulation architecture
		Architecture architecture =
				new Architecture(
						AirConditioningCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
	
	public static Architecture createAirConditioningRTArchitecture(
			ExecutionType currentExecutionType,
			double accelerationFactor)
	throws Exception
	{
		String airConditioningStateModelURI = null;
		String airConditioningTemperatureModelURI = null;
		String externalTemperatureModelURI = null;
		String airConditioningCoupledModelURI = null;
		switch (currentExecutionType) {
		case MIL_RT_SIMULATION:
			airConditioningStateModelURI = AirConditioningStateModel.MIL_RT_URI;
			airConditioningTemperatureModelURI = AirConditioningTemperatureModel.MIL_RT_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.MIL_RT_URI;
			airConditioningCoupledModelURI = AirConditioningCoupledModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			airConditioningStateModelURI = AirConditioningStateModel.SIL_URI;
			airConditioningTemperatureModelURI = AirConditioningTemperatureModel.SIL_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.SIL_URI;
			airConditioningCoupledModelURI = AirConditioningCoupledModel.SIL_URI;
			break;
		default:
			throw new RuntimeException("incorrect executiontype: " +
													currentExecutionType + "!");
		}
		
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				airConditioningStateModelURI,
				RTAtomicModelDescriptor.create(
						AirConditioningStateModel.class,
						airConditioningStateModelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		
		// the air conditioning models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				airConditioningTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						AirConditioningTemperatureModel.class,
						airConditioningTemperatureModelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				externalTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						externalTemperatureModelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		// the air conditioning unit tester model only exchanges event, an
		// atomic model hence we use an AtomicModelDescriptor
		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			atomicModelDescriptors.put(
					AirConditioningUnitTesterModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							AirConditioningUnitTesterModel.class,
							AirConditioningUnitTesterModel.MIL_RT_URI,
							TimeUnit.HOURS,
							null,
							accelerationFactor));
		}

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(airConditioningStateModelURI);
		submodels.add(airConditioningTemperatureModelURI);
		submodels.add(externalTemperatureModelURI);
		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			submodels.add(AirConditioningUnitTesterModel.MIL_RT_URI);
		}

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
						SetPowerAirConditioning.class),
				new EventSink[] {
						new EventSink(airConditioningStateModelURI,
									  SetPowerAirConditioning.class)
				});
			connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
								SwitchOnAirConditioning.class),
				new EventSink[] {
						new EventSink(airConditioningStateModelURI,
									  SwitchOnAirConditioning.class)
				});
			connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
						new EventSink(airConditioningStateModelURI,
									  SwitchOffAirConditioning.class)
				});
			connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI, Cool.class),
				new EventSink[] {
						new EventSink(airConditioningStateModelURI, Cool.class)
				});
			connections.put(
				new EventSource(AirConditioningUnitTesterModel.MIL_RT_URI,
								DoNotCool.class),
				new EventSink[] {
						new EventSink(airConditioningStateModelURI,
									  DoNotCool.class)
				});
		}

		connections.put(
				new EventSource(airConditioningStateModelURI,
								SetPowerAirConditioning.class),
				new EventSink[] {
						new EventSink(airConditioningTemperatureModelURI,
									  SetPowerAirConditioning.class)
				});
		connections.put(
				new EventSource(airConditioningStateModelURI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
						new EventSink(airConditioningTemperatureModelURI,
									  SwitchOffAirConditioning.class)
				});
		connections.put(
				new EventSource(airConditioningStateModelURI, Cool.class),
				new EventSink[] {
						new EventSink(airConditioningTemperatureModelURI,
									  Cool.class)
				});
		connections.put(
				new EventSource(airConditioningStateModelURI, DoNotCool.class),
				new EventSink[] {
						new EventSink(airConditioningTemperatureModelURI,
									  DoNotCool.class)
				});

		Map<Class<? extends EventI>, ReexportedEvent> reexported =
															new HashMap<>();

		reexported.put(SetPowerAirConditioning.class,
					   new ReexportedEvent(airConditioningStateModelURI,
							   			   SetPowerAirConditioning.class));
		reexported.put(SwitchOnAirConditioning.class,
					   new ReexportedEvent(airConditioningStateModelURI,
							   			   SwitchOnAirConditioning.class));
		reexported.put(SwitchOffAirConditioning.class,
					   new ReexportedEvent(airConditioningStateModelURI,
							   			   SwitchOffAirConditioning.class));
		reexported.put(Cool.class,
					   new ReexportedEvent(airConditioningStateModelURI,
							   			   Cool.class));
		reexported.put(DoNotCool.class,
					   new ReexportedEvent(airConditioningStateModelURI,
							   			   DoNotCool.class));

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										externalTemperatureModelURI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  airConditioningTemperatureModelURI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				airConditioningCoupledModelURI,
				new RTCoupledHIOA_Descriptor(
						AirConditioningCoupledModel.class,
						airConditioningCoupledModelURI,
						submodels,
						null,
						reexported,
						connections,
						null,
						null,
						null,
						bindings,
						new HIOA_Composer(),
						accelerationFactor));

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						airConditioningCoupledModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
