package fr.sorbonne_u.components.hem2023e3.equipments.hem;

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
import fr.sorbonne_u.components.hem2023e3.CVMGlobalTest;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.Heater;
import fr.sorbonne_u.components.hem2023e3.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.components.hem2023e1.equipments.hem.AdjustableOutboundPort;
import fr.sorbonne_u.components.hem2023e1.equipments.hem.HeaterConnector;
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
 * As is, this component is only a limited starting point for the actual
 * component. The given code is there only to ease the understanding of the
 * objectives, but most of it must be replaced to get the correct code.
 * Especially, no registration of the components representing the appliances
 * is given.
 * </p>
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

	/** port to connect to the electric meter.								*/
	protected ElectricMeterOutboundPort		meterop;
	/** port to connect to the heater.										*/
	protected AdjustableOutboundPort		heaterop;

	/** period of the HEM control loop.										*/
	protected final long					PERIOD_IN_SECONDS = 60L;

	// Execution/Simulation

	/** port to connect to the clocks server.								*/
	protected ClocksServerOutboundPort		clocksServerOutboundPort;
	/** current type of execution.											*/
	protected final ExecutionType			currentExecutionType;

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
	 * @param currentExecutionType		current execution type for the next run.
	 */
	protected 			HEM(
		ExecutionType currentExecutionType
		)
	{
		// 1 standard thread to execute the method execute and 1 schedulable
		// thread that is used to perform the tests
		super(1, 1);

		this.currentExecutionType = currentExecutionType;

		this.tracer.get().setTitle("Home Energy Manager component");
		this.tracer.get().setRelativePosition(0, 0);
		this.toggleTracing();		
	}

	// -------------------------------------------------------------------------
	// Component internal methods
	// -------------------------------------------------------------------------

	/**
	 * perform once the control and then schedule another task to continue,
	 * unless the end instant has been reached; following this approach, the
	 * decisions to be made by the HEM could be introduced in this method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * pre	{@code end != null}
	 * pre	{@code ac != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	the instant at which the current execution of the control must be scheduled.
	 * @param end		the instant at which the control loop must stop.
	 * @param ac		the accelerated clock used as time reference to interpret the instants.
	 */
	protected void		loop(Instant current, Instant end, AcceleratedClock ac)
	{
		// For each action, compute the waiting time for this action
		// using the above instant and the clock, and then schedule the
		// task that will perform the action at the appropriate time.
		long delayInNanos = ac.nanoDelayUntilInstant(current);
		Instant next = current.plusSeconds(PERIOD_IN_SECONDS);
		if (next.compareTo(end) < 0) {
			this.scheduleTask(
				o -> {
					try	{
						o.traceMessage(
								"Electric meter current consumption: " +
								meterop.getCurrentConsumption() + "\n");
						o.traceMessage(
								"Electric meter current production: " +
								meterop.getCurrentProduction() + "\n");
						loop(next, end, ac);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}, delayInNanos, TimeUnit.NANOSECONDS);
		}
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
		// First, get the clock and wait until the start time that it specifies.
		AcceleratedClock ac = null;
		if (this.currentExecutionType.isIntegrationTest() ||
											this.currentExecutionType.isSIL()) {
			this.clocksServerOutboundPort = new ClocksServerOutboundPort(this);
			this.clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					this.clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.logMessage("HEM gets the clock");
			ac = this.clocksServerOutboundPort.getClock(CVMGlobalTest.CLOCK_URI);
			this.doPortDisconnection(this.clocksServerOutboundPort.getPortURI());
			this.clocksServerOutboundPort.unpublishPort();
			this.logMessage("HEM waits until start time.");
			ac.waitUntilStart();
		}

		this.logMessage("HEM starts.");

		if (this.currentExecutionType.isSIL()) {
			// For SIL simulation, execute the control loop until the end of
			// simulation time.
			long delayUntilEndInSeconds =
					(long) (TimeUnit.HOURS.toSeconds(1)
										* CVMGlobalTest.SIMULATION_DURATION);
			Instant startInstant = ac.getStartInstant();
			Instant endInstant =
					startInstant.plusSeconds(delayUntilEndInSeconds);
			// delay until the first call to the electric meter
			long delayInSecondsOfSimulatedTime = 600L;
			Instant first =
					startInstant.plusSeconds(delayInSecondsOfSimulatedTime);

			this.logMessage("HEM schedules the SIL integration test.");
			this.loop(first, endInstant, ac);
		} else if (this.currentExecutionType.isIntegrationTest()) {
			// Integration test for the meter and the heater
			Instant meterTest = ac.getStartInstant().plusSeconds(60L);
			long delay = ac.nanoDelayUntilInstant(meterTest);
			this.logMessage("HEM schedules the meter integration test in "
										+ delay + " " + TimeUnit.NANOSECONDS);
	
			// This is to avoid mixing the 'this' of the task object with the
			// 'this' representing the component object in the code of the next
			// methods run
			AbstractComponent o = this;

			// For the electric meter, simply perform two calls to test the
			// sensor methods.
			this.scheduleTaskOnComponent(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							o.traceMessage(
									"Electric meter current consumption: " +
									meterop.getCurrentConsumption() + "\n");
							o.traceMessage(
									"Electric meter current production: " +
									meterop.getCurrentProduction() + "\n");
							o.traceMessage("HEM meter test ends.\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, delay, TimeUnit.NANOSECONDS);

			// For the heater, perform a series of call that will also test the
			// adjustable interface.
			Instant heater1 = ac.getStartInstant().plusSeconds(30L);
			delay = ac.nanoDelayUntilInstant(heater1);
			this.logMessage("HEM schedules the heater first call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM heater first call begins.\n");
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
								o.traceMessage("HEM heater first call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);

			Instant heater2 = ac.getStartInstant().plusSeconds(120L);
			delay = ac.nanoDelayUntilInstant(heater2);
			this.logMessage("HEM schedules the heater second call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM heater second call begins.\n");
								o.traceMessage("Heater suspends? " +
											   heaterop.suspend() + "\n");
								o.traceMessage("Heater is suspended? " +
											   heaterop.suspended() + "\n");
								o.traceMessage("Heater emergency? " +
											   heaterop.emergency() + "\n");
								o.traceMessage("HEM heater second call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);

			Instant heater3 = ac.getStartInstant().plusSeconds(240L);
			delay = ac.nanoDelayUntilInstant(heater3);
			this.logMessage("HEM schedules the heater third call in "
										+ delay + " " + TimeUnit.NANOSECONDS);
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("HEM heater third call begins.\n");
								o.traceMessage("Heater emergency? " +
											   heaterop.emergency() + "\n");
								o.traceMessage("Heater resumes? " +
											   heaterop.resume() + "\n");
								o.traceMessage("Heater is suspended? " +
											   heaterop.suspended() + "\n");
								o.traceMessage("Heater current mode is? " +
											   heaterop.currentMode() + "\n");
								o.traceMessage("HEM heater third call ends.\n");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, delay, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.logMessage("HEM ends.");
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