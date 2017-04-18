package cc.wulian.smarthomev5.fragment.uei;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class TopBoxEpgFragment extends WulianFragment {
	H5PlusWebView epgWebView=null;
	private View rootView;
	private String webUrl;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=getArguments();
		if(args!=null){
			webUrl=args.getString("webUrl");
		}
//		initBar();
	}
	private void initBar() {
//		this.mActivity.resetActionMenu();
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setDisplayIconEnabled(true);
//		getSupportActionBar().setDisplayIconTextEnabled(true);
//		getSupportActionBar().setDisplayShowTitleEnabled(true);
//		getSupportActionBar().setDisplayShowMenuEnabled(false);
//		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
//		getSupportActionBar().setTitle("测试");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView= inflater.inflate(R.layout.fragment_topboxepg, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View paramView, Bundle savedInstanceState) {
		super.onViewCreated(paramView, savedInstanceState);
		initView(paramView);
	}

	private void initView(View paramView){
		epgWebView= (H5PlusWebView) paramView.findViewById(R.id.epg_webview);

		if(!StringUtil.isNullOrEmpty(webUrl)){
			epgWebView.loadUrl(webUrl);
		}
	}

	public String getWebUrl(){
		String webUrl="";
		if(epgWebView!=null){
			if(!StringUtil.isNullOrEmpty(epgWebView.getUrl())){
				webUrl=epgWebView.getUrl();
			}
		}
		return webUrl;
	}
}
