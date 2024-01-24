package equipments.CookingPlate.mil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import utils.ExecutionType;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>MILSimulationArchitectures</code> defines the local
 * MIL simulation architectures pertaining to the Cooking Plate component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class provides four static methods that create the local MIL and the MIL
 * real time simulation architectures for the {@code CookingPlate} and the
 * {@code CookingPlateTester} components.
 * </p>
 * <p>
 * These architectures are local in the sense that they define the simulators
 * that are internal to the components. These are meant to be integrated in a
 * global component simulation architecture where they are seen as atomic
 * models that are composed into coupled models that will reside in coordinator
 * components.
 * </p>
 * <p>
 * As there is nothing to change to the simulation architectures of the Cooking 
 * Plate for SIL simulations, the MIL real time architectures can be used to
 * execute SIL simulations.
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
 * <p>Created on : 2024-01-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class MILSimulationArchitectures {

	/**
	 * create the local MIL simulation architecture for the {@code CookingPlate}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code CookingPlate} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture	createCookingPlateMILArchitecture()
			throws Exception {
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// the Cooking Plate model simulating its electricity consumption, an
		// atomic HIOA model hence we use an AtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				CookingPlateStateModel.MIL_URI,
				AtomicModelDescriptor.create(
						CookingPlateStateModel.class,
						CookingPlateStateModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new Architecture(
						CookingPlateStateModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/***********************************************************************************/
	/**
	 * create the local MIL simulation architecture for the {@code CookingPlate}
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the local MIL simulation architecture for the {@code CookingPlateTester} component.
	 * @throws Exception	<i>to do</i>.
	 */
	public static Architecture createCookingPlateUserMILArchitecture()
			throws Exception {
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// for atomic model, we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				CookingPlateUserModel.MIL_URI,
				AtomicModelDescriptor.create(
						CookingPlateUserModel.class,
						CookingPlateUserModel.MIL_URI,
						TimeUnit.HOURS,
						null));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new Architecture(
						CookingPlateUserModel.MIL_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/***********************************************************************************/
	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code CookingPlate} component.
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
	 * @return						the local MIL real time simulation architecture for the {@code CookingPlate} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createCookingPlateRTArchitecture(
			ExecutionType currentExecutionType,
			double accelerationFactor
			) throws Exception
	{
		String modelURI = null;
		switch (currentExecutionType) {
		case MIL_RT_SIMULATION:
			modelURI = CookingPlateStateModel.MIL_RT_URI;
			break;
		case SIL_SIMULATION:
			modelURI = CookingPlateStateModel.SIL_URI;
			break;
		default:
			throw new RuntimeException("incorrect executiontype: " +
					currentExecutionType + "!");
		}

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// the Cooking Plate model simulating its electricity consumption, an
		// atomic HIOA model hence we use an AtomicHIOA_Descriptor
		atomicModelDescriptors.put(
				modelURI,
				RTAtomicModelDescriptor.create(
						CookingPlateStateModel.class,
						modelURI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						modelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

	/***********************************************************************************/
	/**
	 * create the local MIL real time simulation architecture for the
	 * {@code CookingPlateTester} component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	acceleration factor used in this run.
	 * @return						the local MIL simulation architecture for the {@code CookingPlate} component.
	 * @throws Exception			<i>to do</i>.
	 */
	public static Architecture	createCookingPlateUserMILRTArchitecture(
			double accelerationFactor
			) throws Exception
	{
		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
				new HashMap<>();

		// for atomic model, we use an AtomicModelDescriptor
		atomicModelDescriptors.put(
				CookingPlateUserModel.MIL_RT_URI,
				RTAtomicModelDescriptor.create(
						CookingPlateUserModel.class,
						CookingPlateUserModel.MIL_RT_URI,
						TimeUnit.HOURS,
						null,
						accelerationFactor));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>();

		// simulation architecture
		Architecture architecture =
				new RTArchitecture(
						CookingPlateUserModel.MIL_RT_URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.HOURS);

		return architecture;
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/