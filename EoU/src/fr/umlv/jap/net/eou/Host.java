package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;

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
	
	//TODO ajouter une table ARP
	
	
	/** @deprecated pas de creation directe ? */
	public Host(String name, int admin_port, String macAddr, String ip, OurSocket link) {
		super();
		this.name = name;
		this.admin_port = admin_port;
		this.mac_address = new OurMac(macAddr);
		this.ip = new OurIp(ip);
		this.link = link;
	}

	/** Default constructor */
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
			
			//TODO creer une connection TCP qui ecoute le port admin
			
			//TODO creer une connection UDP qui ecoutera le mulsticats et qui reperera ce qui lui appartient..

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
//		int i;
		File f;
		Host h;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
			h = new Host(args[2], f);
			System.out.println(h);
			// structure cree
			// lancer le server d'emulation...
			
		}
		else {
			if (args.length>0) {
				System.out.println("-> default");
				f = new File("network.conf");
				h = new Host(args[0], f);
			}
			else 
				System.err.println("pas assez d'arguments ");
			
		}
			
	}

}
