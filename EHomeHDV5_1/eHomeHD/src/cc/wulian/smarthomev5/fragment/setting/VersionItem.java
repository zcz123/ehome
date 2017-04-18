package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.UpdateManger;
import cc.wulian.smarthomev5.tools.UpdateManger.NewVersionDownloadListener;
import cc.wulian.smarthomev5.utils.VersionUtil;

import com.yuantuo.customview.ui.WLToast;

public class VersionItem extends AbstractSettingItem {

    private UpdateManger updateManager;
    private IconClickListener iconClickListener;
    long[] mHints = new long[5];

    public VersionItem(Context context) {
        super(context, R.drawable.icon_current_version, context.getResources()
                .getString(R.string.set_version_update));
        updateManager = UpdateManger.getInstance(context);
    }

    public void setIconClickListener(IconClickListener iconClickListener) {
        this.iconClickListener = iconClickListener;
    }

    @Override
    public void initSystemState() {
        super.initSystemState();
        infoTextView.setVisibility(View.VISIBLE);
        remindBadgeView.setVisibility(View.VISIBLE);
        final String sysVersion = "V" + VersionUtil.getVersionName(mContext);
        infoTextView.setText(sysVersion);
        if (mContext.getResources().getBoolean(R.bool.use_update)) {
            checkNewVersion();
        }

        //左侧图标连续点击五次 事件
      iconImageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
              mHints[mHints.length - 1] = SystemClock.uptimeMillis();
              if ((SystemClock.uptimeMillis()-mHints[0]) <= 2000)
              {
                  Preference.getPreferences().setInstalServiceToolActivity();
                  iconClickListener.onIconClick();
              }
          }
      });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                WLToast.showToast(
                        mContext,
                        sysVersion + "_" + VersionUtil.getVersionCode(mContext),
                        Toast.LENGTH_SHORT);
                return true;
            }
        });

    }

    public void checkNewVersion() {
        if (updateManager.isNewVersion()) {
            remindBadgeView.show();
            updateManager.setNewVersionDownloadListener(new NewVersionDownloadListener() {

                @Override
                public void processing(int present) {
                    infoTextView.setText(present + "%");
                    if (present >= 100) {
                        updateManager.startInstall();
                    }
                }

                @Override
                public void processError(Exception e) {
                    infoTextView.setText(R.string.set_version_update_erro);
                }
            });
        }
    }

    @Override
    public void doSomethingAboutSystem() {
        if (!mContext.getResources().getBoolean(R.bool.use_update)) {
            return;
        }
        if (updateManager.isNewVersion())
            if (updateManager.isRunning()) {
                WLToast.showToast(mContext, mContext.getResources().getString(R.string.set_version_update_running), Toast.LENGTH_SHORT);
                return;
            } else {
                updateManager.checkUpdate(false);
            }
        else {
            WLToast.showToast(
                    mContext,
                    mContext.getResources().getString(
                            R.string.set_version_update_nothing),
                    Toast.LENGTH_SHORT);
        }
    }

    public interface IconClickListener{
        public void onIconClick();
    }

}
