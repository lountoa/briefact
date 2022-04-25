package com.example.briefact.transtate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.briefact.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;

public class TranslateActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceText;
    private Button translateBtn, copyBtn;
    private TextView translateTV;

    /*

    String[] fromLanguage = {"From", (String) getText(R.string.lan_english),(String) getText(R.string.lan_arabic),
            (String) getText(R.string.lan_belarusian),(String) getText(R.string.lan_bulgarian),
            (String) getText(R.string.lan_czech), (String) getText(R.string.lan_hindi), (String) getText(R.string.lan_russian)};
    String[] toLanguage = {"To", (String) getText(R.string.lan_english),(String) getText(R.string.lan_arabic),
            (String) getText(R.string.lan_belarusian),(String) getText(R.string.lan_bulgarian),
            (String) getText(R.string.lan_czech), (String) getText(R.string.lan_hindi), (String) getText(R.string.lan_russian)};
    */

    String[] fromLanguage = {"From", "English","Arabic",
            "Belarusian", "Bulgarian",
           "Czech", "Hindi", "Russian"};

    String[] toLanguage = {"To", "English","Arabic",
            "Belarusian", "Bulgarian",
            "Czech", "Hindi", "Russian"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    String fromLanguageCode, toLanguageCode;
    String languageCode;

    Translator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);

        sourceText = findViewById(R.id.idEditSource);

        translateBtn = findViewById(R.id.idBtnTranslation);
        copyBtn = findViewById(R.id.copy_translate);

        translateTV = findViewById(R.id.idTranslatedTV);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String sentText = extras.getString("content");
            sourceText.setText(sentText);
        }

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguage[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_translate_item, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);


        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguage[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_translate_item, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);


        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateTV.setVisibility(View.VISIBLE);
                translateTV.setText("");
                if (fromLanguageCode == null){
                    Toast.makeText(TranslateActivity.this, R.string.translate_source, Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode == null){
                    Toast.makeText(TranslateActivity.this, R.string.translate_target, Toast.LENGTH_SHORT).show();
                } else {
                    prepareModel();
                }
            }
        });

        copyBtn.setOnClickListener(v12 -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("translated_note", translateTV.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(TranslateActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

    }

   private void prepareModel() {
       translateTV.setText(R.string.translate_downloading);
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateTV.setText(R.string.translation);
                translateText();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslateActivity.this, R.string.translate_failed_downloading, Toast.LENGTH_SHORT).show();
            }
        });
   }

   private void translateText() {
        translator.translate(sourceText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                translateTV.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslateActivity.this, R.string.translation_failed, Toast.LENGTH_SHORT).show();
            }
        });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }
    }

    private String getLanguageCode(String language) {
        String languageCode;
        switch (language){
            case "English":
                languageCode = TranslateLanguage.ENGLISH;
                break;
            case "Arabic":
                languageCode = TranslateLanguage.ARABIC;
                break;
            case "Belarusian":
                languageCode = TranslateLanguage.BELARUSIAN;
                break;
            case "Bulgarian":
                languageCode = TranslateLanguage.BULGARIAN;
                break;
            case "Czech":
                languageCode = TranslateLanguage.CZECH;
                break;
            case "Russian":
                languageCode = TranslateLanguage.RUSSIAN;
                break;
            case "Hindi":
                languageCode = TranslateLanguage.HINDI;
                break;
            default:
                languageCode = "";
        }
        return languageCode;
    }

}