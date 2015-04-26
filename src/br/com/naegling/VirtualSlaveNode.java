package br.com.naegling;

/**
 * Virtual Slave Node's class
 * @author Daniel Yokoyama
 *
 */
public class VirtualSlaveNode extends VirtualNode {
	/**
	 * Constructor
	 * @param cluster - cluster name that owns this node
	 * @param domain - node's domain name
	 * @param uuid - node's UUID
	 * @param mac - node's bridge MAC address
	 * @param bridgeNetworkInterface - node's bridge network interface
	 * @param hypervisor - node's hypervisor type
	 * @param memory - node's ram memory amount
	 * @param cpu - node's CPU core quantity
	 * @param host - node's host machine
	 * @param vncPort - node's assigned VNC port
	 */
	public VirtualSlaveNode(String cluster,String domain, String uuid,String mac, String bridgeNetworkInterface, String hypervisor, int memory, int cpu, VirtualMachineHost host, String vncPort) {
		super(cluster);
		setDomain(domain);
		setUuid(uuid);
		macs= new String[1];
		setMac(mac, MAC_TYPE.BRIDGE.ordinal());
		setBridgeNetworkInterface(bridgeNetworkInterface);
		setHypervisor(hypervisor);
		setRamMemory(memory);
		setCpuQuantity(cpu);
		setHost(host);
		setVncPort(vncPort);
	}


}
