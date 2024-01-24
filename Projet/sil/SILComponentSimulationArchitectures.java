package sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import equipments.CookingPlate.CookingPlate;
import equipments.CookingPlate.mil.CookingPlateStateModel;
import equipments.CookingPlate.mil.events.DecreaseCookingPlate;
import equipments.CookingPlate.mil.events.IncreaseCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOffCookingPlate;
import equipments.CookingPlate.mil.events.SwitchOnCookingPlate;
import equipments.meter.ElectricMeter;
import equipments.meter.mil.ElectricMeterCoupledModel;
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
import mil.GlobalCoupledModel;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
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
 * <p>Created on : 2024-01-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public abstract class SILComponentSimulationArchitectures {
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

		atomicModelDescriptors.put(
				CookingPlateStateModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						CookingPlateStateModel.SIL_URI,
						(Class<? extends EventI>[]) new Class<?>[]{
							SwitchOnCookingPlate.class,	// notice that the
							SwitchOffCookingPlate.class,	// imported events of
							DecreaseCookingPlate.class,		// the coupled model
							IncreaseCookingPlate.class},	// appears here
						(Class<? extends EventI>[]) new Class<?>[]{
								SwitchOnCookingPlate.class,	// notice that the
								SwitchOffCookingPlate.class,	// exported events of
								DecreaseCookingPlate.class,		// the coupled model
								IncreaseCookingPlate.class},	// appears here
						TimeUnit.HOURS,
						CookingPlate.REFLECTION_INBOUND_PORT_URI
						));

		// The electric meter also has a SIL simulation model
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.SIL_URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.SIL_URI,
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
		submodels.add(CookingPlateStateModel.SIL_URI);
		submodels.add(ElectricMeterCoupledModel.SIL_URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>();

				// Cooking Plate
				connections.put(
						new EventSource(CookingPlateStateModel.SIL_URI,
								SwitchOnCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOnCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.SIL_URI,
								SwitchOffCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										SwitchOffCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.SIL_URI,
								DecreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										DecreaseCookingPlate.class)
						});
				connections.put(
						new EventSource(CookingPlateStateModel.SIL_URI,
								IncreaseCookingPlate.class),
						new EventSink[] {
								new EventSink(ElectricMeterCoupledModel.SIL_URI,
										IncreaseCookingPlate.class)
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
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

