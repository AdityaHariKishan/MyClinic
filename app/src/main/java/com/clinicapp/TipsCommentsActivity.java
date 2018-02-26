package com.clinicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import fragments.ReviewsFragment;
import util.CommonActivity;
import util.NameValuePair;

public class TipsCommentsActivity extends CommonActivity {
    ArrayList<HashMap<String,String>> arrayList;
    ListView listReviewsView;
    RatingBar ratingbar;
    EditText txtComment;
    Button btnAddReview;
    RelativeLayout topLayout;
    AlertDialog alertDialog;
    ReviewsAdapter adapter;
    String tips_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_comments);
        arrayList = new ArrayList<>();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        tips_id = getIntent().getExtras().getString("tips_id");
        nameValuePairs.add(new NameValuePair("tips_id",tips_id));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.TIPS_COMMENTS_URL, handler, true, TipsCommentsActivity.this);
        commonTask.execute();

        listReviewsView = (ListView)findViewById(R.id.listView);
        adapter = new ReviewsAdapter();
        listReviewsView.setAdapter(adapter);

    }
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    arrayList=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    class ReviewsAdapter extends BaseAdapter {

        LayoutInflater inflater;

        public ReviewsAdapter() {
            inflater = LayoutInflater.from(TipsCommentsActivity.this);
        }

        @Override
        public int getCount() {

            if (arrayList == null)
                return 0;
            else
                return arrayList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {

            return arrayList.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.row_reviews, null);

            TextView textReview = (TextView) convertView.findViewById(R.id.txtReview);
            ImageView imageIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            TextView textUser = (TextView) convertView.findViewById(R.id.txtUser);
            TextView textDate = (TextView) convertView.findViewById(R.id.txtDate);
            HashMap<String, String> obj = arrayList.get(position);
            textReview.setText(obj.get("reviews"));
            textUser.setText(obj.get("user_fullname"));
            textDate.setText(obj.get("on_date"));
            if(!obj.get("user_image").equalsIgnoreCase(""))
                Picasso.with(TipsCommentsActivity.this).load(ConstValue.BASE_URL + "/uploads/profile/" + obj.get("user_image")).into(imageIcon);

            RatingBar ratingBar1 = (RatingBar)convertView.findViewById(R.id.ratingBar1);
            ratingBar1.setRating(Float.valueOf(obj.get("ratings")));

            TextView datetime = (TextView) convertView.findViewById(R.id.datetime);
            datetime.setText(obj.get("on_date"));
            return convertView;
        }
    }


    public void openReviewDialog(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(TipsCommentsActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_review, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ratingbar = (RatingBar)view.findViewById(R.id.ratingBar1);
        txtComment = (EditText)view.findViewById(R.id.editText1);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Add Review", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        submitReview(txtComment.getText().toString(),String.valueOf(ratingbar.getRating()));
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void submitReview(String commentText, String ratings){
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("tips_id", tips_id));
        nameValuePairs.add(new NameValuePair("user_id", common.get_user_id()));
        nameValuePairs.add(new NameValuePair("reviews", commentText));
        nameValuePairs.add(new NameValuePair("rating", ratings));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.TIPS_ADDCOMMENT_URL, handler2, true, TipsCommentsActivity.this);
        commonTask.execute();
    }
    private final Handler handler2 = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>>  array=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    arrayList.add(array.get(0));
                    adapter.notifyDataSetChanged();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
}
