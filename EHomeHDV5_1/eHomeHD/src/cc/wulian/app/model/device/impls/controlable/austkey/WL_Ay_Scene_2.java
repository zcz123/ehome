package cc.wulian.app.model.device.impls.controlable.austkey;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.configureable.touch.AbstractTouchDevice;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * Created by Administrator on 2017/2/17.
 */

@DeviceClassify(
        devTypes = {"Ay"},
        category = Category.C_OTHER)
public class WL_Ay_Scene_2 extends AbstractTouchDevice
{
    private static final String[] EP_SEQUENCE = {EP_14, EP_15};


    private GridView mGridView;

    private static final int BIG_NORMAL_D = cc.wulian.app.model.device.R.drawable.device_bind_scene_normal_2_big;
    private ImageView mBottomView;

    private TextView textview1;
    private TextView textview2;
    private String ep14Name ;
    private String ep15Name;
    public WL_Ay_Scene_2(Context context, String type )
    {
        super(context, type);
    }
    @Override
    public String[] getTouchEPResources() {
        return EP_SEQUENCE;
    }
    @Override
    public String[] getTouchEPNames() {
        ep14Name = DeviceUtil.ep2IndexString(EP_14)+getResources().getString(cc.wulian.app.model.device.R.string.device_key_scene_bind);
        ep15Name = DeviceUtil.ep2IndexString(EP_15)+getResources().getString(cc.wulian.app.model.device.R.string.device_key_scene_bind);
        return new String[]{ep14Name,ep15Name};
    }
    @Override
    public Drawable[] getStateBigPictureArray(){
        Drawable[] drawables = new Drawable[]{getResources().getDrawable(BIG_NORMAL_D)};
        return drawables;
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData){
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(cc.wulian.app.model.device.R.string.device_type_32));
        return sb.toString();
    }

}