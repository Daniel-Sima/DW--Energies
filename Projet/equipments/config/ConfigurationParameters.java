package equipments.config;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Hashtable;
import java.util.Set;

//-----------------------------------------------------------------------------
/**
 * The class <code>ConfigurationParameters</code> defines objects holding the
 * component deployment configuration parameters.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The parameters come essentially from the XML configuration file which must
 * conform to the following Relax NG schema:
 * </p>
 * <pre>
 * 
 * start = control-adapter

# Description of how to make a correspondance between the required control
# interfaces of the home energy manager and the offered interface of the
# described equipment.
control-adapter = element control-adapter {
  identification,    # identification of the equipment and its control interface
  consumption,       # experimentally measured energy consumption
  required*,         # classes required to compile the code
  instance-var*,     # describing instance variables of the connector
  operations         # describing how to related operations in the required
                     # interface and the operations in the offered one
}

# Identification of the equipment and its control interface
identification =
  attribute uid { xsd:NMTOKEN }, # unique ide of the equipment (serial number)
  attribute offered { text }     # canonical name of the offered interface

# Predetermined energy consumption of the equipment (e.g., on the bench)
consumption = element consumption {
  attribute min { xsd:double }?,
  attribute nominal { xsd:double },
  attribute max { xsd:double }?
}

# Canonical names of classes that are used in a piece of code and which
# path must be known to be able to compile that code
required = element required {
  text               # canonical name of a class referenced in the code and
                     # that will be needed to compile that code
}

# Instance variables to be defined in the connector class
instance-var = element instance-var {
  modifiers,                      # modifiers (public/private..., static, ...)
  type,                           # type of the variable
  name,                           # name of the variable
  attribute static-init { text }? # static Java expression to initialise
                                  # the variable
}

modifiers = attribute modifiers {text}
type = attribute type { text }
name = attribute name { xsd:NMTOKEN }

# Relate operations in the required interface to the corresponding operations
# in the offered one
operations =
  internal*,           # internal auxiliary methods
  maxMode,             # number of modes
  upMode,              # forcing the equipment to the next more consuming mode
  downMode,            # forcing the equipment to the next less consuming mode
  setMode,             # set the current mode, take [1, numberOfModes]
  currentMode,         # get the current mode, return [1, numberOfModes]
  suspended,           # is the equipment currently suspended, return boolean
  suspend,             # suspend the equipment
  resume,              # resume normal operation for the equipment
  emergency            # degree of emergency of a resumption [0, 1]; the higher
                       # is this degree, the more it should be allowed to
                       # resume (e.g., the water temperature of the boiler
                       # becomes too cold to be useful)

internal       = element internal {
  modifiers,                      # modifiers (public/private..., static, ...)
  type,                           # type of the variable
  name,                           # name of the variable 
  parameter*,	                  # name used in the code to refer to the
                                  # parameter passed by the controller when
                                  # calling the operation
  body
}

# Mode control operations
maxMode       = element maxMode      { body }
upMode        = element upMode       { body }
downMode      = element downMode     { body }
setMode       = element setMode      { parameter, body }
currentMode   = element currentMode  { body }

# suspension control operations
suspended     = element suspended    { body }
suspend       = element suspend      { body }
resume        = element resume       { body }
emergency     = element emergency    { body }

# A name (variable) used in a piece of code to refer to a parameter that
# is passed to an operation having that code to execute
parameter = element parameter {
  type?,
  name
}

# Piece of code that must be executed when a required operation is called in
# order to execute the corresponding services appearing in the offered interface
body =
  thrown-exception*,                         # exceptions thrown by the method
  element body {
    attribute equipmentRef { xsd:NMTOKEN }?, # variable referring to the
                                             # equipment in the code
  text                                       # the code itself, a Java statement
}

# Canonical name of the thrown exception
thrown-exception = element thrown { text }
 */

/**
 * 
 * @author walte
 *
 */
class Parameter {
	protected String type;
	protected String name;

	/**
	 * 
	 * @param type
	 * @param name
	 */
	public Parameter(String type, String name) {
		this.name = name;
		this.type = type;
	}
}

/**
 * 
 * @author walte
 *
 */
class InstanceVar {
	/** modifiers (public/private..., static, ...)			*/
	protected String modifiers;
	/** type of the variable								*/
	protected String	type;
	/** name of the variable								*/
	protected String	name;
	/** static Java expression to initialise the variable	*/
	protected String	staticInit;

	/**
	 * 
	 * @param modifiers
	 * @param type
	 * @param name
	 * @param staticInit
	 */
	public InstanceVar(
			String	modifiers,
			String		type,
			String		name,
			String		staticInit
			) 
	{
		this.modifiers = modifiers;
		this.type = type;
		this.name = name;
		this.staticInit = staticInit;
	}
}

/**
 * 
 * @author walte
 *
 */
class Body {
	/** exceptions thrown by the method					*/
	protected String		thrownException;
	/** variable referring to the equipment in the code	*/
	protected String 		equipmentRef; 
	/**  the code itself, a Java statement				*/
	protected String 		text;

	/**
	 * 
	 * @param thrownException
	 * @param equipmentRef
	 * @param text
	 */
	public Body(
			String thrownException,
			String equipmentRef,
			String text
			) 
	{
		this.equipmentRef = equipmentRef;
		this.thrownException = thrownException;
		this.text = text;
	}
}

