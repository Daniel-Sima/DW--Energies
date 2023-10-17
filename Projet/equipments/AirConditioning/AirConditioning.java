package equipments.AirConditioning;

import equipments.AirConditioning.connections.AirConditioningExternalControlInboundPort;
import equipments.AirConditioning.connections.AirConditioningInternalControlInboundPort;
import equipments.AirConditioning.connections.AirConditioningUserInboundPort;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>AirConditioning</code> is a AirConditioning component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code targetTemperature >= -20.0 && targetTemperature <= 50.0}
 * invariant	{@code currentPowerLevel >= 0.0 && currentPowerLevel <= MAX_POWER_LEVEL}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@OfferedInterfaces(offered={AirConditioningUserCI.class, AirConditioningInternalControlCI.class,
							AirConditioningExternalControlCI.class})
public class			AirConditioning
extends		AbstractComponent
implements	AirConditioningUserImplI,
			AirConditioningInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>AirConditioningState</code> describes the operation
	 * states of the AirConditioning.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
	 */
	protected static enum	AirConditioningState
	{
		/** AirConditioning is on.													*/
		ON,
		/** AirConditioning is cooling.												*/
		COOLING,
		/** AirConditioning is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** max power level of the AirConditioning, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;
	/** standard target temperature for the AirConditioning.							*/
	protected static final double	STANDARD_TARGET_TEMPERATURE = 19.0;

	/** URI of the AirConditioning port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"AirConditioning-USER-INBOUND-PORT-URI";
	/** URI of the AirConditioning port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"AirConditioning-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the AirConditioning port for external control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"AirConditioning-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_TEMPERATURE = 10.0;

	/** current state (on, off) of the AirConditioning.								*/
	protected AirConditioningState		currentState;
	/**	current power level of the AirConditioning.									*/
	protected double			currentPowerLevel;
	/** inbound port offering the <code>AirConditioningUserCI</code> interface.		*/
	protected AirConditioningUserInboundPort	hip;
	/** inbound port offering the <code>AirConditioningInternalControlCI</code>
	 *  interface.															*/
	protected AirConditioningInternalControlInboundPort	hicip;
	/** inbound port offering the <code>AirConditioningExternalControlCI</code>
	 *  interface.															*/
	protected AirConditioningExternalControlInboundPort	hecip;
	/** target temperature for the cooling.	*/
	protected double			targetTemperature;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception <i>to do</i>.
	 */
	protected			AirConditioning() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a new AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioning(
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(AirConditioningUserInboundPortURI,
						AirConditioningInternalControlInboundPortURI,
						AirConditioningExternalControlInboundPortURI);
	}

	/**
	 * create a new AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			AirConditioning(
		String reflectionInboundPortURI,
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(AirConditioningUserInboundPortURI,
						AirConditioningInternalControlInboundPortURI,
						AirConditioningExternalControlInboundPortURI);
	}

	/**
	 * create a new thermostated AirConditioning.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param AirConditioningUserInboundPortURI				URI of the inbound port to call the AirConditioning component for user interactions.
	 * @param AirConditioningInternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for internal control.
	 * @param AirConditioningExternalControlInboundPortURI	URI of the inbound port to call the AirConditioning component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String AirConditioningUserInboundPortURI,
		String AirConditioningInternalControlInboundPortURI,
		String AirConditioningExternalControlInboundPortURI
		) throws Exception
	{
		assert	AirConditioningUserInboundPortURI != null && !AirConditioningUserInboundPortURI.isEmpty();
		assert	AirConditioningInternalControlInboundPortURI != null && !AirConditioningInternalControlInboundPortURI.isEmpty();
		assert	AirConditioningExternalControlInboundPortURI != null && !AirConditioningExternalControlInboundPortURI.isEmpty();

		this.currentState = AirConditioningState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.hip = new AirConditioningUserInboundPort(AirConditioningUserInboundPortURI, this);
		this.hip.publishPort();
		this.hicip = new AirConditioningInternalControlInboundPort(
									AirConditioningInternalControlInboundPortURI, this);
		this.hicip.publishPort();
		this.hecip = new AirConditioningExternalControlInboundPort(
									AirConditioningExternalControlInboundPortURI, this);
		this.hecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("AirConditioning component");
			this.tracer.get().setRelativePosition(1, 2);
			this.toggleTracing();		
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hip.unpublishPort();
			this.hicip.unpublishPort();
			this.hecip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its state: " +
											this.currentState + ".\n");
		}
		
		return this.currentState == AirConditioningState.ON ||
									this.currentState == AirConditioningState.COOLING;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning switches on.\n");
		}

		assert !(this.currentState == AirConditioningState.ON) : new PreconditionException("!(this.currentState == AirConditioningState.ON)");

		this.currentState = AirConditioningState.ON;

		assert this.currentState == AirConditioningState.ON : new PostconditionException("this.currentState == AirConditioningState.ON");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning switches off.\n");
		}

		assert this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");

		this.currentState = AirConditioningState.OFF;

		assert !(this.currentState == AirConditioningState.ON) : new PostconditionException("!(this.currentState == AirConditioningState.ON)");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserImplI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning sets a new target temperature: " + target + "°.\n");
		}

		assert	target >= -50.0 && target <= 50.0 :
				new PreconditionException("target >= -50.0 && target <= 50.0");

		this.targetTemperature = target;

		assert	this.targetTemperature == target :
				new PostconditionException("this.targetTemperature == target");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its target temperature " + this.targetTemperature + "°.\n");
		}

		double ret = this.targetTemperature;

		assert	ret >= -50.0 && ret <= 50.0 :
				new PostconditionException("return >= -50.0 && return <= 50.0");

		return ret;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		assert this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");

		// Temporary implementation; would need a temperature sensor.
		double currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns the current temperature " + currentTemperature + "°.\n");
		}

		return  currentTemperature;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#cooling()
	 */
	@Override
	public boolean		cooling() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its cooling status: " + 
						(this.currentState == AirConditioningState.COOLING) + ".\n");
		}

		return this.currentState == AirConditioningState.COOLING;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning starts cooling.\n");
		}
		assert this.currentState == AirConditioningState.ON  : new PreconditionException("this.currentState == AirConditioningState.ON ");
		assert !(this.currentState == AirConditioningState.COOLING) : new PreconditionException("!(this.currentState == AirConditioningState.COOLING) ");

		this.currentState = AirConditioningState.COOLING;

		assert this.currentState == AirConditioningState.COOLING : new PostconditionException("this.currentState == AirConditioningState.COOLING;");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningInternalControlI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning stops cooling.\n");
		}
		assert	this.currentState == AirConditioningState.COOLING : new PreconditionException("this.currentState == AirConditioningState.COOLING");

		this.currentState = AirConditioningState.ON;

		assert	!(this.currentState == AirConditioningState.COOLING) : new PostconditionException("!(this.currentState == AirConditioningState.COOLING)");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its max power level " + 
					MAX_POWER_LEVEL + "W.\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning sets its power level to " + 
														powerLevel + "W.\n");
		}

		assert	this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= MAX_POWER_LEVEL) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}

		assert	powerLevel > MAX_POWER_LEVEL || this.currentPowerLevel == powerLevel :
				new PostconditionException(
						"powerLevel > MAX_POWER_LEVEL || "
						+ "this.currentPowerLevel  == powerLevel");
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		if (AirConditioning.VERBOSE) {
			this.traceMessage("AirConditioning returns its current power level " + 
					this.currentPowerLevel + "W.\n");
		}

		assert	this.currentState == AirConditioningState.ON : new PreconditionException("this.currentState == AirConditioningState.ON");

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= MAX_POWER_LEVEL :
				new PostconditionException(
							"return >= 0.0 && return <= MAX_POWER_LEVEL");

		return this.currentPowerLevel;
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	public void printSeparator(String title) throws Exception {
		this.traceMessage("**********"+ title +"**********\n");
	}
}
// -----------------------------------------------------------------------------
