package com.clinicapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;



import java.util.Locale;

import Config.ConstValue;


public class LanguagePrfsDialog extends Dialog {

    private Activity context;
    private Button btnEnglish, btnArabic;
    private SharedPreferences sharedPreferences;

    public LanguagePrfsDialog(final Activity context) {
        super(context);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.dialog_language_selection);
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        } catch (Exception ex) {

        }

        this.btnEnglish = (Button) this.findViewById(R.id.btnEnglish);
        this.btnArabic = (Button) this.findViewById(R.id.btnArabic);
        this.btnEnglish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var1) {

                saveLanguage("en");
                initRTL("en");
                LanguagePrfsDialog.this.hide();
                dismiss();
                Intent i1 = new Intent(context, MainActivity.class);
                context.startActivity(i1);
                context.finish();
            }
        });

        this.btnArabic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var1) {
                saveLanguage("ar");
                initRTL("ar");
                LanguagePrfsDialog.this.hide();
                dismiss();
                Intent i1 = new Intent(context, MainActivity.class);
                context.startActivity(i1);
                context.finish();
            }
        });
    }

    public void saveLanguage(String language) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ConstValue.PREFS_LANGUAGE, language);


            editor.apply();
            language =   language.equalsIgnoreCase("ar")  ? "Arabic" : "English";
            //Toast.makeText(context, language + " is your preferred language.", Toast.LENGTH_SHORT).show();
        } catch (Exception exc) {

        }


    }
    public String getLanguage(){
        return sharedPreferences.getString(ConstValue.PREFS_LANGUAGE,"en");
    }
    public void initRTL(String lang) {

        if (lang.equalsIgnoreCase("ar")) {

            Resources res = context.getResources();
            Configuration newConfig = new Configuration(res.getConfiguration());
            Locale locale = new Locale("ar");
            newConfig.setLocale(locale);
            newConfig.setLayoutDirection(locale);
            res.updateConfiguration(newConfig, null);

        }else{

            Resources res = context.getResources();
            Configuration newConfig = new Configuration(res.getConfiguration());
            Locale locale = new Locale("en");
            newConfig.setLocale(locale);
            newConfig.setLayoutDirection(locale);
            res.updateConfiguration(newConfig, null);

        }
    }
    public void dismiss() {
        super.dismiss();
    }
}
