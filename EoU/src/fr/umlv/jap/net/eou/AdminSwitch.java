package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 13 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Network Project
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class AdminSwitch implements Runnable {

	private Socket sock;
	
//	private ServerSocket ss;
	
//	private String name;
	private Switch sw;
	
	private BufferedWriter output;
	
	/** default constructor */
	public AdminSwitch(Switch sw, Socket s) {
		super();
		this.sock = s;
//		this.ss = ss;
		this.sw = sw;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// la connection est etablie on recuper les flots;
		try {
//			System.out.println("yop");
//			Socket s = ss.accept();
//			System.out.println("hey");

			BufferedReader is = new BufferedReader (new InputStreamReader(sock.getInputStream()));
//			BufferedWriter os = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			output = new BufferedWriter (new OutputStreamWriter(sock.getOutputStream()));
			
			String str = "";
			output.write("Administration of the switch <"+sw.getName()+">...\n\tYour command : \n");
			output.flush();
			System.out.println(str);
			while ((str = is.readLine())!=null) {
				System.err.println("admin switch lis : "+str);
//				output.write(str.toUpperCase()); //TODO faire traitement...
//				output.write("\n");
//				output.flush();
				analyse(str);
			}
		} catch (IOException e) {
			System.err.println("erreur d'entree sortie dans l'admin du switch");
		}
	}
	
	private void analyse(String args) throws IOException {
		StringTokenizer st = new StringTokenizer(args);
		String cmd;
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
//			System.out.println(cmd);
			if (cmd.equalsIgnoreCase("info"))
				adminInfo(st);
			else if (cmd.equalsIgnoreCase("priority")) {
				adminPriority(st);
			}
			else if (cmd.equalsIgnoreCase("port")) 
				adminPort(st);
			else if (cmd.equalsIgnoreCase("quit")) 
				sock.close();
/*			else if (cmd.equalsIgnoreCase("stop")) {
				sw.stop=true;
				sock.close();
				System.out.println("Switch <"+sw.getName()+"> arrete");
			}*/
			
			
			// else //TODO gerer les autres cmd
//			else System.out.println("Commande <"+cmd+"> de config de sitch non reconnue");
			else {
				output.write("Command <"+cmd+"> of switch configuration unknown\n");
				output.flush();
			}
		}
	}
	
	private void adminInfo (StringTokenizer st) throws IOException {
		sw.info(st, output);
	}
	
	private void adminPriority(StringTokenizer st) throws IOException {
		if (st.hasMoreTokens()) {
			sw.setPriority(Integer.parseInt(st.nextToken()));
			output.write("New switch priority <"+sw.getName()+"> : "+ new Integer(sw.getPriority())+"\n");
			output.flush();
		}
		else 
//		System.out.println("Priorite du switch <"+sw.getName()+"> : "+ new Integer(sw.getPriority()));
		output.write("Switch priority <"+sw.getName()+"> : "+ new Integer(sw.getPriority())+"\n");
		output.flush();
	}
	
	private void adminPort(StringTokenizer st) throws IOException {
		String str = "";
		try {
			if (st.hasMoreTokens()) {
				str = st.nextToken();
				int i = Integer.parseInt(str);
				if (st.hasMoreTokens()) {
					str = st.nextToken();
					if (str.equalsIgnoreCase("down")) {
						sw.setPort(i, null);
					}
					else /*if (str.startsWith("["))*/ {
						sw.setPort(i, SyntaxAnalyz.parseISA(str));
					}
				}
				else {
					output.write("unsufficient arguments for the port configuration\n");
					output.flush();
				}
			}
			else {
				output.write("unsufficient arguments for the port configuration\n");
				output.flush();
			}
		} catch (NumberFormatException nfe) {
			output.write("invalid value <"+Integer.parseInt(str)+">, integer expected\n");
			output.flush();
		}
	}
	
}
