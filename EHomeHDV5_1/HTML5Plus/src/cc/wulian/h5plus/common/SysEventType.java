package cc.wulian.h5plus.common;

/**
 * Created by Administrator on 2015/11/20.
 */
public enum SysEventType {

    KEYCODE_MENU("KEYCODE_MENU"),

    KEYCODE_HOME("KEYCODE_HOME"),

    KEYCODE_BACK("KEYCODE_BACK"),

    onKeyDown("onKeyDown"),

    onKeyUp("onKeyUp"),

    onKeyLongPress("onKeyLongPress"),

    onConfigurationChanged("onConfigurationChanged"),

    onActivityResult("onActivityResult"),
    
    onActivityResume("onActivityResume"),

    onCreateOptionMenu("onCreateOptionMenu");

    private String value;

    SysEventType(String value){
        this.value=value;
    }

    public String getValue(){
        return  this.value;
    }
}
