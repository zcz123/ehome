package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneView;

public class TimingSceneAdapter extends WLBaseAdapter<TimingSceneEntity>
{
	public TimingSceneAdapter( Context context, List<TimingSceneEntity> data )
	{
		super(context, data);
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ){
		return new TimingSceneView(context);
	}

	@Override
	protected void bindView( Context context, View view, int pos, TimingSceneEntity item ){
		TimingSceneView tsView = (TimingSceneView) view;
		// TODO why add params group
//		tsView.attachTimingScene(null, item, this);
	}
}
