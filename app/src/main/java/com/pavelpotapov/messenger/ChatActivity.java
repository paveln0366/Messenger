package com.pavelpotapov.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final int RC_IMAGE_CHOOSER = 100;

    private ImageView chatSendMessage;
    private EditText chatMessageText;
    private ImageView chatAddImage;
    private ProgressBar chatProgressBar;
    private ListView chatMessageList;

    private MessageAdapter adapter;
    private String userName;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference dbRefMessages;
    private ChildEventListener messagesListener;

    private DatabaseReference dbRefUsers;
    private ChildEventListener usersListener;

    private FirebaseStorage storage;
    private StorageReference storageRefImages;

    private String recipientUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
        } else {
            userName = getString(R.string.no_name);
        }

        db = FirebaseDatabase.getInstance();
        dbRefMessages = db.getReference().child("messages");
        dbRefUsers = db.getReference().child("users");

        storage = FirebaseStorage.getInstance();
        storageRefImages = storage.getReference().child("images");

        chatSendMessage = findViewById(R.id.chatSendMessage);
        chatMessageText = findViewById(R.id.chatMessageText);
        chatAddImage = findViewById(R.id.chatAddImage);
        chatProgressBar = findViewById(R.id.chatProgressBar);
        chatMessageList = findViewById(R.id.chatMessageList);

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
                message.setSender(auth.getCurrentUser().getUid());
                message.setRecipient(recipientUserId);
                message.setImageUrl(null);
                dbRefMessages.push().setValue(message);
                chatMessageText.setText("");
            }
        });

        chatAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_an_image)), RC_IMAGE_CHOOSER);
            }
        });

        usersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
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

        dbRefUsers.addChildEventListener(usersListener);

        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message.getSender().equals(auth.getCurrentUser().getUid()) &&
                        message.getRecipient().equals(recipientUserId) ||
                        message.getRecipient().equals(auth.getCurrentUser().getUid()) &&
                        message.getSender().equals(recipientUserId)) {
                    adapter.add(message);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_CHOOSER && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final StorageReference imageRef = storageRefImages.child(imageUri.getLastPathSegment());

            UploadTask uploadTask = imageRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Message message = new Message();
                        message.setImageUrl(downloadUri.toString());
                        message.setName(userName);
                        message.setSender(auth.getCurrentUser().getUid());
                        message.setRecipient(recipientUserId);
                        dbRefMessages.push().setValue(message);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
}