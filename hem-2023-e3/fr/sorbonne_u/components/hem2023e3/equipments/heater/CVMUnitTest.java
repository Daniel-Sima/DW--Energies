package fr.sorbonne_u.components.hem2023e3.equipments.heater;

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

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2023e3.equipments.heater.HeaterController.ControlMode;
import fr.sorbonne_u.components.hem2023e3.utils.ExecutionType;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code> performs unit tests for the thermostated
 * heater component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2021-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the type of execution, to select among the values of the
	 *  enumeration {@code ExecutionType}.									*/
	public static final ExecutionType	CURRENT_EXECUTION_TYPE =
											//ExecutionType.UNIT_TEST;
											ExecutionType.INTEGRATION_TEST;
	/** the control mode of the heater controller for the next run.			*/
	public static final ControlMode		CONTROL_MODE = ControlMode.PULL;
	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static final String			CLOCK_URI = "hem-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final String			START_INSTANT =
													"2023-11-22T00:00:00.00Z";
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static final double			ACCELERATION_FACTOR = 60.0;
	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.				*/
	public static final long			DELAY_TO_START = 3000L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{

	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
								System.currentTimeMillis() + DELAY_TO_START),
						// start instant synchronised with the start time
						Instant.parse(START_INSTANT),
						ACCELERATION_FACTOR});

		AbstractComponent.createComponent(
				Heater.class.getCanonicalName(),
				new Object[]{
						Heater.REFLECTION_INBOUND_PORT_URI,
						Heater.USER_INBOUND_PORT_URI,
						Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
						Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						Heater.SENSOR_INBOUND_PORT_URI,
						Heater.ACTUATOR_INBOUND_PORT_URI,
						CURRENT_EXECUTION_TYPE,
						"",
						"",
						0.0,
						CLOCK_URI});

		AbstractComponent.createComponent(
				HeaterUser.class.getCanonicalName(),
				new Object[]{
						Heater.USER_INBOUND_PORT_URI,
						Heater.INTERNAL_CONTROL_INBOUND_PORT_URI,
						Heater.EXTERNAL_CONTROL_INBOUND_PORT_URI,
						Heater.SENSOR_INBOUND_PORT_URI,
						Heater.ACTUATOR_INBOUND_PORT_URI,
						CURRENT_EXECUTION_TYPE,
						CLOCK_URI});

		if (CURRENT_EXECUTION_TYPE.isIntegrationTest()) {
			AbstractComponent.createComponent(
					HeaterController.class.getCanonicalName(),
					new Object[]{
							Heater.SENSOR_INBOUND_PORT_URI,
							Heater.ACTUATOR_INBOUND_PORT_URI,
							HeaterController.STANDARD_HYSTERESIS,
							HeaterController.STANDARD_CONTROL_PERIOD,
							CONTROL_MODE,
							CURRENT_EXECUTION_TYPE,
							CLOCK_URI});
		}

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			long executionDuration =
					(long)(TimeUnit.SECONDS.toMillis(1)
									* (660.0/ACCELERATION_FACTOR));
			System.out.println(
					"starting for " + executionDuration + " milliseconds");
			cvm.startStandardLifeCycle(
								DELAY_TO_START + executionDuration + 2000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
