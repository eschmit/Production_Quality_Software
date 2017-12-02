package addressbook;


/**
 * The {@code Contact} class represents a contact that would be added to an address book. 
 * Contacts are immutable; their values cannot be changed after they are created.
 * Objects of this class are inherently thread-safe.
 * <p>
 * {@code Contact} objects are the building block for the {@code AddressBook} class.
 * However, {@code Contact} objects are also available to be used independently of the 
 * {@code AddressBook} class. 
 * <p>
 * The {@code Contact} class is final and is not available to be sub-classed.
 * <p>
 * The class {@code Contact} includes methods for determining equality, comparing two Contact
 * objects, and returning Contact properties, including name, email, phone number, postal address,
 * and a note about the Contact.
 * @author Eric
 * @see AddressBook
 * 
 */

public final class Contact implements Comparable<Contact> {

  private final String name;
  private final String email;
  private final String note;
  private final PhoneNumber phoneNumber;
  private final PostalAddress postalAddress;
  
  /**
   * The {@code Builder} class is a static class that uses the Builder pattern to create a 
   * {@code Contact} object.  		
   * @author Eric
   *
   */
  
  public static class Builder {
    //Required parameters
    private final String name;
    private final String countryCode;
    private final String areaCode;
    private final String subscriberNumber;
			
    //Optional parameters - initialized to default values. Non-final
    private String email = ""; 
    private String number = "";
    private String street = "";
    private String city = "";
    private String state = "";
    private String zipcode = "";
    private String country = "";
    private String note = "";
    
    /** 
     * Required method when using the Builder class to create a Contact object.
     * Each parameter to this method is a required property of Contact objects.
     * If null or an empty String is passed to the parameter field it will be replaced
     * by a default value. 1 for countryCode, 000 for areaCode, and 0000000 for subscriberNumber.
     * @param name the contact's name
     * @param countryCode the country code for the telephone number
     * @param areaCode the area code for the telephone number
     * @param subscriberNumber the remaining numbers in the telephone number
     */
    
    public Builder(String name, String countryCode, String areaCode,
        String subscriberNumber) {
      this.name = ((name == null) ? "" : name);
      this.countryCode = ((countryCode == null || countryCode == "" || 
    		  countryCode.trim().isEmpty() ) ? "1" : countryCode);
      this.areaCode = ((areaCode == null || areaCode == "" || 
    		  areaCode.trim().isEmpty() ) ? "000" : areaCode);
      this.subscriberNumber = ((subscriberNumber == null || subscriberNumber == "" || 
    		  subscriberNumber.trim().isEmpty()) ? "0000000" : subscriberNumber);
    }
    
//    private boolean stringIsWhiteSpace(String stringToCheck) {
//      for (char c: stringToCheck.toCharArray()) {
//    	if (c == ' ') {
//    	  return true;	  
//    	}
//       }
//      return false;
//    }
	
    /**
     * Optional method for creating a Contact object. If method not called, or parameter passed
     * is null, empty or blank String, the email field will be set to "email".
     * @param email the email address of the given contact
     * @return the Builder object used to create the contact
     */
    
    public Builder emailAddress(String email) { 
      this.email = ((email == null || email.trim().isEmpty())
          ? "" : email);
      return this;  
    }
    
    /**
     * Optional method for creating a Contact object. If method not called, or parameter passed
     * is null, empty or blank String, the applicable field will be set to an empty string. 
     * @param number the building number
     * @param street the street name including direction (e.g. East), and apartment/suite/etc.
     * @param city the name of the city
     * @param state the name of the state 
     * @param zipcode the applicable zipcode with or without optional digits
     * @param country the name of the country
     * @return the Builder object used to create the contact
     */
    
    public Builder postalAddress(String number, String street, String city,
        String state, String zipcode, String country) {
      this.number = ((number == null || 
    		  number.trim().isEmpty()) ? "" : number);
      this.street = ((street == null || 
    		  street.trim().isEmpty()) ? "" : street);
      this.city = ((city == null || 
    		  city.trim().isEmpty()) ? "" : city);
      this.state = ((state == null || 
    		  state.trim().isEmpty()) ? "" : state);
      this.zipcode = ((zipcode == null || 
    		  zipcode.trim().isEmpty()) ? "" : zipcode);
      this.country = ((country == null || 
    		  country.trim().isEmpty()) ? "" : country);
      return this;
    }
    
    /**
     * Optional method for creating a Contact object. If method not called, 
     * or parameter passed is null, empty or blank String, the note field will 
     * be set to "note"
     * @param note any additional note related to the contact
     * @return the Builder object used to create the contact
     */
    
