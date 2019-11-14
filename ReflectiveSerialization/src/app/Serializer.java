package app;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Set;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Serializer {
	private BitSet serialized_object_ids = new BitSet();
	//private ArrayList<Object> referenced_objects = new ArrayList<Object>();
	private PriorityQueue<Object> referenced_objects = new PriorityQueue<Object>();
	//Hashtable<Integer, Object> referenced_objects = new Hashtable<Integer, Object>();
	
	public void serialize(Object obj) {
		Element root = new Element("serialized");
		Document document = new Document(root);
		Element object_element;
		Object next_obj = obj;
		int object_id;

		while (next_obj != null) {
			object_id = System.identityHashCode(next_obj);
			serialized_object_ids.set(object_id);
			root.addContent(createObjectElement(next_obj, object_id));
			next_obj = referenced_objects.poll();
		}
		
		outputAsXml(document, System.out);
	}
	
	private Element createObjectElement(Object obj, int object_id) {
		Field[] fields;
		Element object_element = new Element("object");
		Class<?> object_class = obj.getClass();
		Object field_value;
		
		object_element.setAttribute("class", object_class.getSimpleName());
		object_element.setAttribute("id", String.valueOf(object_id));
		
		fields = object_class.getDeclaredFields();
		for (Field f: fields) {
			f.setAccessible(true);
			try {
				field_value = f.get(obj);
				object_element.addContent(createFieldElement(field_value, f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object_element;
	}
	
	private Element createFieldElement(Object field_value, Field field) {
		Element field_element = new Element("field");
		int field_value_id = System.identityHashCode(field_value);
		
		field_element.setAttribute("name", field.getName());
		field_element.setAttribute("declaringclass", String.valueOf(field.getDeclaringClass().getSimpleName()));
		
		if (isPrimitive(field_value)) {
			field_element.addContent(createValueElement(field_value));
		} else {
			field_element.addContent(createReferenceElement(field_value_id));
			if (!serialized_object_ids.get(field_value_id)) {
				referenced_objects.add(field_value);
			}
		}
		
		return field_element;
	}
	
	private static void outputAsXml(Document document, OutputStream out) {
		XMLOutputter xmlout = new XMLOutputter();
		
		try {
			xmlout.setFormat(Format.getPrettyFormat());
			xmlout.output(document, out);			
		} catch(IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static Element createReferenceElement(int object_id) {
		Element reference_element = new Element("reference");
		
		reference_element.setText(String.valueOf(object_id));
		return reference_element;
	}
	
	private static Element createValueElement(Object field_value) {
		Element value_element = new Element("value");
		
		value_element.setText(String.valueOf(field_value));
		return value_element;
	}
	
	private boolean alreadySerialized(Object obj) {
		return serialized_object_ids.get(System.identityHashCode(obj));
	}
	
	private static boolean isPrimitive(Object obj) {
		return isWrapperType(obj.getClass());
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
    
    public static void main(String[] args) {
    	Serializer s = new Serializer();
    	s.serialize(new Simple());
    }

}
