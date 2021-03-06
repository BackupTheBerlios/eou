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
	}

	/**
	 * Constructor for Trame.
	 */
	public Trame(String str) {
		super();
		try {
			StringTokenizer st = new StringTokenizer(str, SEPARATOR);
			this.dest = new OurMac(st.nextToken());
			this.src = new OurMac(st.nextToken());
			this.type = Integer.parseInt(st.nextToken());
			this.opCode = Integer.parseInt(st.nextToken());
			this.msg = st.nextToken();
		} catch (Exception e) {
			System.err.println("format de trame inconnu");
		}
	}

	/*
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
		return sb.toString();
	}
	
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[S] ");
		sb.append("From : "+src+"\tTo : "+dest);
		if (type==TYPE_PING)
					sb.append("\tType : Ping");
		else sb.append("\tType : "+new Integer(type));
		if (opCode==OPCODE_ANSWER) 
			sb.append("\tanswer");
		else if (opCode==OPCODE_REQUEST) 
			sb.append("\trequest");
		else 
		sb.append("\tOp : "+new Integer(opCode));
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
	
	public Trame PingAnswer() {
//		Trame answer = null;
		if (opCode==OPCODE_ANSWER)
			System.err.println("deja une reponse... pb !");
		else {
//			answer = new Trame(getTrame());
			opCode = OPCODE_ANSWER;	
		} 
		return this;
	}
	
	public boolean isPingAnswer(Trame t) {
		if (this.type==TYPE_PING && t.type==TYPE_PING) {
			if (this.opCode==OPCODE_REQUEST && t.opCode==OPCODE_ANSWER) { 
				if (this.dest.toString().equalsIgnoreCase(t.dest.toString()) && this.src.toString().equalsIgnoreCase(t.src.toString()) /*&& this.msg.toString().equalsIgnoreCase(t.msg.toString())*/) {
					return true;
				}
			}
		}
		// trames invalides
		return false;
	}
	
	protected String getTrame() {
		return (dest+SEPARATOR+
					src+SEPARATOR+
					new Integer(type)+SEPARATOR+
					new Integer(opCode)+SEPARATOR+
					msg
		);	
	}
	
	public byte[] getBytes() {
		return this.getTrame().getBytes();	
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		System.out.println("equals sur objets");
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Trame msg) {
		return (this.getTrame().equalsIgnoreCase(msg.getTrame()));
	}

}
