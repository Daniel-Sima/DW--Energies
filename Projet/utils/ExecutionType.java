package utils;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The enumeration <code>ExecutionType</code>
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
 * <p>Created on : 2023-01-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public enum ExecutionType {
	
	STANDARD,			// standard usage, no simulation
	UNIT_TEST,			// unit tests without simulation
	INTEGRATION_TEST,	// integration tests without simulation
	MIL_SIMULATION,		// model-in-the-loop simulation
	MIL_RT_SIMULATION,	// model-in-the-loop real time simulation
	SIL_SIMULATION;		// software-in-the-loop real time simulation
	
	/***********************************************************************************/
	public boolean isStandard() {
		return this == STANDARD;
	}

	/***********************************************************************************/
	public boolean isUnitTest() {
		return this == UNIT_TEST;
	}

	/***********************************************************************************/
	public boolean isIntegrationTest() {
		return this == INTEGRATION_TEST;
	}
	
	/***********************************************************************************/
	public boolean isTest() {
		return this.isUnitTest() || this.isIntegrationTest();
	}
	
	/***********************************************************************************/
	public boolean isSimulated() {
		return this.isMIL() || this.isMILRT() || this.isSIL();
	}
	
	/***********************************************************************************/
	public boolean isMIL() {
		return this == MIL_SIMULATION;
	}
	
	/***********************************************************************************/
	public boolean isMILRT() {
		return this == MIL_RT_SIMULATION;
	}
	
	/***********************************************************************************/
	public boolean isSIL() {
		return this == SIL_SIMULATION;
	}
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

