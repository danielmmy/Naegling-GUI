package br.com.naegling;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;

public class BuildExecScriptDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextPane textPaneScript;
	private JCheckBox checkBoxEditScript;
	private JTextField textFieldNumberOfProcesses;
	private int numberOfProcesses=0;
	private String binaryFile="";
	private final NaeglingJob job;
	private JComboBox<Object> comboBoxShellType;
	private JComboBox<String> comboBoxSourceFile;
	private JComboBox<Object> comboBoxCompiler;
	private JTextField textFieldOutputFile;
	private File script=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NaeglingJob job=new NaeglingJob("Teste", "teste", 1);
			job.addFile(new File("/home/daniel/copy.c"), 1, 1);
			job.addFile(new File("/home/daniel/a.out"),1,2);
			BuildExecScriptDialog dialog = new BuildExecScriptDialog(job);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public BuildExecScriptDialog(final NaeglingJob job) throws Exception{
		if(job!=null){
			this.job=job;
		}else{
			throw new Exception("Error, Empty Job");
		}
		binaryFile=job.getName();
		
		setModal(true);
		setTitle("Build Execution Script");
		setBounds(100, 100, 800, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setResizeWeight(0.5);
			splitPane.setDividerLocation(0.5);
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				textPaneScript = new JTextPane();
				textPaneScript.setEditable(false);
				splitPane.setRightComponent(textPaneScript);
			}
			{
				JPanel panel = new JPanel();
				splitPane.setLeftComponent(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{0, 0, 0};
				gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
				gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					checkBoxEditScript = new JCheckBox("Edit Script");
					GridBagConstraints gbc_checkBoxEditScript = new GridBagConstraints();
					gbc_checkBoxEditScript.anchor = GridBagConstraints.WEST;
					gbc_checkBoxEditScript.insets = new Insets(0, 0, 5, 5);
					gbc_checkBoxEditScript.gridx = 0;
					gbc_checkBoxEditScript.gridy = 0;
					panel.add(checkBoxEditScript, gbc_checkBoxEditScript);
				}
				{
					JLabel labelShellEnvironment = new JLabel("Shell Environment");
					GridBagConstraints gbc_labelShellEnvironment = new GridBagConstraints();
					gbc_labelShellEnvironment.anchor = GridBagConstraints.WEST;
					gbc_labelShellEnvironment.insets = new Insets(0, 0, 5, 5);
					gbc_labelShellEnvironment.gridx = 0;
					gbc_labelShellEnvironment.gridy = 1;
					panel.add(labelShellEnvironment, gbc_labelShellEnvironment);
				}
				{
					comboBoxShellType = new JComboBox<Object>();
					comboBoxShellType.setModel(new DefaultComboBoxModel<Object>(new String[] {"bash", "csh"}));
					comboBoxShellType.setSelectedIndex(0);
					comboBoxShellType.setPreferredSize(new Dimension(250, 24));
					GridBagConstraints gbc_comboBoxShellType = new GridBagConstraints();
					gbc_comboBoxShellType.anchor = GridBagConstraints.WEST;
					gbc_comboBoxShellType.insets = new Insets(0, 0, 5, 0);
					gbc_comboBoxShellType.gridx = 1;
					gbc_comboBoxShellType.gridy = 1;
					panel.add(comboBoxShellType, gbc_comboBoxShellType);
				}
				{
					JLabel labelCompiler = new JLabel("Compiler");
					GridBagConstraints gbc_labelCompiler = new GridBagConstraints();
					gbc_labelCompiler.anchor = GridBagConstraints.WEST;
					gbc_labelCompiler.insets = new Insets(0, 0, 5, 5);
					gbc_labelCompiler.gridx = 0;
					gbc_labelCompiler.gridy = 2;
					panel.add(labelCompiler, gbc_labelCompiler);
				}
				{
					comboBoxCompiler = new JComboBox<Object>();
					comboBoxCompiler.setModel(new DefaultComboBoxModel<Object>(new String[] {"mpicc", "mpixx", "mpic++", "mpif77", "mpif90"}));
					comboBoxCompiler.setSelectedIndex(0);
					comboBoxCompiler.setPreferredSize(new Dimension(250, 24));
					GridBagConstraints gbc_comboBoxCompiler = new GridBagConstraints();
					gbc_comboBoxCompiler.anchor = GridBagConstraints.WEST;
					gbc_comboBoxCompiler.insets = new Insets(0, 0, 5, 0);
					gbc_comboBoxCompiler.gridx = 1;
					gbc_comboBoxCompiler.gridy = 2;
					panel.add(comboBoxCompiler, gbc_comboBoxCompiler);
				}
				{
					JLabel labelSourceFile = new JLabel("Source FIle");
					GridBagConstraints gbc_labelSourceFile = new GridBagConstraints();
					gbc_labelSourceFile.anchor = GridBagConstraints.WEST;
					gbc_labelSourceFile.insets = new Insets(0, 0, 5, 5);
					gbc_labelSourceFile.gridx = 0;
					gbc_labelSourceFile.gridy = 3;
					panel.add(labelSourceFile, gbc_labelSourceFile);
				}
				{
					int fileQuantity=job.getFiles().size();
					DefaultComboBoxModel<String> model=new DefaultComboBoxModel<String>();
					for(int i=0;i<fileQuantity;i++)
						model.addElement(job.getFiles().get(i).getName());
					comboBoxSourceFile = new JComboBox<String>(model);
					comboBoxSourceFile.setPreferredSize(new Dimension(250, 24));
					comboBoxSourceFile.setMinimumSize(new Dimension(100, 24));
					comboBoxSourceFile.setSelectedIndex(0);
					GridBagConstraints gbc_comboBoxSourceFile = new GridBagConstraints();
					gbc_comboBoxSourceFile.insets = new Insets(0, 0, 5, 0);
					gbc_comboBoxSourceFile.anchor = GridBagConstraints.WEST;
					gbc_comboBoxSourceFile.gridx = 1;
					gbc_comboBoxSourceFile.gridy = 3;
					panel.add(comboBoxSourceFile, gbc_comboBoxSourceFile);
				}
				{
					JLabel lableNumberOfProcesses = new JLabel("Number of Processes");
					GridBagConstraints gbc_lableNumberOfProcesses = new GridBagConstraints();
					gbc_lableNumberOfProcesses.anchor = GridBagConstraints.EAST;
					gbc_lableNumberOfProcesses.insets = new Insets(0, 0, 5, 5);
					gbc_lableNumberOfProcesses.gridx = 0;
					gbc_lableNumberOfProcesses.gridy = 4;
					panel.add(lableNumberOfProcesses, gbc_lableNumberOfProcesses);
				}
				{
					textFieldNumberOfProcesses = new JTextField();
					textFieldNumberOfProcesses.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					textFieldNumberOfProcesses.setSize(new Dimension(250, 0));
					textFieldNumberOfProcesses.setMinimumSize(new Dimension(250, 19));
					textFieldNumberOfProcesses.setPreferredSize(new Dimension(250, 19));
					GridBagConstraints gbc_textFieldNumberOfProcesses = new GridBagConstraints();
					gbc_textFieldNumberOfProcesses.insets = new Insets(0, 0, 5, 0);
					gbc_textFieldNumberOfProcesses.anchor = GridBagConstraints.WEST;
					gbc_textFieldNumberOfProcesses.gridx = 1;
					gbc_textFieldNumberOfProcesses.gridy = 4;
					panel.add(textFieldNumberOfProcesses, gbc_textFieldNumberOfProcesses);
					textFieldNumberOfProcesses.setColumns(10);
				}
				{
					JLabel labelOutputFile = new JLabel("Output File");
					GridBagConstraints gbc_labelOutputFile = new GridBagConstraints();
					gbc_labelOutputFile.anchor = GridBagConstraints.WEST;
					gbc_labelOutputFile.insets = new Insets(0, 0, 5, 5);
					gbc_labelOutputFile.gridx = 0;
					gbc_labelOutputFile.gridy = 5;
					panel.add(labelOutputFile, gbc_labelOutputFile);
				}
				{
					textFieldOutputFile = new JTextField();
					GridBagConstraints gbc_textFieldOutputFile = new GridBagConstraints();
					gbc_textFieldOutputFile.anchor = GridBagConstraints.WEST;
					gbc_textFieldOutputFile.insets = new Insets(0, 0, 5, 0);
					gbc_textFieldOutputFile.gridx = 1;
					gbc_textFieldOutputFile.gridy = 5;
					panel.add(textFieldOutputFile, gbc_textFieldOutputFile);
					textFieldOutputFile.setColumns(10);
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
					public void actionPerformed(ActionEvent arg0) {
						script=new File(NaeglingPersistency.getJobDirectoryPath()+System.getProperty("file.separator")+job.getName()+System.getProperty("file.separator")+job.getName()+".script");
						try {
							BufferedWriter output=new BufferedWriter(new FileWriter(script));
							output.write(textPaneScript.getText());
							output.close();
							buildExecScriptDialogClose();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Error Creating job script.","Error Creating Script",JOptionPane.ERROR_MESSAGE);
							script=null;
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
						buildExecScriptDialogClose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		textFieldOutputFile.setText(binaryFile+".out");
		updateText();
		
		/*
		 * Listeners
		 */
		comboBoxShellType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateText();
			}
		});
		
		textFieldNumberOfProcesses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String text=textFieldNumberOfProcesses.getText().trim();
					int number=Integer.parseInt(text);
					numberOfProcesses=number;
					updateText();
				}catch(NumberFormatException ex){
					textFieldNumberOfProcesses.setText(""+numberOfProcesses);
				}
			}
		});
		
		checkBoxEditScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textPaneScript.setEditable(checkBoxEditScript.isSelected());
			}
		});
		
		comboBoxCompiler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateText();
			}
		});
		
		comboBoxSourceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateText();
			}
		});
		
		textFieldOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateText();
			}
		});
	}

	/**
	 * Update Text Script
	 */
	private void updateText() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal= Calendar.getInstance();
		String text=String.format("#%s Execution Script\n"+
								  "#Script build:%s\n"+
								  "#!/bin/%s\n"+
								  "/opt/mpich2/bin/%s %s -o %s\n"+
								  "/opt/mpich2/bin/mpirun -n %d -f machinefile ./%s > %s\n"
								  ,job.getName(), dateFormat.format(cal.getTime()),comboBoxShellType.getSelectedItem().toString(), comboBoxCompiler.getSelectedItem().toString(), comboBoxSourceFile.getSelectedItem().toString(), binaryFile,numberOfProcesses, binaryFile, textFieldOutputFile.getText().trim());
		textPaneScript.setText(text);
		textFieldOutputFile.setText(textFieldOutputFile.getText().trim());
	}

	
	private void buildExecScriptDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	public File display(){
		setVisible(true);
		return script;
	}

}
