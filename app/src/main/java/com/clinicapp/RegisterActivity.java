package com.clinicapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import util.CommonActivity;
import util.NameValuePair;

public class RegisterActivity extends CommonActivity {
    EditText editEmail, editPassword, editPhone, editFullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setHeaderLogo();
        allowBack();
        editEmail = (EditText)findViewById(R.id.txtEmail);
        editPassword = (EditText)findViewById(R.id.txtPassword);
        editFullname = (EditText)findViewById(R.id.txtFirstname);
        editPhone = (EditText)findViewById(R.id.txtPhone);

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
        String password = editPassword.getText().toString();
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
        if (TextUtils.isEmpty(password)) {
            common.setToastMessage(getString(R.string.valid_required_password));
            focusView = editPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(fullname)) {
            common.setToastMessage(getString(R.string.valid_required_fullname));
            focusView = editFullname;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            common.setToastMessage(getString(R.string.valid_required_password));
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
            nameValuePairs.add(new NameValuePair("user_password",password));
            CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.REGISTER_URL, handler, true, this);
            commonTask.execute();
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
                        HashMap<String, String> userdata = loginArray.get(0);
                        Intent intent = null;
                        common.setSession(ApiParams.COMMON_KEY, userdata.get("user_id"));
                        common.setSession(ApiParams.USER_FULLNAME, userdata.get("user_fullname"));
                        common.setSession(ApiParams.USER_EMAIL, userdata.get("user_email"));
                        common.setSession(ApiParams.USER_PHONE,userdata.get("user_phone"));
                        intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
}
