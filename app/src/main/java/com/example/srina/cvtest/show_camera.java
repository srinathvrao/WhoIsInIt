package com.example.srina.cvtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.FaceEmbedding;
import okhttp3.OkHttpClient;


// OpenCV Classes

public class show_camera extends AppCompatActivity implements CvCameraViewListener2 {


    public FloatingActionButton fab;
    private Mat grayscaleImage;
    private CascadeClassifier cascadeClassifier;
    private int absoluteFaceSize;
    public int count = 0;
    String teststr, key;
    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";
    public boolean startre = false;
    private ArrayList<String> REPORT = new ArrayList<String>();
    ByteArrayOutputStream byteArrayOutputStream;
    Bitmap bitmap;
    Bitmap image;
    byte[] byteArray;
    private PrintWriter pw;
    private FileWriter writer;
    public String apiKey ;
    public ClarifaiClient client;
    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    private Button buttupload;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Thread three;

    Mat mRgbaT;
    byte[] return_buff;
    String PREF_NAME = "Pref";
    SharedPreferences prefs = null;
    public Map<String, Integer> MAP = new HashMap<String, Integer>();
    SharedPreferences.Editor editor = null;
    AllFaceData AFD;
    //public double sum[] = new double[1024];

    //public Thread1 t1;

    /*
        image = Bitmap.createBitmap(mRgba.cols(),
                                    mRgba.rows(), Bitmap.Config.RGB_565);

                            Utils.matToBitmap(mRgba, image);

                            bitmap = (Bitmap) image;
                            bitmap = Bitmap.createScaledBitmap(bitmap, 600, 450, false);
                            byteArrayOutputStream = new ByteArrayOutputStream();

                                compress function is inefficient... try something else....

                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    byteArray = byteArrayOutputStream .toByteArray();

     */

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    public show_camera() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public AllFaceData readAllFaceData() {
        prefs = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        Map<String, ?> keys = prefs.getAll();
        int c_size = 0;
        AllFaceData AFD = new AllFaceData();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            AFD.names[c_size] = entry.getKey();
            try {
                JSONObject jA = new JSONObject(entry.getValue().toString());
                JSONArray jAr = jA.getJSONArray("vector");
                for (int i = 0; i < 1024; i++) {
                    AFD.vectors[c_size][i] = jAr.getDouble(i);
                    //Log.i("vAL:",""+AFD.vectors[c_size][i] );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            c_size++;
        }
        AFD.no_of_faces = c_size;
        return AFD;
    }


    public Thread t1 = new Thread() {
        public void run() {
            try {
                while (startre) {
                    //Log.e(TAG, "requesting face embedding");
                    new AsyncTask<Void, Integer, Double>() {
                        @Override
                        protected Double doInBackground(Void... params) {

                            image = Bitmap.createBitmap(mRgba.cols(),
                                    mRgba.rows(), Bitmap.Config.RGB_565);

                            Utils.matToBitmap(mRgba, image);

                            image = Bitmap.createScaledBitmap(image, 600, 450, false);
                            byteArrayOutputStream = new ByteArrayOutputStream();

                            // compress function is inefficient... try something else....

                            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();
                            Log.e(TAG,"API call started");
                            ClarifaiResponse<List<ClarifaiOutput<FaceEmbedding>>> response = client.getDefaultModels().faceEmbeddingModel().predict()
                                    .withInputs(ClarifaiInput.forImage(byteArray))
                                    .executeSync();
                            Log.e(TAG,"API call started222");
                            // 1.5 seconds
                            if (!response.isSuccessful()) {
                                Log.e(TAG, "Response not successful");
                            }
                            else {
                                try {
                                    JSONObject x = new JSONObject(response.rawBody());
                                    JSONArray jarray = null;
                                    double[] testarr = new double[1024];
                                    int i = 0;

                                    int no_faces = x.getJSONArray("outputs").getJSONObject(0).getJSONObject("data").getJSONArray("regions").length();
                                    for (int face_no = 0; face_no < no_faces; face_no++) {
                                        for (int xyz = 0; xyz < 1024; xyz++) testarr[xyz] = 0;
                                        jarray = x.getJSONArray("outputs").getJSONObject(0).getJSONObject("data").getJSONArray("regions").getJSONObject(face_no).getJSONObject("data").getJSONArray("embeddings").getJSONObject(0).getJSONArray("vector");
                                        for (int dimension = 0; dimension < 1024; dimension++) {
                                            testarr[dimension] = jarray.getDouble(dimension);
                                        }

                                        teststr = cosineDistance(testarr);
                                        if (teststr.equals("Unrecognized")) {
                                            Log.e(TAG, "NOT RECOGNIZED");
                                            return -1.0;
                                        } else {
                                            Log.e(TAG, "RECOGNIZED: " + teststr);
                                            return 1.0;
                                        }


                                    }
                                    //int total_no_faces += no_faces;
                                    //Log.e(TAG, "testing " + i + " " + jarray.getDouble(512));
                                } catch (Exception e) {
                                    Log.e(TAG, "EXCEPTION " + e.getMessage());
                                }
                            }
                            return 0.0;
                        }

                        protected void onProgressUpdate(Integer... progress) {

                        }

                        protected void onPostExecute(Double response) {
                            Log.e(TAG, "inside onpostexecute");
                            if (response == 1.0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (MAP.get(teststr) == 0) {
                                            Log.e(TAG, "inside onpostexecute-- recognized ");
                                            Log.e(TAG, "inside onpostexecute-- onuithread ");
                                            fab.show();
                                            MAP.put(teststr, 1);
                                        } else {
                                            Log.e(TAG, "Person already recognized in MAP.....~~~~~~~~~~~~~~~~~~ " + teststr);
                                        }

                                    }
                                });
                            } else {
                                Log.e(TAG, "onpostexecute - not recognized");
                            }
                        }

                    }.execute();
                    this.sleep(330);
                    while (!startre)
                        this.sleep(500);
                }
            } catch (Exception e) {
                Log.e(TAG, "123 " + e.getMessage());
            }
        }

    };


    public String cosineDistance(double f[]) {
        double cosine_val;
        double c_tot, temp;
        double scale = 100.0;
        double A_B, modA, modB;

        double min_cosine = 3; //minimum cosine value
        String min_name = "Unrecognized";
        //Calculating |A|
        c_tot = 0.0;
        for (int dimension = 0; dimension < 1024; dimension++) {
            temp = scale * f[dimension];
            c_tot += temp * temp;
        }
        modA = Math.sqrt(c_tot);

        for (int face_no = 0; face_no < AFD.no_of_faces; face_no++) {
            c_tot = 0.0;
            A_B = 0;
            for (int dimension = 0; dimension < 1024; dimension++) {
                temp = scale * AFD.vectors[face_no][dimension];
                c_tot += temp * temp;
                A_B += (scale * f[dimension]) * (scale * AFD.vectors[face_no][dimension]);
            }
            modB = Math.sqrt(c_tot);
            cosine_val = 1 - (A_B) / (modA * modB);
            if (min_cosine > cosine_val) {
                min_cosine = cosine_val;
                min_name = AFD.names[face_no];
            }
        }
        ///Write code here to return based on the threshold
        // if min_cosine is closer to 2 or min_name is unrecognized return "Unrecognized"
        // else return min_name itself
        //Log.e(TAG,"Minimum cosine: "+min_cosine + " name: "+min_name);

        Log.e(TAG,min_cosine+" ... " + min_name);
        if (min_cosine < 0.5)
            return min_name;
        else
            return "Unrecognized";
        /*
              if minimum cosine is 0.5 or lower, then correct name!!!
               else, nope :(
         */


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_show_camera);


        try {

            apiKey = getString(R.string.clarifai_api_key);
            client =  new ClarifaiBuilder(apiKey).client(
                    new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60,TimeUnit.SECONDS)
                            .writeTimeout(60,TimeUnit.SECONDS)
                            .build()
            ).buildSync();

