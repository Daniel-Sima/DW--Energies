package equipments.AirConditioning;

import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.measures.AirConditioningSensorData;
import utils.Measure;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface 	AirConditioningSensorDataCI 
extends		DataOfferedCI,
			DataRequiredCI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static interface	AirConditioningSensorCI
	extends		OfferedCI,
				RequiredCI
	{
		// ---------------------------------------------------------------------
		// Methods
		// ---------------------------------------------------------------------

		/**
		 * return true if the AirConditioning is currently cooling.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code on()}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return				true if the air conditioning is currently cooling.
		 * @throws Exception	<i>to do</i>.
		 */
		public AirConditioningSensorData<Measure<Boolean>>	coolingPullSensor()
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
		public AirConditioningSensorData<Measure<Double>>	targetTemperaturePullSensor()
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
		public AirConditioningSensorData<Measure<Double>>	currentTemperaturePullSensor()
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
	 * The interface <code>AirConditioningSensorRequiredPullCI</code> is the pull
	 * interface that a client component must require to call the outbound port.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 */
	public static interface		AirConditioningSensorRequiredPullCI
	extends		AirConditioningSensorCI,
				DataRequiredCI.PullCI
	{
	}

	/**
	 * The interface <code>AirConditioningSensorOfferedPullCI</code> is the pull
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
	 */
	public static interface		AirConditioningSensorOfferedPullCI
	extends		AirConditioningSensorCI,
				DataOfferedCI.PullCI
	{
	}
}