package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistoryFactoryI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>Value</code> defines a placeholder for the values of
 * atomic HIOA models with a history mechanism that memorise immediately
 * preceding values within a predefined time window.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class is meant to serve as indirection for the value of variables
 * that are shared among the model (unique by rule) that exports them
 * and models that import them. Because variables take different values
 * over the simulation time, a simulation time is attached to each value
 * when it is computed (i.e., when the variable is assigned a new value).
 * </p>
 * <p>
 * The class also defines a history mechanism able to memorise values of the
 * variable over time. The size of the history can be set when creating the
 * instance of <code>Value</code>. However, the class does not define any
 * processing for these values. Subclasses should therefore introduce such
 * processing, like means to predict values at a given time, either
 * interpolating between memorised values or extrapolating outside the time
 * interval covered by memorised values.
 * </p>
 * <p>
 * Because the models can be executed in real time, aka with concurrent tasks
 * triggered by a real time scheduler, accesses to values must be protected
 * by a lock (for most operations).
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code descriptor == null || owner == descriptor.owner}
 * invariant	{@code timeUnit == null || timeUnit.equals(owner.getSimulatedTimeUnit())}
 * invariant    {@code time == null || time.getTimeUnit().equals(timeUnit)}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Value<Type>
implements	TimedValueI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** the model owning the variable and this value object.				*/
	protected final AtomicHIOA				owner;
	/** the time unit in which the time associated to the value must be
	 *  interpreted.														*/
	protected final TimeUnit				timeUnit;
	/** the descriptor of the variable.										*/
	protected VariableDescriptor			descriptor;

	/** lock protected concurrent accesses to values and their time.		*/
	protected final ReentrantReadWriteLock	valueLock;
	/** true if the value has been given an initial value, false otherwise.	*/
	protected boolean						initialised;
	/** the value at the given time.										*/
	protected Type							v;
	/** the simulated time at which the value was computed/assigned.		*/
	protected Time							time;

	/** the values stored in the history.									*/
	public final ValueHistory<Type>			valueHistory;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a value instance for the given variable and the given owner
	 * with an initial value but without history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code Value.checkInvariant(this)}
	 * post	{@code owner == getOwner()}
	 * post	{@code !isVariableDescriptorSet()}
	 * post	{@code getTime() == null}
	 * post	{@code !isInitialised()}
	 * post	{@code !hasValueHistory()}
	 * </pre>
	 *
	 * @param owner			model owning the variable.
	 */
	public				Value(AtomicHIOA owner)
	{
		this(owner, null, null);
	}

	/**
	 * create a value instance for the given variable and the given owner
	 * with an initial value computed at the given initial time and with
	 * the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code historyWindow == null || historyWindow.greaterThan(Duration.zero(historyWindow.getTimeUnit()))}
	 * pre	{@code historyWindow == null || owner.getSimulatedTimeUnit() == historyWindow.getTimeUnit()}
	 * pre	{@code historyWindow == null && valueHistoryFactory == null || historyWindow != null && valueHistoryFactory != null}
	 * post	{@code Value.checkInvariant(this)}
	 * post	{@code owner == getOwner()}
	 * post	{@code !isVariableDescriptorSet()}
	 * post	{@code getTime() == null}
	 * post	{@code !isInitialised()}
	 * post	{@code historyWindow == null ? !hasValueHistory() : hasValueHistory()}
	 * </pre>
	 *
	 * @param owner					model owning the variable.
	 * @param historyWindow			time window governing the size of the history.
	 * @param valueHistoryFactory	factory to create an history of the values.
	 */
	public				Value(
		AtomicHIOA owner,
		Duration historyWindow,
		ValueHistoryFactoryI<Type> valueHistoryFactory
		)
	{
		assert	owner != null :
				new AssertionError("Precondition violation: owner != null");
		assert	historyWindow == null ||
					owner.getSimulatedTimeUnit() == historyWindow.getTimeUnit() :
				new AssertionError("Precondition violation: "
									+ "historyWindow == null || "
									+ "owner.getSimulatedTimeUnit() == "
									+ "historyWindow.getTimeUnit()");
		assert	historyWindow == null ||
					historyWindow.greaterThan(
								Duration.zero(historyWindow.getTimeUnit())) :
				new AssertionError("Precondition violation: "
									+ "historyWindow == null || "
									+ "historyWindow.greaterThan("
									+ "Duration.zero(historyWindow.getTimeUnit()))");
		assert	historyWindow == null && valueHistoryFactory == null ||
						historyWindow != null && valueHistoryFactory != null :
				new AssertionError("Precondition violation: "
									+ "historyWindow == null && "
									+ "valueHistoryFactory == null || "
									+ "historyWindow != null && "
									+ "valueHistoryFactory != null");

		this.owner = owner;
		this.initialised = false;
		if (historyWindow != null) {
			this.valueHistory =
						valueHistoryFactory.createHistory(historyWindow);
		} else {
			this.valueHistory = null;
		}
		this.timeUnit = owner.getSimulatedTimeUnit();
		this.valueLock = new ReentrantReadWriteLock();

		assert	Value.checkInvariant(this) :
				new AssertionError("Postcondition violation: "
										+ "Value.checkInvariant(this)");
		assert	owner == this.getOwner() :
				new AssertionError("Postcondition violation: owner == getOwner()");
		assert	this.getTimeUnit() == this.getOwner().getSimulatedTimeUnit() :
				new AssertionError("Postcondition violation: "
						+ "getTimeUnit() == getOwner().getSimulatedTimeUnit()");
		assert	!this.isVariableDescriptorSet() :
				new AssertionError("Postcondition violation: "
						+ "isVariableDescriptorSet()");
		assert	this.getTime() == null :
				new AssertionError("Postcondition violation: getTime() == null");
		assert	!this.isInitialised() :
				new AssertionError("Postcondition violation: !isInitialised()");
		assert	historyWindow == null ?
					!this.hasValueHistory()
				:	this.hasValueHistory() :
				new AssertionError("Postcondition violation: "
									+ "historyWindow == null ? "
									+ "!this.hasValueHistory()"
									+ ": this.hasValueHistory()");
	}

	/**
	 * create a value instance for the given variable and the given owner
	 * and the given variable descriptor with an initial value computed at
	 * the given initial time and with the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code descriptor != null}
	 * pre	{@code historyWindow == null || historyWindow.greaterThan(Duration.zero(historyWindow.getTimeUnit()))}
	 * pre	{@code historyWindow == null || owner.getSimulatedTimeUnit() == historyWindow.getTimeUnit()}
	 * pre	{@code historyWindow == null && valueHistoryFactory == null || historyWindow != null && valueHistoryFactory != null}
	 * post	{@code Value.checkInvariant(this)}
	 * post	{@code owner == getOwner()}
	 * post	{@code isVariableDescriptorSet()}
	 * post	{@code getTime() == null}
	 * post	{@code !isInitialised()}
	 * post	{@code historyWindow == null ? !hasValueHistory() : hasValueHistory()}
	 * </pre>
	 *
	 * @param owner					model owning the variable.
	 * @param descriptor			descriptor of the variable.
	 * @param historyWindow			time window governing the size of the history.
	 * @param valueHistoryFactory	factory to create an history of the values.
	 */
	public				Value(
		AtomicHIOA owner,
		VariableDescriptor descriptor,
		Duration historyWindow,
		ValueHistoryFactoryI<Type> valueHistoryFactory
		)
	{
		this(owner, historyWindow, valueHistoryFactory);

		assert	descriptor != null :
				new AssertionError("Precondition violation: descriptor != null");

		this.descriptor = descriptor;

		assert	Value.checkInvariant(this) :
				new AssertionError("Postcondition violation: "
										+ "Value.checkInvariant(this)");
		assert	this.isVariableDescriptorSet() :
				new AssertionError("Postcondition violation: "
										+ "isVariableDescriptorSet()");
	}

	/**
	 * check the invariant.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code val != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param val			a value to be checked.
	 * @return				true if the invariant is observed.
	 */
	public static boolean	checkInvariant(Value<?> val)
	{
		assert	val != null :
				new AssertionError("Precondition violation: val != null");

		val.valueLock.readLock().lock();
		try {
			boolean invariant = true;
			invariant &=	val.descriptor == null ||
										val.owner == val.descriptor.owner;
			invariant &=	val.timeUnit == null ||
										val.timeUnit.equals(
											val.owner.getSimulatedTimeUnit());
			invariant &=	val.time == null ||
									val.time.getTimeUnit().equals(val.timeUnit);
			invariant &= 	(val.hasValueHistory() ?
								ValueHistory.checkInvariant(val.valueHistory)
							:	true
							);

			return invariant;
		} finally {
			val.valueLock.readLock().unlock();
		}
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * set the variable descriptor for this value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vd != null}
	 * pre	{@code !this.isVariableDescriptorSet()}
	 * pre	{@code vd.getOwner() == getOwner()}
	 * pre	{@code vd.getField().get(getOwner()) == this}
	 * post	{@code getDescriptor().equals(vd)}
	 * post	{@code isVariableDescriptorSet()}
	 * </pre>
	 *
	 * @param vd	the variable descriptor for this value.
	 */
	public void			setVariableDescriptor(VariableDescriptor vd)
	{
		assert	vd != null :
				new AssertionError("Precondition violation: vd != null");
		assert	!this.isVariableDescriptorSet() :
				new AssertionError("Precondition violation: "
										+ "!isVariableDescriptorSet()");
		assert	vd.getOwner() == this.getOwner() :
				new AssertionError("Precondition violation: "
										+ "vd.getOwner() == this.getOwner()");
		try {
			assert	vd.getField().get(this.owner) == this;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e) ;
		}

		this.descriptor = vd;
	}

	/**
	 * return true if the variable descriptor of this value has been set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the variable descriptor of this value has been set.
	 */
	public boolean		isVariableDescriptorSet()
	{
		return this.descriptor != null;
	}

	/**
	 * initialise the simulated time associated to the initial value, when the
	 * latter has been set by the constructor (<i>i.e.</i>, before the initial
	 * time of the simulation is known); this will not trigger a push in the
	 * value history even if one is present, rather use {@code setNewValue} to
	 * force a push.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code initialTime != null}
	 * pre	{@code initialTime.getTimeUnit().equals(getTimeUnit())}
	 * pre	{@code initialTime.greaterThanOrEqual(Time.zero(getTimeUnit()))}
	 * post	{@code getTime().equals(initialTime)}
	 * </pre>
	 *
	 * @param initialTime	simulated time at which the initial value corresponds.
	 */
	public void			initialiseTime(Time initialTime)
	{
		assert	initialTime != null :
				new AssertionError("Precondition violation: initialTime != null");
		assert	initialTime.getTimeUnit().equals(this.getTimeUnit()) :
				new AssertionError("Precondition violation: "
						+ "initialTime.getTimeUnit().equals(this.getTimeUnit())");
		assert	initialTime.greaterThanOrEqual(Time.zero(this.getTimeUnit())) :
				new AssertionError("Precondition violation: "
						+ "initialTime.greaterThanOrEqual("
						+ "Time.zero(this.getTimeUnit()))");

		this.valueLock.writeLock().lock();
		try {
			this.time = initialTime;
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * return the owner of the variable.
	 * 
	 * @return	the owner of the variable.
	 */
	public AtomicHIOA	getOwner()
	{
		return this.owner;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.TimedValueI#getTimeUnit()
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit;
	}

	/**
	 * return the descriptor of the variable.
	 * 
	 * @return	the descriptor of the variable.
	 */
	public VariableDescriptor	getDescriptor()
	{
		return this.descriptor;
	}

	/**
	 * return true if this value has an history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this value has an history.
	 */
	public boolean		hasValueHistory()
	{
		this.valueLock.readLock().lock();
		try {
			return this.valueHistory != null;
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * return true if the value has been given an initial value, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the value has been given an initial value, false otherwise.
	 */
	public boolean		isInitialised()
	{
		this.valueLock.readLock().lock();
		try {
			return this.initialised;
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * get the current value assigned to this {@code Value} object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the current value assigned to this {@code Value} object.
	 */
	public Type			getValue()
	{
		this.valueLock.readLock().lock();
		try {
			assert	this.initialised :
					new AssertionError("Precondition violation: isInitialised()");

			return this.v;
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.TimedValueI#getTime()
	 */
	@Override
	public Time			getTime()
	{
		this.valueLock.readLock().lock();
		try {
			return this.time;
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * initialise the value with {@code v} at the time used when calling
	 * {@code initialiseTime}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isInitialised()}
	 * pre	{@code getTime() != null}
	 * post	{@code isInitialised()}
	 * post	{@code getValue().equals(v)}
	 * </pre>
	 *
	 * @param v	initial value to be set.
	 * @return	this object to enable an assignment after initialisation.
	 */
	public Value<Type>	initialise(Type v)
	{
		this.valueLock.writeLock().lock();
		try {
			assert	!this.initialised :
					new AssertionError("Precondition violation: "
											+ "!isInitialised()");
			assert	this.time != null :
					new AssertionError("Precondition violation: "
											+ "getTime() != null");

			this.v = v;
			this.initialised = true;
			if (this.hasValueHistory()) {
				this.valueHistory.add(this);
				this.valueHistory.trimToWindow();
			}
			return this;
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * set a new value for this {@code Value} object; safer but less efficient
	 * to use than modifying the variables {@code v} and {@code time} directly;
	 * mandatory when using a value history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code t != null && t.getTimeUnit().equals(getTimeUnit())}
	 * pre	{@code getTime() != null && t.greaterThanOrEqual(getTime())}
	 * post	{@code getValue().equals(v)}
	 * post	{@code getTime().equals(t)}
	 * </pre>
	 *
	 * @param v	new value to be set.
	 * @param t	time at which the new value corresponds.
	 */
	public void			setNewValue(Type v, Time t)
	{
		this.valueLock.writeLock().lock();
		try {
			assert	t != null && t.getTimeUnit().equals(this.timeUnit) :
					new AssertionError("Precondition violation: "
						+ "t != null && t.getTimeUnit().equals(getTimeUnit())");
			assert	this.time != null && t.greaterThanOrEqual(this.time) :
					new AssertionError("Precondition violation: "
						+ "getTime() != null && t.greaterThanOrEqual(getTime()");

			this.v = v;
			this.time = t;
			if (this.hasValueHistory()) {
				this.valueHistory.add(this);
				this.valueHistory.trimToWindow();
			}
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * unset the value so that it can be reused for another simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * post	{@code !isInitialised()}
	 * </pre>
	 *
	 */
	public void			reinitialise()
	{
		this.valueLock.writeLock().lock();
		try {
			assert	this.initialised :
					new AssertionError("Precondition violation: "
												+ "isInitialised()");

			this.v = null;
			this.time = null;
			if (this.hasValueHistory()) {
				this.valueHistory.reinitialise();
			}
			this.initialised = false;
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * get a value at time {@code t} for this {@code Value} object; safer but
	 * less efficient to use than accessing the variable {@code v} directly;
	 * mandatory when using a value history with an interpolation or
	 * extrapolation scheme. This implementation returns the evaluation
	 * returned by the history if there is one; if there is no history, the
	 * current value is returned, regardless of {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null && t.getTimeUnit().equals(getTimeUnit())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time at which the value is required.
	 * @return	the value for this {@code Value} object for the time {@code t}.
	 */
	public Type			evaluateAt(Time t)
	{
		assert	t != null && t.getTimeUnit().equals(this.timeUnit) :
				new AssertionError("Precondition violation: "
						+ "t != null && t.getTimeUnit().equals(getTimeUnit())");

		this.valueLock.readLock().lock();
		try {
			if (this.hasValueHistory()) {
				return this.valueHistory.evaluateAt(t);
			} else {
				return this.v;
			}
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		this.valueLock.readLock().lock();
		try {
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			this.content2String(sb);
			sb.append(']');
			return sb.toString();
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * add the content pertaining to this value to a string buffer to produce
	 * a visualisation o its current content.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sb != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sb	the string buffer to which the content is added.
	 */
	protected void		content2String(StringBuffer sb)
	{
		this.valueLock.readLock().lock();
		try {
			sb.append(v);
			sb.append(", ");
			sb.append(this.time);
			if (this.hasValueHistory()) {
				sb.append(", ");
				sb.append(this.valueHistory.toString());
			}
		} finally {
			this.valueLock.readLock().unlock();
		}
	}
}
// -----------------------------------------------------------------------------
