package com.clinicapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import util.CommonActivity;
import util.NameValuePair;
import util.SwipeDetector;

public class MyAppointmentsActivity extends CommonActivity {
    ArrayList<HashMap<String,String>> appointmentArray;
    AppointmentAdapter adapter;
    SwipeDetector swipeDetector;
    AlertDialog alertDialog;
    int selected_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        setHeaderLogo();
        allowBack();

        swipeDetector = new SwipeDetector();
        appointmentArray = common.getParseObject("my_appointments");
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setOnTouchListener(swipeDetector);
        adapter = new AppointmentAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        deleteConfirm(position);
                    } else {

                    }
                }

            }
        });
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("user_id",common.get_user_id()));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.MYAPPOINTMENTS_URL, handlerUserDate, true, this);
        commonTask.execute();
    }
    public void deleteConfirm(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cancel_confirmation)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        selected_index = position;
                        HashMap<String,String> map = appointmentArray.get(position);
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new NameValuePair("user_id",common.get_user_id()));
                        nameValuePairs.add(new NameValuePair("app_id",map.get("id")));
                        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.CANCELAPPOINTMENTS_URL, handlerCancel, true, MyAppointmentsActivity.this);
                        commonTask.execute();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }
    private final Handler handlerCancel = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    appointmentArray.remove(selected_index);
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    private final Handler handlerUserDate = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    appointmentArray =  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    common.parseObject("my_appointments",appointmentArray);
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    class AppointmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (appointmentArray==null)
                return 0;
            else
                return appointmentArray.size();
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return appointmentArray.get(i);

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
                view = mInflater.inflate(R.layout.row_appointment, null);
            }
            HashMap<String, String> appointment = appointmentArray.get(i);

            TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
            TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
            TextView txtName = (TextView)view.findViewById(R.id.txtName);
            TextView txtPhone = (TextView)view.findViewById(R.id.txtPhone);

            txtDate.setText(parseDateToddMM(appointment.get("appointment_date")));
            txtTime.setText(parseTime(appointment.get("start_time")));
            txtName.setText(appointment.get("app_name"));
            txtPhone.setText(appointment.get("app_phone"));
            return view;
        }
    }
    public String parseDateToddMM(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public String parseTime(String time) {
        String inputPattern = "HH:mm:ss";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog!=null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
