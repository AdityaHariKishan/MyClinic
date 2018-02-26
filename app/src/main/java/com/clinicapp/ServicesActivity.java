package com.clinicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import getset.ServiceRowdata;
import util.CommonActivity;
import util.NameValuePair;

public class ServicesActivity extends CommonActivity {
    ArrayList<ServiceRowdata> serviceArray;
    ListView listview;
    ServiceAdapter adapter;
    HashMap<String,String> clinicInfo;
    TextView clinicfees, txttotalAmount;
    //HashMap<String,String> slot;

    View tapToContinue;
    Bundle passBundle;
    ArrayList<String> choosen_services;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        setHeaderLogo();
        allowBack();
        serviceArray = new ArrayList<>();
        choosen_services = new ArrayList<>();
        //slot = (HashMap<String, String>)getIntent().getExtras().getSerializable("slot");
        passBundle = getIntent().getExtras();
        listview = (ListView)findViewById(R.id.servicelist);
        adapter = new ServiceAdapter();
        listview.setAdapter(adapter);
        clinicfees = (TextView)findViewById(R.id.clinicfees);
        txttotalAmount = (TextView)findViewById(R.id.totlaAmount);
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.SERVICES_URL, handler, true, this);
        commonTask.execute();

        tapToContinue = (View)findViewById(R.id.bottomPanel);
        tapToContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServicesActivity.this,PersonInfoActivity.class);
                passBundle.putSerializable("services",choosen_services);
                intent.putExtras(passBundle);
                startActivity(intent);
            }
        });
    }
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>> outputArray =  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    HashMap<String,String> map = outputArray.get(0);
                    JSONArray loginArray = new JSONArray();
                    try {
                        clinicInfo = common.getMapJsonObject(new JSONObject(map.get("clinicinfo")));
                        clinicfees.setText(getCurrencyAmount(clinicInfo.get("bus_fee")));
                        loginArray = new JSONArray(map.get("services"));
                        addTotalAmount();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int  i = 0 ; i < loginArray.length(); i++){
                        JSONObject jmap = null;
                        try {
                            jmap = loginArray.getJSONObject(i);
                            ServiceRowdata row = new ServiceRowdata(false,i,jmap.getString("id"),jmap.getString("service_price"),jmap.getString("service_discount"),jmap.getString("service_title"));
                            serviceArray.add(row);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    class ServiceAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(serviceArray!= null)
                return serviceArray.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
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
                view = mInflater.inflate(R.layout.row_service_charge, null);


            }
            final ServiceRowdata inforow = serviceArray.get(i);
            TextView txtPrice = (TextView)view.findViewById(R.id.textView1);
            txtPrice.setText(getCurrencyAmount(inforow.getDiscountAmount()));

            final CheckBox chbox = (CheckBox)view.findViewById(R.id.checkBox1);
            chbox.setText(inforow.getServiceName());
            chbox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    inforow.setChecked(chbox.isChecked());
                    addTotalAmount();
                }
            });

            return view;
        }
    }
    public void addTotalAmount(){
        double totalAmount = Double.parseDouble(clinicInfo.get("bus_fee"));
        for (int i = 0; i < serviceArray.size(); i++) {
            ServiceRowdata inforow = serviceArray.get(i) ;
            if(inforow.isChecked()){
                totalAmount = totalAmount + Double.parseDouble(inforow.getDiscountAmount());
                choosen_services.add(inforow.getServiceId());
            }
        }

        txttotalAmount.setText(getCurrencyAmount(String.format(Locale.ENGLISH, "%.2f", totalAmount)));

    }
}
