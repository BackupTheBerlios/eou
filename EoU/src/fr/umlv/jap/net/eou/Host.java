package fr.umlv.jap.net.eou;

import java.io.File;
import java.io.LineNumberReader;

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

	public Host(String name, File fich) {
		super();
		LineNumberReader lnr;
		String line;
		if ((lnr = SyntaxAnalyz.find(fich, "host", name))!=null) {
			this.name = name;
		this.admin_port = SyntaxAnalyz.readAdminPort(lnr);
		this.mac_address = SyntaxAnalyz.readMac(lnr);
		this.ip = SyntaxAnalyz.readIp(lnr);
		this.link = SyntaxAnalyz.readLink(lnr);
		}
		else
			System.err.println ("Host <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");

	}

	//TODO voir pour les accesseurs necessaires
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("##### HOST #####\n");
		sb.append("[host "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port).toString()+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
		sb.append("IP-address: "+ip+"\n");
		sb.append("link: "+link+"\n");
		sb.append("##### host #####\n");
		return sb.toString();
	}
	
	// TESTS
	public static void main(String[] args) {
		int i;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			File f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
			Host h = new Host(args[2], f);
			System.out.println(h);
			// structure cree
			// lancer le server d'emulation...
			
		}
		else {
			System.err.println("pas assez d'arguments");
		}
			
	}

}
