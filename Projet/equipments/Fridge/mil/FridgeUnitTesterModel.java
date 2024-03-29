package equipments.Fridge.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import equipments.Fridge.mil.events.Cool;
import equipments.Fridge.mil.events.DoNotCool;
import equipments.Fridge.mil.events.SetPowerFridge;
import equipments.Fridge.mil.events.SetPowerFridge.PowerValue;
import equipments.Fridge.mil.events.SwitchOffFridge;
import equipments.Fridge.mil.events.SwitchOnFridge;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>FridgeUnitTesterModel</code> defines a model that is used
 * to test the models defining the Fridge simulator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnFridge},
 *   {@code SwitchOffFridge},
 *   {@code Cool},
 *   {@code DoNotCool},
 *   {@code SetPowerFridge}</li>
 * </ul>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code step >= 0}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
@ModelExternalEvents(exported = {SwitchOnFridge.class,
		SwitchOffFridge.class,
		Cool.class,
		DoNotCool.class,
		SetPowerFridge.class})
public class FridgeUnitTesterModel 
extends AtomicModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = FridgeUnitTesterModel.class.getSimpleName();

	/** steps in the test scenario.											*/
	protected int step;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>FridgeUnitTesterModel</code> instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public FridgeUnitTesterModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine
			) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);
		this.step = 1;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// Simple way to implement a test scenario. Here each step generates
		// an event sent to the other models in the standard order.
		if (this.step > 0 && this.step < 11) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			switch (this.step) {
			case 1:
				ret.add(new SwitchOnFridge(this.getTimeOfNextEvent()));
				break;
			case 2:
				ret.add(new Cool(this.getTimeOfNextEvent())); // init at 1200W
				break;
			case 3:
				ret.add(new DoNotCool(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new Cool(this.getTimeOfNextEvent()));
				break;
			case 5:
				ret.add(new SetPowerFridge(this.getTimeOfNextEvent(),
						new PowerValue(100.0)));
				break;
			case 6:
				ret.add(new SetPowerFridge(this.getTimeOfNextEvent(),
						new PowerValue(300.0)));
				break;
			case 7:
				ret.add(new SetPowerFridge(this.getTimeOfNextEvent(),
						new PowerValue(400.0)));
				break;
			case 8:
				ret.add(new SetPowerFridge(this.getTimeOfNextEvent(),
						new PowerValue(500.0)));
				break;
			case 9:
				ret.add(new SwitchOffFridge(this.getTimeOfNextEvent()));
				break;
			}
			return ret;
		} else {
			return null;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		// As long as events have to be created and sent, the next internal
		// transition is set at one second later, otherwise, no more internal
		// transitions are triggered (delay = infinity).
		if (this.step < 10) {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);

		// advance to the next step in the scenario
		this.step++;
	}

	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) {
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
