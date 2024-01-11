package equipments.HEM;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.AirConditioningExternalControlCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>AirConditioningConnector</code> manually implements a connector
 * bridging the gap between the given generic component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The code given here illustrates how a connector can be used to implement
 * a required interface given some offered interface that is different.
 * The objective is to be able to automatically generate such a connector
 * at run-time from an XML descriptor of the required adjustments.
 * </p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentMode >= 0 && currentMode <= MAX_MODE}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2023-10-16</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class AirConditioningConnector 
extends AbstractConnector
implements AdjustableCI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** modes will be defined by five power levels, including a power
	 *  level of 0.0 watts.													*/
	protected static final int MAX_MODE = 6;
	/** the maximum admissible temperature from which the air conditioning 
	 * should be resumed in priority after being suspended to save energy.	*/
	protected static final double MAX_ADMISSIBLE_TEMP = 30.0;
	/** the maximal admissible difference between the target and the
	 *  current temperature from which the air conditioning should be resumed in
	 *  priority after being suspended to save energy.						*/
	protected static final double MAX_ADMISSIBLE_DELTA = 15.0;

	/** the current mode of the air conditioning.							*/
	protected int currentMode;
	/** true if the air conditioning has been suspended, false otherwise.	*/
	protected boolean isSuspended;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of connector.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !suspended}
	 * post	{@code currentMode() == MAX_MODE}
	 * </pre>
	 *
	 */
	public AirConditioningConnector() {
		super();
		this.currentMode = MAX_MODE;
		this.isSuspended = false;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public int maxMode() throws Exception {
		return MAX_MODE;
	}

	/***********************************************************************************/
	/**
	 * compute the power level associated with the {@code newMode} and set
	 * the air conditioning at this power level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newMode >= 0 && newMode <= MAX_MODE}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param newMode		a new mode for the air conditioning.
	 * @throws Exception	<i>to do</i>.
	 */
	protected void computeAndSetNewPowerLevel(int newMode) throws Exception {
		assert	newMode >= 0 && newMode <= MAX_MODE :
			new PreconditionException("newMode >= 0 && newMode <= MAX_MODE");

		double maxPowerLevel =
				((AirConditioningExternalControlCI)this.offering).getMaxPowerLevel();

		double newPowerLevel =
				(newMode - 1) * maxPowerLevel/(MAX_MODE - 1);
		((AirConditioningExternalControlCI)this.offering).
		setCurrentPowerLevel(newPowerLevel);
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean upMode() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() <= MAX_MODE :
			new PreconditionException("currentMode() < MAX_MODE");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode + 1);
			this.currentMode++;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean downMode() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() > 0 : new PreconditionException("currentMode() > 0");
		try {
			this.computeAndSetNewPowerLevel(this.currentMode - 1);
			this.currentMode--;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean setMode(int modeIndex) throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	modeIndex >= 0 && modeIndex <= this.maxMode() :
			new PreconditionException(
					"modeIndex >= 0 && modeIndex <= maxMode()");

		try {
			this.computeAndSetNewPowerLevel(modeIndex);
			this.currentMode = modeIndex;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public int currentMode() throws Exception {
		if (this.suspended()) {
			return 0;
		} else {
			return this.currentMode;
		}
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean suspended() throws Exception {
		return this.isSuspended;
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public boolean suspend() throws Exception {
		assert	!this.suspended() : new PreconditionException("!suspended()");

		try {
			((AirConditioningExternalControlCI)this.offering).setCurrentPowerLevel(0.0);
			this.isSuspended = true;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.hem2023.bases.AdjustableCI#resume()
	 */
	@Override
	public boolean resume() throws Exception {
		assert	this.suspended() : new PreconditionException("suspended()");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode);
			this.isSuspended = false;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/***********************************************************************************/
	/**
	 * @see 
	 */
	@Override
	public double emergency() throws Exception {
		assert	this.suspended() : new PreconditionException("suspended()");

		double currentTemperature =
				((AirConditioningExternalControlCI)this.offering).
				getCurrentTemperature();
		double targetTemperature =
				((AirConditioningExternalControlCI)this.offering).
				getTargetTemperature();
		double delta = Math.abs(currentTemperature - targetTemperature);
		double ret = -1.0;
		if (currentTemperature > AirConditioningConnector.MAX_ADMISSIBLE_TEMP ||
				delta >= AirConditioningConnector.MAX_ADMISSIBLE_DELTA) {
			ret = 1.0;
		} else {
			ret = delta/AirConditioningConnector.MAX_ADMISSIBLE_DELTA;
		}
		
		assert	ret >= 0.0 && ret <= 1.0 :
			new PostconditionException("return >= 0.0 && return <= 1.0");

		return ret;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
