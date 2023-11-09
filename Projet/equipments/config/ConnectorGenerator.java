package equipments.config;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.XPathExpressionException;

import equipments.config.ConfigurationFileParser;
import equipments.config.ConfigurationParameters;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cvm.config.exceptions.ConfigurationException;
import fr.sorbonne_u.components.cvm.config.exceptions.InvalidConfigurationFileFormatException;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import javassist.*;

/**
 * 
 * @author walte
 * Class that generates a connector from an XML descriptor with MakeConnectors class
 * the connector implements AbstractConnector and uses AdjustableCI as interface
 * along with the configuration parsed
 */
public class ConnectorGenerator {
	/**
	 * 
	 * @param descriptor
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> generate(String descriptor) throws ClassNotFoundException {
		String configFileName = "Projet/equipments/HEM/"+descriptor;
		File configFile = new File(configFileName);
		try {
			ConfigurationFileParser cfp = new ConfigurationFileParser();
			if (!cfp.validateConfigurationFile(configFile)) {
				throw new InvalidConfigurationFileFormatException(
								"invalid configuration file " + configFileName);
			}
			ConfigurationParameters configurationParameters = cfp.parseConfigurationFile(configFile);
			
			String className = ""+configurationParameters.getInternal().equipmentRef+"";
			
			ClassPool pool = ClassPool.getDefault();
			Loader cl = new Loader(pool);
			Class<?> controlCI = null;
			try {
				controlCI = Class.forName(configurationParameters.identificationOffered);
			} catch(ClassNotFoundException e) {
				System.out.println("La classe " + className+"ExternalControlCI" + " n’existe pas.");
				throw e;
			}
			Class<?> adjustableCI = null;
			try {
				adjustableCI = Class.forName("equipments.HEM.AdjustableCI");
			} catch(ClassNotFoundException e) {
				System.out.println("La classe AdjustableCI n’est pas trouvable.");
				throw e;
			}
			Class<?> connector = null;
			try {
				connector = MakeConnectors.makeConnectorClassJavassist(
									"equipments.HEM."+className+"Connector",
									AbstractConnector.class,
									adjustableCI,
									controlCI,
									configurationParameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return connector;
			
		} catch (ConfigurationException | XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
