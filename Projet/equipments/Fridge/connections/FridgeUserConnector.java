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

import fr.sorbonne_u.components.connectors.AbstractConnector;

// -----------------------------------------------------------------------------
/**
 * The class <code>FridgeConnector</code> implements a connector for the
 * {@code FridgeCI} component interface.
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
public class			FridgeUserConnector
extends		AbstractConnector
implements	FridgeUserCI
{
	/**
	 * @see equipments.Fridge.FridgeUserCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		return ((FridgeUserCI)this.offering).on();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		((FridgeUserCI)this.offering).switchOn();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		((FridgeUserCI)this.offering).switchOff();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void			setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		((FridgeUserCI)this.offering).setTargetCoolerTemperature(targetCooler);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.offering).getTargetCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.offering).getCurrentCoolerTemperature();
	}
	
	/**
	 * @see equipments.Fridge.FridgeUserCI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void			setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		((FridgeUserCI)this.offering).setTargetFreezerTemperature(targetFreezer);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.offering).getTargetFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return ((FridgeUserCI)this.offering).getCurrentFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((FridgeUserCI)this.offering).getMaxPowerLevel();
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((FridgeUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.Fridge.FridgeUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((FridgeUserCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
