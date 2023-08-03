package com.example.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignInFragment extends Fragment {

    EditText txtEmailID, txtPassword;
    Button btnSignIn, btnForgotPassword, btnSignUpOption;
    SignInButton btnSignInWithGoogle;
    GoogleSignInClient mGoogleSignInClient;
    static ExecutorService executorService;
    boolean entryFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                                 R.layout.sign_in, container, false);

        txtEmailID = (EditText) rootView.findViewById(R.id.txtEmailID);
        txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);
        btnSignIn = (Button) rootView.findViewById(R.id.btnSignIn);
        btnForgotPassword = (Button) rootView.findViewById(R.id.btnForgotPassword);
        btnSignInWithGoogle = (SignInButton) rootView.findViewById(R.id.btnSignInWithGoogle);
        btnSignUpOption = (Button) rootView.findViewById(R.id.btnSignUpOption);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String personalEmail = txtEmailID.getText().toString();
                String password = txtPassword.getText().toString();
                if(validations(personalEmail,password)){
                    if(checkLogIn(personalEmail,password)){
                        Toast.makeText(getContext(),"Welcome back "+personalEmail+"!",Toast.LENGTH_LONG).show();
                        startNextActivity(personalEmail,password);
                    }else{
                        Toast.makeText(getContext(),"Invalid Credentials!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        return rootView;
    }
    public boolean validations(String email,String password){
        if( email.trim().equals("") )
        {
            txtEmailID.setError("Enter E-mail Id");
            txtEmailID.requestFocus();
            return false;
        }
        if( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmailID.setError("Enter valid E-mail Id");
            txtEmailID.requestFocus();
            return false;
        }
        if( password.trim().equals("") )
        {
            txtPassword.setError("Enter Password");
            txtPassword.requestFocus();
            return false;
        }
        if( password.length() < 6 )
        {
            txtPassword.setError("Password requires minimum of 6 characters");
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }
    private boolean checkLogIn(String email,String password) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread thread =new Thread(new Runnable() {
            public void run() {
                entryFound=false;
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new SignInThread(email, password));
                try {
                    String jsonData = futureDataset.get();
                    if(jsonData.contains("true")){
                        entryFound=true;
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return entryFound;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (acct != null) {
                String personlEmail = acct.getEmail();
                String id = acct.getId();
                if(checkLogIn(personlEmail,id)){
                    startNextActivity(personlEmail,personlEmail);
                }
            }
        } catch (ApiException e) {
        }
    }

    private void startNextActivity(String personalEmail,String password){
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("gharseva.txt", Context.MODE_PRIVATE));){
            outputStreamWriter.write("Email : "+personalEmail);
            outputStreamWriter.write("\nPassword : "+password);
        } catch (IOException e) {
        }
        Intent intent = new Intent(getContext(),ServicesOfferedActivity.class);
        intent.putExtra("email",personalEmail);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public static SignInFragment newInstance(String text) {

        SignInFragment f = new SignInFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
