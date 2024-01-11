package utils;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>Electricity</code> defines some utilities to work with
 * electricity concepts.
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
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class Electricity {
	/**
	 * convert the duration {@code d} in hours.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d		the duration to be converted.
	 * @return		the duration equal to {@code d} in hours.
	 */
	public static double toHours(Duration d) {
		long factor = 1;
		if (d.getTimeUnit() != TimeUnit.HOURS) {
			factor = d.getTimeUnit().convert(1, TimeUnit.HOURS);
		}
		double ret = d.getSimulatedDuration()/factor;
		return ret;
	}

	/***********************************************************************************/
	/**
	 * compute the total consumption in kwh for the given intensity {@code i}
	 * in watts consumed during the duration {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code i >= 0.0}
	 * post	{@code ret >= 0.0}
	 * </pre>
	 *
	 * @param d		duration of the consumption to be computed.
	 * @param i		constant intensity in watts during the duration {@code d}.
	 * @return		the total consumption in kwh.
	 */
	public static double computeConsumption(Duration d, double i)
	{
		double h = toHours(d);
		return h*i/1000.0;
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
