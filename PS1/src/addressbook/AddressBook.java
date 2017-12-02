package addressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * The {@code AddressBook} class represents an address book that contains a sorted list of 
 * {@code Contact} objects, sorted by a Contact's name.
 * <p>
 * {@code AddressBook} uses an {@code ArrayList} to store the list of Contacts.
 * The {@code ArrayList} cannot be directly accessed but an unmodifiable view can be retrieved. 
 * <p>
 * The class {@code AddressBook} includes methods for adding a Contact to the address book,
 * removing a Contact from the address book, either by passing an index or the Contact object
 * itself, searching for a string of characters related to one or more Contacts in the address
 * book, saving the address book to a file and reading the address book from a file. 
 * <p>
 * {@code Contact} objects are immutable. Therefore, Contacts in the Address Book cannot be 
 * directly modified. In order to update a Contact, pass the Contact to the 
 * {@code Builder(Contact contact)} method.
 * <p>
 * Methods in the {@code AddressBook} class are not thread-safe.
 * @see Contact
 * @author Eric
 *
 */

public class AddressBook {
  private List<Contact> contactsList;
  
  public AddressBook() { 
    this.contactsList = new ArrayList<Contact>();
  }
  
  /**
   * Takes a {@code Contact} object to be added to the Address Book. 
   * If the Contact is null the method will return false. 
   * If the Contact already exists in the Address Book the method will return false.
   * When the Contact is successfully added he/she/it will be inserted in the Address Book's
   * {@code ArrayList} in sorted order by the Contact's name.  
   * <p>
   * {@code addContact} is not thread-safe.
   * @param contact the {@code Contact} object to be added to the Address Book.
   * @return true if the contact is successfully added; false otherwise.
   * @throws NullPointerException if {@code Contact} is null.
   */
	
  public boolean addContact(Contact contact) { 
    /*Contact is immutable. Don't need to use Defensive Copy */
    if (contact == null) {
      throw new NullPointerException("contact cannot be null");
    }
    if (contactsList.contains(contact)) {	
      return false;	
    } else {
      contactsList.add(contact);
      Collections.sort(contactsList);
      return true;
    }
  }
  
  /**
   * Search for a provided string of characters in each property field for 
   * all contacts in the Address Book. Return all contacts for which the provided
   * string is a substring of at least one of the property fields of {@code Contact}.
   * <p>
   * Searching for an empty string or null will return an empty list.
   * <p>
   * The search is not case-sensitive.
   * <p>
   * When searching a phone number, the first character must be a digit, 0-9, 
   * a '+' or a '('.
   * <p>
   * Spaces between two words must be provided where applicable. Searching 'EricS'
   * will not return 'Eric Schmitterer'.  
   * @param searchString the string or substring of characters to be searched for 
   * within each contact's set of property fields.
   * @return a list of contacts who match the search
   */
  
  public List<Contact> searchContactsList(String searchString) {
	if (searchString.isEmpty() || searchString == null) {
      return Collections.emptyList();	
    }
	List<Contact> matchingContacts = new ArrayList<Contact>();
    String lowerCaseSearchString = searchString.toLowerCase();
    char firstChar = searchString.charAt(0);
    String number;
    if (Character.isDigit(firstChar) || firstChar == '+' || firstChar == '(') {
      number = Contact.parseStringToNumberString(searchString);    	
    } else {
      number = "";	
    }
    for (Contact contact: contactsList) {
      //if searchString is contained within property string insert contact into 
      //matchingContacts	
      if (!number.isEmpty()) {
        if (contact.getPhoneNumber().contains(number)){
          matchingContacts.add(contact);
          continue;
        }  	
      }
      if (contact.getName().toLowerCase().contains(lowerCaseSearchString)) {
    	matchingContacts.add(contact);
        continue;
      }
      if (contact.getEmail().toLowerCase().contains(lowerCaseSearchString)){
    	matchingContacts.add(contact);
        continue;
      }
      if (contact.getPostalAddress().toLowerCase().contains(lowerCaseSearchString)){
    	matchingContacts.add(contact);
        continue;
      }
      if (contact.getNote().toLowerCase().contains(lowerCaseSearchString)) {
    	matchingContacts.add(contact);
        continue;    	  
      }
    }
    return matchingContacts;
  }
 
  /**
   * Removes the provided contact from the Address Book.
   * <p>
   * If contact is null, the method will return false.
   * <p>
   * This is one of two methods provided to remove a contact.
   * <p>
   * {@code removeContact} is not thread-safe.
   * @param contact
   * @return true if the contact is successfully removed; false otherwise.
   * @throws NullPointerException if {@code Contact} is null.
   * @see removeContact
   */
  
  public boolean removeContact(Contact contact) {
    if (contact == null) {
      throw new NullPointerException("contact cannot be null");	
    }  
    return contactsList.remove(contact);
  }
  
  /**
   * Removes contact from the Address Book at the provided index.
   * <p>
   * If the Address Book is empty the method will return null.
   * <p>
   * This is one of two methods provided to remove a contact.
   * <p>
   * {@code removeContactAtIndex} is not thread-safe.
   * @param index index in {@code ArrayList} where contact is to be removed.
   * @return the contact that was removed. null if the {@code ArrayList} is empty.
   * @throws IndexOutOfBoundsException if contacts list is empty.
   * @throws IllegalArgumentException if index is negative or out of range.
   */
  
