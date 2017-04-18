package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.about.AboutMessageFragment;

public class AboutusActivity extends EventBusActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, new AboutMessageFragment()).commit();
    }

    @Override
    protected boolean finshSelf() {
        return false;
    }

}