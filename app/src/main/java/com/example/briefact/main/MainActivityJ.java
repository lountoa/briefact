package com.example.briefact.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.briefact.R;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

import com.example.briefact.ocr.ImageTextReader;
import com.example.briefact.utils.Constants;
import com.example.briefact.utils.CrashUtils;
import com.example.briefact.utils.SpUtil;
import com.example.briefact.utils.Utils;

public class MainActivityJ extends AppCompatActivity implements TessBaseAPI.ProgressNotifier {

    private static final String TAG = "MainActivity";
    private static long back_pressed;

    private static final int REQUEST_CODE_SETTINGS = 797;
    private static final int REQUEST_CODE_SELECT_IMAGE = 172;
    private static boolean isRefresh = false;

    public static boolean status = false;

    ProgressDialog mProgressDialog;

    private CrashUtils crashUtils;
    private ConvertImageToTextTask convertImageToTextTask;
    private DownloadTrainingTask downloadTrainingTask;
    private File dirBest;
    private File dirStandard;
    private File dirFast;
    private File currentDirectory;
    private ImageTextReader mImageTextReader;
    private String mTrainingDataType;
    private String mLanguage;
    private int mPageSegMode;
    private AlertDialog dialog;
    private ImageView mImageView;
    private LinearProgressIndicator mProgressIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    Button fab1;
    Button fab2;
    Button fab3;