  public Contact removeContactAtIndex(int index) {
    if (contactsList.isEmpty()) {
      throw new IndexOutOfBoundsException("List of contacts is empty");
    }
    if (index < 0) {
       throw new IllegalArgumentException();
    }
    return contactsList.remove(index);
  }
  
  /**
   * Saves the list of contacts in Address Book to a file.
   * <p>
   * Uses JSON format to store the list of contacts.
   * {@code saveAddressBookToFile} is not thread-safe.
   * @param filePath the absolute path where the list of contacts are to be saved.
   * @throws IOException if the method fails to save the file for any reason.
   * @throws FileNotFoundException if the specified pathname does not exist.
   */
  
  public void saveAddressBookToFile(String filePath) throws IOException, 
      FileNotFoundException {
    List<HashMap<String,String>> contactsArray = new JSONArray();
    JSONObject contacts = new JSONObject();
    for (Contact c: contactsList) {	
      HashMap<String,String> contact = new JSONObject();
      contact.put("name", c.getName());
      //can create both phone number representations from this version
      contact.put("number", c.getPhoneNumber());
      contact.put("email", c.getEmail());
      contact.put("address", c.getPostalAddress());
      contact.put("note", c.getNote());
      contactsArray.add(contact);
    }
    contacts.put("Contacts List", contactsArray);
	
    FileWriter file = new FileWriter(filePath);
    file.write(contacts.toJSONString());
    file.flush();
    file.close();
  }
  
  /**
   * Reads an Address Book of contacts from the provided file.
   * {@code readAddressBookFromFile} is not thread-safe.
   * @param filePath the absolute path where the list of contacts are to be read.
   * @throws IOException if the method fails to read the file for any reason.
   * @throws ParseException if the method fails to parse the data in the file.
   * @throws FileNotFoundException if the specified pathname does not exist.
   */
  
  public void readAddressBookFromFile(String filePath) throws IOException, ParseException,
      FileNotFoundException {
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(filePath));
    JSONObject contacts = (JSONObject) obj;
    JSONArray contactsArray = (JSONArray) contacts.get("Contacts List");
    Iterator<?> iterator = contactsArray.iterator();
    while (iterator.hasNext()) {
      JSONObject JSONContact = (JSONObject) iterator.next();
      String name = (String) JSONContact.get("name");
      String phoneNumber = (String) JSONContact.get("number");
      String email = (String) JSONContact.get("email");
      String address = (String) JSONContact.get("address");
      String note = (String) JSONContact.get("note");
      String delims = "[ ]+";
      String[] numberTokens = phoneNumber.split(delims);
      String[] addressTokens = address.split(delims);
      int addressLength = 6;
      int numberLength = 3;
      
      Contact contact;
      if (numberTokens.length == numberLength && addressTokens.length == addressLength) {
        contact = new Contact.Builder(name, numberTokens[0], numberTokens[1], numberTokens[2])
    	    .postalAddress(addressTokens[0], addressTokens[1], addressTokens[2], addressTokens[3],
    	    addressTokens[4], addressTokens[5]).emailAddress(email).note(note).build();  
      } else if (numberTokens.length == numberLength) {
        contact = new Contact.Builder(name, numberTokens[0], numberTokens[1], numberTokens[2])
            .emailAddress(email).note(note).build();  
      } else if (addressTokens.length == addressLength) {
        contact = new Contact.Builder(name, "1", "000", "0000000")
    	    .postalAddress(addressTokens[0], addressTokens[1], addressTokens[2], addressTokens[3],
    	    addressTokens[4], addressTokens[5]).emailAddress(email).note(note).build();   
      } else {
        contact = new Contact.Builder(name, "1", "000", "0000000")
            .emailAddress(email).note(note).build();  
      }	  
      addContact(contact); 
    }
        
  }
  
  /**
   * Builds a single string composed of each {@code toString} method for each 
   * contact in the Address Book.
   * @return a string that includes all the string representations for each 
   * contact in the Address Book.
   */
  
  @Override
  public String toString() {
    if (contactsList.isEmpty()) {
      return "";	
    }
    Contact first = contactsList.get(0);  
    int initialSize = (first.getName().length() + first.getEmail().length() + first.getPhoneNumber().length()
	    + first.getNote().length() + first.getPostalAddress().length())*contactsList.size();  
    StringBuilder contacts = new StringBuilder(initialSize);
    for (Contact contact: contactsList) {
      contacts.append(contact.toString());
    }
    return contacts.toString();
  }
  
  /**
   * Accessor method to get an unmodifiable list of the current contacts in the Address Book.
   * <p>
   * The returned list is an immutable view of the internal {@code ArrayList} that
   * contains the Address Book contacts. 
   * Attempting to modify the view that is returned will throw an 
   * UnsupportedOperationException.
   * <p>
   * In order to modify the internal {@code ArrayList} use {@code addContact} and
   * {@code removeContact} or {@code removeContactAtIndex}.
   * @return an unmodifiable list of contacts in the Address Book.
   */
  
  public final List<Contact> getUnmodifiableContactsList() {
    /*Return unmodifiable view */  
    return Collections.unmodifiableList(contactsList);
  }
  
}