    public Builder note(String note) {
      this.note = ((note == null || note.trim().isEmpty())
    	  ? "" : note);
      return this;    	
    }
    
    /**
     * Convenience Builder provided for "modifying" a contact. Builder copies
     * current Contact fields to a new Contact object. Use in conjunction with 
     * {@code withEmail}, {@code withPostalAddress} and {@code withNote} to modify
     * non-final fields.
     * @param contact the Contact object to be modified
     */
    
    public Builder(Contact contact) {
      this.name = contact.getName();
      String delims = "[ ]+";
      String[] numberTokens = contact.getPhoneNumber().split(delims);
      if (numberTokens.length == 3) {
        this.countryCode = numberTokens[0];
        this.areaCode = numberTokens[1];
        this.subscriberNumber = numberTokens[2];
      } else {
        this.countryCode = "1";
        this.areaCode = "000";
        this.subscriberNumber = "0000000";
      }
      this.email = contact.getEmail();
      this.note = contact.getNote();
    }

    /**
     * Required method when using the Builder class to create a Contact object.
     * This method is called after the Builder method and any optional methods called.
     * @return the Contact object to be created.
     */
			
    public Contact build() {
      return new Contact(this);
    }
  }
		
  private Contact(Builder builder) {
    name = builder.name;
    email = builder.email;
    phoneNumber = new PhoneNumber(builder.countryCode, builder.areaCode, 
        builder.subscriberNumber);		
    postalAddress = new PostalAddress(builder.number, builder.street, 
        builder.city, builder.state, builder.zipcode, builder.country);
    note = builder.note;		
  }
  
  /**
   * Compare this Contact object with the specified Contact object for order. 
   * The comparison is based on the value of each Contact's name. {@code compareTo} uses the 
   * {@code String} classes compareTo method which compares two strings lexicographically. 
   * Character case is taken into account. 
   * @param the Contact object to be compared with this one
   * @return  negative integer, zero, or a positive integer as this object is less than, equal to, 
   * or greater than the specified object.
   * @see String
   */
		
  @Override
  public int compareTo(Contact contact) {
	 if (getPhoneNumber().equals(contact.getPhoneNumber())) {
		 if (name.equalsIgnoreCase(contact.getName())) {
			 if (email.equalsIgnoreCase(contact.getEmail())) {
			      if (getPostalAddress().equalsIgnoreCase(contact.getPostalAddress())) {
			    	  return 0;
			      } else {
			    	  return getPostalAddress().compareToIgnoreCase(contact.getPostalAddress());
			      }
			 } else {
				 return email.compareToIgnoreCase(contact.getEmail());
			 }
		 } else {
			 return name.compareToIgnoreCase(contact.getName());
		 }
	 } else {
		 return getPhoneNumber().compareTo(contact.getPhoneNumber());
	 }
  }
  
