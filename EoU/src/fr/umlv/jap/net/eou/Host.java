package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;

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
//	private OurIp ip;
	private InetAddress ip;
	
//	private InetSocketAddress link;
	private OurPort link;
	
	//TODO ajouter une table ARP
	
	// communication
//	private MulticastSocket mcs; // ecoute ?
//	private DatagramSocket dgs; // envoie ?
	
	
	/** @deprecated pas de creation directe ? */
/*	public Host(String name, int admin_port, String macAddr, String ip, OurSocket link) {
		super();
		this.name = name;
		this.admin_port = admin_port;
		this.mac_address = new OurMac(macAddr);
		this.ip = new OurIp(ip);
		this.link = link;
	}
*/
	/** Default constructor */
	public Host(String name, File fich) {
		super();
		LineNumberReader lnr;
		String line;
		if ((lnr = SyntaxAnalyz.find(fich, "host", name))!=null) {
			this.name = name;
			try {
				line = lnr.readLine();
				while (line != null && !line.startsWith("[")) {
					
					if (line.startsWith("admin-port:"))
						this.admin_port = SyntaxAnalyz.readAdminPort(line);
					else if (line.startsWith("MAC-address:"))
						this.mac_address = SyntaxAnalyz.readMac(line);
					else if (line.startsWith("IP-address:"))
						this.ip = SyntaxAnalyz.readIp(line);
					else if (line.startsWith("link:"))
						this.link = new OurPort(SyntaxAnalyz.readLink(line), this);
					line = lnr.readLine();
				}
				
			} catch (IOException e) {
				System.err.println("Erreur d'E/S sur la construction de l'hote");
			}
			
			//TODO creer une connection UDP qui ecoutera le mulsticats et qui reperera ce qui lui appartient..
		//	new Thread() {
		//		public void run() {
		//			try {
		//				survey();
		//			} catch (IOException e) {
		//				System.err.println("Erreur d'E/S sur l\'ecoute de l'hote");
		//			}
		//		}
		//	}.start();

			
			//TODO creer une connection TCP qui ecoute le port admin
			runAdmin();
			
		}
		else
			System.err.println ("Host <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");
		
	}
	
	private void runAdmin() {
		try {
			final ServerSocket ss = new ServerSocket(admin_port);
			System.out.println("ready");
					while (!Main.stop) {//Idealement, il faurait gerer un pool de threads
						Socket s = ss.accept();
						System.out.println("connection");
						// un client s'est connect?
		//				new Thread (new AdminSwitch(name, s)).start();
						new Thread (new AdminHost(this, s)).start();
					}
				
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}

	}
	
//	protected void survey() throws IOException {
//		//TODO une boucle ?
//		byte[] buf = new byte[1024];
//		DatagramSocket dgs = new DatagramSocket(); // num de port choisi par java
////		DatagramPacket dgp = new DatagramPacket(buf, 0, link);
//		dgs.send(dgp);
//		dgp.setLength(1024);
//		dgs.receive(dgp);
//		System.out.println("sniff lis : "+new String(dgp.getData(), 0, dgp.getLength()));
//		//TODO preciser affichage + vers le term...
//		
//	}

	protected void ping(OurMac dest_mac, String msg) {
			 System.out.println("je veux envoyer "+msg+" a "+dest_mac);
	}

	public boolean isMyMac(OurMac mac) {
		return this.mac_address.equals(mac)	;
	}
	
	
	//TODO voir pour les accesseurs necessaires
	

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Returns the ip.
	 */
	public InetAddress getIp() {
		return ip;
	}
	
	/**
	 * @param ip The ip to set.
	 */
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	/**
	 * @param link The link to set.
	 */
	public void setLink(InetSocketAddress link) throws IOException {
		this.link = new OurPort(link);
	}

	/**
	 * @return Returns the link.
	 */
	public OurPort getLink() {
		return link;
	}

	
	/**
     * Returns the mac_address.
	 * @return OurMac
	 */
	public OurMac getMac_address() {
		return mac_address;
	}
	
	
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
	//			f = new File("network.conf");
				f = Main.DEFAULT_CONF_FILE;
				h = new Host(args[0], f);
			}
			else 
				System.err.println("pas assez d'arguments ");
			
		}
			
	}

}
