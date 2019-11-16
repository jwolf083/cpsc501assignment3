package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Deserializer {
		
	public Object deserialize(Document document) {
		Map<Integer, Object> objects = new HashMap<Integer, Object>();
		List<Element> elements = document.getRootElement().getChildren();
		Object root_obj;
 		
		instantiateObjects(elements, objects);
		initObjectData(elements, objects);
		
		root_obj = objects.get(Integer.parseInt(elements.get(0).getAttributeValue("id")));
		return root_obj;
	}
	
	private void initObjectData(List<Element> elements, Map<Integer, Object> objects) {
		Class<?> object_class;
		Object object;
		
		for (Element e: elements) {
			object = objects.get(Integer.parseInt(e.getAttributeValue("id")));
			object_class = object.getClass();
			
			if (object_class.isArray()) {
				initArrayElements(object, e.getChildren(), objects);
			} else {
				initObjectFields(object, e.getChildren(), objects);
			}
		}
	}
	
	private void initArrayElements(Object object, List<Element> arr_elements, Map<Integer, Object> objects) {
		Class<?> component_type = object.getClass().getComponentType();
		
		for (int i = 0; i < arr_elements.size(); i+=1) {
			Array.set(object, i,  getValue(arr_elements.get(i), component_type, objects));
		}
	}
	
	private void initObjectFields(Object object, List<Element> field_elements, Map<Integer, Object> objects) {
		Object field_value;
		
		for (Element field_element: field_elements) {
			try {
				Class<?> field_class = Class.forName(
						field_element.getAttributeValue("declaringclass"));
				Field field = field_class.getDeclaredField(field_element.getAttributeValue("name"));
				field.setAccessible(true);
				field_value = getValue(field_element.getChildren().get(0), field.getType(), objects);
				field.set(object, field_value);
			} catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	private Object getValue(Element value_element, Class<?> value_type, Map<Integer, Object> objects) {
		String string_value = value_element.getText();
		
		if (string_value.equals("null")) {
			return null;
		} else {
			if (value_element.getName().equals("reference")) {
				return objects.get(Integer.parseInt(string_value));
			} else {
				return strToPrimConverter(string_value, value_type);
			}
		}
	}
	
	private Object strToPrimConverter(String value, Class<?> value_type) {
		Map<Class<?>, Object> state_map = new HashMap<Class<?>, Object>();
		
		if (value_type.equals(boolean.class)) {
			if (value.equals("true"))
				return Boolean.TRUE;
			else
				return Boolean.FALSE;
		} else {
			state_map.put(int.class, new Integer(value));
			state_map.put(byte.class, new Byte(value));
			state_map.put(short.class, new Short(value));
			state_map.put(long.class, new Long(value));
			state_map.put(double.class, new Double(value));
			state_map.put(float.class, new Float(value));
			state_map.put(char.class, new Character(value.charAt(0)));
			
		}
		
		return state_map.get(value_type);	
	}
	
	private void instantiateObjects(List<Element> elements, Map<Integer, Object> objects) {
		Class<?> object_class;
		Object instance = null;
		int length;
		
		for (Element e: elements) {
			try {
				object_class = Class.forName(e.getAttributeValue("class"));
				if (object_class.isArray()) {
					length = Integer.parseInt(e.getAttributeValue("length"));
					instance = createArrayInstance(object_class, length);
				} else {
					instance = createClassInstance(object_class);
				}
				objects.put(Integer.parseInt(e.getAttributeValue("id")), instance);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private Object createArrayInstance(Class<?> _class, int length) throws Exception {
		return Array.newInstance(_class.getComponentType(), length);
	}
	
	private Object createClassInstance(Class<?> _class) throws Exception {
		Object class_instance = null;
		Constructor<?> constructor = _class.getDeclaredConstructor();
		 
		constructor.setAccessible(true);
		class_instance = constructor.newInstance();
		
		return class_instance; 
	}
	
	
	
}
