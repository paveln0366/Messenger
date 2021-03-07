package com.pavelpotapov.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "Start";

    private FirebaseAuth auth;

    private EditText userEmail;
    private EditText userPassword;
    private EditText confirmPassword;
    private EditText userName;
    private Button enterButton;
    private TextView switchTextView;

    private boolean signInMode;

    private FirebaseDatabase db;
    private DatabaseReference dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRefUsers = db.getReference().child("users");

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        userName = findViewById(R.id.userName);
        enterButton = findViewById(R.id.enterButton);
        switchTextView = findViewById(R.id.switchTextView);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter(userEmail.getText().toString().trim(),
                        userPassword.getText().toString().trim());
            }
        });

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, UserListActivity.class));
        }
    }

    private void enter(String email, String password) {
        if (signInMode) {
            if (userEmail.getText().toString().trim().equals("")) {
                Toast.makeText(this, getResources().getString(R.string.enter_your_email), Toast.LENGTH_SHORT).show();
            } else if (userPassword.getText().toString().trim().length() < 7) {
                Toast.makeText(this, getResources().getString(R.string.small_password), Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    //updateUI(user);
                                    Intent intent = new Intent(StartActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", userName.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(StartActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                    // ...
                                }

                                // ...
                            }
                        });
            }
        } else {
            if (!userPassword.getText().toString().trim()
                    .equals(confirmPassword.getText().toString().trim())) {
                Toast.makeText(this, getResources().getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            } else if (userEmail.getText().toString().trim().equals("")) {
                Toast.makeText(this, getResources().getString(R.string.enter_your_email), Toast.LENGTH_SHORT).show();
            } else if (userPassword.getText().toString().trim().length() < 7) {
                Toast.makeText(this, getResources().getString(R.string.small_password), Toast.LENGTH_SHORT).show();
            } else if (userName.getText().toString().trim().equals("")) {
                Toast.makeText(this, getResources().getString(R.string.enter_your_name), Toast.LENGTH_SHORT).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    //updateUI(user);
                                    Intent intent = new Intent(StartActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", userName.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(StartActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(userName.getText().toString().trim());

        dbRefUsers.push().setValue(user);
    }

    public void switchMode(View view) {
        if (signInMode) {
            signInMode = false;
            enterButton.setText(getResources().getString(R.string.sign_up));
            switchTextView.setText(getResources().getString(R.string.sign_in));
            confirmPassword.setVisibility(View.VISIBLE);
            //userName.setVisibility(View.VISIBLE);
        } else {
            signInMode = true;
            enterButton.setText(getResources().getString(R.string.sign_in));
            switchTextView.setText(getResources().getString(R.string.sign_up));
            confirmPassword.setVisibility(View.INVISIBLE);
            //userName.setVisibility(View.INVISIBLE);
        }
    }
}