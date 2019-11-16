package app;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Driver {

	public static void main(String[] args) {
		Object selected_obj;
		ObjectCreator o = new ObjectCreator();
		selected_obj = o.createObject();
		Inspector i = new Inspector();
		
		Serializer s = new Serializer();
		Document d = s.serialize(selected_obj);
		Deserializer de = new Deserializer();
		
		i.inspect(de.deserialize(d), true);
	}

}
