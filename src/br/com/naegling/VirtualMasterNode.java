package br.com.naegling;

import javax.swing.JOptionPane;


/**
 * Virtual Master Node's class
 * @author Daniel Yokoyama
 */
public class VirtualMasterNode extends VirtualNode {
	
	private String template;


	private String virtualDiskPath;
	
	public VirtualMasterNode(String cluster,String domain,String template, String virtualDiskPath, String uuid,String bridgeMac, String bridgeNetworkInterface,String naeglingMac, String hypervisor, int memory, int cpu, VirtualMachineHost host, String vncPort) {
		super(cluster);
		setDomain(domain);
		setTemplate(template);
		setVirtualDiskPath("/var/lib/libvirt/images/"+domain+".img");
		setUuid(uuid);
		macs= new String[2];
		setMac(bridgeMac, MAC_TYPE.BRIDGE.ordinal());
		setMac(naeglingMac,MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal());
		setBridgeNetworkInterface(bridgeNetworkInterface);
		setHypervisor(hypervisor);
		setRamMemory(memory);
		setCpuQuantity(cpu);
		setHost(host);
		setVncPort(vncPort);
	}
	
	
	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	}

	public String getVirtualDiskPath() {
		return virtualDiskPath;
	}

	public void setVirtualDiskPath(String virtualDiskPath) {
		this.virtualDiskPath = virtualDiskPath;
	}
	
	@Override
	public void start(){
		try {
			String ip=NaeglingPersistency.getClusterNetwork(this.getDomain());
			if(ip!=null){
				String message=NaeglingCom.COM_TYPE.START_MASTER_VIRTUAL_NODE.getValue()+
						NaeglingCom.MESSAGE_DELIMITER+
						this.getDomain()+
						NaeglingCom.MESSAGE_DELIMITER+
						this.getHypervisor()+
						NaeglingCom.MESSAGE_DELIMITER+
						this.getMac(MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal())+
						NaeglingCom.MESSAGE_DELIMITER+
						this.getMac(MAC_TYPE.BRIDGE.ordinal())+
						NaeglingCom.MESSAGE_DELIMITER+
						ip;
				NaeglingCom.sendMessageToHostname(message, this.getHost().getHostName(), this.getHost().getNaeglingPort());
			}else{
				JOptionPane.showMessageDialog(null,"Could not get a valid cluster IP address.","Error",JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error starting node.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void stop(){
		try {
			String message=NaeglingCom.COM_TYPE.STOP_MASTER_VIRTUAL_NODE.getValue()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getDomain()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getHypervisor()+
					NaeglingCom.MESSAGE_DELIMITER+
					this.getMac(MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal());
			NaeglingCom.sendMessageToHostname(message, this.getHost().getHostName(), this.getHost().getNaeglingPort());
			NaeglingPersistency.deleteFromClusterNetworkTable(domain);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error stopping node.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
