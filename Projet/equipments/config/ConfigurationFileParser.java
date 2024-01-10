package equipments.config;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.cvm.config.exceptions.ConfigurationException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ConfigurationFileParser</code> provides methods to validate
 * and parse component control-adapter configuration files.
 *
 * <p><strong>Description</strong></p>
 * 
 * The class relies on packages for XML processing to validate the configuration
 * file using the Relax NG schema <code>control-adapter.rnc</code> assumed to be
 * available in a directory <code>config</code> accessible from the base
 * directory of the running application.  The method
 * <code>parseConfigurationFile</code> parses the file and return the
 * information as an instance of the class <code>ConfigurationParameters</code>
 * that it returns as its result.
 * 
 * TODO: put the schema location in the configuration file?
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true	
 * </pre>
 * 
 * <p>Created on : 2012-10-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ConfigurationFileParser
{
	/** standard file name for the schema file used to validate the
	 *  XML configuration file.												*/
	public static String	SCHEMA_FILENAME = "control-adapter.rnc" ;
	/** the XML document builder used to parse the configuration file.		*/
	protected DocumentBuilder db ;

	/**
	 * create the configuration file parser.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>

	 * @throws ConfigurationException <i>todo.</i>
	 *
	 */
	public				ConfigurationFileParser() throws ConfigurationException
	{
		super();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance() ;
		try {
			this.db = dbf.newDocumentBuilder() ;
		} catch (ParserConfigurationException e) {
			throw new ConfigurationException(
					"ConfigurationFileParser can't configure the XML "
							+ "document builder!", e) ;
		}
	}

	/**
	 * validate a configuration file against the configuration Relax NG schema
	 * <code>control-adapter.rnc</code> assumed to be available in a directory
	 * <code>config</code> accessible from the base directory of the running
	 * application
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param configFile				the File object which reference must be validated.
	 * @return							true if the file is valid, false otherwise
	 * @throws ConfigurationException 	<i>to do.</i>
	 */
	public boolean		validateConfigurationFile(File configFile)
			throws	ConfigurationException
	{
		// Specify you want a factory for RELAX NG
		System.setProperty(
				SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
				"com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
		SchemaFactory factory =
				SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);

		// Load the specific schema you want.
		// Here I load it from a java.io.File, but we could also use a
		// java.net.URL or a javax.xml.transform.Source
		File schemaLocation = new File(SCHEMA_FILENAME);

		// Compile the schema.
		Schema schema;
		try {
			schema = factory.newSchema(schemaLocation);
		} catch (SAXException e) {
			throw new ConfigurationException(e) ;
		}
		// Get a validator from the schema.
		Validator validator = schema.newValidator();
		// And finally, validate the file.
		try {
			System.out.println(configFile);
			validator.validate(new StreamSource(configFile));
		} catch (SAXException e) {
			throw new ConfigurationException(
					"configuration file XML validation problem "
							+ "(invalid format)", e) ;
		} catch (IOException e) {
			throw new ConfigurationException(
					"configuration file I/0 problem", e) ;
		}
		return true ;
	}

	/**
	 * parse the configuration file and return the information as an instance
	 * of <code>ConfigurationParameters</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param configFile				the File object which reference must be parsed.
	 * @return							the configuration parameters.
	 * @throws ConfigurationException	<i>to do.</i>
	 */
	public ConfigurationParameters	parseConfigurationFile(File configFile)
			throws	ConfigurationException, XPathExpressionException
	{
		String			identificationUid = null;
		String			identificationOffered = null;
		double			consumptionMin = -1;
		double			consumptionNominal = -1;
		double			consumptionMax = -1;
		String			required = null;
		ArrayList<InstanceVar>	instanceVars = new ArrayList<InstanceVar>();
		ArrayList<Operation>	operations = new ArrayList<Operation>();
		Internal		internal = null;
		String 			thrownException = null;
		String 			equipmentRef = null;
		String 			body = null;

		Document doc = null;
		try {
			doc = this.db.parse(configFile);
		} catch (SAXException e) {
			throw new ConfigurationException(
					"configuration file XML parsing problem "
							+ "(invalid format)", e) ;
		} catch (IOException e) {
			throw new ConfigurationException(
					"configuration file I/0 problem", e) ;
		}

		XPath xpathEvaluator = XPathFactory.newInstance().newXPath();

		Node controlAdapterNode;
		try {
			controlAdapterNode = ((Node)xpathEvaluator.evaluate(
					"control-adapter",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the identification node", e) ;
		}
		
		identificationUid = controlAdapterNode.getAttributes().item(1).getNodeValue();
		identificationOffered = controlAdapterNode.getAttributes().item(0).getNodeValue();

		Node consumptionNode;
		try{
			consumptionNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/consumption",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the consumption node", e) ;
		}
		consumptionMin = Double.parseDouble(consumptionNode.getAttributes().item(1).getNodeValue());
		consumptionNominal = Double.parseDouble(consumptionNode.getAttributes().item(2).getNodeValue());
		consumptionMax = Double.parseDouble(consumptionNode.getAttributes().item(0).getNodeValue());
		

		try{
			required = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/required",
					doc,
					XPathConstants.NODE)).getNodeValue();
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the required node", e) ;
		}

		NodeList varsList = doc.getElementsByTagName("instance-var");
		for (int i = 0; i < varsList.getLength(); i++) {
			Node instanceVar = (Node) varsList.item(i);
			String modifiers = null;
			try {
				modifiers = ((Node)xpathEvaluator.evaluate(
						"@modifiers", instanceVar,
						XPathConstants.NODE)).getNodeValue();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			String type = null;
			try {
				type = ((Node)xpathEvaluator.evaluate(
						"@type", instanceVar,
						XPathConstants.NODE)).getNodeValue();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			String name = null;
			try {
				name = ((Node)xpathEvaluator.evaluate(
						"@name", instanceVar,
						XPathConstants.NODE)).getNodeValue();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			String staticInit = null;
			try {
				staticInit = ((Node)xpathEvaluator.evaluate(
						"@static-init", instanceVar,
						XPathConstants.NODE)).getNodeValue();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			instanceVars.add(new InstanceVar(modifiers, type, name, staticInit));
		}
//		for(int i=0;i<instanceVars.size();i++)
//			System.out.println(instanceVars.get(i).modifiers + " " + instanceVars.get(i).type + " " +instanceVars.get(i).name+ " = " + instanceVars.get(i).staticInit);

		Node internalNode;
		String internalModifiers = null;
		String internalType = null;
		String internalName = null;
		Parameter internalParameter = null;
		
		try{
			internalNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/internal",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the internal node", e) ;
		}
		if (internalNode != null) {
			try {
				internalModifiers = (((Node)xpathEvaluator.evaluate(
						"@modifiers",
						internalNode,
						XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the internal modifiers node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the internal modifiers node", e) ;
			}
			try {
				internalType = (((Node)xpathEvaluator.evaluate(
						"@type",
						internalNode,
						XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the internal type node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the internal type node", e) ;
			}
			try {
				internalName = (((Node)xpathEvaluator.evaluate(
						"@name",
						internalNode,
						XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the internal name node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the internal name node", e) ;
			}
			Node parameterNode;
			try{
				parameterNode = ((Node)xpathEvaluator.evaluate(
						"parameter",
						internalNode,
						XPathConstants.NODE));
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the internal node", e) ;
			}
			String name = parameterNode.getAttributes().item(0).getNodeValue();
			String type = parameterNode.getAttributes().item(1).getNodeValue();
			internalParameter = new Parameter(type,name);

			Node thrownNode;
			try {
				thrownNode = ((Node)xpathEvaluator.evaluate(
						"thrown",
						internalNode,
						XPathConstants.NODE));
				thrownException = thrownNode.getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the thrown node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the thrown node", e) ;
			}
			
			Node bodyNode;
			try {
				bodyNode = ((Node)xpathEvaluator.evaluate(
						"body",
						internalNode,
						XPathConstants.NODE));
				try {
					equipmentRef = (((Node)xpathEvaluator.evaluate(
							"@equipmentRef",
							bodyNode,
							XPathConstants.NODE)).getNodeValue());
					body = bodyNode.getTextContent();
				} catch (DOMException e) {
					throw new ConfigurationException(
							"node access error for the equipmentRef inside internal body node",
							e) ;
				} catch (XPathExpressionException e) {
					throw new ConfigurationException(
							"error fetching the equipmentRef inside internal body node", e) ;
				}
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the internal equipmentRef node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the internal equipmentRef node", e) ;
			}
			internal = new Internal(internalModifiers, 
					internalType, 
					internalName,
					internalParameter,
					thrownException,
					equipmentRef,
					body);
		}
		
		
		Node maxModeNode;
		try{
			maxModeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/maxMode",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the maxMode node", e) ;
		}
		if (maxModeNode != null) {
			String maxModeBody;
			try {
				maxModeBody = ((Node)xpathEvaluator.evaluate(
								"body",
								maxModeNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for maxMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the maxMode body node", e) ;
			}
			 operations.add(new Operation("maxMode","int",null,new Body(null,null,maxModeBody)));
		}
		
		Node upModeNode;
		try{
			upModeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/upMode",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the upMode node", e) ;
		}
		if (upModeNode != null) {
			String upModeBody;
			try {
				upModeBody = ((Node)xpathEvaluator.evaluate(
								"body",
								upModeNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for upMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the upMode body node", e) ;
			}
			 operations.add(new Operation("upMode","boolean",null,new Body(null,null,upModeBody)));
		}

		Node downModeNode;
		try{
			downModeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/downMode",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the downMode node", e) ;
		}
		if (downModeNode != null) {
			String downModeBody;
			try {
				downModeBody = ((Node)xpathEvaluator.evaluate(
								"body",
								downModeNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for downMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the downMode body node", e) ;
			}
			 operations.add(new Operation("downMode","boolean",null,new Body(null,null,downModeBody)));
		}
		
		Node setModeNode;
		try{
			setModeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/setMode",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the setMode node", e) ;
		}
		if (setModeNode != null) {
			String setModeBody;
			Parameter opParameter;
			try {
				opParameter = 
					new Parameter(
						((Node)xpathEvaluator.evaluate
							("parameter",setModeNode,
								XPathConstants.NODE)).getAttributes().item(1).getNodeValue(),
						((Node)xpathEvaluator.evaluate
							("parameter",setModeNode,
								XPathConstants.NODE)).getAttributes().item(0).getNodeValue());
								
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			try {
				setModeBody = ((Node)xpathEvaluator.evaluate(
								"body",
								setModeNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for setMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the setMode body node", e) ;
			}
			 operations.add(new Operation("setMode","boolean", opParameter ,new Body(null,null,setModeBody)));
		}
		
		Node currentModeNode;
		try{
			currentModeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/currentMode",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the currentMode node", e) ;
		}
		if (currentModeNode != null) {
			String currentModeBody;
			try {
				currentModeBody = ((Node)xpathEvaluator.evaluate(
								"body",
								currentModeNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for currentMode body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the currentMode body node", e) ;
			}
			 operations.add(new Operation("currentMode","int",null,new Body(null,null,currentModeBody)));
		}
		
		Node suspendedNode;
		try{
			suspendedNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/suspended",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the suspended node", e) ;
		}
		if (suspendedNode != null) {
			String suspendedBody;
			try {
				suspendedBody = ((Node)xpathEvaluator.evaluate(
								"body",
								suspendedNode,
								XPathConstants.NODE)).getTextContent();
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for suspended body node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the suspended body node", e) ;
			}
			 operations.add(new Operation("suspended","boolean",null,new Body(null,null,suspendedBody)));
		}
		
		Node suspendNode;
		try{
			suspendNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/suspend",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the suspend node", e) ;
		}
		if (suspendNode != null) {
			Node bodyNode;
			try {
				bodyNode = ((Node)xpathEvaluator.evaluate(
						"body",
						suspendNode,
						XPathConstants.NODE));
				try {
					equipmentRef = (((Node)xpathEvaluator.evaluate(
							"@equipmentRef",
							bodyNode,
							XPathConstants.NODE)).getNodeValue());
					body = bodyNode.getTextContent();
				} catch (DOMException e) {
					throw new ConfigurationException(
							"node access error for the equipmentRef inside suspend body node",
							e) ;
				} catch (XPathExpressionException e) {
					throw new ConfigurationException(
							"error fetching the equipmentRef inside suspend body node", e) ;
				}
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the suspend node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the suspend node", e) ;
			}
			 operations.add(new Operation("suspend","boolean",null,new Body(null,equipmentRef,body)));
		}
		
		Node resumeNode;
		try{
			resumeNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/resume",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the resume node", e) ;
		}
		if (resumeNode != null) {
			Node bodyNode;
			try {
				bodyNode = ((Node)xpathEvaluator.evaluate(
						"body",
						resumeNode,
						XPathConstants.NODE));
				try {
					equipmentRef = (((Node)xpathEvaluator.evaluate(
							"@equipmentRef",
							bodyNode,
							XPathConstants.NODE)).getNodeValue());
					body = bodyNode.getTextContent();
				} catch (DOMException e) {
					throw new ConfigurationException(
							"node access error for the equipmentRef inside resume body node",
							e) ;
				} catch (XPathExpressionException e) {
					throw new ConfigurationException(
							"error fetching the equipmentRef inside resume body node", e) ;
				}
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the resume node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the resume node", e) ;
			}
			 operations.add(new Operation("resume","boolean",null,new Body(null,equipmentRef,body)));
		}
		//System.out.println(operations.get("resume"));
		
		Node emergencyNode;
		try{
			emergencyNode = ((Node)xpathEvaluator.evaluate(
					"/control-adapter/emergency",
					doc,
					XPathConstants.NODE));
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(
					"error fetching the emergency node", e) ;
		}
		if (emergencyNode != null) {
			Node bodyNode;
			try {
				bodyNode = ((Node)xpathEvaluator.evaluate(
						"body",
						emergencyNode,
						XPathConstants.NODE));
				try {
					equipmentRef = (((Node)xpathEvaluator.evaluate(
							"@equipmentRef",
							bodyNode,
							XPathConstants.NODE)).getNodeValue());
					body = bodyNode.getTextContent();
				} catch (DOMException e) {
					throw new ConfigurationException(
							"node access error for the equipmentRef inside emergency body node",
							e) ;
				} catch (XPathExpressionException e) {
					throw new ConfigurationException(
							"error fetching the equipmentRef inside emergency body node", e) ;
				}
			} catch (DOMException e) {
				throw new ConfigurationException(
						"node access error for the emergency node",
						e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
						"error fetching the emergency node", e) ;
			}
			operations.add(new Operation("emergency","double",null,new Body(null,equipmentRef,body)));
		}
		
//		for(Operation o : operations)
//			System.out.println(o.name+": "+o.body.text);
		
		return new ConfigurationParameters(identificationUid,
				identificationOffered,
				consumptionMin,
				consumptionNominal,
				consumptionMax,
				required,
				instanceVars,
				operations,
				internal
				) ;
	}
}
// -----------------------------------------------------------------------------
