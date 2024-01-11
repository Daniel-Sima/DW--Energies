package fr.sorbonne_u.components.hem2023e1.equipments.heater.connections;

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
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndExternalControlI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserAndControlI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserImplI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterUserInboundPort</code> implements an inbound port for
 * the {@code HeaterUserCI} component interface.
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
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HeaterUserInboundPort
extends		AbstractInboundPort
implements	HeaterUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof HeaterUserImplI}
	 * pre	{@code owner instanceof HeaterUserAndControlI}
	 * pre	{@code owner instanceof HeaterUserAndExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				HeaterUserInboundPort(ComponentI owner) throws Exception
	{
		super(HeaterUserCI.class, owner);

		assert	owner instanceof HeaterUserImplI;
		assert	owner instanceof HeaterUserAndControlI;
		assert	owner instanceof HeaterUserAndExternalControlI;
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof HeaterUserImplI}
	 * pre	{@code owner instanceof HeaterUserAndControlI}
	 * pre	{@code owner instanceof HeaterUserAndExternalControlI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @throws Exception			<i>to do</i>.
	 */
	public				HeaterUserInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, HeaterUserCI.class, owner);

		assert	owner instanceof HeaterUserImplI;
		assert	owner instanceof HeaterUserAndControlI;
		assert	owner instanceof HeaterUserAndExternalControlI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		return this.getOwner().handleRequest(o -> ((HeaterUserImplI)o).on());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((HeaterUserImplI)o).switchOn();;
									return null;
							});
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((HeaterUserImplI)o).switchOff();;
									return null;
							});
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((HeaterUserImplI)o).
												setTargetTemperature(target);
									return null;
							});
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((HeaterUserAndControlI)o).
													getTargetTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((HeaterUserAndControlI)o).
													getCurrentTemperature());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((HeaterUserAndExternalControlI)o).
														getCurrentPowerLevel());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((HeaterUserAndExternalControlI)o).
														getMaxPowerLevel());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e1.equipments.heater.HeaterUserCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
							o -> { ((HeaterUserAndExternalControlI)o).
											setCurrentPowerLevel(powerLevel);
									return null;
							});
	}
}
// -----------------------------------------------------------------------------
