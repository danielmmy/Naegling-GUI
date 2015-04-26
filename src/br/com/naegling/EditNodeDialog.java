package br.com.naegling;

import javax.swing.JDialog;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import br.com.naegling.NaeglingCom.COM_TYPE;


/**
 * Dialog to change node fields
 */
public class EditNodeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField cpuTextField;
	private JTextField ramTextField;
	private JTextField vncPortTextField;
	private JButton ok;
	private JButton cancel;

	public EditNodeDialog(final VirtualNode node) {
		setModal(true);
		setBounds(100, 100, 300, 200);
		cpuTextField=new JTextField(node.getCpuQuantity()+"");
		ramTextField=new JTextField(node.getRamMemory()+"");
		vncPortTextField= new JTextField(node.getVncPort());
		ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!cpuTextField.getText().trim().isEmpty() && !ramTextField.getText().trim().isEmpty() && !vncPortTextField.getText().trim().isEmpty()){
					if(Integer.parseInt(cpuTextField.getText())!=node.getCpuQuantity()||Integer.parseInt(ramTextField.getText())!=node.getRamMemory()||!(vncPortTextField.getText().equals(node.getVncPort()))){
						try {
							int retval;
							NaeglingPersistency.insertIntoUpdateNodeTask(node.getDomain());
							String message=COM_TYPE.EDIT_NODE.getValue()+
									NaeglingCom.MESSAGE_DELIMITER+
									node.getDomain()+
									NaeglingCom.MESSAGE_DELIMITER+
									node.getHypervisor()+
									NaeglingCom.MESSAGE_DELIMITER+
									ramTextField.getText()+
									NaeglingCom.MESSAGE_DELIMITER+
									cpuTextField.getText()+
									NaeglingCom.MESSAGE_DELIMITER+
									vncPortTextField.getText();
							retval=NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
							if(retval==0){
								if(node.getClass()==VirtualSlaveNode.class){
									NaeglingPersistency.updateVirtualSlaveNodeTableTable(node.getDomain(), Integer.parseInt(cpuTextField.getText()), Integer.parseInt(ramTextField.getText()), vncPortTextField.getText());
								}else{
									NaeglingPersistency.updateVirtualMasterNodeTableTable(node.getDomain(), Integer.parseInt(cpuTextField.getText()), Integer.parseInt(ramTextField.getText()), vncPortTextField.getText());
								}
								NaeglingPersistency.deleteFromUpdateNodeTaskTable(node.getDomain());
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
							
						
						
					}else
						System.out.println("Nothing changed");
					editNodeDialogClose();
				}else
					JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editNodeDialogClose();
			}
		});
		
		setTitle(node.getDomain());
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		getContentPane().add(new JLabel("CPU:"));
		getContentPane().add(cpuTextField);
		getContentPane().add(new JLabel("Memory:"));
		getContentPane().add(ramTextField);
		getContentPane().add(new JLabel("VNC Port:"));
		getContentPane().add(vncPortTextField);
		getContentPane().add(ok);
		getContentPane().add(cancel);
		
		
		
		
	}
	
	public void showDialog(){
		setVisible(true);
		return;
	}
	
	private void editNodeDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

}
