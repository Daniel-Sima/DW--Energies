package equipments.AirConditioning.mil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.AirConditioning.AirConditioningState;
import equipments.AirConditioning.mil.events.AirConditioningEventI;
import equipments.AirConditioning.mil.events.Cool;
import equipments.AirConditioning.mil.events.DoNotCool;
import equipments.AirConditioning.mil.events.SetPowerAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOffAirConditioning;
import equipments.AirConditioning.mil.events.SwitchOnAirConditioning;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;


@ModelExternalEvents(imported = {SwitchOnAirConditioning.class,
		 SwitchOffAirConditioning.class,
		 SetPowerAirConditioning.class,
		 Cool.class,
		 DoNotCool.class},
exported = {SwitchOnAirConditioning.class,
	 	 SwitchOffAirConditioning.class,
	 	 SetPowerAirConditioning.class,
	 	 Cool.class,
	 	 DoNotCool.class})
public class AirConditioningStateModel
extends AtomicModel
implements AirConditioningOperationI{

	public static enum	State {
		/** heater is on but not heating.									*/
		ON,
		/** heater is on and heating.										*/
		COOLING,
		/** heater is off.													*/
		OFF
	}
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a MIL model; works when only one instance is created.		*/
	public static final String	MIL_URI = AirConditioningStateModel.class.
													getSimpleName() + "-MIL";
	/** URI for a MIL real time model; works when only one instance is
	 *  created.															*/
	public static final String	MIL_RT_URI = AirConditioningStateModel.class.
													getSimpleName() + "-MIL-RT";
	/** URI for a SIL model; works when only one instance is created.		*/
	public static final String	SIL_URI = AirConditioningStateModel.class.
													getSimpleName() + "-SIL";
	
	/** current state of the air conditioning.										*/
	protected State			currentState = State.OFF;
	/** external event that has been received and that must be reemitted.	*/
	protected EventI		toBeReemitted;
				
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public		AirConditioningStateModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}
	
	/**
	 * @see equipments.AirConditioning.mil.AirConditioningOperationI#setState(equipments.AirConditioning.AirConditioning.AirConditioningState)
	 */
	@Override
	public void			setState(State s)
	{
		this.currentState = s;
	}

	/**
	 * @see equipments.AirConditioning.mil.AirConditioningOperationI#getState()
	 */
	@Override
	public State		getState()
	{
		return this.currentState;
	}

	/**
	 * @see equipments.AirConditioning.mil.AirConditioningOperationI#setCurrentHeatingPower(double, fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			setCurrentCoolingPower(double newPower, Time t)
	{
		// Nothing to be done here
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.toBeReemitted == null) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(getSimulatedTimeUnit());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.toBeReemitted != null) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			ret.add(this.toBeReemitted);
			this.toBeReemitted = null;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the air conditioning model, there will be exactly one by
		// construction.
		assert	currentEvents != null && currentEvents.size() == 1;

		this.toBeReemitted = (Event) currentEvents.get(0);
		assert	this.toBeReemitted instanceof AirConditioningEventI;
		this.toBeReemitted.executeOn(this);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.\n");
	}
	
	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(
			Map<String, Object> simParams)
	throws MissingRunParameterException
	{
		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			// by the following, all of the logging will appear in the owner
			// component logger
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}
}
