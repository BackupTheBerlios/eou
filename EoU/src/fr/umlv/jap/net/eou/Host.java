package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Host {

	private String name;
	
	private int admin_port;
	
//	private String mac_address;
	private OurMac mac_address;
	
//	private String ip;
	private OurIp ip;
	
	private OurSocket link;
	
	
	/** Default constructor */
	public Host(String name, int admin_port, String macAddr, String ip, OurSocket link) {
		super();
		this.name = name;
		this.admin_port = admin_port;
		this.mac_address = new OurMac(macAddr);
		this.ip = new OurIp(ip);
		this.link = link;
	}

	//TODO voir pour les accesseurs necessaires
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port).toString()+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
		sb.append("IP-address: "+ip+"\n");
		sb.append("link: "+link+"\n");
		return sb.toString();
	}
	
	// TESTS
	public static void main(String[] args) {
	}
}
