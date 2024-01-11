package equipments.config;

import java.util.ArrayList;
import javassist.*;
import javassist.Modifier;

/**
 * 
 * @author walte
 * Class that makes a connector from a configuration obtained by parsing an XML file
 */
public class MakeConnectors {
	
	/**
	 * 
	 * @param connectorCanonicalClassName
	 * @param connectorSuperclass
	 * @param connectorImplementedInterface
	 * @param offeredInterface
	 * @param cfp
	 * @return
	 * @throws Exception
	 */
	public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName,
			Class<?> connectorSuperclass,
			Class<?> connectorImplementedInterface,
			Class<?> offeredInterface,
			ConfigurationParameters cfp
			) throws Exception

	{
		ClassPool pool = ClassPool.getDefault() ;
		CtClass cs = pool.get(connectorSuperclass.getCanonicalName()) ;
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName()) ;
		CtClass oi = pool.get(offeredInterface.getCanonicalName()) ;
		CtClass connectorCtClass = pool.makeClass("equipments.HEM."+connectorCanonicalClassName+"Connector") ;
		connectorCtClass.setSuperclass(cs) ;
		ArrayList<Operation> cfpOps = cfp.getOperations();
		ArrayList<InstanceVar> instanceVars = cfp.getInstanceVars();
		
		for(int i=0; i<instanceVars.size()-2; i++) {
			CtField field = new CtField(toType(instanceVars.get(i).type),
												instanceVars.get(i).name,
												connectorCtClass);
			field.setModifiers(Modifier.PROTECTED);
			field.setModifiers(Modifier.FINAL);
			connectorCtClass.addField(field, instanceVars.get(i).staticInit);
		}
		for(int i=instanceVars.size()-2; i<instanceVars.size(); i++) {
			CtField field = new CtField(toType(instanceVars.get(i).type),
												instanceVars.get(i).name,
												connectorCtClass);
			field.setModifiers(Modifier.PROTECTED);
			connectorCtClass.addField(field);
		}
		
		String internal = "public " ;
		internal += cfp.getInternal().type + " " ;
		internal += cfp.getInternal().name + "(" ;
		Parameter pt = cfp.getInternal().parameter ;
		if(pt != null) {
			internal += pt.type + " " + pt.name ;
		}
		internal += ") throws java.lang.Exception";
		internal += "\n{" ;
		internal += cfp.getInternal().body;
		System.out.println(cfp.getInternal().equipmentRef);
		String n = internal.replace(
				cfp.getInternal().equipmentRef,
				"(("+offeredInterface.getCanonicalName() + ")this.offering)");
		n += "\n}";
		connectorCtClass.addMethod(CtMethod.make(n, connectorCtClass)) ;
		System.out.println(cfp.internal.name);
		
		for (int i = cfpOps.size()-1 ; i >= 0; i--) {
			String source = "public " ;
			source += cfpOps.get(i).type + " " ;
			source += cfpOps.get(i).name + "(" ;
			pt = cfpOps.get(i).parameter ;
			if(pt != null) {
				source += pt.type + " " + pt.name ;
			}
			source += ") throws java.lang.Exception";
			source += "\n{" ;
			source += cfpOps.get(i).body.text;
			if(cfpOps.get(i).body.equipmentRef != null ){
				source = source.replace(
						cfpOps.get(i).body.equipmentRef,
						"(("+offeredInterface.getCanonicalName() + ")this.offering)");
			}
			//source += "(" + callParam + ") ;\n}" ;
			source += "\n}";
			n = source.replace(
						cfp.internal.name,
						"this."+cfp.internal.name);
			CtMethod theCtMethod = CtMethod.make(n, connectorCtClass) ;
			connectorCtClass.addMethod(theCtMethod) ;
		}
		connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
		CtConstructor c = CtNewConstructor.make("public "+connectorCanonicalClassName+"Connector() {\r\n"
				+ "		super();\r\n"
				+ "		this.currentMode = MAX_MODE;\r\n"
				+ "		this.isSuspended = false;\r\n"
				+ "	}", connectorCtClass);
		connectorCtClass.addConstructor(c);
		cii.detach() ; cs.detach() ; oi.detach() ;
		
//		for(int i=0;i<connectorCtClass.getFields().length; i++) {
//			System.out.println(connectorCtClass.getFields()[i]);
//			System.out.println(connectorCtClass.getFields()[i].getConstantValue());
//		}
		
		Class<?> ret = connectorCtClass.toClass() ;
		connectorCtClass.detach() ;
		return ret ;
	}
	
	/**
	 * Returns CtClass type matching with entry type
	 * @param type
	 * @return CtClass type
	 */
	public static CtClass toType( String type ) {
	    if( type.equals("int") ) return CtClass.intType;
	    if( type.equals("double") ) return CtClass.doubleType;
	    if( type.equals("boolean") ) return CtClass.booleanType;
	    if( type.equals("void") ) return CtClass.voidType;
	    else return null;
	}
	
}