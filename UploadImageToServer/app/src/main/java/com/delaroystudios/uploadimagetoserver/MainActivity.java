package com.delaroystudios.uploadimagetoserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class MainActivity extends ActionBarActivity {

    public static String URL = "http://www.delaroystudios.com/delaroyandroid/uploads/uploadimage.php";
    private static final String IMAGE_CAPTURE_FOLDER = "delaroyandroid/uploads";
    private static final int CAMERA_PIC_REQUEST = 1111;
    private Button btnCamera;
    private static File file;
    private static Uri _imagefileUri;
    private TextView resultText;
    private static String _bytes64Sting, _imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _imageFileName = String.valueOf(System.currentTimeMillis());
        btnCamera = (Button) findViewById(R.id.button);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
    }

    

    private void captureImage() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        _imagefileUri = Uri.fromFile(getFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imagefileUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST) {
                uploadImage(_imagefileUri.getPath());
            }
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT).show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void uploadImage(String picturePath) {
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] byteArray = bao.toByteArray();
        _bytes64Sting = Base64.encodeBytes(byteArray);
        RequestPackage rp = new RequestPackage();
        rp.setMethod("POST");
        rp.setUri(URL);
        rp.setSingleParam("base64", _bytes64Sting);
        rp.setSingleParam("ImageName", _imageFileName + ".jpg");

        // Upload image to server
        new uploadToServer().execute(rp);

    }

    public class uploadToServer extends AsyncTask<RequestPackage, Void, String> {

        private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            resultText = (TextView) findViewById(R.id.textView);
            resultText.setText("New file "+_imageFileName+".jpg created\n");
            pd.setMessage("Image uploading!, please wait..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            String content = MyHttpURLConnection.getData(params[0]);
            return content;

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            resultText.append(result);
        }
    }

    private File getFile() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, IMAGE_CAPTURE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file + File.separator + _imageFileName
                + ".jpg");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }   

}
