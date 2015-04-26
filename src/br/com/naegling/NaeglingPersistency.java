package br.com.naegling;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import br.com.naegling.VirtualNode.MAC_TYPE;

/**
 * Class to deal with database transactions
 * @author Daniel Yokoyama
 *
 */
public class NaeglingPersistency {
	
	private static final java.io.File DATABASE= new java.io.File(System.getProperty("user.home")+ System.getProperty("file.separator")+ ".Naegling"+ System.getProperty("file.separator") + "Naegling.db");
	private static final java.io.File TASKS_DATABASE= new java.io.File(System.getProperty("user.home")+ System.getProperty("file.separator")+ ".Naegling"+ System.getProperty("file.separator") + "NaeglingTasks.db");
	private static final java.io.File JOBS_DIRECTORY= new java.io.File(System.getProperty("user.home")+ System.getProperty("file.separator")+ ".Naegling"+ System.getProperty("file.separator") + "Jobs");
	
	/**
	 * Create database in the .Naegling directory in the user's home
	 * directory.
	 */
	public static void createDatabase() throws Exception{
		try { 
			 DATABASE.getParentFile().mkdirs();
			 DATABASE.createNewFile();
			 if (!DATABASE.exists()) 
				 throw new Exception("Error creating Naegling database file.\n");
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 
			 s.execute("CREATE TABLE IF NOT EXISTS cluster_table ("+
					    "name TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS virtual_machine_host_table ("+
					    "hostname TEXT,"+
					 	"ip TEXT,"+
					    "naegling_port TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS virtual_master_node_table ("+
					 	"cluster TEXT,"+
					 	"domain TEXT PRIMARY KEY,"+
					    "uuid TEXT,"+
					 	"bridge_mac TEXT,"+
					    "vir_naegling_mac TEXT,"+
					 	"bridge_network_interface TEXT,"+
					 	"hypervisor TEXT,"+
					 	"template TEXT,"+
					    "vdisk_path TEXT,"+
					 	"ram_memory INTEGER,"+
					    "cpu_quantity INTEGER,"+
					 	"host TEXT,"+
					    "vnc_port TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS virtual_slave_node_table ("+
					    "cluster TEXT,"+
					    "domain TEXT PRIMARY KEY,"+
					    "uuid TEXT,"+
					    "bridge_mac TEXT,"+
					    "bridge_network_interface TEXT,"+
					    "hypervisor TEXT,"+
					    "ram_memory INTEGER,"+
					    "cpu_quantity INTEGER,"+
					    "host TEXT,"+
					    "vnc_port TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS template_table ("+
					    "name TEXT,"+
					 	"path TEXT,"+
					    "md5 TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS cluster_network_table ("+
					    "ip TEXT PRIMARY KEY,"+
					    "domain TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS job_table ("+
					    "name TEXT,"+
					 	"cluster TEXT,"+
					    "status INTEGER,"+
					 	"PRIMARY KEY(name,cluster))");
			 s.execute("CREATE TABLE IF NOT EXISTS job_file_table ("+
					    "name TEXT,"+
					    "job TEXT,"+
					 	"path TEXT,"+
					 	"type INTEGER,"+
					    "status INTEGER,"+
					 	"PRIMARY KEY(name,job))");
		} catch (Exception e) {
			 throw new Exception("Error creating Naegling database.\n" + e.getMessage());
		}
	}
	
	/**
	 * Create tasks database in the .Naegling directory in the user's home
	 * directory.
	 */
	public static void createTasksDatabase() throws Exception{
		try { 
			 TASKS_DATABASE.getParentFile().mkdirs();
			 TASKS_DATABASE.createNewFile();
			 if (!TASKS_DATABASE.exists()) 
				 throw new Exception("Error creating Naegling database file.\n");
			 Connection conn = getTasksDatabaseConnection();
			 Statement s = conn.createStatement();
			 
			 s.execute("CREATE TABLE IF NOT EXISTS remove_node_table ("+
					    "domain TEXT)");
			 s.execute("CREATE TABLE IF NOT EXISTS update_node_table ("+
					    "domain TEXT)");
			 		} catch (Exception e) {
			 throw new Exception("Error creating Naegling database.\n" + e.getMessage());
		}
	}
	
	public static void createJobsDirectory(){
		JOBS_DIRECTORY.mkdirs();
	}
	
