package com.example.briefact.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.briefact.R;
import com.example.briefact.main.MainActivityJ;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    Intent data;
    EditText editTitle, editContent;
    Button saveNote;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editTitle = findViewById(R.id.editNoteTitle);
        editContent = findViewById(R.id.editNoteText);
        saveNote = findViewById(R.id.editNoteSave);

        data = getIntent();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolBarEditNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = editTitle.getText().toString();
                String newContent = editContent.getText().toString();

                if (newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.registerFailedPasOrEmail, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes")
                            .document(firebaseUser.getUid()).collection("userNotes").document(data.getStringExtra("noteId"));
                    Map<String,Object> note = new HashMap<>();

                    note.put("title", newTitle);
                    note.put("content", newContent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), R.string.note_updated, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(EditNoteActivity.this, MainActivityJ.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.note_updated, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //Toast.makeText(getApplicationContext(), "Saved pressed", Toast.LENGTH_LONG).show();
            }
        });

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        editTitle.setText(noteTitle);
        editContent.setText(noteContent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}