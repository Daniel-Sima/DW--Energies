package equipments.CookingPlate;

import equipments.CookingPlate.CookingPlateImplementationI.CookingPlateMode;
import equipments.CookingPlate.CookingPlateImplementationI.CookingPlateState;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerMode;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerState;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CookingPlate</code> implements the cooking plate component.
 *
 * <p><strong>Description TODO </strong></p>
 * 
 * <p> 
 * The cooking plate is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. However, it will connect later to the
 * electric panel to take its (simulated) electricity consumption into account.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentState != null}
 * invariant	{@code currentMode != null}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@OfferedInterfaces(offered={CookingPlateUserCI.class})
public class PlaqueCuisson extends AbstractComponent{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	/** URI of the hair dryer inbound port used in tests.					*/
	public static final String	INBOUND_PORT_URI = "COOKING-PLATE-INBOUND-PORT-URI";
	
	/** when true, methods trace their actions.								*/
	public static final boolean			VERBOSE = true;
	public static final CookingPlateState	INITIAL_STATE = CookingPlateState.OFF;
	public static final CookingPlateMode	INITIAL_MODE = CookingPlateMode.MODE_1;
	
	/** current state (on, off) of the cooking plate.							*/
	protected CookingPlateState currentState;
	/** current mode of operation (1 to 7) of the cooking plate.			*/
	protected CookingPlateMode currentMode;

	protected PlaqueCuisson(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
