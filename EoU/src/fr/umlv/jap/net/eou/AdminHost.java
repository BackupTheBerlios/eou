package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Network Project
 *  Administration of the host
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class AdminHost implements Runnable {
	/** the socket administrator */
	private Socket sock;
	/** the parent host */
	private Host h;
	/** where to write output */
	protected BufferedWriter output;

	
	/** Default constructor */
	public AdminHost(Host parent, Socket sock_admin) {
		super();
		this.sock = sock_admin;
		this.h = parent;
		try {
			output = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
	//		System.err.println("Erreur d'E/S dans la creation de l'output de l'admin host");
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// la connection est etablie on recuper les flots;
		try {
			BufferedReader is = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			
			String str = "";
			output.write("Host <"+h.getName()+"> Administration...\n\tYour command : ");
			output.flush();
			while ((str = is.readLine())!=null) {
				analyse(str);
			}
		} catch (IOException e) {
			// deconnection
		}
	}
	
	/**
	 * Analyse the reception of the administrator
	 * @param args receipt arguments 
	 * @throws IOException
	 */
	private void analyse(String args) throws IOException {
		StringTokenizer st = new StringTokenizer(args);
		String cmd = "";
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
									
			if (cmd.equalsIgnoreCase("ip"))
				adminIp(st);
			//		else if (cmd.equalsIgnoreCase("arp")) 
			//			adminArp(st);
			else if (cmd.equalsIgnoreCase("link")) 
				adminLink(st);
			else if (cmd.equalsIgnoreCase("ping")) 
				adminPing(st);
			else if (cmd.equalsIgnoreCase("quit")) 
				sock.close();
			else {
				output.write("Unknown command <"+cmd+"> for Host configuration\n");
				output.flush();
			}
		}
		else {
			output.write("Unknown command <"+cmd+"> for Host configuration\n");
			output.flush();
		}
	}
	
	protected void write(String str) throws IOException {
		output.write(str);
		output.flush();
	}
	
	/**
	 * Treatment for the Ip command
	 * @param st the arguments of that command
	 * @throws IOException
	 */
	private void adminIp(StringTokenizer st) throws IOException {
		if (!st.hasMoreTokens()) {
			output.write("IP : "+h.getIp()+"\n");
			output.flush();
		}
		else {
			h.setIp(InetAddress.getByName(st.nextToken()));
		}
	}
	
	/**
	 * 
	 * Treatment for the Link command
	 * @param st the arguments of that command
	 * @throws IOException
	 */
	private void adminLink(StringTokenizer st) throws IOException {
		if (!st.hasMoreTokens()) {
			output.write("Link : "+h.getLink()+"\n");
			output.flush();
		}
		else {
			String str = st.nextToken();
			if (str.equalsIgnoreCase("down")) 
				h.setLink(null);
				else {
					h.setLink(SyntaxAnalyz.parseISA(str));
				}
		}
	}
		/**
		 * 
		 * Treatment for the Ping command
	 	 * @param st the arguments of that command
		 * @throws IOException
		 */
		private void adminPing(StringTokenizer st) throws IOException {
			// appel : ping -conf mort-subite.conf host2 10:10:10:aa:10:11
			String str;
			if (st.hasMoreTokens()) {
						str = st.nextToken(); // MAC address
						InetAddress ip = null;
						try {
							ip = InetAddress.getByName(str);
							System.err.println("ping pas permis en destination de ip");
							System.exit(-1);
						} catch (IOException ioe){
							OurMac dest_mac = new OurMac(str);
							h.doPing(dest_mac);				
						}
			}
			else  {
				output.write("Pas assez d'args : \n\tping MAC-address\n");
				output.flush();
			}
		}
		
}
