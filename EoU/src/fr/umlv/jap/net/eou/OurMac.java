package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 10 mars 2004 */

/**
 * Network Project
 *  Manage the Mac-Address
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class OurMac {
	/** the Mac-Address */
	private String address;
	
	/** Default constructor */
	public OurMac(String str) {
		super();
		address = str;
	}
	

	/**
	 * Tests wether the two mas-address are the same
	 * @param mac the mac to compare
	 * @return true if the Mac-Address are the same
	 */
	public boolean equals(OurMac mac) {
		return (this.toString().compareTo(mac.toString())==0);
	}


	/** Returns a string representation of that Mac-Address  */
	public String toString() {
		return address;
	}

}
