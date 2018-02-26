package com.clinicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import configfcm.MyFirebaseRegister;
import getset.MenuRowdata;
import util.CommonActivity;
import util.NameValuePair;

public class MainActivity extends CommonActivity {
    ArrayList<MenuRowdata> menuArray;
    ArrayList<HashMap<String,String>> tipsArray;
    TipsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setHeaderLogo();

        if (!common.getSessionBool("fcm_registered")){
            MyFirebaseRegister fireReg = new MyFirebaseRegister(this);
            fireReg.RegisterUser(common.get_user_id());
        }

        menuArray = new ArrayList<>();
        menuArray.add(new MenuRowdata(getString(R.string.menu_appointment),R.mipmap.icon_appointment));
        menuArray.add(new MenuRowdata(getString(R.string.menu_doctor_info),R.mipmap.icon_doctor));
        menuArray.add(new MenuRowdata(getString(R.string.menu_ask_doct),R.mipmap.icon_chat));
        menuArray.add(new MenuRowdata(getString(R.string.menu_get_contact),R.mipmap.icon_contact));
        menuArray.add(new MenuRowdata(getString(R.string.menu_tips),R.mipmap.icon_tips));
        menuArray.add(new MenuRowdata(getString(R.string.menu_account),R.mipmap.icon_account));

        GridView gridView = (GridView)findViewById(R.id.gridMenu);
        MenuAdapter menuAdapter = new MenuAdapter();
        gridView.setAdapter(menuAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                    startActivity(intent);
                }
                if (i==1){
                    Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                    startActivity(intent);
                }
                if (i==2){
                    Intent intent = new Intent(MainActivity.this, AskMeActivity.class);
                    startActivity(intent);
                }
                if (i==3){
                    Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
                    startActivity(intent);
                }
                if (i==4){
                    Intent intent = new Intent(MainActivity.this, HealthTipsActivity.class);
                    startActivity(intent);
                }
                if (i==5){
                    Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                    startActivity(intent);
                }
            }
        });

        tipsArray = common.getParseObject("main_tips");
        ListView listView = (ListView)findViewById(R.id.listview);
        adapter = new TipsAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailTipsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("id",tipsArray.get(position).get("tips_id"));
                intent.putExtra("id",tipsArray.get(position).get("tips_id"));
                startActivity(intent);
            }
        });
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("todays","1"));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.GET_TIPS_URL, handler, true, MainActivity.this);
        commonTask.execute();

    }
    class MenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return menuArray.size();
        }

        @Override
        public MenuRowdata getItem(int i) {
            return menuArray.get(i);

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.row_menu, null);
            }
            ImageView menuIcon = (ImageView)view.findViewById(R.id.menuIcon);
            TextView menuTitle = (TextView)view.findViewById(R.id.menuTitle);

            MenuRowdata data = getItem(i);
            menuIcon.setImageResource(data.getDrawable());
            menuTitle.setText(data.getTitle());

            return view;
        }
    }


    /**
     * Tips Listing functions
     */
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    tipsArray=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    common.parseObject("main_tips",tipsArray);
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    class TipsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tipsArray.size();
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return tipsArray.get(i);

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.row_tips, null);
            }
            HashMap<String, String> tips = tipsArray.get(i);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageview);
            TextView tipTitle = (TextView)view.findViewById(R.id.txtTitle);
            TextView tipDate = (TextView)view.findViewById(R.id.txtDate);

            Picasso.with(MainActivity.this).load(ConstValue.BASE_URL + "/uploads/tips/" + tips.get("tips_photo")).into(imageView);
            tipTitle.setText(tips.get("tips_title"));
            tipDate.setText(tips.get("on_date"));

            return view;
        }
    }
}
