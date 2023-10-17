package equipments.config;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class TestJavassist {
	
	public TestJavassist() {
		
	}
	
	public Class<?> getInstanceClass() {
		 try {
	            ClassPool classPool = ClassPool.getDefault();
	            CtClass ctClass = classPool.makeClass("HelloWorldClass");

	            CtMethod printHelloWorldMethod = CtMethod.make("public void printHelloWorld() { System.out.println(\"Hello, World!\"); }", ctClass);
	            ctClass.addMethod(printHelloWorldMethod);
	            CtMethod printPong = CtMethod.make("public void printPong() { System.out.println(\"PONG!\"); }", ctClass);
	            ctClass.addMethod(printPong);
	            
	            ctClass.writeFile(System.getProperty("user.dir")+"/Projet/equipments/config");

	            Class<?> loadedClass = ctClass.toClass();

	            // Créez une instance de la classe chargée en mémoire
	            Object instance = loadedClass.getDeclaredConstructor().newInstance();

//	            return instance;
	            return loadedClass;
//	            loadedClass.getMethod("printHelloWorld").invoke(instance);
//	            loadedClass.getMethod("printPong").invoke(instance);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return null;
	}
}