	public static void createJobDirectory(String jobName){
		File f=new File(JOBS_DIRECTORY.getAbsolutePath()+System.getProperty("file.separator")+jobName);
		f.mkdir();
	}
	
	
	/**
	 * Check if database exists in the .Naegling directory in the user's home
	 * directory.
	 */
	public static void checkDatabases() throws Exception{
		 if(!DATABASE.exists())
			 createDatabase();
		 if(!TASKS_DATABASE.exists())
			 createTasksDatabase();
	}
	
	public static void checkJobsDirectory(){
		if(!JOBS_DIRECTORY.exists()){
			createJobsDirectory();
		}
	}
	
	/**
	 * Get connection with naegling database.
	 * @return Connection
	 */
	 public static Connection getDatabaseConnection() throws Exception{ 
		 Class.forName("org.sqlite.JDBC");
		 Connection conn=DriverManager.getConnection("jdbc:sqlite:" + DATABASE.getPath());
		 return conn;	 
	 }
	 
	 /**
		 * Get connection with naegling database.
		 * @return Connection
		 */
		 public static Connection getTasksDatabaseConnection() throws Exception{ 
			 Class.forName("org.sqlite.JDBC");
			 Connection conn=DriverManager.getConnection("jdbc:sqlite:" + TASKS_DATABASE.getPath());
			 return conn;	 
		 }
	 
	 
	 /**
	  * Insert a new cluster into the database.
	  * @param cluster - Cluster to be inserted
	  */
	 public static void insertIntoClusterTable(Cluster cluster) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();

