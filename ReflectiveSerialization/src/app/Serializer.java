package app;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Serializer {
	public static void serialize(Object obj) {
		Element root = new Element("serialized");
		Document document = new Document(root);
		Element object_element = new Element("object");
		Class<?> object_class = obj.getClass();
		
		object_element.setAttribute("class", object_class.getSimpleName());
		object_element.setAttribute("id", String.valueOf(System.identityHashCode(obj)));
		
	}

}
