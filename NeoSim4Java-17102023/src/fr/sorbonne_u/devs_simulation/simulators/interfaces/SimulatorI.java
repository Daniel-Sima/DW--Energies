package fr.sorbonne_u.devs_simulation.simulators.interfaces;

import java.io.Serializable;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
// import fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI;
// import fr.sorbonne_u.devs_simulation.models.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SimulationI</code> declares the core behaviour of
 * DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-02-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SimulatorI
extends		ParentNotificationI,
			VariableInitialisationI,
			SimulationManagementI
{
	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * associate the provided simulation model with the simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedModel != null}
	 * pre	{@code !isModelSet()}
	 * post	{@code isModelSet()}
	 * </pre>
	 *
	 * @param simulatedModel	model to be simulated.
	 */
	public void			setSimulatedModel(ModelI simulatedModel);

	/**
	 * return true if the simulator has been given its model to be simulated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulator has been given its model to be simulated.
	 */
	public boolean		isModelSet();

	/**
	 * return the model simulated by this simulation engine or null if not set
	 * yet.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the model simulated by this simulation engine or null if not set yet.
	 */
	public ModelI		getSimulatedModel();

	/**
	 * return the URI of the simulation engine acting as proxy to the given
	 * simulation model.
	 * 
	 * <p>
	 * As coupled models can be seen as atomic models (by the closure under
	 * composition property of DEVS models), models need not have a dedicated
	 * simulation engine but rather can share the simulation engine attached to
	 * on of their parent coupled model. This method provides a unique mean to
	 * get the reference on the simulation engine enacting the given model.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the simulation model.
	 * @return				the URI of the simulation engine acting as proxy to the given simulation model.
	 */
	public String		findProxyAtomicEngineURI(String modelURI);

	/**
	 * return the reference to the atomic engine as an event exchanging entity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicEngineURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param atomicEngineURI	the URI of the atomic engine which reference is sought.
	 * @return					the reference to the atomic engine as an event exchanging entity.
	 */
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		);

	/**
	 * return true if the simulator is a real time simulator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulator is a real time simulator.
	 */
	default boolean		isRealTime()
	{
		return false;
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * return true if the parent simulation engine of this engine is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the parent simulation engine of this engine is set.
	 */
	public boolean		isParentSet();

	/**
	 * set the parent simulation engine of this engine to {@code c}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isParentSet()}
	 * pre	{@code c != null}
	 * post	{@code isParentSet()}
	 * </pre>
	 *
	 * @param c	parent coordinator engine.	
	 */
	public void			setParent(CoordinatorI c);

	/**
	 * return the reference to the parent simulation engine or null of if it is
	 * the simulation engine of the root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the reference to the parent simulation engine or null of if it is the engine of the root model.
	 */
	public CoordinatorI	getParent();

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	default void		setSimulationRunParameters(
		Map<String, Serializable> simParams
		) throws MissingRunParameterException
	{
		getSimulatedModel().setSimulationRunParameters(simParams);
	}

	/**
	 * initialise the simulation engine for a run with a time of start set to 0;
	 * for runs that will be stopped by a call to <code>stopSimulation</code>,
	 * the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationDuration != null}
	 * pre	{@code simulationDuration.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationDuration.greaterThan(Duration.zero(getSimulatedTimeUnit()))}
	 * pre	{@code isModelSet()}
	 * pre	{@code !isSimulationInitialised()}
	 * post	{@code isSimulationInitialised()}
	 * </pre>
	 *
	 * @param simulationDuration	duration of the simulation to be launched.
	 */
	public void			initialiseSimulation(Duration simulationDuration);

	/**
	 * initialise the simulation engine for a run with a time of start set to
	 * the given time; for runs that will be stopped by a call to
	 * <code>stopSimulation</code>, the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationStartTime != null}
	 * pre	{@code simulationStartTime.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationStartTime.greaterThanOrEqual(Time.zero(getSimulatedTimeUnit()))}
	 * pre	{@code simulationDuration != null}
	 * pre	{@code simulationDuration.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationDuration.greaterThan(Duration.zero(getSimulatedTimeUnit()))}
	 * pre	{@code isModelSet()}
	 * pre	{@code !isSimulationInitialised()}
	 * post	{@code isSimulationInitialised()}
	 * </pre>
	 *
	 * @param simulationStartTime	time at which the simulation must start.
	 * @param simulationDuration	duration of the simulation to be launched.
	 */
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		);

	/**
	 * return true if the simulation has been initialised for a run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulation has been initialised.
	 */
	public boolean		isSimulationInitialised();

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * process the next internal event by advancing the simulation time to
	 * the forecast time of occurrence of this and then do the transition to
	 * the next state in the corresponding simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationInitialised()}
	 * pre	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getTimeOfLastEvent()))}
	 * post	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getTimeOfLastEvent()))}
	 * </pre>
	 *
	 */
	public void			internalEventStep();

	/**
	 * process an external input event at a simulated time which must
	 * correspond to the current value of the global simulated time clock
	 * at the time of the call i.e., the sum of the time of the last
	 * event in this engine and the given elapsed time.
	 * 
	 * <p>
	 * The processing first cancel the previously forecast internal event
	 * if any and then make the transition to a new model state given the
	 * processed external event and then forecast a new internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code elapsedTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	time elapsed since the last event executed by this engine.
	 */
	public void			externalEventStep(Duration elapsedTime);

	/**
	 * process an external input event or an internal event at the given
	 * simulated time which must correspond to the current value of the
	 * simulated time clock at the time of the call; when more than on event
	 * can be executed, only one is chosen and executed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code elapsedTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param elapsedTime	time elapsed since the last event executed by this engine.
	 */
	public void			confluentEventStep(Duration elapsedTime);

	/**
	 * force the model to forward externals event generated before their
	 * next internal transition step.
	 * 
	 * <p>
	 * The implementation follows the principle of the "peer message
	 * exchanging implementation" of DEVS i.e., the simulators exchanges
	 * directly with each others the exported and imported events without
	 * passing through their parent (and ancestors).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current		current simulation time at which external events may be output.
	 */
	public void			produceOutput(Time current);

	/**
	 * return the time of occurrence of the last event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the time of occurrence of the last event.
	 */
	public Time			getTimeOfLastEvent();

	/**
	 * return the currently forecast time of occurrence of the next event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the currently forecast time of occurrence of the next event.
	 */
	public Time			getTimeOfNextEvent();

	/**
	 * return the duration until the next internal event as previously
	 * computed by <code>timeAdvance()</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the duration until the next internal event as previously computed by <code>timeAdvance()</code>.
	 */
	public Duration		getNextTimeAdvance();

	/**
	 * terminate the current simulation, doing the necessary catering tasks.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code endTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param endTime		time at which the simulation ends.
	 */
	public void			endSimulation(Time endTime);

	// -------------------------------------------------------------------------
	// Logging
	// -------------------------------------------------------------------------

	/**
	 * return true if the logger is set, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the logger is set, false otherwise.
	 */
	public boolean		isLoggerSet();

	/**
	 * set the logger of this model i.e. an object through which
	 * logging can be done during simulation runs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code logger != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param logger	the logger to be set.
	 */
	public void			setLogger(MessageLoggingI logger);

	/**
	 * log a message through the logger or do nothing if no logger is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of the simulation model issuing the message.
	 * @param message	message to be logged.
	 */
	public void			logMessage(String modelURI, String message);

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * activate the lowest debug level or deactivate the debug mode completely.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			toggleDebugMode();

	/**
	 * return true if the debug level is greater than 0.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the debug level is greater than 0.
	 */
	public boolean		isDebugModeOn();

	/**
	 * set the debug level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newDebugLevel >= 0}
	 * post	{@code hasDebugLevel(newDebugLevel)}
	 * </pre>
	 *
	 * @param newDebugLevel	the new debug level of the model
	 */
	public void			setDebugLevel(int newDebugLevel);

	/**
	 * true if the current debug level is equal to <code>debugLevel</code>,
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code debugLevel >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param debugLevel	a debug level to be tested.
	 * @return				true if the current debug level is equal to <code>debugLevel</code>.
	 */
	public boolean		hasDebugLevel(int debugLevel);

	/**
	 * return the model information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indenting string.
	 * @return				the model information as a string.
	 */
	public String		modelAsString(String indent);

	/**
	 * return the simulator information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the simulator information as a string.
	 */
	public String		simulatorAsString();

	/**
	 * print the current state.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 */
	public void			showCurrentState(String indent,Duration elapsedTime);

	/**
	 * print the content of the current state, without pre- and post-formatting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		);
}
// -----------------------------------------------------------------------------
