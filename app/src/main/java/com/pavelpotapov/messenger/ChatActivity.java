package com.pavelpotapov.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ImageView chatSendMessage;
    private EditText chatMessageText;
    private ImageView chatAddImage;
    private ProgressBar chatProgressBar;
    private ListView chatMessageList;
    private MessageAdapter adapter;
    private String userName;
    private FirebaseDatabase db;
    private DatabaseReference dbRefMessages;
    private ChildEventListener messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseDatabase.getInstance();
        dbRefMessages = db.getReference().child("messages");

        chatSendMessage = findViewById(R.id.chatSendMessage);
        chatMessageText = findViewById(R.id.chatMessageText);
        chatAddImage = findViewById(R.id.chatAddImage);
        chatProgressBar = findViewById(R.id.chatProgressBar);
        chatMessageList = findViewById(R.id.chatMessageList);

        userName = "Name";
        List<Message> messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.message_item, messages);
        chatMessageList.setAdapter(adapter);
        chatProgressBar.setVisibility(ProgressBar.INVISIBLE);

        chatMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    chatSendMessage.setVisibility(View.VISIBLE);
                } else {
                    chatSendMessage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chatMessageText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(500)
        });

        chatSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setText(chatMessageText.getText().toString().trim());
                message.setName(userName);
                message.setImageUrl(null);
                dbRefMessages.push().setValue(message);
                chatMessageText.setText("");
            }
        });

        chatAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                adapter.add(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRefMessages.addChildEventListener(messagesListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, StartActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}