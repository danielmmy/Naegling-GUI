package br.com.naegling;

import java.io.File;
import java.util.ArrayList;

public class NaeglingJob {
	
	public enum FILE_STATUS{
		FILE_READY(0),
		COPYING(1),
		NOT_READY(2);

		private int value;    

		  private FILE_STATUS(int value) {
		    this.value = value;
		  }

		  public int getValue() {
		    return value;
		  }
	}

	public enum JOB_STATUS{
		JOB_STOPED(0),
		JOB_EXECUTING(2),
		JOB_DONE(3);

		private int value;    

		  private JOB_STATUS(int value) {
		    this.value = value;
		  }

		  public int getValue() {
		    return value;
		  }
	}
	
	public enum FILE_TYPE{
		SOURCE(1),
		ATTACHED(2),
		SCRIPT(3);

		private int value;    

		  private FILE_TYPE(int value) {
		    this.value = value;
		  }

		  public int getValue() {
		    return value;
		  }
	}
	
	
	private String name;
	private String cluster;
	private int status;
	private ArrayList<File> files;
	private ArrayList<Integer> filesStatus;
	private ArrayList<Integer> filesType;
	
	public NaeglingJob(String name, String cluster, int status){
		setName(name);
		setCluster(cluster);
		setStatus(status);
		files=new ArrayList<File>();
		filesStatus=new ArrayList<Integer>();
		filesType=new ArrayList<Integer>();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void addFile(File file,int status, int type){
		files.add(file);
		filesStatus.add(status);
		filesType.add(type);
	}
	
	public void removeFile(File file){
		int idx;
		for(idx=0;idx<files.size();++idx){
			if(file.getName().equals(files.get(idx).getName())){
				files.remove(idx);
				filesStatus.remove(idx);
				filesType.remove(idx);
			}
		}
	}


	public ArrayList<File> getFiles() {
		return files;
	}
	
	public int getFileIndex(String name){
		int idx;
		for(idx=0;idx<files.size();++idx){
			if(name.equals(files.get(idx).getName())){
				return idx;
			}
		}
		return -1;
	}
	
	public int getFileStatus(int idx){
		return filesStatus.get(idx);
	}
	
	public int getFileType(int idx){
		return filesType.get(idx);
	}
	
	public void sendFiles() throws Exception{
		/*
		 * Send program file
		 */
		Cluster c=NaeglingPersistency.getCluster(this.cluster);
		NaeglingJobFilesTransfer fileTransfer=new NaeglingJobFilesTransfer(this.getName(), this.getFiles(), c.getMaster());
		fileTransfer.sendJobFiles();
	}

}
