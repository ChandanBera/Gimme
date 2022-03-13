package com.xentric.gimme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xentric.gimme.dbhelper.DBHelper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by goutam on 7/4/16.
 */
public class ESign extends AppCompatActivity
implements View.OnClickListener{
    private Button btn_done,btn_cancel;
    private LinearLayout lin_menu,lin_sync;
    private TextView tv_title;
    private String customerId,customername,duedate,invoiceid;
    private Bitmap bm;
    private DBHelper db;
    String custId;
    /*private LogCollection logCollection;*/
    private static final String UPLOAD_URL = "http://ezeebookzdemo.xentricserver.com/customer/uploadcustomerphoto";
    private static final String KEY_IMAGE = "FileName";
    private static final String KEY_CUSTOMER_ID = "customerid";
    private static final String KEY_DESC = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esign);
        custId = getIntent().getStringExtra("customer_id");
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_sync = (LinearLayout) findViewById(R.id.lin_sync);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lin_menu.setVisibility(View.GONE);
        lin_sync.setVisibility(View.GONE);
        tv_title.setText("Please ESignature Here");

        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        db = new DBHelper(ESign.this);
        /*logCollection =new LogCollection(this);*/

        /*Intent intent = getIntent();
        customerId = intent.getStringExtra("customer_id");
        customername = intent.getStringExtra("customer_name");
        duedate = intent.getStringExtra("duedate");
        invoiceid = intent.getStringExtra("invoice");*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_done:
                System.out.println("%%%%%%%%%%%%%%% Save clicked %%%%%%%%%%%%%");
                saveSig();
                break;
            case R.id.btn_cancel:
                  onBackPressed();
                break;
        }
    }

    public void saveSig() {
        try {
            GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
            gestureView.setDrawingCacheEnabled(true);
            bm = Bitmap.createBitmap(gestureView.getDrawingCache());
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "signature.png");
            f.createNewFile();
            FileOutputStream os = new FileOutputStream(f);
            os = new FileOutputStream(f);
            //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
            bm.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();

            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/PhysicsSketchpad";
            File dir = new File(file_path);
            //if(!dir.exists)
                dir.mkdirs();
            File file = new File(dir, "sketchpad" + ".png");
            FileOutputStream fOut = new FileOutputStream(file);

            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            new FileUploadAsync().execute(UPLOAD_URL, custId,"description");
        } catch (Exception e) {
           //Log.v("Gestures", e.getMessage());
            e.printStackTrace();
        }

        /*long row = db.addOrder(new OrderModel(
                invoiceid,
                duedate,
                customername,
                customerId));

        if (row > -1){
            db.dropAndRecreateAddItemTable();
            Intent intent = new Intent(ESign.this,AllOrderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        }else
            logCollection.showToastLong("Order not inserted");*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }



    public class FileUploadAsync extends AsyncTask<String,Void,String> {
        URL connectURL;
        String responseString;
        String customerCode;
        String Description;
        byte[ ] dataToServer;
        FileInputStream fileInputStream = null;
        String str = null;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ESign.this, "", "Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                connectURL = new URL(params[0]);
                customerCode= params[1];
                Description = params[2];

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/PhysicsSketchpad/"+"sketchpad.png");
                fileInputStream = fstrm;

                String iFileName = "sketchpad.png";
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String Tag="fSnd";
                try
                {
                    Log.e(Tag,"Starting Http File Sending to URL");

                    // Open a HTTP connection to the URL
                    HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

                    // Allow Inputs
                    conn.setDoInput(true);

                    // Allow Outputs
                    conn.setDoOutput(true);

                    // Don't use a cached copy.
                    conn.setUseCaches(false);

                    // Use a post method.
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"customerid\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(customerCode);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"description\""+ lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Description);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"FileName\";filename=\"" + iFileName +"\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.e(Tag,"Headers are written");

                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[ ] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0)
                    {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // close streams
                    fileInputStream.close();

                    dos.flush();

                    Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

                    InputStream is = conn.getInputStream();

                    // retrieve the response from server
                    int ch;

                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                    str=b.toString();
                    Log.i("Response!!!!!",str);
                    dos.close();
                }
                catch (MalformedURLException ex)
                {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                }

                catch (IOException ioe)
                {
                    Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
                }


            }catch(Exception ex){
                Log.i("HttpFileUpload","URL Malformatted");
            }
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Intent intent = new Intent(ESign.this,UploadPic.class);
            intent.putExtra("customer_id",custId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            /*if (s != null && !TextUtils.isEmpty(str)){
                if (s.equalsIgnoreCase("Success")){
                    Intent intent = new Intent(ESign.this,UploadPic.class);
                    intent.putExtra("customer_id",custId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
            }*/
        }
    }

}
