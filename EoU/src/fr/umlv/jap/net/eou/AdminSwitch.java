package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 13 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 * Network Project
 *	 Switch Administrator
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class AdminSwitch implements Runnable {
	/** the administration TCP socket */
	private Socket sock;
	/** the parent switch */
	private Switch sw;
	/** where to write outputs */
	private BufferedWriter output;
	
	/** default constructor */
	public AdminSwitch(Switch sw, Socket s) {
		super();
		this.sock = s;
		this.sw = sw;
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
			output.write("Administration of the switch <"+sw.getName()+">...\n\tYour command : ");
			output.flush();
			while ((str = is.readLine())!=null) {
				analyse(str);
			}
		} catch (IOException e) {
			// deconnection
		}
	}
	
	
	/**
	 * Analyse the command for the administrator 
	 * @param args argument of the command
	 * @throws IOException
	 */
	private void analyse(String args) throws IOException {
		StringTokenizer st = new StringTokenizer(args);
		String cmd;
		if (st.hasMoreTokens()) {
			cmd = st.nextToken();
			if (cmd.equalsIgnoreCase("info"))
				adminInfo(st);
			else if (cmd.equalsIgnoreCase("priority")) {
				adminPriority(st);
			}
			else if (cmd.equalsIgnoreCase("port")) 
				adminPort(st);
			else if (cmd.equalsIgnoreCase("quit")) 
				sock.close();
			else {
				output.write("Command <"+cmd+"> of switch configuration unknown\n");
				output.flush();
			}
		}
	}
	
	
	/**
	 * 
	 * Treatment for the Info command
	 * @param st the arguments of that command
	 * @throws IOException
	 */
	private void adminInfo (StringTokenizer st) throws IOException {
		sw.info(st, output);
	}
	
	
	/**
	 * 
	 * Treatment for the Priority command
	 * @param st the arguments of that command
	 * @throws IOException
	 */
	private void adminPriority(StringTokenizer st) throws IOException {
		if (st.hasMoreTokens()) {
			sw.setPriority(Integer.parseInt(st.nextToken()));
			output.write("New switch priority <"+sw.getName()+"> : "+ new Integer(sw.getPriority())+"\n");
			output.flush();
		}
		else {
				output.write("Switch priority <"+sw.getName()+"> : "+ new Integer(sw.getPriority())+"\n");
				output.flush();
		}
	}
	
	
	/**
	 * 
	 * Treatment for the Port command
	 * @param st the arguments of that command
	 * @throws IOException
	 */
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
					else {
						sw.setPort(i, new OurPort(SyntaxAnalyz.parseISA(str), sw));
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
