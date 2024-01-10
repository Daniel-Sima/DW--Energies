package fr.sorbonne_u.components.cyphy.plugins.devs.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.utils.Pair;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulatorOutboundPort</code> implements the outbound
 * port for the required interface <code>SimulatorCI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulatorOutboundPort
extends		AbstractOutboundPort
implements	SimulatorCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public				SimulatorOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, SimulatorCI.class, owner);
	}

	/**
	 * create the outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public				SimulatorOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(SimulatorCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).isParentSet();
	}

	@Override
	public void			setParent(String parentNotificationInboundPortURI)
	throws Exception
	{
		((SimulatorCI)this.getConnector()).
									setParent(parentNotificationInboundPortURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception
	{
		((SimulatorCI)this.getConnector()).initialiseSimulation(simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		((SimulatorCI)this.getConnector()).
					initialiseSimulation(startTime, simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).isSimulationInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).
										useFixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesTimeInitialised()
	 */
	@Override
	public boolean		allModelVariablesTimeInitialised() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).
										allModelVariablesTimeInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesInitialised()
	 */
	@Override
	public boolean		allModelVariablesInitialised() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).allModelVariablesInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	throws Exception
	{
		return ((SimulatorCI)this.getConnector()).fixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#internalEventStep()
	 */
	@Override
	public void			internalEventStep() throws Exception
	{
		((SimulatorCI)this.getConnector()).internalEventStep();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		((SimulatorCI)this.getConnector()).externalEventStep(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current) throws Exception
	{
		((SimulatorCI)this.getConnector()).produceOutput(current);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		((SimulatorCI)this.getConnector()).endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).getTimeOfLastEvent();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).getTimeOfNextEvent();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).getNextTimeAdvance();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode() throws Exception
	{
		((SimulatorCI)this.getConnector()).toggleDebugMode();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).isDebugModeOn();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		((SimulatorCI)this.getConnector()).setDebugLevel(newDebugLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel) throws Exception
	{
		return ((SimulatorCI)this.getConnector()).hasDebugLevel(debugLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		return ((SimulatorCI)this.getConnector()).simulatorAsString();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		((SimulatorCI)this.getConnector()).showCurrentStateContent(
													indent, elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	throws Exception
	{
		((SimulatorCI)this.getConnector()).showCurrentState(indent, elapsedTime);
	}
}
// -----------------------------------------------------------------------------
