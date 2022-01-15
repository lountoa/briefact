package com.example.briefact.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.briefact.R;
public class InfoNoteActivity extends AppCompatActivity {

    private TextView infoTitle, infoContent;
    Button editNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_note);

        infoTitle = findViewById(R.id.infoNoteTitle);
        infoContent = findViewById(R.id.infoNoteText);
        editNote = findViewById(R.id.infoNoteEdit);

        Toolbar toolbar = findViewById(R.id.toolBarInfoNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

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