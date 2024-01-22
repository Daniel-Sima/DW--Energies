package equipments.Lamp;

/*
Lampe à 3 modes de luminosité
Matériel: plastique et silicone
Couleur de la lumière: blanc chaud
Tension de l'adaptateur: 12 V 0,5 A
Lumen: 500LM
Température de couleur: 3000K
Indice de rendu des couleurs:> 80 Ra
Pmax = 5.5W
mode 1 = 10% | mode 2 = 50% | mode 3 = 100% 
 */

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The interface <code>LampImplementationI</code> defines the signatures
 * of services service implemented by the lamp component.
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
public interface LampImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------
	/**
	 * The enumeration <code>LampState</code> describes the operation states
	 * of the lamp.
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
	public static enum LampState {
		/** lamp is on. */
		ON,
		/** lamp is off. */
		OFF
	}

	/***********************************************************************************/
	/**
	 * The enumeration <code>LampMode</code> describes the operation modes
	 * of the lamp.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * The lamp has 7 modes, from 1 to 7, from the coldest to the hottest temperature.
	 * </p>
	 * 
	 * <p>
	 * Created on : 2023-10-10
	 * </p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	public static enum LampMode {
		LOW, // 10%
		MEDIUM, // 50%
		HIGH	// 100%
	}
	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the lamp.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the lamp.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public LampState getState() throws Exception;
	
	/***********************************************************************************/
	/**
	 * return the current operation mode of the lamp.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the lamp.
	 * @throws Exception 	<i>TODO</i>.
	 */
	public LampMode	getMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn on the lamp, put in the mode 0 (50°).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == Lamp.OFF}
	 * post	{@code getMode() == LampMode.MODE_1}
	 * post	{@code getState() == LampMode.ON}
	 * </pre>
	 *
	 * @throws Exception <i>TODO</i>.
	 */
	public void turnOn() throws Exception;
	
	/***********************************************************************************/
	/**
	 * turn off the lamp.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == LampState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	turnOff() throws Exception;
	
	/***********************************************************************************/
	/**
	 * increase the lamp MODE.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == LampState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	increaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * decrease the lamp MODE
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == LampState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void	decreaseMode() throws Exception;
	
	/***********************************************************************************/
	/**
	 * This functions prints a separator for better visualization of the traces.
	 */
	public void printSeparator(String title) throws Exception;
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