  /**
   * Indicates whether another Contact object "equals" this one. Two objects are considered equal
   * if they reference the same object or if all Contact fields are equal. 
   * Character case is ignored when comparing name, email, and postal address for equality
   * <p><b>Note:</b> {@code hashCode} overridden to be consistent with {@code equals} method
   * @param the Contact object to be compared for equality with this one
   * @return true if this object is the same as the Contact argument; false otherwise
   * @see hashCode
   */
  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;	
    }
    if (!(o instanceof Contact)) {
      return false;
    }
    Contact contact = (Contact)o;
    return getPhoneNumber().equals(contact.getPhoneNumber()) && 
    	name.equalsIgnoreCase(contact.name) && email.equalsIgnoreCase(contact.email) 
    	&& getPostalAddress().equalsIgnoreCase(contact.getPostalAddress());
  }
  
  /**
   * Returns a hash code value for the Contact object. The hash code value is calculated
   * using all Contact fields
   * @return a hash code value for this object
   * @see equals
   */
	
  @Override
  public int hashCode() {
      int result = 17;
      result = ((name.isEmpty()) ? result : 31 * result + name.toLowerCase().hashCode());
      result = ((email.isEmpty()) ? result : 31 * result + email.toLowerCase().hashCode());
      result = ((getPhoneNumber().isEmpty()) ? result : 31 * result + getPhoneNumber().hashCode());
      String address = getPostalAddress();
      result = ((address.isEmpty()) ? result : 31 * result + address.toLowerCase().hashCode()); 
      return result;
  }
  
  /** 
   * Provides a string representation of the Contact object that includes all 
   * Contact fields, separated by a newline character, including after the final field.
   * @return a string representation of all Contact fields
   */
	
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (!name.isEmpty()) {
    	stringBuilder.append(name + "\n");
    }
    if (!getPhoneNumber().isEmpty()) {
    	stringBuilder.append(getPhoneNumber() + "\n");
    }
    if (!email.isEmpty()) {
    	stringBuilder.append(email + "\n");
    }
    if (!getPostalAddress().isEmpty()) {
    	stringBuilder.append(getPostalAddress() + "\n");
    }
    if (!note.isEmpty()) {
    	stringBuilder.append(note + "\n");
    }
    return stringBuilder.toString();

  }
  
  /**
   * Returns the Contact's phone number. The phone number is represented by 
   * three fields: country code, area code, and subscriber number. 
   * The fields are separated by a single space.
   * @return the Contact's phone number with spaces between fields.
   */
  
  public String getPhoneNumber() {
    return phoneNumber.toString();
  }
  
  public String getName() {
    /* String immutable. Don't need Defensive Copy */  
    return name;
  }
  
  public String getEmail() {
    /* String immutable. Don't need Defensive Copy */ 
    return email;
  }
  
  public String getNote() {
    /* String immutable. Don't need Defensive Copy */ 
    return note;	  
  }
  
  /**
   * Returns the Contact's postal address. The postal address is represented by 
   * six fields: building number, street, city, state, zipcode and country. 
   * The fields are separated by a single space.
   * @return the Contact's postal address
   */
  
  public String getPostalAddress() {
    /* String immutable. Don't need Defensive Copy */   
    return postalAddress.toString();
  }
  
  /**
   * The {@code PostalAddress} class is a static class that represents a Contact's postal address.
   * <p>
   * {@code PostalAddress} is in effect a top level class that has been nested for 
   * encapsulation purposes and is defined static because it has no need to reference 
   * instance variables or methods belonging to Contact
   * @author Eric
   *
   */
  
  private static class PostalAddress {
    private String address;
    
    private PostalAddress(String number, String street, String city,
        String state, String zipcode, String country) {
      address = buildAddress(number, street, city, state, zipcode, country);
    }
    
    private String buildAddress(String number, String street, String city,
        String state, String zipcode, String country) {
      int spaces = 4;
      int length = number.length() + street.length() + city.length() + state.length()
          + zipcode.length() + country.length() + spaces;
      StringBuilder addressBuilder = new StringBuilder(length);
      addressBuilder.append(number);
      addressBuilder.append(" ");
      addressBuilder.append(street);
      addressBuilder.append(" ");
      addressBuilder.append(city);
      addressBuilder.append(" ");
      addressBuilder.append(state);
      addressBuilder.append(" ");
      addressBuilder.append(country);
      return addressBuilder.toString();
    }
    
    /**
     * Returns the Contact's postal address. The postal address is represented by 
     * six fields: building number, street, city, state, zipcode and country. 
     * The fields are separated by a single space.
     * @return the Contact's postal address
     */
    
    @Override
    public String toString() { 
      return address;
    }
    
  }
  
  /**
   * The {@code PhoneNumber} class is a static class that represents a Contact's phone number.
   * <p>
   * {@code PhoneNumber} is in effect a top level class that has been nested for 
   * encapsulation purposes and is defined static because it has no need to reference 
   * instance variables or methods belonging to Contact
   * @author Eric
   *
   */
  
  private static class PhoneNumber {
    private short countryCode; 
    private short areaCode;
    private int subscriberNumber; 

    private PhoneNumber(String countryCode, String areaCode, String subscriberNumber) {
      String parsedCountryCode = parseStringToNumberString(countryCode);
      String parsedAreaCode = parseStringToNumberString(areaCode);
      String parsedSubscriberNumber = parseStringToNumberString(subscriberNumber);
      this.countryCode = Short.valueOf(parsedCountryCode);
      this.areaCode = Short.valueOf(parsedAreaCode);
      this.subscriberNumber = Integer.valueOf(parsedSubscriberNumber);
    }
	
    /**
     * Returns the Contact's phone number. The phone number is represented by 
     * three fields: country code, area code, and subscriber number. 
     * The fields are separated by a single space.
     * This representation of phone number is alternatively used for external and
     * long term storage purposes.
     * @return the Contact's phone number with spaces between fields.
     */

    @Override
    public String toString() {
      return countryCode + " " + areaCode + " " + subscriberNumber;
    }
  }

  /**
   * Returns an alternative version of a given string, excluding any non-digit
   * characters or spaces. Used internally for short and int representations of
   * a string and for uniform comparisons.
   * @param string a string to be stripped of any non-digit characters
   * @return a new representation of the string with only digits included
   */
  
  static String parseStringToNumberString(String string) {
    StringBuilder stringBuilder = new StringBuilder();
    for (char c: string.toCharArray()) {
      if (Character.isDigit(c)) {
        stringBuilder.append(c);
      }
    }
    return stringBuilder.toString();
  }
  
}
