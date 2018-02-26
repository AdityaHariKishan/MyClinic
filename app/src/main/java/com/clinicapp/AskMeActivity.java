package com.clinicapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import Config.ConstValue;
import configfcm.MyFirebaseMessagingService;
import util.CommonActivity;
import util.FilenameUtils;
import util.JSONParser;
import util.NameValuePair;

public class AskMeActivity extends CommonActivity {
    ArrayList<HashMap<String,String>> chatArray;
    ChatAdapter adapter;
    EditText txtMessage;
    ListView listview;
    MyBroadcastReceiver myBroadcastReceiver;
    ImageButton attachButton;
    private static final int PICKFILE_RESULT_CODE = 1;
    File attachment_file_path;
    String media_type;
    NotificationManager mNotifyManager;
    android.support.v7.app.NotificationCompat.Builder  mBuilder;
    DownloadPdf downloadPDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_me);
        setHeaderLogo();
        allowBack();
        chatArray = new ArrayList<>();
        txtMessage = (EditText) findViewById(R.id.editChat);


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new NameValuePair("user_id",common.get_user_id()));
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.GET_CHAT_URL, handler, true, AskMeActivity.this);
        commonTask.execute();

        listview = (ListView)findViewById(R.id.listview);
        adapter = new ChatAdapter();
        listview.setAdapter(adapter);

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.broadCastReceiver);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        attachButton = (ImageButton) findViewById(R.id.btnAttachment);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("**");
                startActivityForResult(intent,PICKFILE_RESULT_CODE);*/
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.putExtra("browseCoa", itemToBrowse);
                //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
                try {
                    //startActivityForResult(chooser, FILE_SELECT_CODE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),PICKFILE_RESULT_CODE);
                } catch (Exception ex) {
                    System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
                }
            }
        });
    }
    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("jsonobject");
            try {
                JSONObject jsonObject = new JSONObject(result);
                 HashMap<String,String> map = common.getMapJsonObject(jsonObject);
                for (int i = 0 ; i < chatArray.size(); i++){
                    HashMap<String,String> chat = chatArray.get(i);
                    if (chat.get("message_id").equalsIgnoreCase(map.get("message_id"))){
                        chatArray.set(i,map);
                        adapter.notifyDataSetChanged();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    chatArray=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(chatArray.size() - 1);
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };

    public void sendMessage(View view){
        String message = txtMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(message)){
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new NameValuePair("user_id",common.get_user_id()));
            nameValuePairs.add(new NameValuePair("message",message));
            if (attachment_file_path == null ) {
                CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.SEND_CHAT_FCM_URL, handlerSend, true, AskMeActivity.this);
                commonTask.execute();
            }else {
                new SendAttachmentTask(nameValuePairs).execute();
            }
            txtMessage.setText("");
        }
    }
    private final Handler handlerSend = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)){
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)){
                    ArrayList<HashMap<String,String>> Array=  (ArrayList<HashMap<String,String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    chatArray.add(Array.get(0));
                    adapter.notifyDataSetChanged();
                    listview.setSelection(chatArray.size() - 1);
                }else{
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    class SendAttachmentTask extends AsyncTask<Void,Void,Void>{
        ArrayList<NameValuePair> nameValuePairs;
        Boolean is_success;
        String error_string;
        ArrayList<HashMap<String,String>> Array;
        public SendAttachmentTask(ArrayList<NameValuePair> namePairs){
            nameValuePairs = namePairs;
        }

        @Override
        protected void onPreExecute() {
            Array = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (is_success && Array.size() > 0) {
                chatArray.add(Array.get(0));
                adapter.notifyDataSetChanged();
                listview.setSelection(chatArray.size() - 1);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser(AskMeActivity.this);

            try {
               String json_responce = jsonParser.execMultiPartPostScriptJSON(ApiParams.SEND_CHAT_FCM_URL, nameValuePairs, attachment_file_path,media_type, "upload");
                JSONObject jObj = new JSONObject(json_responce);
                if (jObj.has(ApiParams.PARM_RESPONCE) && !jObj.getBoolean(ApiParams.PARM_RESPONCE)) {
                    is_success = false;
                    error_string = jObj.getString(ApiParams.PARM_ERROR);

                }else {
                    if(jObj.has(ApiParams.PARM_DATA)){
                        if (jObj.get(ApiParams.PARM_DATA) instanceof  String){
                            is_success = true;
                            error_string = jObj.getString(ApiParams.PARM_DATA);
                        }else if(jObj.get(ApiParams.PARM_DATA) instanceof JSONObject){
                            is_success = true;
                            JSONObject d = jObj.getJSONObject(ApiParams.PARM_DATA);
                            Array.add(common.getMapJsonObject(d));

                        }else if(jObj.get(ApiParams.PARM_DATA) instanceof JSONArray){
                            is_success = true;
                            JSONArray services = jObj.getJSONArray(ApiParams.PARM_DATA);
                            for (int i = 0; i < services.length(); i++) {
                                JSONObject d = services.getJSONObject(i);
                                Array.add(common.getMapJsonObject(d));
                            }

                        }
                    }else{

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (chatArray == null)
                return 0;
            else
                return chatArray.size();
        }

        @Override
        public HashMap<String,String> getItem(int i) {
            return chatArray.get(i);

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = mInflater.inflate(R.layout.row_chats, null);
            }
            final HashMap<String, String> chat = chatArray.get(i);

            LinearLayout layoutMessage = (LinearLayout)view.findViewById(R.id.layoutMessage);
            LinearLayout layoutReply = (LinearLayout)view.findViewById(R.id.layoutReply);
            LinearLayout sentattachment = (LinearLayout) view.findViewById(R.id.sentattachment);
            LinearLayout replyattachment = (LinearLayout) view.findViewById(R.id.replyattachment);
            sentattachment.setVisibility(View.GONE);
            replyattachment.setVisibility(View.GONE);

            TextView txtMessage = (TextView)view.findViewById(R.id.chatmessage);
            TextView txtReaply = (TextView)view.findViewById(R.id.chatreaply);
            TextView txtDate = (TextView)view.findViewById(R.id.chatdate);
            TextView txtReaplyDate = (TextView)view.findViewById(R.id.chatreaplydate);
            TextView txtFilename = (TextView)view.findViewById(R.id.txtFilename);
            ImageView imgType = (ImageView)view.findViewById(R.id.imgType);

            TextView replytxtFilename = (TextView)view.findViewById(R.id.replytxtFilename);
            ImageView replyimgType = (ImageView)view.findViewById(R.id.replyimgType);

            txtMessage.setText(Html.fromHtml(chat.get("message")));
            txtReaply.setText(Jsoup.parse(chat.get("reply")).text());
            txtDate.setText(common.printDifference2(chat.get("created_at")));
            txtReaplyDate.setText(common.printDifference2(chat.get("created_at")));

            if (!chat.get("attachment").equalsIgnoreCase("")){
                sentattachment.setVisibility(View.VISIBLE);
                txtFilename.setText(chat.get("attachment"));
                txtFilename.setVisibility(View.VISIBLE);
                if (chat.get("attachment_type").equalsIgnoreCase("png") || chat.get("attachment_type").equalsIgnoreCase("jpg")){
                    Picasso.with(getApplicationContext()).load(ConstValue.BASE_URL+"/uploads/"+ chat.get("attachment")).into(imgType);
                    txtFilename.setVisibility(View.GONE);
                }else if (chat.get("attachment_type").equalsIgnoreCase("pdf")){
                    imgType.setImageResource(R.drawable.ic_type_pdf);
                }else if (chat.get("attachment_type").equalsIgnoreCase("doc") || chat.get("attachment_type").equalsIgnoreCase("docx")){
                    imgType.setImageResource(R.drawable.ic_type_doc);
                }else if (chat.get("attachment_type").equalsIgnoreCase("xls") || chat.get("attachment_type").equalsIgnoreCase("xlsx")){
                    imgType.setImageResource(R.drawable.ic_type_excel);
                }
                sentattachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (downloadPDF == null) {
                            downloadPDF = new DownloadPdf(chat.get("attachment"),Long.parseLong(chat.get("attachment_size")) , i);
                            downloadPDF.execute();
                        }

                    }
                });
            }


            if (chat.get("reply").equalsIgnoreCase("")){
                layoutReply.setVisibility(View.GONE);
            }else{
                layoutReply.setVisibility(View.VISIBLE);
            }

            if (!chat.get("reply_attachment").equalsIgnoreCase("")){
                replyattachment.setVisibility(View.VISIBLE);
                replytxtFilename.setText(chat.get("reply_attachment"));
                replytxtFilename.setVisibility(View.VISIBLE);
                if (chat.get("reply_attachment_type").equalsIgnoreCase("png") || chat.get("reply_attachment_type").equalsIgnoreCase("jpg")){
                    Picasso.with(getApplicationContext()).load(ConstValue.BASE_URL+"/uploads/"+ chat.get("reply_attachment")).into(replyimgType);
                    replytxtFilename.setVisibility(View.GONE);
                }else if (chat.get("reply_attachment_type").equalsIgnoreCase("pdf")){
                    replyimgType.setImageResource(R.drawable.ic_type_pdf);
                }else if (chat.get("reply_attachment_type").equalsIgnoreCase("doc") || chat.get("reply_attachment_type").equalsIgnoreCase("docx")){
                    replyimgType.setImageResource(R.drawable.ic_type_doc);
                }else if (chat.get("reply_attachment_type").equalsIgnoreCase("xls") || chat.get("reply_attachment_type").equalsIgnoreCase("xlsx")){
                    replyimgType.setImageResource(R.drawable.ic_type_excel);
                }
                replyattachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (downloadPDF == null) {
                            downloadPDF = new DownloadPdf(chat.get("reply_attachment"),Long.parseLong(chat.get("reply_attachment_size")) , i);
                            downloadPDF.execute();
                        }

                    }
                });
            }
            return view;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String filename;
                        String mimeType = getContentResolver().getType(uri);
                    media_type = mimeType;
                        if (mimeType == null) {
                            String path = FilenameUtils.getPath(this, uri);
                            //if (path == null) {
                            //    filename =  FilenameUtils.getName(uri.toString());
                            //} else {
                                File file = new File(path);
                                filename = file.getName();
                            //}
                        } else {
                            Uri returnUri = data.getData();
                            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndex);
                            String size = Long.toString(returnCursor.getLong(sizeIndex));
                        }
                        File fileSave = getExternalFilesDir(null);
                        String sourcePath = getExternalFilesDir(null).toString();
                        try {
                            copyFileStream(new File(sourcePath + "/" + filename), uri,this);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
            attachment_file_path = dest;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }



    private class DownloadPdf extends AsyncTask<Void,Integer,String>{
        //ProgressDialog dialog;
        String download_url;
        File downloaded_file;
        long contentlength;
        int id=1;
        String name;

        public DownloadPdf(String filename, long contentlength,int position) {
            this.download_url = ConstValue.BASE_URL+"/uploads/"+ filename;
            this.contentlength = contentlength;
            this.name=filename;
            this.id=position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mNotifyManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new android.support.v7.app.NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle("Download")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true);

            Toast.makeText(getApplicationContext(),"Downloading Started",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(Void... voids) {
            int count;
            try {
                URL my_url = new URL(download_url);
                URLConnection conection = my_url.openConnection();
                conection.connect();
                // getting file length
                long lenghtOfFile = contentlength;

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(my_url.openStream(), 8192);

                // Output stream to write file
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/clinicapp/download");
                if (dir.exists() == false) {
                    dir.mkdirs();
                }
                String filepath = root.getAbsolutePath() + "/clinicapp/download/"+name;
                File f = new File(filepath);
                if (f.exists()){
                    downloaded_file = f;
                }else {
                    downloaded_file = new File(dir, name);
                    OutputStream output = new FileOutputStream(downloaded_file.getAbsoluteFile());

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
          /*  dialog.setMaxProgress(100);
            dialog.setProgress(values[0]);*/

            mBuilder.setProgress(100, values[0], false);
            // Displays the progress bar for the first time.
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (downloaded_file != null) {
                    mBuilder.setContentText("Download complete")
                            // Removes the progress bar
                            .setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());

                    //show notification
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);

                    String name = downloaded_file.getName();
                    String path = "" + downloaded_file.getAbsolutePath();


                    String ext = MimeTypeMap.getFileExtensionFromUrl(path);
                    ext = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

                    File open = new File(path);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".my.package.name.provider", open);

                    intent.setDataAndType(photoURI, ext);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Download complete")
                                    .setAutoCancel(true)
                                    .setContentText(name);

                    mBuilder.setContentIntent(pendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(id, mBuilder.build());

                    startActivity(intent);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
            //dialog.dismiss();
            downloadPDF = null;
        }
    }

    @Override
    protected void onDestroy() {
        downloadPDF = null;
        super.onDestroy();
    }
}
