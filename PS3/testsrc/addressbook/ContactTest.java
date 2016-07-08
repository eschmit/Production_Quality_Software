package addressbook;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContactTest {
  Contact contact;

  @Before
  public void setUp() {
    contact = buildContact("Eric Schmitterer", "9173334444",
        "140 East 64 New York, New York", "es3620@nyu.edu",
        "Made up contact");
  }

  @Test
  public void testGetName() {
    assertEquals(new MatchableString("Eric Schmitterer").toString(),
        contact.getName());
  }

  @Test(expected = NullPointerException.class)
  public void testGetName_Null() {
    Contact newContact = new Contact.Builder().withName(null).build();
    newContact.getName().toString();
  }

  @Test
  public void testGetEmailAddress() {
    assertEquals(new MatchableString("es3620@nyu.edu").toString(),
        contact.getEmailAddress());
  }

  @Test
  public void testGetAddress() {
    assertEquals(new MatchableString("140 East 64 New York, New York").toString(),
        contact.getAddress());
  }

  @Test
  public void testGetNote() {
    assertEquals(new MatchableString("Made up contact").toString(), contact.getNote());
  }

  @Test
  public void testGetPhoneNumber() {
    assertEquals(new PhoneNumber("9173334444").toString(), contact.getPhoneNumber());
  }

  /* Fails because Contact toString method uses MatchableString not MatchableString.toString()
   * See getter methods */
  @Test
  public void testToString() {
    StringBuilder contactInfo = new StringBuilder();
    String name = "Chet";
    String number = null;
    String address = null;
    String email = null;
    String note = null;
    contactInfo.append(name);
    contactInfo.append("\n");
    assertEquals(contactInfo.toString(), buildContact(name, number, address, email, note)
        .toString());
    email = "madeUp@nyu.edu";
    contactInfo.append(email);
    contactInfo.append("\n");
    assertEquals(contactInfo.toString(), buildContact(name, number, address, email, note)
        .toString());
    number = "9173334444";
    contactInfo.append(number);
    contactInfo.append("\n");
    assertEquals(contactInfo.toString(),buildContact(name, number, address, email, note)
        .toString());
    address = "made up address";
    contactInfo.append(address);
    contactInfo.append("\n");
    assertEquals(contactInfo.toString(), buildContact(name, number, address, email, note)
        .toString());
    note = "some note";
    contactInfo.append(note);
    contactInfo.append("\n");
    assertEquals(contactInfo.toString(),buildContact(name, number, address, email, note)
        .toString());
  }

  public Contact buildContact(String name, String number, String address,
      String email, String note) {
    return new Contact.Builder().withName(name).withPhoneNumber(number)
        .withAddress(address).withEmail(email).withNote(note).build(); 
  }
  
  /* Fails because builder methods don't check for null */
  @Test(expected = NullPointerException.class) 
  public void testBuilder_nullParams() {
    buildContact(null, null, null, null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithName_nameReassignment() {
    new Contact.Builder().withName("Eric").withPhoneNumber("9173334444")
        .withName("Chet").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithName_phoneReassignment() {
    new Contact.Builder().withName("Eric").withPhoneNumber("9173334444").
    withPhoneNumber("3473334444").build();	
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithName_addressReassignment() {
    new Contact.Builder().withName("Eric").withPhoneNumber("9173334444")
        .withAddress("140 East 64 New York, New York")
        .withAddress("148 East 64 New York, New York").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithName_emailReassignment() {
    new Contact.Builder().withName("Eric").withPhoneNumber("9173334444")
        .withEmail("es3620@nyu.edu").withEmail("madeUp@nyu.edu").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithName_noteReassignment() {
    new Contact.Builder().withName("Eric").withPhoneNumber("9173334444")
        .withNote("Made up contact").withNote("Second Note").build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void  testContact_emptyContact() {
    new Contact.Builder().build();
  }

  @Test
  public void testContact_notSameContact() {
    Contact contactCopy = buildContact("Eric Schmitterer", "9173334444", 
        "140 East 64 New York, New York", "es3620@nyu.edu", "Made up contact");
    assertNotSame("should not be same Contact", contact, contactCopy);
  }

  @Test
  public void testContact_sameContact() {
    Contact contactAlias = contact;
    assertSame("should be same", contact, contactAlias);
  }

  @Test(expected = NullPointerException.class)
  public void testMatch_nullParam() {
    boolean found = contact.match(AddressBook.ContactAttribute.NAME, null);
    assertFalse(found);
  }

  @Test
  public void testMatch_NullField() {
    Contact secondContact = buildContact("Chet", "", "", "", "");
    boolean found = secondContact.match(AddressBook.ContactAttribute
        .PHONE, "917");
    assertFalse(found);
  }

  @Test
  public void testMatch() {
    boolean found = contact.match(AddressBook.ContactAttribute.NAME, "Eric");
    assertTrue(found);
    found = contact.match(AddressBook.ContactAttribute.NAME, "Brad");
    assertFalse(found);
    found = contact.match(AddressBook.ContactAttribute.PHONE, "917");
    assertTrue(found);
    found = contact.match(AddressBook.ContactAttribute.PHONE, "347");
    assertFalse(found);
    found = contact.match(AddressBook.ContactAttribute.ADDRESS, "64");
    assertTrue(found);
    found = contact.match(AddressBook.ContactAttribute.ADDRESS, "NYC");
    assertFalse(found);
    found = contact.match(AddressBook.ContactAttribute.EMAIL, "Es");
    assertTrue(found);
    found = contact.match(AddressBook.ContactAttribute.EMAIL, "ch");
    assertFalse(found);
    found = contact.match(AddressBook.ContactAttribute.NOTE, "Made up");
    assertTrue(found);
    found = contact.match(AddressBook.ContactAttribute.NOTE, "not");
    assertFalse(found);
  }
}
