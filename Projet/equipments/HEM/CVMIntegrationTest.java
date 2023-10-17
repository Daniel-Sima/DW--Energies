package equipments.HEM;

import equipments.AirConditioning.AirConditioning;
import equipments.AirConditioning.AirConditioningTester;
import equipments.CookingPlate.CookingPlate;
import equipments.CookingPlate.CookingPlateTester;
import equipments.Fridge.Fridge;
import equipments.Fridge.FridgeTester;
import equipments.Lamp.Lamp;
import equipments.Lamp.LampTester;
import equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.exceptions.ContractException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CVMIntegrationTest</code> defines the integration test
 * for the household energy management example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-16</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CVMIntegrationTest 
extends	AbstractCVM {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String TEST_CLOCK_URI = "test-clock";
	public static final long DELAY_TO_START_IN_MILLIS = 3000;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMIntegrationTest() throws Exception {
		ContractException.VERBOSE = true;
		ClocksServer.VERBOSE = true;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**AirConditioningSwitchOff
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void	deploy() throws Exception {
		AbstractComponent.createComponent(
				ClocksServer.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				ElectricMeter.class.getCanonicalName(),
				new Object[]{});

		// At this stage, the tester for the Cooking Plate is added only
		// to show the Cooking Plate functioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				CookingPlate.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				CookingPlateTester.class.getCanonicalName(),
				new Object[]{true});
		
		// At this stage, the tester for the Lamp is added only
		// to show the Lamp functioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				Lamp.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				LampTester.class.getCanonicalName(),
				new Object[]{true});

		// At this stage, the tester for the Air Conditioning is added only
		// to switch on and off the Air Conditioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				AirConditioning.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				AirConditioningTester.class.getCanonicalName(),
				new Object[]{false});

		// At this stage, the tester for the Fridge is added only
		// to switch on and off the Air Conditioning; later on, it will be replaced
		// by a simulation of users' actions.
		AbstractComponent.createComponent(
				Fridge.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				FridgeTester.class.getCanonicalName(),
				new Object[]{false});

		AbstractComponent.createComponent(
				HEM.class.getCanonicalName(),
				new Object[]{});

		super.deploy();
	}

	/***********************************************************************************/
	public static void	main(String[] args) {
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
			cvm.startStandardLifeCycle(120000L);
			Thread.sleep(300000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/


