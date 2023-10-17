package equipments.config;

import java.lang.reflect.InvocationTargetException;

public class TestRecuperation {
	public static void main(String[] args) {
		TestJavassist res = new TestJavassist();

		Class<?> loadedClass = res.getInstanceClass();
		
		try {
			Object instance = loadedClass.getDeclaredConstructor().newInstance();
			loadedClass.getMethod("printPong").invoke(instance);
			 loadedClass.getMethod("printHelloWorld").invoke(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
