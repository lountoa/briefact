package com.example.briefact.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.briefact.R;
import com.example.briefact.main.MainActivityJ;
import com.example.briefact.transtate.TranslateActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoNoteActivity extends AppCompatActivity {

    private TextView infoTitle,deleteNote, infoContent;


    Button editNote;
    ImageButton btnCopy, btnShare;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_note);

        infoTitle = findViewById(R.id.infoNoteTitle);
        infoContent = findViewById(R.id.infoNoteText);
        editNote = findViewById(R.id.infoNoteEdit);
        deleteNote = findViewById(R.id.infoNoteDelete);

        Toolbar toolbar = findViewById(R.id.toolBarInfoNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent data = getIntent();

        btnCopy = findViewById(R.id.btn_copy);
        btnShare = findViewById(R.id.btn_share);

        btnCopy.setOnClickListener(v12 -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("translated_note", infoContent.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(InfoNoteActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, infoContent.getText().toString());
            startActivity(Intent.createChooser(intent, null));
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                v.getContext().startActivity(intent);
            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore.collection("notes")
                        .document(firebaseUser.getUid()).collection("userNotes").document(data.getStringExtra("noteId"));
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(), R.string.deleted_successfully, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(v.getContext(), MainActivityJ.class);
                        v.getContext().startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), R.string.not_deleted, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        infoTitle.setText(data.getStringExtra("title"));
        infoContent.setText(data.getStringExtra("content"));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}