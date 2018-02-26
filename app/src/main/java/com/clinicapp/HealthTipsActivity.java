package com.clinicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import util.CommonActivity;
import util.NameValuePair;

public class HealthTipsActivity extends CommonActivity {
    private ArrayList<HashMap<String,String>> postItems;
    private ArrayList<HashMap<String,String>> postCategories;

    public int current_page;
    public boolean loadingMore;
    public boolean stopLoadingData ;
    public boolean is_first_time;
    public int number_of_item;

    ListView listView;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    String parent_id;
    TipsAdapter adapter;
    CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);
        setHeaderLogo();
        allowBack();
        postItems = common.getParseObject("tips_list");
        postCategories = common.getParseObject("tips_Categories");

        number_of_item = 10;
        current_page = 0;
        loadingMore = true;
        stopLoadingData = true;
        is_first_time = true;


        listView = (ListView)findViewById(R.id.listView);
        adapter = new TipsAdapter();
        listView.setAdapter(adapter);
        loadHealthTips();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(HealthTipsActivity.this, DetailTipsActivity.class);

                Bundle bundle = new Bundle();
                    bundle.putString("id",postItems.get(position).get("tips_id"));
                    intent.putExtra("id",postItems.get(position).get("tips_id"));
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)) {
                    if (!(loadingMore)) {
                        if (stopLoadingData == false) {
                            // FETCH THE NEXT BATCH OF FEEDS
                            is_first_time = false;
                            loadHealthTips();
                        }
                    }
                }
            }
        });




        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        categoryAdapter = new CategoryAdapter();
        mDrawerList.setAdapter(categoryAdapter);

        loadCategories();
    }

    /**
     * Action menu Categories
      */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tips, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_category:
                drawerToggle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void drawerToggle(){
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen){
            mDrawerLayout.closeDrawer(mDrawerList);
        }else{
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerList.setItemChecked(position, true);
            HashMap<String, String> map = postCategories.get(position);
                parent_id = map.get("id");
                loadHealthTips();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
    private final Handler handlerCategory = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    postCategories=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    common.parseObject("tips_Categories",postCategories);
                    categoryAdapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    public void loadCategories(){
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.CATEGORY_TIPS_URL, handlerCategory, true, HealthTipsActivity.this);
        commonTask.execute();

    }
    class CategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return postCategories.size();
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return postCategories.get(i);

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
                view = mInflater.inflate(R.layout.row_tips_category, null);
            }
            HashMap<String, String> tips = postCategories.get(i);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageview);
            TextView tipTitle = (TextView)view.findViewById(R.id.txtTitle);

            Picasso.with(HealthTipsActivity.this).load(ConstValue.BASE_URL + "/uploads/category/" + tips.get("image")).into(imageView);
            tipTitle.setText(tips.get("title"));

            return view;
        }
    }

    /**
     * Action menu Categories
     */

    /**
     * Tips Listing
     */
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    if(is_first_time){
                        postItems.clear();
                    }
                    ArrayList<HashMap<String,String>> array=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    if (array.size() < number_of_item ){
                        stopLoadingData = true;
                        loadingMore = true;
                    }else{
                        stopLoadingData = false;
                        loadingMore = false;
                    }
                    for (int i = 0; i < array.size() ; i++ ) {
                        postItems.add(array.get(i));

                    }
                    common.parseObject("tips_list",postItems);
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    public void loadHealthTips(){
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("todays", "1"));
        nameValuePairs.add(new NameValuePair("offcet", String.valueOf(current_page)));
        nameValuePairs.add(new NameValuePair("number_row", String.valueOf(number_of_item)));
        if(parent_id!=null && parent_id!=""){
            nameValuePairs.add(new NameValuePair("cat_id", parent_id));
        }
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.GET_TIPS_URL, handler, true, HealthTipsActivity.this);
        commonTask.execute();

    }
    class TipsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return postItems.size();
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return postItems.get(i);

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
            HashMap<String, String> tips = postItems.get(i);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageview);
            TextView tipTitle = (TextView)view.findViewById(R.id.txtTitle);
            TextView tipDate = (TextView)view.findViewById(R.id.txtDate);

            Picasso.with(HealthTipsActivity.this).load(ConstValue.BASE_URL + "/uploads/tips/" + tips.get("tips_photo")).into(imageView);
            tipTitle.setText(tips.get("tips_title"));
            tipDate.setText(tips.get("on_date"));

            return view;
        }
    }
}
