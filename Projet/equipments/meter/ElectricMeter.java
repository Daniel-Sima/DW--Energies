package equipments.meter;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>ElectricMeter</code> implements a simplified electric meter
 * component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
@OfferedInterfaces(offered={ElectricMeterCI.class})
public class ElectricMeter 
extends	AbstractComponent
implements ElectricMeterImplementationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the electric meter inbound port used in tests.				*/
	public static final String ELECTRIC_METER_INBOUND_PORT_URI =
			"ELECTRIC-METER";
	/** when true, methods trace their actions.								*/
	public static final boolean	VERBOSE = true;

	/** inbound port offering the <code>ElectricMeterCI</code> interface.	*/
	protected ElectricMeterInboundPort electricMeterInboundPort;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ELECTRIC_METER_INBOUND_PORT_URI != null && !ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected ElectricMeter() throws Exception {
		this(ELECTRIC_METER_INBOUND_PORT_URI);

		assert	ELECTRIC_METER_INBOUND_PORT_URI != null &&
				!ELECTRIC_METER_INBOUND_PORT_URI.isEmpty() :
					new PreconditionException(
							"ELECTRIC_METER_INBOUND_PORT_URI != null && "
									+ "!ELECTRIC_METER_INBOUND_PORT_URI.isEmpty()");
	}

	/***********************************************************************************/
	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected ElectricMeter(String electricMeterInboundPortURI) throws Exception {
		this(electricMeterInboundPortURI, 1, 0);

		assert	electricMeterInboundPortURI != null &&
				!electricMeterInboundPortURI.isEmpty() :
					new PreconditionException(
							"electricMeterInboundPortURI != null && "
									+ "!electricMeterInboundPortURI.isEmpty()");
	}

	/***********************************************************************************/
	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param nbThreads						number of standard threads.
	 * @param nbSchedulableThreads			number of schedulable threads.
	 * @throws Exception					<i>to do</i>.
	 */
	protected ElectricMeter(
			String electricMeterInboundPortURI,
			int nbThreads,
			int nbSchedulableThreads
			) throws Exception
	{
		super(nbThreads, nbSchedulableThreads);

		assert	electricMeterInboundPortURI != null &&
				!electricMeterInboundPortURI.isEmpty() :
					new PreconditionException(
							"electricMeterInboundPortURI != null && "
									+ "!electricMeterInboundPortURI.isEmpty()");

		this.initialise(electricMeterInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * create an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI		URI of the reflection innbound port of the component.
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @param nbThreads						number of standard threads.
	 * @param nbSchedulableThreads			number of schedulable threads.
	 * @throws Exception					<i>to do</i>.
	 */
	protected ElectricMeter(
			String reflectionInboundPortURI,
			String electricMeterInboundPortURI,
			int nbThreads,
			int nbSchedulableThreads
			) throws Exception
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		assert	electricMeterInboundPortURI != null &&
				!electricMeterInboundPortURI.isEmpty() :
					new PreconditionException(
							"electricMeterInboundPortURI != null && "
									+ "!electricMeterInboundPortURI.isEmpty()");

		this.initialise(electricMeterInboundPortURI);
	}

	/***********************************************************************************/
	/**
	 * initialise an electric meter component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code electricMeterInboundPortURI != null && !electricMeterInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param electricMeterInboundPortURI	URI of the electric meter inbound port.
	 * @throws Exception					<i>to do</i>.
	 */
	protected void	initialise(String electricMeterInboundPortURI)
			throws Exception
	{
		assert	electricMeterInboundPortURI != null &&
				!electricMeterInboundPortURI.isEmpty() :
					new PreconditionException(
							"electricMeterInboundPortURI != null && "
									+ "!electricMeterInboundPortURI.isEmpty()");

		this.electricMeterInboundPort =
				new ElectricMeterInboundPort(electricMeterInboundPortURI, this);
		this.electricMeterInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Electric meter component");
			this.tracer.get().setRelativePosition(1, 0);
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
			this.electricMeterInboundPort.unpublishPort();
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
	public double getCurrentConsumption() throws Exception {
		if (VERBOSE) {
			this.traceMessage("Electric meter returns its current consumption.\n");
		}

		// TODO will need a computation.
		double ret = 0.0;

		assert ret >= 0.0 : new PostconditionException("ret >= 0.0");

		return ret;
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public double getCurrentProduction() throws Exception {
		if (VERBOSE) {
			this.traceMessage("Electric meter returns its current production.\n");
		}

		// TODO will need a computation.
		double ret = 0.0;

		assert	ret >= 0.0 : new PostconditionException("ret >= 0.0");

		return ret;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
