package com.ylimielinen.projectstudentnote.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.CreateStudent;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

import java.util.concurrent.ExecutionException;

/**
 * Register fragment for a new student who connect to the app
 */
public class RegisterFragment extends Fragment {
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
    }

    private void initForm(){
        // Get graphical elements
        etFirstName = getActivity().findViewById(R.id.firstname_register_edittext);
        etLastName = getActivity().findViewById(R.id.lastname_register_edittext);
        etEmail = getActivity().findViewById(R.id.email_register_edittext);
        etPassword = getActivity().findViewById(R.id.password_register_edittext);
        etConfirmPassword = getActivity().findViewById(R.id.confirm_password_register_edittext);

        Button btRegister = getActivity().findViewById(R.id.confirm_register_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerUser()){
                    Log.i("Register", "done");
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private boolean registerUser(){
        // Reset errors
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        // Store values
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String pass1 = etPassword.getText().toString();
        String pass2 = etConfirmPassword.getText().toString();

        // Check for errors
        if(TextUtils.isEmpty(firstName)) {
            // First name cannot be empty
            etFirstName.setError((getString(R.string.error_field_required)));
            etFirstName.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(lastName)) {
            // Last name cannot be empty
            etLastName.setError((getString(R.string.error_field_required)));
            etLastName.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(email)){
            // Email cannot be empty
            etEmail.setError((getString(R.string.error_field_required)));
            etEmail.requestFocus();
            return false;
        }else if(!isEmailValid(email)){
            // email must be valid
            etEmail.setError((getString(R.string.error_field_required)));
            etEmail.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
            // Passwords cannot be empty
            etPassword.setError((getString(R.string.error_field_required)));
            etPassword.requestFocus();
            return false;
        }else if(!pass1.equals(pass2)){
            // Check if pass 1 and 2 match
            etPassword.setError(getString(R.string.error_password_mismatch));
            etPassword.requestFocus();
            etPassword.setText("");
            etConfirmPassword.setText("");
            return false;
        }

        try {
            StudentEntity std = new GetStudent(getContext()).execute(email).get();
            if(std != null) {
                etEmail.setError((getString(R.string.error_email_already_exists)));
                etEmail.requestFocus();
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.d("REGISTER", "User already exists");
        }

        StudentEntity student = new StudentEntity();
        student.setEmail(email);
        student.setPassword(pass1);
        student.setFirstName(firstName);
        student.setLastName(lastName);

        new CreateStudent(getContext()).execute(student);

        return true;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}