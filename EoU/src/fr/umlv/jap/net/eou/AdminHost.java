package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class AdminHost implements Runnable {

	private Socket sock;
	
	private Host h;
	
	private BufferedWriter output;
	
	private Date start_ping = null;

	
	/** Default constructor */
	public AdminHost(Host h, Socket s) {
		super();
		this.sock = s;
		this.h = h;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// la connection est etablie on recuper les flots;
		try {
			BufferedReader is = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			output = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			
			String str = "";
			output.write("Host <"+h.getName()+"> Administration...\n\tYour command : \n");
			output.flush();
			System.out.println(str);
			while ((str = is.readLine())!=null) {
				System.err.println("admin host lis : "+str);
//				output.write(str.toUpperCase()); //TODO faire traitement...
//				output.write("\n");
//				output.flush();
				analyse(str);
			}
		} catch (IOException e) {
			System.err.println("erreur d'entree sortie dans l'admin de l'hote");
		}
	}
	
	
	private void analyse(String args) throws IOException {
		StringTokenizer st = new StringTokenizer(args);
		String cmd = "";
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
			
						System.err.println(cmd);
			if (cmd.equalsIgnoreCase("ip"))
				adminIp(st);
			//		else if (cmd.equalsIgnoreCase("arp")) 
			//			adminArp(st);
			//		else if (cmd.equalsIgnoreCase("ping")) 
			//			adminPing(st);
			else if (cmd.equalsIgnoreCase("link")) 
				adminLink(st);
			else if (cmd.equalsIgnoreCase("ping")) {
	//			System.err.println("admin ping");
				adminPing(st);
			}
			else if (cmd.equalsIgnoreCase("quit")) 
				sock.close();
			else {
				output.write("Unknown command <"+cmd+"> for Host configuration\n");
				output.flush();
			}
		}
		// else //TODO gerer les autres cmd
		//			else System.out.println("Commande <"+cmd+"> de config de sitch non reconnue");
		else {
			output.write("Unknown command <"+cmd+"> for Host configuration\n");
			output.flush();
		}
	}
	
	
	private void adminIp(StringTokenizer st) throws IOException {
		if (!st.hasMoreTokens()) {
			output.write("IP : "+h.getIp()+"\n");
			output.flush();
		}
		else {
			h.setIp(InetAddress.getByName(st.nextToken()));
		}
	}
	
	
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
	
		private void adminPing(StringTokenizer st) throws IOException {
			// appel : ping -conf mort-subite.conf host2 10:10:10:aa:10:11
			String str;
	//		String name;
	//		File f;
//	System.err.println("admin ping");
			
			if (st.hasMoreTokens()) {
//				output.write("args ping");
//				output.flush();
						str = st.nextToken(); // MAC address
						InetAddress ip = null;
						try {
							ip = InetAddress.getByName(str);
							System.err.println("ping pas permis en destination de ip");
							System.exit(-1);
						} catch (IOException ioe){
							OurMac dest_mac = new OurMac(str);
			//				Trame t = new Trame(dest_mac, h.getMac_address(), Trame.TYPE_PING, Trame.OPCODE_REQUEST, "test de ping");
							doPing(dest_mac, h.getMac_address());				
						}
			}
			else  {
				output.write("Pas assez d'args : \n\tping MAC-address\n");
				output.flush();
			}
		}
		


	private void doPing(OurMac dest_mac, OurMac src_mac) throws IOException {
			System.out.println("on ping ?");
			Trame msg = new Trame(	dest_mac,
															src_mac,
															Trame.TYPE_PING,
															Trame.OPCODE_REQUEST,
															"<<pong>>");
			//	sw.getPort(num_port).write(msg.getBytes());
			byte[] buf = msg.getBytes();
	//		System.out.println("avant dgs origin : "+origin);
			DatagramPacket dp = new DatagramPacket(buf, buf.length, h.getLink().getIsa());
	//		DatagramSocket ds = new DatagramSocket(this.origin);
			DatagramSocket ds = new DatagramSocket();
			ds.send(dp);
	//		System.out.println("apres dgs");
	//		this.start_time = new Date();
	//		listenPingReception(msg);
	
			start_ping = new Date();
			System.out.println("ping parti a  : "+start_ping);
	
	}
	
	
}
