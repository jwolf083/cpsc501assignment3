package app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

public class ObjectCreator {
	public Object createObject() {
		Scanner keyboard = new Scanner(System.in);
		Inspector inspector = new Inspector();
		Object selected_object = selectObject(keyboard);
		if (selected_object != null) {
			editObject(selected_object, keyboard, inspector); 
		}
		return selected_object;
		
	}
	public Object selectObject(Scanner keyboard) {
		String choice = null;
		Object chosen_object = null;
		do {
			System.out.println("\n\n(1) Simple Object\n"
					+ "(2) Object with reference to other object\n"
					+ "(3) Object with array of primitives\n"
					+ "(4) Object with array of object references\n"
					+ "(5) Object with java collection\n"
					+ "\nPlease enter the number of the object to edit & serialize or enter 'q' to quit: ");
			choice = keyboard.nextLine();
			switch(choice) {
				case "1": 
					chosen_object = new Simple();
					break;
				case "2": 
					chosen_object = new ReferenceGraph().createDefaultGraph();
					break;
				case "3":
					PrimitiveArray p = new PrimitiveArray();
					p.initializeArray(new int[] {0, 1, 2, 3, 4, 5});
					chosen_object = p;
					break;
				case "4": 
					GraphNode[] g;
			    	GraphNode a = new GraphNode(0, true);
					GraphNode b = new GraphNode(1, false);
					GraphNode c = new GraphNode(2, true);
					a.setNext(b);
					b.setNext(c);
					c.setNext(a);
					g = new GraphNode[] {a, b, c};
					ReferenceArray ra = new ReferenceArray();
					ra.initializeArray(g);
					chosen_object = ra;
					break;
				case "5": 
					ArrayList<GraphNode> al = new ArrayList<GraphNode>();
					GraphNode a2 = new GraphNode(0, true);
					GraphNode b2 = new GraphNode(1, false);
					GraphNode c2 = new GraphNode(2, true);
					a2.setNext(b2);
					b2.setNext(c2);
					c2.setNext(a2);
					al.add(a2);
					al.add(b2);
					al.add(c2);
					chosen_object = al;
					break;
				case "q":
					break;
				default:
					System.out.println("Sorry I didn't understand that, please enter your choice without brackets");
					choice = null;
			}
		} while (choice == null);
		
		return chosen_object;
	}
	
	private void editObject(Object obj, Scanner keyboard, Inspector inspector) {
		String choice = null;
		Field selected_field = null;
		
		do {
			System.out.println("Your selected objects fields/vals:\n");
			inspector.inspectFields(obj, false);
			System.out.println("Would you like to edit its primitive fields?: (y/n)");
			choice = keyboard.nextLine();
			
			if (choice.equals("y")) {
				selected_field = selectField(obj, inspector, keyboard);
				selected_field.setAccessible(true);
				if (!(selected_field.getType().equals(boolean.class) 
						|| selected_field.getType().equals(int.class))) {
					System.out.println("Please select a field with primitive type");
					choice = null;
				} else {
					setNewValue(obj, keyboard, selected_field);
					choice = null;
				}
			} else if (!choice.equals("n")) {
				System.out.println("Sorry I didn't understand that, please enter your choice without brackets");
				choice = null;
			}
			
		} while (choice == null);
	}
	
	private void setNewValue(Object obj, Scanner keyboard, Field field) {
		String new_value = null;
		Object field_class = null;
		int int_value;
		boolean wrong_input = false;
		
		try {
			field_class = field.get(obj).getClass();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		do {
			System.out.println("Enter new value of '" + field + "':");
			new_value = keyboard.nextLine();
			
			if (field_class == Boolean.class) {
				new_value = new_value.toLowerCase();
				if (new_value.equals("true") || new_value.equals("false")) {
					try {
						field.set(obj, Boolean.valueOf(new_value));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					wrong_input = false;
				} else {
					System.out.println("Selected field has type boolean, please enter true/false");
					wrong_input = true;
				}
			} else if (field_class == Integer.class) {
				try {
					int_value = Integer.parseInt(new_value);
					field.set(obj, int_value);
					wrong_input = false;
				} catch (NumberFormatException e) {
					System.out.println("Selected field has type integer, please enter an integer");
					wrong_input = true;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (wrong_input); 
	}
	
	private Field selectField(Object obj, Inspector inspector, Scanner keyboard) {
		Class<?> object_class = obj.getClass();
		Field selected_field = null;
		String choice = null;
		
		do {
			System.out.println("These are the fields for this object: ");
			inspector.inspectFields(obj, false);
			System.out.println("Enter the name of the field you would like to edit: ");
			
			choice = keyboard.nextLine();
			try {
				selected_field = object_class.getDeclaredField(choice);
				System.out.println("Found Field " + selected_field);
			} catch (NoSuchFieldException e) {
				System.out.println("I was unable to find a field with name '" + choice + "' " + " please try again");
			}
			
		} while (selected_field == null);
		
		return selected_field;
	}
	
	public static void main(String[] args) {
		ObjectCreator o = new ObjectCreator();
		//o.createObject();
		Inspector inspector = new Inspector();
		inspector.inspect(o.createObject(), false);
	}
}
