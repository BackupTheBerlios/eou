package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Network Project
 * 
 * Emulates a bridge/switch for the virtual network
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Switch {

	// pas d'IP ???
//	private InetAddress ip;
	/** IP-address of the switch */
	private int admin_port;
	/** mac-adress of the switch */
	private OurMac mac_address;
	/** name of the Switch */
	private String name;
	/** priority of the base camp */
	private int priority;
		
//	protected boolean stop = false;
	
	private ArrayList list; // stocke les trames envoy?es pour pas les renvoyer...
	
	private static final int CAPACITY = 100;
	
	/** storing the ports of the switch */
	private OurPort[] ports;
	/** a default maximum number of ports */
	private static final int MAX_PORT = 9999;
//	private static final int DEFAULT_ADMIN_PORT = 8000;
		

	
	/** Build a new Switch from a configuration file*/
	public Switch(String name, File fich) {
		super();
		ports = new OurPort[MAX_PORT];
		LineNumberReader lnr;
		String line = "";
		list = new ArrayList(CAPACITY);
		if ((lnr = SyntaxAnalyz.find(fich, "switch", name))!=null) {
			try {
				this.name = name;
				for (line = lnr.readLine(); line != null && !line.startsWith("["); line = lnr.readLine()) {
					if (line.startsWith("admin-port:"))
						this.admin_port = SyntaxAnalyz.readAdminPort(line);
					else if (line.startsWith("priority:"))
						this.priority = SyntaxAnalyz.readPriority(line);
					else if (line.startsWith("MAC-address:"))
						this.mac_address = SyntaxAnalyz.readMac(line);
					else if (line.startsWith("port-"))
						addPorts(line);
				}
			}catch (IOException e) {
				System.err.println("probleme d'E/S dans la construnction du switch "+name);
			}
			System.out.println(this);
			
			//TODO creer une connection TCP qui ecoute le port admin
			runAdmin();
		}
		else
			System.err.println ("Switch <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");
	}

	
	/** Returns the name of the switch */
	public String getName() {
		return this.name;
	}
	
	/**
	 *  Tests if the Trame as already been seen by the switch
	 * 
	 * @param msg the Trame to verify
	 * @return true if the trame as already been seen
	 */
	private boolean dejaVu(Trame msg) {
		for (int i=0; i<list.size(); ++i) 
			if (((Trame)list.get(i)).equals(msg))
				return true;
		return false;
	}
	
	/**
	 * Propagate an incoming trame to all the other ports of the switch
	 * @param msg the incoming trame
	 * @param from the receiving port
	 */
	public void propagate(Trame msg, OurPort from) {
		for (int i=0; i<ports.length; ++i) {
			if (ports[i]!=null && !ports[i].equals(from) && !dejaVu(msg)) {
				ports[i].send(msg);
			}
		}
		list.add(msg);
	}
	
	/**
	 * Add a new port to the switch
	 * @param line the line of the configuration file containing the port information
	 */
	private  void addPorts(String line) {
		String token = null;
		int i;
		try {
			if (line.startsWith("port-")) {
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					String tmp = token.substring("port-".length(), token.length()-1);
					i = Integer.parseInt(tmp);
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						InetSocketAddress isa = SyntaxAnalyz.parseISA(token);
						setPort(i, new OurPort(isa, this));
					}
				}
			}
		} catch (NumberFormatException e) {
			System.err.println ("format incorrect : <"+token+"> n\'est pas un nombre ?");
		} catch (IOException e) {
			System.err.println ("Erreur d'entree sortie pendant l\'ajout d\'un port ");
		}
	}
	
	

	/**
	 * Analyse the command line.
	 * 
	 * @param line line of string to analyse.
	 * @deprecated
	 */
	protected boolean analyse(String line) {
		if (SyntaxAnalyz.isComment(line)) {
			StringTokenizer args = new StringTokenizer(line);
			
			if (args.hasMoreElements()) {
				String token = args.nextToken();
				System.out.println ("ACTION");
				System.out.println(token);
				while (args.hasMoreTokens()) 
					System.out.println(args.nextToken());
				return true;
			} 
			else {
			System.out.println("ligne vide ?");
				return false;
			}
		}
		System.out.println ("com");
		return true; //commentaires
	}
	
	
	/**
	 * Sets a port to a new value (an activate it if it wasn't
	 * @param num_port the number of the port to change
	 * @param value the new port value
	 */
	public void setPort (int num_port, OurPort value) {
		ports[num_port] = value;
	}
	
	/**
	 * Returns the port corresponding to a specified number on the switch
	 * @param num_port the number of the port
	 * @return the port
	 */
	public OurPort getPort (int num_port) {
		return ports[num_port];
	}
	
	
	/**
	 * Returns the number of a port on the switch
	 * @param os the port
	 * @return the number of the port
	 */
	public int getPortNumber (InetSocketAddress os) {
		for (int i=0; i<ports.length; i++)
			if (os.equals(ports[i]))
				return i;
		throw new NoSuchElementException();
	}
	
	
	/** launches the administration of the switch */
	private void runAdmin() {
		try {
			final ServerSocket ss = new ServerSocket(admin_port);
			System.out.println("ready");
			while (/*!this.stop &&*/ !Main.stop) {//Idealement, il faurait gerer un pool de threads
				Socket s = ss.accept();
				System.out.println("nvelle connection");
				// un client s'est connect?
				new Thread (new AdminSwitch(this, s)).start();
			}
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}
	}
	

	/**
	 * Returns the priority of the switch
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets the priority of the switch
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	
	/**
	 * Gets the informations for the switch
	 * @param st the arguments of the command
	 * @param output where to write
	 * @throws IOException 
	 */
	protected void info(StringTokenizer st, BufferedWriter output) throws IOException {
		if (!st.hasMoreTokens()) {
			for (int i=0; i<ports.length; ++i)
				if (ports[i]!=null)
					printInfoPort(i, output);
		}
		else {// info du port i
			int i = Integer.parseInt(st.nextToken());
			printInfoPort(i, output);
		}
	}

	/**
	 * print the informations for one specified port
	 * @param i theport
	 * @param output where to write/print
	 * @throws IOException 
	 */
	private void printInfoPort (int i, BufferedWriter output) throws IOException {
		StringBuffer sb = new StringBuffer();
		InetSocketAddress os = getPort(i).getIsa();
		sb.append("Port <"+ new Integer(i).toString() +">\n State : ");
		if (os == null) 
			sb.append("down");
		else {
			sb.append("up\n");
			sb.append(" Colision domain : "+os);
			//TODO ajouter pour le spanning tree
		}
		output.write(sb.toString()+"\n");
		output.flush();
	}
	
	/** Returns a string representation of that switch */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("##### SWITCH #####\n");
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port)+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
		sb.append("priority: "+new Integer(priority)+"\n");
		for (int i=0; i<ports.length; ++i)
			if (ports[i]!=null)
				sb.append(ports[i]+"\n");
		sb.append("##### switch #####\n");
		return sb.toString();
	}
	
	
	// TESTS ...
	public static void main(String[] args) {
		int i = 0;
		File f = null;
		Switch s = null;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			f = new File(args[1]);
			i = 2;
		}
		else {
			if (args.length>0) {
				System.out.println("-> default file");
				f = Main.DEFAULT_CONF_FILE;
			}
			else {
				System.err.println("pas assez d'arguments ");
				System.exit(-1);
			}
			s = new Switch(args[i], f);
			
		}
		
	}


}
