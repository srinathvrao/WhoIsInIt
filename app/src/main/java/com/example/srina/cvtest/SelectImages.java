package com.example.srina.cvtest;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.FaceEmbedding;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class SelectImages extends AppCompatActivity {

    private String label = "";
    public int i,max,test=0;
    String imageEncoded;
    public byte[] inputData;
    private static final String TAG = "OCVSample::Activity";
    List<String> imagesEncodedList;
    private final int PICK_IMAGE_MULTIPLE = 1;
    public String apiKey;
    public ClarifaiClient client;
    //public ProgressBar pb;
    ProgressDialog progressDialog;
    double embedding[]=new double[1024];
    double sumembedding[]=new double[1024];
    public int no_faces = 0;
    public boolean embed = false;
    public InputStream iStream;
    public JSONObject x;
   public ArrayList<Uri> mArrayUri;
    public double sum[] = new double[1024];
    public double total_no_faces = 0;
    public JSONArray jarray;
    String PREF_NAME="Pref";
    SharedPreferences prefs=null;
    SharedPreferences.Editor editor=null;
    int countthres=0;
    double threstest[][] = new double[2][1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);
        progressDialog = new ProgressDialog(SelectImages.this);
        progressDialog.setTitle("Training images....");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        final Button b = (Button) findViewById(R.id.select);
        final EditText edt = (EditText) findViewById(R.id.label);

        try{
            apiKey = getString(R.string.clarifai_api_key);
            client = new ClarifaiBuilder(apiKey).client(
                    new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60,TimeUnit.SECONDS)
                            .writeTimeout(60,TimeUnit.SECONDS)
                            .build()
            ).buildSync();
        }
        catch (Exception e){
            Toast.makeText(this, "Error accessing clarifai", Toast.LENGTH_SHORT).show();
        }

        //pb = (ProgressBar) findViewById(R.id.progbar);

//        P.setq(q);
//        S.setq(q);
//        q.setContext2(getApplicationContext());

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label = edt.getEditableText().toString();
                if(label.equals(""))
                    Toast.makeText(SelectImages.this, "Enter a proper label.", Toast.LENGTH_SHORT).show();
                else{
                    //q.setLabel(label);
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);

                    /*
                        startActivity for result... select images

                     */
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();
                    Toast.makeText(getApplicationContext(), "Select more than one image.", Toast.LENGTH_SHORT).show();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        //Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        max = mArrayUri.size();
