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
	
	private int priority;
		
	protected boolean stop = false;

//	private ArrayList ports;
//	private Hashtable ports; // la ya peut ?tre mieux !
//	private LinkedHashMap ports;
//	private InetSocketAddress[] ports;
	private OurPort[] ports;
	
	private static final int MAX_PORT = 9999;
//	private static final int DEFAULT_ADMIN_PORT = 8000;
	
	/* communication */
//	private MulticastSocket[] ms; // ecoute
//	private DatagramSocket[] ds;  // envoi
	
	

	
	/** @deprecated not directly ? */
/*	public Switch(String switch_name, String mac_add, int adminPort) {
		super();
		this.name = switch_name;
		this.mac_address = new OurMac(mac_add);
		this.admin_port = adminPort;
	//	ports = new ArrayList(MAX_PORT);'
//	ports = new Hashtable();
//		ports = new LinkedHashMap();
	} */
	
	/** Build a new Switch from a configuration file*/
	public Switch(String name, File fich) {
		super();
		ports = new OurPort[MAX_PORT];
		LineNumberReader lnr;
		String line = "";
		if ((lnr = SyntaxAnalyz.find(fich, "switch", name))!=null) {
			try {
				//			lnr.mark(1);
				
				this.name = name;
				for (line = lnr.readLine(); line != null && !line.startsWith("["); line = lnr.readLine()) {
					//			if (line.startsWith("[switch"))
					// faire une copie de sauvegarde de "lnr" pour assurer si l'ordre n'est pas le bon ?
					if (line.startsWith("admin-port:"))
						this.admin_port = SyntaxAnalyz.readAdminPort(line);
					// verifier si on n'a pas 0 ?
					// recup priorite
					else if (line.startsWith("priority:"))
						this.priority = SyntaxAnalyz.readPriority(line);
					else if (line.startsWith("MAC-address:"))
						this.mac_address = SyntaxAnalyz.readMac(line);
					else if (line.startsWith("port-"))
						addPorts(line);
					// verif null ?
					//			ports = new ArrayList(MAX_PORT);
					//			ports = new Hashtable();
					//			ports = new LinkedHashMap();
	//				line = lnr.readLine();
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//		}
		//			test();
		
		//TODO creer une connection UDP qui ecoutera le mulsticats et qui reperera ce qui lui appartient..
		//			try {
//		ms = new MulticastSocket[MAX_PORT];
//		ds = new DatagramSocket[MAX_PORT];
		//			} catch (IOException e) {
		//				System.err.println("Erreur de connection UDP du switch <"+name+">");
		//			}
		System.out.println(this);
		
		//TODO creer une connection TCP qui ecoute le port admin
		runAdmin();
		
	}
	else
		System.err.println ("Switch <"+name+"> introuvable dans le fichier <"+fich.getAbsolutePath()+">");

}

	public String getName() {
		return this.name;
	}
	
	
	private  void addPorts(String line) {
//		String line;
		String token = null;
		int i;
		try {
//			while ((line = lnr.readLine())!=null)
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
							InetSocketAddress isa = SyntaxAnalyz.parseISA(token);
							//			ports.add(i, os);
	//						System.out.println("port : "+new Integer(i)+"\t socket : "+os);
							setPort(i, new OurPort(isa));
	//						System.out.println("un");
	//						this.ds[i] = new DatagramSocket(isa);
	//						System.out.println("deux");
	//						this.ms[i] = new MulticastSocket(isa);
	//						System.out.println("trois");
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
	
	public void setPort (int num_port, OurPort value) {
//		ports.set(num_port, value);
//		ports.add(num_port, value);
//		ports.put(new Integer(num_port), value);
		ports[num_port] = value;
	}
	
	public OurPort getPort (int num_port) {
//		return (OurSocket)ports.get(num_port);
//		return (OurSocket)ports.get(new Integer(num_port));
		return ports[num_port];
	}
	
	public int getPortNumber (InetSocketAddress os) {
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
			System.out.println("ready");
					while (/*!this.stop &&*/ !Main.stop) {//Idealement, il faurait gerer un pool de threads
						Socket s = ss.accept();
						System.out.println("nvelle connection");
						// un client s'est connect?
		//				new Thread (new AdminSwitch(name, s)).start();
//						new Thread (new AdminSwitch(this, s)).start();
						new Thread (new AdminSwitch(this, s)).start(); // finalement pas threade ?
					}
				
		} catch (IOException e) {
			System.err.println("pb de connection admin switch");
		}

	}
	
	/**
	 * @return Returns the ds.
	 */
//	public DatagramSocket getDs(int num_port) {
//		return ds[num_port];
//	}

	/**
	 * @return Returns the ms.
	 */
//	public MulticastSocket getMs(int num_port) {
//		return ms[num_port];
//	}

	/**
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
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
//		System.out.println(sb);
		output.write(sb.toString()+"\n");
		output.flush();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("##### SWITCH #####\n");
		sb.append("[switch "+name+"]\n");
		sb.append("admin-port: "+new Integer(admin_port)+"\n");
		sb.append("MAC-address: "+mac_address+"\n");
		sb.append("priority: "+new Integer(priority)+"\n");
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
	
	public void write(String msg, int port) {
		
	}
	
//	protected void finalize() throws Throwable {
//		Main.stop=true;
//	}
	
	
/*	private void test() {
		System.out.println(this);
		System.out.println("## infos : ");
		this.info("info");
		System.out.println("## infos 6: ");
		this.info("info 6");
		System.out.println("## infos 7: ");
		this.info("info 7");		
	}
	*/
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
//				f = new File("network.conf");
				f = Main.DEFAULT_CONF_FILE;
//				i = 0;
			}
			else {
				System.err.println("pas assez d'arguments ");
				System.exit(-1);
			}
				
		s = new Switch(args[i], f);
			
		}
		
	/*	System.out.println(s);
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
