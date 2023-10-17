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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FridgeInternalControlOutboundPort</code> implements an
 * outbound port for the {@code FridgeInternalControlCI} component interface.
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
public class FridgeInternalControlOutboundPort
extends		AbstractOutboundPort
implements	FridgeInternalControlCI
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
	public				FridgeInternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(FridgeInternalControlCI.class, owner);
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
	public				FridgeInternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FridgeInternalControlCI.class, owner);
	}


	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void		setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		((FridgeInternalControlCI)this.getConnector()).setTargetCoolerTemperature(targetCooler);
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.getConnector()).getTargetCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.getConnector()).
													getCurrentCoolerTemperature();
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void		setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		((FridgeInternalControlCI)this.getConnector()).setTargetFreezerTemperature(targetFreezer);
	}

	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.getConnector()).
													getTargetFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return ((FridgeInternalControlCI)this.getConnector()).
													getCurrentFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingFreezer()
	 */
	@Override
	public boolean coolingFreezer() throws Exception 
	{
		return ((FridgeInternalControlCI)this.getConnector()).coolingFreezer();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingFreezer()
	 */
	@Override
	public void startCoolingFreezer() throws Exception 
	{
		((FridgeInternalControlCI)this.getConnector()).startCoolingFreezer();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingFreezer()
	 */
	@Override
	public void stopCoolingFreezer() throws Exception 
	{
		((FridgeInternalControlCI)this.getConnector()).stopCoolingFreezer();
	}
	
	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingCooler()
	 */
	@Override
	public boolean coolingCooler() throws Exception 
	{
		return ((FridgeInternalControlCI)this.getConnector()).coolingCooler();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#startCoolingCooler()
	 */
	@Override
	public void startCoolingCooler() throws Exception 
	{
		((FridgeInternalControlCI)this.getConnector()).startCoolingCooler();
	}

	/**
	 * @see equipments.Fridge.FridgeInternalControlCI#stopCoolingCooler()
	 */
	@Override
	public void stopCoolingCooler() throws Exception 
	{
		((FridgeInternalControlCI)this.getConnector()).stopCoolingCooler();
	}
}
// -----------------------------------------------------------------------------
