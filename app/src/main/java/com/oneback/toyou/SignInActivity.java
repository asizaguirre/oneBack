package com.oneback.toyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.oneback.toyou.models.Post;
import com.oneback.toyou.models.User;


public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        // Views
        mStatusTextView = findViewById(R.id.sigInStatus);
        mDetailTextView = findViewById(R.id.sigInDetail);
        mEmailField = findViewById(R.id.sgnInFieldEmail);
        mPasswordField = findViewById(R.id.signInfieldPassword);

        // Buttons
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.sigInCreateAccount).setOnClickListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {

                            getUserDetails();
//                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {

        Log.d(TAG, "signUp");


        Toast.makeText(SignInActivity.this, "Falta cadastrar o tipo",
                Toast.LENGTH_SHORT).show();

        startActivity(new Intent(SignInActivity.this, ChooserActivity.class));
        finish();
    }

//    private void signUp() {
//        Log.d(TAG, "signUp");
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressDialog();
//        String email = mEmailField.getText().toString();
//        String password = mPasswordField.getText().toString();
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
//                        hideProgressDialog();
//
//                        if (task.isSuccessful()) {
//                            onAuthSuccess(task.getResult().getUser());
//                        } else {
//                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void onAuthSuccess(FirebaseUser user) {
     //   String username = usernameFromEmail(user.getEmail());

        // Write new user
        //writeNewUser(user.getUid(), username, user.getEmail());

//        DatabaseReference userDetais = mDatabase.child("users").child(getUid());
//        getUserDetails(userDetais);


    }

    private void getUserDetails() {
        DatabaseReference userDetais = mDatabase.child("users").child(getUid());
        userDetais.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User p = mutableData.getValue(User.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }

                if(p == null || p.userType == null){
                    p = new User("username","email", "loja", "", "", "","","","","","","","");
                    mDatabase.child("users").child(getUid()).child(getUid()).setValue(p);
//                    Toast.makeText(SignInActivity.this, "Coloquei qualquer coisa nos detalhes",
//                            Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "SALVOU MERDA SO PRA TESTAR");

                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();


                }else{
                    // Go to MainActivity
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        } else if (i == R.id.sigInCreateAccount) {
            signUp();
        }
    }
}
