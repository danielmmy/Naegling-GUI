package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JProgressBar;

public class AddTemplateDialog extends JDialog implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldName;
	private JTextField textFieldPath;
	private JProgressBar progressBar;
	private JLabel labelMD5;
	private JButton okButton;
	private JButton cancelButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddTemplateDialog dialog = new AddTemplateDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddTemplateDialog() {
		final Thread thread=new Thread(this);
		setBounds(100, 100, 449, 197);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{93, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{45, 35, 43, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel labelName = new JLabel("Name");
			labelName.setFont(new Font("Dialog", Font.BOLD, 16));
			GridBagConstraints gbc_labelName = new GridBagConstraints();
			gbc_labelName.insets = new Insets(0, 0, 5, 5);
			gbc_labelName.gridx = 0;
			gbc_labelName.gridy = 0;
			contentPanel.add(labelName, gbc_labelName);
		}
		{
			textFieldName = new JTextField();
			textFieldName.setText("");
			GridBagConstraints gbc_textFieldName = new GridBagConstraints();
			gbc_textFieldName.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldName.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldName.gridx = 1;
			gbc_textFieldName.gridy = 0;
			contentPanel.add(textFieldName, gbc_textFieldName);
			textFieldName.setColumns(10);
		}
		
		JLabel labelPath = new JLabel("Path");
		labelPath.setFont(new Font("Dialog", Font.BOLD, 16));
		GridBagConstraints gbc_labelPath = new GridBagConstraints();
		gbc_labelPath.insets = new Insets(0, 0, 5, 5);
		gbc_labelPath.gridx = 0;
		gbc_labelPath.gridy = 1;
		contentPanel.add(labelPath, gbc_labelPath);
		{
			textFieldPath = new JTextField();
			textFieldPath.setEditable(false);
			GridBagConstraints gbc_textFieldPath = new GridBagConstraints();
			gbc_textFieldPath.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldPath.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldPath.gridx = 1;
			gbc_textFieldPath.gridy = 1;
			contentPanel.add(textFieldPath, gbc_textFieldPath);
			textFieldPath.setColumns(10);
		}
		{
			JButton btnNewButton = new JButton("Find");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc=new JFileChooser();
					int retval=jfc.showOpenDialog(null);
					if (retval == JFileChooser.APPROVE_OPTION) {
				        File file = jfc.getSelectedFile();
				        System.out.println(file.getAbsolutePath() );
				        textFieldPath.setText(file.getAbsolutePath());
				    }
				}
			});
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
			gbc_btnNewButton.gridx = 2;
			gbc_btnNewButton.gridy = 1;
			contentPanel.add(btnNewButton, gbc_btnNewButton);
		}
		{
			labelMD5 = new JLabel("Calculando md5...");
			labelMD5.setEnabled(true);
			labelMD5.setVisible(false);
			GridBagConstraints gbc_labelMD5 = new GridBagConstraints();
			gbc_labelMD5.insets = new Insets(0, 0, 5, 5);
			gbc_labelMD5.gridx = 0;
			gbc_labelMD5.gridy = 2;
			contentPanel.add(labelMD5, gbc_labelMD5);
		}
		{
			progressBar = new JProgressBar();
			progressBar.setEnabled(false);
			GridBagConstraints gbc_progressBar = new GridBagConstraints();
			gbc_progressBar.insets = new Insets(0, 0, 0, 5);
			gbc_progressBar.gridx = 1;
			gbc_progressBar.gridy = 2;
			contentPanel.add(progressBar, gbc_progressBar);
			progressBar.setVisible(false);
			progressBar.setIndeterminate(true);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(!textFieldName.getText().trim().isEmpty()&&!textFieldPath.getText().trim().isEmpty()){
							labelMD5.setVisible(true);
							progressBar.setVisible(true);
							okButton.setEnabled(false);
							cancelButton.setEnabled(false);
							thread.start();			
						}
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addTemplateDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Function to close the dialog
	 */
	private void addTemplateDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

	
	public void run(){
		try {
			String md5=NaeglingCom.getMD5(textFieldPath.getText().trim());
			NaeglingPersistency.insertIntoTamplateTable(new Template(textFieldName.getText().trim(), textFieldPath.getText().trim(), md5));
			JOptionPane.showMessageDialog(this, "Template "+textFieldName.getText().trim()+" successfully added.","Template Added",JOptionPane.PLAIN_MESSAGE);
			addTemplateDialogClose();
		} catch(Exception ex){
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
