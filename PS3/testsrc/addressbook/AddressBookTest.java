package addressbook;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.containsString;

public class AddressBookTest {
  AddressBook addressbook;
  Contact contact;
  File temp;
  int contactsCount = 0;

  @Before
  public void setUp() {
    addressbook = new AddressBook();
    contact = new Contact.Builder().withName("Eric Schmitterer")
        .withPhoneNumber("9178889999")
        .withAddress("140 East 64 New York, New York")
        .withEmail("es3620@nyu.edu")
        .withNote("Made up contact").build();
  }

  @After
  public void tearDown() {
    if (temp != null) {
      temp.delete();
    }
  }

  @Test
  public void testAddContact() {
    assertTrue(addressbook.addContact(contact));
    contactsCount += 1;
    assertEquals(contactsCount, addressbook.getUnmodifiableContactsList().size());
  }

  @Test
  public void testRemoveContact() {
    addContact(contact);
    assertTrue(addressbook.removeContact(contact));
    contactsCount -= 1;
    assertEquals(contactsCount, addressbook.getUnmodifiableContactsList().size());
  }

  public void addContact(Contact contact) {
    assertTrue(addressbook.addContact(contact));
    contactsCount += 1;
  }

  @Test
  public void testAddressBook_isEmpty() {
    /* I added getUnmodifiableContactsList method to AddressBook*/
    List<Contact> emptyList = addressbook.getUnmodifiableContactsList();
    assertTrue(emptyList.isEmpty());
  }

  @Test
  public void testAddressBook_ContainsAddedItems() {
    Contact secondContact = buildContact("Chet", "9173334444", "", "", "");
    addContact(contact);
    addContact(secondContact);
    /* I added getUnmodifiableContactsList method to AddressBook*/
    assertThat(addressbook.getUnmodifiableContactsList(), hasItems(contact,secondContact));
  }
  
  public Contact buildContact(String name, String number, String address,
      String email, String note) {
    return new Contact.Builder().withName(name).withPhoneNumber(number)
        .withAddress(address).withEmail(email).withNote(note).build(); 
  }

  @Test(expected = NullPointerException.class)
  public void testSearch_nullSearchParam() {
    addressbook.addContact(contact);
    List<Contact> matches = addressbook.search(AddressBook.ContactAttribute.NAME, null);
    assertTrue(matches.isEmpty());
  }

  @Test
  public void testSearch_searchNullField() {
    Contact secondContact = new Contact.Builder().withName("Chet").build();
    addContact(secondContact);
    List<Contact> matches = addressbook.search(AddressBook.ContactAttribute.PHONE, "917");
    assertTrue(matches.isEmpty());
  }

  @Test
  public void testSearch_emptyAddressBook() {
    List<Contact> matches = addressbook.search(AddressBook.ContactAttribute.PHONE, "917");
    assertTrue(matches.isEmpty());
  }

  /* Perform search on each field */
  @Test
  public void testSearch_allFields() {
    Contact secondContact = buildContact("Chet", "9173334444", "", "", "");
    addContact(contact);
    addContact(secondContact);
    List<Contact> matches = addressbook.search(AddressBook.ContactAttribute.PHONE, "917");
    assertThat(matches, hasItems(contact,secondContact));
    matches = addressbook.search(AddressBook.ContactAttribute.NAME, "e");
    assertThat(matches, hasItems(contact,secondContact));
    matches = addressbook.search(AddressBook.ContactAttribute.NAME, "eric");
    assertThat(matches, hasItems(contact));
    matches = addressbook.search(AddressBook.ContactAttribute.EMAIL, "es");
    assertThat(matches, hasItems(contact));
    matches = addressbook.search(AddressBook.ContactAttribute.ADDRESS, "New York");
    assertThat(matches, hasItems(contact));
    matches = addressbook.search(AddressBook.ContactAttribute.NOTE, "made up");
    assertThat(matches, hasItems(contact));
    matches = addressbook.search(AddressBook.ContactAttribute.NAME, "brad");
    assertTrue(matches.isEmpty());
    matches = addressbook.search(AddressBook.ContactAttribute.PHONE, "347");
    assertTrue(matches.isEmpty());
    matches = addressbook.search(AddressBook.ContactAttribute.NOTE, "not in note");
    assertTrue(matches.isEmpty());
    matches = addressbook.search(AddressBook.ContactAttribute.EMAIL, "wrongemail");
    assertTrue(matches.isEmpty());
  }

