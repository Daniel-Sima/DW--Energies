package fr.sorbonne_u.devs_simulation.models.time;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>Duration</code> defines a representation for duration
 * values with their time unit as well as methods to manipulate them.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: factor out common code between Time and Duration into an AbstractTime
 * abstract class.
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code simulatedDuration >= 0.0}
 * invariant	{@code timeUnit != null}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-02-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@SuppressWarnings("unused")
public class			Duration
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** maximum number of digits in the fractional part of the time used
	 *  to format in the method {@code toString}.							*/
	public static final int			MAX_FRACTION_DIGITS = 2;
	/** number format (single) instance used to format in the method
	 *  {@code toString}.													*/
	protected static NumberFormat 	NF;

	/** the representation of the duration infinity i.e., the largest
	 *  possible.															*/
	public static final Duration	INFINITY =
										new Duration(Double.POSITIVE_INFINITY,
													 TimeUnit.SECONDS);
	/** tolerance on the precision of floating points d to consider two
	 *  durations as equal.													*/
	public static double			TOLERANCE = 0.000000001;

	/** the value of the duration represented by this object.				*/
	protected double				simulatedDuration;
	/** the time unit of this duration, allowing to unambiguously
	 *  interpret it.														*/
	protected TimeUnit				timeUnit;

	// -------------------------------------------------------------------------
	// Static initialisers
	// -------------------------------------------------------------------------

	static {
		assert	MAX_FRACTION_DIGITS > 0 :
				new AssertionError(
						"Precondition violation: MAX_FRACTION_DIGITS > 0");

		NF = NumberFormat.getInstance(Locale.US);
		NF.setGroupingUsed(false);
		NF.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
	}

	/**
	 * set the number of digits in the decimal part of duration values to be
	 * used when constructing a string representation of a {@code Duration}
	 * instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code digits > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param digits	number of digits in the decimal part.
	 */
	public static void	setPrintPrecision(int digits)
	{
		assert	digits > 0 :
				new AssertionError("Precondition violation: digits > 0");

		NF = NumberFormat.getInstance(Locale.US);
		NF.setGroupingUsed(false);
		NF.setMaximumFractionDigits(digits);
	}

	/**
	 * set the tolerance used in the comparisons among times and durations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code tolerance >= 0.0}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param tolerance	tolerance to be used in the comparisons among times and durations.
	 */
	public static void	setTolerance(double tolerance)
	{
		assert	tolerance >= 0.0 :
				new AssertionError("Precondition violation: tolerance >= 0.0");

		TOLERANCE = tolerance;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new duration object with the given duration value and
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedDuration >= -Duration.TOLERANCE}
	 * pre	{@code timeUnit != null}
	 * post	{@code Duration.checkInvariant(this)}
	 * </pre>
	 *
	 * @param simulatedDuration	represented duration value.
	 * @param timeUnit			time unit of the represented duration.
	 */
	public				Duration(
		double simulatedDuration,
		TimeUnit timeUnit
		)
	{
		super();

		assert	timeUnit != null :
				new AssertionError("Precondition violation: timeUnit != null");
		assert	simulatedDuration >= -Duration.TOLERANCE :
				new AssertionError("Precondition violation: "
								+ "simulatedDuration >= -Duration.TOLERANCE");

		if (simulatedDuration < 0.0 && simulatedDuration >= -Duration.TOLERANCE)
		{
			simulatedDuration = 0.0;
		}

		this.simulatedDuration = simulatedDuration;
		this.timeUnit = timeUnit;

		assert	Duration.checkInvariant(this) :
				new AssertionError("Postcondition violation: "
										+ "Duration.checkInvariant(this)");
	}

	/**
	 * return true if {@code d} respects its invariant.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	a duration instance.
	 * @return	true if {@code d} respects its invariant.
	 */
	protected static boolean	checkInvariant(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");

		boolean inv = true;
		inv &= d.simulatedDuration >= 0.0;
		inv &= d.timeUnit != null;
		return inv;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the duration 0 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 0 in the given time unit.
	 */
	public static Duration	zero(TimeUnit u)
	{
		assert	u != null :
				new AssertionError("Precondition violation: u != null");

		return new Duration(0.0, u);
	}

	/**
	 * return the duration 1 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 1 in the given time unit.
	 */
	public static Duration	one(TimeUnit u)
	{
		assert	u != null :
				new AssertionError("Precondition violation: u != null");

		return new Duration(1.0, u);
	}

	/**
	 * get the simulated duration value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the simulated duration value.
	 */
	public double		getSimulatedDuration()
	{
		return this.simulatedDuration;
	}

	/**
	 * get this duration object time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	this duration object time unit.
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit;
	}

	/**
	 * return true if this duration object and <code>d</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");

		return (this == Duration.INFINITY || d == Duration.INFINITY
							|| this.getTimeUnit().equals(d.getTimeUnit()));
	}

	/**
	 * return true if this duration object and <code>t</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Time t)
	{
		assert	t != null :
				new AssertionError("Precondition violation: t != null");

		return (this == Duration.INFINITY || t == Time.INFINITY
							|| this.getTimeUnit().equals(t.getTimeUnit()));
	}

	/**
	 * create a new duration object with the given simulated value and
	 * the same time unit as this one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d >= 0.0}
	 * post	{@code ret != null && ret.getSimulatedDuration() == d && ret.getTimeUnit().equals(this.getTimeUnit())}
	 * </pre>
	 *
	 * @param d	simulated duration value.
	 * @return	a new duration object with the given simulated value and the same time unit as this one.
	 */
	public Duration		createFromSimulatedDuration(double d)
	{
		assert	d >= 0.0 : new AssertionError("Precondition violation: d >= 0.0");

		return new Duration(d, this.timeUnit);
	}

	/**
	 * return a copy of this duration object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null && ret != this && ret.equals(this)}
	 * </pre>
	 *
	 * @return	a copy of this duration object.
	 */
	public Duration		copy()
	{
		return new Duration(this.simulatedDuration, this.timeUnit);
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code>.
	 */
	public boolean		equals(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			return d == Duration.INFINITY;
		} else if (d == Duration.INFINITY) {
			return this == Duration.INFINITY;
		} else {
			return Math.abs(this.simulatedDuration - d.simulatedDuration)
										< Duration.TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents a smaller duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a smaller duration than <code>d</code>.
	 */
	public boolean		lessThan(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			return false;
		} else if (d == Duration.INFINITY) {
			return this != Duration.INFINITY;
		} else {
			return this.simulatedDuration < d.simulatedDuration + TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or less.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or less.
	 */
	public boolean		lessThanOrEqual(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			return d == Duration.INFINITY;
		} else if (d == Duration.INFINITY) {
			return true;
		} else {
			return this.simulatedDuration <= d.simulatedDuration + TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents a greater duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a greater duration than <code>d</code>.
	 */
	public boolean		greaterThan(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			return d != Duration.INFINITY;
		} else if (d == Duration.INFINITY) {
			return false;
		} else {
			return this.simulatedDuration > d.simulatedDuration - TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or greater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or greater.
	 */
	public boolean		greaterThanOrEqual(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			return true;
		} else if (d == Duration.INFINITY) {
			return this == Duration.INFINITY;
		} else {
			return this.simulatedDuration >= d.simulatedDuration - TOLERANCE;
		}
	}

	/**
	 * add two durations and return a new duration object representing the
	 * result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	a new duration object representing the sum.
	 */
	public Duration		add(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY || d == Duration.INFINITY) {
			return Duration.INFINITY;
		} else {
			return new Duration(this.simulatedDuration + d.simulatedDuration,
								this.timeUnit);
		}
	}

	/**
	 * subtract two durations and return a new duration object representing the
	 * result; if the duration {@code d} is larger than this duration, the
	 * result is the Duration zero; also an exception is raised if the result is undefined
	 * <i>i.e.</i>, when {@code d} is {@code Duration.INFINITY}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	a new duration object representing the subtraction.
	 */
	public Duration		subtract(Duration d)
	{
		assert	d != null :
				new AssertionError("Precondition violation: d != null");
		assert	this.hasSameUnit(d) :
				new AssertionError("Precondition violation: hasSameUnit(d)");

		if (this == Duration.INFINITY) {
			if (d == Duration.INFINITY) {
				throw new RuntimeException("Duration not defined!");
			} else {
				return Duration.INFINITY;
			}
		} else if (d == Duration.INFINITY) {
			throw new RuntimeException("Duration not defined!");
		} else {
			double result = this.simulatedDuration - d.simulatedDuration;
			return new Duration(result >= 0.0 ? result : 0.0,
								this.timeUnit);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('(');
		if (this.equals(Duration.INFINITY)) {
			sb.append("INFINITY");
		} else {
			sb.append(NF.format(this.getSimulatedDuration()));
			sb.append(", ");
			sb.append(this.getTimeUnit());
		}
		sb.append(')');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
