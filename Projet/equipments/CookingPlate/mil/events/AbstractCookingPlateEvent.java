package equipments.CookingPlate.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The abstract class <code>AbstractCookingPlateEvent</code> enforces a common
 * type for all Cooking Plate simulation events.
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
 * <p>Created on : 2023-11-08</p>
 *  
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class AbstractCookingPlateEvent
extends	ES_Event {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * used to create an event used by the Cooking Plate simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content (data) associated with the event.
	 */
	public AbstractCookingPlateEvent(
			Time timeOfOccurrence,
			EventInformationI content
			) {
		super(timeOfOccurrence, content);
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
