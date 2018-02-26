package com.clinicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import Config.ConstValue;
import util.CommonActivity;

public class MyAccountActivity extends CommonActivity {
    LanguagePrfsDialog languagePrfsDialog;
    Button btnLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        languagePrfsDialog = new LanguagePrfsDialog(this);
        btnLanguage = (Button)findViewById(R.id.btnlanguage);
        btnLanguage.setText(languagePrfsDialog.getLanguage());
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languagePrfsDialog.show();
            }
        });
        setHeaderLogo();
        allowBack();

        if (ConstValue.ALLOW_ARABIC_LANG)
            btnLanguage.setVisibility(View.VISIBLE);
        else
            btnLanguage.setVisibility(View.GONE);

    }
    public void updateProfile(View view){
        Intent intent = new Intent(MyAccountActivity.this,UpdateProfileActivity.class);
        startActivity(intent);
    }
    public void changePassword(View view){
        Intent intent = new Intent(MyAccountActivity.this,ChangePasswordActivity.class);
        startActivity(intent);
    }
    public void myAppoitment(View view){
        Intent intent = new Intent(MyAccountActivity.this,MyAppointmentsActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_doctor_info, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                common.logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
