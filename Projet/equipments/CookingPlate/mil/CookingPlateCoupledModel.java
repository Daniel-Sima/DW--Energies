package equipments.CookingPlate.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlateCoupledModel</code> defines the DEVS coupled
 * model for a first prototype example of the Cooking Plate simulator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is only used to perform unitary tests on the Cooking Plate
 * simulation models, hence it is kept to a minimal implementation.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariants
 * </pre>
 * 
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CookingPlateCoupledModel 
extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an MIL model; works as long as only one instance is
	 *  created.															*/
	public static final String	MIL_URI = CookingPlateUserModel.class.getSimpleName()
											                + "MIL-URI";
	/** URI for MIL_RT model; works as long as only one instance is created. */
	public static final String MIL_RT_URI = CookingPlateUserModel.class.getSimpleName() 
															+ "MIL-RT-URI";
	/** URI for  SIL model; works as long as only one instance is created. */
	public static final String SIL_URI = CookingPlateUserModel.class.getSimpleName()
															+ "SIL-URI";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating the coupled model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public CookingPlateCoupledModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			CoordinatorI simulationEngine,
			ModelI[] submodels,
			Map<Class<? extends EventI>,EventSink[]> imported,
			Map<Class<? extends EventI>,ReexportedEvent> reexported,
			Map<EventSource, EventSink[]> connections
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
				imported, reexported, connections);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

