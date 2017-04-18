package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.InputMethodUtils;

import com.yuantuo.customview.ui.CustomToast;

public class EditDoorLockFragment extends WulianFragment implements ICommand406_Result {

    public static final String DEVICE_DOOR_LOCK_12 = "DEVICE_DOOR_LOCK_12";
    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    public static final String DEVICE_PASS_WORD_69 = "DEVICE_PASS_WORD_69";
    private static final String DOOR_LOCK_ACCOUNT_KEY_PAD = "DOOR_LOCK_ACCOUNT_KEY_PAD";
    private EditText oldpwdEditText;
    private EditText newpwdEditText;
    private EditText commitpwdEditText;

    private String oldPassword;
    private String newPassword;
    private String commitPassword;

    private Preference mPreference = Preference.getPreferences();
    private String gwID;
    private String deviceID;
    private WulianDevice DoorDevice;
    private String lockPass;
    private Command406_DeviceConfigMsg command406;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        gwID = (String) bundle.getString(GWID);
        deviceID = (String) bundle.getString(DEVICEID);
        lockPass = bundle.getString(DEVICE_PASS_WORD_69);
        DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
                mActivity, gwID, deviceID);
        initBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_door_lock_password_change, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oldpwdEditText = (EditText) view.findViewById(R.id.oldpwdEditText);
        oldpwdEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        oldpwdEditText.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        oldpwdEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4),
                new LoginFilter.PasswordFilterGMail()});
        newpwdEditText = (EditText) view.findViewById(R.id.newpwdEditText);
        commitpwdEditText = (EditText) view
                .findViewById(R.id.commitpwdEditText);
        newpwdEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        newpwdEditText.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        commitpwdEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        commitpwdEditText.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        newpwdEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4),
                new LoginFilter.PasswordFilterGMail()});
        commitpwdEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4),
                new LoginFilter.PasswordFilterGMail()});

        oldpwdEditText.setHint(getString(R.string.device_door_passwords_hint));
        newpwdEditText.setHint(getString(R.string.device_door_passwords_hint));
        commitpwdEditText.setHint(getString(R.string.device_door_passwords_hint));

    }

    private void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowMenuTextEnabled(true);
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.device_ir_setting));
        getSupportActionBar().setRightIconText(
                getResources().getString(R.string.device_ir_save));
        getSupportActionBar().setRightMenuClickListener(
                new OnRightMenuClickListener() {

                    @Override
                    public void onClick(View v) {
                        saveChangePW();
                    }
                });
        getSupportActionBar().setLeftIconClickListener(
                new OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
    }

    private void saveChangePW() {
        getValueFromView();
        confirmBeforeCommitDoorPwd();

    }

    private void getValueFromView() {

        oldPassword = oldpwdEditText.getText().toString();
        newPassword = newpwdEditText.getText().toString();
        commitPassword = commitpwdEditText.getText().toString();
    }

    private boolean confirmBeforeCommitDoorPwd() {
        boolean whetherCommitNewPwd = true;
        TextView errorView = null;
        String newPwd = newpwdEditText.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(oldPassword)) {
            whetherCommitNewPwd = false;
            errorView = oldpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.set_password_not_null_hint));
        } else if (StringUtil.isNullOrEmpty(newPassword)) {
            whetherCommitNewPwd = false;
            errorView = newpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.set_password_not_null_hint));
        } else if (StringUtil.isNullOrEmpty(commitPassword)) {
            whetherCommitNewPwd = false;
            errorView = commitpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.set_password_not_null_hint));
        } else if (oldPassword.length() < 4) {
            whetherCommitNewPwd = false;
            errorView = oldpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_door_passwords_length_hint));
        } else if (newPassword.length() < 4) {
            whetherCommitNewPwd = false;
            errorView = newpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_door_passwords_length_hint));
        } else if (commitPassword.length() < 4) {
            whetherCommitNewPwd = false;
            errorView = commitpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_door_passwords_length_hint));
        } else if (containWhiteSpace(oldPassword)) {
            whetherCommitNewPwd = false;
            errorView = oldpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_passwords_space_hint));
        } else if (containWhiteSpace(newPassword)) {
            whetherCommitNewPwd = false;
            errorView = newpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_passwords_space_hint));
        } else if (containWhiteSpace(commitPassword)) {
            whetherCommitNewPwd = false;
            errorView = commitpwdEditText;
            errorView.requestFocus();
            errorView.setError(resources
                    .getString(R.string.device_passwords_space_hint));
        }
        if (errorView == null) {
//			String originalPwdCheck = mPreference.getString(
//					IPreferenceKey.P_KEY_DEVICE_DOOR_LOCK_PWD,
//					AbstractDoorLock.WINDOWS_PWD_MD5);
			oldPassword = MD5Util.encrypt(oldPassword);
            if (!TextUtils.equals(lockPass, oldPassword)) {
                errorView = oldpwdEditText;
                errorView.requestFocus();
                errorView.setError(resources
                        .getString(R.string.device_account_old_password_not_correct));
                whetherCommitNewPwd = false;
            } else {
                if (!TextUtils.equals(newPassword, commitPassword)) {
                    errorView = newpwdEditText;
                    errorView.requestFocus();
                    errorView
                            .setError(resources
                                    .getString(R.string.set_account_manager_modify_gw_password_new_compare_sure_unequal));
                    newpwdEditText.setText("");
                    commitpwdEditText.setText("");
                    whetherCommitNewPwd = false;
                } else if (TextUtils.equals(oldPassword, MD5Util.encrypt(newPassword))) {
                    errorView = newpwdEditText;
                    errorView.requestFocus();
                    errorView
                            .setError(resources
                                    .getString(R.string.set_account_new_old_passwords));
                    newpwdEditText.setText("");
                    commitpwdEditText.setText("");
                    whetherCommitNewPwd = false;
                } else {
                    updateWindowsInfo(MD5Util.encrypt(newPwd));
//					CustomToast
//							.showToast(
//									getActivity(),
//									resources
//											.getString(R.string.device_account_modify_password_success),
//									CustomToast.LENGTH_LONG, false);
//					oldpwdEditText.setText("");
//					newpwdEditText.setText("");
//					commitpwdEditText.setText("");

                }

            }
        }
        return whetherCommitNewPwd;

    }

    public static boolean containWhiteSpace(String input) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(input);
        boolean found = matcher.find();
        return found;
    }

    private void updateWindowsInfo(String doorpwd) {
//		doorpwd = MD5Util.encrypt(doorpwd);
//		mPreference.putString(IPreferenceKey.P_KEY_DEVICE_DOOR_LOCK_PWD,
//				doorpwd);
        if (command406 == null) {
            command406 = new Command406_DeviceConfigMsg(getActivity());
            command406.setConfigMsg(this);
            command406.setDevID(deviceID);
            command406.setGwID(gwID);
        }
        String paw = "{\"pass\" : \""+doorpwd+"\"}";
		mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY_PAD, getActivity(), null, null);
        command406.SendCommand_Update("lock_pass", paw);
    }

    @Override
    public void Reply406Result(Command406Result result) {
        oldpwdEditText.setText("");
        newpwdEditText.setText("");
        commitpwdEditText.setText("");
        mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY_PAD, 0);
        CustomToast
                .showToast(
                        getActivity(),
                        resources
                                .getString(R.string.device_account_modify_password_success),
                        CustomToast.LENGTH_LONG, false);
        // 如果输入法显示,就隐藏
        if (InputMethodUtils.isShow(mActivity)) {
            InputMethodUtils.hide(mActivity);
        }
    }

    @Override
    public void Reply406Result(List<Command406Result> results) {

    }
}
