package addressbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Contact contact1 = new Contact.Builder("Eric", "1", "917", "8030684").build();
		Contact contact2 = new Contact.Builder("Brad", "1", "917", "8030684").build();
		AddressBook addressbook = new AddressBook();
		addressbook.addContact(contact1);
		
		try {
			addressbook.saveAddressBookToFile("/Users/Eric/PQS/PS1_Contacts.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
		} catch (NullPointerException e){
			System.out.println(e.getMessage());
		}
		
		/*
		List<Contact> view = addressbook.getUnmodifiableContactsList();
		
		System.out.println(view.size());
		
		addressbook.addContact(contact2);
		
		System.out.println(view.size());
		*/
	}

}
