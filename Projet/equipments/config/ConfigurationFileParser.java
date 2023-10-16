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
import java.util.HashSet;
import java.util.Hashtable;
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
 * and parse component deployment configuration files.
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
	public static String	SCHEMA_FILENAME = "config" + File.separatorChar +
															"control-adapter.rnc" ;
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
	throws	ConfigurationException
	{
		long			identificationUid = -1;
		String			identificationOffered = null;
		double			consumptionMin = -1;
		double			consumptionNominal = -1;
		double			consumptionMax = -1;
		String			required = null;
		InstanceVar[]	instanceVars = null;
		String			internalModifiers = null;
		String			internalType = null;
		String			internalName = null;
		Parameter		internalParameter = new Parameter(null, null);
		Body			maxMode = new Body(null, null, null);
		Body			upMode = new Body(null, null, null);
		Body			downMode = new Body(null, null, null);
		Body			setMode = new Body(null, null, null);
		Body			currentMode = new Body(null, null, null);
		Body			suspended = new Body(null, null, null);
		Body			suspend = new Body(null, null, null);
		Body			resume = new Body(null, null, null);
		Body			emergency = new Body(null, null, null);
		
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
		
		Node identificationNode;
		try {
			identificationNode = ((Node)xpathEvaluator.evaluate(
							"/control-adapter/identification",
							doc,
							XPathConstants.NODE));
		} catch (XPathExpressionException e) {
		throw new ConfigurationException(
		"error fetching the identification node", e) ;
		}
		
		if (identificationNode != null) {
			try {
				identificationUid =
						Long.parseLong(((Node)xpathEvaluator.evaluate(
								"@uid",
								identificationNode,
								XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
							"node access error for the identification uid node",
							e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
							"error fetching the identification uid node", e) ;
			}
			try {
				identificationOffered =
						((Node)xpathEvaluator.evaluate(
								"@offered",
								identificationNode,
								XPathConstants.NODE)).getNodeValue();
			} catch (DOMException e) {
				throw new ConfigurationException(
							"node access error for the identification offered node",
							e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
							"error fetching the identification offered node", e) ;
			}
		}
		
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
		
		if (consumptionNode != null) {
			try {
				consumptionMin =
						Double.parseDouble(((Node)xpathEvaluator.evaluate(
								"@consumptionMin",
								consumptionNode,
								XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
							"node access error for the consumption min node",
							e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
							"error fetching the consumption min node", e) ;
			}
			try {
				consumptionNominal =
						Double.parseDouble(((Node)xpathEvaluator.evaluate(
								"@consumptionNominal",
								consumptionNode,
								XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
							"node access error for the consumption nominal node",
							e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
							"error fetching the consumption nominal node", e) ;
			}
			try {
				consumptionMax =
						Double.parseDouble(((Node)xpathEvaluator.evaluate(
								"@consumptionMax",
								consumptionNode,
								XPathConstants.NODE)).getNodeValue());
			} catch (DOMException e) {
				throw new ConfigurationException(
							"node access error for the consumption max node",
							e) ;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(
							"error fetching the consumption max node", e) ;
			}
		}

		return new ConfigurationParameters(identificationUid,
											identificationOffered,
											consumptionMin,
											consumptionNominal,
											consumptionMax,
											required,
											instanceVars,
											internalModifiers,
											internalType,
											internalName,
											internalParameter,
											maxMode,
											upMode,
											downMode,
											setMode,
											currentMode,
											suspended,
											suspend,
											resume,
											emergency
											) ;
	}
}
// -----------------------------------------------------------------------------
