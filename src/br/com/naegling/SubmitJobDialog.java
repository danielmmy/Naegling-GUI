package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComboBox;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class SubmitJobDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldFile;
	private File file=null;
	private JTextField textFieldJobName;
	private ArrayList<File> attachedFiles;
	private DefaultComboBoxModel<File> comboBoxModelAttachedFile;
	private JComboBox<File> comboBoxAttachedFiles;
	private NaeglingJob job=null;
	private Cluster cluster;



	/**
	 * Create the dialog.
	 */
	public SubmitJobDialog(final Cluster cluster) {
		this.cluster=cluster;
		this.setModal(true);
		setTitle("Submit Job");
		attachedFiles=new ArrayList<File>();
		comboBoxModelAttachedFile=new DefaultComboBoxModel<File>();
		setBounds(100, 100, 600, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {70, 600, 350, 446, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JButton jButtonFind = new JButton("");
			jButtonFind.setOpaque(false);
			jButtonFind.setContentAreaFilled(false);
			jButtonFind.setBorderPainted(false);
			jButtonFind.setIcon(new ImageIcon(SubmitJobDialog.class.getResource("/images/imageSearch.png")));
			jButtonFind.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser jfc=new JFileChooser();
					int retval=jfc.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION) {
				        file = jfc.getSelectedFile();
				        textFieldFile.setText(file.getAbsolutePath());
				    }
				}
			});
			{
				JLabel labelJobName = new JLabel("Job Name");
				GridBagConstraints gbc_labelJobName = new GridBagConstraints();
				gbc_labelJobName.insets = new Insets(0, 0, 5, 5);
				gbc_labelJobName.gridx = 0;
				gbc_labelJobName.gridy = 0;
				contentPanel.add(labelJobName, gbc_labelJobName);
			}
			{
				textFieldJobName = new JTextField();
				GridBagConstraints gbc_textFieldJobName = new GridBagConstraints();
				gbc_textFieldJobName.insets = new Insets(0, 0, 5, 5);
				gbc_textFieldJobName.fill = GridBagConstraints.HORIZONTAL;
				gbc_textFieldJobName.gridx = 1;
				gbc_textFieldJobName.gridy = 0;
				contentPanel.add(textFieldJobName, gbc_textFieldJobName);
				textFieldJobName.setColumns(10);
			}
			{
				JLabel labelFile = new JLabel("File");
				GridBagConstraints gbc_labelFile = new GridBagConstraints();
				gbc_labelFile.insets = new Insets(0, 0, 5, 5);
				gbc_labelFile.gridx = 0;
				gbc_labelFile.gridy = 1;
				contentPanel.add(labelFile, gbc_labelFile);
			}
			{
				textFieldFile = new JTextField();
				textFieldFile.setEditable(false);
				GridBagConstraints gbc_textFieldFile = new GridBagConstraints();
				gbc_textFieldFile.insets = new Insets(0, 0, 5, 5);
				gbc_textFieldFile.fill = GridBagConstraints.HORIZONTAL;
				gbc_textFieldFile.gridx = 1;
				gbc_textFieldFile.gridy = 1;
				contentPanel.add(textFieldFile, gbc_textFieldFile);
				textFieldFile.setColumns(10);
			}
			GridBagConstraints gbc_jButtonFind = new GridBagConstraints();
			gbc_jButtonFind.insets = new Insets(0, 0, 5, 5);
			gbc_jButtonFind.gridx = 2;
			gbc_jButtonFind.gridy = 1;
			contentPanel.add(jButtonFind, gbc_jButtonFind);
		}
		{
			JButton jButtonAttachFiles = new JButton("Attach Files");
			jButtonAttachFiles.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser jfc=new JFileChooser();
					int retval=jfc.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION) {
						File f=jfc.getSelectedFile();
				         attachedFiles.add(f);
				        comboBoxModelAttachedFile.addElement(f.getAbsoluteFile());
				    }
				}
			});
			GridBagConstraints gbc_jButtonAttachFiles = new GridBagConstraints();
			gbc_jButtonAttachFiles.insets = new Insets(0, 0, 5, 5);
			gbc_jButtonAttachFiles.gridx = 0;
			gbc_jButtonAttachFiles.gridy = 2;
			contentPanel.add(jButtonAttachFiles, gbc_jButtonAttachFiles);
		}
		{
			comboBoxAttachedFiles = new JComboBox<File>(comboBoxModelAttachedFile);
			comboBoxAttachedFiles.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_DELETE){
						System.out.println("Del");
					}
				}
			});
			GridBagConstraints gbc_comboBoxAttachedFiles = new GridBagConstraints();
			gbc_comboBoxAttachedFiles.insets = new Insets(0, 0, 5, 5);
			gbc_comboBoxAttachedFiles.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxAttachedFiles.gridx = 1;
			gbc_comboBoxAttachedFiles.gridy = 2;
			contentPanel.add(comboBoxAttachedFiles, gbc_comboBoxAttachedFiles);
		}
		{
			JButton jButtonRemoveFile = new JButton("");
			jButtonRemoveFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int idx=comboBoxAttachedFiles.getSelectedIndex();
					if(idx>=0){
						attachedFiles.remove(idx);
						comboBoxModelAttachedFile.removeElementAt(idx);
					}
					
					/*For some reason the last item is not removed from the text field*/
					if(attachedFiles.size()<1)
						comboBoxModelAttachedFile.removeAllElements();
				}
			});
			jButtonRemoveFile.setOpaque(false);
			jButtonRemoveFile.setContentAreaFilled(false);
			jButtonRemoveFile.setBorderPainted(false);
			jButtonRemoveFile.setIcon(new ImageIcon(SubmitJobDialog.class.getResource("/images/imageRemove.png")));
			GridBagConstraints gbc_jButtonRemoveFile = new GridBagConstraints();
			gbc_jButtonRemoveFile.insets = new Insets(0, 0, 5, 5);
			gbc_jButtonRemoveFile.gridx = 2;
			gbc_jButtonRemoveFile.gridy = 2;
			contentPanel.add(jButtonRemoveFile, gbc_jButtonRemoveFile);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(cluster.getHasMasterNode()&&!textFieldJobName.getText().trim().isEmpty()&&file!=null&&file.exists()){
							createJob();
							submitJobDialogClose();
						}
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
						submitJobDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void createJob(){
		try {
			/*
			 * Create job and insert into database
			 */
			job=new NaeglingJob(textFieldJobName.getText().trim(), cluster.getName(), NaeglingJob.JOB_STATUS.JOB_STOPED.getValue());
			NaeglingPersistency.insertIntoJobTable(job);
			job.addFile(file, NaeglingJob.FILE_STATUS.COPYING.getValue(),NaeglingJob.FILE_TYPE.SOURCE.getValue());
			for(File f : attachedFiles){
				job.addFile(f, NaeglingJob.FILE_STATUS.COPYING.getValue(),NaeglingJob.FILE_TYPE.ATTACHED.getValue());
			}
			NaeglingPersistency.insertIntoJobFileTable(job);
			
			
		}catch(Exception ex){
			job=null;
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);				
		}
	}
	
	/**
	 * Function to close the dialog
	 */
	private void submitJobDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	public NaeglingJob display(){
		setVisible(true);
		return job;
	}

}
