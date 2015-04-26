package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class NaeglingFileTransfer implements Runnable{

	private final String hostname;
	private final String port;
	private final String filePath;

	/**
	 * Transfer file to naegling server in hostname
	 * @param hostname - naegling server host
	 * @param filePath - template image absolute path
	 */
	public NaeglingFileTransfer(String hostname, String port,String filePath) {
		this.hostname=hostname;
		this.port=port;
		this.filePath=filePath;
	}
	


	public void run(){	
		if(hostname!=null&&filePath!=null){
			try {
				JProgressBar copyProgress;
				JPanel copyPanel=new JPanel();
				copyPanel.setLayout(new BorderLayout());
				final JDialog copyDialog;
				JLabel copyLabel=new JLabel("Transfering file...");
				JLabel rateLabel=new JLabel("OMB/s");
				JButton okButton=new JButton("Ok");
				JPanel okPanel=new JPanel();
				okPanel.setLayout(new BorderLayout());
				ImageIcon icon=new ImageIcon(NaeglingGui.class.getResource("/images/imageRunning.png"));
				okPanel.add(new JLabel(icon),BorderLayout.WEST);
				okPanel.add(new JLabel("Transfer completed"),BorderLayout.CENTER);
				int progress=0;
				long start;
				long elapsed;
				Socket tcpSocket;
				InputStream fis;

				File f=new File(filePath);
				int maxsize=(int)(f.length()/1024);
				copyProgress=new JProgressBar(0, maxsize);
				copyProgress.setStringPainted(true);
				copyPanel.add(copyLabel,BorderLayout.NORTH);
				copyPanel.add(copyProgress,BorderLayout.CENTER);
				copyPanel.add(rateLabel,BorderLayout.SOUTH);
				copyDialog=new JDialog();
				copyDialog.setTitle("Image Transfer Progress");
				copyDialog.getContentPane().add(copyPanel, BorderLayout.CENTER);
				copyDialog.pack();
				copyDialog.setPreferredSize(new Dimension(250,60));
				copyDialog.setLocationRelativeTo(null);
				copyDialog.setVisible(true);
				copyDialog.toFront();
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						copyDialog.dispose();
					}});
				okPanel.add(okButton,BorderLayout.EAST);


				fis=new FileInputStream(f);

				int read=0;
				byte[] buff=new byte[NaeglingCom.TCP_MESSAGE_MAX_SIZE];
				while((read=fis.read(buff)) >= 0){
					start=System.currentTimeMillis();
					copyProgress.setValue(progress*(NaeglingCom.TCP_MESSAGE_MAX_SIZE/1024));
					tcpSocket = new Socket(hostname,Integer.parseInt(port));
					tcpSocket.getOutputStream().write(buff,0,read);
					tcpSocket.close();
					++progress;
					elapsed=System.currentTimeMillis();
					rateLabel.setText(String.format("%10.2fMB/s", (NaeglingCom.TCP_MESSAGE_MAX_SIZE/1048576)/((elapsed-start)/1000F)));
					Thread.sleep(1500);
				}
				if(f.length()%NaeglingCom.TCP_MESSAGE_MAX_SIZE==0){
					tcpSocket = new Socket(hostname,Integer.parseInt(port));
					tcpSocket.close();
				}
				fis.close();
				copyPanel.remove(copyProgress);
				copyPanel.add(okPanel,BorderLayout.CENTER);
				copyPanel.revalidate();
			}catch (SocketException ex){
				ex.printStackTrace();
			}catch (Exception ex) {
				ex.printStackTrace();

			}
		}
	}


}
