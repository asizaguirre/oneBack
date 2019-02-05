package br.com.oneback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import br.com.oneback.models.Usuario;

import br.com.oneback.commons.CommonActivity;
import br.com.oneback.commons.GoogleSignInActivity;

public class CadastroActivity extends CommonActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Usuario usuario;
    private AutoCompleteTextView name;

    private Button FabCadastrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null || usuario.getId() != null) {
                    return;
                }

                usuario.setId(firebaseUser.getUid());
                usuario.saveDB(CadastroActivity.this);
            }
        };

        inicializarViews();

        FabCadastrar = findViewById(R.id.fab_enviarDados_Cadastro);
        FabCadastrar.setOnClickListener(this);


        findViewById(R.id.googleSignin).setOnClickListener(this);

//        CadastroGoogle = findViewById(R.id.googleSignin);
//        CadastroGoogle.setOnClickListener(this);

    }

    protected void inicializarViews() {
        name = findViewById(R.id.edt_Nome_Cadastro);
        email = findViewById(R.id.edt_Email_Cadastro);
        password = (AutoCompleteTextView) findViewById(R.id.edt_Senha_Cadastro);
        progressBar = findViewById(R.id.sign_up_progress);
    }

    protected void inicializarUsuario() {
        usuario = new Usuario();
        usuario.setName(name.getText().toString());
        usuario.setEmail(email.getText().toString());
        usuario.setPassword(password.getText().toString());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.googleSignin) {


            Intent intent = new Intent(this, GoogleSignInActivity.class);
            startActivity(intent);
            finish();

        } else {

            inicializarUsuario();

            String NOME = name.getText().toString();
            String EMAIL = email.getText().toString();
            String SENHA = password.getText().toString();

            boolean ok = true;

            if (NOME.isEmpty()) {
                name.setError("O campo nome não pode ser vazio");
                ok = false;
            }

            if (EMAIL.isEmpty()) {
                email.setError("O campo email não pode ser vazio");
                ok = false;
            }

            if (SENHA.isEmpty()) {
                password.setError("Por favor, informe uma senha!");
                ok = false;
            }

            if (ok) {
                FabCadastrar.setEnabled(false);
                progressBar.setFocusable(true);

                openProgressBar();
                salvarUsuario();
            } else {
                closeProgressBar();
            }
        }
    }


    private void sendEmailVerification() {


        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(CadastroActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            showToast("Enviamos um e-mail para saber que você é você, confirme teu cadastro e OneBack");

                            mAuth.signOut();
                            closeProgressBar();
                            finish();
                        } else {
                            Log.e("thu", "sendEmailVerification", task.getException());
                            Toast.makeText(CadastroActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });


        // [END send_email_verification]
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void salvarUsuario() {

        mAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    closeProgressBar();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FabCadastrar.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        sendEmailVerification();


        mAuth.signOut();
        closeProgressBar();
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}