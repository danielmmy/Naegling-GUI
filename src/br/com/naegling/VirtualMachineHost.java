package br.com.naegling;
/**
 * Host's class
 * @author daniel
 *
 */
public class VirtualMachineHost {
	private String hostName;
	private String ip;
	private String naeglingPort;
	
	public VirtualMachineHost(String hostname, String ip, String port){
		this.hostName=hostname;
		this.ip=ip;
		this.naeglingPort=port;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNaeglingPort() {
		return naeglingPort;
	}
	public void setNaeglingPort(String naeglingPort) {
		this.naeglingPort = naeglingPort;
	}
}
