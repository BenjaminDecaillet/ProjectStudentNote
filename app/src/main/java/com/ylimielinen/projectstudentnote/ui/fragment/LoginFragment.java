package com.ylimielinen.projectstudentnote.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.util.Utils;

import java.util.concurrent.ExecutionException;

public class LoginFragment extends Fragment {
    private final String BACK_STACK_ROOT_TAG = "LOGIN_FRAGMENT";
    private static final String TAG = "LoginFragment";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private StudentEntity student = null;
    private FragmentManager fragmentManager;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // get the field to be filled by the User
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);

        Button btEmailSignIn = view.findViewById(R.id.email_sign_in_button);
        btEmailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
             }
        });

        Button btRegister = view.findViewById(R.id.register_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = RegisterFragment.newInstance();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContentLogin, fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void attemptLogin(){
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store the values of the field when the button Sign In is pressed
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            try {
                student = new GetStudent(getContext()).execute(email).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Student not found" + e.getMessage(), e);
            }
            if (student != null) {
                if (student.getPassword().equals(password)) {
                    // We need an Editor object to make preference changes.
                    // All objects are from android.context.Context
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
                    editor.putString(MainActivity.PREFS_USER, student.getEmail());
                    editor.apply();

                    // Open the main activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    mEmailView.setText("");
                    mPasswordView.setText("");
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    mPasswordView.setText("");
                }
            } else {
                mEmailView.setError(getString(R.string.error_invalid_email));
                mEmailView.requestFocus();
                mPasswordView.setText("");
            }
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}