package cc.wulian.smarthomev5.entity.uei;
/**
 * 根据模板学习的按键
 * @author yuxiaoxuan
 * @date 2016年8月5日09:32:24
 *
 */
public class UeiVirtualBtn {
	public UeiVirtualBtn(){}
	public UeiVirtualBtn(String keyid){
		this.keyid=keyid;
	}
	/**
	 * learnedCodeid学习按键ID，一般用于自定义遥控
	 */
	private String lc="";
	/**
	 * 按键编码，一般是需要通过12命令发过去的
	 */
	private String keyid="";
	/**
	 * 别名，一般用于自定义遥控
	 */
	private String nm="";
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
}
