package mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.CookingPlate;
import equipments.CookingPlate.CookingPlateTester;
import equipments.CookingPlate.mil.CookingPlateStateModel;
import equipments.CookingPlate.mil.CookingPlateUserModel;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.meter.ElectricMeter;
import equipments.meter.mil.ElectricMeterCoupledModel;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
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

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>MILComponentSimulationArchitectures</code> defines the global
 * MIL component simulation architecture for the whole HEM application.
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
 * <p>Created on : 2024-10-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
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
	public static ComponentModelArchitecture
	createMILComponentSimulationArchitectures(
			String architectureURI
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		atomicModelDescriptors.put(
				CookingPlateStateModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						CookingPlateStateModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnCookingPlate.class,
								SwitchOffCookingPlate.class,
								DecreaseCookingPlate.class,
								IncreaseCookingPlate.class},
						TimeUnit.HOURS,
						CookingPlate.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				CookingPlateUserModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						CookingPlateUserModel.MIL_URI,
						null,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						TimeUnit.HOURS,
						CookingPlateTester.REFLECTION_INBOUND_PORT_URI));
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						null,
						TimeUnit.HOURS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(CookingPlateStateModel.MIL_URI);
		submodels.add(CookingPlateUserModel.MIL_URI);
		submodels.add(ElectricMeterCoupledModel.MIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_URI,
								SwitchOnCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_URI,
										SwitchOnCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_URI,
								SwitchOffCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_URI,
										SwitchOffCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_URI,
								DecreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_URI,
										DecreaseCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_URI,
								IncreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_URI,
										IncreaseCookingPlate.class)
						});

				connections.put(
						new EventSource(CookingPlateStateModel.MIL_URI,
								SwitchOnCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOnCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_URI,
								SwitchOffCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										SwitchOffCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_URI,
								DecreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										DecreaseCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_URI,
								IncreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_URI,
										IncreaseCookingPlate.class)
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
	/***********************************************************************************/
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
				CookingPlateStateModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						CookingPlateStateModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnCookingPlate.class,
								SwitchOffCookingPlate.class,
								DecreaseCookingPlate.class,
								IncreaseCookingPlate.class},
						TimeUnit.HOURS,
						CookingPlate.REFLECTION_INBOUND_PORT_URI
						));
		atomicModelDescriptors.put(
				CookingPlateUserModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						CookingPlateUserModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{},
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						TimeUnit.HOURS,
						CookingPlateTester.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.MIL_RT_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.MIL_RT_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,
							SwitchOffCookingPlate.class,
							DecreaseCookingPlate.class,
							IncreaseCookingPlate.class},
						(Class<? extends EventI>[]) new Class<?>[]{},
						TimeUnit.HOURS,
						ElectricMeter.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(CookingPlateStateModel.MIL_RT_URI);
		submodels.add(CookingPlateUserModel.MIL_RT_URI);
		submodels.add(ElectricMeterCoupledModel.MIL_RT_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_RT_URI,
								SwitchOnCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_RT_URI,
										SwitchOnCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_RT_URI,
								SwitchOffCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_RT_URI,
										SwitchOffCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_RT_URI,
								DecreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_RT_URI,
										DecreaseCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateUserModel.MIL_RT_URI,
								IncreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(CookingPlateStateModel.MIL_RT_URI,
										IncreaseCookingPlate.class)
						});

				connections.put(
						new EventSource(CookingPlateStateModel.MIL_RT_URI,
								SwitchOnCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOnCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_RT_URI,
								SwitchOffCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										SwitchOffCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_RT_URI,
								DecreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										DecreaseCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.MIL_RT_URI,
								IncreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.MIL_RT_URI,
										IncreaseCookingPlate.class)
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
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
