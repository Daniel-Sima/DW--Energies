package fr.sorbonne_u.components.hem2023e3.equipments.heater;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterSensorCI</code>
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
 * <p>Created on : 2023-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		HeaterSensorDataCI
extends		DataOfferedCI,
			DataRequiredCI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static interface	HeaterSensorCI
	extends		OfferedCI,
				RequiredCI
	{
		// ---------------------------------------------------------------------
		// Methods
		// ---------------------------------------------------------------------

		/**
		 * return true if the heater is currently heating.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				true if the heater is currently heating.
		 * @throws Exception	<i>to do</i>.
		 */
		public HeaterSensorData<Measure<Boolean>>	heatingPullSensor()
				throws Exception;

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
		public HeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
				throws Exception;

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
		public HeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
				throws Exception;

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

	/**
	 * The interface <code>HeaterSensorRequiredPullCI</code> is the pull
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
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static interface		HeaterSensorRequiredPullCI
	extends		HeaterSensorCI,
				DataRequiredCI.PullCI
	{
	}

	/**
	 * The interface <code>HeaterSensorOfferedPullCI</code> is the pull
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
	public static interface		HeaterSensorOfferedPullCI
	extends		HeaterSensorCI,
				DataOfferedCI.PullCI
	{
	}
}
// -----------------------------------------------------------------------------
