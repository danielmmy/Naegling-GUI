package br.com.naegling;

public class Template {
	private String name;
	private String path;
	private String md5;
	
	
	public Template(String name, String path, String md5){
		this.setName(name);
		this.setPath(path);
		this.setMd5(md5);
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}


	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
