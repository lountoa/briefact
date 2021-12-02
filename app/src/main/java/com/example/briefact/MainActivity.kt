package com.example.briefact

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.googlecode.tesseract.android.TessBaseAPI.ProgressNotifier
import kotlinx.android.synthetic.main.fab_layout.*
import kotlinx.android.synthetic.main.fragment_main.*

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.Global.getString
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener

import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.android.material.progressindicator.LinearProgressIndicator
import com.googlecode.tesseract.android.TessBaseAPI
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import com.example.briefact.ocr.ImageTextReader.TAG
import com.example.briefact.utils.Constants
import com.example.briefact.utils.CrashUtils
import com.example.briefact.utils.SpUtil
import com.example.briefact.utils.Utils
import com.googlecode.tesseract.android.TessBaseAPI.ProgressValues
import java.lang.Exception
import java.net.MalformedURLException
import com.example.briefact.ocr.ImageTextReader as ImageTextReader


abstract class MainActivity : AppCompatActivity(), ProgressNotifier{
    /*private val TAG = "MainActivity"

    var mImageTextReader: ImageTextReader
    var dialog = null as? AlertDialog

    private val REQUEST_CODE_SETTINGS = 797
    private val REQUEST_CODE_SELECT_IMAGE = 172
    private var isRefresh = false

    abstract var mProgressDialog: ProgressDialog?
    lateinit var crashUtils: CrashUtils
    private lateinit var convertImageToTextTask: ConvertImageToTextTask
    private lateinit var downloadTrainingTask: DownloadTrainingTask
    lateinit var dirBest: File
    lateinit var dirStandard: File
    lateinit var dirFast: File
    lateinit var currentDirectory: File

    lateinit var mTrainingDataType: String

    lateinit var mLanguage: String

    var mPageSegMode: Int = 0

    lateinit var mImageViewL: ImageView

    lateinit var mProgressIndicator: LinearProgressIndicator

    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mFloatingActionButton: FloatingActionButton

    lateinit var show_fab_1: Animation
    lateinit var hide_fab_1: Animation
    lateinit var show_fab_2: Animation
    lateinit var hide_fab_2: Animation
    lateinit var show_fab_3: Animation
    lateinit var hide_fab_3: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

        var status = false

        show_fab_1 = AnimationUtils.loadAnimation(application, R.anim.fab1_show)
        hide_fab_1 = AnimationUtils.loadAnimation(application, R.anim.fab1_hide)
        show_fab_2 = AnimationUtils.loadAnimation(application, R.anim.fab2_show)
        hide_fab_2 = AnimationUtils.loadAnimation(application, R.anim.fab2_hide)
        show_fab_3 = AnimationUtils.loadAnimation(application, R.anim.fab3_show)
        hide_fab_3 = AnimationUtils.loadAnimation(application, R.anim.fab3_hide)

        fab.setOnClickListener {
            if (status == false) {
                expandFAB()
                status = true
            } else {
                hideFAB()
                status = false
            }
        }

        SpUtil.getInstance().init(this)
        crashUtils = CrashUtils(applicationContext, "")

        mImageViewL = findViewById(R.id.source_image)
        mProgressIndicator = findViewById(R.id.progress_indicator)
        mSwipeRefreshLayout = findViewById(R.id.swipe_to_refresh)
        mFloatingActionButton = findViewById(R.id.fab_1)

        initDirectories()
        initializeOCR()
        initIntent()
        initViews()
    }

    private fun initViews() {

        mFloatingActionButton.setOnClickListener { v: View? ->
            if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
                if (mImageTextReader != null) {
                    selectImage()
                } else {
                    initializeOCR()
                }
            } else {
                downloadLanguageData(mTrainingDataType, mLanguage)
            }
        }
        mSwipeRefreshLayout.setOnRefreshListener {
            if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
                if (mImageTextReader != null) {
                    val drawable: Drawable = mImageViewL.getDrawable()
                    if (drawable != null) {
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        if (bitmap != null) {
                            isRefresh = true
                            ConvertImageToTextTask()
                                .execute(bitmap)
                        }
                    }
                } else {
                    initializeOCR()
                }
            } else {
                downloadLanguageData(mTrainingDataType, mLanguage)
            }
            mSwipeRefreshLayout.isRefreshing = false
        }
        if (Utils.isPersistData()) {
            val bitmap: Bitmap? = loadBitmapFromStorage()
            if (bitmap != null) {
                mImageViewL.setImageBitmap(bitmap)
            }
        }
    }

    private fun initIntent() {
        val intent = intent
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/")) {
                val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                if (imageUri != null) {
                    mImageViewL.setImageURI(imageUri)
                    CropImage.activity(imageUri).start(this)
                }
            }
        }
    }
    private fun initDirectories() {
        dirBest = File(getExternalFilesDir("best")!!.absolutePath)
        dirFast = File(getExternalFilesDir("fast")!!.absolutePath)
        dirStandard = File(getExternalFilesDir("standard")!!.absolutePath)
        dirBest.mkdirs()
        dirStandard.mkdirs()
        dirFast.mkdirs()
        currentDirectory = File(dirBest, "tessdata")
        currentDirectory.mkdirs()
        currentDirectory = File(dirStandard, "tessdata")
        currentDirectory.mkdirs()
        currentDirectory = File(dirFast, "tessdata")
        currentDirectory.mkdirs()
    }

    private fun initializeOCR() {
        val cf: File
        mTrainingDataType = Utils.getTrainingDataType()
        mLanguage = Utils.getTrainingDataLanguage()
        mPageSegMode = Utils.getPageSegMode()
        when (mTrainingDataType) {
            "best" -> {
                currentDirectory = File(dirBest, "tessdata")
                cf = dirBest
            }
            "standard" -> {
                cf = dirStandard
                currentDirectory = File(dirStandard, "tessdata")
            }
            else -> {
                cf = dirFast
                currentDirectory = File(dirFast, "tessdata")
            }
        }
        if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
            //region Initialize image text reader
            object : Thread() {
                override fun run() {
                    try {
                        if (mImageTextReader != null) {
                            mImageTextReader!!.tearDownEverything()
                        }
                        mImageTextReader = ImageTextReader.geInstance(
                            cf.absolutePath,
                            mLanguage,
                            mPageSegMode,
                            ::onProgressValues
                        )
                        //check if current language data is valid
                        //if it is invalid(i.e. corrupted, half downloaded, tempered) then delete it
                        if (!mImageTextReader.success) {
                            val destf = File(
                                currentDirectory,
                                java.lang.String.format(Constants.LANGUAGE_CODE, mLanguage)
                            )
                            destf.delete()
                            mImageTextReader = null
                        } else {
                            Log.d(TAG,
                                "initializeOCR: Reader is initialize with lang:$mLanguage"
                            )
                        }
                    } catch (e: Exception) {
                        crashUtils.logException(e)
                        val destf = File(
                            currentDirectory,
                            java.lang.String.format(Constants.LANGUAGE_CODE, mLanguage)
                        )
                        destf.delete()
                        mImageTextReader = null
                    }
                }
            }.start()
            //endregion
        } else {
            downloadLanguageData(mTrainingDataType, mLanguage)
        }
    }

    private fun downloadLanguageData(dataType: String, lang: String) {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        val langToDownload = ArrayList<String>()
        if (lang.contains("+")) {
            val lang_codes = lang.split("\\+").toTypedArray()
            for (lang_code in lang_codes) {
                if (!isLanguageDataExists(dataType, lang)) {
                    langToDownload.add(lang_code)
                }
            }
        }
        if (ni == null) {
            //You are not connected to Internet
            Toast.makeText(
                this,
                getString(R.string.you_are_not_connected_to_internet),
                Toast.LENGTH_SHORT
            ).show()
        } else if (ni.isConnected) {
            //region show confirmation dialog, On 'yes' download the training data.
            val msg = String.format(getString(R.string.download_description), lang)
            dialog = AlertDialog.Builder(this)
                .setTitle(R.string.training_data_missing)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(
                    R.string.yes
                ) { dialog, which ->
                    dialog.cancel()
                    downloadTrainingTask =
                        DownloadTrainingTask()
                    downloadTrainingTask.execute(dataType, lang)
                }
                .setNegativeButton(
                    R.string.no
                ) { dialog, which ->
                    dialog.cancel()
                    if (!isLanguageDataExists(dataType, lang)) {
                        //  show dialog to change language
                    }
                }.create()
            dialog.show()
            //endregion
        } else {
            Toast.makeText(
                this,
                getString(R.string.you_are_not_connected_to_internet),
                Toast.LENGTH_SHORT
            ).show()
            //You are not connected to Internet
        }
    }

    /**
     * Check if language data exists
     *
     * @param dataType data type i.e best, fast, standard
     * @param lang     language
     * @return true if language data exists
     */
    private fun isLanguageDataExists(dataType: String, lang: String): Boolean {
        currentDirectory = when (dataType) {
            "best" -> File(dirBest, "tessdata")
            "standard" -> File(dirStandard, "tessdata")
            else -> File(dirFast, "tessdata")
        }
        return if (lang.contains("+")) {
            val lang_codes = lang.split("\\+").toTypedArray()
            for (code in lang_codes) {
                val file =
                    File(currentDirectory, java.lang.String.format(Constants.LANGUAGE_CODE, code))
                if (!file.exists()) return false
            }
            true
        } else {
            val language =
                File(currentDirectory, java.lang.String.format(Constants.LANGUAGE_CODE, lang))
            language.exists()
        }
    }

