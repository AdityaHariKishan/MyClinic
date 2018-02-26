package AsyTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.clinicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Config.ApiParams;
import util.CommonClass;
import util.JSONParser;
import util.NameValuePair;

/**
 * Created by subhashsanghani on 1/15/17.
 */

public class CommonAsyTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {
    ArrayList<NameValuePair> _nameValuePairs ;
    String _baseUrl;
    Activity _activity;
    String error_string;
    private Handler handler;
    ProgressDialog progressDialog;
    boolean is_progress_show;
    CommonClass common;
    boolean is_success;
    JSONObject responceObj;
    public CommonAsyTask(ArrayList<NameValuePair> nameValuePairs, String baseUrl, Handler handler, boolean is_progress_show , Activity activity){
        responceObj = new JSONObject();
        _nameValuePairs = nameValuePairs;
        _baseUrl = baseUrl;
        _activity = activity;
        this.handler = handler;
        this.is_progress_show = is_progress_show;
        common = new CommonClass(_activity);
    }

    @Override
    protected void onPreExecute() {
        if (is_progress_show){
            progressDialog = ProgressDialog.show(_activity, "", _activity.getResources().getString(R.string.process_with_Data), true);
        }
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onCancelled();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
        JSONParser jsonParser = new JSONParser(_activity);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        try {
            String json_responce = jsonParser.execPostScriptJSON(_baseUrl, _nameValuePairs);

            JSONObject jObj = new JSONObject(json_responce);
            responceObj = jObj;
            if (jObj.has(ApiParams.PARM_RESPONCE) && !jObj.getBoolean(ApiParams.PARM_RESPONCE)) {
                is_success = false;
                error_string = jObj.getString(ApiParams.PARM_ERROR);
                return null;
            }else {
                if(jObj.has(ApiParams.PARM_DATA)){
                    if (jObj.get(ApiParams.PARM_DATA) instanceof  String){
                        is_success = true;
                        error_string = jObj.getString(ApiParams.PARM_DATA);
                    }else if(jObj.get(ApiParams.PARM_DATA) instanceof JSONObject){
                        is_success = true;
                        arrayList.clear();
                        JSONObject d = jObj.getJSONObject(ApiParams.PARM_DATA);
                        arrayList.add(common.getMapJsonObject(d));
                        return arrayList;
                    }else if(jObj.get(ApiParams.PARM_DATA) instanceof JSONArray){
                        is_success = true;
                        JSONArray services = jObj.getJSONArray(ApiParams.PARM_DATA);
                        arrayList.clear();
                        for (int i = 0; i < services.length(); i++) {
                            JSONObject d = services.getJSONObject(i);
                            arrayList.add(common.getMapJsonObject(d));
                        }

                        return arrayList;
                    }
                }else{

                }
            }
            return null;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            error_string = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            error_string = e.getMessage();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (hashMaps== null){
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(ApiParams.PARM_ERROR, error_string);
            bundle.putString("object",responceObj.toString());
            bundle.putBoolean(ApiParams.PARM_RESPONCE, is_success);
            message.setData(bundle);
            handler.sendMessage(message);
        }else{
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ApiParams.PARM_DATA, hashMaps);
            bundle.putBoolean(ApiParams.PARM_RESPONCE, is_success);
            bundle.putString("object",responceObj.toString());
            message.setData(bundle);
            handler.sendMessage(message);
        }
        super.onPostExecute(hashMaps);
    }
}
