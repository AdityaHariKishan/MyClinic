package com.clinicapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import util.CommonActivity;
import util.NameValuePair;
import util.NotifyService;

public class PersonInfoActivity extends CommonActivity {
    EditText editEmail,  editPhone, editFullname;
    HashMap<String,String> timeSlot;
    ArrayList<String> services;
    String choose_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        setHeaderLogo();
        allowBack();
        timeSlot = (HashMap<String, String>)getIntent().getExtras().getSerializable("slot");
        services = (ArrayList<String>)getIntent().getExtras().getSerializable("services");
        choose_date = getIntent().getExtras().getString("date");
        editEmail = (EditText)findViewById(R.id.txtEmail);
        editFullname = (EditText)findViewById(R.id.txtFirstname);
        editPhone = (EditText)findViewById(R.id.txtPhone);

        editEmail.setText(common.getSession(ApiParams.USER_EMAIL));
        editFullname.setText(common.getSession(ApiParams.USER_FULLNAME));
        editPhone.setText(common.getSession(ApiParams.USER_PHONE));

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    public void register(){

        String email = editEmail.getText().toString();
        String fullname = editFullname.getText().toString();
        String phone = editPhone.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            common.setToastMessage(getString(R.string.valid_required_email));
            focusView = editEmail;
            cancel = true;
        }
        if (!isValidEmail(email)) {
            common.setToastMessage(getString(R.string.valid_email));
            focusView = editEmail;
            cancel = true;
        }
        if (TextUtils.isEmpty(fullname)) {
            common.setToastMessage(getString(R.string.valid_required_fullname));
            focusView = editFullname;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            common.setToastMessage(getString(R.string.valid_required_phone));
            focusView = editPhone;
            cancel = true;
        }
        if (cancel) {
            if (focusView!=null)
                focusView.requestFocus();
        } else {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new NameValuePair("user_fullname",fullname));
            nameValuePairs.add(new NameValuePair("user_phone",phone));
            nameValuePairs.add(new NameValuePair("user_email",email));
            nameValuePairs.add(new NameValuePair("start_time",timeSlot.get("slot")));
            nameValuePairs.add(new NameValuePair("time_token",timeSlot.get("time_token")));
            nameValuePairs.add(new NameValuePair("appointment_date",choose_date));
            nameValuePairs.add(new NameValuePair("user_id",common.get_user_id()));

            String str_services = "";
            for (int i = 0; i < services.size(); i++) {

                    if(str_services.equalsIgnoreCase("")){
                        str_services = services.get(i);
                    }else {
                        str_services = str_services+","+services.get(i);
                    }

            }
            nameValuePairs.add(new NameValuePair("services",str_services));
            if (ConstValue.enable_paypal){
                CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.BOOKAPPOINTMENT_TEMP_URL, handler_temp, true, this);
                commonTask.execute();

            }else{
                CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.BOOKAPPOINTMENT_URL, handler, true, this);
                commonTask.execute();

            }
        }

    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>> loginArray =  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    if(loginArray!=null) {
                        HashMap<String, String> appointmentData = loginArray.get(0);
                        String messageType = "Your appointment is booked successfully, Appointment ID : "+appointmentData.get("id")+ " visit My Appointment for more details";
                        common.setToastMessage("Your appointment is booked successfully, Appointment ID : "+appointmentData.get("id")+ " visit My Appointment for more details");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss", Locale.UK);
                        try {
                            Date testDate = formatter.parse(choose_date);
                            Date testTime = formatter2.parse(timeSlot.get("slot"));
                            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);


                            Calendar myAlarmDate = Calendar.getInstance();
                            myAlarmDate.setTimeInMillis(System.currentTimeMillis());
                            myAlarmDate.set(testDate.getYear(), testDate.getMonth(), testDate.getDate(), testTime.getHours(), testTime.getMinutes(), 0);

                            Intent _myIntent = new Intent(PersonInfoActivity.this, NotifyService.class);
                            _myIntent.putExtra("MyMessage",messageType);
                            PendingIntent _myPendingIntent = PendingIntent.getBroadcast(PersonInfoActivity.this, 123, _myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(),_myPendingIntent);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        Intent intent = null;

                        intent = new Intent(PersonInfoActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };

    private final Handler handler_temp = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>> loginArray =  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    if(loginArray!=null) {
                        HashMap<String, String> appointmentData = loginArray.get(0);

                        Intent intent = null;

                        intent = new Intent(PersonInfoActivity.this, PaymentActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("order_details",appointmentData);
                        intent.putExtras(b);
                        startActivity(intent);

                    }
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
}
