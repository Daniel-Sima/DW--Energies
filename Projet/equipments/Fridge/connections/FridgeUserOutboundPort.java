package equipments.Fridge.connections;

import equipments.Fridge.FridgeUserCI;

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
 * The class <code>HeaterUserOutboundPort</code> implements an outbound port for
 * the {@code HeaterUserCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class			FridgeUserOutboundPort
extends		AbstractOutboundPort
implements	FridgeUserCI
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
	public FridgeUserOutboundPort(ComponentI owner) throws Exception
	{
		super(FridgeUserCI.class, owner);
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
	public				FridgeUserOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, FridgeUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Fridge.FridgeUserCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).on();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		((FridgeUserCI)this.getConnector()).switchOn();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		((FridgeUserCI)this.getConnector()).switchOff();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void			setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		((FridgeUserCI)this.getConnector()).setTargetCoolerTemperature(targetCooler);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getTargetCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getCurrentCoolerTemperature();
	}
	
	/**
	 * @see equipments.Fridge.FridgeUserCI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void			setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		((FridgeUserCI)this.getConnector()).setTargetFreezerTemperature(targetFreezer);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getTargetFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getCurrentFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getMaxPowerLevel();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((FridgeUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((FridgeUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
