package fr.sorbonne_u.components.hem2023e3.equipments.heater.mil;

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
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
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

// -----------------------------------------------------------------------------
/**
 * The class <code>MILSimulationArchitectures</code> defines the local
 * MIL simulation architectures pertaining to the hair dryer components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class provides three static methods that create the local MIL, MIL
 * real time and SIL simulation architectures for the {@code Heater}.
 * </p>
 * <p>
 * These architectures are local in the sense that they define the simulators
 * that are internal to the component. These are meant to be integrated in a
 * global component simulation architecture where they are seen as atomic
 * models that are composed into coupled models that will reside in coordinator
 * components.
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
 * <p>Created on : 2023-11-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	MILSimulationArchitectures
{
	/**
	 * create the local MIL simulation architecture for the {@code HairDryer}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code HairDryer} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture	createHeaterMILArchitecture()
	throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				HeaterStateModel.MIL_URI,
				AtomicModelDescriptor.create(
						HeaterStateModel.class,
						HeaterStateModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		// the heater models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				HeaterTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						HeaterTemperatureModel.class,
						HeaterTemperatureModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		atomicModelDescriptors.put(
				ExternalTemperatureModel.MIL_URI,
				AtomicHIOA_Descriptor.create(
						ExternalTemperatureModel.class,
						ExternalTemperatureModel.MIL_URI,
						TimeUnit.HOURS,
						null));
		// the heater unit tester model only exchanges event, an
		// atomic model hence we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				HeaterUnitTesterModel.MIL_URI,
				AtomicModelDescriptor.create(
						HeaterUnitTesterModel.class,
						HeaterUnitTesterModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(HeaterStateModel.MIL_URI);
		submodels.add(HeaterTemperatureModel.MIL_URI);
		submodels.add(ExternalTemperatureModel.MIL_URI);
		submodels.add(HeaterUnitTesterModel.MIL_URI);
		
		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_URI,
						SetPowerHeater.class),
				new EventSink[] {
						new EventSink(HeaterStateModel.MIL_URI,
									  SetPowerHeater.class)
				});
		connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_URI,
								SwitchOnHeater.class),
				new EventSink[] {
						new EventSink(HeaterStateModel.MIL_URI,
									  SwitchOnHeater.class)
				});
		connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_URI,
								SwitchOffHeater.class),
				new EventSink[] {
						new EventSink(HeaterStateModel.MIL_URI,
									  SwitchOffHeater.class)
				});
		connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_URI, Heat.class),
				new EventSink[] {
						new EventSink(HeaterStateModel.MIL_URI, Heat.class)
				});
		connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_URI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(HeaterStateModel.MIL_URI, DoNotHeat.class)
				});

		connections.put(
				new EventSource(HeaterStateModel.MIL_URI,
								SetPowerHeater.class),
				new EventSink[] {
						new EventSink(HeaterTemperatureModel.MIL_URI,
									  SetPowerHeater.class)
				});
		connections.put(
				new EventSource(HeaterStateModel.MIL_URI,
								SwitchOffHeater.class),
				new EventSink[] {
						new EventSink(HeaterTemperatureModel.MIL_URI,
									  SwitchOffHeater.class)
				});
		connections.put(
				new EventSource(HeaterStateModel.MIL_URI, Heat.class),
				new EventSink[] {
						new EventSink(HeaterTemperatureModel.MIL_URI,
									  Heat.class)
				});
		connections.put(
				new EventSource(HeaterStateModel.MIL_URI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(HeaterTemperatureModel.MIL_URI,
									  DoNotHeat.class)
				});

		Map<Class<? extends EventI>, ReexportedEvent> reexported =
															new HashMap<>();

		reexported.put(SetPowerHeater.class,
					   new ReexportedEvent(HeaterStateModel.MIL_URI,
							   			   SetPowerHeater.class));
		reexported.put(SwitchOnHeater.class,
					   new ReexportedEvent(HeaterStateModel.MIL_URI,
							   			   SwitchOnHeater.class));
		reexported.put(SwitchOffHeater.class,
					   new ReexportedEvent(HeaterStateModel.MIL_URI,
							   			   SwitchOffHeater.class));
		reexported.put(Heat.class,
					   new ReexportedEvent(HeaterStateModel.MIL_URI,
							   			   Heat.class));
		reexported.put(DoNotHeat.class,
					   new ReexportedEvent(HeaterStateModel.MIL_URI,
							   			   DoNotHeat.class));

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										ExternalTemperatureModel.MIL_URI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  HeaterTemperatureModel.MIL_URI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				HeaterCoupledModel.MIL_URI,
				new CoupledHIOA_Descriptor(
						HeaterCoupledModel.class,
						HeaterCoupledModel.MIL_URI,
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
						HeaterCoupledModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code Heater} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentExecutionType	current execution type for the next run.
	 * @param accelerationFactor	acceleration factor used in this run.
	 * @return						the local MIL real time simulation architecture for the {@code HairDryer} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createHeaterRTArchitecture(
		ExecutionType currentExecutionType,
		double accelerationFactor
		) throws Exception
	{
		String heaterStateModelURI = null;
		String heaterTemperatureModelURI = null;
		String externalTemperatureModelURI = null;
		String heaterCoupledModelURI = null;
		switch (currentExecutionType) {
		case MIL_RT_SIMULATION:
			heaterStateModelURI = HeaterStateModel.MIL_RT_URI;
			heaterTemperatureModelURI = HeaterTemperatureModel.MIL_RT_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.MIL_RT_URI;
			heaterCoupledModelURI = HeaterCoupledModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			heaterStateModelURI = HeaterStateModel.SIL_URI;
			heaterTemperatureModelURI = HeaterTemperatureModel.SIL_URI;
			externalTemperatureModelURI = ExternalTemperatureModel.SIL_URI;
			heaterCoupledModelURI = HeaterCoupledModel.SIL_URI;
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
				heaterStateModelURI,
				RTAtomicModelDescriptor.create(
						HeaterStateModel.class,
						heaterStateModelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));
		// the heater models simulating its electricity consumption, its
		// temperatures and the external temperature are atomic HIOA models
		// hence we use an AtomicHIOA_Descriptor(s)
		atomicModelDescriptors.put(
				heaterTemperatureModelURI,
				RTAtomicHIOA_Descriptor.create(
						HeaterTemperatureModel.class,
						heaterTemperatureModelURI,
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
		// the heater unit tester model only exchanges event, an
		// atomic model hence we use an AtomicModelDescriptor
		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			atomicModelDescriptors.put(
					HeaterUnitTesterModel.MIL_RT_URI,
					RTAtomicModelDescriptor.create(
							HeaterUnitTesterModel.class,
							HeaterUnitTesterModel.MIL_RT_URI,
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
		submodels.add(heaterStateModelURI);
		submodels.add(heaterTemperatureModelURI);
		submodels.add(externalTemperatureModelURI);
		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			submodels.add(HeaterUnitTesterModel.MIL_RT_URI);
		}

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		if (currentExecutionType == ExecutionType.MIL_RT_SIMULATION) {
			connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
						SetPowerHeater.class),
				new EventSink[] {
						new EventSink(heaterStateModelURI,
									  SetPowerHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								SwitchOnHeater.class),
				new EventSink[] {
						new EventSink(heaterStateModelURI,
									  SwitchOnHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								SwitchOffHeater.class),
				new EventSink[] {
						new EventSink(heaterStateModelURI,
									  SwitchOffHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_RT_URI, Heat.class),
				new EventSink[] {
						new EventSink(heaterStateModelURI, Heat.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.MIL_RT_URI,
								DoNotHeat.class),
				new EventSink[] {
						new EventSink(heaterStateModelURI,
									  DoNotHeat.class)
				});
		}

		connections.put(
				new EventSource(heaterStateModelURI,
								SetPowerHeater.class),
				new EventSink[] {
						new EventSink(heaterTemperatureModelURI,
									  SetPowerHeater.class)
				});
		connections.put(
				new EventSource(heaterStateModelURI,
								SwitchOffHeater.class),
				new EventSink[] {
						new EventSink(heaterTemperatureModelURI,
									  SwitchOffHeater.class)
				});
		connections.put(
				new EventSource(heaterStateModelURI, Heat.class),
				new EventSink[] {
						new EventSink(heaterTemperatureModelURI,
									  Heat.class)
				});
		connections.put(
				new EventSource(heaterStateModelURI, DoNotHeat.class),
				new EventSink[] {
						new EventSink(heaterTemperatureModelURI,
									  DoNotHeat.class)
				});

		Map<Class<? extends EventI>, ReexportedEvent> reexported =
															new HashMap<>();

		reexported.put(SetPowerHeater.class,
					   new ReexportedEvent(heaterStateModelURI,
							   			   SetPowerHeater.class));
		reexported.put(SwitchOnHeater.class,
					   new ReexportedEvent(heaterStateModelURI,
							   			   SwitchOnHeater.class));
		reexported.put(SwitchOffHeater.class,
					   new ReexportedEvent(heaterStateModelURI,
							   			   SwitchOffHeater.class));
		reexported.put(Heat.class,
					   new ReexportedEvent(heaterStateModelURI,
							   			   Heat.class));
		reexported.put(DoNotHeat.class,
					   new ReexportedEvent(heaterStateModelURI,
							   			   DoNotHeat.class));

		// variable bindings between exporting and importing models
		Map<VariableSource,VariableSink[]> bindings =
							new HashMap<VariableSource,VariableSink[]>();

		bindings.put(new VariableSource("externalTemperature",
										Double.class,
										externalTemperatureModelURI),
					 new VariableSink[] {
							 new VariableSink("externalTemperature",
									 		  Double.class,
									 		  heaterTemperatureModelURI)
					 });

		// coupled model descriptor
		coupledModelDescriptors.put(
				heaterCoupledModelURI,
				new RTCoupledHIOA_Descriptor(
						HeaterCoupledModel.class,
						heaterCoupledModelURI,
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
						heaterCoupledModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
