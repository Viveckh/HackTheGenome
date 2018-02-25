package com.sequencing.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sequencing.androidoauth.core.OAuth2Parameters;
import com.sequencing.appchains.AndroidAppChainsImpl;
import com.sequencing.appchains.AppChains;
import com.sequencing.appchains.DefaultAppChainsImpl;
import com.sequencing.appchains.DefaultAppChainsImpl.Report;
import com.sequencing.appchains.DefaultAppChainsImpl.Result;
import com.sequencing.appchains.DefaultAppChainsImpl.ResultType;
import com.sequencing.appchains.DefaultAppChainsImpl.TextResultValue;
import com.sequencing.fileselector.FileEntity;
import com.sequencing.fileselector.core.ISQFileCallback;
import com.sequencing.fileselector.core.SQUIFileSelectHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TestAppChainsActivity extends AppCompatActivity implements ISQFileCallback, View.OnClickListener {

    private static final String TAG = "TestAppChainsActivity";

    private SQUIFileSelectHandler fileSelectHandler;

    private Button btnSelectFile;
    private Button btnVitaminD;
    private Button btnMelanomaRisk;
    private Button btnBulkChain;
    private TextView tvTitle;
    private TextView tvFileName;
    private TextView tvResult;

    private String selectedFileId;
    private FileEntity entity;

    private AsyncTaskChain9 asyncTaskChain9;
    private AsyncTaskChain88 asyncTaskChain88;
    private AsyncTaskBulkChains asyncTaskBulkChains;


    /*POPACK CODE*/
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;

    private String capturedPhotoType = "image/*";
    private String capturedPhotoFilename = "capturedPhoto.png";
    private String capturedPhotoPath = ""; //will get updated with stored path
    private String capturedImageUrl = "";

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_app_chains);

        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(this);

        btnVitaminD = (Button) findViewById(R.id.btnVitaminD);
        btnVitaminD.setOnClickListener(this);

        btnMelanomaRisk = (Button) findViewById(R.id.btnMelanomaRisk);
        btnMelanomaRisk.setOnClickListener(this);

        btnBulkChain = (Button) findViewById(R.id.btnGetVitaminDMelanomaRisk);
        btnBulkChain.setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        tvResult = (TextView) findViewById(R.id.tvResult);

        fileSelectHandler = new SQUIFileSelectHandler(this);

        imageView = (ImageView) this.findViewById(R.id.imageView);
        Button takePicture = (Button) this.findViewById(R.id.takePictureBtn);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dispatchTakePictureIntent();
            }
        });

        Button shareInstaBtn = (Button) this.findViewById((R.id.shareInstaBtn));
        shareInstaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInstagramIntent(capturedPhotoType, capturedPhotoPath);
            }
        });

        ActivityCompat.requestPermissions(TestAppChainsActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(TestAppChainsActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("Write permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("Read permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            System.out.println("photo taking successful");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap photo = BitmapFactory.decodeFile(capturedPhotoPath,bmOptions);
            photo = Bitmap.createScaledBitmap(photo, imageView.getWidth(),imageView.getHeight(),true);
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            uploadImage();
            //saveCapturedImage(photo);

        }
    }

    private void uploadImage() {

        Uri file = Uri.fromFile(new File(capturedPhotoPath));
        StorageReference imageRef = mStorageRef.child("images/" + capturedPhotoFilename);

        imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println("Download URL: " + downloadUrl);
                        capturedImageUrl = downloadUrl.toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void saveCapturedImage(Bitmap finalBitmap) {
        //File file = new File(Environment.getExternalStorageDirectory() + '/vivid', );
        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
        //File directory = cw.getDir("images", Context.MODE_PRIVATE);
        //String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/longana.txt";
        // Create imageDir
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), capturedPhotoFilename);
        capturedPhotoPath = file.getPath();
        System.out.println(capturedPhotoPath);
        if (file.exists()) {
            System.out.println("captured image exists already");
            file.delete ();
        }
        try {
            file.createNewFile();
            System.out.println("Create new file");
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            System.out.println(out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        System.out.println(storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        capturedPhotoPath = image.getAbsolutePath();
        capturedPhotoFilename = image.getName();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    @Override
    public void onFileSelected(FileEntity entity, Activity activity) {
        Log.i(TAG, "File " + entity.getFriendlyDesc1() + " has been selected");
        activity.finish();
        this.entity = entity;
        selectedFileId = entity.getId();

        tvFileName.setText(entity.getFriendlyDesc1() + " - " + entity.getFriendlyDesc2());

        btnVitaminD.setVisibility(View.VISIBLE);
        btnMelanomaRisk.setVisibility(View.VISIBLE);
        btnBulkChain.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvFileName.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btnSelectFile:
                fileSelectHandler.selectFile(OAuth2Parameters.getInstance().getOauth(), this, selectedFileId);
                tvResult.setVisibility(View.GONE);
            break;

            case R.id.btnVitaminD:
                tvResult.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Computing result...", Toast.LENGTH_LONG).show();

                asyncTaskChain88 = new AsyncTaskChain88();
                asyncTaskChain88.execute();
                break;

            case R.id.btnMelanomaRisk:
                tvResult.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Computing result...", Toast.LENGTH_LONG).show();

                asyncTaskChain9 = new AsyncTaskChain9();
                asyncTaskChain9.execute();
                break;
            case R.id.btnGetVitaminDMelanomaRisk:
                tvResult.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Computing result...", Toast.LENGTH_LONG).show();

                asyncTaskBulkChains = new AsyncTaskBulkChains();
                asyncTaskBulkChains.execute();
                break;
        }
    }

    private boolean hasVitD() {
        AndroidAppChainsImpl chains = new AndroidAppChainsImpl(OAuth2Parameters.getInstance().getOauth().getToken().getAccessToken(), "api.sequencing.com");
            Report resultChain88;
            try {
                resultChain88 = chains.getReport("StartApp", "Chain88", entity.getId());
            } catch (Exception e) {
                Toast.makeText(this, "Failure to get availability of vitamin D, please try again", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (resultChain88.isSucceeded() == false) {
                Toast.makeText(this, "Failure to get availability of vitamin D, please try again", Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean result = false;
            for (Result r : resultChain88.getResults()) {
                ResultType type = r.getValue().getType();
                if (type == ResultType.TEXT) {
                    TextResultValue v = (TextResultValue) r.getValue();

                    if (r.getName().equals("result"))
                        result = v.getData().equals("No") ? false : true;
                }
            }

        return result;
    }

    private String getMelanomaRisk() {
        AndroidAppChainsImpl chains = new AndroidAppChainsImpl(OAuth2Parameters.getInstance().getOauth().getToken().getAccessToken(), "api.sequencing.com");
        Report resultChain9;

        try {
            resultChain9 = chains.getReport("StartApp", "Chain9", entity.getId());
        } catch (Exception e) {
            Toast.makeText(this, "Failure to get melanoma risk, please try again", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (resultChain9.isSucceeded() == false) {
            Toast.makeText(this, "Failure to get melanoma risk, please try again", Toast.LENGTH_SHORT).show();
            return null;
        }

        for (Result r : resultChain9.getResults()) {
            ResultType type = r.getValue().getType();
            if (type == ResultType.TEXT) {
                TextResultValue v = (TextResultValue) r.getValue();

                if (r.getName().equals("RiskDescription"))
                    return v.getData();
            }
        }

        return null;
    }

    private Map<String, String> getBulkChains(){
        Map<String, String> bulkResult = new HashMap<>();
        AndroidAppChainsImpl chains = new AndroidAppChainsImpl(OAuth2Parameters.getInstance().getOauth().getToken().getAccessToken(), "api.sequencing.com");
        Map<String, String> appChainsParams = new HashMap<>();
        appChainsParams.put("Chain9", entity.getId());
        appChainsParams.put("Chain88", entity.getId());
        try {
            Map<String, Report> resultChain = chains.getReportBatch("StartAppBatch", appChainsParams);
            for (String key : resultChain.keySet()) {
                Report report = resultChain.get(key);
                List<Result> results = report.getResults();
                for (DefaultAppChainsImpl.Result result : results) {
                    ResultType type = result.getValue().getType();
                    if (type == ResultType.TEXT) {
                        DefaultAppChainsImpl.TextResultValue textResultValue = (DefaultAppChainsImpl.TextResultValue) result.getValue();
                        if (result.getName().equals("RiskDescription") && key.equals("Chain9")) {
                            String riskDescription = textResultValue.getData();
                            bulkResult.put("riskDescription", riskDescription);
                        }
                        if (result.getName().equals("result") && key.equals("Chain88")) {
                            String hasVitD = textResultValue.getData().equals("No") ? "False" : "True";
                            bulkResult.put("vitaminD", hasVitD);
                        }
                    }
                }
            }
        }catch (Exception e){
//            showError();
        }
        return bulkResult;
    }

    class AsyncTaskChain9 extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return getMelanomaRisk();
        }

        @Override
        protected void onPostExecute(String result) {
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("Melanoma issue level is: " + result);
        }
    }

    class AsyncTaskChain88 extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return hasVitD();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            tvResult.setVisibility(View.VISIBLE);
            if(result)
                tvResult.setText("There is issue with vitamin D");
            else
                tvResult.setText("There is no issue with vitamin D");
        }
    }

    class AsyncTaskBulkChains extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            return getBulkChains();
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            tvResult.setVisibility(View.VISIBLE);
            boolean vitD = Boolean.parseBoolean(result.get("vitaminD"));
            String melanomaRisk = result.get("riskDescription");
            if(vitD){
                tvResult.setText("There is issue with vitamin D" + " \nMelanoma issue level is: " + melanomaRisk);
            } else{
                tvResult.setText("There is no issue with vitamin D" + " \nMelanoma issue level is: " + melanomaRisk);
            }

        }
    }
}