			 s.executeUpdate("INSERT INTO cluster_table VALUES("+
					 "'"+cluster.getName()+"')");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into cluster_table.\n" + e.getMessage());
		 }
	 }
	 
	 
	 /**
	  * Insert a new Template into the database.
	  * @param template - template to be inserted
	  */
	 public static void insertIntoTamplateTable(Template template) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();

			 s.executeUpdate("INSERT INTO template_table VALUES("+
					 "'"+template.getName()+"',"+
					 "'"+template.getPath()+"',"+
					 "'"+template.getMd5()+"')");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into template_table.\n" + e.getMessage());
		 }
	 }
	 
	 
	 /**
	  * Insert a new Host into the database.
	  * @param host - Host to be inserted
	  */
	 public static void insertIntoVirtualMachineHostTable(VirtualMachineHost host) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();

			 s.executeUpdate("INSERT INTO virtual_machine_host_table VALUES("+
					 "'"+host.getHostName()+"',"+
					 "'"+host.getIp()+"',"+
					 "'"+host.getNaeglingPort()+"')");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into virtual_machine_host_table.\n" + e.getMessage());
		 }
	 }
	 
	/**
	 * Insert a new virtual master node into the database.
	 * @param master - Virtual Master Node to be inserted
	 */
	 public static void insertIntoVirtualMasterNodeTable(VirtualMasterNode master) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 
			 s.executeUpdate("INSERT INTO virtual_master_node_table VALUES("+
					         "'"+master.getCluster()+"',"+
					 	     "'"+master.getDomain()+"',"+
					 		 "'"+master.getUuid()+"',"+
					 		 "'"+master.getMac(MAC_TYPE.BRIDGE.ordinal())+"',"+
					 		 "'"+master.getMac(MAC_TYPE.VIR_NETWORK_NAEGLING.ordinal())+"',"+
					 		 "'"+master.getBridgeNetworkInterface()+"',"+
					 		 "'"+master.getHypervisor()+"',"+
					 		"'"+master.getTemplate()+"',"+
					 		 "'"+master.getVirtualDiskPath()+"',"+
					 		 +master.getRamMemory()+","+
					 		 +master.getCpuQuantity()+","+
					 		 "'"+master.getHost().getHostName()+"',"+
					 		 "'"+master.getVncPort()+"')");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into virtual_master_node_table.\n" + e.getMessage());
		 }
	 }


	/**
	 * Insert a new virtual slave node into the database.
	 * @param slave - Virtual Slave Node to be inserted
	 */
	 public static void insertIntoVirtualSlaveNodeTable(VirtualSlaveNode slave) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 
			 s.executeUpdate("INSERT INTO virtual_slave_node_table VALUES("+
					         "'"+slave.getCluster()+"',"+
					 		 "'"+slave.getDomain()+"',"+
					 		 "'"+slave.getUuid()+"',"+
					 		 "'"+slave.getMac(MAC_TYPE.BRIDGE.ordinal())+"',"+
					 		 "'"+slave.getBridgeNetworkInterface()+"',"+
					 		 "'"+slave.getHypervisor()+"',"+
					 		 +slave.getRamMemory()+","+
					 		 +slave.getCpuQuantity()+","+
					 		 "'"+slave.getHost().getHostName()+"',"+
					 		 "'"+slave.getVncPort()+"')");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into virtual_slave_node_table.\n" + e.getMessage());
		 }
	 }
	 
	/**
	 * Insert a new job into database
	 * @param job
	 */
	public static void insertIntoJobTable(NaeglingJob job) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 
			 s.executeUpdate("INSERT INTO job_table VALUES("+
					         "'"+job.getName()+"',"+
					 		 "'"+job.getCluster()+"',"+
					 		 +job.getStatus()+")");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into job_table.\n" + e.getMessage());
		 }
	}
	 

	/**
	 * Insert job's file into database
	 * @param job
	 * @throws Exception
	 */
	public static void insertIntoJobFileTable(NaeglingJob job) throws Exception{
		try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ArrayList<File> files=job.getFiles();
			 for(File f : files)
				 s.executeUpdate("INSERT INTO job_file_table VALUES("+
						 	 					 "'"+f.getName()+"',"+
						 	 					 "'"+job.getName()+"',"+
						 	 					 "'"+f.getAbsolutePath()+"',"+
						 	 					 ""+job.getFileType(job.getFileIndex(f.getName()))+","+
						 	 					 ""+job.getFileStatus(job.getFileIndex(f.getName()))+")");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into job_file_table.\n" + e.getMessage());
		 }
	}
	

	/**
	 * Insert a file into job_file_table
	 * @param file
	 * @param job
	 * @param type
	 * @param status
	 * @throws Exception
	 */
	public static void insertIntoJobFileTable(File file,NaeglingJob job,int type, int status) throws Exception{
		try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 s.executeUpdate("INSERT INTO job_file_table VALUES("+
						 	 					 "'"+file.getName()+"',"+
						 	 					 "'"+job.getName()+"',"+
						 	 					 "'"+file.getAbsolutePath()+"',"+
						 	 					 ""+type+","+
						 	 					 ""+status+")");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error inserting into job_file_table.\n" + e.getMessage());
		 }
	}
	 
	 /**
	  * Read cluster_table from the database.
	  * @return ArrayList
	  */
	 public static ArrayList<Cluster> readFromClusterTable() throws Exception{
		 try{
			 ArrayList<Cluster> clusterList=new ArrayList<Cluster>();
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM cluster_table");
			 while(rs.next()){
				 Cluster c=new Cluster(rs.getString("name"));
				 ArrayList<VirtualSlaveNode> slaves=readFromVirtualSlaveNodeTable(c.getName());
				 c.setSlaves(slaves);
				 VirtualMasterNode master=readFromVirtualMasterNodeTable(c.getName());
				 c.setMaster(master);
				 clusterList.add(c);
			 }
			 conn.close();
			 return clusterList;
		 }catch(Exception e){
			 throw new Exception("Error reading from cluster_table.\n" + e.getMessage());
		 }
	 }
	 
	 /**
	  * Read virtual_machine_host_table from the database.
	  * @return ArrayList
	  */
	 public static ArrayList<VirtualMachineHost> readFromVirtualMachineHostTable() throws Exception{
		 try{
			 ArrayList<VirtualMachineHost> hosts=new ArrayList<VirtualMachineHost>();
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_machine_host_table");
			 while(rs.next())
				 hosts.add(new VirtualMachineHost(rs.getString("hostname"), rs.getString("ip"), rs.getString("naegling_port")));
			 conn.close();
			 return hosts;
		 }catch(Exception e){
			 throw new Exception("Error reading from virtual_machine_host_table.\n" + e.getMessage());
		 }
	 }
	 
	 
	 /**
	  * Read template_table from the database.
	  * @return ArrayList
	  */
	 public static ArrayList<Template> readFromTemplateTable() throws Exception{
		 try{
			 ArrayList<Template> templates=new ArrayList<Template>();
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM template_table");
			 while(rs.next())
				 templates.add(new Template(rs.getString("name"), rs.getString("path"), rs.getString("md5")));
			 conn.close();
			 return templates;
		 }catch(Exception e){
			 throw new Exception("Error reading from template_table.\n" + e.getMessage());
		 }
	 }
	 
	 /**
	  * Read virtual_slave_node_tabel From Database
	  * @param cluster - Cluster that owns the slaves to be read
	  * @return ArrayList
	  */
	 private static ArrayList<VirtualSlaveNode> readFromVirtualSlaveNodeTable(String cluster) throws Exception{
		 try{
			 ArrayList<VirtualSlaveNode> slaves=new ArrayList<VirtualSlaveNode>();
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_slave_node_table WHERE cluster='"+cluster+"'");
			 while(rs.next()){
				 Statement s2=conn.createStatement();
				 ResultSet rs2=s2.executeQuery("SELECT * FROM virtual_machine_host_table WHERE hostname='"+rs.getString("host")+"'");
				 rs2.next();
				 slaves.add(new VirtualSlaveNode(rs.getString("cluster"), rs.getString("domain"), rs.getString("uuid"),rs.getString("bridge_mac"),rs.getString("bridge_network_interface"),rs.getString("hypervisor"),rs.getInt("ram_memory"),rs.getInt("cpu_quantity"),new VirtualMachineHost(rs2.getString("hostname"),rs2.getString("ip"),rs2.getString("naegling_port")), rs.getString("vnc_port")));
			 }
			 conn.close();
			 return slaves;
		 }catch(Exception e){
			 throw new Exception("Error reading from database.\n" + e.getMessage());
		 }
	 }

	 
	 /**
	  * Read virtual_master_node_tabel From Database
	  * @param cluster - Cluster that owns the slaves to be read
	  * @return VirtualMasterNode
	  */
	 private static VirtualMasterNode readFromVirtualMasterNodeTable(String cluster) throws Exception{
		 try{
			 VirtualMasterNode master=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_master_node_table WHERE cluster='"+cluster+"'");
			 if(rs.next()){
				 Statement s2=conn.createStatement();
				 ResultSet rs2=s2.executeQuery("SELECT * FROM virtual_machine_host_table WHERE hostname='"+rs.getString("host")+"'");
				 rs2.next();
				 master=new VirtualMasterNode(rs.getString("cluster"), rs.getString("domain"),rs.getString("template"), rs.getString("vdisk_path"),rs.getString("uuid"),rs.getString("bridge_mac"),rs.getString("bridge_network_interface"),rs.getString("vir_naegling_mac"),rs.getString("hypervisor"),rs.getInt("ram_memory"),rs.getInt("cpu_quantity"),new VirtualMachineHost(rs2.getString("hostname"),rs2.getString("ip"),rs2.getString("naegling_port")), rs.getString("vnc_port"));
			 }
			 conn.close();
			 return master;
		 }catch(Exception e){
			 throw new Exception("Error reading from database.\n" + e.getMessage());
		 }
	 }
	 
	 
	 /**
	  * Read a master node From Database based on the domain name
	  * @param domain - Node's domain name
	  * @return VirtualMasterNode
	  */
	 public static VirtualMasterNode getMaster(String domain) throws Exception{
		 try{
			 VirtualMasterNode master=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_master_node_table WHERE domain='"+domain+"'");
			 if(rs.next()){
				 Statement s2=conn.createStatement();
				 ResultSet rs2=s2.executeQuery("SELECT * FROM virtual_machine_host_table WHERE hostname='"+rs.getString("host")+"'");
				 rs2.next();
				 master=new VirtualMasterNode(rs.getString("cluster"), rs.getString("domain"),rs.getString("template"),rs.getString("vdisk_path"), rs.getString("uuid"),rs.getString("bridge_mac"),rs.getString("bridge_network_interface"),rs.getString("vir_naegling_mac"),rs.getString("hypervisor"),rs.getInt("ram_memory"),rs.getInt("cpu_quantity"),new VirtualMachineHost(rs2.getString("hostname"),rs2.getString("ip"),rs2.getString("naegling_port")), rs.getString("vnc_port"));
			 }
			 conn.close();
			 return master;
		 }catch(Exception e){
			 throw new Exception("Error reading from database.\n" + e.getMessage());
		 }
	 }
	 

	 /**
	  * Read a slave node From Database based on the domain.
  	  * @param domain - Node's domain name
	  * @return VirtualSlaveNode
	  */
	 public static VirtualSlaveNode getSlave(String domain) throws Exception{
		 try{
			 VirtualSlaveNode slave=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_slave_node_table WHERE domain='"+domain+"'");
			 if(rs.next()){
				 Statement s2=conn.createStatement();
				 ResultSet rs2=s2.executeQuery("SELECT * FROM virtual_machine_host_table WHERE hostname='"+rs.getString("host")+"'");
				 rs2.next();
				 slave=new VirtualSlaveNode(rs.getString("cluster"), rs.getString("domain"), rs.getString("uuid"),rs.getString("bridge_mac"),rs.getString("bridge_network_interface"),rs.getString("hypervisor"),rs.getInt("ram_memory"),rs.getInt("cpu_quantity"),new VirtualMachineHost(rs2.getString("hostname"),rs2.getString("ip"),rs2.getString("naegling_port")), rs.getString("vnc_port"));
			 }
			 conn.close();
			 return slave;
		 }catch(Exception e){
			 throw new Exception("Error reading from database.\n" + e.getMessage());
		 }
	 }
	 
	 /**
	  * Delete cluster from the database.
	  * @param name - cluster name
	  */
	 public static void deleteFromClusterTable(String name) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 s.executeUpdate("DELETE FROM cluster_table where name='"+name+"'");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error deleting row from cluster_table.\n" + e.getMessage());
		 }
	 }
	 
	 
	 /**
	  * Delete host from the database.
	  * @param hostname
	  */
	 public static void deleteFromVirtualMachineHostTable(String hostname) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_slave_node_table WHERE host='"+hostname+"'");
			 if(rs.next()){
				 conn.close();
				 throw new Exception("Can not remove. Host is being used.");
			 }
			 rs=s.executeQuery("SELECT * FROM virtual_master_node_table WHERE host='"+hostname+"'");
			 if(rs.next()){
				 conn.close();
				 throw new Exception("Can not remove. Host is being used.");
			 }
			 
			 s.executeUpdate("DELETE FROM virtual_machine_host_table where hostname='"+hostname+"'");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error deleting row from virtual_machine_host_table.\n" + e.getMessage());
		 }
	 }
	 
	 /**
	  * Delete master from the database.
	  * @param domain - Node's domain name
	  */
	 public static void deleteFromVirtualMasterNodeTable(String domain) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 s.executeUpdate("DELETE FROM virtual_master_node_table where domain='"+domain+"'");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error deleting row from virtual_master_node_table.\n" + e.getMessage());
		 }
	 }

	 /**
	  * Returns VirtualMachineHost based on the hostname.
	  * @param hostname
	  * @return
	  * @throws Exception
	  */
	public static VirtualMachineHost getHost(String hostname) throws Exception{
		 try{
			 VirtualMachineHost host=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM virtual_machine_host_table WHERE hostname='"+hostname+"'");
			 if(rs.next()){
				 host=new VirtualMachineHost(rs.getString("hostname"),rs.getString("ip"),rs.getString("naegling_port"));
			 }
			 conn.close();
			 return host;
		 }catch(Exception e){
			 throw new Exception("Error reading from virtual_machine_host_table.\n" + e.getMessage());
		 }
	}
	
	
	/**
	 * returns Template based on the name
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Template getTemplate(String name) throws Exception{
		 try{
			 Template t=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM template_table WHERE name='"+name+"'");
			 if(rs.next()){
				 t=new Template(rs.getString("name"),rs.getString("path"),rs.getString("md5"));
			 }
			 conn.close();
			 return t;
		 }catch(Exception e){
			 throw new Exception("Error reading from template_table.\n" + e.getMessage());
		 }
	}

	/**
	 * Functions to obtain a free network
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public static String getClusterNetwork(String domain)throws Exception{
		try{
			ArrayList<String> ips=new ArrayList<String>();
			int ip=0;
			String networkIP=null;
			Connection conn = getDatabaseConnection();
			Statement s = conn.createStatement();
			ResultSet rs=s.executeQuery("SELECT ip FROM cluster_network_table");
			while(rs.next()){
				ips.add(rs.getString("ip"));
			}
			conn.close();
			ArrayList<Integer> networks=new ArrayList<Integer>();
			for (String string : ips) {
				String []tokens=string.split("\\.");
				networks.add(Integer.parseInt(tokens[2]));
			}
			Collections.sort(networks);
			int count=networks.size();
			int i,j;
			for(i=126,j=0;j<count;i++,j++){
				if(networks.get(j)!=i){
					ip=i;
					break;
				}
			}
			if(j==count){
				ip=i;
			}
			if(ip>125&&ip<255){
				networkIP=String.format("192.168.%d.254", ip);


				conn = getDatabaseConnection();
				s = conn.createStatement();

				s.executeUpdate("INSERT INTO cluster_network_table VALUES("+
						"'"+networkIP+"',"+
						"'"+domain+"')");
				conn.close();
			}

			return networkIP;
		}catch(Exception e){
			throw new Exception("Error reading from cluster_network_table.\n" + e.getMessage());
		}
	}
	
	/**
	 * Delete all entries on cluster_network_table based on the domain
	 * @param domain
	 * @throws Exception
	 */
	 public static void deleteFromClusterNetworkTable(String domain) throws Exception{
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 s.executeUpdate("DELETE FROM cluster_network_table WHERE domain='"+domain+"'");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error deleting row from cluster_network_table.\n" + e.getMessage());
		 }
	 }

	 /**
	  * Update job's file status
	  * @param fileName
	  * @param jobName
	  * @param status
	  */
	public static void updateJobFileStatus(String fileName, String jobName, int status) throws Exception {
		 try{
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 s.executeUpdate("UPDATE job_file_table SET status="+status+" WHERE name='"+fileName+"' AND job='"+jobName+"'");
			 conn.close();
		 }catch(Exception e){
			 throw new Exception("Error updateing row from job_file_table.\n" + e.getMessage());
		 }
	}

	/**
	 * Return a cluster based on the name
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Cluster getCluster(String name) throws Exception{
		 try{
			 Cluster c=null;
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM cluster_table WHERE name='"+name+"'");
			 if(rs.next()){
				 c=new Cluster(rs.getString("md5"));
				 ArrayList<VirtualSlaveNode> slaves=readFromVirtualSlaveNodeTable(c.getName());
				 c.setSlaves(slaves);
				 VirtualMasterNode master=readFromVirtualMasterNodeTable(c.getName());
				 c.setMaster(master);
			 }
			 conn.close();
			 return c;
		 }catch(Exception e){
			 throw new Exception("Error reading from cluster_table.\n" + e.getMessage());
		 }
	}


	public static String getJobDirectoryPath(){
		return JOBS_DIRECTORY.getAbsolutePath();
	}


	 public static ArrayList<NaeglingJob> getJobs(String cluster) throws Exception{
		 try{
			 ArrayList<NaeglingJob> jobs=new ArrayList<NaeglingJob>();
			 Connection conn = getDatabaseConnection();
			 Statement s = conn.createStatement();
			 ResultSet rs=s.executeQuery("SELECT * FROM job_table WHERE cluster='"+cluster+"'");
			 while(rs.next()){
				 Statement s2=conn.createStatement();
				 NaeglingJob job=new NaeglingJob(rs.getString("name"),rs.getString("cluster"),rs.getInt("status"));
				 ResultSet rs2=s2.executeQuery("SELECT * FROM job_file_table WHERE job='"+job.getName()+"'");
				 while(rs2.next())
					 job.addFile( new File(rs2.getString("path")),rs2.getInt("status"), rs2.getInt("type"));
				 jobs.add(job);
			 }
			 conn.close();
			 return jobs;
		 }catch(Exception e){
			 throw new Exception("Error reading from database.\n" + e.getMessage());
		 }
	 }
	 
	 /**
		 * Insert 'remove node' task into tasks database.
		 * @param domain - Virtual Slave Node to be inserted
		 */
		 public static void insertIntoRemoveNodeTask(String domain) throws Exception{
			 try{
				 Connection conn = getTasksDatabaseConnection();
				 Statement s = conn.createStatement();
				 
				 s.executeUpdate("INSERT INTO remove_node_table VALUES("+
						         "'"+domain+"')");
				 conn.close();
			 }catch(Exception e){
				 throw new Exception("Error inserting into remove_node_table from TasksDatabase.\n" + e.getMessage());
			 }
		 }
		 
		 
		 /**
		  * Delete virtual slave node from database
		  * @param domain
		  * @throws Exception
		  */
		 public static void deleteFromRemoveNodeTaskTable(String domain)throws Exception{
			 try{
				 Connection conn = getTasksDatabaseConnection();
				 Statement s = conn.createStatement();
				 
				 s.executeUpdate("DELETE FROM remove_node_table WHERE domain='"+domain+"'");
				 conn.close();
			 }catch(Exception e){
				 throw new Exception("Error deleting from remove_node_table from TasksDatabase.\n" + e.getMessage());
			 }
		 }
		 
		 
		 
		 
		 /**
		  * Delete virtual slave node from database
		  * @param domain
		  * @throws Exception
		  */
		 public static void deleteFromVirtualSlaveNodeTable(String domain)throws Exception{
			 try{
				 Connection conn = getDatabaseConnection();
				 Statement s = conn.createStatement();
				 
				 s.executeUpdate("DELETE FROM virtual_slave_node_table WHERE domain='"+domain+"'");
				 conn.close();
			 }catch(Exception e){
				 throw new Exception("Error deleting from virtual_slave_node_table from Database.\n" + e.getMessage());
			 }
		 }
		 
		 /**
			 * Insert 'update node' task into tasks database.
			 * @param domain - Virtual Slave Node to be inserted
			 * @throws Exception
			 */
			 public static void insertIntoUpdateNodeTask(String domain) throws Exception{
				 try{
					 Connection conn = getTasksDatabaseConnection();
					 Statement s = conn.createStatement();
					 
					 s.executeUpdate("INSERT INTO update_node_table VALUES("+
							         "'"+domain+"')");
					 conn.close();
				 }catch(Exception e){
					 throw new Exception("Error inserting into remove_node_table from TasksDatabase.\n" + e.getMessage());
				 }
			 }
			 
			 
			 /**
			  * Remove from 'update node' tasks database 
			  * @param domain
			  * @throws Exception
			  */
			 public static void deleteFromUpdateNodeTaskTable(String domain)throws Exception{
				 try{
					 Connection conn = getTasksDatabaseConnection();
					 Statement s = conn.createStatement();
					 
					 s.executeUpdate("DELETE FROM update_node_table WHERE domain='"+domain+"'");
					 conn.close();
				 }catch(Exception e){
					 throw new Exception("Error deleting from remove_node_table from TasksDatabase.\n" + e.getMessage());
				 }
			 }
			 
			 /**
			  * Update 'virtual_slave_node_table' table on database.
			  * @param domain
			  * @param cpuQuantity
			  * @param ramMemory
			  * @param vncPort
			  * @throws Exception
			  */
			 public static void updateVirtualSlaveNodeTableTable(String domain,int cpuQuantity, int ramMemory, String vncPort)throws Exception{
				 try{
					 Connection conn = getDatabaseConnection();
					 Statement s = conn.createStatement();
					 
					 s.executeUpdate("UPDATE virtual_slave_node_table set cpu_quantity="+cpuQuantity+",ram_memory="+ramMemory+",vnc_port='"+vncPort+"' WHERE domain='"+domain+"'");
					 conn.close();
				 }catch(Exception e){
					 throw new Exception("Error updating virtual_slave_node_table from Database.\n" + e.getMessage());
				 }
			 }
			 
			 /**
			  * Update 'virtual_slave_node_table' table on database.
			  * @param domain
			  * @param cpuQuantity
			  * @param ramMemory
			  * @param vncPort
			  * @throws Exception
			  */
			 public static void updateVirtualMasterNodeTableTable(String domain,int cpuQuantity, int ramMemory, String vncPort)throws Exception{
				 try{
					 Connection conn = getDatabaseConnection();
					 Statement s = conn.createStatement();
					 
					 s.executeUpdate("UPDATE virtual_master_node_table set cpu_quantity="+cpuQuantity+",ram_memory="+ramMemory+",vnc_port='"+vncPort+"' WHERE domain='"+domain+"'");
					 conn.close();
				 }catch(Exception e){
					 throw new Exception("Error updating virtual_slave_node_table from Database.\n" + e.getMessage());
				 }
			 }

	
}
