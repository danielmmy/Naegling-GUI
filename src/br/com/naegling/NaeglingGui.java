package br.com.naegling;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;
import javax.swing.ListSelectionModel;
import javax.swing.JSeparator;
import br.com.naegling.NaeglingCom.COM_TYPE;
import br.com.naegling.VirtualNode.MAC_TYPE;
import java.awt.ComponentOrientation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Naegling GUI main window 
 * @author Daniel Yokoyama
 *
 */
public class NaeglingGui {

	private JFrame frmNaegling;
	private DefaultListModel<String> defaultListModelCluster;
	private ArrayList<Cluster> clusterList;
	private JTable table;
	private JList<String> listCluster;
	private JScrollPane scrollPane;


	class StatusWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			updateStatus();
			return null;
		}

		private void updateStatus(){
			int count=table.getRowCount();
    		for(int i=0;i<count;i++){
    			try{
    				String message=COM_TYPE.NODE_STATUS.getValue()+
    						NaeglingCom.MESSAGE_DELIMITER+
    						table.getValueAt(i,0).toString()+
    						NaeglingCom.MESSAGE_DELIMITER+
    						table.getValueAt(i,2).toString();
    				String hostname=table.getValueAt(i,5).toString();
    				String naeglingPort="";
    				VirtualMachineHost h=NaeglingPersistency.getHost(hostname);
    				if(h!=null)
    					naeglingPort=h.getNaeglingPort();
    				if(naeglingPort.equals(""))
    					throw new Exception();
    				int retval=NaeglingCom.sendMessageToHostname(message,hostname,naeglingPort);
    				switch(retval){
    				case 0:
    					//table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imagePoweroff.png")), i, 7);
    					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imagePoweroff.png")), i, 7);
    					break;
    				case 1:
    					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageRunning.png")), i, 7);
    					break;
    				default:
    					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageError.png")), i, 7);

    				}
    			}catch(Exception e){
    				table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageError.png")), i, 7);
    			}
    		}
    	}
	}
	
	
	/**
	 * Class to create a menu context with right click on table
	 * @author daniel
	 *
	 */
	class TablePopUpMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JMenuItem itemEditNode;
		public TablePopUpMenu(){
			itemEditNode = new JMenuItem("Edit Node");
			itemEditNode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {


					ArrayList<VirtualNode> nodes=getSelectedNodes();
					for(VirtualNode node : nodes){
						try {
		    				String message=COM_TYPE.NODE_STATUS.getValue()+
		    						NaeglingCom.MESSAGE_DELIMITER+
		    						node.getDomain()+
		    						NaeglingCom.MESSAGE_DELIMITER+
		    						node.getHypervisor();
		    				int retval=NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
		    				if(retval==0){
		    					EditNodeDialog dialog = new EditNodeDialog(node);
		    					dialog.showDialog();
		    					clusterList=NaeglingPersistency.readFromClusterTable();							
		    					updateNodesTable();
		    					updateStatus();
		    				}else{
		    					JOptionPane.showMessageDialog(null, "Domain "+node.getDomain()+" is on.\nPlease turn it off and try again.","Error, Can Not Edit",JOptionPane.ERROR_MESSAGE);
		    				}

						}catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			add(itemEditNode);
		}
	}

	/**
	 * Naegling GUI main method. Launch the application.
	 */
	public static void main(String[] args) {
		/*
		 * Create Database
		 */
		try{
			NaeglingPersistency.checkDatabases();
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NaeglingGui window = new NaeglingGui();
					window.frmNaegling.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
		 * Create Jobs directory
		 */
		NaeglingPersistency.checkJobsDirectory();
		
	}
	


	/**
	 * Create the application.
	 */
	public NaeglingGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		try{
			clusterList=NaeglingPersistency.readFromClusterTable();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		frmNaegling = new JFrame();
		frmNaegling.setTitle("Naegling");
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		frmNaegling.setBounds(100, 100,(int)( dim.getWidth()*0.8), (int)(dim.getHeight()*0.8));
		frmNaegling.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNaegling.getContentPane().setLayout(new MigLayout("", "[][1524px,grow]", "[48px][][][grow]"));
		
		JToolBar toolBar = new JToolBar();
		frmNaegling.getContentPane().add(toolBar, "cell 1 0,grow");
		
		JButton jButtonNewCluster = new JButton("");
		jButtonNewCluster.setToolTipText("Create New Cluster");
		jButtonNewCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCluster();
			}
		});
		jButtonNewCluster.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageNewCluster.png")));
		toolBar.add(jButtonNewCluster);
		
		JButton jButtonAddNode = new JButton("");
		jButtonAddNode.setToolTipText("Add Node");
		jButtonAddNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNode();
			}
		});
		
		JButton jButtonAddHost = new JButton("");
		jButtonAddHost.setToolTipText("Add Host");
		jButtonAddHost.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageNewHost.png")));
		jButtonAddHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addHost();
			}
		});
		toolBar.add(jButtonAddHost);
		jButtonAddNode.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageNewNode.png")));
		toolBar.add(jButtonAddNode);
		
		JButton jButtonStartNode = new JButton("");
		jButtonStartNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final ArrayList<VirtualNode> nodes=getSelectedNodes();
				final Cluster c=getCurrentSelectedCluster();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							for(VirtualNode n: nodes){
								if(n.getClass().getName().equals("br.com.naegling.VirtualSlaveNode")){
									try{
										String message=NaeglingCom.COM_TYPE.ADD_WORKING_NODE.getValue()+
												NaeglingCom.MESSAGE_DELIMITER+
												n.getDomain()+
												NaeglingCom.MESSAGE_DELIMITER+
												n.getMac(VirtualNode.MAC_TYPE.BRIDGE.ordinal())+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getDomain()+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getHypervisor()+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getMac(VirtualNode.MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal());
										int retval=NaeglingCom.sendMessageToHostname(message, c.getMaster().getHost().getHostName(), c.getMaster().getHost().getNaeglingPort());
										if(retval==0)
											n.start();
									}catch(Exception ex){
										JOptionPane.showMessageDialog(null, "Error starting node.\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
									}
								}else{
									n.start();
								}
								updateStatus();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}
				}).start();

			}

		});
		jButtonStartNode.setToolTipText("Start Node");
		jButtonStartNode.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageStart.png")));
		toolBar.add(jButtonStartNode);
		
		JButton jButtonOpenGraphics = new JButton("");
		jButtonOpenGraphics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<VirtualNode> nodes=getSelectedNodes();
				/*
				for(VirtualNode n: nodes){
					n.openGraphics();
				}*/
				int i=table.getSelectedRow();
				try{
					String message=COM_TYPE.NODE_STATUS.getValue()+
							NaeglingCom.MESSAGE_DELIMITER+
							table.getValueAt(i,0).toString()+
							NaeglingCom.MESSAGE_DELIMITER+
							table.getValueAt(i,2).toString();
					String hostname=table.getValueAt(i,5).toString();
					String naeglingPort="";
					VirtualMachineHost h=NaeglingPersistency.getHost(hostname);
					naeglingPort=h.getNaeglingPort();
					if(naeglingPort.equals(""))
						throw new Exception();
					int retval=NaeglingCom.sendMessageToHostname(message,hostname,naeglingPort);
					switch(retval){
					case 0:
						JOptionPane.showMessageDialog(null, "Machine state is Poweroff. Can not use VNC.", "Error", JOptionPane.ERROR_MESSAGE);
						break;
					case 1:
						nodes.get(0).openGraphics();
						break;
					default:
						JOptionPane.showMessageDialog(null, "Can't use VNC", "Error", JOptionPane.ERROR_MESSAGE);

					}
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		jButtonOpenGraphics.setToolTipText("Open Graphical Interface");
		jButtonOpenGraphics.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageOpenGraphics.png")));
		toolBar.add(jButtonOpenGraphics);
		
		JButton jButtonStopNode = new JButton("");
		jButtonStopNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final ArrayList<VirtualNode> nodes=getSelectedNodes();
				final Cluster c=getCurrentSelectedCluster();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							for(VirtualNode n: nodes){
								n.stop();
								if(n.getClass().getName().equals("br.com.naegling.VirtualSlaveNode")){
									try{
										String message=NaeglingCom.COM_TYPE.REMOVE_WORKING_NODE.getValue()+
												NaeglingCom.MESSAGE_DELIMITER+
												n.getDomain()+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getDomain()+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getHypervisor()+
												NaeglingCom.MESSAGE_DELIMITER+
												c.getMaster().getMac(VirtualNode.MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal());
										int retval=NaeglingCom.sendMessageToHostname(message, c.getMaster().getHost().getHostName(), c.getMaster().getHost().getNaeglingPort());
										System.out.println(retval);
										if(retval!=0){
											JOptionPane.showMessageDialog(null, "Error undefining node from cluster.", "Error", JOptionPane.ERROR_MESSAGE);
										}
									}catch(Exception ex){
										JOptionPane.showMessageDialog(null, "Error stoping node.\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
									}
								}
								updateStatus();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}
				}).start();
			}
		});
		jButtonStopNode.setToolTipText("Stop Node");
		jButtonStopNode.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageStop.png")));
		toolBar.add(jButtonStopNode);
		
		JButton jButtonDestroyCluster = new JButton("");
		jButtonDestroyCluster.setToolTipText("Destroy Cluster");
		jButtonDestroyCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				destroyCluster();
			}
		});
		jButtonDestroyCluster.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageDestroyCluster.png")));
		toolBar.add(jButtonDestroyCluster);
		
		JButton jButtonRemoveHost = new JButton("");
		jButtonRemoveHost.setToolTipText("Remove Host");
		jButtonRemoveHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewHosts();
			}
		});
		jButtonRemoveHost.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageRemoveHost.png")));
		toolBar.add(jButtonRemoveHost);
		
		JButton jButtonRemoveNode = new JButton("");
		jButtonRemoveNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<VirtualNode> nodes=getSelectedNodes();
				Cluster c=getCurrentSelectedCluster();
				int res=JOptionPane.showConfirmDialog(null, "Delete selected nodes?");
				if(res==JOptionPane.YES_OPTION){
				c.removeNodes(nodes);
				//getCurrentSelectedCluster().removeNodes(getSelectedNodes());
				try{
					clusterList=NaeglingPersistency.readFromClusterTable();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				updateNodesTable();
				updateStatus();
				}
			}
		});
		jButtonRemoveNode.setToolTipText("Delete Node");
		jButtonRemoveNode.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageRemoveNode.png")));
		toolBar.add(jButtonRemoveNode);
		
		JButton jButtonSubmitJob = new JButton("");
		jButtonSubmitJob.setIcon(new ImageIcon(NaeglingGui.class.getResource("/images/imageJob.png")));
		jButtonSubmitJob.setToolTipText("Submit Job");
		jButtonSubmitJob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cluster c=getCurrentSelectedCluster();
				if(c.getMaster().getNodeStatus()==1){
					NaeglingJob job;
					SubmitJobDialog jobDialog=new SubmitJobDialog(c);
					job=jobDialog.display();
					if(job!=null){
						NaeglingPersistency.createJobDirectory(job.getName());
						try {
							BuildExecScriptDialog execScript=new BuildExecScriptDialog(job);
							File f=execScript.display();
							if(f!=null){
								job.addFile(f, NaeglingJob.FILE_STATUS.COPYING.getValue(), NaeglingJob.FILE_TYPE.SCRIPT.getValue());
								NaeglingPersistency.insertIntoJobFileTable(f, job,NaeglingJob.FILE_STATUS.COPYING.getValue(), NaeglingJob.FILE_TYPE.SCRIPT.getValue());
							}
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(),  "Submit job failed",JOptionPane.ERROR_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Error Creating job.",  "Submit job failed",JOptionPane.ERROR_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(null, "Can not send job.\nCluster is powered off.",  "Submit job failed",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		toolBar.add(jButtonSubmitJob);
		
		JSplitPane splitPane = new JSplitPane();
		frmNaegling.getContentPane().add(splitPane, "cell 1 3,grow");
		
		defaultListModelCluster=new DefaultListModel<String>();
		if(!clusterList.isEmpty())
			for(Cluster c: clusterList)
				defaultListModelCluster.addElement(c.getName());
		
		
		JPanel panelNodes = new JPanel();
		panelNodes.setPreferredSize(new Dimension(80, 10));
		panelNodes.setBorder(new TitledBorder(null, "Nodes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNodes.setBackground(Color.WHITE);
		splitPane.setRightComponent(panelNodes);
		panelNodes.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		panelNodes.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON3){
					TablePopUpMenu menu = new TablePopUpMenu();
			        menu.show(e.getComponent(), e.getX(), e.getY());
			        
			        // get the coordinates of the mouse click
					Point p = e.getPoint();
		 
					// get the row index that contains that coordinate
					int rowNumber = table.rowAtPoint( p );
		 
					// Get the ListSelectionModel of the JTable
					ListSelectionModel model = table.getSelectionModel();
		 
					// set the selected interval of rows. Using the "rowNumber"
					// variable for the beginning and end selects only that one row.
					model.setSelectionInterval( rowNumber, rowNumber );
				}
				
			}
		});
		scrollPane.setViewportView(table);
		
		JPanel panelCluster = new JPanel();
		panelCluster.setPreferredSize(new Dimension(150, 0));
		splitPane.setLeftComponent(panelCluster);
		panelCluster.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneCluster = new JScrollPane();
		panelCluster.add(scrollPaneCluster, BorderLayout.CENTER);
		
		listCluster = new JList<String>(defaultListModelCluster);
		scrollPaneCluster.setViewportView(listCluster);
		listCluster.setBorder(new TitledBorder(null, "Clusters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listCluster.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				updateNodesTable();
				StatusWorker statusWorker=new StatusWorker();
				statusWorker.execute();
			}
				
		});
		listCluster.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		listCluster.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		
		JMenuBar menuBar = new JMenuBar();
		frmNaegling.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem menuItemNewCluster = new JMenuItem("New Cluster");
		menuItemNewCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createCluster();
			}
		});
		menuFile.add(menuItemNewCluster);
		
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				naeglingGuiClose();
			}
		});
		
		JMenuItem menuItemAddNode = new JMenuItem("Add Node");
		menuItemAddNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createNode();
			}
		});
		
		JMenuItem menuItemAddHost = new JMenuItem("Add Host");
		menuItemAddHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addHost();
			}
		});
		menuFile.add(menuItemAddHost);
		menuFile.add(menuItemAddNode);
		
		JMenuItem menuItemAddTemplate = new JMenuItem("Add Template");
		menuItemAddTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddTemplateDialog templateDialog=new AddTemplateDialog();
				templateDialog.setVisible(true);
			}
		});
		menuFile.add(menuItemAddTemplate);
		
		JSeparator separator_1 = new JSeparator();
		menuFile.add(separator_1);
		
		JMenuItem menuItemDestroyCluster = new JMenuItem("Destroy Cluster");
		menuItemDestroyCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				destroyCluster();
			}
		});
		menuFile.add(menuItemDestroyCluster);
		
		JMenuItem menuItemRemoveHost = new JMenuItem("Remove Host");
		menuItemRemoveHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				viewHosts();
			}
		});
		menuFile.add(menuItemRemoveHost);
		
		JMenuItem menuItemRemoveNode = new JMenuItem("Remove Node");
		menuFile.add(menuItemRemoveNode);
		
		JSeparator separator = new JSeparator();
		menuFile.add(separator);
		menuFile.add(menuItemExit);
		
		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);
		
		JMenuItem menuItemViewHosts = new JMenuItem("View Hosts");
		menuItemViewHosts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				viewHosts();
			}
		});
		menuView.add(menuItemViewHosts);
		
		JMenuItem menuItemViewTemplates = new JMenuItem("View Templates");
		menuItemViewTemplates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				viewTemplates();
			}
		});
		menuView.add(menuItemViewTemplates);
		
		JMenuItem menuItemViewJobs = new JMenuItem("View Jobs");
		menuItemViewJobs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewJobs();
			}
		});
		menuView.add(menuItemViewJobs);
		
		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		
		JMenuItem menuItemHelpCreatingTemplates = new JMenuItem("Creating Templates");
		menuItemHelpCreatingTemplates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					 CreatingTemplatesHelpDialog dialog = new CreatingTemplatesHelpDialog();
					 dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					 dialog.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		menuHelp.add(menuItemHelpCreatingTemplates);
	}
	


	/**
	 * Close NaeglingGUI.
	 */
	private void naeglingGuiClose() {
        WindowEvent wev = new WindowEvent(frmNaegling,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	
	/**
	 * Create a new cluster.
	 */
	private void createCluster(){
		String name = JOptionPane.showInputDialog(frmNaegling, "Cluster name?");
		if(name!=null){
			boolean exists=false;
			for(Cluster c: clusterList){
				if(name.equalsIgnoreCase(c.getName()))
					exists=true;
			}
			if(!exists){
				try{
					Cluster newCluster=new Cluster(name);
					NaeglingPersistency.insertIntoClusterTable(newCluster);
					clusterList.add(newCluster);
					defaultListModelCluster.addElement(newCluster.getName());
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}else{
				JOptionPane.showMessageDialog(null, "Cluster already exists.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	/**
	 * Create a new Node.
	 */
	private void createNode(){
		ArrayList<VirtualMachineHost> hosts=null;
		try{
			hosts=NaeglingPersistency.readFromVirtualMachineHostTable();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		if(!listCluster.isSelectionEmpty()&&!hosts.isEmpty()){
			int idx=listCluster.getSelectedIndex();
			CreateNodeDialog createNodeDialog=new CreateNodeDialog(hosts,clusterList.get(idx).getName());
			VirtualNode node=createNodeDialog.Display();
			if(node==null){
				
			}
			else if(node.getClass().toString().contains("VirtualSlaveNode")){
				try{
					String message=COM_TYPE.CREATE_SLAVE_NODE.getValue()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getDomain()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getUuid()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getMac(MAC_TYPE.BRIDGE.ordinal())+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getBridgeNetworkInterface()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getHypervisor()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getRamMemory()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getCpuQuantity()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getVncPort();
					
					NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
					NaeglingPersistency.insertIntoVirtualSlaveNodeTable((VirtualSlaveNode)node);
					clusterList.get(idx).addSlave((VirtualSlaveNode)node);
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else if(node.getClass().toString().contains("VirtualMasterNode")){
				try{
					String message=COM_TYPE.CREATE_MASTER_NODE.getValue()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getDomain()+
							NaeglingCom.MESSAGE_DELIMITER+
							((VirtualMasterNode)node).getTemplate()+
							NaeglingCom.MESSAGE_DELIMITER+
							((VirtualMasterNode)node).getVirtualDiskPath()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getUuid()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getMac(MAC_TYPE.BRIDGE.ordinal())+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getBridgeNetworkInterface()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getMac(MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal())+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getHypervisor()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getRamMemory()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getCpuQuantity()+
							NaeglingCom.MESSAGE_DELIMITER+
							node.getVncPort();
					
					
					if(clusterList.get(idx).getMaster()!=null){
						int retval=JOptionPane.showConfirmDialog(null, "Cluster already contains Master node.\nReplace","Replace Mater Node?",JOptionPane.YES_NO_OPTION);
						if(retval==JOptionPane.YES_OPTION){
							NaeglingPersistency.deleteFromVirtualMasterNodeTable(clusterList.get(idx).getMaster().getDomain());
							NaeglingPersistency.insertIntoVirtualMasterNodeTable((VirtualMasterNode)node);
							clusterList.get(idx).setMaster((VirtualMasterNode)node);
							NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
						}
					}else{
						NaeglingPersistency.insertIntoVirtualMasterNodeTable((VirtualMasterNode)node);
						clusterList.get(idx).setMaster((VirtualMasterNode)node);
						NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
					}
					
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		updateNodesTable();
		updateStatus();
	}
	
	
	/**
	 * Add a new Host.
	 */
	private void addHost(){
		AddHostDialog addHostDialog=new AddHostDialog();
		VirtualMachineHost h=(VirtualMachineHost)addHostDialog.showDialog();
		if(h!=null){
			boolean exists=false;
			VirtualMachineHost vmh=null;
			try {
				vmh=NaeglingPersistency.getHost(h.getHostName());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
				if(vmh!=null)
					exists=true;
			if(!exists){
				try{
					NaeglingPersistency.insertIntoVirtualMachineHostTable(h);
				}catch(Exception e){
					JOptionPane.showMessageDialog(null,e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}else
				JOptionPane.showMessageDialog(null, "Host already exists.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Destroy cluster.
	 */
	public void destroyCluster(){
		int res=JOptionPane.showConfirmDialog(null, "Delete selected nodes?");
		if(!listCluster.isSelectionEmpty()&&res==JOptionPane.YES_OPTION){
			int[] idx=listCluster.getSelectedIndices();
			try{
				NaeglingPersistency.deleteFromClusterTable(clusterList.get(idx[0]).getName());
				defaultListModelCluster.remove(idx[0]);
				clusterList.remove(idx[0]);
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	

	
	
	/**
	 * Updates the nodes JTable
	 */
	private void updateNodesTable(){
		String[] headers={"Domain","Type","Hypervisor","Memory","CPU Cores","Host","VNC Port","Status"};
		Object[][] data=getData(headers.length);
		if(data!=null){
			table.setModel(new NodesTableModel(data,headers));
		}else{
			table=new JTable();
			scrollPane.setViewportView(table);
		}		
	}



	private Cluster getCurrentSelectedCluster(){
		if(!listCluster.isSelectionEmpty()){
			int idx=listCluster.getSelectedIndex();
			Cluster c=clusterList.get(idx);
			return c;
		}else{
			return null;
		}
			
	}
	
	/**
	 * Create the object used by the NodesTableModel
	 * @param columnSize - Number of columns
	 * @return Object[][] 
	 */
	private Object[][] getData(int columnSize){
		Object[][] data=null;
		Cluster c=getCurrentSelectedCluster();
		if(c!=null){
			if(c.getSlaveNodesQuantity()>0||c.getHasMasterNode()){
				ArrayList<VirtualSlaveNode> slaves=c.getSlaves();
				int size=slaves.size();
				if(c.getHasMasterNode()){
					++size;
					data = new Object[size][columnSize];
					data[0][0]=c.getMaster().getDomain();
					data[0][1]="Master";
					data[0][2]=c.getMaster().getHypervisor();
					data[0][3]=c.getMaster().getRamMemory();
					data[0][4]=c.getMaster().getCpuQuantity();
					data[0][5]=c.getMaster().getHost().getHostName();
					data[0][6]=Integer.parseInt(c.getMaster().getVncPort());
					data[0][7]=new ImageIcon(NaeglingGui.class.getResource("/images/imageWait.png"));
					for(int i=1;i<size;i++){
						data[i][0]=slaves.get(i-1).getDomain();
						data[i][1]="Slave";
						data[i][2]=slaves.get(i-1).getHypervisor();
						data[i][3]=slaves.get(i-1).getRamMemory();
						data[i][4]=slaves.get(i-1).getCpuQuantity();
						data[i][5]=slaves.get(i-1).getHost().getHostName();
						data[i][6]=Integer.parseInt(slaves.get(i-1).getVncPort());
						data[i][7]=new ImageIcon(NaeglingGui.class.getResource("/images/imageWait.png"));
					}
				}else{
					data = new Object[size][columnSize];
					for(int i=0;i<size;i++){
						data[i][0]=slaves.get(i).getDomain();
						data[i][1]="Slave";
						data[i][2]=slaves.get(i).getHypervisor();
						data[i][3]=slaves.get(i).getRamMemory();
						data[i][4]=slaves.get(i).getCpuQuantity();
						data[i][5]=slaves.get(i).getHost().getHostName();
						data[i][6]=Integer.parseInt(slaves.get(i).getVncPort());
						data[i][7]=new ImageIcon(NaeglingGui.class.getResource("/images/imageWait.png"));
					}
				}
			}
		}
		return data;
	}
	
	
	/**
	 * Update nodes JTable status column.
	 */
	private void updateStatus(){
		int count=table.getRowCount();
		for(int i=0;i<count;i++){
			try{
				String message=COM_TYPE.NODE_STATUS.getValue()+
						NaeglingCom.MESSAGE_DELIMITER+
						table.getValueAt(i,0).toString()+
						NaeglingCom.MESSAGE_DELIMITER+
						table.getValueAt(i,2).toString();
				String hostname=table.getValueAt(i,5).toString();
				String naeglingPort="";
				VirtualMachineHost h=NaeglingPersistency.getHost(hostname);
				if(h!=null)
					naeglingPort=h.getNaeglingPort();
				if(naeglingPort.equals(""))
					throw new Exception();
				int retval=NaeglingCom.sendMessageToHostname(message,hostname,naeglingPort);
				switch(retval){
				case 0:
					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imagePoweroff.png")), i, 7);
					break;
				case 1:
					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageRunning.png")), i, 7);
					break;
				default:
					table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageError.png")), i, 7);

				}
			}catch(Exception e){
				table.setValueAt(new ImageIcon(NaeglingGui.class.getResource("/images/imageError.png")), i, 7);
			}
		}
	}
	
	
	/**
	 * Get the selected nodes.
	 * @return ArrayList<VirtualNode>
	 */
	private ArrayList<VirtualNode> getSelectedNodes(){
		ArrayList<VirtualNode> nodes=new ArrayList<VirtualNode>();
		try{
			int []idx=table.getSelectedRows();
			boolean hasMaster=false;
			String domain;
			if(table.getModel().getValueAt(0, 1).toString().equalsIgnoreCase("Master"))
				hasMaster=true;
			for(int i:idx){
				domain=table.getModel().getValueAt(i, 0).toString();
				if(i==0&&hasMaster)
					nodes.add(NaeglingPersistency.getMaster(domain));
				else
					nodes.add(NaeglingPersistency.getSlave(domain));
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			nodes.clear();
		}
		return nodes;
	}
	
	/**
	 * Open View Hosts Dialog
	 */
	private void viewHosts(){
		ViewHostsDialog viewHosts=new ViewHostsDialog();
		viewHosts.setVisible(true);
	}
	
	/**
	 * Open View Templates Dialog
	 */
	private void viewTemplates() {
		ViewTemplatesDialog viewTemplates=new ViewTemplatesDialog();
		viewTemplates.setVisible(true);
		
	}
	
	/**
	 * Open View Jobs Dialog
	 */
	private void viewJobs() {
		ViewJobsDialog viewJobs=new ViewJobsDialog(getCurrentSelectedCluster());
		viewJobs.setVisible(true);
		
	}


}

