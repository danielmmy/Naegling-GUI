package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
/**
 * Class to display the new host dialog
 * @author Daniel Yokoyama
 *
 */
public class AddHostDialog extends JDialog {


	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldHostname;
	private JTextField textFieldIP;
	private JTextField textFieldPort;
	private VirtualMachineHost host;

	/**
	 * Create the dialog.
	 */
	public AddHostDialog() {
		setModal(true);
		setTitle("Add New Host");
		setBounds(100, 100, 354, 216);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{113, 225, 0};
		gbl_contentPanel.rowHeights = new int[]{40, 40, 40, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel labelHostname = new JLabel("Hostname:");
			labelHostname.setFont(new Font("Dialog", Font.BOLD, 16));
			GridBagConstraints gbc_labelHostname = new GridBagConstraints();
			gbc_labelHostname.anchor = GridBagConstraints.WEST;
			gbc_labelHostname.insets = new Insets(0, 0, 5, 5);
			gbc_labelHostname.gridx = 0;
			gbc_labelHostname.gridy = 0;
			contentPanel.add(labelHostname, gbc_labelHostname);
		}
		{
			textFieldHostname = new JTextField();
			textFieldHostname.setText("localhost");
			textFieldHostname.setFont(new Font("Dialog", Font.PLAIN, 16));
			GridBagConstraints gbc_textFieldHostname = new GridBagConstraints();
			gbc_textFieldHostname.fill = GridBagConstraints.BOTH;
			gbc_textFieldHostname.insets = new Insets(0, 0, 5, 0);
			gbc_textFieldHostname.gridx = 1;
			gbc_textFieldHostname.gridy = 0;
			contentPanel.add(textFieldHostname, gbc_textFieldHostname);
			textFieldHostname.setColumns(10);
		}
		{
			JLabel labelIP = new JLabel("IP Address:");
			labelIP.setFont(new Font("Dialog", Font.BOLD, 16));
			GridBagConstraints gbc_labelIP = new GridBagConstraints();
			gbc_labelIP.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelIP.insets = new Insets(0, 0, 5, 5);
			gbc_labelIP.gridx = 0;
			gbc_labelIP.gridy = 1;
			contentPanel.add(labelIP, gbc_labelIP);
		}
		{
			textFieldIP = new JTextField();
			textFieldIP.setText("127.0.0.1");
			textFieldIP.setFont(new Font("Dialog", Font.PLAIN, 16));
			GridBagConstraints gbc_textFieldIP = new GridBagConstraints();
			gbc_textFieldIP.fill = GridBagConstraints.BOTH;
			gbc_textFieldIP.insets = new Insets(0, 0, 5, 0);
			gbc_textFieldIP.gridx = 1;
			gbc_textFieldIP.gridy = 1;
			contentPanel.add(textFieldIP, gbc_textFieldIP);
			textFieldIP.setColumns(10);
		}
		{
			JLabel labelPort = new JLabel("Port#:");
			labelPort.setFont(new Font("Dialog", Font.BOLD, 16));
			GridBagConstraints gbc_labelPort = new GridBagConstraints();
			gbc_labelPort.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelPort.insets = new Insets(0, 0, 0, 5);
			gbc_labelPort.gridx = 0;
			gbc_labelPort.gridy = 2;
			contentPanel.add(labelPort, gbc_labelPort);
		}
		{
			textFieldPort = new JTextField();
			textFieldPort.setText("9292");
			textFieldPort.setFont(new Font("Dialog", Font.PLAIN, 16));
			GridBagConstraints gbc_textFieldPort = new GridBagConstraints();
			gbc_textFieldPort.fill = GridBagConstraints.BOTH;
			gbc_textFieldPort.gridx = 1;
			gbc_textFieldPort.gridy = 2;
			contentPanel.add(textFieldPort, gbc_textFieldPort);
			textFieldPort.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(!textFieldHostname.getText().trim().isEmpty() && !textFieldIP.getText().trim().isEmpty() && !textFieldPort.getText().trim().isEmpty()){
							host=new VirtualMachineHost(textFieldHostname.getText(), textFieldIP.getText(),textFieldPort.getText());
							addHostDialogClose();
						}else
							JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
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
						host=null;
						addHostDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public Object showDialog(){
		setVisible(true);
		return host;
	}
	
	private void addHostDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
}
