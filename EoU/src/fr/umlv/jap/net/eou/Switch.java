package fr.umlv.jap.net.eou;

/* Maitrise info - Reseau - projet */
/* Created on 9 mars 2004 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Network Project
 * 
 * Emulates a bridge
 *
 * @author Yam Jean Paul
 * @author Bruneteau Adrien
 */
public class Switch {

	// pas d'IP ???
//	private OurIp ip;
	
	private int admin_port;
	
//	private String mac_address;
	private OurMac mac_address;
	
	private String name;
	
//	private int priority;
	
//	private ArrayList ports;
//	private Hashtable ports; // la ya peut être mieux !
//	private LinkedHashMap ports;
	private OurSocket[] ports;
	
//	private static final int DEFAULT_ADMIN_PORT = 8000;
	
	private static final int MAX_PORT = 9999;
	
	protected boolean stop = false;
	
	
	/** @deprecated not directly ? */
	public Switch(String switch_name, String mac_add, int adminPort) {
		super();
		this.name = switch_name;
		this.mac_address = new OurMac(mac_add);
		this.admin_port = adminPort;
	//	ports = new ArrayList(MAX_PORT);
//	ports = new Hashtable();
//		ports = new LinkedHashMap();
	}
	
	/** Build a new Switch from a configuration file*/
	public Switch(String name, File fich) {
		super();
		LineNumberReader lnr;
		String line;
		if ((lnr = SyntaxAnalyz.find(fich, "switch", name))!=null) {
//			lnr.mark(1);
			this.name = name;
			// faire une copie de sauvegarde de "lnr" pour assurer si l'ordre n'est pas le bon ?
			this.admin_port = SyntaxAnalyz.readAdminPort(lnr);
			// verifier si on n'a pas 0 ?
			//TODO recup priorite
			this.mac_address = SyntaxAnalyz.readMac(lnr);
			// verif null ?
//			ports = new ArrayList(MAX_PORT);
//			ports = new Hashtable();
//			ports = new LinkedHashMap();
			ports = new OurSocket[MAX_PORT];
			addPorts(lnr);
			
			test();
			
			//TODO creer une connection TCP qui ecoute le port admin
			runAdmin();
			
			//TODO creer une connection UDP qui ecoutera le mulsticats et qui reperera ce qui lui appartient..
		}
		else
			System.err.println ("Switch <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");

	}

