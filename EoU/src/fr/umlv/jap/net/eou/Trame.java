package fr.umlv.jap.net.eou;

import java.util.StringTokenizer;

/* Maitrise info - Reseau - projet */
/* Created on 14 mars 2004 */

/**
 * Network Project
 * 
 * @author Jean Paul Yam
 * @author Adrien Bruneteau
 */
public class Trame {

	protected static final int TYPE_PING = 0x0E11;
	
	protected static final int OPCODE_REQUEST = 1;
	protected static final int OPCODE_ANSWER = 2;
	
	private static final String SEPARATOR = "#";
	
	private OurMac dest;
	private OurMac src;
	private int type;
	private int opCode;
	private String msg;

	/**
	 * representation theorique de la trame : 
	   <pre>
	    6 octets   6 octets  2 octets 1 octet                     4 octets 
	+-----------+----------+--------+--------+-----------------+---------+
	| Dest Mac@ | Src Mac@ |  Type  | OpCode |     Message     |   CRC   |
	+-----------+----------+--------+--------+-----------------+---------+
	</pre>
	  
	 */

	/* representation en string pour des raisons pratiques */
	//TODO si le reste marche, passer ? du binaire...

	/**
	 * Constructor for Trame.
	 */
	public Trame(OurMac dest_mac, OurMac src_mac, int type, int opCode, String msg) {
		super();
		this.dest = dest_mac;
		this.src = src_mac;
		this.type = type;
		this.opCode = opCode;
		this.msg = msg;
		System.out.println("trame cree : "+this);
	}

	/**
	 * Constructor for Trame.
	 */
	public Trame(String str) {
		super();
		StringTokenizer st = new StringTokenizer(str, SEPARATOR);
//		if (st.hasMoreTokens()) { // TODO caser des verifs...
		this.dest = new OurMac(st.nextToken());
		this.src = new OurMac(st.nextToken());
		this.type = Integer.parseInt(st.nextToken());
		this.opCode = Integer.parseInt(st.nextToken());
		this.msg = st.nextToken();
		System.out.println("trame cree : "+this);
	}

	// methode getbyte - > byte[] pour le datagram packet
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nDestination : "+dest+
						"\nSource : "+src);
		if (type==TYPE_PING)
					sb.append("\nType : Ping");
		else sb.append("\nType : "+new Integer(type));
		if (opCode==OPCODE_ANSWER) 
			sb.append("\nOp : answer");
		else if (opCode==OPCODE_REQUEST) 
			sb.append("\nOp : request");
		else 
		sb.append("\nOp : "+new Integer(opCode));
//		sb.append("\n"):
		return sb.toString();
	}
	
	public boolean isRequest() {
		return (opCode==OPCODE_REQUEST);	
	}
	
	public boolean isAnswer() {
		return (opCode==OPCODE_ANSWER);	
	}		
	
	public boolean isPing()  {
	 	return (type==TYPE_PING);	
	}
	
	public String getMsg()  {
		return msg;	
	}
	
	public OurMac getDest() {
		return dest;	
	}
	
	public OurMac getSrc() {
		return src;	
	}
	
	private String getTrame() {
		return (dest+SEPARATOR+
					src+SEPARATOR+
					new Integer(type)+SEPARATOR+
					new Integer(opCode)+SEPARATOR+
					msg
		);	
	}
	
	public byte[] getBytes() {
		return this.toString().getBytes();	
	}
	
	
}
