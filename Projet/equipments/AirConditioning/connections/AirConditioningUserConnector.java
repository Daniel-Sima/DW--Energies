package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningUserCI;

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
 * The class <code>HeaterConnector</code> implements a connector for the
 * {@code HeaterCI} component interface.
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
public class			AirConditioningUserConnector
extends		AbstractConnector
implements	AirConditioningUserCI
{
	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		return ((AirConditioningUserCI)this.offering).on();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		((AirConditioningUserCI)this.offering).switchOn();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		((AirConditioningUserCI)this.offering).switchOff();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#setTargetTemperature(double)
	 */
	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		((AirConditioningUserCI)this.offering).setTargetTemperature(target);
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((AirConditioningUserCI)this.offering).getTargetTemperature();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((AirConditioningUserCI)this.offering).getCurrentTemperature();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((AirConditioningUserCI)this.offering).getMaxPowerLevel();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((AirConditioningUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((AirConditioningUserCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------