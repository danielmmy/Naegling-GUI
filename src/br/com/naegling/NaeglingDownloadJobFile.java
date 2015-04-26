package br.com.naegling;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class NaeglingDownloadJobFile implements Runnable{
	private final String hostname;
	private final String filePath;
	private final String message;
	private final String port;

	/**
	 * Download file from cluster on hostname ans save to filePath
	 * @param hostname - naegling server host
	 * @param filePath - template image absolute path
	 */
	public NaeglingDownloadJobFile(String hostname,String port,String filePath,String message) {
		this.hostname=hostname;
		this.filePath=filePath;
		this.message=message;
		this.port=port;
	}
	
	private static Socket sendMessageToHostname(String message,String hostname,String naeglingPort) throws Exception{

		/*
		 * Create socket and send message.
		 */
		if(message.length()<NaeglingCom.TCP_MESSAGE_MAX_SIZE){
			InetAddress serverAddr=InetAddress.getByName(hostname);
			Socket socket = new Socket(serverAddr, Integer.parseInt(naeglingPort));
			socket.setSoTimeout(NaeglingCom.NAEGLING_COM_TIMEOUT);//time to wait
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			if(socket.isConnected()){
				System.out.println("sending: "+message);
				out.print(message);
				out.flush();
				message=br.readLine();
				System.out.println("received: "+message);
			}			
			String[] receivedFlags=message.split(NaeglingCom.MESSAGE_DELIMITER);
			if(receivedFlags[0].equals("0")){
				return socket;
			}
			socket.close();
			return null;
		}else{
			throw new Exception("Message exceeds maximum size.");
		}
	}

	public void run(){	
		if(hostname!=null&&filePath!=null){
			try {
				File f=new File(filePath);
				Socket socket=sendMessageToHostname(message, hostname, port);
				DataInputStream dis= new DataInputStream(socket.getInputStream());
				byte[] buff=new byte[NaeglingCom.TCP_MESSAGE_MAX_SIZE];
				int len = dis.read(buff, 0, NaeglingCom.TCP_MESSAGE_MAX_SIZE);
				System.out.println("len: "+len);
				
				

				DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
				dos.write(buff, 0, len);
				dos.close();

				socket.close();

			}catch (SocketException ex){
				ex.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();

			}
		}
	}
}
