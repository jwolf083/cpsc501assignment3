package app;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Serializer {
	private BitSet serialized_object_ids = new BitSet();
	//private ArrayList<Object> referenced_objects = new ArrayList<Object>();
	//private PriorityQueue<Object> referenced_objects = new PriorityQueue<Object>();
	Queue referenced_objects = new Queue(); 
	//Hashtable<Integer, Object> referenced_objects_table = new Hashtable<Integer, Object>();
	
	public void serialize(Object obj) {
		Element root = new Element("serialized");
		Document document = new Document(root);
		Object next_obj = obj;

		serialized_object_ids.set(System.identityHashCode(next_obj));
		while (next_obj != null) {
			root.addContent(createObjectElement(next_obj));
			next_obj = referenced_objects.poll();
		}
		
		outputAsXml(document, System.out);
	}
	
	private Element createObjectElement(Object obj) {
		Element object_element = new Element("object");
		Class<?> object_class = obj.getClass();
		int object_id = System.identityHashCode(obj);
		int array_length;
		
		object_element.setAttribute("class", object_class.getName());
		object_element.setAttribute("id", String.valueOf(object_id));
		
		if (object_class.isArray()) {
			array_length = Array.getLength(obj);
			object_element.setAttribute("length", String.valueOf(array_length));
			loadObjectsArrayElements(obj, object_element, array_length);
		} else {
			loadObjectsFieldElements(obj, object_element, object_class);
		}
		
		return object_element;
	}
	
	private void loadObjectsArrayElements(Object obj, Element object_element, int array_length) {
		
		for (int i = 0; i < array_length; i+=1) {
			object_element.addContent(getObjectDataElement(Array.get(obj, i)));
		}
	}
	
	private void loadObjectsFieldElements(Object obj, Element object_element, Class<?> object_class) {
		Object field_value;
		Field[] fields;
		
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
	}
	
	private Element createFieldElement(Object field_value, Field field) {
		Element field_element = new Element("field");
		
		field_element.setAttribute("name", field.getName());
		field_element.setAttribute("declaringclass", String.valueOf(field.getDeclaringClass().getSimpleName()));
		
		field_element.addContent(getObjectDataElement(field_value));
		
		return field_element;
	}
	
	private Element getObjectDataElement(Object obj) {
		Element object_data_ele;
		int object_id = System.identityHashCode(obj);
		
		if (isPrimitive(obj)) {
			object_data_ele = createValueElement(obj);
		} else {
			object_data_ele = createReferenceElement(object_id);
			if (!serialized_object_ids.get(object_id)) {
				referenced_objects.add(obj);
				serialized_object_ids.set(object_id);
			}
		}
		
		return object_data_ele;
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
		if (obj == null) {
			return true;
		}
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
    	//s.serialize(new Simple());
    	ReferenceGraph r = new ReferenceGraph();
    	//PrimitiveArray a = new PrimitiveArray();
    	GraphNode[] g;
    	GraphNode a = new GraphNode(0, true);
		GraphNode b = new GraphNode(1, false);
		GraphNode c = new GraphNode(2, true);
		
		ArrayList<GraphNode> al = new ArrayList<GraphNode>();
		al.add(a);
		al.add(b);
		al.add(c);
		
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		g = new GraphNode[] {a, b, c};
		//s.serialize(g);
		s.serialize(al);

    	//a.initializeArray(new int[] {1, 2, 3, 4 ,5});
    	//char[] c = new char[0];
    	
    	//referenced_objects_table.put(0, new Simple());
    	//referenced_objects_table.put(1, new char[] {'a', 'b'});
    	
    	//s.serialize(r.createDefaultGraph());
    	//s.serialize(new char[] {'a', 'b', 'c'});
    	//s.serialize("abcde");
    	//s.serialize(new char[0]);
    	
    	//System.out.println(String.valueOf(null));
    	
    }

}
