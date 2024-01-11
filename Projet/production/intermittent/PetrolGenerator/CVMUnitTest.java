package production.intermittent.PetrolGenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>CVMUnitTest</code> performs unit tests for the petrol generator.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-15</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public class CVMUnitTest 
extends AbstractCVM {
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	public CVMUnitTest() throws Exception {}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------
	/***********************************************************************************/
	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception
	{
		AbstractComponent.createComponent(
				PetrolGenerator.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				PetrolGeneratorTester.class.getCanonicalName(),
				new Object[]{true});

		super.deploy();
	}

	/***********************************************************************************/
	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
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