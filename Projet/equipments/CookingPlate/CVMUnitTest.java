package equipments.CookingPlate;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import utils.ExecutionType;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CVMUnitTest</code> performs unit tests on the cooking plate
 * component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code DELAY_TO_START > 0}
 * invariant	{@code CLOCK_URI != null && !CLOCK_URI.isEmpty()}
 * invariant	{@code START_INSTANT != null && !START_INSTANT.isEmpty()}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CVMUnitTest 
extends	AbstractCVM {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators.				*/
	public static final long DELAY_TO_START = 3000L;
	/** for unit tests and SIL simulation tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static final String CLOCK_URI = "hem-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static final String START_INSTANT = "2023-11-22T00:00:00.00Z";
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMUnitTest() throws Exception {}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void	deploy() throws Exception {
		AbstractComponent.createComponent(
				CookingPlate.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				CookingPlateTester.class.getCanonicalName(),
				new Object[]{CookingPlate.INBOUND_PORT_URI, 
						ExecutionType.INTEGRATION_TEST});
		
		long unixEpochStartTimeInMillis = System.currentTimeMillis() + DELAY_TO_START;
		
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(
									 		unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						Instant.parse(START_INSTANT),
						1.0});

		super.deploy();
	}

	/***********************************************************************************/
	public static void main(String[] args){
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(DELAY_TO_START + 10000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/