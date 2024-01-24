package global;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.exceptions.PreconditionException;
import mil.MILComponentSimulationArchitectures;
import sil.SILComponentSimulationArchitectures;
import utils.ExecutionType;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>GlobalSupervisor</code> implements the supervisor component
 * for simulated runs of the HEM project.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In BCM-CyPhy-Components, simulated runs execute both the components and their
 * DEVS simulators. In this case, the supervisor component is responsible for
 * the creation, initialisation and execution of the global component simulation
 * architecture using models disseminated into the different application
 * components.
 * </p>
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
 * <p>Created on : 2024-01-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class GlobalSupervisor 
extends	AbstractCyPhyComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the simulation architecture when a MIL simulation is
	 *  executed.															*/
	public static final String	MIL_SIM_ARCHITECTURE_URI = "hem-mil-simulator";
	/** URI of the simulation architecture when a MIL real time
	 *  simulation is executed.												*/
	public static final String	MIL_RT_SIM_ARCHITECTURE_URI =
			"hem-mil-rt-simulator";
	/** URI of the simulation architecture when a SIL simulation is
	 *  executed.															*/
	public static final String	SIL_SIM_ARCHITECTURE_URI = "hem-sil-simulator";

	// Execution/Simulation

	/** current type of execution.											*/
	protected final ExecutionType	currentExecutionType;
	/** URI of the simulation architecture to be created or the empty string
	 *  if the component does not execute as a SIL simulation.				*/
	protected final String			simArchitectureURI;
	/** start time for the real time simulation in milliseconds.			*/
	protected final long			unixEpochStartTimeInMillis;
	/** start time of the simulation, in simulated logical time.			*/
	protected final double 			simulatedStartTime;
	/** duration of the simulation, in simulated logical time.				*/
	protected final double			simulationDuration;
	/** time unit in which {@code simulatedStartTime} and
	 *  {@code simulationDuration} are expressed.							*/
	protected final TimeUnit		simulationTimeUnit;
	/** acceleration factor, if a real time simulation is executed.			*/
	protected final double			accelerationFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param currentExecutionType			current execution type for the next run.
	 * @param simArchitectureURI			URI of the simulation architecture to be created or the empty string if the component does not execute as a simulation.
	 * @param unixEpochStartTimeInMillis	start time for the real time simulation in milliseconds.
	 * @param simulatedStartTime			time of start of the simulation, in simulated time.
	 * @param simulationDuration			duration of the simulation, in simulated time.
	 * @param simulationTimeUnit			time unit in which {@code simulatedStartTime} and {@code simulationDuration} are expressed.
	 * @param accelerationFactor			acceleration factor for real time simulations.
	 */
	protected			GlobalSupervisor(
			ExecutionType currentExecutionType,
			String simArchitectureURI,
			long unixEpochStartTimeInMillis,
			double simulatedStartTime,
			double simulationDuration,
			TimeUnit simulationTimeUnit,
			double accelerationFactor
			)
	{
		// one thread for execute and one for report reception
		super(2, 0);

		assert	currentExecutionType != null :
			new PreconditionException("currentExecutionType != null");
		assert	currentExecutionType.isSimulated() || 
		(simArchitectureURI != null &&
		!simArchitectureURI.isEmpty()) :
			new PreconditionException(
					"currentExecutionType.isSimulated() ||  "
							+ "(simArchitectureURI != null && "
							+ "!simArchitectureURI.isEmpty())");

		this.currentExecutionType = currentExecutionType;
		this.simArchitectureURI = simArchitectureURI;
		this.unixEpochStartTimeInMillis = unixEpochStartTimeInMillis;
		this.simulatedStartTime = simulatedStartTime;
		this.simulationDuration = simulationDuration;
		this.simulationTimeUnit = simulationTimeUnit;
		this.accelerationFactor = accelerationFactor;

		this.tracer.get().setTitle("Global supervisor");
		this.tracer.get().setRelativePosition(0, 1);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void	execute() throws Exception {
		switch (this.currentExecutionType) {
		case MIL_SIMULATION:
			ComponentModelArchitecture cma =
			MILComponentSimulationArchitectures.
			createMILComponentSimulationArchitectures(
					this.simArchitectureURI);
			SupervisorPlugin sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.doStandAloneSimulation(0.0, this.simulationDuration);
			this.logMessage("simulation ends.");
			break;
		case MIL_RT_SIMULATION:
			cma = MILComponentSimulationArchitectures.
			createMILRTComponentSimulationArchitectures(
					this.simArchitectureURI,
					this.accelerationFactor);
			sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.MIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.startRTSimulation(this.unixEpochStartTimeInMillis,
					this.simulatedStartTime,
					this.simulationDuration);
			// For real time simulations, simulationDuration schedules tasks
			// and releases control immediately; hence, we must wait for the
			// entire duration of the simulation execution before getting the
			// final report of the simulation run.
			long executionDuration =
					TimeUnit.NANOSECONDS.toMillis(
							(long) (simulationTimeUnit.toNanos(1)*
									(this.simulationDuration/this.accelerationFactor)));
			Thread.sleep(CVMGlobalTest.DELAY_TO_START_SIMULATION +
					executionDuration + 2000L);
			this.logMessage(sp.getFinalReport().toString());
			break;
		case SIL_SIMULATION:
			cma = SILComponentSimulationArchitectures.
			createSILComponentSimulationArchitectures(
					this.simArchitectureURI,
					this.accelerationFactor);
			sp = new SupervisorPlugin(cma);
			sp.setPluginURI(GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI);
			this.installPlugin(sp);
			this.logMessage("plug-in installed.");
			sp.constructSimulator();
			this.logMessage("simulator constructed, simulation begins.");
			sp.setSimulationRunParameters(new HashMap<>());
			sp.startRTSimulation(this.unixEpochStartTimeInMillis,
					this.simulatedStartTime,
					this.simulationDuration);
			// For real time simulations, simulationDuration schedules tasks
			// and releases control immediately; hence, we must wait for the
			// entire duration of the simulation execution before getting the
			// final report of the simulation run.
			executionDuration =
					TimeUnit.NANOSECONDS.toMillis(
							(long) (simulationTimeUnit.toNanos(1)*
									(this.simulationDuration/this.accelerationFactor)));
			Thread.sleep(CVMGlobalTest.DELAY_TO_START_SIMULATION +
					executionDuration + 2000L);
			this.logMessage(sp.getFinalReport().toString());
			break;
		case STANDARD:
		case INTEGRATION_TEST:
		default:
		}		
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

