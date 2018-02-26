package util;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Config.ApiParams;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Lenovo on 10-05-2017.
 */

public class VJsonRequest {
    VJsonResponce vresponce;
    Activity instance;
    String url;
    Map<String, String> params;
    JSONObject parmsJson;
    //int method;

    public VJsonRequest(Activity activity, String url, VJsonResponce  vresponce){
            this.instance = activity;
            this.vresponce = vresponce;
            this.url = url;
            //method = Request.Method.GET;
            this.params = new HashMap<>();
            new jsonRequestTask().execute();
    }

    public VJsonRequest(Activity activity, String url, Map<String, String> parms, VJsonResponce  vresponce){
        this.instance = activity;
        this.vresponce = vresponce;
        this.url = url;
        //this.method = Request.Method.POST;
        this.params = parms;
        parmsJson =     new JSONObject(params);

        new jsonRequestTask().execute();
    }
    public interface VJsonResponce
    {
        public void VResponce(String responce);
        public void VError(String responce);
    }
    private class jsonRequestTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void[] params) {
            // do above Server call here
            return  requestStringData();

        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            //process message
            if(jObj != null) {
                try {
                    if (jObj.has(ApiParams.PARM_RESPONCE)) {
                        if (jObj.getBoolean(ApiParams.PARM_RESPONCE)) {
                            vresponce.VResponce(jObj.getString(ApiParams.PARM_DATA));
                        } else {
                            vresponce.VError(jObj.getString(ApiParams.PARM_ERROR));
                        }
                    }else{
                        vresponce.VResponce(jObj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private JSONObject requestStringData(){
        /*StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String responseString)
                    {
                        try {
                            JSONObject response = new JSONObject(responseString);
                            if (response.getBoolean(ApiParams.PARM_RESPONCE)){
                                vresponce.VResponce(response.getString(ApiParams.PARM_DATA));
                            }else{
                                vresponce.VError(response.getString(ApiParams.PARM_ERROR));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        vresponce.VError(error.getMessage());
                        // hide the progress dialog
                        //  Log.e("inside","Error");
                        checkInternetConncetion(error,instance);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(instance);
        requestQueue.add(strRequest);
        */

        //if (common.is_internet_connected()){

            OkHttpClient client = new OkHttpClient();
            //increased timeout for slow response
            OkHttpClient eagerClient = client.newBuilder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            //Log.i("PARAMETERS", "PARAMETERS ::" + valuePairs);


            FormBody.Builder builder = new FormBody.Builder();

        try {
            for(Map.Entry<String, String> valuePair : this.params.entrySet()) {
                String val="";
                if(!valuePair.getValue().contains("@") && !valuePair.getValue().contains("+"))

                        val = URLEncoder.encode(valuePair.getValue(), "UTF-8").replace("+","%20");

                else
                    val = valuePair.getValue();

                builder.add(valuePair.getKey(), val);

            }

            Request request = new Request.Builder().url(this.url).post(builder.build()).build();
            Log.i("Registration Request::", request.toString());

            Response response = eagerClient.newCall(request).execute();
            Log.i("REGISTRATION RESPONSE::", response.toString());
            String res = response.body().string();

            JSONObject jObj = null;

                return new JSONObject(res);
            } catch (JSONException e) {
                e.printStackTrace();
               // common.setSession(ApiParams.PREF_ERROR, e.getMessage().toString());
                //return null;
            } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //res = res.replace("%20"," ");
        //}else
        //{
            //Snackbar.make(null, "Replace with your own action", Snackbar.LENGTH_LONG)
            //		.setAction("Action", null).show();
        //}
        return null;
    }
    /*
    private void requestData() {
        JsonObjectRequest request = new JsonObjectRequest(
                url,parmsJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getBoolean(ApiParams.PARM_RESPONCE)){
                                vresponce.VResponce(response.getString(ApiParams.PARM_DATA));
                            }else{
                                vresponce.VError(response.getString(ApiParams.PARM_ERROR));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                vresponce.VError(error.getMessage());
                // hide the progress dialog
                //  Log.e("inside","Error");
                checkInternetConncetion(error,instance);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(instance);
        requestQueue.add(request);
    }
    public void  checkInternetConncetion(VolleyError volleyError, Activity activity){
        if (volleyError instanceof NetworkError || volleyError instanceof AuthFailureError || volleyError instanceof ParseError || volleyError instanceof NoConnectionError){
            //NoInternetDialog noInternetDialog = new NoInternetDialog(activity);
            //noInternetDialog.show();
            Toast.makeText(activity,activity.getString(R.string.error_no_internet),Toast.LENGTH_SHORT).show();
        }else if(volleyError instanceof ServerError){
            Toast.makeText(activity,activity.getString(R.string.error_can_not_connect_to_server),Toast.LENGTH_SHORT).show();
        }else if (volleyError instanceof TimeoutError){
            Toast.makeText(activity,activity.getString(R.string.error_can_not_connect_to_server),Toast.LENGTH_SHORT).show();
        }

    }
    */
}
