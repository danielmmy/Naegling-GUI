package br.com.naegling;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Class to deal with the communications routines with the Naegling server.
 * @author Daniel Yokoyama
 *
 */
public abstract class NaeglingCom{
	
	public static final String MESSAGE_DELIMITER="#";
	public static final int  NAEGLING_COM_TIMEOUT=5000;
	public static final int  UDP_MESSAGE_MAX_SIZE=512;
	public static final int  TCP_MESSAGE_MAX_SIZE=5242880;

	
	/**
	 * enum with the communication type.
	 * @author Daniel Yokoyama
	 *
	 */
	public enum COM_TYPE{
		START_NODE(1),
		STOP_NODE(2),
		CREATE_MASTER_NODE(3),
		CREATE_SLAVE_NODE(4),
		NODE_STATUS(5),
		TEMPLATE_STATUS(6),
		START_MASTER_VIRTUAL_NODE(7),
		STOP_MASTER_VIRTUAL_NODE(8),
        START_NEW_CLUSTER(9),
        ADD_WORKING_NODE(10),
        REMOVE_WORKING_NODE(11),
        GET_CLUSTER_STATUS(12),
        REQUEST_CLUSTER_IP(13),
        REQUEST_TEMPLATE_TRANSFER(14),
        REQUEST_JOB_TRANSFER(15),
        EXECUTE_JOB(16),
        DOWNLOAD_JOB_FILE(17),
        DELETE_NODE(18),
        EDIT_NODE(19);

		private int value;    

		  private COM_TYPE(int value) {
		    this.value = value;
		  }

		  public int getValue() {
		    return value;
		  }
	}
	
	/**
	 * Sends Message to Naegling server.
	 * @param message
	 * @param hostname
	 * @param naeglingPort
	 * @throws Exception 
	 */
	public static int sendMessageToHostname(String message,String hostname,String naeglingPort) throws Exception{

		/*
		 * Create socket and send message.
		 */
		if(message.length()<TCP_MESSAGE_MAX_SIZE){
			InetAddress serverAddr=InetAddress.getByName(hostname);
			Socket socket = new Socket(serverAddr, Integer.parseInt(naeglingPort));
			socket.setSoTimeout(NAEGLING_COM_TIMEOUT);//time to wait
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			if(socket.isConnected()){
				System.out.println("sending: "+message);
				out.print(message);
				out.flush();
				message=br.readLine();
				System.out.println("received: "+message);
			}else{
				message="-2#";
			}
			//DatagramSocket socket;
			//DatagramPacket packet;
			//InetAddress address;
			//socket = new DatagramSocket();
			//address = InetAddress.getByName(hostname);
			//int pnum = Integer.parseInt(naeglingPort);
			//byte messageBytes[] = message.getBytes();
			//packet =   new DatagramPacket(messageBytes, messageBytes.length, address, pnum);
			//socket.send(packet);
			//System.out.println("Sending message to "+hostname+" on port "+naeglingPort+".");

			
			String[] receivedFlags=message.split(MESSAGE_DELIMITER);
			socket.close();
			if(receivedFlags[0].equals("-1"))
				return -1;
			return Integer.parseInt(receivedFlags[0]);
		}else{
			throw new Exception("Message exceeds maximum size.");
		}
	}



	/**
	 * Compute file MD5 hash
	 * @param filePath - path to the file
	 * @return String with the MD5 hash in 32 hexadecimal characters 
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 */
	public static String getMD5(String filePath) throws NoSuchAlgorithmException, FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		File f = new File(filePath);
		InputStream fis = new FileInputStream(f);				
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while( (read = fis.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			return output;
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
		finally {
			try {
				fis.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}		
	}
	
	

}
