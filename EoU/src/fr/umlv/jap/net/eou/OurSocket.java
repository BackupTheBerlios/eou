package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 10 mars 2004 */

import java.util.StringTokenizer;

/**
 * Network Project
 * 
 * Manage an IP address + its port
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class OurSocket {

//	private String ip;
	private OurIp ip;
	
	private int port;
	
	/** Default constructor */
	public OurSocket(String ip, int port) {
		super();
		this.ip = new OurIp(ip);
		this.port = port;
	}
	
	public OurSocket(String sock) {
		super();
		StringTokenizer st = new StringTokenizer(sock, ":");
		//TODO ajouter verif ...
		this.ip = new OurIp(st.nextToken());
		this.port = Integer.parseInt(st.nextToken());
	}
	
	public String toString() {
		return ip+":"+new Integer(port).toString();
	}

	public boolean equals(OurSocket os) {
		return (this.toString().compareTo(os.toString())==0);
		// pas beau, faire mieux ?
	}
	
	
	
	// TESTS
	public static void main(String[] args) {
		
	}
}
