package br.com.naegling;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import br.com.naegling.NaeglingCom.COM_TYPE;

/**
 * Cluster class
 * @author Daniel Yokoyama
 *
 */
public class Cluster {
	private String name;
	private VirtualMasterNode master;
	private boolean hasMasterNode;
	private ArrayList<VirtualSlaveNode> slaves;
	private int slaveNodesQuantity;
	

	public Cluster(String name){
		this.name=name;
		slaveNodesQuantity=0;
		master=null;
		slaves=new ArrayList<VirtualSlaveNode>();
		setHasMasterNode(false);
	}
	
	public String getName() {
		return name;
	}
	public VirtualMasterNode getMaster() {
		return master;
	}

	public void setMaster(VirtualMasterNode master) {
		this.master = master;
		if(master!=null)
			setHasMasterNode(true);
		else
			setHasMasterNode(false);
	}
	
	public void addSlave(VirtualSlaveNode slave){
		this.slaves.add(slave);
		++slaveNodesQuantity;
	}

	public int getSlaveNodesQuantity() {
		return slaveNodesQuantity;
	}

	public boolean getHasMasterNode() {
		return hasMasterNode;
	}

	private void setHasMasterNode(boolean hasMasterNode) {
		this.hasMasterNode = hasMasterNode;
	}

	public void setSlaves(ArrayList<VirtualSlaveNode> slaves) {
		this.slaves=slaves;
		slaveNodesQuantity=slaves.size();
		if(master!=null)
			++slaveNodesQuantity;
	}

	public ArrayList<VirtualSlaveNode> getSlaves() {
		return slaves;
	}
	
	public void removeNodes(ArrayList<VirtualNode> nodes){
		int retval;
		for(VirtualNode node : nodes){
			try {

				String message=COM_TYPE.NODE_STATUS.getValue()+
						NaeglingCom.MESSAGE_DELIMITER+
						node.getDomain()+
						NaeglingCom.MESSAGE_DELIMITER+
						node.getHypervisor();
				retval=NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
				if(retval==0){
					if(hasMasterNode&&this.master.domain.equals(node.getDomain())){

					}else{
						NaeglingPersistency.insertIntoRemoveNodeTask(node.domain);
						message=COM_TYPE.DELETE_NODE.getValue()+
								NaeglingCom.MESSAGE_DELIMITER+
								node.getDomain()+
								NaeglingCom.MESSAGE_DELIMITER+
								node.getHypervisor();
						retval=NaeglingCom.sendMessageToHostname(message, node.getHost().getHostName(), node.getHost().getNaeglingPort());
						if(retval==0){
							NaeglingPersistency.deleteFromVirtualSlaveNodeTable(node.getDomain());
							NaeglingPersistency.deleteFromRemoveNodeTaskTable(node.domain);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "Domain "+node.getDomain()+" is on.\nPlease turn it off and try again.","Error, Can Not delete",JOptionPane.ERROR_MESSAGE);
				}


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}



}
