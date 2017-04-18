package cc.wulian.smarthomev5.service.html5plus.plugins;

public class PluginModel {
	
	public static String NAME="name";
	public static String FOLDER="folder";
	public static String VERSION="version";
	public static String ENTITY="home_page";
	public static String ROOT_FOLDER="root_folder";

	private String name;
	
	private String folder;  //插件所在位置
	
	private String version;
	
	private String description;
	
	private String entry; //插件入口

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
		
}
