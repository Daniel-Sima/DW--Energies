package fr.sorbonne_u.components.hem2023e3.equipments.heater.connections;

import java.util.concurrent.TimeUnit;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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
import fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterSensorData;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterController;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterSensorDataOutboundPort</code>
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
 * <p>Created on : 2023-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class 			HeaterSensorDataOutboundPort
extends		AbstractDataOutboundPort
implements	HeaterSensorDataCI.HeaterSensorRequiredPullCI
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
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				HeaterSensorDataOutboundPort(ComponentI owner)
	throws Exception
	{
		super(HeaterSensorDataCI.HeaterSensorRequiredPullCI.class,
			  DataRequiredCI.PushCI.class, owner);
	}

	/**
	 * create the outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception 	<i>to do</i>.
	 */
	public				HeaterSensorDataOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, HeaterSensorDataCI.HeaterSensorRequiredPullCI.class,
			  DataRequiredCI.PushCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#heatingPullSensor()
	 */
	@Override
	public HeaterSensorData<Measure<Boolean>>	heatingPullSensor()
	throws Exception
	{
		return ((HeaterSensorDataCI.HeaterSensorRequiredPullCI)
							this.getConnector()).heatingPullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#targetTemperaturePullSensor()
	 */
	@Override
	public HeaterSensorData<Measure<Double>>	targetTemperaturePullSensor()
	throws Exception
	{
		return ((HeaterSensorDataCI.HeaterSensorRequiredPullCI)
							this.getConnector()).targetTemperaturePullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#currentTemperaturePullSensor()
	 */
	@Override
	public HeaterSensorData<Measure<Double>>	currentTemperaturePullSensor()
	throws Exception
	{
		return ((HeaterSensorDataCI.HeaterSensorRequiredPullCI)
							this.getConnector()).currentTemperaturePullSensor();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterSensorDataCI.HeaterSensorCI#startTemperaturesPushSensor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			startTemperaturesPushSensor(
		long controlPeriod,
		TimeUnit tu
		) throws Exception
	{
		((HeaterSensorDataCI.HeaterSensorRequiredPullCI)this.getConnector()).
								startTemperaturesPushSensor(controlPeriod, tu);
	}

	/**
	 * @see fr.sorbonne_u.components.interfaces.DataRequiredCI.PushCI#receive(fr.sorbonne_u.components.interfaces.DataRequiredCI.DataI)
	 */
	@Override
	public void			receive(DataRequiredCI.DataI d) throws Exception
	{
		this.getOwner().runTask(
			o -> ((HeaterController)o).receiveDataFromHeater(d));
	}
}
// -----------------------------------------------------------------------------
