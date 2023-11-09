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

public class ConnectorGenerator {
	public static Class<?> generate(String descriptor) throws ClassNotFoundException {
//		TestJavassist res = new TestJavassist();
//
//		Class<?> loadedClass = res.getInstanceClass();
//		
//		try {
//			Object instance = loadedClass.getDeclaredConstructor().newInstance();
//			loadedClass.getMethod("printPong").invoke(instance);
//			 loadedClass.getMethod("printHelloWorld").invoke(instance);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
				adjustableCI = Class.forName("fr.sorbonne_u.components.hem2023.bases.AdjustableCI");
			} catch(ClassNotFoundException e) {
				System.out.println("La classe AdjustableCI n’est pas trouvable.");
				throw e;
			}
			Class<?> test = null;
			try {
				test = MakeConnectors.makeConnectorClassJavassist(
									"equipements.config."+className+"Connector",
									AbstractConnector.class,
									adjustableCI,
									controlCI,
									configurationParameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return test;
//			// Créez une instance de la classe chargée en mémoire
//            Object instance = null;
//			try {
//				instance = test.getDeclaredConstructor().newInstance();
//				System.out.println(instance.getClass().getMethod("emergency").getName());
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            System.out.println(instance.toString());
			
			
		} catch (ConfigurationException | XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
