package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.Date;

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
	/** the last unreceived ping trame */ 
	private Trame lastPing;
	/** starting date of a ping */
	private Date start_ping = null;
	/** the number of the ping */
	private static int count = 0;
	
	AdminHost admin_host = null;
	
	
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
	//			System.err.println("Erreur d'E/S sur la construction de l'hote");
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
			while (!Main.stop) {//Idealement, il faurait gerer un pool de threads
				Socket s = ss.accept();
				(job_admin = new Thread (admin_host = new AdminHost(this, s))).start();
			}
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}
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
		this.link = new OurPort(link, this);
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
	public void treatTrame(Trame msg) throws IOException {
		if (is4mePingRequest(msg)) {
			Trame answer = msg.PingAnswer();
			byte[] buf = msg.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, getLink().getIsa());
			DatagramSocket ds = new DatagramSocket();
			ds.send(dp);
		}
		else if (lastPing != null && lastPing.isPingAnswer(msg)) {
			// TODO bonne sortie
			System.err.println(
				" Reception Ping Duree ecoulee : "
					+ new Long(new Date().getTime() - start_ping.getTime())
						.toString()
					+ " ms");
			admin_host.output.write(
				" Reception Ping Duree ecoulee : "
					+ new Long(new Date().getTime() - start_ping.getTime())
						.toString()
					+ " ms");
			admin_host.output.flush();
		}
		
		//else ignore
		
	}

	private boolean is4mePingRequest(Trame msg) {
		if (msg.isPing() && !msg.isAnswer()) {
			return (msg.getDest().equals(mac_address));
		}
		return false;
	}

	private boolean is4me(Trame msg) {
			return (msg.getDest().equals(mac_address));
	}

	/**
	 * Launches the Ping
	 * @param dest_mac destination Mac-Address of the Ping
	 * @param src_mac source Mac-Address of the Ping
	 * @throws IOException
	 */
	protected void doPing(OurMac dest_mac) throws IOException {
			lastPing = new Trame(	dest_mac,
															mac_address,
															Trame.TYPE_PING,
															Trame.OPCODE_REQUEST,
															"<<pong>>"+new Integer(count++));
			byte[] buf = lastPing.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, getLink().getIsa());
			DatagramSocket ds = new DatagramSocket();
			start_ping = new Date();
			ds.send(dp);
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
