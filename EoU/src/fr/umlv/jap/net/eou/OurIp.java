package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 10 mars 2004 */

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class OurIp {

	private String address;
	
	// ou plutot :
//	private byte[] address;
	
	/** Default constructor */
	public OurIp(String ip) {
		//TODO caser une vérif d'ip
		address = ip;
	}
	
	public String toString() {
		return address;
	}

	public static void main(String[] args) {
	}
}