/**
 * </pre>
 * <p>
 * Most of this information and derived one are included in instances of
 * <code>ConfigurationParameters</code>. These instances are linked to Component
 * Virtual Machines and thus one instance exist in each JVM running a CVM in
 * a distributed execution.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true		// TODO
 * </pre>
 * 
 * <p>Created on : 2012-10-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				ConfigurationParameters
{	
	/** identification uid of the equipment and its control interface		*/
	protected long 				identificationUid;
	/** identification offered of the equipment and its control interface	*/
	protected String			identificationOffered;
	/** experimentally minimum measured energy consumption					*/
	protected double			consumptionMin;
	/** experimentally nominal measured energy consumption					*/
	protected double			consumptionNominal;
	/** experimentally maximum measured energy consumption					*/
	protected double			consumptionMax;
	/** classes required to compile the code 								*/
	protected String			required;
	/** Instance variables to be defined in the connector class				*/
	protected InstanceVar[]		instanceVars;
	/** modifiers (public/private..., static, ...)							*/
	protected String		internalModifiers;
	/** type of the variable												*/
	protected String		internalType;
	/** name of the variable												*/
	protected String		internalName;
	/** static Java expression to initialise the variable					*/
	protected Parameter		internalParameter;

	/** number of modes.													*/
	protected Body		maxMode;
	/** forcing the equipment to the next more consuming mode				*/
	protected Body		upMode;  
	/** forcing the equipment to the next less consuming mode				*/
	protected Body		downMode;   
	/** set the current mode, take [1, numberOfModes]						*/
	protected Body 		setMode;
	/** get the current mode, return [1, numberOfModes]						*/
	protected Body 		currentMode;
	/** is the equipment currently suspended, return boolean				*/
	protected Body 		suspended;
	/** suspend the equipment												*/
	protected Body 		suspend;
	/** resume normal operation for the equipment							*/
	protected Body 		resume;
	/** degree of emergency of a resumption [0, 1];
	 * 	the higher is this degree, the more it should be
	 *  allowed to resume (e.g., the water temperature of
	 *   the boiler becomes too cold to be useful)							*/
	protected Body 		emergency;

	/**
	 * create a configuration parameters holder.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition. TODO
	 * post	true			// no postcondition. TODO
	 * </pre>
	 * 
	 * @param identificationUid
	 * @param identificationOffered
	 * @param consumptionMin
	 * @param consumptionNominal
	 * @param consumptionMax
	 * @param required
	 * @param instanceVar
	 * @param operations
	 */
	public	ConfigurationParameters(
			long			identificationUid,
			String			identificationOffered,
			double			consumptionMin,
			double			consumptionNominal,
			double			consumptionMax,
			String			required,
			InstanceVar[]	instanceVars,
			String			internalModifiers,
			String			internalType,
			String			internalName,
			Parameter		internalParameter,
			Body			maxMode,
			Body			upMode,
			Body			downMode,
			Body			setMode,
			Body			currentMode,
			Body			suspended,
			Body			suspend,
			Body			resume,
			Body			emergency
			)
	{
		super();
		this.identificationUid = identificationUid;
		this.identificationOffered = identificationOffered;
		this.consumptionMin = consumptionMin;
		this.consumptionNominal = consumptionNominal;
		this.consumptionMax = consumptionMax;
		this.required = required;
		this.instanceVars = instanceVars;
	}

	/**
	 * @return the identificationUid
	 */
	public long		getIdentificationUid() {
		return this.identificationUid;
	}

	/**
	 * @return the identificationOffered
	 */
	public String		getIdentificationOffered() {
		return this.identificationOffered;
	}

	/**
	 * @return the consumptionMin;
	 */
	public double		getConsumptionMin() {
		return this.consumptionMin;
	}

	/**
	 * @return the consumptionMax;
	 */
	public double		getConsumptionMax() {
		return this.consumptionMax;
	}

	/**
	 * @return the consumptionNominal;
	 */
	public double		getConsumptionNominal() {
		return this.consumptionNominal;
	}

	/**
	 * @return the required;
	 */
	public String		getRequired() {
		return this.required;
	}

	/**
	 * @return the instanceVars;
	 */
	public InstanceVar[]	getInstanceVars() {
		return this.instanceVars;
	}
	
	/**
	 * @return the getInternalModifiers;
	 */
	public String		getInternalModifiers() {
		return this.internalModifiers;
	}
	
	/**
	 * @return the internalType;
	 */
	public String		getInternalType() {
		return this.internalType;
	}
	
	/**
	 * @return the internalName;
	 */
	public String		getInternalName() {
		return this.internalName;
	}
	
	/**
	 * @return the internalParameter;
	 */
	public Parameter		getInternalParameter() {
		return this.internalParameter;
	}
	
	/**
	 * @return the maxMode;
	 */
	public Body		getMaxMode() {
		return this.maxMode;
	}

	/**
	 * @return the maupMode;
	 */
	public Body		getUpMode() {
		return this.upMode;
	}
	
	/**
	 * @return the downMode;
	 */
	public Body		getDownMode() {
		return this.downMode;
	}
	
	/**
	 * @return the setMode;
	 */
	public Body		getSetMode() {
		return this.setMode;
	}
	
	/**
	 * @return the currentMode;
	 */
	public Body		getCurrentMode() {
		return this.currentMode;
	}
	
	/**
	 * @return the suspended;
	 */
	public Body		getSuspended() {
		return this.suspended;
	}
	
	/**
	 * @return the suspend;
	 */
	public Body		getSuspend() {
		return this.suspend;
	}
	
	/**
	 * @return the resume;
	 */
	public Body		getResume() {
		return this.resume;
	}
	
	/**
	 * @return the emergency;
	 */
	public Body		getEmergency() {
		return this.emergency;
	}
	
	@Override
	public String		toString() {
		return "nada";

	}
}
// -----------------------------------------------------------------------------
