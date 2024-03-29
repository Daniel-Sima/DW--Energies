package equipments.Lamp;


// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>LampOutboundPort</code> implements an outbound port for
 * the <code>LampUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class LampOutboundPort
extends		AbstractOutboundPort
implements	LampUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public				LampOutboundPort(ComponentI owner)
	throws Exception
	{
		super(LampUserCI.class, owner);
	}

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception 			<i>to do</i>.
	 */
	public				LampOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, LampUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampUserCI#getState()
	 */
	@Override
	public LampState	getState() throws Exception
	{
		return ((LampUserCI)this.getConnector()).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampUserCI#getMode()
	 */
	@Override
	public LampMode	getMode() throws Exception
	{
		return ((LampUserCI)this.getConnector()).getMode();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampUserCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		((LampUserCI)this.getConnector()).turnOn();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.Lamp.LampUserCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		((LampUserCI)this.getConnector()).turnOff();
	}

	@Override
	public void increaseMode() throws Exception 
	{
		((LampUserCI)this.getConnector()).increaseMode();
	}

	@Override
	public void decreaseMode() throws Exception 
	{
		((LampUserCI)this.getConnector()).decreaseMode();
	}

	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void printSeparator(String title) throws Exception {
		((LampUserCI)this.getConnector()).printSeparator(title);
	}
}
// -----------------------------------------------------------------------------
