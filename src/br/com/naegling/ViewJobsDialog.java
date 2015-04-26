package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;

public class ViewJobsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final java.io.File HOME_DIR= new java.io.File(System.getProperty("user.home"));
	private final JPanel contentPanel = new JPanel();
	private Cluster cluster=null;
	private ArrayList<NaeglingJob> jobs;
	private DefaultListModel<String> defaultListModelJobs;
	private DefaultListModel<String> defaultListModelFiles;
	private JList<String> listJobs;
	private JList<String> listFiles;
	private JLabel labelName;
	private JLabel labelStatus;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewJobsDialog dialog = new ViewJobsDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewJobsDialog(final Cluster cluster) {
		setTitle("Jobs");
		if(cluster!=null){
			this.cluster=cluster;
			try{
				jobs=NaeglingPersistency.getJobs(this.cluster.getName());
				defaultListModelJobs=new DefaultListModel<String>();
				for(NaeglingJob j : jobs){
					defaultListModelJobs.addElement(j.getName());
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else{
			JOptionPane.showMessageDialog(null, "No cluster received.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		setBounds(100, 100, 600, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				JScrollPane scrollPaneJobs = new JScrollPane();
				scrollPaneJobs.setViewportBorder(new TitledBorder(null, "Jobs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				splitPane.setLeftComponent(scrollPaneJobs);
				{
					listJobs = new JList<String>(defaultListModelJobs);
					listJobs.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							
							int idx=listJobs.getSelectedIndex();
							NaeglingJob job=jobs.get(idx);
							if(job!=null){
								defaultListModelFiles=new DefaultListModel<String>();
								ArrayList<File> files=job.getFiles();
								for(File f : files){
									defaultListModelFiles.addElement(f.getName());
								}
								labelName.setText("Name: "+job.getName());
								int i=job.getStatus();
								switch (i) {
								case 0:
									labelStatus.setText("Status: Stoped");
									break;
								case 1:
									labelStatus.setText("Status: Executing");
									break;
								case 2:
									labelStatus.setText("Status: Done");
									break;

								default:
									labelStatus.setText("Status: Unknown");
									break;
								}
							}else{
								listFiles.setModel(null);
								labelName.setText("Name");
								labelStatus.setText("Status");
							}
							listFiles.setModel(defaultListModelFiles);
						}
					});
					scrollPaneJobs.setViewportView(listJobs);
				}
			}
			{
				JSplitPane splitPane_1 = new JSplitPane();
				splitPane_1.setBorder(new TitledBorder(null, "Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				splitPane_1.setResizeWeight(0.5);
				splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
				splitPane.setRightComponent(splitPane_1);
				{
					JPanel panel = new JPanel();
					splitPane_1.setLeftComponent(panel);
					GridBagLayout gbl_panel = new GridBagLayout();
					gbl_panel.columnWidths = new int[]{0, 0};
					gbl_panel.rowHeights = new int[]{0, 0, 0};
					gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
					gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
					panel.setLayout(gbl_panel);
					{
						labelName = new JLabel("Name");
						GridBagConstraints gbc_labelName = new GridBagConstraints();
						gbc_labelName.insets = new Insets(0, 0, 5, 0);
						gbc_labelName.anchor = GridBagConstraints.WEST;
						gbc_labelName.gridx = 0;
						gbc_labelName.gridy = 0;
						panel.add(labelName, gbc_labelName);
					}
					{
						labelStatus = new JLabel("Status");
						GridBagConstraints gbc_labelStatus = new GridBagConstraints();
						gbc_labelStatus.gridx = 0;
						gbc_labelStatus.gridy = 1;
						panel.add(labelStatus, gbc_labelStatus);
					}
				}
				{
					JScrollPane scrollPaneFiles = new JScrollPane();
					scrollPaneFiles.setBorder(new TitledBorder(null, "Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					splitPane_1.setRightComponent(scrollPaneFiles);
					{
						listFiles = new JList<String>();
						scrollPaneFiles.setViewportView(listFiles);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						viewJobsDialogClose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						viewJobsDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JToolBar toolBar = new JToolBar();
			getContentPane().add(toolBar, BorderLayout.NORTH);
			{
				JButton jButtonSendFiles = new JButton("");
				jButtonSendFiles.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						int idx=listJobs.getSelectedIndex();
						NaeglingJobFilesTransfer transfer=new NaeglingJobFilesTransfer(jobs.get(idx).getName(), jobs.get(idx).getFiles(), cluster.getMaster());
						try {
							transfer.sendJobFiles();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				jButtonSendFiles.setIcon(new ImageIcon(ViewJobsDialog.class.getResource("/images/imageTransfer.png")));
				jButtonSendFiles.setToolTipText("Send Files");
				toolBar.add(jButtonSendFiles);
			}
			{
				JButton jButtonStartJob = new JButton("");
				jButtonStartJob.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						NaeglingJob job =jobs.get(listJobs.getSelectedIndex());
						if(job!=null){
							try{
								String message=NaeglingCom.COM_TYPE.EXECUTE_JOB.getValue()+
										NaeglingCom.MESSAGE_DELIMITER+
										cluster.getMaster().getDomain()+
										NaeglingCom.MESSAGE_DELIMITER+
										job.getName()+
										NaeglingCom.MESSAGE_DELIMITER+
										job.getName()+".script";
										
								NaeglingCom.sendMessageToHostname(message, cluster.getMaster().getHost().getHostName(), cluster.getMaster().getHost().getNaeglingPort());
							}catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Error starting job.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
				jButtonStartJob.setIcon(new ImageIcon(ViewJobsDialog.class.getResource("/images/imageStart.png")));
				toolBar.add(jButtonStartJob);
			}
			{
				JButton jButtonDownloadFIle = new JButton("");
				jButtonDownloadFIle.setIcon(new ImageIcon(ViewJobsDialog.class.getResource("/images/imageDownload.png")));
				jButtonDownloadFIle.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						NaeglingJob job =jobs.get(listJobs.getSelectedIndex());
						if(job!=null){
							try{
								String fileName=JOptionPane.showInputDialog("File name");
								fileName=fileName.trim();
								if(!fileName.equalsIgnoreCase("")){

									String message=NaeglingCom.COM_TYPE.DOWNLOAD_JOB_FILE.getValue()+
											NaeglingCom.MESSAGE_DELIMITER+
											cluster.getMaster().getDomain()+
											NaeglingCom.MESSAGE_DELIMITER+
											"/home/naegling/jobs/"+
											job.getName()+
											"/"+
											fileName;
									System.out.println(HOME_DIR.getAbsolutePath()+System.getProperty("file.separator")+"Downloads"+System.getProperty("file.separator")+fileName);
									NaeglingDownloadJobFile download=new NaeglingDownloadJobFile(cluster.getMaster().getHost().getHostName(),cluster.getMaster().getHost().getNaeglingPort(), HOME_DIR.getAbsolutePath()+System.getProperty("file.separator")+"Downloads"+System.getProperty("file.separator")+fileName,message);
									Thread t=new Thread(download);
									t.run();
									
								}
							}catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Error downloadding job file.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
				toolBar.add(jButtonDownloadFIle);
			}
			{
				JButton jButtonAdd = new JButton("");
				jButtonAdd.setIcon(new ImageIcon(ViewJobsDialog.class.getResource("/images/imageAdd.png")));
				toolBar.add(jButtonAdd);
			}
			{
				JButton jButtonDelete = new JButton("");
				jButtonDelete.setIcon(new ImageIcon(ViewJobsDialog.class.getResource("/images/imageRemove.png")));
				toolBar.add(jButtonDelete);
			}
		}
	}
	
	
	private void viewJobsDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

}
