package production.aleatory.SolarPanel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import utils.ExecutionType;
import fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

public class 	SolarPanelController
extends		AbstractComponent
implements	SolarPanelPushImplementationI
{
	// -------------------------------------------------------------------------
	public static enum	ControlMode {
		PULL,
		PUSH
	}

    // -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, some methods trace their actions.						*/
	protected static boolean		VERBOSE = true;
	/** when true, some methods trace their actions.						*/
	public static boolean			DEBUG = true;

	/** the standard hysteresis used by the controller.						*/
	public static final double	STANDARD_HYSTERESIS = 0.1;
	/** standard control period in seconds.									*/
	public static final double	STANDARD_CONTROL_PERIOD = 60.0;

	/** the actual hysteresis used in the control loop.						*/
	protected double								hysteresis;
	/* user set control period in seconds.									*/
	protected double								controlPeriod;
	protected final ControlMode						controlMode;
	/* actual control period, either in pure real time (not under test)
	 * or in accelerated time (under test), expressed in nanoseconds;
	 * used for scheduling the control task.								*/
	protected long									actualControlPeriod;
	/** lock controlling the access to {@code currentState}.				*/
	protected final Object							stateLock;

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType					currentExecutionType;
	/** outbound port to connect to the centralised clock server.			*/
	protected ClocksServerOutboundPort				clockServerOBP;
	/** URI of the clock to be used to synchronise the test scenarios and
	 *  the simulation.														*/
	protected final String							clockURI;
	/** accelerated clock governing the timing of actions in the test
	 *  scenarios.															*/
	protected final CompletableFuture<AcceleratedClock>	clock;


	protected			SolarPanelController(
		) throws Exception
	{
		this(STANDARD_HYSTERESIS, STANDARD_CONTROL_PERIOD,
			 ControlMode.PULL);
	}

	protected			SolarPanelController(
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode
		) throws Exception
	{
		this(hysteresis, controlPeriod,ControlMode.PULL, ExecutionType.STANDARD, null);
	}

	protected			SolarPanelController(
		double hysteresis,
		double controlPeriod,
		ControlMode controlMode,
		ExecutionType currentExecutionType,
		String clockURI
		) throws Exception
	{
		super(2, 1);

		assert	hysteresis > 0.0 :
				new PreconditionException("hysteresis > 0.0");
		assert	controlPeriod > 0 :
				new PreconditionException("controlPeriod > 0");
		assert	!currentExecutionType.isUnitTest() :
				new PreconditionException("!currentExecutionType.isUnitTest()");
		assert	currentExecutionType.isStandard() ||
									clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"currentExecutionType.isStandard() || "
						+ "clockURI != null && !clockURI.isEmpty()");

		this.hysteresis = hysteresis;
		this.controlPeriod = controlPeriod;
		this.controlMode = controlMode;
		this.currentExecutionType = currentExecutionType;
		this.clockURI = clockURI;
		this.clock = new CompletableFuture<>();

		// just a common initialisation; if the run is in test mode, the
		// acceleration factor will be taken into account at start time
		// first convert to nanoseconds before casting to get a better
		// precision for fractional control periods
		this.actualControlPeriod =
				(long) (this.controlPeriod * TimeUnit.SECONDS.toNanos(1));
		this.stateLock = new Object();

		if (VERBOSE || DEBUG) {
			this.tracer.get().setTitle("SolarPanel controller component");
			this.tracer.get().setRelativePosition(2, 2);
			this.toggleTracing();
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		if (!this.currentExecutionType.isStandard()) {
			this.clockServerOBP = new ClocksServerOutboundPort(this);
			this.clockServerOBP.publishPort();
			this.doPortConnection(
					this.clockServerOBP.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			AcceleratedClock clock =
					this.clockServerOBP.getClock(this.clockURI);
			this.doPortDisconnection(this.clockServerOBP.getPortURI());
			this.clockServerOBP.unpublishPort();
			// the accelerated period is in nanoseconds, hence first convert
			// the period to nanoseconds, perform the division and then
			// convert to long (hence providing a better precision than
			// first dividing and then converting to nanoseconds...)
			this.actualControlPeriod =
				(long)((this.controlPeriod * TimeUnit.SECONDS.toNanos(1))/
												clock.getAccelerationFactor());
			// release readers so that we make sure that actualControlPeriod
			// has also been properly set before
			this.clock.complete(clock);
		}
	}


    @Override
    public void receiveDataFromSolarPanel(DataI sd) {
		assert	sd != null : new PreconditionException("sd != null");
		if (DEBUG) {
			this.traceMessage("receives solar panel sensor data: " + sd + ".\n");
		}
	}

}
