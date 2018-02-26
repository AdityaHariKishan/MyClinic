package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

import com.clinicapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import util.CommonClass;
import util.NameValuePair;

/**
 * Created by subhashsanghani on 1/17/17.
 */

public class ReviewsFragment  extends Fragment {
    CommonClass common;
    ArrayList<HashMap<String,String>> arrayList;
    ListView listReviewsView;
    RatingBar ratingbar;
    EditText txtComment;
    Button btnAddReview;
    RelativeLayout topLayout;
    AlertDialog alertDialog;
    ReviewsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        common = new CommonClass(getActivity());
        arrayList = common.getParseObject("dr_reviews");

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.REVIEWS_URL, handler, true, getActivity());
        commonTask.execute();

        listReviewsView = (ListView)rootView.findViewById(R.id.listView);
        adapter = new ReviewsAdapter();
        listReviewsView.setAdapter(adapter);

        topLayout = (RelativeLayout)rootView.findViewById(R.id.topLayout);

        btnAddReview = (Button)rootView.findViewById(R.id.addreview);
        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReviewDialog();
            }
        });

        return rootView;
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    arrayList=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    common.parseObject("dr_reviews",arrayList);
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
            inflater = LayoutInflater.from(getActivity());
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

            HashMap<String, String> obj = arrayList.get(position);
            textReview.setText(obj.get("reviews"));
            textUser.setText(obj.get("user_fullname"));

            if(!obj.get("user_image").equalsIgnoreCase(""))
            Picasso.with(getActivity()).load(ConstValue.BASE_URL + "/uploads/profile/" + obj.get("user_image")).into(imageIcon);

            RatingBar ratingBar1 = (RatingBar)convertView.findViewById(R.id.ratingBar1);
            ratingBar1.setRating(Float.valueOf(obj.get("ratings")));

            TextView datetime = (TextView) convertView.findViewById(R.id.datetime);
            datetime.setText(common.printDifference2(obj.get("on_date")));
            return convertView;
        }
    }


    public void openReviewDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
        nameValuePairs.add(new NameValuePair("user_id", common.get_user_id()));
        nameValuePairs.add(new NameValuePair("reviews", commentText));
        nameValuePairs.add(new NameValuePair("rating", ratings));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.REVIEWS_ADD_URL, handler2, true, getActivity());
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
