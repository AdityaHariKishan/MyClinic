package com.clinicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import util.CommonActivity;
import util.NameValuePair;
import util.RoundedImageView;

public class DetailTipsActivity extends CommonActivity {
    String recipe_id;
    HashMap<String, String> postItems;
    ArrayList<HashMap<String, String>> postArray;
    ImageView imgBanner;
    ImageView likebutton;
    ImageView favoritebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipe_id = getIntent().getExtras().getString("id");
        setHeaderLogo();
        allowBack();
        postArray = new ArrayList<>();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("tips_id", recipe_id));
        nameValuePairs.add(new NameValuePair("user_id", common.get_user_id()));

        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.DETAILED_TIPS_URL, handlerCategory, true, DetailTipsActivity.this);
        commonTask.execute();
    }
    private final Handler handlerCategory = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    postArray=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    postItems = postArray.get(0);
                    updateUI();
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };

    public void updateUI(){
        setContentView(R.layout.activity_detail_tips);

        if(postItems != null){

                TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
                TextView txtNote = (TextView)findViewById(R.id.txtNote);
                TextView txtDesc = (TextView)findViewById(R.id.txtDesc);
                TextView txtNeed = (TextView)findViewById(R.id.txtNeed);

            likebutton = (ImageView) findViewById(R.id.likeButton);
            favoritebutton = (ImageView) findViewById(R.id.iconFavourite);
            TextView totalLikes = (TextView) findViewById(R.id.totalLikes);

                txtTitle.setText(postItems.get("tips_title"));
                RatingBar rb=(RatingBar)findViewById(R.id.ratingBar1);

                imgBanner =(ImageView)findViewById(R.id.imageView);
                if (postItems.get("tips_photo").equalsIgnoreCase("")){
                    imgBanner.setVisibility(View.GONE);
                }else{
                    imgBanner.setVisibility(View.VISIBLE);
                    Picasso.with(DetailTipsActivity.this).load(ConstValue.BASE_URL + "/uploads/tips/" +postItems.get("tips_photo"))
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imgBanner);


                }
                if (postItems.get("tips_note").equalsIgnoreCase("")){
                    txtNote.setVisibility(View.GONE);
                }else{
                    txtNote.setText(Html.fromHtml("Note :"+postItems.get("tips_note")));
                }
                txtDesc.setText(Html.fromHtml(postItems.get("tips_desc")));
                txtNeed.setText(Html.fromHtml(postItems.get("tips_need")));
            rb.setRating(0);
            if(!postItems.get("avg_ratings").equalsIgnoreCase("0"))
                rb.setRating(Float.parseFloat(postItems.get("avg_ratings")));

            if (postItems.get("is_found").equalsIgnoreCase("1")){
                likebutton.setImageResource(R.drawable.ic_heart_white_fill);
                favoritebutton.setImageResource(R.drawable.ic_heart_white_fill);
            }
            totalLikes.setText(postItems.get("total_likes"));


        }
    }


    /**
     * Bottom button events
     */
    public void shareImage(View v){
        if (postItems!=null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String shareText = "";

                shareText = shareText + "Tips : "+postItems.get("tips_title")+"\n"+
                        "Needs : \n"+postItems.get("tips_need")+"\n\n"+
                        "Process : \n"+postItems.get("tips_desc")+"\n\n";
                intent.putExtra(Intent.EXTRA_TEXT, shareText);

            Bitmap bitmap = ((BitmapDrawable) imgBanner.getDrawable()).getBitmap();
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
            Uri screenshotUri = Uri.parse(path);

            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share Tips..."));
        }
    }
    public void listComment(View v){
        if(postItems!=null) {
            Intent intent = new Intent(this, TipsCommentsActivity.class);
            intent.putExtra("tips_id", recipe_id);
            startActivity(intent);
        }

    }
    public void likeButtonClick(View v){
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("tips_id", recipe_id));
        nameValuePairs.add(new NameValuePair("user_id", common.get_user_id()));

        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.LIKE_TIPS_URL, handlerLike, true, DetailTipsActivity.this);
        commonTask.execute();
    }
    private final Handler handlerLike = new Handler() {
        public void handleMessage(Message message) {

            if (message.getData().containsKey("object")){
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(message.getData().getString("object"));
                    if (jObj.has("responce") && !jObj.getBoolean("responce")) {
                    }else {

                        if(jObj.has("is_found")){

                          String  counts = jObj.getString("count");
                           boolean is_like = jObj.getBoolean("is_found");
                            ((TextView) findViewById(R.id.totalLikes)).setText(counts);
                            if (is_like) {
                                likebutton.setImageResource(R.drawable.ic_heart_white_fill);
                                favoritebutton.setImageResource(R.drawable.ic_heart_white_fill);
                            }
                            else {
                                likebutton.setImageResource(R.drawable.ic_heart_white);
                                favoritebutton.setImageResource(R.drawable.ic_heart_white);
                            }
                        }else{

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    /**
     * End
     */
}
