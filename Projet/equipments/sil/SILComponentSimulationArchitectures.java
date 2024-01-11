package equipments.sil;

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

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.mil.AirConditioningCoupledModel;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import equipments.Lamp.Lamp;
import equipments.Lamp.mil.LampStateModel;
import equipments.Lamp.mil.events.DecreaseLamp;
import equipments.Lamp.mil.events.IncreaseLamp;
import equipments.Lamp.mil.events.SwitchOffLamp;
import equipments.Lamp.mil.events.SwitchOnLamp;
import equipments.meter.ElectricMeter;
import equipments.meter.mil.ElectricMeterCoupledModel;
import equipments.mil.GlobalCoupledModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import global.GlobalCoordinator;
import global.GlobalSupervisor;

// -----------------------------------------------------------------------------
/**
 * The class <code>SILComponentSimulationArchitectures</code> defines the global
 * SIL component simulation architecture for the whole HEM application.
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
 * <p>Created on : 2023-11-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	SILComponentSimulationArchitectures
{
	/**
	 * create the global SIL real time component simulation architecture for the
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
									createSILComponentSimulationArchitectures(
		String architectureURI,
		double accelerationFactor
		) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();
		
//		atomicModelDescriptors.put(
//				LampStateModel.SIL_URI,
//				RTComponentAtomicModelDescriptor.create(
//						LampStateModel.SIL_URI,
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SwitchOnLamp.class,
//							SwitchOffLamp.class,
//							DecreaseLamp.class,
//							IncreaseLamp.class
//						},
//						(Class<? extends EventI>[]) new Class<?>[]{
//							SwitchOnLamp.class,
//							SwitchOffLamp.class,
//							DecreaseLamp.class,
//							IncreaseLamp.class
//						},
//						TimeUnit.HOURS,
//						Lamp.REFLECTION_INBOUND_PORT_URI
//						));

		atomicModelDescriptors.put(
				AirConditioningCoupledModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						AirConditioningCoupledModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SetPowerAirConditioning.class,
							SwitchOnAirConditioning.class,
							SwitchOffAirConditioning.class,
							Cool.class,
							DoNotCool.class},
						TimeUnit.HOURS,
						AirConditioning.REFLECTION_INBOUND_PORT_URI));

		// The electric meter also has a SIL simulation model
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
//							SwitchOnLamp.class,
//							SwitchOffLamp.class,
//							DecreaseLamp.class,
//							IncreaseLamp.class,
							SetPowerAirConditioning.class,
							SwitchOnAirConditioning.class,
							SwitchOffAirConditioning.class,
							Cool.class,
							DoNotCool.class},
						(Class<? extends EventI>[]) new Class<?>[]{},
						TimeUnit.HOURS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
//		submodels.add(LampStateModel.SIL_URI);
		submodels.add(AirConditioningCoupledModel.SIL_URI);
		submodels.add(ElectricMeterCoupledModel.SIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		// first, the events going from the cooking plate to the electric meter
		// LAMP connections
//		connections.put(
//				new EventSource(LampStateModel.SIL_URI,
//								SwitchOnLamp.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  SwitchOnLamp.class)
//				});
//			connections.put(
//				new EventSource(LampStateModel.SIL_URI,
//								SwitchOffLamp.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  SwitchOffLamp.class)
//				});
//			connections.put(
//				new EventSource(LampStateModel.SIL_URI,
//								DecreaseLamp.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  DecreaseLamp.class)
//				});
//			connections.put(
//				new EventSource(LampStateModel.SIL_URI,
//								IncreaseLamp.class),
//				new EventSink[] {
//					new EventSink(ElectricMeterCoupledModel.SIL_URI,
//								  IncreaseLamp.class)
//				});

		// second, the events going from the AirConditioning to the electric meter
		connections.put(
				new EventSource(AirConditioningCoupledModel.SIL_URI,
								SetPowerAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.SIL_URI,
								  SetPowerAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.SIL_URI,
								SwitchOnAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.SIL_URI,
								  SwitchOnAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.SIL_URI,
								SwitchOffAirConditioning.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.SIL_URI,
								  SwitchOffAirConditioning.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.SIL_URI,
								Cool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.SIL_URI,
								  Cool.class)
				});
		connections.put(
				new EventSource(AirConditioningCoupledModel.SIL_URI,
								DoNotCool.class),
				new EventSink[] {
					new EventSink(ElectricMeterCoupledModel.SIL_URI,
								  DoNotCool.class)
				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				GlobalCoupledModel.SIL_URI,
				RTComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						GlobalCoupledModel.SIL_URI,
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
						GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI,
						GlobalCoupledModel.SIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
