package equipments.AirConditioning.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning.PowerValue;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
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
 * The class <code>AirConditioningUnitTesterModel</code> defines a model that is used
 * to test the models defining the AirConditioning simulator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 *   {@code SwitchOnAirConditioning},
 *   {@code SwitchOffAirConditioning},
 *   {@code Cool},
 *   {@code DoNotCool},
 *   {@code SetPowerAirConditioning}</li>
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
 */
@ModelExternalEvents(exported = {SwitchOnAirConditioning.class,
		SwitchOffAirConditioning.class,
		Cool.class,
		DoNotCool.class,
		SetPowerAirConditioning.class})
public class AirConditioningUnitTesterModel 
extends AtomicModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = AirConditioningUnitTesterModel.class.getSimpleName();

	/** steps in the test scenario.											*/
	protected int step;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a <code>AirConditioningUnitTesterModel</code> instance.
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
	public AirConditioningUnitTesterModel(
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
				ret.add(new SwitchOnAirConditioning(this.getTimeOfNextEvent()));
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
				ret.add(new SetPowerAirConditioning(this.getTimeOfNextEvent(),
						new PowerValue(800.0)));
				break;
			case 6:
				ret.add(new SetPowerAirConditioning(this.getTimeOfNextEvent(),
						new PowerValue(1200.0)));
				break;
			case 7:
				ret.add(new SetPowerAirConditioning(this.getTimeOfNextEvent(),
						new PowerValue(1500.0)));
				break;
			case 8:
				ret.add(new SetPowerAirConditioning(this.getTimeOfNextEvent(),
						new PowerValue(2000.0)));
				break;
			case 9:
				ret.add(new SwitchOffAirConditioning(this.getTimeOfNextEvent()));
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
