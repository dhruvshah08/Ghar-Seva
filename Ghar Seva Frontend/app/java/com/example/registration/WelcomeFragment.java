package com.example.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class WelcomeFragment extends Fragment {

    Button btnSignIn, btnSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                                 R.layout.welcome, container, false);

        btnSignIn = (Button) rootView.findViewById(R.id.btnSignIn);
        btnSignUp = (Button) rootView.findViewById(R.id.btnSignUp);

        return rootView;
    }
    public static WelcomeFragment newInstance(String text) {

        WelcomeFragment f = new WelcomeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
