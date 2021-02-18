import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.codelab.mlkit.BrowseAcrivity;
import com.google.codelab.mlkit.FaceContourGraphic;
import com.google.codelab.mlkit.GraphicOverlay;
import com.google.codelab.mlkit.NewBrowserActivity;
import com.google.codelab.mlkit.R;
import com.google.codelab.mlkit.TextGraphic;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static com.google.codelab.mlkit.BrowseAcrivity.isPicture;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Button mTextButton;
    private Button mFaceButton;
    private Bitmap mSelectedImage;
    boolean recFinshed = false;
    int SELECT_PICTURE =101;
    int BROWSE = 201;
    int SELECTFOLDER = 301;
    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    String recognized = "";
    String path;
    ProgressDialog progressDialog;

    Context context;
    /**
     * Number of results to show in the UI.
     */
    private static final int RESULTS_TO_SHOW = 3;

    /**
     * Dimensions of inputs.
     */
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;


    private void doRecognizeLoop(String path) {
        File roort = new File(path);
        File[] files = roort.listFiles();
        new com.google.codelab.mlkit.MainActivity.myClass(com.google.codelab.mlkit.MainActivity.this, "Hello!", path).execute();
    }

    public class myClass extends AsyncTask<Void,Void,Void> {
        //        private ProgressDialog dialog;
        private String paramOne;
        private String path;

        public myClass(Activity activity, String paramOne, String path) {
//            dialog = new ProgressDialog(activity);
            this.paramOne = paramOne; // "Hello"
            this.path = path; // 123
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File roort = new File(path);
            File[] files = roort.listFiles();
            progressDialog.setMax(files.length);

            for (File file : files) {
                final String fn = file.getPath();
                if (mSelectedImage != null) {
                    mSelectedImage = null;
                }
                progressDialog.incrementProgressBy(1);
                if (!isPicture(fn))
                    continue;

                ActivityManager activityManager =  (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                mSelectedImage = BitmapFactory.decodeFile(fn);
                runTextRecognition(fn);
                mGraphicOverlay.clear();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(com.google.codelab.mlkit.MainActivity.this);
            progressDialog.setTitle("Recognition Progress");
            progressDialog.setMessage("Recognizing...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
//            dialog.setMessage("Please wait...");
//            dialog.setIndeterminate(true);
//            dialog.show();
        }
    }



    InputImage image;
    TextRecognizer recognizer;
    private void runTextRecognition(final String localPath) {

        image = null;
        if(recognizer != null)
            recognizer.close();
        recognizer = null;
        if(mSelectedImage == null)
            return;
        image = InputImage.fromBitmap(mSelectedImage, 0);
        mSelectedImage = null;
        recognizer = TextRecognition.getClient();
//        mTextButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                mTextButton.setEnabled(true);
                                recognized = "";
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }
}
