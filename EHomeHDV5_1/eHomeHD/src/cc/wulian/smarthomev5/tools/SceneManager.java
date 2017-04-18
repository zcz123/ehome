package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.entity.IconResourceEntity;
import cc.wulian.smarthomev5.entity.TaskEntity.TaskGroup;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.IconChooseView;
import cc.wulian.smarthomev5.view.IconChooseView.OnIconClickListener;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class SceneManager {

    private static List<IconResourceEntity> mSceneIconBlackList;
    private static List<IconResourceEntity> mSceneIconBrightList;
    private static List<IconResourceEntity> mSceneIconLightSmallList;

    private static final String ICON_TYPE_PREFIX_BLACK = "scene_black_";
    private static final String ICON_TYPE_PREFIX_BRIGHT = "scene_bright_";

    // these two icon had small type
    private static final String ICON_TYPE_PREFIX_LIGHT = "scene_light_";
    private static final String ICON_TYPE_SUFFIX_SMALL = "_small";

    private static final String DEF_TYPE_DRAWABLE = "drawable";

    private static WLDialog dialog;
    public TaskGroup taskGroup;

    static {
        mSceneIconBlackList = Lists.newArrayList();
        mSceneIconBrightList = Lists.newArrayList();
        mSceneIconLightSmallList = Lists.newArrayList();

        MainApplication application = MainApplication.getApplication();

        final Resources resources = application.getResources();

        Integer iconSupportSize = resources
                .getInteger(R.integer.scene_icon_support_count);

        inflaterSceneIcon(mSceneIconBlackList, ICON_TYPE_PREFIX_BLACK, null,
                iconSupportSize, application);
        inflaterSceneIcon(mSceneIconBrightList, ICON_TYPE_PREFIX_BRIGHT, null,
                iconSupportSize, application);
        inflaterSceneIcon(mSceneIconLightSmallList, ICON_TYPE_PREFIX_LIGHT,
                ICON_TYPE_SUFFIX_SMALL, iconSupportSize, application);
    }

    private static void inflaterSceneIcon(List<IconResourceEntity> listData,
                                          String prefix, String suffix, int supportCount, Context context) {
        final Resources resources = context.getResources();
        for (int i = 0; i < supportCount; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            sb.append(i);
            if (suffix != null)
                sb.append(suffix);

            int brightIconRes = resources.getIdentifier(sb.toString(),
                    DEF_TYPE_DRAWABLE, context.getPackageName());
            String brighticon=String.format("scene_bright_%s", i);//添加场景时的图片获取方式
            int selectedRes = resources.getIdentifier(brighticon,
                    DEF_TYPE_DRAWABLE, context.getPackageName());
            IconResourceEntity entity = new IconResourceEntity();
            entity.iconkey = i;
            entity.iconRes = brightIconRes;
            entity.iconSelectedRes=selectedRes;//此处赋值
            listData.add(entity);
        }
    }

    private SceneManager() {
    }

    public static boolean isSceneInUse(String state) {
        return TextUtils.equals(CmdUtil.SCENE_USING, state);
    }

    public static Drawable getSceneIconDrawable_Black(Context context,
                                                      String iconIndex) {
        return getSceneIconDrawable(context, iconIndex, ICON_TYPE_PREFIX_BLACK,
                false);
    }

    public static Drawable getSceneIconDrawable_Bright(Context context,
                                                       String iconIndex) {
        return getSceneIconDrawable(context, iconIndex,
                ICON_TYPE_PREFIX_BRIGHT, false);
    }

    public static Drawable getSceneIconDrawable_Light_Small(Context context,
                                                            String iconIndex) {
        return getSceneIconDrawable(context, iconIndex, ICON_TYPE_PREFIX_LIGHT,
                true);
    }

    private static Drawable getSceneIconDrawable(Context context,
                                                 String iconIndex, String type, boolean smallType) {
        IconResourceEntity entity = new IconResourceEntity();
        entity.iconkey = StringUtil.toInteger(iconIndex);

        List<IconResourceEntity> rList = mSceneIconLightSmallList;

        if (ICON_TYPE_PREFIX_BLACK.equals(type)) {
            rList = mSceneIconBlackList;
        } else if (ICON_TYPE_PREFIX_BRIGHT.equals(type)) {
            rList = mSceneIconBrightList;
        } else if (ICON_TYPE_PREFIX_LIGHT.equals(type)) {
            rList = mSceneIconLightSmallList;
        }

        int index = rList.indexOf(entity);
        if (index != -1) {
            entity.iconRes = rList.get(index).iconRes;
        }
        // get default icon
        else {
            entity.iconRes = rList.get(0).iconRes;
        }

        Drawable icon = context.getResources().getDrawable(entity.iconRes);
        return icon;
    }

    public static int getSceneIconDrawableInt(Context context,
                                              String iconIndex, String type, boolean smallType) {
        IconResourceEntity entity = new IconResourceEntity();
        entity.iconkey = StringUtil.toInteger(iconIndex);

        List<IconResourceEntity> rList = mSceneIconLightSmallList;

        if (ICON_TYPE_PREFIX_BLACK.equals(type)) {
            rList = mSceneIconBlackList;
        } else if (ICON_TYPE_PREFIX_BRIGHT.equals(type)) {
            rList = mSceneIconBrightList;
        } else if (ICON_TYPE_PREFIX_LIGHT.equals(type)) {
            rList = mSceneIconLightSmallList;
        }

        int index = rList.indexOf(entity);
        if (index != -1) {
            entity.iconRes = rList.get(index).iconRes;
        }
        // get default icon
        else {
            entity.iconRes = rList.get(0).iconRes;
        }

        return entity.iconRes;
    }

    public static void deleteSceneInfo(final Context context,
                                       final SceneInfo info,final AutoProgramTaskInfo timingInfo,final AutoProgramTaskInfo sceneTaskInfo) {

        class DeleteSceneListener implements MessageListener {

            @Override
            public void onClickPositive(View contentViewLayout) {
                if (info != null) {
                    SendMessage.sendSetSceneMsg(context, info.getGwID(),
                            CmdUtil.MODE_DEL, info.getSceneID(), null, null,
                            null, true);
                    if(Preference.getPreferences().getBoolean(IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false)){
                        if(!StringUtil.isNullOrEmpty(sceneTaskInfo.getProgramID())){
                            JsonTool.deleteAndQueryAutoTaskList("D", sceneTaskInfo);
                        }
                        if(!StringUtil.isNullOrEmpty(timingInfo.getProgramID())){
                            JsonTool.deleteAndQueryAutoTaskList("D", timingInfo);
                        }
                    }else{
                        TimingSceneGroupEntity timingSceneGroup = TimingSceneManager
                                .getInstance().getDefaultGroup();
                        List<TimingSceneEntity> timingSceneEntities = timingSceneGroup.getTimingSceneEntities(info.getSceneID());
                        if(null !=timingSceneEntities){
                            JsonTool.uploadTimingSceneList(CmdUtil.MODE_ADD,
                                    timingSceneGroup,timingSceneGroup.removeTimingSceneListNewList(timingSceneEntities));
                        }
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }

        }
        DeleteSceneListener deleteSceneListener = new DeleteSceneListener();
        WLDialog.Builder builder = new Builder(context);
        builder.setTitle(R.string.device_config_edit_dev_area_create_item_delete)
                .setContentView(R.layout.common_dialog_delete)
                .setPositiveButton(R.string.common_ok)
                .setNegativeButton(R.string.cancel)
                .setListener(deleteSceneListener);
        dialog = builder.create();
        dialog.show();
    }

    public static void editSceneInfo(final Context context,final SceneInfo editInfo) {
        final IconChooseView chooseView = new IconChooseView(context,mSceneIconBlackList);

        chooseView.setOnItemClickListener(new OnIconClickListener() {

            @Override
            public void onIconClick(IconResourceEntity entity) {
                String str = null;
                String mSceneName = chooseView.getInputTextContent();
                if(StringUtil.isNullOrEmpty(mSceneName)){
                    str = DeviceTool.getSceneTextByIcon(context,entity.iconkey);
                    chooseView.setInputHintTextContent(str);
                }
                chooseView.setSelectedChangedBackgroundColor(false);
                chooseView.setSelectedChangedImageDrawable(true);

            }
        });
        chooseView.setInputHintTextContent(context.getResources().getString(R.string.nav_scene_title));
        if (editInfo != null) {
            int index = StringUtil.toInteger(editInfo.getIcon());
            chooseView.setSelectIcon((index <0 || index > mSceneIconBrightList.size()) ? 0 : index);
            chooseView.setInputTextContent(editInfo.getName());
        }
        class AddSceneListener implements MessageListener {

            @Override
            public void onClickPositive(View contentViewLayout) {
                String name = chooseView.getInputTextContent().trim();
                if(StringUtil.isNullOrEmpty(name))
                    name = chooseView.getInputHintTextContent();
                String mode = CmdUtil.MODE_ADD;
                if (name.isEmpty()) {
                    chooseView.requestFocus();
                    chooseView
                            .setError(context.getText(R.string.hint_not_null_edittext));
                }else if(StringUtil.isNullOrEmpty(String.valueOf(chooseView.getCheckedItem()))){
                    WLToast.showToast(context, context.getResources().getString(R.string.scene_icon_choose),  WLToast.TOAST_SHORT);
                } else {
                    SceneInfo info = new SceneInfo();
                    info.setGwID(AccountManager.getAccountManger().getmCurrentInfo()
                            .getGwID());
                    if(editInfo !=null ){
                        info.setSceneID(editInfo.getSceneID());
                        info.setStatus(editInfo.getStatus());
                        mode = CmdUtil.MODE_UPD;
                    }else{
                        info.setStatus(CmdUtil.SCENE_UNUSE);
                        mode = CmdUtil.MODE_ADD;
                    }
                    info.setIcon(chooseView.getCheckedItem().iconkey+"");
                    info.setName(name);
                    SendMessage.sendSetSceneMsg(context, info.getGwID(), mode,
                            info.getSceneID(), info.getName(), info.getIcon(),
                            info.getStatus(), false);
                }
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
            }

        }
        AddSceneListener addSceneListener = new AddSceneListener();
        WLDialog.Builder builder = new Builder(context);
        builder.setTitle(
                (editInfo == null) ? R.string.add : R.string.scene_info_rename_scene)
                .setContentView(chooseView)
                .setHeightPercent(0.6F)
                .setPositiveButton(
                        context.getResources().getString(
                                R.string.common_ok))
                .setNegativeButton(
                        context.getResources().getString(
                                R.string.cancel))
                .setListener(addSceneListener);
        dialog = builder.create();
        dialog.show();

    }

    public static void switchSceneInfo(Context context, SceneInfo editInfo,
                                       boolean progressDialogShow) {
        SendMessage.sendSetSceneMsg(context, editInfo.getGwID(),
                CmdUtil.MODE_SWITCH, editInfo.getSceneID(), null, null,
                editInfo.getStatus(), progressDialogShow);
    }
    /**
     * 场景管理类中提供--需要创建的默认场景的对应信息的List<SceneInfo>集合，通过默认场景管理类传给对应的Adapter
     */
    public static  List<SceneInfo> createDefaultScenes(Context context){
        ArrayList<SceneInfo> defaultSceneList = new ArrayList<SceneInfo>();
        SceneInfo defScene0 = new SceneInfo();
        SceneInfo defScene1 = new SceneInfo();
        SceneInfo defScene2 = new SceneInfo();
        SceneInfo defScene3 = new SceneInfo();
        SceneInfo defScene4 = new SceneInfo();
        SceneInfo defScene5 = new SceneInfo();

        defScene0.setIcon("0");
        defScene1.setIcon("1");
        defScene2.setIcon("2");
        defScene3.setIcon("4");
        defScene4.setIcon("9");
        defScene5.setIcon("10");

        defScene0.setName(context.getString(R.string.scene_default_back_home));
        defScene1.setName(context.getString(R.string.scene_icon_leave_hom));
        defScene2.setName(context.getString(R.string.scene_icon_sleep));
        defScene3.setName(context.getString(R.string.scene_icon_get_up));
        defScene4.setName(context.getString(R.string.scene_icon_all_on));
        defScene5.setName(context.getString(R.string.scene_icon_all_off));

        defaultSceneList.add(defScene0);
        defaultSceneList.add(defScene1);
        defaultSceneList.add(defScene2);
        defaultSceneList.add(defScene3);
        defaultSceneList.add(defScene4);
        defaultSceneList.add(defScene5);
        return defaultSceneList;
    }

    public static List<String> allLightType(){
        ArrayList<String> allLightDeviceList = new  ArrayList<String>();
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_2);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_3);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_4);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_AUS_DIMMING_LIGHT);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_LED);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_91_Temp_led);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_92);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_93_Module);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_1);
        allLightDeviceList.add(ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_2);
        return allLightDeviceList;
    }

    public static List<String> allDefenseSetupType(){
        ArrayList<String> allDefenseSetupList = new  ArrayList<String>();
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_WARNING);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_MOTION);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_CONTACT);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_MOTION_F);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_NH3);
//		allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_BARRIER);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_MOTION_LIGHT_S);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_DOORBELL_C);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_DOORBELL_S);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_IPADWARNING);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_ONETRANSLATOR);
        allDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_HUMANINDUCTOR);

        return allDefenseSetupList;
    }

    public static List<String> allLongDefenseSetupType(){
        ArrayList<String> allLongDefenseSetupList = new  ArrayList<String>();

        allLongDefenseSetupList.addAll(allDefenseSetupType());
        allLongDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_EMERGENCY);
        allLongDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_WATER);
        allLongDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_FIRE);
        allLongDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_GAS);
        allLongDefenseSetupList.add(ConstUtil.DEV_TYPE_FROM_GW_FIRE_SR);

        return allLongDefenseSetupList;
    }

    public static List<String> allSocketType(){
        ArrayList<String> allSocketList = new  ArrayList<String>();
        allSocketList.add(ConstUtil.DEV_TYPE_FROM_GW_DOCK);
        allSocketList.add(ConstUtil.DEV_TYPE_FROM_GW_DOCK_1);
        allSocketList.add(ConstUtil.DEV_TYPE_FROM_GW_DOCK_2);
        allSocketList.add(ConstUtil.DEV_TYPE_FROM_GW_EMS_SR);
        return allSocketList;
    }

    public static List<String> allCurtainType(){
        ArrayList<String> allCurtainList = new  ArrayList<String>();
        allCurtainList.add(ConstUtil.DEV_TYPE_FROM_GW_SHADE);
        allCurtainList.add(ConstUtil.DEV_TYPE_FROM_GW_BLIND);
        allCurtainList.add(ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_1);
        allCurtainList.add(ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_2);
        return allCurtainList;
    }

    public static List<String> allWaterValveType(){
        ArrayList<String> allWaterValveList = new  ArrayList<String>();
        allWaterValveList.add(ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE);
        return allWaterValveList;
    }
}
