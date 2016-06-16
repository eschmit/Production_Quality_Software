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
 * directly modified. In order to update a Contact, you must remove the Contact from the Address Book
 * using one of the provided methods and subsequently add a new Contact.
 * <p>
 * Methods in the {@code AddressBook} class are not thread-safe.
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
   */
	
  public boolean addContact(Contact contact) { 
    /*Contact is immutable. Don't need to use Defensive Copy */
    if (contact == null) {
	  return false;	
    }
    //insert contact into AddressBook
    int index = getContactListInsertionIndex(contactsList, contact);
    if (index == -1) {
      //Contact already exists in addressBook	
      return false;	
    } else {
      contactsList.add(index, contact);
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
    List<Contact> matchingContacts = new ArrayList<Contact>();
    if (searchString.isEmpty() || searchString == null) {
      return matchingContacts;	
    }
    String lowerCaseSearchString = searchString.toLowerCase();
    char firstChar = searchString.charAt(0);
    String number;
    if (Contact.isOperand(firstChar) || firstChar == '+' || firstChar == '(') {
      number = Contact.parseStringToNumberString(searchString);    	
    } else {
      number = "";	
    }
    for (Contact contact: contactsList) {
      //if searchString is contained within property string insert contact into 
      //matchingContacts	
      if (!number.isEmpty()) {
        if (contact.privateGetPhoneNumber().contains(number)){
          addContactToMatchingContacts(matchingContacts, contact);
          continue;
        }  	
      }
      if (contact.getName().toLowerCase().contains(lowerCaseSearchString)) {
    	addContactToMatchingContacts(matchingContacts, contact);
        continue;
      }
      if (contact.getEmail().toLowerCase().contains(lowerCaseSearchString)){
    	addContactToMatchingContacts(matchingContacts, contact);
        continue;
      }
      if (contact.getPostalAddress().toLowerCase().contains(lowerCaseSearchString)){
    	addContactToMatchingContacts(matchingContacts, contact);
        continue;
      }
      if (contact.getNote().toLowerCase().contains(lowerCaseSearchString)) {
    	addContactToMatchingContacts(matchingContacts, contact);
        continue;    	  
      }
    }
    return matchingContacts;
  }
  
  /**
   * Find the index in the sorted list of contacts who match the search string where
   * the contact to be inserted belongs and insert the contact at that position.
   * @param matchingContacts list of contacts who match the search string.
   * @param contact contact to be added to the list of matching contacts.
   */
  
  private void addContactToMatchingContacts(List<Contact> matchingContacts,
      Contact contact) {
    int index = getContactListInsertionIndex(matchingContacts, contact);
    matchingContacts.add(index, contact);	  
  }
  
  /**
   * Return the index in the provided sorted list of contacts where the contact
   * to be inserted into the list belongs. 
   * <p>
   * If the contact already exists in the provided list of contacts return -1.
   * <p>
   * This method is used by both {@code addContactToMatchingContacts()} and 
   * {@code addContact()}
   * @param localContacts sorted list of contacts for the provided contact to be added to.
   * @param contact the contact to be inserted into the sorted list.
   * @return index where contact is to be inserted into the sorted list. -1 
   * if contact already exists in the list.
   */
  
  private int getContactListInsertionIndex(List<Contact> localContacts,
      Contact contact) {
    for(int i = 0; i < localContacts.size(); ++i){
      if (contact.equals(localContacts.get(i))) {
        return -1;	  
      }
      if(contact.compareTo(localContacts.get(i)) < 0) {
        return i;	  
      }
    }
    return localContacts.size();
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
   * @see removeContact
   */
  
  public boolean removeContact(Contact contact) {
    if (contact == null) {
      return false;	
    }  
    for (int i = 0; i < contactsList.size(); ++i) {
      if (contact.equals(contactsList.get(i))) {
        contactsList.remove(i);
        return true;
      }
    }
    return false;
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
   */
  
  public Contact removeContactAtIndex(int index) {
    if (contactsList.isEmpty()) {
      return null;	
    }
    Contact removedContact = contactsList.get(index);
    contactsList.remove(index);
    return removedContact;
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

