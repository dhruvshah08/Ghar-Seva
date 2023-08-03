package com.example.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignUpFragment extends Fragment {

    EditText txtEmailID, txtPassword, txtConfirmPassword;
    Button btnSignUp,btnSignInOption;
    SignInButton btnSignUpWithGoogle;
    GoogleSignInClient mGoogleSignInClient;
    static ExecutorService executorService;
    boolean signUpSucessfull;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                                 R.layout.sign_up, container, false);

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        txtEmailID = (EditText) rootView.findViewById(R.id.txtEmailID);
        txtPassword = (EditText) rootView.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) rootView.findViewById(R.id.txtConfirmPassword);
        btnSignUp = (Button) rootView.findViewById(R.id.btnSignUp);
        btnSignUpWithGoogle = (SignInButton) rootView.findViewById(R.id.btnSignUpWithGoogle);
        btnSignInOption = (Button) rootView.findViewById(R.id.btnSignInOption);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String personalEmail = txtEmailID.getText().toString();
                String password = txtPassword.getText().toString();
                String confirmPassword = txtConfirmPassword.getText().toString();
                if(validations(personalEmail,password,confirmPassword)){
                    if(checkLogIn(personalEmail,password)){
                        //CALL FROM HERE
                        Future<String> addUserfutureDataset = executorService.submit(new AddUserThread(personalEmail));
                        String result1 = null;
                        try {
                            result1 = addUserfutureDataset.get();
                            if("200".equals(result1)) {
                                Toast.makeText(getContext(), "Welcome " + personalEmail + "!", Toast.LENGTH_LONG).show();
                                startNextActivity(personalEmail, password);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getContext(),"Account already exists!\nPlease Sign In!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnSignUpWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        return rootView;
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
                String personalEmail = acct.getEmail();
                String id = acct.getId();
                if(checkLogIn(personalEmail,id)){
                    //CALL FROM HERE
                    Future<String> addUserfutureDataset = executorService.submit(new AddUserThread(personalEmail));
                    String result1 = addUserfutureDataset.get();
                    if("200".equals(result1)){
                        Toast.makeText(getContext(),"Welcome "+personalEmail+"!",Toast.LENGTH_LONG).show();
                        startNextActivity(personalEmail,personalEmail);
                    }else{

                    }

                }else{
                    Toast.makeText(getContext(),"Account already exists!\nPlease Sign In!",Toast.LENGTH_LONG).show();
                }
            }
        } catch (ApiException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private boolean checkLogIn(String email,String password) {
        signUpSucessfull=false;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Thread thread = new Thread(new Runnable() {
            public void run() {

                Future<String> futureDataset = executorService.submit(new SignUpThread(email, password));
                try {
                    String jsonData = futureDataset.get();
                    if ("0".equals(jsonData)){
                        signUpSucessfull=true;
                    }else{
                        signUpSucessfull=false;
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
        return signUpSucessfull;

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

    public static SignUpFragment newInstance(String text) {

        SignUpFragment f = new SignUpFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private boolean validations(String email,String password,String confirmPassword){

        if( email.trim().equals("") )
        {
            txtEmailID.setError("Enter E-mail Id");
            txtEmailID.requestFocus();
            return false;
        }
        else if( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            txtEmailID.setError("Enter valid E-mail Id");
            txtEmailID.requestFocus();
            return false;
        }
        else if( password.trim().equals("") )
        {
            txtPassword.setError("Enter Password");
            txtPassword.requestFocus();
            return false;
        }
        else if( password.length() < 6 )
        {
            txtPassword.setError("Password requires minimum of 6 characters");
            txtPassword.requestFocus();
            return false;
        }
        else if( confirmPassword.trim().equals("") )
        {
            txtConfirmPassword.setError("Enter Confirm Password");
            txtConfirmPassword.requestFocus();
            return false;
        }
        else if( !confirmPassword.equals(password) )
        {
            txtConfirmPassword.setError("Password does not match");
            txtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }
}

/*
* Start SpringBoot Server
* Start Neo4j server
* Start Neo4j browser
* */
