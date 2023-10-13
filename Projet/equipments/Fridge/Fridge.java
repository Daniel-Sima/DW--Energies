package equipments.Fridge;

import equipments.Fridge.connections.FridgeExternalControlInboundPort;
import equipments.Fridge.connections.FridgeInternalControlInboundPort;
import equipments.Fridge.connections.FridgeUserInboundPort;

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
 * The class <code>Fridge</code> a Fridge component.
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
@OfferedInterfaces(offered={FridgeUserCI.class, FridgeInternalControlCI.class,
							FridgeExternalControlCI.class})
public class			Fridge
extends		AbstractComponent
implements	FridgeUserImplI,
			FridgeInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>FridgeState</code> describes the operation
	 * states of the Fridge.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum	FridgeState
	{
		/** Fridge is on.													*/
		ON,
		/** Fridge is cooling.												*/
		COOLING,
		/** Fridge is off.													*/
		OFF
	}
	
	/**
	 * The enumeration <code>FridgeCompartment</code> describes the 
	 * compartment types of the Fridge.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2021-09-10</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static enum	FridgeCompartment
	{
		/** Fridge compartment is COOLER.													*/
		COOLER,
		/** Fridge compartment is FREEZER.												*/
		FREEZER
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** max power level of the Fridge, in watts.							*/
	protected static final double	MAX_POWER_LEVEL = 2000.0;

	/** URI of the Fridge port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"Fridge-USER-INBOUND-PORT-URI";
	/** URI of the Fridge port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"Fridge-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the Fridge port for external control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"Fridge-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean		VERBOSE = true;
	/** fake current 	*/
	public static final double		FAKE_CURRENT_COOLER_TEMPERATURE = -5.0;
	public static final double		FAKE_CURRENT_FREEZER_TEMPERATURE = 4.0;

	protected static final double STANDARD_TARGET_COOLER_TEMPERATURE = 4.0;
	protected static final double STANDARD_TARGET_FREEZER_TEMPERATURE = -18.0;

	/** current state (on, off) of the Fridge.								*/
	protected FridgeState		currentState;
	/** current compartment (cooler, freeze) of the Fridge.								*/
	protected FridgeCompartment	currentCompartment;
	/**	current power level of the Fridge.									*/
	protected double			currentPowerLevel;
	/** inbound port offering the <code>FridgeUserCI</code> interface.		*/
	protected FridgeUserInboundPort	fip;
	/** inbound port offering the <code>FridgeInternalControlCI</code>
	 *  interface.															*/
	protected FridgeInternalControlInboundPort	ficip;
	/** inbound port offering the <code>FridgeExternalControlCI</code>
	 *  interface.															*/
	protected FridgeExternalControlInboundPort	fecip;
	/** target temperature for the cooler compartment.	*/
	protected double			targetCoolerTemperature;
	/** target temperature for the freezer compartment.	*/
	protected double			targetFreezerTemperature;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new Fridge.
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
	protected			Fridge() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Fridge(
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(FridgeUserInboundPortURI,
						FridgeInternalControlInboundPortURI,
						FridgeExternalControlInboundPortURI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected			Fridge(
		String reflectionInboundPortURI,
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(FridgeUserInboundPortURI,
						FridgeInternalControlInboundPortURI,
						FridgeExternalControlInboundPortURI);
	}

	/**
	 * create a new Fridge.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty()}
	 * pre	{@code FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param FridgeUserInboundPortURI				URI of the inbound port to call the Fridge component for user interactions.
	 * @param FridgeInternalControlInboundPortURI	URI of the inbound port to call the Fridge component for internal control.
	 * @param FridgeExternalControlInboundPortURI	URI of the inbound port to call the Fridge component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void		initialise(
		String FridgeUserInboundPortURI,
		String FridgeInternalControlInboundPortURI,
		String FridgeExternalControlInboundPortURI
		) throws Exception
	{
		assert	FridgeUserInboundPortURI != null && !FridgeUserInboundPortURI.isEmpty();
		assert	FridgeInternalControlInboundPortURI != null && !FridgeInternalControlInboundPortURI.isEmpty();
		assert	FridgeExternalControlInboundPortURI != null && !FridgeExternalControlInboundPortURI.isEmpty();

		this.currentState = FridgeState.OFF;
		this.currentPowerLevel = MAX_POWER_LEVEL;
		this.targetCoolerTemperature = STANDARD_TARGET_COOLER_TEMPERATURE;
		this.targetCoolerTemperature = STANDARD_TARGET_FREEZER_TEMPERATURE;

		this.fip = new FridgeUserInboundPort(FridgeUserInboundPortURI, this);
		this.fip.publishPort();
		this.ficip = new FridgeInternalControlInboundPort(
									FridgeInternalControlInboundPortURI, this);
		this.ficip.publishPort();
		this.fecip = new FridgeExternalControlInboundPort(
									FridgeExternalControlInboundPortURI, this);
		this.fecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Fridge component");
			this.tracer.get().setRelativePosition(1, 1);
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
			this.fip.unpublishPort();
			this.ficip.unpublishPort();
			this.fecip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Fridge.FridgeUserImplI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its state: " +
											this.currentState + ".\n");
		}
		this.traceMessage(""+(this.currentState == FridgeState.ON ||
				this.currentState == FridgeState.COOLING)+"\n");
		
		return this.currentState == FridgeState.ON ||
									this.currentState == FridgeState.COOLING;
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = FridgeState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = FridgeState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}
	
	/**
	 * @see equipments.Fridge.FridgeUserImplI#setTargetFreezerTemperature(double targetFreezer)
	 */
	@Override
	public void	setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets a new target "
										+ "freezer temperature: " + targetFreezer + ".\n");
		}

		assert	targetFreezer >= -20.0 && targetFreezer <= 0.0 :
				new PreconditionException("target >= -25.0 && target <= 0.0");

		this.targetFreezerTemperature = targetFreezer;

		assert	this.getTargetFreezerTemperature() == targetFreezer :
				new PostconditionException("getTargetTemperature() == target");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetFreezerTemperature()
	 */
	@Override
	public double getTargetFreezerTemperature() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its freezer target"
							+ " temperature " + this.targetFreezerTemperature + ".\n");
		}

		double ret = this.targetFreezerTemperature;

		assert	ret >= -20.0 && ret <= 0.0 :
				new PostconditionException("return >= -25.0 && return <= 0.0");

		return ret;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentFreezeTemperature()
	 */
	@Override
	public double getCurrentFreezerTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentFreezerTemperature = FAKE_CURRENT_FREEZER_TEMPERATURE;
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns the current"
							+ " freezer temperature " + currentFreezerTemperature + ".\n");
		}

		return  currentFreezerTemperature;
	}

	/**
	 * @see equipments.Fridge.FridgeUserImplI#setTargetCoolerTemperature(double targetCooler)
	 */
	@Override
	public void	setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets a new target "
										+ "cooler temperature: " + targetCooler + ".\n");
		}

		assert	targetCooler >= 0.0 && targetCooler <= 15.0 :
				new PreconditionException("target >= 0.0 && target <= 15.0");

		this.targetCoolerTemperature = targetCooler;

		assert	this.getTargetCoolerTemperature() == targetCooler :
				new PostconditionException("getTargetTemperature() == target");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetCoolerTemperature()
	 */
	@Override
	public double getTargetCoolerTemperature() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its cooler target"
							+ " temperature " + this.targetCoolerTemperature + ".\n");
		}

		double ret = this.targetCoolerTemperature;

		assert	ret >= 0.0 && ret <= 15.0 :
				new PostconditionException("return >= 0.0 && return <= 15.0");

		return ret;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		double currentCoolerTemperature = FAKE_CURRENT_COOLER_TEMPERATURE;
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns the current"
							+ " cooler temperature " + currentCoolerTemperature + ".\n");
		}

		return  currentCoolerTemperature;
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#cooling()
	 */
	@Override
	public boolean		cooling() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its cooling status " + 
						(this.currentState == FridgeState.COOLING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == FridgeState.COOLING;
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#startCooling()
	 */
	@Override
	public void			startCooling() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge starts cooling.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.cooling() : new PreconditionException("!cooling()");

		this.currentState = FridgeState.COOLING;

		assert	this.cooling() : new PostconditionException("cooling()");
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#stopCooling()
	 */
	@Override
	public void			stopCooling() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge stops cooling.\\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.cooling() : new PreconditionException("cooling()");

		this.currentState = FridgeState.ON;

		assert	!this.cooling() : new PostconditionException("!cooling()");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge sets its power level to " + 
														powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel >= 0.0 : new PreconditionException("powerLevel >= 0.0");

		if (powerLevel <= getMaxPowerLevel()) {
			this.currentPowerLevel = powerLevel;
		} else {
			this.currentPowerLevel = MAX_POWER_LEVEL;
		}

		assert	powerLevel > getMaxPowerLevel() ||
										getCurrentPowerLevel() == powerLevel :
				new PostconditionException(
						"powerLevel > getMaxPowerLevel() || "
						+ "getCurrentPowerLevel() == powerLevel");
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		if (Fridge.VERBOSE) {
			this.traceMessage("Fridge returns its current power level " + 
					this.currentPowerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= getMaxPowerLevel() :
				new PostconditionException(
							"return >= 0.0 && return <= getMaxPowerLevel()");

		return this.currentPowerLevel;
	}
}
// -----------------------------------------------------------------------------