//            File cascadeDir = getDir("cascade",Context.MODE_PRIVATE);
//            File mCascadeFile = new File(cascadeDir,"haarcascade_frontalface.xml");
//            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

            final TextView txtview = (TextView) findViewById(R.id.faces123);

            mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

            //mOpenCvCameraView.setLayoutParams(new WindowManager.LayoutParams(width, hei));
            final ScrollView sv =((ScrollView) findViewById(R.id.MainScroll));

            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

            mOpenCvCameraView.setCvCameraViewListener(this);
            final Button b = (Button) findViewById(R.id.startrec);

            AFD = readAllFaceData();
            for(int hello=0;hello<AFD.no_of_faces;hello++)
                Log.e(TAG,AFD.names[hello]);


            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    count++;
                    if (count % 2 != 0) {
                        b.setText("Stop recognizing");
                        startre = true;
                        if (count == 1) t1.start();
                    } else {
                        b.setText("Start recognizing");
                        startre = false;
                    }


                }
            });
            fab = (FloatingActionButton) findViewById(R.id.fab);

            buttupload = (Button) findViewById(R.id.uploadDeets);

            Log.e(TAG,"Srinath: ...... "+MAP.get("Srinath"));
            for (int siddsux = 0; siddsux < AFD.no_of_faces; siddsux++)
                MAP.put(AFD.names[siddsux], 0);
            //MAP.put("Srinath",1);
            //MAP.put("Vignesh",1);
            //REPORT.add("Srinath");
            Toast.makeText(getApplicationContext(), "Loaded the data. \nGo ahead and test it out!", Toast.LENGTH_SHORT).show();

            three = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!three.isInterrupted()) {
                            three.sleep(1500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    //Log.e(TAG,"started? "+startre);
                                        String names = "";
                                        for (String key2 : MAP.keySet()) {
                                            if (MAP.get(key2) == 1) {
                                                names += "\n"+key2;
                                                txtview.setText("People that appeared:"+names + "\n");
                                                //Log.e(TAG, "updating textview.......... " + key2);
                                            }
                                        }

                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.hide();
                    final ScrollView svtest = (ScrollView) findViewById(R.id.MainScroll);
                    svtest.scrollTo(0,svtest.getBottom());
                }
            });

            three.start();

        } catch (Exception e) {
            Log.e(TAG, "oncreate error " + e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {


        grayscaleImage = new Mat(height, width,CvType.CV_8UC4);
        Log.e(TAG, height + " " + width);
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        absoluteFaceSize = (int) (height * 0.2);
        //t1 = new Thread1(mRgba);

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        // Rotate mRgba 90 degrees
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, mRgba, 1);

        return mRgba; // This function must return
    }

//    Mat getFaceOutlineDrawn(Mat img){
//
//
//////code for converting mat "img" to bitmap "picture" enclose it below
//        //This is the variable that stores the converted object dont change its name
//        // image is Bitmap.....
//        try {
//
//            Bitmap picture = Bitmap.createBitmap(img.cols(),
//                    img.rows(), Bitmap.Config.RGB_565);
//            Utils.matToBitmap(img, picture);
//
////            picture = Bitmap.createScaledBitmap(picture, 600, 450, false);
//
//
//            //picture = Bitmap.createScaledBitmap(picture, 600, 450, false);
//////
//
//            Paint myRectPaint = new Paint();
//            myRectPaint.setStrokeWidth(2); //Change this  for thickness of outline
//            myRectPaint.setColor(Color.RED); //Change for color of outline
//            myRectPaint.setStyle(Paint.Style.STROKE);
//            Bitmap tempBitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
//            Canvas tempCanvas = new Canvas(tempBitmap);
//            tempCanvas.drawBitmap(picture, 0, 0, null);
//            FaceDetector faceDetector = new
//                    FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
//                    .build();
//            Frame frame = new Frame.Builder().setBitmap(picture).build();
//            SparseArray<Face> faces = faceDetector.detect(frame);
//
//            for (int i = 0; i < faces.size(); i++) {
//                Face thisFace = faces.valueAt(i);
//                float x1 = thisFace.getPosition().x;
//                float y1 = thisFace.getPosition().y;
//                float x2 = x1 + thisFace.getWidth();
//                float y2 = y1 + thisFace.getHeight();
//                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
//            }
//
//            picture = (new BitmapDrawable(getResources(), tempBitmap)).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
//
//            Utils.bitmapToMat(picture, img);
//        }
//        catch (Exception e){
//            Log.e(TAG," conversion!!!??? "+e.getMessage());
//        }
//////Include code here for converting bitmap "picture" to mat "img" and return that mat
//        //To save resources store the converted bitmap in mat img itself and return it
//        // img=....
//        return img;
//////
//    }


}

//}