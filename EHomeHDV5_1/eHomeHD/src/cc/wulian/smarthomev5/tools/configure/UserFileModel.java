package cc.wulian.smarthomev5.tools.configure;

/**
 * 保存用户个人下载的文件信息
 */
public class UserFileModel {

	private String fileName;

	private String storageFolder;

	private String version;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStorageFolder() {
		return storageFolder;
	}

	public void setStorageFolder(String storageFolder) {
		this.storageFolder = storageFolder;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}