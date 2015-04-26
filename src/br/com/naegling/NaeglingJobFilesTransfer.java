package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


/**
 * Transfer job files to cluster master node
 * @author Daniel Yokoyama
 *
 */
public class NaeglingJobFilesTransfer{
	private String jobName;
	private ArrayList<File> files;
	private VirtualMasterNode master;
	private JDialog copyDialog;


	public NaeglingJobFilesTransfer(String jobName, ArrayList<File> files, VirtualMasterNode master){
		this.jobName=jobName;
		this.files=files;
		this.master=master;
		copyDialog=new JDialog();
	}

	private Socket sendMessageToHostname(String message,String hostname,String naeglingPort) throws Exception{

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

	public void sendJobFiles() throws Exception{
		try {
			int filesQuantity=files.size();
			if(filesQuantity>0){
				final JProgressBar []copyProgress=new JProgressBar[filesQuantity];
				JPanel copyPanel=new JPanel();
				int columns=3;
				GridLayout layout=new GridLayout(filesQuantity,columns);
				JPanel [][]placeholder=new JPanel[filesQuantity][columns];
				for(int i=0;i<filesQuantity;i++){
					for(int j=0;j<columns;j++){
						placeholder[i][j]=new JPanel();
						copyPanel.add(placeholder[i][j]);
					}
				}
				copyPanel.setLayout(layout);


				for(int i=0;i<filesQuantity;i++){
					if(files.get(i).exists()){
						placeholder[i][0].add(new JLabel(files.get(i).getName()));
						int maxsize=(int)(files.get(i).length());
						copyProgress[i]=new JProgressBar(0, maxsize);
						copyProgress[i].setStringPainted(true);
						copyProgress[i].setValue(0);
						placeholder[i][1].add(copyProgress[i]);
					}else{
						throw new Exception("Could not open file. File does not exist.");
					}
				}

				JButton okButton=new JButton("Ok");
				JPanel okPanel=new JPanel();
				okPanel.setLayout(new BorderLayout());
				okPanel.add(okButton);

				copyDialog.setTitle("File Transfer Progress");
				copyDialog.getContentPane().add(copyPanel, BorderLayout.CENTER);
				copyDialog.setPreferredSize(new Dimension(600,300));
				copyDialog.setLocationRelativeTo(null);
				copyDialog.setSize(600,300);
				copyDialog.pack();
				copyDialog.toFront();
				copyDialog.setVisible(true);





				/*
				 * Request file transfer and send files
				 */
				for(int i=0;i<filesQuantity;i++){
					String message=NaeglingCom.COM_TYPE.REQUEST_JOB_TRANSFER.getValue()+
							NaeglingCom.MESSAGE_DELIMITER+
							master.getDomain()+
							NaeglingCom.MESSAGE_DELIMITER+
							master.getHypervisor()+
							NaeglingCom.MESSAGE_DELIMITER+
							master.getMac(VirtualNode.MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal())+
							NaeglingCom.MESSAGE_DELIMITER+
							jobName+
							NaeglingCom.MESSAGE_DELIMITER+
							files.get(i).getName();
					Socket s=sendMessageToHostname(message, master.getHost().getHostName(),master.getHost().getNaeglingPort());
					if(s!=null){
						InputStream fis=new FileInputStream(files.get(i));
						int progress=0;
						int read=0;
						byte[] buff=new byte[NaeglingCom.TCP_MESSAGE_MAX_SIZE];
						while((read=fis.read(buff)) >= 0){
							s.getOutputStream().write(buff,0,read);
							++progress;
							copyProgress[i].setValue(progress*(NaeglingCom.TCP_MESSAGE_MAX_SIZE)); 
						}
						s.close();
						fis.close();
						NaeglingPersistency.updateJobFileStatus(files.get(i).getName(),jobName,NaeglingJob.FILE_STATUS.FILE_READY.getValue());
					}
				}
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						copyDialog.dispose();
					}});
				okPanel.add(okButton,BorderLayout.EAST);
				copyDialog.getContentPane().add(okPanel,BorderLayout.SOUTH);
			}
		}catch (Exception ex){
			throw ex;
		}
	}
}