	public String getName() {
		return this.name;
	}
	
	
	private  void addPorts(LineNumberReader lnr) {
		
		String line;
		String token = null;
		int i;
		
		try {
			while ((line = lnr.readLine())!=null)
				if (line.startsWith("port-")) {
					//		i = Integer.parseInt(line.substring("port-".length(), line.indexOf(":")));

					StringTokenizer st = new StringTokenizer(line);
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						String tmp = token.substring("port-".length(), token.length()-1);
						i = Integer.parseInt(tmp);
	//					System.err.println (i);
						if (st.hasMoreTokens()) {
							token = st.nextToken();
	//						System.err.println (token);
							OurSocket os = new OurSocket(token);
							//			ports.add(i, os);
	//						System.out.println("port : "+new Integer(i)+"\t socket : "+os);
							setPort(i, os);
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
	
	public void setPort (int num_port, OurSocket value) {
//		ports.set(num_port, value);
//		ports.add(num_port, value);
//		ports.put(new Integer(num_port), value);
		ports[num_port] = value;
	}
	
	public OurSocket getPort (int num_port) {
//		return (OurSocket)ports.get(num_port);
//		return (OurSocket)ports.get(new Integer(num_port));
		return ports[num_port];
	}
	
	public int getPortNumber (OurSocket os) {
//		System.out.println("num de port ? "+ )
//		return ports.hashCode();
		for (int i=0; i<ports.length; i++)
			if (os.equals(ports[i]))
				return i;
		throw new NoSuchElementException();
	}
	
	
	private void runAdmin() {
		try {
			final ServerSocket ss = new ServerSocket(admin_port);
			System.err.println("yop");
					while (!stop) {//Idealement, il faurait gerer un pool de threads
						Socket s = ss.accept();
						System.err.println("hey");
						// un client s'est connecté
		//				new Thread (new AdminSwitch(name, s)).start();
						new Thread (new AdminSwitch(this, s)).start();
					}
				
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}

	}
	
/*	
	protected void admin(Socket sock) {
		//TODO gerer toute l'admin switch
		
		// sinon en faire une classe runnable ?
		String str;
		try {
			InputStream is = sock.getInputStream();
			while (sock.isConnected()) { // pas sur !
				// la on a un administrateur connecte, on veut prendre tout ce qu il dit jusqu a sa deconnection...
//				is.read();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
*/	
	private void info(String args) {
		StringTokenizer st = new StringTokenizer(args);
		if(!st.nextToken().equalsIgnoreCase("info"))
			System.err.println ("pb dans les param de l'info");
		else {
			if (!st.hasMoreTokens()) {
				// par defaut;
		//		Enumeration ens = ports.keys();
//				Iterator iter = ports.values().iterator();
		//		while (ens.hasMoreElements()) {
//				while (iter.hasNext()) {
		//			printInfoPort(Integer.parseInt((String)ens.nextElement())); // plus simple ?
		//			printInfoPort(((OurSocket)iter.next())); // plus simple ?
	    //			printInfoPort(ports.(iter.next())); // plus simple ?
//				}
				for (int i=0; i<ports.length; ++i)
					if (ports[i]!=null)
						printInfoPort(i);
			}
			else {// info du port i
				int i = Integer.parseInt(st.nextToken());
				printInfoPort(i);
			}
		}
	}
	
	private void printInfoPort (int i) {
		StringBuffer sb = new StringBuffer();
		OurSocket os = getPort(i);
		sb.append("Port <"+ new Integer(i).toString() +">\n Etat : ");
		if (os == null) 
			sb.append("down");
		else {
			sb.append("up\n");
			sb.append(" Domaine de collision : "+os);
			//TODO ajouter pour le spanning tree
		}
		System.out.println(sb);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("##### SWITCH #####\n");
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port).toString()+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
//		Iterator i = ports.iterator();
/*		Iterator i = ports.values().iterator();

		while (i.hasNext()) {
			sb.append((OurSocket)i.next()+"\n");
		} 
*/
		for (int i=0; i<ports.length; ++i)
			if (ports[i]!=null)
				sb.append(ports[i]+"\n");
		
		sb.append("##### switch #####\n");
		return sb.toString();
	}
	
	protected void finalize() throws Throwable {
		stop=true;
	}
	
	
	private void test() {
		System.out.println(this);
		System.out.println("## infos : ");
		this.info("info");
		System.out.println("## infos 6: ");
		this.info("info 6");
		System.out.println("## infos 7: ");
		this.info("info 7");		
	}
	
	// TESTS ...
	public static void main(String[] args) {
		int i = 0;
		File f = null;
		Switch s = null;
		if (args.length>1 && args[0].equalsIgnoreCase("-conf")) {
			f = new File(args[1]);
//			System.out.println(f.getAbsolutePath());
			// structure cree
			// lancer le server d'emulation...
			i = 2;
		}
		else {
			if (args.length>0) {
				System.out.println("-> default");
				f = new File("network.conf");
//				i = 0;
			}
			else 
				System.err.println("pas assez d'arguments ");
			
		}
		
		s = new Switch(args[i], f);
/*		System.out.println(s);
		System.out.println("## infos : ");
		s.info("info");
		System.out.println("## infos 6: ");
		s.info("info 6");
		System.out.println("## infos 7: ");
		s.info("info 7");
		

		s.stop = true;
		System.out.println("bye");
		*/
	}
}
