package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 10 mars 2004 */

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class OurMac {

	private String address;
	
	/** Default constructor */
	public OurMac(String str) {
		super();
		//TODO verifier qu'on a une mac
		address = str;
	}
	
	public String toString() {
		return address;
	}

}
