package br.com.naegling;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreatingTemplatesHelpDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<File> clusterType;
	private ArrayList<File> OSType;
	private Choice choiceType=new Choice();
	private Choice choiceOSType =new Choice();
	private JSplitPane splitPane;
	private JTextArea textArea;
	private JSplitPane splitPane_1;
	private JButton btnClose;

	public static void main(String [] args){	
		try {
			CreatingTemplatesHelpDialog dialog = new CreatingTemplatesHelpDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public CreatingTemplatesHelpDialog(){
		setBounds(100, 100, 500, 300);
		clusterType=new ArrayList<File>();
		OSType=new ArrayList<File>();
		URL url =this.getClass().getResource("/TemplateCreationHelp");
		System.out.println("url:"+url.getPath());
		File f=new File(url.getPath());
		//enter if outside of jar	
		if(f.isDirectory()){
			File []types=f.listFiles();
			for(File t : types)
				if(t.isDirectory()){
					clusterType.add(t);
				}
		}else{//enter in case of jar
			try {
				CodeSource src = CreatingTemplatesHelpDialog.class.getProtectionDomain().getCodeSource();
				URL jar = src.getLocation();
				ZipInputStream zis= new ZipInputStream(jar.openStream());
				ZipEntry ze=zis.getNextEntry();
				while(ze!=null){
					if(ze.getName().startsWith("TemplateCreationHelp/M")&&ze.isDirectory()){
						System.out.println(ze.getName());
						clusterType.add(new File(ze.getName()));
					}
					ze=zis.getNextEntry();
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		getContentPane().setLayout(new BorderLayout());

		splitPane = new JSplitPane();
		splitPane.setDividerSize(5);
		splitPane.setResizeWeight(0.5);
		getContentPane().add(splitPane,BorderLayout.NORTH);
		
		splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.5);
		splitPane.setLeftComponent(splitPane_1);
		
				choiceType=new Choice();
				splitPane_1.setRightComponent(choiceType);
				
				btnClose = new JButton("Close");
				btnClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						creatingTemplatesHelpDialogClose();
					}
				});
				splitPane_1.setLeftComponent(btnClose);
				choiceOSType =new Choice();
				splitPane.setRightComponent(choiceOSType);
				
						choiceOSType.addItemListener(new ItemListener(){
							public void itemStateChanged(ItemEvent ie){
								updateText();
							}
						});
				
						choiceType.addItemListener(new ItemListener(){
							public void itemStateChanged(ItemEvent ie){
								updateChoiceOSType();
								updateText();
							}
						});

		for(File t : clusterType){
			choiceType.addItem(t.getName());
			System.out.println(t.getName());
		}

		textArea = new JTextArea();
		textArea.setEditable(false);

		JScrollPane scroll = new JScrollPane(textArea);
		getContentPane().add(scroll, BorderLayout.CENTER);


		updateChoiceOSType();
		updateText();




	}


	/*private void CreatingTemplatesHelpDialogClose(){
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}*/

	private void updateChoiceOSType(){
		if(!clusterType.isEmpty()){
			File [] OSFiles=clusterType.get(choiceType.getSelectedIndex()).listFiles();
			choiceOSType.removeAll();
			for(File t : OSFiles){
				OSType.add(t);
				choiceOSType.addItem(t.getName());
			}
		}
	}

	private void updateText(){
		try {
			if(!OSType.isEmpty()){
				System.out.println(OSType.get(choiceOSType.getSelectedIndex()).getAbsolutePath());
				BufferedReader br=new BufferedReader(new FileReader(OSType.get(choiceOSType.getSelectedIndex())));
				String line=null;
				String text="";
				while((line=br.readLine())!=null){
					text+=line+"\n";
				}
				textArea.setText(text);
				textArea.setCaretPosition(0);
				br.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void creatingTemplatesHelpDialogClose() {
        WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

}
