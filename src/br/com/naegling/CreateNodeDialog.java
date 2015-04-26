package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Font;
import java.awt.Choice;
import java.awt.Color;
import java.awt.TextField;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.ImageIcon;
import java.awt.GridLayout;


/**
 * Class to create a new host using JDialog
 * @author Daniel Yokoyama
 *
 */
public class CreateNodeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup buttonGroupType = new ButtonGroup();
	private JPanel panelTemplates=new JPanel();
	private JTextField textFieldDomain;
	private Label labelCreatedUUID;
	private Label labelCreatedMAC;
	private VirtualNode node=null;
	private JRadioButton radioButtonMaster;
	private JRadioButton radioButtomSlave;
	private String clusterName;
	private Choice choiceHosts;
	private ArrayList<VirtualMachineHost> hosts;
	private ArrayList<Template> templates;
	private Choice choiceHypervisor;
	private Choice choiceBridgeInterface;
	private TextField textFieldMemory;
	private TextField textFieldCPU;
	private TextField textFieldVNC;
	private Choice choiceTemplates;

	/**
	 * Create the dialog.
	 */
	public CreateNodeDialog(ArrayList<VirtualMachineHost> hosts,String clusterName) {
		try {
			templates=NaeglingPersistency.readFromTemplateTable();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		setTitle("Create Node");
		this.setModal(true);
		this.clusterName=clusterName;
		this.hosts=hosts;
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		setBounds(100, 100, (int)(dim.getWidth()*0.8),(int)(dim.getHeight()*0.7));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				final JSplitPane splitPane_1 = new JSplitPane();
				splitPane.setLeftComponent(splitPane_1);
				{
					JPanel panelType = new JPanel();
					splitPane_1.setLeftComponent(panelType);
					panelType.setBorder(new TitledBorder(null, "Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panelType.setLayout(new BorderLayout(0, 0));
					{
						radioButtonMaster = new JRadioButton("Master");
						radioButtonMaster.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								choiceTemplates.setVisible(true);
							}
						});
						buttonGroupType.add(radioButtonMaster);
						panelType.add(radioButtonMaster, BorderLayout.NORTH);
					}
					{
						radioButtomSlave = new JRadioButton("Slave");
						radioButtomSlave.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								choiceTemplates.setVisible(false);
							}
						});
						radioButtomSlave.setSelected(true);
						buttonGroupType.add(radioButtomSlave);
						panelType.add(radioButtomSlave, BorderLayout.SOUTH);
					}
					
					
					splitPane_1.setRightComponent(panelTemplates);
				}
			}
			panelTemplates.setLayout(new GridLayout(0, 1, 0, 0));
			{
				choiceTemplates = new Choice();
				choiceTemplates.setVisible(false);
				panelTemplates.add(choiceTemplates);
				for(Template t: templates){
					choiceTemplates.add(t.getName());
				}
			}
			{
				JPanel panelSettings = new JPanel();
				panelSettings.setBorder(new TitledBorder(null, "Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				splitPane.setRightComponent(panelSettings);
				GridBagLayout gbl_panelSettings = new GridBagLayout();
				gbl_panelSettings.columnWidths = new int[]{20, 115, 454, 176, 131, 0};
				gbl_panelSettings.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
				gbl_panelSettings.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_panelSettings.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				panelSettings.setLayout(gbl_panelSettings);
				{
					Label labelDomain = new Label("Domain Name:");
					labelDomain.setFont(new Font("Dialog", Font.BOLD, 12));
					GridBagConstraints gbc_labelDomain = new GridBagConstraints();
					gbc_labelDomain.anchor = GridBagConstraints.WEST;
					gbc_labelDomain.insets = new Insets(0, 0, 5, 5);
					gbc_labelDomain.gridx = 1;
					gbc_labelDomain.gridy = 1;
					panelSettings.add(labelDomain, gbc_labelDomain);
				}
				{
					textFieldDomain = new JTextField();
					GridBagConstraints gbc_textFieldDomain = new GridBagConstraints();
					gbc_textFieldDomain.fill = GridBagConstraints.HORIZONTAL;
					gbc_textFieldDomain.insets = new Insets(0, 0, 5, 5);
					gbc_textFieldDomain.gridx = 2;
					gbc_textFieldDomain.gridy = 1;
					panelSettings.add(textFieldDomain, gbc_textFieldDomain);
					textFieldDomain.setColumns(10);
				}
				{
					Label labelUUID = new Label("UUID:");
					labelUUID.setFont(new Font("Dialog", Font.BOLD, 12));
					GridBagConstraints gbc_labelUUID = new GridBagConstraints();
					gbc_labelUUID.anchor = GridBagConstraints.WEST;
					gbc_labelUUID.insets = new Insets(0, 0, 5, 5);
					gbc_labelUUID.gridx = 1;
					gbc_labelUUID.gridy = 2;
					panelSettings.add(labelUUID, gbc_labelUUID);
				}
				{
					labelCreatedUUID = new Label("UUID");
					GridBagConstraints gbc_labelCreatedUUID = new GridBagConstraints();
					gbc_labelCreatedUUID.insets = new Insets(0, 0, 5, 5);
					gbc_labelCreatedUUID.gridx = 2;
					gbc_labelCreatedUUID.gridy = 2;
					panelSettings.add(labelCreatedUUID, gbc_labelCreatedUUID);
					genUUID();
				}
				{
					JButton jButtonRefreshUUID = new JButton("");
					jButtonRefreshUUID.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							genUUID();
						}
					});
					jButtonRefreshUUID.setIcon(new ImageIcon(CreateNodeDialog.class.getResource("/images/imageRefresh.png")));
					GridBagConstraints gbc_jButtonRefreshUUID = new GridBagConstraints();
					gbc_jButtonRefreshUUID.anchor = GridBagConstraints.WEST;
					gbc_jButtonRefreshUUID.insets = new Insets(0, 0, 5, 5);
					gbc_jButtonRefreshUUID.gridx = 3;
					gbc_jButtonRefreshUUID.gridy = 2;
					panelSettings.add(jButtonRefreshUUID, gbc_jButtonRefreshUUID);
				}
				{
					Label labelMac = new Label("MAC Address:");
					labelMac.setFont(new Font("Dialog", Font.BOLD, 12));
					GridBagConstraints gbc_labelMac = new GridBagConstraints();
					gbc_labelMac.anchor = GridBagConstraints.WEST;
					gbc_labelMac.insets = new Insets(0, 0, 5, 5);
					gbc_labelMac.gridx = 1;
					gbc_labelMac.gridy = 3;
					panelSettings.add(labelMac, gbc_labelMac);
				}
				{
					labelCreatedMAC = new Label("MAC");
					GridBagConstraints gbc_labelCreatedMAC = new GridBagConstraints();
					gbc_labelCreatedMAC.insets = new Insets(0, 0, 5, 5);
					gbc_labelCreatedMAC.gridx = 2;
					gbc_labelCreatedMAC.gridy = 3;
					panelSettings.add(labelCreatedMAC, gbc_labelCreatedMAC);
					updateLabelCreatedMac();
				}
				{
					JButton jButtonRefreshMac = new JButton("");
					jButtonRefreshMac.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateLabelCreatedMac();
						}
					});
					jButtonRefreshMac.setIcon(new ImageIcon(CreateNodeDialog.class.getResource("/images/imageRefresh.png")));
					GridBagConstraints gbc_jButtonRefreshMac = new GridBagConstraints();
					gbc_jButtonRefreshMac.anchor = GridBagConstraints.WEST;
					gbc_jButtonRefreshMac.insets = new Insets(0, 0, 5, 5);
					gbc_jButtonRefreshMac.gridx = 3;
					gbc_jButtonRefreshMac.gridy = 3;
					panelSettings.add(jButtonRefreshMac, gbc_jButtonRefreshMac);
				}
				{
					Label labelBridgeInterface = new Label("Bridge Interface:");
					labelBridgeInterface.setFont(new Font("Dialog", Font.BOLD, 12));
					GridBagConstraints gbc_labelBridgeInterface = new GridBagConstraints();
					gbc_labelBridgeInterface.anchor = GridBagConstraints.WEST;
					gbc_labelBridgeInterface.insets = new Insets(0, 0, 5, 5);
					gbc_labelBridgeInterface.gridx = 1;
					gbc_labelBridgeInterface.gridy = 4;
					panelSettings.add(labelBridgeInterface, gbc_labelBridgeInterface);
				}
				{
					choiceBridgeInterface = new Choice();
					choiceBridgeInterface.setFont(new Font("Dialog", Font.BOLD, 12));
					choiceBridgeInterface.setForeground(Color.BLACK);
					GridBagConstraints gbc_choiceBridgeInterface = new GridBagConstraints();
					gbc_choiceBridgeInterface.fill = GridBagConstraints.HORIZONTAL;
					gbc_choiceBridgeInterface.insets = new Insets(0, 0, 5, 5);
					gbc_choiceBridgeInterface.gridx = 2;
					gbc_choiceBridgeInterface.gridy = 4;
					panelSettings.add(choiceBridgeInterface, gbc_choiceBridgeInterface);
					{
						Label labelHypervisor = new Label("Hypervisor:");
						labelHypervisor.setFont(new Font("Dialog", Font.BOLD, 12));
						GridBagConstraints gbc_labelHypervisor = new GridBagConstraints();
						gbc_labelHypervisor.anchor = GridBagConstraints.WEST;
						gbc_labelHypervisor.insets = new Insets(0, 0, 5, 5);
						gbc_labelHypervisor.gridx = 1;
						gbc_labelHypervisor.gridy = 5;
						panelSettings.add(labelHypervisor, gbc_labelHypervisor);
					}
					{
						choiceHypervisor = new Choice();
						choiceHypervisor.setFont(new Font("Dialog", Font.BOLD, 12));
						choiceHypervisor.setForeground(Color.BLACK);
						GridBagConstraints gbc_choiceHypervisor = new GridBagConstraints();
						gbc_choiceHypervisor.fill = GridBagConstraints.HORIZONTAL;
						gbc_choiceHypervisor.insets = new Insets(0, 0, 5, 5);
						gbc_choiceHypervisor.gridx = 2;
						gbc_choiceHypervisor.gridy = 5;
						panelSettings.add(choiceHypervisor, gbc_choiceHypervisor);
						{
							Label labelMemory = new Label("Memory(bytes):");
							labelMemory.setFont(new Font("Dialog", Font.BOLD, 12));
							GridBagConstraints gbc_labelMemory = new GridBagConstraints();
							gbc_labelMemory.anchor = GridBagConstraints.WEST;
							gbc_labelMemory.insets = new Insets(0, 0, 5, 5);
							gbc_labelMemory.gridx = 1;
							gbc_labelMemory.gridy = 6;
							panelSettings.add(labelMemory, gbc_labelMemory);
						}
						{
							textFieldMemory = new TextField();
							GridBagConstraints gbc_textFieldMemory = new GridBagConstraints();
							gbc_textFieldMemory.fill = GridBagConstraints.HORIZONTAL;
							gbc_textFieldMemory.insets = new Insets(0, 0, 5, 5);
							gbc_textFieldMemory.gridx = 2;
							gbc_textFieldMemory.gridy = 6;
							panelSettings.add(textFieldMemory, gbc_textFieldMemory);
						}
						{
							Label labelCPU = new Label("CPU:");
							labelCPU.setFont(new Font("Dialog", Font.BOLD, 12));
							GridBagConstraints gbc_labelCPU = new GridBagConstraints();
							gbc_labelCPU.anchor = GridBagConstraints.WEST;
							gbc_labelCPU.insets = new Insets(0, 0, 5, 5);
							gbc_labelCPU.gridx = 1;
							gbc_labelCPU.gridy = 7;
							panelSettings.add(labelCPU, gbc_labelCPU);
						}
						{
							textFieldCPU = new TextField();
							GridBagConstraints gbc_textFieldCPU = new GridBagConstraints();
							gbc_textFieldCPU.fill = GridBagConstraints.HORIZONTAL;
							gbc_textFieldCPU.insets = new Insets(0, 0, 5, 5);
							gbc_textFieldCPU.gridx = 2;
							gbc_textFieldCPU.gridy = 7;
							panelSettings.add(textFieldCPU, gbc_textFieldCPU);
						}
						{
							Label labelHost = new Label("Host:");
							labelHost.setFont(new Font("Dialog", Font.BOLD, 12));
							GridBagConstraints gbc_labelHost = new GridBagConstraints();
							gbc_labelHost.anchor = GridBagConstraints.WEST;
							gbc_labelHost.insets = new Insets(0, 0, 5, 5);
							gbc_labelHost.gridx = 1;
							gbc_labelHost.gridy = 8;
							panelSettings.add(labelHost, gbc_labelHost);
						}
						{
							choiceHosts = new Choice();
							choiceHosts.setFont(new Font("Dialog", Font.BOLD, 12));
							choiceHosts.setForeground(Color.BLACK);
							GridBagConstraints gbc_choiceHosts = new GridBagConstraints();
							gbc_choiceHosts.fill = GridBagConstraints.HORIZONTAL;
							gbc_choiceHosts.insets = new Insets(0, 0, 5, 5);
							gbc_choiceHosts.gridx = 2;
							gbc_choiceHosts.gridy = 8;
							panelSettings.add(choiceHosts, gbc_choiceHosts);
							{
								Label labelVNC = new Label("VNC Port:");
								labelVNC.setFont(new Font("Dialog", Font.BOLD, 12));
								GridBagConstraints gbc_labelVNC = new GridBagConstraints();
								gbc_labelVNC.anchor = GridBagConstraints.WEST;
								gbc_labelVNC.insets = new Insets(0, 0, 0, 5);
								gbc_labelVNC.gridx = 1;
								gbc_labelVNC.gridy = 9;
								panelSettings.add(labelVNC, gbc_labelVNC);
							}
							{
								textFieldVNC = new TextField();
								GridBagConstraints gbc_textFieldVNC = new GridBagConstraints();
								gbc_textFieldVNC.fill = GridBagConstraints.HORIZONTAL;
								gbc_textFieldVNC.insets = new Insets(0, 0, 0, 5);
								gbc_textFieldVNC.gridx = 2;
								gbc_textFieldVNC.gridy = 9;
								panelSettings.add(textFieldVNC, gbc_textFieldVNC);
							}
							for(VirtualMachineHost h: hosts)
								choiceHosts.add(h.getHostName());
						}
						choiceHypervisor.add("qemu:///system");
					}
					choiceBridgeInterface.add("br0");
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
					try{
						int memory=Integer.parseInt(textFieldMemory.getText());
						int cpu=Integer.parseInt(textFieldCPU.getText());
						createNode(memory,cpu);
						CreateNodeDialogClose();
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "Incorrect Input format\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
					public void actionPerformed(ActionEvent e) {
						CreateNodeDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Generate random UUID.
	 */
	private void genUUID(){
		labelCreatedUUID.setText(UUID.randomUUID().toString());
	}
	
	
	/**
	 * update the label labelCreatedMAC
	 */
	private void updateLabelCreatedMac(){
		labelCreatedMAC.setText(genMacKvm());
	}
	
	/**
	 * Generate kvm mac address
	 * @return String - generated MAC address for kvm virtual machine.
	 */
	private String genMacKvm(){
		String mac="52:54:00:";
		mac+=UUID.randomUUID().toString().substring(0, 2)+":";
		mac+=UUID.randomUUID().toString().substring(0, 2)+":";
		mac+=UUID.randomUUID().toString().substring(0, 2);
		return mac;
	}
	
	/**
	 * Create a node
	 * @param memory - node ram memory amount
	 * @param cpu - node CPU core quantity
	 */
	private void createNode(int memory, int cpu){
		if(radioButtomSlave.isSelected()){
			createSlaveNode(memory,cpu);
		}else
			createMasterNode(memory,cpu);
	}		
	
	/**
	 * Create a slave node
	 * @param memory - node ram memory amount
	 * @param cpu - node CPU core quantity
	 */
	private void createSlaveNode(int memory, int cpu){
			String hostname=choiceHosts.getSelectedItem();
			VirtualMachineHost host=null;
			for(VirtualMachineHost h : hosts)
				if(h.getHostName()==hostname)
					host=h;
			node=new VirtualSlaveNode(clusterName,textFieldDomain.getText(),labelCreatedUUID.getText(),labelCreatedMAC.getText(),choiceBridgeInterface.getSelectedItem(),choiceHypervisor.getSelectedItem(),memory,cpu,host,textFieldVNC.getText());
	}
	

	/**
	 * Create a master node
	 * @param memory - node ram memory amount
	 * @param cpu - node CPU core quantity
	 */
	private void createMasterNode(int memory, int cpu){
		try{
			String hostname=choiceHosts.getSelectedItem();
			VirtualMachineHost host=null;
			for(VirtualMachineHost h : hosts)
				if(h.getHostName()==hostname)
					host=h;
			int retval=NaeglingCom.sendMessageToHostname(NaeglingCom.COM_TYPE.TEMPLATE_STATUS.getValue()+NaeglingCom.MESSAGE_DELIMITER+choiceTemplates.getSelectedItem(),host.getHostName(), host.getNaeglingPort());
			System.out.println("retval: "+retval);
			if(retval==1){
				node=new VirtualMasterNode(clusterName,textFieldDomain.getText(),choiceTemplates.getSelectedItem(),null,labelCreatedUUID.getText(),labelCreatedMAC.getText(),choiceBridgeInterface.getSelectedItem(),genMacKvm(),choiceHypervisor.getSelectedItem(),memory,cpu,host,textFieldVNC.getText());
			}else if(retval==0){
				 int copy = JOptionPane.showConfirmDialog(null, "The chosen template does not exist in the host "+host.getHostName()+". Do you wish to copy","alert", JOptionPane.OK_CANCEL_OPTION);
				 if(copy==JOptionPane.OK_OPTION){
					 String templateName=choiceTemplates.getSelectedItem();
					 Template template=null;
						for(Template t : templates)
							if(t.getName()==templateName)
								template=t;
						String message=NaeglingCom.COM_TYPE.REQUEST_TEMPLATE_TRANSFER.getValue()+
								NaeglingCom.MESSAGE_DELIMITER+
								template.getName()+
								NaeglingCom.MESSAGE_DELIMITER+
								template.getMd5();
						int prepared=NaeglingCom.sendMessageToHostname(message, hostname, host.getNaeglingPort());
						if(prepared==0){
							NaeglingFileTransfer sendImage=new NaeglingFileTransfer(host.getHostName(),host.getNaeglingPort(), template.getPath());
							Thread thread=new Thread(sendImage);
							thread.start();
						}
					 
					 //Check again to see if the transfer was successful. The delay is to allow the remote host to rewrite the templates table.
					 //Thread.sleep(1000);
					 //retval=NaeglingCom.sendMessageToHostname(NaeglingCom.COM_TYPE.TEMPLATE_STATUS.getValue()+NaeglingCom.MESSAGE_DELIMITER+choiceTemplates.getSelectedItem(),host.getHostName(), host.getNaeglingPort());
					 //System.out.println("retval="+retval);
					 //if(retval==1)
						// node=new VirtualMasterNode(clusterName,textFieldDomain.getText(),choiceTemplates.getSelectedItem(),null,labelCreatedUUID.getText(),labelCreatedMAC.getText(),choiceBridgeInterface.getSelectedItem(),genMacKvm(),choiceHypervisor.getSelectedItem(),memory,cpu,host,textFieldVNC.getText());
				 }
			}else if(retval==-1){
				JOptionPane.showMessageDialog(null, "Error checking for template on remote host.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Display the dialog 
	 */
	public VirtualNode Display(){
		setVisible(true);
		return node;
	}
	
	/**
	 * Close create node dialog.
	 */
	private void CreateNodeDialogClose(){
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	

}
