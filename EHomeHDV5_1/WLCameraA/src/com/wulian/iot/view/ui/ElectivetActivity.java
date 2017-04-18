package com.wulian.iot.view.ui;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.wulian.icam.R;
import com.wulian.iot.view.adapter.SimpleAdapter;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import java.io.Serializable;
import java.util.List;
/**
 * Created by syf on 2016/10/14.
 */
@SuppressLint("NewApi")
public class ElectivetActivity extends SimpleFragmentActivity {
    private ListView eleListView = null;
    private TextView txtBar = null;
    private ImageView imgBack = null;
    private ElectivetAdapter electivetAdapter =null;
    List<ElectivetPojo> electivetPojos = null;

    public void setElectivetPojos(List<ElectivetPojo> electivetPojos) {
        this.electivetPojos = electivetPojos;
    }

    public List<ElectivetPojo> getElectivetPojos() {
        return electivetPojos;
    }

    @Override
    public void root() {
        setContentView(R.layout.activity_electivet_config);
    }

    @Override
    public void initView() {
        eleListView = (ListView) findViewById(R.id.electivet_item);
        txtBar = (TextView)findViewById(R.id.titlebar_title);
        imgBack = (ImageView)findViewById(R.id.titlebar_back);
    }
    @Override
    public void initData() {
        int type = getIntent().getIntExtra("check",-1);
        if(type==0){
            txtBar.setText(R.string.desk_broadcast_language);
        }else if(type==1){
            txtBar.setText(R.string.set_resolving_power);
        }else if(type ==2){
            txtBar.setText(R.string.dt_red_vision);
        }else if (type == 3){
            txtBar.setText(R.string.camera_set_volume);
        }
        setElectivetPojos((List<ElectivetPojo>) getIntent().getSerializableExtra("datas"));
        bindAdapter();
    }

    @Override
    public void initEvents() {
        imgBack.setOnClickListener(onClickListener);
    }
    private void bindAdapter(){
        if(electivetAdapter==null){
            electivetAdapter = new ElectivetAdapter(null,this);
            eleListView.setAdapter(electivetAdapter);
            eleListView.setOnItemClickListener(onItemClickListener);
        }
        electivetAdapter.swapData(getElectivetPojos());
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animationExit();
        }
    };
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(electivetAdapter!=null){
                electivetAdapter.restore();
                electivetAdapter.check(electivetAdapter.getItem(position));
                setResult(RESULT_OK,new Intent().putExtra("code",electivetAdapter.getItem(position).getValue()));
                animationExit();
            }
        }
    };
    private class ElectivetAdapter extends SimpleAdapter<ElectivetPojo>{
        public ElectivetAdapter(List<ElectivetPojo> electivetPojos, Context context){
            super(context,electivetPojos);
        }
        @Override
        public View view(int position, View convertView, ViewGroup parent) {
            ElectivetPojo electivetPojo = eList.get(position);
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_electivet,null);
                viewHolder.check = (ImageView) convertView.findViewById(R.id.electivet_img);
                viewHolder.key = (TextView) convertView.findViewById(R.id.electivet_key);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.key.setText(electivetPojo.getKey());
            if(electivetPojo.isCheck()){
                viewHolder.check.setVisibility(View.VISIBLE);
            } else {
                viewHolder.check.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
        private class ViewHolder{
            private TextView key;
            private ImageView check;
        }
        private void restore(){
            for(ElectivetPojo pojo:eList){
                pojo.setCheck(false);
                notifyDataSetChanged();
            }
        }
        private void check(ElectivetPojo pojo){
            pojo.setCheck(true);
            notifyDataSetChanged();
        }
    }
    public static class ElectivetPojo implements Serializable {
        private int requestCode;
        public String key;
        public int value;
        public boolean isCheck;
        public ElectivetPojo(){

        }
        public ElectivetPojo(int requestCode,String key, int value, boolean isCheck) {
            this.requestCode = requestCode;
            this.key = key;
            this.value = value;
            this.isCheck = isCheck;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }
}
