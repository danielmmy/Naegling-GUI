package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.border.TitledBorder;
import javax.swing.JToolBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ListSelectionModel;
import javax.swing.ImageIcon;

public class ViewTemplatesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Template> templates;
	private DefaultListModel<String> defaultListModelTemplates;
	private JList<String> listTemplates; 
	private final JPanel contentPanel = new JPanel();
	private JLabel labelName;
	private JLabel labelPath;
	private JLabel labelMd5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewTemplatesDialog dialog = new ViewTemplatesDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewTemplatesDialog() {
		try{
			templates=NaeglingPersistency.readFromTemplateTable();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		defaultListModelTemplates=new DefaultListModel<String>();
		if(!templates.isEmpty())
			for(Template t: templates)
				defaultListModelTemplates.addElement(t.getName());
		setBounds(100, 100, 500, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			contentPanel.add(splitPane);
			{
				JScrollPane scrollPaneTemplates = new JScrollPane();
				scrollPaneTemplates.setViewportBorder(new TitledBorder(null, "Templates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				splitPane.setLeftComponent(scrollPaneTemplates);
				{
					listTemplates = new JList<String>(defaultListModelTemplates);
					listTemplates.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					listTemplates.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							try{
								Template t=NaeglingPersistency.getTemplate((String)listTemplates.getSelectedValue());
								if(t!=null){
									labelName.setText("Name: "+t.getName());
									labelPath.setText("Path: "+t.getPath());
									labelMd5.setText("MD5Sum: "+t.getMd5());
								}else{
									labelName.setText("Name:");
									labelPath.setText("Path:");
									labelMd5.setText("MD5Sum:");
								}
							}catch(Exception ex){
								JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					scrollPaneTemplates.setViewportView(listTemplates);
				}
			}
			{
				JScrollPane scrollPaneDetails = new JScrollPane();
				scrollPaneDetails.setViewportBorder(new TitledBorder(null, "Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				splitPane.setRightComponent(scrollPaneDetails);
				{
					JPanel panelDetails = new JPanel();
					scrollPaneDetails.setViewportView(panelDetails);
					GridBagLayout gbl_panelDetails = new GridBagLayout();
					gbl_panelDetails.columnWidths = new int[]{106, 0};
					gbl_panelDetails.rowHeights = new int[]{0, 0, 0, 0};
					gbl_panelDetails.columnWeights = new double[]{0.0, Double.MIN_VALUE};
					gbl_panelDetails.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
					panelDetails.setLayout(gbl_panelDetails);
					{
						labelName = new JLabel("Name: ");
						GridBagConstraints gbc_labelName = new GridBagConstraints();
						gbc_labelName.insets = new Insets(0, 0, 5, 0);
						gbc_labelName.anchor = GridBagConstraints.WEST;
						gbc_labelName.gridx = 0;
						gbc_labelName.gridy = 0;
						panelDetails.add(labelName, gbc_labelName);
					}
					{
						labelPath = new JLabel("Path:");
						GridBagConstraints gbc_labelPath = new GridBagConstraints();
						gbc_labelPath.insets = new Insets(0, 0, 5, 0);
						gbc_labelPath.anchor = GridBagConstraints.WEST;
						gbc_labelPath.gridx = 0;
						gbc_labelPath.gridy = 1;
						panelDetails.add(labelPath, gbc_labelPath);
					}
					{
						labelMd5 = new JLabel("MD5Sum:");
						GridBagConstraints gbc_labelMd5 = new GridBagConstraints();
						gbc_labelMd5.anchor = GridBagConstraints.WEST;
						gbc_labelMd5.gridx = 0;
						gbc_labelMd5.gridy = 2;
						panelDetails.add(labelMd5, gbc_labelMd5);
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
						viewTemplatesDialogClose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						viewTemplatesDialogClose();
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
				JButton buttonTransfer = new JButton("");
				buttonTransfer.setToolTipText("Transfer template to host");
				buttonTransfer.setIcon(new ImageIcon(ViewTemplatesDialog.class.getResource("/images/imageTransfer.png")));
				buttonTransfer.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try{
							Template template=NaeglingPersistency.getTemplate((String)listTemplates.getSelectedValue());
							if(template!=null){
								File f=new File(template.getPath());
								if(f.exists()){
									String hostname=JOptionPane.showInputDialog("Host: ");
									VirtualMachineHost host=NaeglingPersistency.getHost(hostname);
									String message=NaeglingCom.COM_TYPE.REQUEST_TEMPLATE_TRANSFER.getValue()+
											NaeglingCom.MESSAGE_DELIMITER+
											template.getName()+
											NaeglingCom.MESSAGE_DELIMITER+
											template.getMd5();
									int retval=NaeglingCom.sendMessageToHostname(message, hostname, host.getNaeglingPort());
									if(retval==0){
										NaeglingFileTransfer sendImage=new NaeglingFileTransfer(hostname,host.getNaeglingPort(), template.getPath());
										Thread thread=new Thread(sendImage);
										thread.start();
									}else if(retval==1){
										JOptionPane.showMessageDialog(null, "File transfer not possible. Server busy.", "Server Busy", JOptionPane.PLAIN_MESSAGE);
									}
								}else{
									JOptionPane.showMessageDialog(null, "Could not open file on path: " + f.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				toolBar.add(buttonTransfer);
			}
		}
	}
	
	private void viewTemplatesDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

}
