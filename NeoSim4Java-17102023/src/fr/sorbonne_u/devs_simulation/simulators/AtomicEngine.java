package fr.sorbonne_u.devs_simulation.simulators;

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

import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.AtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicEngine</code> implements the DEVS simulation protocol
 * to run simulation models that are either atomic or coupled models which
 * submodels all share this atomic engine (i.e., the same simulation algorithm).
 *
 * <p><strong>Description</strong></p>
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
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
/**
 * The class <code>AtomicEngine</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// TODO	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicEngine
extends		SimulationEngine
implements	AtomicSimulatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * set the atomic model executed by this simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedModel instanceof AtomicModelI}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	{
		// TODO Auto-generated method stub
		assert	simulatedModel instanceof AtomicModelI :
				new AssertionError("Precondition violation: "
								   + "simulatedModel instanceof AtomicModelI");

		super.setSimulatedModel(simulatedModel);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#findProxyAtomicEngineURI(java.lang.String)
	 */
	@Override
	public String		findProxyAtomicEngineURI(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new AssertionError("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	this.isModelSet() :
				new AssertionError("Precondition violation: isModelSet()");

		if (this.simulatedModel.getURI().equals(modelURI)) {
			return this.simulatedModel.getURI();
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getAtomicEngineReference(java.lang.String)
	 */
	@Override
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		)
	{
		assert	atomicEngineURI != null && !atomicEngineURI.isEmpty() :
				new AssertionError("Precondition violation: "
					+ "atomicEngineURI != null && !atomicEngineURI.isEmpty()");

		if (this.simulatedModel.getURI().equals(atomicEngineURI)) {
			return new AtomicSinkReference((AtomicModelI)this.simulatedModel);
		} else {
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// Simulation related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		)
	{
		assert	this.isModelSet() :
				new AssertionError("Precondition violation: isModelSet()");

		super.initialiseSimulation(simulationStartTime, simulationDuration);
		this.simulatedModel.initialiseState(this.simulationStartTime);
		if (!this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.initialiseVariables();
		}
		if (this.simulatedModel.isRoot() &&
						this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.fixpointInitialiseVariables();
		}
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised()
	{
		assert	this.isModelSet() :
				new AssertionError("Precondition violation: isModelSet()");

		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
								"AtomicEngine#isSimulationInitialised\n");
		}

		return super.isSimulationInitialised() &&
				this.simulatedModel.isStateInitialised() &&
				this.simulatedModel.allModelVariablesInitialised() &&
				this.getTimeOfNextEvent() != null &&
				this.getNextTimeAdvance() != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep()
	{
		assert	this.isModelSet() :
				new AssertionError("Precondition violation: isModelSet()");

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
									"AtomicEngine>>internalEventStep "
									+ this.simulatedModel.getURI() + ".\n");
		}
		this.simulatedModel.internalTransition();
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime();
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();

		assert	this.getNextTimeAdvance().equals(
									this.getTimeOfNextEvent().subtract(
													this.getTimeOfLastEvent())) :
														new AssertionError("Postcondition violation: ");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	{
		if (!this.simulatedModel.isRoot()) {
			this.getParent().hasReceivedExternalEvents(
												this.simulatedModel.getURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	{
		if (!this.simulatedModel.isRoot()) {
			this.getParent().hasPerformedExternalEvents(modelURI);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
				"AtomicEngine#produceOutput " + this.simulatedModel.getURI() +
				" " + current);
			if (!this.isSimulationInitialised()) {
				this.simulatedModel.logMessage(
						"%%% super.isSimulationInitialised() " +
									super.isSimulationInitialised());
				this.simulatedModel.logMessage(
						"%%% this.simulatedModel.isStateInitialised() " +
									this.simulatedModel.isStateInitialised());
				this.simulatedModel.logMessage(
						"%%% this.getTimeOfNextEvent() != null " +
									(this.getTimeOfNextEvent() != null));
				this.simulatedModel.logMessage(
						"%%% this.getNextTimeAdvance() != null " +
									(this.getNextTimeAdvance() != null));
			}
		}

		this.simulatedModel.produceOutput(current);
	}

	/**
	 * add the external events {@code es} to the model associated with this
	 * simulation engine and plan the execution of the external event step that
	 * will process them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isModelSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI	URI of the model to which {@code es} must be delivered.
	 * @param es				received events.
	 */
	public void			planExternalEventStep(
		String destinationURI,
		ArrayList<EventI> es
		)
	{
		if (this.hasDebugLevel(2)) {
			assert	this.isModelSet() :
					new AssertionError("Precondition violation: isModelSet()");
		}
		assert	getSimulatedModel().getURI().equals(destinationURI);
		((AtomicModel)this.getSimulatedModel()).
										actualStoreInput(destinationURI, es);

		// Notify the parent that the model has to perform an
		// external transition during this simulation step.
		if (!this.getSimulatedModel().isRoot() &&
									this.getSimulatedModel().isCoordinated()) {
			this.hasReceivedExternalEvents(this.getSimulatedModel().getURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	{
		assert	this.isModelSet() :
				new AssertionError("Precondition violation: isModelSet()");

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
					"AtomicEngine>>externalEventStep " +
					this.simulatedModel.getURI() +
					" at " + this.getTimeOfLastEvent() +
					" with elapsed time " + elapsedTime + "\n");
		}

		this.simulatedModel.externalTransition(elapsedTime);
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime();
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();

		assert	this.nextTimeAdvance.equals(
					this.timeOfNextEvent.subtract(this.timeOfLastEvent)) :
						new AssertionError("Postcondition violation: ");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#confluentEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentEventStep(Duration elapsedTime)
	{
		throw new RuntimeException(
					"AtomicEngine>>confluentEventStep() not implemented yet!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.simulatedModel.endSimulation(endTime);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation()
	{
		this.simulatedModel.finalise();
		super.finaliseSimulation();
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		this.simulatedModel.showCurrentState(indent + "    ", elapsedTime);
	}
}
// -----------------------------------------------------------------------------
