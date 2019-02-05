package br.com.oneback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import br.com.oneback.models.Usuario;

import br.com.oneback.commons.CommonActivity;

public class LoginActivity extends CommonActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Usuario usuario;

    private TextView cadastrar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();

        inicializarViews();

        btnLogin = findViewById(R.id.btn_Login);
        btnLogin.setOnClickListener(this);
    }

    protected void inicializarViews() {
        email = findViewById(R.id.edt_Email_Login);
        password = findViewById(R.id.edt_Senha_Login);
        progressBar = findViewById(R.id.login_progress);
        cadastrar = findViewById(R.id.txt_Cadastrar);
    }

    protected void inicializarUsuario() {
        usuario = new Usuario();
        usuario.setEmail(email.getText().toString());
        usuario.setPassword(password.getText().toString());
    }

    @Override
    public void onClick(View v) {

        inicializarUsuario();

        int id = v.getId();
        if (id == R.id.btn_Login) {

            String EMAIL = email.getText().toString();
            String SENHA = password.getText().toString();

            boolean ok = true;

            if (!EMAIL.isEmpty()) {
            } else {
                email.setError("E-mail não informado!");

                ok = false;
            }

            if (SENHA.isEmpty()) {
                password.setError("Por favor digite uma senha!");

                ok = false;
            }

            if (ok) {
                btnLogin.setEnabled(false);
                cadastrar.setEnabled(false);
                progressBar.setFocusable(true);

                openProgressBar();
                verifyLogin();
            } else {
                closeProgressBar();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    private void verifyLogged() {

        if (firebaseAuth.getCurrentUser() != null) {
            chamarMainActivity();
        } else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    return;
                }

                if (usuario.getId() == null && isNameOk(usuario, userFirebase)) {

                    usuario.setId(userFirebase.getUid());
                    usuario.setNameIfNull(userFirebase.getDisplayName());
                    usuario.setEmailIfNull(userFirebase.getEmail());
                    usuario.saveDB();
                }

                if (userFirebase.isEmailVerified()) {
                    chamarMainActivity();
                } else {
                    showToast("Teu email ainda não está confirmado, confirma no teu e-mail por favor!");
                }


            }
        };
        return (callback);
    }

    private void verifyLogin() {
        firebaseAuth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressBar();

                            btnLogin.setEnabled(true);
                            cadastrar.setEnabled(true);

                            return;
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
    }

    private boolean isNameOk(Usuario usuario, FirebaseUser firebaseUser) {
        return (usuario.getName() != null || firebaseUser.getDisplayName() != null);
    }

    private void chamarMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void chamarCadastro(View view) {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }
}