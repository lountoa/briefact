package com.example.briefact.main;

import static android.view.View.GONE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.briefact.R;
import com.example.briefact.transtate.TranslateActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BottomSheetResultsFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_TEXT = "arg_text";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    TextView resultantText;
    TextView resultantTitle;
    ImageButton btnCopy;
    ImageButton btnShare;
    CardView btnSave;
    CardView btnTranslate;
    CardView prgSheet;


    private Context context;
    private Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Сейчас в", "BottomSheetResults");

        View v = inflater.inflate(R.layout.bottom_sheet_dialog_results, container, false);

        context = getContext();
        bundle = getArguments();

        assert bundle != null;
        assert context != null;

        resultantText = v.findViewById(R.id.resultant_text);
        resultantTitle = v.findViewById(R.id.resultant_title);

        btnCopy = v.findViewById(R.id.btn_copy);
        btnShare = v.findViewById(R.id.btn_share);

        btnSave = v.findViewById(R.id.resultant_save);
        btnTranslate = v.findViewById(R.id.resultant_translate);
        prgSheet = v.findViewById(R.id.progressbar_bottom_sheet);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = resultantTitle.getText().toString();
                String content = resultantText.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(context, R.string.noTitle, Toast.LENGTH_LONG).show();
                } else {
                    prgSheet.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);
                    note.put("search", title.toLowerCase());

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, R.string.noteCreated, Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, R.string.noteNotCreated, Toast.LENGTH_LONG).show();
                            prgSheet.setVisibility(GONE);
                        }
                    });
                }
            }
        });
        
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = resultantText.getText().toString();
                Intent intent = new Intent(getActivity(), TranslateActivity.class);
                intent.putExtra("content",content);
                startActivity(intent);
            }
        });

        btnCopy.setOnClickListener(v12 -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("nonsense_data", bundle.getString(ARGUMENT_TEXT));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnShare.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, bundle.getString(ARGUMENT_TEXT));
            startActivity(Intent.createChooser(intent, null));
            dismiss();
        });

        if (bundle.getString(ARGUMENT_TEXT).trim().isEmpty()) {
            resultantText.setText(R.string.no_results);
            resultantTitle.setVisibility(GONE);
            btnSave.setVisibility(GONE);
            btnTranslate.setVisibility(GONE);
            btnCopy.setVisibility(GONE);
            btnShare.setVisibility(GONE);
            resultantText.setEnabled(false);
        } else {
            resultantText.setText(bundle.getString(ARGUMENT_TEXT));
        }
        return v;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        cancel();
    }

    @Override
    public void dismiss() {
        cancel();
        super.dismiss();
    }

    private void cancel() {

    }

    public static BottomSheetResultsFragment newInstance(String text) {
        BottomSheetResultsFragment fragment = new BottomSheetResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

}