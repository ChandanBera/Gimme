package com.xentric.gimme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xentric.gimme.dbhelper.DBHelper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by goutam on 7/4/16.
 */
public class UploadPic extends AppCompatActivity
implements View.OnClickListener{
    private Button btn_done;
    private String customerId,customername,duedate,invoiceid;
    private Bitmap bm;
    ImageView viewImage;
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
        setContentView(R.layout.activity_upload_pic);
        custId = getIntent().getStringExtra("customer_id");
        viewImage = (ImageView) findViewById(R.id.imageView);
        btn_done = (Button) findViewById(R.id.btn_done);


        btn_done.setOnClickListener(this);
        db = new DBHelper(UploadPic.this);
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
                selectImage();
                //saveSig();
                break;
        }
    }

    public void saveSig(Bitmap bitmap) {
        try {
            bm = Bitmap.createBitmap(bitmap);
            /*File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "signature.png");
            f.createNewFile();
            FileOutputStream os = new FileOutputStream(f);
            os = new FileOutputStream(f);
            //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
            bm.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();*/

            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/UserImage";
            File dir = new File(file_path);
            //if(!dir.exists)
                dir.mkdirs();
            File file = new File(dir, "userpic" + ".png");
            FileOutputStream fOut = new FileOutputStream(file);

            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            new FileUploadAsync().execute(UPLOAD_URL, custId,"description");
        } catch (Exception e) {
           //Log.v("Gestures", e.getMessage());
            e.printStackTrace();
        }

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
            progressDialog = ProgressDialog.show(UploadPic.this, "", "Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                connectURL = new URL(params[0]);
                customerCode= params[1];
                Description = params[2];

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/UserImage/"+"userpic.png");
                fileInputStream = fstrm;

                String iFileName = "userpic.png";
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

                    System.out.println("File Sent, Response: "+String.valueOf(conn.getResponseCode()));
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
            Intent intent = new Intent(UploadPic.this,CollectionEntry.class);
            //intent.putExtra("customer_id",custId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            /*if (s != null && !TextUtils.isEmpty(str)){
                if (s.equalsIgnoreCase("Success")){
                    Intent intent = new Intent(UploadPic.this,CollectionEntry.class);
                    //intent.putExtra("customer_id",custId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
            }*/
        }
    }

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPic.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                } else if (options[item].equals("Cancel")) {

                    //dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        f = temp;

                        break;

                    }

                }
                new ImageCompression(UploadPic.this).execute(f.getAbsolutePath());

            } else if (requestCode == 2) {


                Uri selectedImage = data.getData();

                new ImageCompression(UploadPic.this).execute(getRealPathFromURI(UploadPic.this,selectedImage));
            }

        }

    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            System.out.println("======================================= Image path ================================== : "+cursor.getString(column_index));
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        if(bitmap!=null) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            System.out.println("+++++++++++++++++++++++++++bitmap.getWidth()+++++++++++++++++++++++++++++++++++ :" + bitmap.getWidth());
            System.out.println("+++++++++++++++++++++++++++bitmap.getHeight()+++++++++++++++++++++++++++++++++++ :" + bitmap.getHeight());
            if (bitmap.getWidth() < bitmap.getHeight()) {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getWidth() / 2, paint);
            } else if (bitmap.getWidth() > bitmap.getHeight()) {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getHeight() / 2, paint);
            } else {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getHeight() / 2, paint);
            }
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
        else
        {
            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            bitmap = ((BitmapDrawable) myDrawable).getBitmap();

            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            System.out.println("+++++++++++++++++++++++++++bitmap.getWidth()+++++++++++++++++++++++++++++++++++ :" + bitmap.getWidth());
            System.out.println("+++++++++++++++++++++++++++bitmap.getHeight()+++++++++++++++++++++++++++++++++++ :" + bitmap.getHeight());
            if (bitmap.getWidth() < bitmap.getHeight()) {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getWidth() / 2, paint);
            } else if (bitmap.getWidth() > bitmap.getHeight()) {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getHeight() / 2, paint);
            } else {
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                        bitmap.getHeight() / 2, paint);
            }
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }


    public class ImageCompression extends AsyncTask<String, Void, String> {

        private Context context;
        private static final float maxHeight = 1280.0f;
        private static final float maxWidth = 1280.0f;


        public ImageCompression(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... strings) {
            if(strings.length == 0 || strings[0] == null)
                return null;

            return compressImage(strings[0]);
        }

        protected void onPostExecute(String imagePath){
            // imagePath is path of new compressed image.
            Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
            System.out.println("==================================== Size after compression ====================================: "+thumbnail.getByteCount());
            viewImage.setImageBitmap(getCroppedBitmap(thumbnail));
            saveSig(thumbnail);
            //new Upload_Pic_Services().Upload_Pic_Services(UploadPic.this,thumbnail);
        }


        public String compressImage(String imagePath) {
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
            //System.out.println("==================================== Size before compression ====================================: "+bmp.getByteCount());

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(imagePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            if(bmp!=null)
            {
                bmp.recycle();
            }

            ExifInterface exif;
            try {
                exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filepath = getFilename();
            try {
                out = new FileOutputStream(filepath);

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filepath;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }

            return inSampleSize;
        }

        public String getFilename() {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + context.getApplicationContext().getPackageName()
                    + "/Files/Compressed");

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                mediaStorageDir.mkdirs();
            }

            String mImageName="IMG_"+ String.valueOf(System.currentTimeMillis()) +".jpg";
            String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);;
            return uriString;

        }

    }

}
