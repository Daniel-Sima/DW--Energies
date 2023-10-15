package production.aleatory;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PreconditionException;
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>SolarPanel</code> is a solar panel component.
 *
 * <p><strong>Always on, not producing if the weather is bad</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code currentPowerLevelProduction >= 0.0 && currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION}
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
@OfferedInterfaces(offered={SolarPanelExternalControlCI.class, SolarPanelMeteoControlCI.class})
public class SolarPanel 
extends	AbstractComponent  
implements SolarPanelExternalControlI, SolarPanelMeteoControlI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/** max power level production of the solar panel, in watts.			*/
	protected static final double MAX_POWER_LEVEL_PRODUCTION = 400.0;

	/** URI of the solar panel port for external control.				    */
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI =
			"SOLAR-PANEL-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the solar panel port for meteo control.				    */
	public static final String METEO_CONTROL_INBOUND_PORT_URI =
			"SOLAR-PANEL-METEO-CONTROL-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static final boolean VERBOSE = true;

	/**	current power level production of the solar panel.					*/
	protected double currentPowerLevelProduction;
	/** inbound port offering the <code>SolarPanelExternalControlCI</code>
	 *  interface.															*/
	protected SolarPanelExternalControlInboundPort solarPanelExternalControlInbound;
	/** inbound port offerinr the <code>SolarPanelMeteoControlCI</code> inteface */
	protected SolarPanelMeteoControlInboundPort solarPanelMeteoControlInboundPort;
	/***********************************************************************************/
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create a new solar panel.
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
	protected SolarPanel() throws Exception {
		this(EXTERNAL_CONTROL_INBOUND_PORT_URI, METEO_CONTROL_INBOUND_PORT_URI);
	}

	/***********************************************************************************/
	/**
	 * create a new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanel(String solarPanelExternalControlInboundPortURI, String solarPanelMeteoControlInboundPort) throws Exception{
		super(1, 0);
		this.initialise(solarPanelExternalControlInboundPortURI, solarPanelMeteoControlInboundPort);
	}
	/***********************************************************************************/
	/**
	 * create a new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI				URI of the reflection inbound port of the component.
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected SolarPanel(String reflectionInboundPortURI, String solarPanelExternalControlInboundPortURI, String solarPanelMeteoControlInboundPort) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(solarPanelExternalControlInboundPortURI, solarPanelMeteoControlInboundPort);
	}

	/***********************************************************************************/
	/**
	 * initialisation of the new solar panel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code solarPanelMeteoControlInboundPort != null && !solarPanelMeteoControlInboundPort.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param solarPanelExternalControlInboundPortURI	URI of the inbound port to call the solar panel component for external control.
	 * @throws Exception							<i>to do</i>.
	 */
	protected void initialise(
			String solarPanelExternalControlInboundPortURI,
			String solarPanelMeteoControlInboundPort
			) throws Exception
	{
		assert	solarPanelExternalControlInboundPortURI != null && !solarPanelExternalControlInboundPortURI.isEmpty();

		this.currentPowerLevelProduction = 0;

		this.solarPanelExternalControlInbound = new SolarPanelExternalControlInboundPort(
				solarPanelExternalControlInboundPortURI, this);
		this.solarPanelExternalControlInbound.publishPort();
		
		this.solarPanelMeteoControlInboundPort = new SolarPanelMeteoControlInboundPort(solarPanelMeteoControlInboundPort, this);
		this.solarPanelMeteoControlInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Solar panel component");
			this.tracer.get().setRelativePosition(1, 1);
			this.toggleTracing();		
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.solarPanelExternalControlInbound.unpublishPort();
			this.solarPanelMeteoControlInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getMaxPowerLevelProduction() throws Exception {
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel max power production level production is "+ this.MAX_POWER_LEVEL_PRODUCTION + ".\n");
		}
		return MAX_POWER_LEVEL_PRODUCTION;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentPowerLevelProduction() throws Exception {
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel current power production level production is "+ this.currentPowerLevelProduction + ".\n");
		}
		return currentPowerLevelProduction;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void setPowerLevelProduction(double percentage) throws Exception {
		assert	percentage >= 0 && percentage <= 100 : new PreconditionException("percentage > 0 && percentage < 100");
		
		// TODO mby to change
		this.currentPowerLevelProduction = 4 * percentage; 
		
		if (SolarPanel.VERBOSE) {
			this.traceMessage("Solar panel power production level changed to "+ this.currentPowerLevelProduction + ".\n");
		}
		
		assert	this.currentPowerLevelProduction >= 0 && this.currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION : new PreconditionException("this.currentPowerLevelProduction >= 0 && this.currentPowerLevelProduction <= MAX_POWER_LEVEL_PRODUCTION ");
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/