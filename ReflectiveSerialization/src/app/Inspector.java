package app;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class<?> c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }
    
    public void inspectFields(Object obj, boolean recursive) {
    	Class<?> c = obj.getClass();
    	inspectFields(c, obj, recursive, 0);
    }

    private void inspectClass(Class<?> c, Object obj, boolean recursive, int depth) {
    	String indent = getTabs(depth);
    	
    	System.out.println("Entering new class with depth: " + depth);
    	if (c.isArray()) {
    		inspectArray(c, obj, recursive, depth);
    	} else {
    		System.out.println(indent + "Declaring Class Name: " 
    					+ Modifier.toString(c.getModifiers()) + " " + c.getCanonicalName());
    	}
    	inspectSuperClass(c, obj, depth);
    	inspectInterfaces(c, obj, depth);
    	inspectConstructors(c, depth);
    	inspectMethods(c, depth);
    	inspectFields(c, obj, recursive, depth);
    }
    
    private void inspectSuperClass(Class<?> c, Object obj, int depth) {
    	Class<?> super_class;
    	String indent = getTabs(depth);
    	
    	super_class = c.getSuperclass();
    	System.out.println(indent + "Super-class: ");
    	if (super_class != Object.class && super_class != null) {
    		inspectClass(super_class, obj, true, depth + 1);
    	} else {
    		System.out.println(indent + " None");
    	}
    }
    
    private void inspectInterfaces(Class<?> c, Object obj, int depth) {
    	Class<?>[] interfaces;
    	String indent = getTabs(depth);
    	
    	interfaces = c.getInterfaces();
    	System.out.println(indent + "Interfaces: ");
    	for (int i = 0; i < interfaces.length; i+=1) {
    		inspectClass(interfaces[i], obj, true, depth + 1);
    	}
    	if (interfaces.length == 0) {
    		System.out.println(indent + " None");
    	}
    }
    
    private void inspectConstructors(Class<?> c, int depth) {
    	Constructor<?>[] constructors = c.getDeclaredConstructors();
    	String indent = getTabs(depth);
    	
    	for (int i = 0; i < constructors.length; i+=1) {
    		System.out.println(indent + "Constructor " 
    						+ Integer.toString(i) + " of " 
    						+ c.getCanonicalName() 
    						+ " with depth: " + depth);
    		constructors[i].setAccessible(true);
    		System.out.println(indent + " Name: " + constructors[i].getName());
    		inspectParameters(constructors[i], depth);
    		System.out.println(indent + " Modifier: " + Modifier.toString(constructors[i].getModifiers()));
    	}
    	
    }
    
    private void inspectMethods(Class<?> c, int depth) {
    	Method[] methods = c.getDeclaredMethods();
    	String indent = getTabs(depth);
    	
    	for (int i = 0; i < methods.length; i+=1) {
    		System.out.println(indent + "Method " 
    				+ Integer.toString(i) + " of " 
					+ c.getCanonicalName() 
					+ " with depth: " + depth);
    		methods[i].setAccessible(true);
    		System.out.println(indent + " Name: " + methods[i].getName());
    		
    		inspectParameters(methods[i], depth);
    		inspectExceptions(methods[i], depth);
    		
    		System.out.println(indent + " Return Type: " + methods[i].getReturnType());
    		System.out.println(indent + " Modifier: " + Modifier.toString(methods[i].getModifiers()));
    	}
    }
    
    private void inspectParameters(Executable executable, int depth) {
    	String indent = getTabs(depth);
    	Parameter[] parameters = executable.getParameters();
    	
    	if (parameters.length > 0) {
			System.out.print(indent + " Parameter Types: " + parameters[0].getType());
			for (int l = 1; l < parameters.length; l+=1) {
    			System.out.print(", " + parameters[l].getType());
    		}
			System.out.println();
		} else {
			System.out.println(indent + " Parameter Types: None");
		}
    }
    
    private void inspectExceptions(Executable executable, int depth) {
    	String indent = getTabs(depth);
    	Class<?>[] exceptions = executable.getExceptionTypes();
    	
		if (exceptions.length > 0) {
			System.out.print(indent + " Throws Exceptions: " + exceptions[0].getCanonicalName());
			for (int l = 1; l < exceptions.length; l+=1) {
    			System.out.print(", " + exceptions[l].getCanonicalName());
    		}
			System.out.println();
		} else {
			System.out.println(indent + " Throws Exceptions: None");
		}
    }
    
    private void inspectFields(Class<?> c, Object obj, boolean recursive, int depth) {
    	String indent = getTabs(depth);
    	Field[] fields = c.getDeclaredFields();
    	
    	for (int i = 0; i < fields.length; i+=1) {
    		System.out.println(indent + "Field " 
    				+ Integer.toString(i) + " of " 
					+ c.getCanonicalName() 
					+ " with depth: " + depth);
    		fields[i].setAccessible(true);
    		System.out.println(indent + " Name: " + fields[i].getName());
    		System.out.println(indent + " Type: " + fields[i].getType());
    		System.out.println(indent + " Modifier: " + Modifier.toString(fields[i].getModifiers()));
    		try {
				inspectValue(fields[i].get(obj), recursive, depth);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void inspectValue(Object obj, boolean recursive, int depth) {
    	Class<?> obj_class;
    	String indent = getTabs(depth);
    	
		if (obj == null) {
			System.out.println(indent + " Value: null");
		} else {
			obj_class = obj.getClass();
			if (isWrapperType(obj_class)) {
    			System.out.println(indent + " Value: " + obj);
    		} else {
    			if (recursive) {
        			System.out.println(indent + " Value: ");
        			inspectClass(obj_class, obj, true, depth + 1);
        		} else {
        			System.out.println(indent + " Value: " + obj_class.getCanonicalName()
        					+ "@" + System.identityHashCode(obj));
        		}
    		}
		}
    }
    
    private void inspectArray(Class<?> c, Object obj, boolean recursive, int depth) {
    	String indent = getTabs(depth);
    	int length = Array.getLength(obj);
    	
    	System.out.println(indent + "Declaring Class Name: " 
				+ Modifier.toString(c.getModifiers()) + " " + c.getCanonicalName());
    	System.out.println(indent + "Component Type: " + c.getComponentType().getCanonicalName());
    	System.out.println(indent + "Length: " + Integer.toString(length));
    	if (length > 0) {
    		System.out.println(indent + "Content: ");
    	} else {
    		System.out.println(indent + "Content: None");
    	}
    	for (int i = 0; i < length; i+=1) {
    		inspectValue(Array.get(obj, i), recursive, depth);
    	}
    }
    
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    private static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
    private String getTabs(int num_tabs) {
    	String tabs = "";
    	for (int i = 0; i < num_tabs; i++) {
    		tabs += "\t";
    	}
    	return tabs;
    }

}
