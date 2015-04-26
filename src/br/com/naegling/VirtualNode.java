package br.com.naegling;



import javax.swing.JOptionPane;
import br.com.naegling.JavaViewer.VncViewer;
import br.com.naegling.NaeglingCom.COM_TYPE;

/**
 * Virtual Node's class
 * @author Daniel Yokoyama
 *
 */
public abstract class VirtualNode {
	public enum MAC_TYPE{
		BRIDGE,
		VIR_NETWORK_NAEGLING;
	}
	
	protected String cluster;
	protected String domain;
	protected String uuid;
	protected String macs[];
	protected String bridgeNetworkInterface;
	protected String hypervisor;
	protected int ramMemory;
	protected int cpuQuantity;
	protected VirtualMachineHost host;
	protected String vncPort;
	protected VncViewer vnc;
	
	
	public  VirtualNode(String cluster){
		setCluster(cluster);
	}
	
	
	/*
	 * Getters and Setters.
	 */	
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getMac(int index) {
		if(index<macs.length)
			return macs[index];
		else
			return null;
		
	}
	public void setMac(String mac, int index) {
		if(index<macs.length)
			this.macs[index] = mac;
	}
	public String getBridgeNetworkInterface() {
		return bridgeNetworkInterface;
	}
	public void setBridgeNetworkInterface(String bridgeNetworkInterface) {
		this.bridgeNetworkInterface = bridgeNetworkInterface;
	}
	public String getHypervisor() {
		return hypervisor;
	}
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}
	public int getRamMemory() {
		return ramMemory;
	}
	public void setRamMemory(int ramMemory) {
		this.ramMemory = ramMemory;
	}
	public int getCpuQuantity() {
		return cpuQuantity;
	}
	public void setCpuQuantity(int cpuQuantity) {
		this.cpuQuantity = cpuQuantity;
	}
	public VirtualMachineHost getHost() {
		return host;
	}
	public void setHost(VirtualMachineHost host) {
		this.host = host;
	}
	public String getVncPort() {
		return vncPort;
	}
	public void setVncPort(String vncPort) {
		this.vncPort = vncPort;
	}
	
	
	/*
	 * Function to check the status of the virtual machine
	 * Returns:
	 * 	-1 error
	 *	0 inactive
	 * 	1 running
	 */
	public int getNodeStatus(){
		int retval=-1;
		try{
			String message=COM_TYPE.NODE_STATUS.getValue()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getDomain()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getHypervisor();
			retval=NaeglingCom.sendMessageToHostname(message,this.getHost().getHostName(),this.getHost().getNaeglingPort());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Error obtaining  node status.\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return retval;
	}
	
	/**
	 * Start the virtual machine
	 */
	public void start(){
		try {
			String message=NaeglingCom.COM_TYPE.START_NODE.getValue()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getDomain()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getHypervisor();
			NaeglingCom.sendMessageToHostname(message, this.getHost().getHostName(), this.getHost().getNaeglingPort());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error starting node.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Stop the virtual machine
	 */
	public void stop(){
		try {
			String message=NaeglingCom.COM_TYPE.STOP_NODE.getValue()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getDomain()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getHypervisor();
			NaeglingCom.sendMessageToHostname(message, this.getHost().getHostName(), this.getHost().getNaeglingPort());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error stopping node.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Open Graphical access to the machine
	 */
	public void openGraphics(){
		vnc = new VncViewer();
		String parameters[]={"HOST",this.host.getIp(),"PORT",this.getVncPort()};
		vnc.mainArgs = parameters;
		vnc.setInAnApplet(false);
		vnc.setInSeparateFrame(true);
		vnc.init();
		vnc.start();
	}


}
