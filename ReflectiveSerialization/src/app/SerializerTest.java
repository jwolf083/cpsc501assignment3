package app;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.jupiter.api.Test;

class SerializerTest {
	Serializer s = new Serializer();
	Deserializer de = new Deserializer();
	//s.serialize(new Simple());
	//PrimitiveArray a = new PrimitiveArray();
	GraphNode[] g;
	GraphNode a = new GraphNode(0, true);
	GraphNode b = new GraphNode(1, false);
	GraphNode c = new GraphNode(2, true);
	
	ArrayList<GraphNode> al = new ArrayList<GraphNode>();
	
	//s.serialize(g);
	//Document d = s.serialize(a);
	
	//d = new Document()
	
	static Field[] getAllFields(Class<?> obj_class) {
		ArrayList<Field> all_fields = new ArrayList<Field>();
		Field[] fields;
		Field[] all_fields_arr;
		
		if (obj_class == Object.class || obj_class == null) {
			return new Field[0];
		} else {
			appendToArrayList(all_fields, getAllFields(obj_class.getSuperclass()));
			
			fields = obj_class.getDeclaredFields();
			for (Field f: fields) {
				if (!Modifier.isStatic(f.getModifiers())) {
					all_fields.add(f);
				}
			}
			
			all_fields.trimToSize();
			all_fields_arr = new Field[all_fields.size()];
			all_fields.toArray(all_fields_arr);
		}
		
		return all_fields_arr;
	}
	
	static void appendToArrayList(ArrayList<Field> arr_list, Field[] arr) {
		for (Field f: arr) {
			arr_list.add(f);
		}
	}
	
	/*static boolean fields_equal(Object a, Object b) {
		Field[] a_fields = getAllFields(a.getClass());
		Field[] b_fields = getAllFields(b.getClass());
		
		if (a_fields.length != b_fields.length) {
			return false;
		} else {
			for (Field f: a_fields) {
				if (f.getClass().)
			}
		}
	}
	*/
	
	//i.inspect(de.deserialize(d), false);

	//a.initializeArray(new int[] {1, 2, 3, 4 ,5});
	//char[] c = new char[0];
	
	//s.serialize(r.createDefaultGraph());
	//s.serialize(new char[] {'a', 'b', 'c'});
	//s.serialize("abcde");
	//s.serialize(new char[0]);
	@Before
	void setUp() {
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		
		al.add(a);
		al.add(b);
		al.add(c);
		
		g = new GraphNode[] {a, b, c};
		
	}
	@Test
	void testSimple() {
		a.setNext(b);
		b.setNext(c);
		c.setNext(a);
		Inspector i = new Inspector();
		
		
		Object expected = a;
		i.inspect(expected, true);
		System.out.println("\n\n\n");
		Object actual = de.deserialize(s.serialize(a));
		i.inspect(actual, true);
		assert(true);
	}

}
