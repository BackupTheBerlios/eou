package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;

/**
 * Network Project
 * Manage the simulation of an Host
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Host {
	/** the name of that host */ 
	private String name;
	/** the administration, TCP port */
	private int admin_port;
	/** the Host Mac-Address */
	private OurMac mac_address;
	/** the ip-adress of the Host */
	private InetAddress ip;
	/** the Socket for the Link */
	private OurPort link;
	/** the Thread for the administration */
	private Thread job_admin;
	
	//TODO ajouter une table ARP
	
	
	
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
			/* creation de la socket TCP pour l'administration */
			runAdmin();
		}
		else
			System.err.println ("Host <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");
	}
	
	
	/** Launch the administration services */
	private void runAdmin() {
		try {
			final ServerSocket ss = new ServerSocket(admin_port);
			System.out.println("ready");
			while (!Main.stop) {//Idealement, il faurait gerer un pool de threads
				Socket s = ss.accept();
				System.out.println("connection");
				(job_admin = new Thread (new AdminHost(this, s))).start();
			}
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}
	}
	
	
	/** @deprecated */
	//TODO virer
	protected void ping(OurMac dest_mac) {
			 System.out.println("je veux envoyer "+" a "+dest_mac);
	}

	
	/**
	 * Tests if the mac is the same as the one of the Host
	 * @param mac the mac-address to compare
	 * @return true if the specified mac is the same as the Host's one
	 */
	public boolean isMyMac(OurMac mac) {
		return this.mac_address.equals(mac)	;
	}
	
	
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
	
	/**Treatment of a trame by the Host */
	public void treatTrame(Trame msg) {
		//TODO
		System.out.println ("je suis "+name+" et je recois..."+msg.getTrame());
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


	/** main methode */
	public static void main(String[] args) {
		File f;
		Host h;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			f = new File(args[1]);
			h = new Host(args[2], f);
			System.out.println(h);
			// structure cree
			// lancer le server d'emulation...
		}
		else {
			if (args.length>0) {
				System.out.println("-> default file ");
				f = Main.DEFAULT_CONF_FILE;
				h = new Host(args[0], f);
			}
			else 
				System.err.println("pas assez d'arguments ");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		job_admin.destroy();
		super.finalize();
	}

}