    private FirebaseAuth firebaseAuth;
    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    //FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        fab1 = findViewById(R.id.fab_1);
        fab2 = findViewById(R.id.fab_2);
        fab3 = findViewById(R.id.fab_3);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivityJ.this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOCRResult(Utils.getLastUsedText());
            }
        });

        SpUtil.getInstance().init(this);
        crashUtils = new CrashUtils(getApplicationContext(), "");

        mImageView = findViewById(R.id.source_image);
        mProgressIndicator = findViewById(R.id.progress_indicator);
        mSwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        initDirectories();
        initializeOCR();
        initIntent();
        initViews();
    }

    private void initViews() {

        fab1.setOnClickListener(v -> {
            if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
                if (mImageTextReader != null) {
                    selectImage();
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData(mTrainingDataType, mLanguage);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
                if (mImageTextReader != null) {
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (bitmap != null) {
                            isRefresh = true;
                            new ConvertImageToTextTask().execute(bitmap);
                        }
                    }
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData(mTrainingDataType, mLanguage);
            }
            mSwipeRefreshLayout.setRefreshing(false);

        });

        if (Utils.isPersistData()) {
            Bitmap bitmap = loadBitmapFromStorage();
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    mImageView.setImageURI(imageUri);
                    CropImage.activity(imageUri).start(this);
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initDirectories() {
        dirBest = new File(getExternalFilesDir("best").getAbsolutePath());
        dirFast = new File(getExternalFilesDir("fast").getAbsolutePath());
        dirStandard = new File(getExternalFilesDir("standard").getAbsolutePath());
        dirBest.mkdirs();
        dirStandard.mkdirs();
        dirFast.mkdirs();
        currentDirectory = new File(dirBest, "tessdata");
        currentDirectory.mkdirs();
        currentDirectory = new File(dirStandard, "tessdata");
        currentDirectory.mkdirs();
        currentDirectory = new File(dirFast, "tessdata");
        currentDirectory.mkdirs();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void initializeOCR() {
        File cf;
        mTrainingDataType = Utils.getTrainingDataType();
        mLanguage = Utils.getTrainingDataLanguage();
        mPageSegMode=Utils.getPageSegMode();


        switch (mTrainingDataType) {
            case "best":
                currentDirectory = new File(dirBest, "tessdata");
                cf = dirBest;
                break;
            case "standard":
                cf = dirStandard;
                currentDirectory = new File(dirStandard, "tessdata");
                break;
            default:
                cf = dirFast;
                currentDirectory = new File(dirFast, "tessdata");

        }

        if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        if (mImageTextReader != null) {
                            mImageTextReader.tearDownEverything();
                        }
                        mImageTextReader = ImageTextReader.geInstance(cf.getAbsolutePath(), mLanguage, mPageSegMode, MainActivityJ.this::onProgressValues);
                        if (!mImageTextReader.success) {
                            File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, mLanguage));
                            destf.delete();
                            mImageTextReader = null;
                        } else {
                            Log.d(TAG, "initializeOCR: Reader is initialize with lang:" + mLanguage);
                        }

                    } catch (Exception e) {
                        crashUtils.logException(e);
                        File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, mLanguage));
                        destf.delete();
                        mImageTextReader = null;
                    }
                }
            }.start();
        } else {
            downloadLanguageData(mTrainingDataType, mLanguage);
        }
    }

    private void downloadLanguageData(final String dataType, final String lang) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        ArrayList<String> langToDownload = new ArrayList<>();
        if (lang.contains("+")) {
            String[] lang_codes = lang.split("\\+");
            for (String lang_code : lang_codes) {
                if (!isLanguageDataExists(dataType, lang)) {
                    langToDownload.add(lang_code);
                }
            }
        }

        if (ni == null) {
            Toast.makeText(this, getString(R.string.internet_issues), Toast.LENGTH_SHORT).show();
        } else if (ni.isConnected()) {
            String msg = String.format(getString(R.string.download_description), lang);
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.training_data_missing)
                    .setCancelable(false)
                    .setMessage(msg)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            downloadTrainingTask = new DownloadTrainingTask();
                            downloadTrainingTask.execute(dataType, lang);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (!isLanguageDataExists(dataType, lang)) {
                                startActivityForResult(new Intent(MainActivityJ.this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
                            }

                        }
                    }).create();
            dialog.show();
        } else {
            Toast.makeText(this, getString(R.string.internet_issues), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLanguageDataExists(final String dataType, final String lang) {
        switch (dataType) {
            case "best":
                currentDirectory = new File(dirBest, "tessdata");
                break;
            case "standard":
                currentDirectory = new File(dirStandard, "tessdata");
                break;
            default:
                currentDirectory = new File(dirFast, "tessdata");

        }
        if (lang.contains("+")) {
            String[] lang_codes = lang.split("\\+");
            for (String code : lang_codes) {
                File file = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, code));
                if (!file.exists()) return false;
            }
            return true;
        } else {
            File language = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang));
            return language.exists();
        }
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void convertImageToText(Uri imageUri) {
        //Utils.putLastUsedImageLocation(imageUri.toString());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mImageView.setImageURI(imageUri);
        convertImageToTextTask = new ConvertImageToTextTask();
        convertImageToTextTask.execute(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            initializeOCR();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                Uri selectedImageUri;
                selectedImageUri = data.getData();
                if (selectedImageUri == null) return;
                convertImageToText(selectedImageUri);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                convertImageToText(result.getUri());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (convertImageToTextTask != null && convertImageToTextTask.getStatus() == AsyncTask.Status.RUNNING) {
            convertImageToTextTask.cancel(true);
            Log.d(TAG, "onDestroy: image processing canceled");
        }
        if (downloadTrainingTask != null && downloadTrainingTask.getStatus() == AsyncTask.Status.RUNNING) {
            downloadTrainingTask.cancel(true);
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        if (mImageTextReader != null) mImageTextReader.tearDownEverything();
    }

    @Override
    public void onProgressValues(final TessBaseAPI.ProgressValues progressValues) {
        Log.d(TAG, "onProgressValues: percent " + progressValues.getPercent());
        runOnUiThread(() -> mProgressIndicator.setProgress((int) (progressValues.getPercent() * 1.46)));
    }

    public void saveBitmapToStorage(Bitmap bitmap) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("last_file.jpeg", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadBitmapFromStorage() {
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = openFileInput("last_file.jpeg");
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void showOCRResult(String text) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            BottomSheetResultsFragment bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text);
            bottomSheetResultsFragment.show(getSupportFragmentManager(), "bottomSheetResultsFragment");
        }

    }

    private class ConvertImageToTextTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            if (!isRefresh && Utils.isPreProcessImage()) {
                bitmap = Utils.preProcessBitmap(bitmap);
                // bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            }
            isRefresh = false;
            saveBitmapToStorage(bitmap);
            return mImageTextReader.getTextFromBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressIndicator.setProgress(0);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String text) {
            mProgressIndicator.setVisibility(View.GONE);
            String clean_text = Html.fromHtml(text).toString().trim();
            Log.d(TAG, "onPostExecute: text\n" + clean_text);
            showOCRResult(clean_text);
            Toast.makeText(MainActivityJ.this,  mImageTextReader.getAccuracy() + "%", Toast.LENGTH_SHORT).show();

            Utils.putLastUsedText(clean_text);
            Bitmap bitmap = loadBitmapFromStorage();
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }

    }

    private class DownloadTrainingTask extends AsyncTask<String, Integer, Boolean> {
        String size;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivityJ.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(getString(R.string.downloading));
            mProgressDialog.setMessage(getString(R.string.downloading_language));
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int percentage = values[0];
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(percentage + getString(R.string.percentage_downloaded) + size);
                mProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
                mProgressDialog = null;
            }
            initializeOCR();
        }


        @Override
        protected Boolean doInBackground(String... languages) {
            String dataType = languages[0];
            String lang = languages[1];
            boolean ret = true;
            if (lang.contains("+")) {
                String[] lang_codes = lang.split("\\+");
                for (String code : lang_codes) {
                    if (!isLanguageDataExists(dataType, code)) {
                        ret &= downloadTraningData(dataType, code);
                    }
                }
                return ret;
            } else {
                return downloadTraningData(dataType, lang);
            }
        }

        private boolean downloadTraningData(String dataType, String lang) {
            boolean result = true;
            String downloadURL;
            String location;

            switch (dataType) {
                case "best":
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                    break;
                case "standard":
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                    break;
                default:
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
            }

            URL url, base, next;
            HttpURLConnection conn;
            try {
                while (true) {
                    Log.v(TAG, "downloading " + downloadURL);
                    try {
                        url = new URL(downloadURL);
                    } catch (java.net.MalformedURLException ex) {
                        Log.e(TAG, "url " + downloadURL + " is bad: " + ex);
                        return false;
                    }
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    switch (conn.getResponseCode()) {
                        case HttpURLConnection.HTTP_MOVED_PERM:
                        case HttpURLConnection.HTTP_MOVED_TEMP:
                            location = conn.getHeaderField("Location");
                            base = new URL(downloadURL);
                            next = new URL(base, location);  // Deal with relative URLs
                            downloadURL = next.toExternalForm();
                            continue;
                    }
                    break;
                }
                conn.connect();

                int totalContentSize = conn.getContentLength();
                size = Utils.getSize(totalContentSize);

                InputStream input = new BufferedInputStream(url.openStream());

                File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang));
                destf.createNewFile();
                OutputStream output = new FileOutputStream(destf);

                byte[] data = new byte[1024 * 6];
                int count, downloaded = 0;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    downloaded += count;
                    int percentage = (downloaded * 100) / totalContentSize;
                    publishProgress(percentage);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                result = false;
                Log.e(TAG, "failed to download " + downloadURL + " : " + e);
                e.printStackTrace();
                crashUtils.logException(e);
            }
            return result;
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

}
