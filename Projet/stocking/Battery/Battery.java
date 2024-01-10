package stocking.Battery;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import stocking.Battery.connections.BatteryExternalControlInboundPort;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>Battery</code> is a battery component.
 *
 * <p><strong>Husehold battery</strong> whit 5kWh capacity.</p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentPowerLevel >= 0.0 && currentPowerLevel <= MAX_POWER_CAPACITY}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@OfferedInterfaces(offered={BatteryExternalControlCI.class})
public class Battery 
extends AbstractComponent
implements BatteryExternalControlI{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>BatteryState</code> describes the operation
	 * states of the Battery.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2023-10-15</p>
	 * 
	 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
	 */
	public static enum BatteryState {
		/** Battery is producing (destocking energy).						*/
		PRODUCING,
		/** Battery is consuming (stocking energy).							*/
		CONSUMING,
		/** Battery is consuming and producing at the same time 			*/
		PRODUCING_AND_CONSUMING,
	}

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/** max power capacity of the Battery, in W/h.				   			*/
	protected static final double MAX_POWER_CAPACITY = 5000.0; // 5 kW

	/** URI of the petrol generator port for external control.				*/
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI =			
			"BATTERY-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;

	/** current state (PRODUCING, CONSUMING, PRODUCING_AND_CONSUMING) 
	 * of the Battery.														*/
	protected BatteryState currentState;
	/**	current power level produced (destocking) or consumed by (stocking) 
	 *  of the Battery, depending of the currentState						*/
	protected double currentPowerLevel;
	/** inbound port offering the BatteryExternalControlCI interface		*/
	protected BatteryExternalControlInboundPort	batteryExternalControlInboundPort; 

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a new Battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	protected Battery() throws Exception{
		this(EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a new Battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param batteryExternalControlInboundPortURI	URI of the inbound port to call the Battery component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected Battery(String batteryExternalControlInboundPortURI) throws Exception
	{
		super(1, 0);
		this.initialise(batteryExternalControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create a new Battery.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param batteryExternalControlInboundPortURI	URI of the inbound port to call the Battery component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected Battery(
			String reflectionInboundPortURI,
			String batteryExternalControlInboundPortURI
			) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(batteryExternalControlInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * initialise the Battery
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param batteryExternalControlInboundPortURI	URI of the inbound port to call the Battery component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String batteryExternalControlInboundPortURI
			) throws Exception
	{
		assert	batteryExternalControlInboundPortURI != null && !batteryExternalControlInboundPortURI.isEmpty();

		this.currentState = BatteryState.CONSUMING;  // by default
		this.currentPowerLevel = 0;

		this.batteryExternalControlInboundPort = new BatteryExternalControlInboundPort(batteryExternalControlInboundPortURI, this);
		this.batteryExternalControlInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Battery component");
			this.tracer.get().setRelativePosition(1, 4);
			this.toggleTracing();		
		}
	}

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.batteryExternalControlInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/***********************************************************************************/
	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerCapacity() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery returns its max power capacity " + 
					MAX_POWER_CAPACITY + "W.\n");
		}

		return MAX_POWER_CAPACITY;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevel() throws Exception {
		if (Battery.VERBOSE) {
			if (this.currentState == BatteryState.PRODUCING) {
				this.traceMessage("Battery returns its current power production level " + 
						this.currentPowerLevel + "W.\n");
			} else if (this.currentState == BatteryState.CONSUMING) {
				this.traceMessage("Battery returns its current power consumption level " + 
						this.currentPowerLevel + "W.\n");
			} else {
				this.traceMessage("Battery returns its current power level " + this.currentPowerLevel + "W.\n");
			}
		}

		double ret = this.currentPowerLevel;

		assert	ret >= 0.0 && ret <= MAX_POWER_CAPACITY :
			new PostconditionException(
					"return >= 0.0 && return <= MAX_POWER_LEVEL_PRODUCTION");

		return this.currentPowerLevel;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public BatteryState getBatteryState() throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery returns its state : " + this.currentState + ".\n");
		}

		return this.currentState;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setBatteryState(BatteryState newState) throws Exception {
		if (Battery.VERBOSE) {
			this.traceMessage("Battery changes state in: " + newState + ".\n");
		}
		
		this.currentState = newState;
	}

	/***********************************************************************************/
	/**
	 * @see
	 * TODO a supprimer
	 */
	@Override
	public void addPowerBattery(double powerValue) throws Exception {
		assert this.currentState == BatteryState.CONSUMING : new PreconditionException("this.currentState == BatteryState.CONSUMING");
		
		if (Battery.VERBOSE) {
			this.traceMessage("Battery consumes (stocking): " + powerValue + "W.\n");
		}

		assert	powerValue >= 0.0 : new PreconditionException("powerValue >= 0.0");

		if (this.currentPowerLevel+powerValue <= MAX_POWER_CAPACITY) {
			this.currentPowerLevel = this.currentPowerLevel+powerValue;
		} else {
			this.traceMessage("Battery storage is full. \n");
			this.currentPowerLevel = MAX_POWER_CAPACITY;
		}
	}

	/***********************************************************************************/
	/**
	 * @see
 	 * TODO a supprimer
	 */
	@Override
	public void pullPowerBattery(double powerValue) throws Exception {
		assert this.currentState == BatteryState.PRODUCING : new PreconditionException("this.currentState == BatteryState.PRODUCING");
		
		if (Battery.VERBOSE) {
			this.traceMessage("Battery produces (destocking): " + powerValue + "W.\n");
		}

		assert	powerValue >= 0.0 : new PreconditionException("powerValue >= 0.0");
		assert  this.currentPowerLevel-powerValue > 0 : new PreconditionException("this.currentPowerLevel-powerValue > 0");

		this.currentPowerLevel -= powerValue;
	}	
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/