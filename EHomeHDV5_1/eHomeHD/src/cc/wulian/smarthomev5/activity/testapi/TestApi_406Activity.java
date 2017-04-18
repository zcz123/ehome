package cc.wulian.smarthomev5.activity.testapi;


import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.testApi.testApi406Fragment;

public class TestApi_406Activity extends EventBusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            Bundle args=getIntent().getBundleExtra("args");
            testApi406Fragment testFragment=new testApi406Fragment();
            testFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, testFragment).commit();
        }
    }


}
