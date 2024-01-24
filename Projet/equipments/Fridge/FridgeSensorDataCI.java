package equipments.Fridge;

import java.util.concurrent.TimeUnit;

import equipments.Fridge.measures.FridgeSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import utils.Measure;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>FridgeSensorDataCI</code>
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
 * <p>Created on : 2024-10-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface FridgeSensorDataCI 
extends DataOfferedCI, DataRequiredCI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static interface	FridgeSensorCI
	extends		OfferedCI,
	RequiredCI
	{
		// ---------------------------------------------------------------------
		// Methods
		// ---------------------------------------------------------------------

		/**
		 * return true if the Fridge is currently cooling.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				true if the Fridge is currently cooling.
		 * @throws Exception	<i>to do</i>.
		 */
		public FridgeSensorData<Measure<Boolean>> coolingPullSensor()
				throws Exception;

		/***********************************************************************************/
		/**
		 * get the current target temperature.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return >= -50.0 && return <= 50.0}
		 * </pre>
		 *
		 * @return				the current target temperature.
		 * @throws Exception	<i>to do</i>.
		 */
		public FridgeSensorData<Measure<Double>>	targetTemperaturePullSensor()
				throws Exception;

		/***********************************************************************************/
		/**
		 * return the current temperature measured by the thermostat.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				the current temperature measured by the thermostat.
		 * @throws Exception	<i>to do</i>.
		 */
		public FridgeSensorData<Measure<Double>>	currentTemperaturePullSensor()
				throws Exception;

		/***********************************************************************************/
		/**
		 * start a sequence of temperatures pushes with the given period.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code controlPeriod > 0}
		 * pre	{@code tu != null}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param controlPeriod	period at which the pushes must be made.
		 * @param tu			time unit in which {@code controlPeriod} is expressed.
		 * @throws Exception	<i>to do</i>.
		 */
		public void			startTemperaturesPushSensor(
				long controlPeriod,
				TimeUnit tu
				) throws Exception;

	}

	/***********************************************************************************/
	/**
	 * The interface <code>FridgeSensorRequiredPullCI</code> is the pull
	 * interface that a client component must require to call the outbound port.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-12-04</p>
	 * 
	 */
	public static interface		FridgeSensorRequiredPullCI
	extends		FridgeSensorCI,
	DataRequiredCI.PullCI
	{
	}

	/***********************************************************************************/
	/**
	 * The interface <code>FridgeSensorOfferedPullCI</code> is the pull
	 * interface that a server component must offer to be called the inbound
	 * port.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-12-04</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static interface		FridgeSensorOfferedPullCI
	extends		FridgeSensorCI,
	DataOfferedCI.PullCI
	{
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