    /**
     * select the image when button is clicked
     * using edmodo
     */
    private fun selectImage() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    /**
     * Executed after onActivityResult or when used from shared menu
     *
     *
     * convert the image uri to bitmap
     * call the appropriate AsyncTask based on Settings
     *
     * @param imageUri uri of selected image
     */
    private fun convertImageToText(imageUri: Uri) {
        //Utils.putLastUsedImageLocation(imageUri.toString());
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mImageViewL.setImageURI(imageUri)
        convertImageToTextTask = ConvertImageToTextTask()
        convertImageToTextTask.execute(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTINGS) {
            initializeOCR()
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                val isCamera: Boolean
                isCamera = if (data == null || data.data == null) {
                    true
                } else {
                    val action = data.action
                    action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
                }
                val selectedImageUri: Uri?
                selectedImageUri = data!!.data
                if (selectedImageUri == null) return
                convertImageToText(selectedImageUri)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                convertImageToText(result.uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (convertImageToTextTask != null && convertImageToTextTask.getStatus() == AsyncTask.Status.RUNNING) {
            convertImageToTextTask.cancel(true)
            Log.d(TAG, "onDestroy: image processing canceled")
        }
        if (downloadTrainingTask != null && downloadTrainingTask.getStatus() == AsyncTask.Status.RUNNING) {
            downloadTrainingTask.cancel(true)
        }
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        if (mProgressDialog != null) {
            mProgressDialog!!.cancel()
            mProgressDialog = null
        }
        if (mImageTextReader != null) mImageTextReader!!.tearDownEverything()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val showHistoryItem = menu.findItem(R.id.fab_3)
        showHistoryItem.isVisible = Utils.isPersistData()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.fab_2) {
            startActivityForResult(
                Intent(this, SettingsActivity::class.java),
                REQUEST_CODE_SETTINGS
            )
        } else if (id == R.id.fab_3) {
            showOCRResult(Utils.getLastUsedText())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProgressValues(progressValues: ProgressValues) {
        Log.d(TAG, "onProgressValues: percent " + progressValues.percent)
        runOnUiThread { mProgressIndicator.progress = (progressValues.percent * 1.46).toInt() }
    }

    fun saveBitmapToStorage(bitmap: Bitmap) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput("last_file.jpeg", MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream)
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun loadBitmapFromStorage(): Bitmap? {
        var bitmap: Bitmap? = null
        val fileInputStream: FileInputStream
        try {
            fileInputStream = openFileInput("last_file.jpeg")
            bitmap = BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun showOCRResult(text: String?) {
        if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            val bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text)
            bottomSheetResultsFragment.show(supportFragmentManager, "bottomSheetResultsFragment")
        }
    }

    /**
     * A Async Task to convert the image into text the return the text in String
     */
    private inner class ConvertImageToTextTask : AsyncTask<Bitmap?, Void?, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mProgressIndicator.setProgress(0)
            mProgressIndicator.setVisibility(View.VISIBLE)
            mImageViewL.animate()
                .alpha(.2f)
                .setDuration(450)
                .start()
        }

        override fun onPostExecute(text: String) {
            mProgressIndicator.setVisibility(View.GONE)
            mImageViewL.animate()
                .alpha(1f)
                .setDuration(450)
                .start()
            val clean_text = Html.fromHtml(text).toString().trim { it <= ' ' }
            Log.d(TAG, "onPostExecute: text\n$clean_text")
            showOCRResult(clean_text)
            Toast.makeText(
                this@MainActivity,
                "With Confidence:" + mImageTextReader?.accuracy.toString() + "%",
                Toast.LENGTH_SHORT
            ).show()
            Utils.putLastUsedText(clean_text)
            val bitmap: Bitmap? = loadBitmapFromStorage()
            if (bitmap != null) {
                mImageViewL.setImageBitmap(bitmap)
            }
        }

        override fun doInBackground(vararg bitmaps: Bitmap?): String {
            var bitmap: Bitmap? = bitmaps[0]
            if (!isRefresh && Utils.isPreProcessImage()) {
                bitmap = Utils.preProcessBitmap(bitmap)
                // bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            }
            isRefresh = false
            if (bitmap != null) {
                saveBitmapToStorage(bitmap)
            }
            return mImageTextReader?.getTextFromBitmap(bitmap).toString()
        }
    }

    /**
     * Download the training Data and save this to external storage
     */
    private open inner class DownloadTrainingTask : AsyncTask<String?, Int?, Boolean?>() {
        var size: String? = null
        override fun onPreExecute() {
            super.onPreExecute()
            mProgressDialog = ProgressDialog(this@MainActivity)
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgressDialog.setIndeterminate(true)
            mProgressDialog.setCancelable(false)
            mProgressDialog.setTitle(getString(R.string.downloading))
            mProgressDialog.setMessage(getString(R.string.downloading_language))
            mProgressDialog.show()
        }

        protected override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val percentage = values[0]
            if (mProgressDialog != null) {
                mProgressDialog!!.setMessage(percentage.toString() + getString(R.string.percentage_downloaded) + size)
                mProgressDialog!!.show()
            }
        }

        override fun onPostExecute(bool: Boolean?) {
            if (mProgressDialog != null) {
                mProgressDialog!!.cancel()
                mProgressDialog = null
            }
            initializeOCR()
        }

        /**
         * done the actual work of download
         *
         * @param dataType data type i.e best, fast, standard
         * @param lang     language
         * @return true if success else false
         */
        private fun downloadTraningData(dataType: String, lang: String): Boolean {
            var result = true
            var downloadURL: String
            var location: String
            downloadURL = when (dataType) {
                "best" -> java.lang.String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang)
                "standard" -> java.lang.String.format(
                    Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD,
                    lang
                )
                else -> java.lang.String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang)
            }
            var url: URL
            var base: URL
            var next: URL
            var conn: HttpURLConnection
            try {
                while (true) {
                    Log.v(TAG, "downloading $downloadURL")
                    url = try {
                        URL(downloadURL)
                    } catch (ex: MalformedURLException) {
                        Log.e(TAG, "url $downloadURL is bad: $ex")
                        return false
                    }
                    conn = url.openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = false
                    when (conn.responseCode) {
                        HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP -> {
                            location = conn.getHeaderField("Location")
                            base = URL(downloadURL)
                            next = URL(base, location) // Deal with relative URLs
                            downloadURL = next.toExternalForm()
                            continue
                        }
                    }
                    break
                }
                conn.connect()
                val totalContentSize = conn.contentLength
                size = Utils.getSize(totalContentSize)
                val input: InputStream = BufferedInputStream(url.openStream())
                val destf: File =
                    File(currentDirectory, java.lang.String.format(Constants.LANGUAGE_CODE, lang))
                destf.createNewFile()
                val output: OutputStream = FileOutputStream(destf)
                val data = ByteArray(1024 * 6)
                var count: Int
                var downloaded = 0
                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                    downloaded += count
                    val percentage = downloaded * 100 / totalContentSize
                    publishProgress(percentage)
                }
                output.flush()
                output.close()
                input.close()
            } catch (e: Exception) {
                result = false
                Log.e(TAG, "failed to download $downloadURL : $e")
                e.printStackTrace()
                crashUtils.logException(e)
            }
            return result
        }

        override fun doInBackground(vararg languages: String?): Boolean? {
            val dataType = languages[0]
            val lang = languages[1]
            var ret = true
            return if (lang!!.contains("+")) {
                val lang_codes = lang?.split("\\+").toTypedArray()
                for (code in lang_codes) {
                    if (!isLanguageDataExists(dataType.toString(), code)) {
                        ret = ret and downloadTraningData(dataType!!, code)
                    }
                }
                ret
            } else {
                downloadTraningData(dataType!!, lang)
            }
        }
    }

    private fun expandFAB() {
        val layoutParams1: FrameLayout.LayoutParams = fab_1.layoutParams as FrameLayout.LayoutParams
        layoutParams1.rightMargin += (fab_1.width * 1.7).toInt()
        layoutParams1.bottomMargin += (fab_1.height * 0.25).toInt()
        fab_1.layoutParams = layoutParams1
        fab_1.startAnimation(show_fab_1)
        fab_1.isClickable = true

        val layoutParams2 = fab_2.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin += (fab_2.width * 1.5).toInt()
        layoutParams2.bottomMargin += (fab_2.height * 1.5).toInt()
        fab_2.layoutParams = layoutParams2
        fab_2.startAnimation(show_fab_2)
        fab_2.isClickable = true

        val layoutParams3 = fab_3.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin += (fab_3.width * 0.25).toInt()
        layoutParams3.bottomMargin += (fab_3.height * 1.7).toInt()
        fab_3.layoutParams = layoutParams3
        fab_3.startAnimation(show_fab_3)
        fab_3.isClickable = true
    }

    private fun hideFAB() {
        val layoutParams1 = fab_1.layoutParams as FrameLayout.LayoutParams
        layoutParams1.rightMargin -= (fab_1.width * 1.7).toInt()
        layoutParams1.bottomMargin -= (fab_1.height * 0.25).toInt()
        fab_1.layoutParams = layoutParams1
        fab_1.startAnimation(hide_fab_1)
        fab_1.isClickable = false

        val layoutParams2 = fab_2.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin -= (fab_2.width * 1.5).toInt()
        layoutParams2.bottomMargin -= (fab_2.height * 1.5).toInt()
        fab_2.layoutParams = layoutParams2
        fab_2.startAnimation(hide_fab_2)
        fab_2.isClickable = false

        val layoutParams3 = fab_3.layoutParams as FrameLayout.LayoutParams
        layoutParams3.rightMargin -= (fab_3.width * 0.25).toInt()
        layoutParams3.bottomMargin -= (fab_3.height * 1.7).toInt()
        fab_3.layoutParams = layoutParams3
        fab_3.startAnimation(hide_fab_3)
        fab_3.isClickable = false
    }
    */
}