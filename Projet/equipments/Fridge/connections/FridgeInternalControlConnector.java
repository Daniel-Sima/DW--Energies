package equipments.Fridge.connections;

import equipments.Fridge.FridgeInternalControlCI;

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
 * The class <code>FridgeInternalControlConnector</code> implements a connector
 * for the {@code FridgeInternalControlCI} component interface.
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
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class			FridgeInternalControlConnector
extends		AbstractConnector
implements	FridgeInternalControlCI
{
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		((FridgeInternalControlCI)this.offering).setTargetCoolerTemperature(targetCooler);
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).getTargetCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).getCurrentCoolerTemperature();
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		((FridgeInternalControlCI)this.offering).setTargetFreezerTemperature(targetFreezer);
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).getTargetFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).getCurrentFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#coolingCooler()
	 */
	@Override
	public boolean		coolingCooler() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).coolingCooler();
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingCooler()
	 */
	@Override
	public void			startCoolingCooler() throws Exception
	{
		((FridgeInternalControlCI)this.offering).startCoolingCooler();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingCooler()
	 */
	@Override
	public void			stopCoolingCooler() throws Exception
	{
		((FridgeInternalControlCI)this.offering).stopCoolingCooler();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlI#coolingFreezer()
	 */
	@Override
	public boolean		coolingFreezer() throws Exception
	{
		return ((FridgeInternalControlCI)this.offering).coolingFreezer();
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingFreezer()
	 */
	@Override
	public void			startCoolingFreezer() throws Exception
	{
		((FridgeInternalControlCI)this.offering).startCoolingFreezer();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingFreezer()
	 */
	@Override
	public void			stopCoolingFreezer() throws Exception
	{
		((FridgeInternalControlCI)this.offering).stopCoolingFreezer();
	}
	
	/***********************************************************************************/
	/**
	 * @see
	 */
	@Override
	public void printSeparator(String title) throws Exception {
		((FridgeInternalControlCI)this.offering).printSeparator(title);
	}
}
// -----------------------------------------------------------------------------
