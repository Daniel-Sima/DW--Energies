package equipments.config;

import java.util.ArrayList;
import java.util.HashMap;

import javassist.*;
import javassist.Modifier;

import java.lang.reflect.*;

public class MakeConnectors {
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
		CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName) ;
		connectorCtClass.setSuperclass(cs) ;
		ArrayList<Operation> cfpOps = cfp.getOperations();
		ArrayList<InstanceVar> instanceVars = cfp.getInstanceVars();
		
		for(int i=0; i<instanceVars.size(); i++) {
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
//		String callParam = "" ;
		if(pt != null) {
			internal += pt.type + " " + pt.name ;
//			callParam += pt.name ;
		}
		internal += ") throws java.lang.Exception";
		internal += "\n{" ;
		internal += cfp.getInternal().body;
		System.out.println(cfp.getInternal().equipmentRef);
		System.out.println(internal);
		String n = internal.replace(
				cfp.getInternal().equipmentRef,
				"(("+offeredInterface.getCanonicalName() + ")this.offering)");
		System.out.println("(("+offeredInterface.getCanonicalName() + ")this.offering)");
		//internal += "(" + callParam + ") ;\n}" ;
		n += "\n}";
		System.out.println(n);
		connectorCtClass.addMethod(CtMethod.make(n, connectorCtClass)) ;
		
//		Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods() ;
		for (int i = cfpOps.size()-1 ; i > 0; i--) {
			String source = "public " ;
			source += cfpOps.get(i).type + " " ;
			source += cfpOps.get(i).name + "(" ;
			pt = cfpOps.get(i).parameter ;
//			String callParam = "" ;
			if(pt != null) {
				source += pt.type + " " + pt.name ;
//				callParam += pt.name ;
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
			System.out.println(source);
			CtMethod theCtMethod = CtMethod.make(source, connectorCtClass) ;
			connectorCtClass.addMethod(theCtMethod) ;
		}
		connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
		CtConstructor c = CtNewConstructor.make("public AirConditioningConnector() {\r\n"
				+ "		super();\r\n"
				+ "		this.currentMode = MAX_MODE;\r\n"
				+ "		this.isSuspended = false;\r\n"
				+ "	}", connectorCtClass);
		connectorCtClass.addConstructor(c);
		cii.detach() ; cs.detach() ; oi.detach() ;
		
		for(int i=0;i<connectorCtClass.getMethods().length; i++) {
			System.out.println(connectorCtClass.getMethods()[i].getName());
		}
		
		Class<?> ret = connectorCtClass.toClass() ;
		connectorCtClass.detach() ;
		return ret ;
	}
	
	public static CtClass toType( String type ) {
	    if( type.equals("int") ) return CtClass.intType;
	    if( type.equals("double") ) return CtClass.doubleType;
	    if( type.equals("boolean") ) return CtClass.booleanType;
	    if( type.equals("void") ) return CtClass.voidType;
	    else return null;
	}

//	HashMap<String, String> methodNamesMap = new HashMap<String, String>() ;
//	methodNamesMap.put("sum", "add") ;
//	Class<?> connectorClass =
//			this.makeConnectorClassJavassist(
//					"fr.upmc.alasca.summing.assembly.GeneratedConnector",
//					AbstractConnector.class,
//					SummingServiceI.class,
//					CalculatorServicesI.class,
//					methodNamesMap) ;
//	this.getOwner().doPortConnection(
//			clientPortURI,
//			serverPortURI,
//			connectorClass.getCanonicalName()) ;
}