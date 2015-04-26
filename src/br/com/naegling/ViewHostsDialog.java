package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ViewHostsDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<VirtualMachineHost> hosts;
	private DefaultListModel<String> defaultListModelHosts;
	private final JPanel contentPanel = new JPanel();
	private JList<String> listHosts; 
	private JLabel labelHostname;
	private JLabel labelIP;
	private JLabel labelPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewHostsDialog dialog = new ViewHostsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewHostsDialog() {
		try{
			hosts=NaeglingPersistency.readFromVirtualMachineHostTable();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		defaultListModelHosts=new DefaultListModel<String>();
		if(!hosts.isEmpty())
			for(VirtualMachineHost h: hosts)
				defaultListModelHosts.addElement(h.getHostName());
		setBounds(100, 100, 500, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				JPanel panelHosts = new JPanel();
				splitPane.setLeftComponent(panelHosts);
				panelHosts.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					panelHosts.add(scrollPane, BorderLayout.CENTER);
					{
						listHosts = new JList<String>(defaultListModelHosts);
						listHosts.addMouseListener(new MouseAdapter() {
							public void mouseReleased(MouseEvent e) {
								try{
									VirtualMachineHost h=NaeglingPersistency.getHost((String)listHosts.getSelectedValue());
									if(h!=null){
										labelHostname.setText("Hostname: "+h.getHostName());
										labelIP.setText("IP: "+h.getIp());
										labelPort.setText("Port: "+h.getNaeglingPort());
									}else{
										labelHostname.setText("Hostname:");
										labelIP.setText("IP:");
										labelPort.setText("Port:");
									}
								}catch(Exception ex){
									JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						scrollPane.setViewportView(listHosts);
						listHosts.setBorder(new TitledBorder(null, "Hosts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					}
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
					gbl_panelDetails.columnWidths = new int[]{108, 0};
					gbl_panelDetails.rowHeights = new int[]{0, 0, 0, 0};
					gbl_panelDetails.columnWeights = new double[]{0.0, Double.MIN_VALUE};
					gbl_panelDetails.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
					panelDetails.setLayout(gbl_panelDetails);
					{
						labelHostname = new JLabel("Hostname:");
						GridBagConstraints gbc_labelHostname = new GridBagConstraints();
						gbc_labelHostname.anchor = GridBagConstraints.WEST;
						gbc_labelHostname.insets = new Insets(0, 0, 5, 0);
						gbc_labelHostname.gridx = 0;
						gbc_labelHostname.gridy = 0;
						panelDetails.add(labelHostname, gbc_labelHostname);
					}
					{
						labelIP = new JLabel("IP:");
						GridBagConstraints gbc_labelIP = new GridBagConstraints();
						gbc_labelIP.anchor = GridBagConstraints.WEST;
						gbc_labelIP.insets = new Insets(0, 0, 5, 0);
						gbc_labelIP.gridx = 0;
						gbc_labelIP.gridy = 1;
						panelDetails.add(labelIP, gbc_labelIP);
					}
					{
						labelPort = new JLabel("Port:");
						GridBagConstraints gbc_labelPort = new GridBagConstraints();
						gbc_labelPort.anchor = GridBagConstraints.WEST;
						gbc_labelPort.gridx = 0;
						gbc_labelPort.gridy = 2;
						panelDetails.add(labelPort, gbc_labelPort);
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
						viewHostsDialogClose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			JToolBar toolBar = new JToolBar();
			getContentPane().add(toolBar, BorderLayout.NORTH);
			{
				JButton jButtonDelete = new JButton("");
				jButtonDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeHost();
					}
				});
				jButtonDelete.setIcon(new ImageIcon(ViewHostsDialog.class.getResource("/images/imageError.png")));
				toolBar.add(jButtonDelete);
			}
		}
	}

	private void viewHostsDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	/**
	 * Remove host.
	 */
	public void removeHost(){
		if(!listHosts.isSelectionEmpty()){
			int[] idx=listHosts.getSelectedIndices();
			try{
				for(int i=idx.length;i>0;i--){
					NaeglingPersistency.deleteFromVirtualMachineHostTable(hosts.get(idx[i-1]).getHostName());
					defaultListModelHosts.remove(idx[i-1]);
					hosts.remove(idx[i-1]);
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
