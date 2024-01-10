package fr.sorbonne_u.components.hem2023e1.equipments.hem;

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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2023.bases.AdjustableCI;
import fr.sorbonne_u.components.hem2023e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2023e1.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterCI;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterConnector;
import fr.sorbonne_u.components.hem2023e1.equipments.meter.ElectricMeterOutboundPort;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEM</code> implements the basis for a household energy
 * management component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * As is, this component is only a very limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
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
@RequiredInterfaces(required = {AdjustableCI.class, ElectricMeterCI.class,
								ClocksServerCI.class})
public class			HEM
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort		clocksServerOutboundPort;
	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort		meterop;
	/** port to connect to the heater.										*/
	protected AdjustableOutboundPort		heaterop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a household energy manager component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected 			HEM()
	{
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.tracer.get().setTitle("Home Energy Manager component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());

			this.meterop = new ElectricMeterOutboundPort(this);
			this.meterop.publishPort();
			this.doPortConnection(
					this.meterop.getPortURI(),
					ElectricMeter.ELECTRIC_METER_INBOUND_PORT_URI,
					ElectricMeterConnector.class.getCanonicalName());

			this.heaterop = new AdjustableOutboundPort(this);
			this.heaterop.publishPort();
			this.doPortConnection(
					this.heaterop.getPortURI(),
					Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
					HeaterConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		long startTimeInNanos =
				TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()
				+ CVMIntegrationTest.DELAY_TO_START_IN_MILLIS);
		Instant startInstant = Instant.parse("2023-09-20T15:00:00.00Z");
		System.out.println("HEM creates the clock");
		AcceleratedClock ac =
				this.clocksServerOutboundPort.createClock(
							CVMIntegrationTest.TEST_CLOCK_URI,
							startTimeInNanos,
							startInstant,
							1.0);
		System.out.println("HEM has created the clock");
		this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
		this.clocksServerOutboundPort.unpublishPort();
		System.out.println("HEM has disconnected from the clocks server");

		// simplified integration testing for meter.
		this.traceMessage("Electric meter current consumption? " +
				this.meterop.getCurrentConsumption() + "\n");
		this.traceMessage("Electric meter current production? " +
				this.meterop.getCurrentProduction() + "\n");

		System.out.println("HEM waits until start");
		// Test for the heater
		Instant heaterTestStart = Instant.parse("2023-09-20T15:00:05.00Z");
		ac.waitUntilStart();
		long delay = ac.nanoDelayUntilInstant(heaterTestStart);
		System.out.println("HEM schedules the heater test");
	
		// This is to avoid mixing the 'this' of the task object with the 'this'
		// representing the component object in the code of the next methods run
		AbstractComponent o = this;

		// schedule the switch on heater in one second
		this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							o.traceMessage("Heater maxMode index? " +
												heaterop.maxMode() + "\n");
							o.traceMessage("Heater current mode index? " +
												heaterop.currentMode() + "\n");
							o.traceMessage("Heater going down one mode? " +
												heaterop.downMode() + "\n");
							o.traceMessage("Heater current mode is? " +
												heaterop.currentMode() + "\n");
							o.traceMessage("Heater going up one mode? " +
												heaterop.upMode() + "\n");
							o.traceMessage("Heater current mode is? " +
												heaterop.currentMode() + "\n");
							o.traceMessage("Heater setting current mode? " +
												heaterop.setMode(2) + "\n");
							o.traceMessage("Heater current mode is? " +
												heaterop.currentMode() + "\n");
							o.traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							o.traceMessage("Heater suspends? " +
												heaterop.suspend() + "\n");
							o.traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							o.traceMessage("Heater emergency? " +
												heaterop.emergency() + "\n");
							Thread.sleep(1000);
							o.traceMessage("Heater emergency? " +
												heaterop.emergency() + "\n");
							o.traceMessage("Heater resumes? " +
												heaterop.resume() + "\n");
							o.traceMessage("Heater is suspended? " +
												heaterop.suspended() + "\n");
							o.traceMessage("Heater current mode is? " +
									heaterop.currentMode() + "\n");
							System.out.println("HEM heater test ends");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.meterop.getPortURI());
		this.doPortDisconnection(this.heaterop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.meterop.unpublishPort();
			this.heaterop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
