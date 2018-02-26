package util;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import Config.ApiParams;
import Config.ConstValue;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONParser {
	Activity activity;
	static CommonClass common;
	// constructor
	public JSONParser(Activity act) {
		activity = act;
		common = new CommonClass(activity);
	}

	public  final String apiLoc = ConstValue.BASE_URL;
	// public static final String apiLocLogin ="http://fmv.cc/micron/index.php/marketplace/api/index/";
	//http://fmv.cc/micron/index.php/marketplace/api/index/username/test@gmail.com/password/test@123

	public  ArrayList<HashMap<String,String>> execPostScript(String url, ArrayList<NameValuePair> valuePairs)
			throws IOException {
		ArrayList<HashMap<String,String>> postItems = new ArrayList<>();
		if (common.is_internet_connected()){

			OkHttpClient client = new OkHttpClient();
			//increased timeout for slow response
			OkHttpClient eagerClient = client.newBuilder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			Log.i("PARAMETERS", "PARAMETERS ::" + valuePairs);


			FormBody.Builder builder = new FormBody.Builder();


			for (NameValuePair valuePair : valuePairs) {
				String val="";
				if(!valuePair.value.contains("@") && !valuePair.value.contains("+"))
					val = URLEncoder.encode(valuePair.value, "UTF-8").replace("+","%20");
				else
					val = valuePair.value;

				builder.add(valuePair.name, val);

			}

			Request request = new Request.Builder().url(apiLoc+url).post(builder.build()).build();
			Log.i("Registration Request::", request.toString());

			Response response = eagerClient.newCall(request).execute();
			Log.i("REGISTRATION RESPONSE::", response.toString());
			String res = response.body().string();

			JSONObject jObj = null;
			try {
				jObj = new JSONObject(res);
				if (jObj.has(ApiParams.PARM_RESPONCE) && !jObj.getBoolean(ApiParams.PARM_RESPONCE)) {
					common.setSession(ApiParams.PREF_ERROR, jObj.getString(ApiParams.PARM_ERROR));
					return null;
				} else {
					if (jObj.has(ApiParams.PARM_DATA)) {
						JSONArray jsonArray = jObj.getJSONArray(ApiParams.PARM_DATA);
						for(int i = 0 ; i < jsonArray.length(); i++){
							postItems.add(common.getMapJsonObject(jsonArray.getJSONObject(i)));
						}
						return  postItems;
					} else {
						common.setSession(ApiParams.PREF_ERROR, "No data found");
						return null;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				common.setSession(ApiParams.PREF_ERROR, e.getMessage().toString());
				return null;
			}


			//res = res.replace("%20"," ");
		}else
		{
			//Snackbar.make(null, "Replace with your own action", Snackbar.LENGTH_LONG)
			//		.setAction("Action", null).show();
		}
		return null;
	}
	public  String execPostScriptJSON(String url, ArrayList<NameValuePair> valuePairs)
			throws IOException {
		String responce = null;
		if (common.is_internet_connected()){

			OkHttpClient client = new OkHttpClient();
			//increased timeout for slow response
			OkHttpClient eagerClient = client.newBuilder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			Log.i("PARAMETERS", "PARAMETERS ::" + valuePairs);


			FormBody.Builder builder = new FormBody.Builder();


			for (NameValuePair valuePair : valuePairs) {
				String val="";
				//if(!valuePair.value.contains("@") && !valuePair.value.contains("+"))
				//	val = URLEncoder.encode(valuePair.value, "UTF-8");
				//else
				val = valuePair.value;

				builder.add(valuePair.name, val);

			}
//.header("Authorization","passme")
			Request request = new Request.Builder()

					.url(apiLoc+url)
					.addHeader("Authorization","passme")
					.post(builder.build()).build();
			Log.i("Registration Request::", request.toString());

			Response response = eagerClient.newCall(request).execute();
			Log.i("REGISTRATION RESPONSE::", response.toString());
			responce = response.body().string();




			//res = res.replace("%20"," ");
		}else
		{
			responce ="{responce : false, error : 'No internet connection'}";
			//Toast.makeText(activity,activity.getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
		}
		return responce;
	}
	public  String execMultiPartPostScriptJSON(String url, ArrayList<NameValuePair> valuePairs,File fileu,String MIMETYPE, String imagename)
			throws IOException {
		String responce = null;
		if (common.is_internet_connected()){

			OkHttpClient client = new OkHttpClient();
			//increased timeout for slow response
			OkHttpClient eagerClient = client.newBuilder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			Log.i("PARAMETERS", "PARAMETERS ::" + valuePairs);


			MultipartBody.Builder builder = new MultipartBody.Builder()
					.setType(MultipartBody.FORM);
			if (fileu!=null) {
					RequestBody requestFile =
							RequestBody.create(
									MediaType.parse(MIMETYPE),
									fileu
							);
					builder.addFormDataPart(imagename, fileu.getName(),
							requestFile)
					;


			}
			for (NameValuePair valuePair : valuePairs) {
				String val="";
				//if(!valuePair.value.contains("@") && !valuePair.value.contains("+"))
				//	val = URLEncoder.encode(valuePair.value, "UTF-8");
				//else
				val = valuePair.value;

				builder.addFormDataPart(valuePair.name, val);

			}

			RequestBody requestBody = builder.build();

			Request request = new Request.Builder().url(apiLoc+url).post(requestBody).build();
			Log.i("Registration Request::", request.toString());

			/*Request request = new Request.Builder()
					.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
					.url("https://api.imgur.com/3/image")
					.post(requestBody)
					.build();
*/
			Response response = eagerClient.newCall(request).execute();
			Log.i("REGISTRATION RESPONSE::", response.toString());
			responce = response.body().string();




			//res = res.replace("%20"," ");
		}else
		{
			//Snackbar.make(null, "Replace with your own action", Snackbar.LENGTH_LONG)
			//		.setAction("Action", null).show();
		}
		return responce;
	}

	public  String exeGetRequest(String url,String params) throws IOException{
		String result="";

		OkHttpClient client = new OkHttpClient();
		//increased timeout for slow response
		OkHttpClient eagerClient = client.newBuilder()
				.readTimeout(30, TimeUnit.SECONDS)
				.build();

		String my_url = apiLoc+url+"?"+params;
		Request request = new Request.Builder()
				.url(my_url)
				.build();

		Call call = client.newCall(request);
		Response response = eagerClient.newCall(request).execute();;
		result = response.body().string();
		return result;
	}
}
