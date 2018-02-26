package com.clinicapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import fragments.AboutFragment;
import fragments.PhotosFragment;
import fragments.ReviewsFragment;
import fragments.ServicesFragment;
import util.CommonActivity;
import util.NameValuePair;

public class InformationActivity extends CommonActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    HashMap<String,String> clinicInfo;
    String serviceJSON;
    TextView clinic_name;
    SampleFragmentPagerAdapter adapter;
    ImageView clinic_banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        setHeaderLogo();
        allowBack();
        clinicInfo = new HashMap<>();
        serviceJSON = "";
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        clinic_name = (TextView)findViewById(R.id.clinic_name);
        clinic_banner = (ImageView)findViewById(R.id.clinic_banner);
        adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.SERVICES_URL, handler, true, this);
        commonTask.execute();
    }
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>> outputArray =  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    HashMap<String,String> map = outputArray.get(0);

                    try {
                        clinicInfo = common.getMapJsonObject(new JSONObject(map.get("clinicinfo")));
                        serviceJSON = map.get("services");
                        clinic_name.setText(clinicInfo.get("bus_title"));
                        if(!clinicInfo.get("bus_logo").equalsIgnoreCase(""))
                        {
                            Picasso.with(getApplicationContext()).load(ConstValue.BASE_URL + "/uploads/business/" +clinicInfo.get("bus_logo")).into(clinic_banner);
                        }
                        viewPager.setAdapter(adapter);
                        tabLayout.setupWithViewPager(viewPager);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 4;
        private String tabTitles[] = new String[] { getString(R.string.tab_aboutus), getString(R.string.tab_services), getString(R.string.tab_photos) , getString(R.string.tab_reviews) };
        //private int tabIcons[] = {R.drawable.ic_service, R.drawable.ic_contact, R.drawable.ic_chat_s, R.drawable.ic_image};

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = new Bundle();

            switch (position){
                case 0:
                    fragment = new AboutFragment();
                    args.putSerializable("clincinfo",clinicInfo);
                    break;
                case 1:
                    fragment = new ServicesFragment();
                    args.putSerializable("services",serviceJSON);
                    break;
                case 2:
                    fragment = new PhotosFragment();
                    break;
                case 3:
                    fragment = new ReviewsFragment();
                    break;
            }
            if(fragment!=null) {
                fragment.setArguments(args);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];
        }
    }
}
