package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

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
			//			System.out.println(cmd);
			if (cmd.equalsIgnoreCase("ip"))
				adminIp(st);
			//		else if (cmd.equalsIgnoreCase("arp")) 
			//			adminArp(st);
			//		else if (cmd.equalsIgnoreCase("ping")) 
			//			adminPing(st);
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
			String name;
			File f;
			if (!st.hasMoreTokens()) {
				str = st.nextToken();
				if (str.equalsIgnoreCase("-conf")) {
					f = new File(st.nextToken());
				} else {
					f = Main.DEFAULT_CONF_FILE;
				}
				if (st.hasMoreTokens()) {
					name = st.nextToken();
					if (st.hasMoreTokens()) {
						str = st.nextToken(); // MAC address
					}
				}
			} else {
				h.setIp(InetAddress.getByName(st.nextToken()));
			}
		}

	
}