  @Test
  public void testSave_emptyAddressBook() {
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
    } catch (FileNotFoundException aFileNotFoundException) {
      fail("Did not expect a FileNotFoundException to be thrown");
    } catch (IOException e) {
      fail("Did not expect an IOException to be thrown");
    }
  }

  public void createTempFile() {
    try {
      temp = File.createTempFile("temp-file", ".tmp");
    } catch (IOException e) {
      fail("Did not expect an IOException to be thrown");
    }
  }

  /* Method fails because it does not test for NPE */
  @Test
  public void testSave_nullSearchParam() {
    addContact(contact);
    try {
      addressbook.save(null);
      fail("Expected a NullPointerException to be thrown");
    } catch (FileNotFoundException e) {
      fail("Expected a NullPointerException to be thrown");
    } catch (IOException e) {
      fail("Expected a NullPointerException to be thrown");
    }catch (NullPointerException aNullPointerException){
      assertThat(aNullPointerException.getMessage(), containsString("null"));
    }
  }

  @Test
  public void testSave_FileNotFound() {
    addContact(contact);
    try {
      addressbook.save("/made/up/file/path");
      fail("Expected a FileNotFoundException to be thrown");
    } catch (FileNotFoundException aFileNotFoundException) {
      assertThat(aFileNotFoundException.getMessage(),
          containsString("/made/up/file/path (No such file or directory)"));
    } catch (IOException e) {
      fail("Expected a FileNotFoundException to be thrown");	
    } 
  }

  @Test
  public void testSave_ToTempFile() {
    addContact(contact);
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
    } catch (FileNotFoundException aFileNotFoundException) {
      fail("Did not expect a FileNotFoundException to be thrown");
    } catch (IOException e) {
      fail("Did not expect an IOException to be thrown");
    }
  }

  @Test
  public void testAddressBook_readEmptyFile() {
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
      AddressBook recoveredAddressBook = new AddressBook(temp.getAbsolutePath());
      assertTrue(recoveredAddressBook.getUnmodifiableContactsList().isEmpty());
    } catch (FileNotFoundException aFileNotFoundException) {
      fail("Did not expect a FileNotFoundException to be thrown");
    } catch (IOException e) {
      fail("Did not expect an IOException to be thrown");
    }
  }

  @Test
  public void testAddressBook_FromFilePath() {
    Contact secondContact = buildContact("Chet", "9173334444", "", "", "");
    addContact(contact);
    addContact(secondContact);
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
      AddressBook recoveredAddressBook = new AddressBook(temp.getAbsolutePath());
      assertEquals(addressbook.toString(), recoveredAddressBook.toString());
    } catch (FileNotFoundException aFileNotFoundException) {
      fail("Did not expect a FileNotFoundException to be thrown");
    } catch (IOException e) {
      fail("Did not expect an IOException to be thrown");
    }
  }

  /* Method fails because it does not test for NPE */
  @Test
  public void testAddressBook_withNullPath() {
    addContact(contact);
    addContact(buildContact("Chet", "9173334444", "", "", ""));
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
      new AddressBook(null);
      fail("Expected a NullPointerException to be thrown");
    } catch (FileNotFoundException aFileNotFoundException) {
      fail("Expected a NullPointerException to be thrown");
    } catch (IOException e) {
      fail("Expected a NullPointerException to be thrown");
    }catch (NullPointerException aNullPointerException){
      assertThat(aNullPointerException.getMessage(), containsString("null"));
    }
  }

  @Test
  public void testAddressBook_FileNotFound() {
    addContact(contact);
    addContact(buildContact("Chet", "9173334444", "", "", ""));
    try {
      createTempFile();
      addressbook.save(temp.getAbsolutePath());
      new AddressBook("/made/up/file/path");
      fail("Expected a FileNotFoundException to be thrown");
    } catch (FileNotFoundException aFileNotFoundException) {
      assertThat(aFileNotFoundException.getMessage(),
          containsString("/made/up/file/path (No such file or directory)"));
    } catch (IOException e) {
      fail("Expected a FileNotFoundException to be thrown");
    }
  }

  @Test
  public void testToString_emptyString() {
    assertEquals("", addressbook.toString());
  }

  @Test
  public void testToString() {
    StringBuilder contactsBuilder = new StringBuilder();
    addContact(contact);
    contactsBuilder.append(contact.toString());
    contactsBuilder.append("\n");
    assertEquals(contactsBuilder.toString(), addressbook.toString());
    Contact secondContact = buildContact("Chet", "9173334444", "", "", "");
    addContact(secondContact);
    contactsBuilder.append(secondContact.toString());
    contactsBuilder.append("\n");
    assertEquals(contactsBuilder.toString(), addressbook.toString());
  }
}
