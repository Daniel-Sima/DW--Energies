package equipments.CookingPlate;

import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerMode;
import fr.sorbonne_u.components.hem2023e1.equipments.hairdryer.HairDryerImplementationI.HairDryerState;

/*
 *  2 plaques
chaque plaques a 7 mode
chaleur va de 50° a 300°
Pmax = 3000W
mode 1 = 50° | mode 2 = 80° | mode 3 = 120° | mode 4 = 160° | mode 5 = 200° | mode 6 = 250° | mode 7 = 300°
 * */

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>CookingPlateImplementationI</code> defines the signatures
 * of services service implemented by the cooking plate component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Black-box Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no invariant
 * </pre>
 * 
 * <p>
 * Created on : 2023-10-10
 * </p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public interface CookingPlateImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------
	/**
	 * The enumeration <code>CookingPlateState</code> describes the operation states
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum CookingPlateState {
		/** cooking plate is on. */
		ON,
		/** cooking plate is off. */
		OFF
	}

	/***********************************************************************************/
	/**
	 * The enumeration <code>CookingPlateMode</code> describes the operation modes
	 * of the cooking plate.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * The cooking plate has 7 modes, from 1 to 7, from the coldest to the hottest temperature.
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum CookingPlateMode {
		MODE_1, // 50 °
		MODE_2, // 80°
		MODE_3, // 120 °
		MODE_4, // 160°
		MODE_5, // 200 °
		MODE_6, // 250°
		MODE_7  // 300 °
	}
	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the cooking plate.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public CookingPlateState getState() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current operation mode of the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the cooking plate.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public CookingPlateMode	getMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn on the cooking plate, put in the mode 0 (50°).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == CookingPlate.OFF}
	 * post	{@code getMode() == CookingPlateMode.MODE_1}
	 * post	{@code getState() == CookingPlateMode.ON}
	 * </pre>
	 *
	 * @throws Exception <i>TODO</i>.
	 */
	public void turnOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn off the cooking plate.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == CookingPlateState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOff() throws Exception;
	
	/***********************************************************************************/
	/**
	 * increase the cooking plate MODE.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == CookingPlateState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	increaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * decrease the cooking plate MODE
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == HairDryerState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	decreaseMode() throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