//                        q.max_faces = max;
//                        P.t.start();
//                        S.t.start();
                        Log.e(TAG,"Traversing through the images...... number of images: "+max);
                             new AsyncTask<Void, Void, Integer>() {
                                @Override
                                protected Integer doInBackground(Void... params) {
                                    Log.e(TAG,"evaluating BYTE ARRAY");
                                    // mRgba to return_buff (Mat to byte[] )
                                    Log.e(TAG,"requesting face embedding for face "+i );
                                    for(int hey=0;hey<1024;hey++) sum[i] = 0;
                                    // max
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.setMax(max);
                                            progressDialog.show();

                                        }
                                    });

                                    for(i=0;i<max && i<25;i++) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                progressDialog.setProgress(i);
                                            }
                                        });
                                        try{
                                            iStream = getContentResolver().openInputStream(mArrayUri.get(i));
                                            inputData = getBytes(iStream);

                                            ClarifaiResponse<List<ClarifaiOutput<FaceEmbedding>>> response = client.getDefaultModels().faceEmbeddingModel().predict()
                                                    .withInputs(ClarifaiInput.forImage(inputData))
                                                    .executeSync();
                                            if (!response.isSuccessful()) {
                                                Log.e(TAG,"Response not successful");
                                            }
                                            else {
                                                // convert JSON array to float array
                                                x = new JSONObject(response.rawBody());
                                                no_faces = x.getJSONArray("outputs").getJSONObject(0).getJSONObject("data").getJSONArray("regions").length();
                                                for (int face_no = 0; face_no < no_faces; face_no++) {
                                                    jarray = x.getJSONArray("outputs").getJSONObject(0).getJSONObject("data").getJSONArray("regions").getJSONObject(face_no).getJSONObject("data").getJSONArray("embeddings").getJSONObject(0).getJSONArray("vector");
                                                    //if(jarray.getDouble(512)!=0)
                                                    for (int dimension = 0; dimension < 1024; dimension++) {
                                                        sum[dimension] += jarray.getDouble(dimension);
                                                        //threstest[countthres][dimension] = jarray.getDouble(dimension);
                                                    }
                                                    //countthres++;
                                                }
                                                //if(jarray.getDouble(512)!=0.0)
                                                total_no_faces += no_faces;
                                                Log.e(TAG, "testing " + i + " " + jarray.getDouble(512));

                                                //Log.e(TAG,"NUMBER OF FACES: "+no_faces);
                                            }
                                        }
                                        catch (Exception e){
                                            Log.e(TAG,e.getMessage());
                                        }

                                    }
                                    for(int wassup=0;wassup<1024;wassup++){
                                        sum[wassup] /= total_no_faces;
                                    }
                                    writeFaceData(label,sum);
                                    Log.e(TAG,"Finished training: "+label+" "+sum[512]);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            Toast.makeText(SelectImages.this, "Training complete!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SelectImages.this,MainActivity.class));
                                            finish();

                                        }
                                    });

                                    //cosineDistance(threstest[0],threstest[1]);

                                    return 1;
                                }

                            }.execute();

                        }

                        Toast.makeText(getApplicationContext(), "The face will be trained in the background.\nGive upto 3 minutes.\nDo not close this activity", Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(this, "You haven't picked an Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String cosineDistance(double f[], double f2[]){
        double cosine_val;
        double c_tot,temp;
        double scale=100.0;
        double A_B,modA,modB;

        double min_cosine=3; //minimum cosine value
        String min_name="Unrecognized";
        //Calculating |A|
        c_tot=0.0;
        for(int dimension=0;dimension<1024;dimension++){
            temp=scale*f[dimension];
            c_tot+=temp*temp;
        }
        Log.e(TAG,"A dimen : "+f[512]);
        modA=Math.sqrt(c_tot);
        Log.e(TAG,"modA : "+modA);
        //for(int face_no=0;face_no<AFD.no_of_faces;face_no++){
        c_tot=0.0;
        A_B=0;
        for(int dimension=0;dimension<1024;dimension++){
            temp=scale*f2[dimension];
            c_tot+=temp*temp;
            A_B+=(scale*f[dimension])*(scale*f2[dimension]);
        }
        Log.e(TAG,"B dimen : "+f2[512]);
        modB=Math.sqrt(c_tot);
        Log.e(TAG,"modB : "+modB);
        cosine_val= 1 - (A_B)/(modA*modB);
        if(min_cosine>cosine_val) min_cosine=cosine_val; //min_name=AFD.names[face_no];}
        //}
        ///Write code here to return based on the threshold
        // if min_cosine is closer to 2 or min_name is unrecognized return "Unrecognized"
        // else return min_name itself
        Log.e(TAG,"Minimum cosine: "+min_cosine + " cosine distance : " + cosine_val);

        return "";

    }

    void writeFaceData(String name,double v[])  {
        prefs=getApplicationContext().getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        editor=prefs.edit();
        try {
            JSONArray jr = new JSONArray();
            JSONObject jobj=new JSONObject();
            for (int i = 0; i < 1024; i++) jr.put(v[i]);
            jobj.put("vector",jr);
            editor.putString(name,jobj.toString());
            editor.apply();
            //Toast.makeText(getApplicationContext(), "Trained : "+label, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e(TAG,"ERROR writing facedata "+e.getMessage());
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
