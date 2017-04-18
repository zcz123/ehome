package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.tools.StateDrawableFactory.Builder;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class SceneQuickInfoAdapter extends WLBaseAdapter<SceneInfo> {
    private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager.getInstance();
    public SceneQuickInfoAdapter(Context context, List<SceneInfo> data) {
        super(context, data);
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater,
                           ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.item_scene_home, parent, false);
    }

    @Override
    protected void bindView(Context context, View view, int pos, SceneInfo item) {
        ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
        TextView nameText = (TextView) view.findViewById(R.id.name);
        ImageView iconTime= (ImageView) view.findViewById(R.id.icon_scene_timing);

        String programType=getSceneTimingTask(item.getSceneID()).getProgramType();
        if(!StringUtil.isNullOrEmpty(programType) && StringUtil.equals(programType, "1")){
            iconTime.setVisibility(View.VISIBLE);
        }else{
            iconTime.setVisibility(View.INVISIBLE);
        }
        boolean isUsing = SceneManager.isSceneInUse(item.getStatus());

        Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(context, item.getIcon());
        Drawable checkedIcon = null;
        nameText.setText(item.getName());
        if (isUsing) {
            checkedIcon = SceneManager.getSceneIconDrawable_Bright(context, item.getIcon());
            nameText.setTextColor(mContext.getResources().getColor(R.color.action_bar_bg));
        } else {
            nameText.setTextColor(mContext.getResources().getColor(cc.wulian.app.model.device.R.color.text_color));
            checkedIcon = SceneManager.getSceneIconDrawable_Black(context, item.getIcon());
        }

        Builder builder = StateDrawableFactory.makeSimpleStateDrawable(context, normalIcon, checkedIcon);

        iconImage.setImageDrawable(builder.create());
        View state = view.findViewById(R.id.linearLayout_state);
        state.setSelected(isUsing);
        state.setOnClickListener(new OnClick(item));
    }

    private final class OnClick implements View.OnClickListener {

        private final SceneInfo item;

        public OnClick(SceneInfo item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            SceneInfo newSceneInfo = item.clone();
            if (SceneManager.isSceneInUse(newSceneInfo.getStatus())) {
                newSceneInfo.setStatus(CmdUtil.SCENE_UNUSE);
            } else {
                newSceneInfo.setStatus(CmdUtil.SCENE_USING);
            }
            SceneManager.switchSceneInfo(mContext, newSceneInfo, true);
        }

    }

    public AutoProgramTaskInfo getSceneTimingTask(String sceneID) {
        AutoProgramTaskInfo info = autoProgramTaskManager.getAutoProgramTypeTime(sceneID);
        if(info == null)
            info = new AutoProgramTaskInfo();
        return info;
    }

}
