package com.ylimielinen.projectstudentnote.ui.fragment;

import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.CreateStudent;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.async.student.UpdateStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;

import java.util.concurrent.ExecutionException;

/**
 * Register fragment for a new student who connect to the app
 */
public class RegisterFragment extends Fragment {
    private static final String ARG_PARAM1 = "editMode";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etOldPassword;

    private StudentEntity student = null;
    private boolean editMode;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(boolean editMode) {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, editMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null)
            editMode = getArguments().getBoolean(ARG_PARAM1);

        // Get student if editmode
        if(editMode){
            getActivity().setTitle(R.string.modify_user);
            SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
            String studentEmail = settings.getString(MainActivity.PREFS_USER, null);
            try {
                this.student = new GetStudent(getContext()).execute(studentEmail).get();
            } catch (InterruptedException | ExecutionException e) {
                this.student = null;
            }
        }else {
            getActivity().setTitle(R.string.register_user);
        }
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
        if(editMode) {
            // change button text
            ((Button)getActivity().findViewById(R.id.confirm_register_button)).setText(R.string.save);
            fillForm();
        }
    }

    private void initForm(){
        // Get graphical elements
        etFirstName = getActivity().findViewById(R.id.firstname_register_edittext);
        etLastName = getActivity().findViewById(R.id.lastname_register_edittext);
        etEmail = getActivity().findViewById(R.id.email_register_edittext);
        etOldPassword = getActivity().findViewById(R.id.old_password_register_edittext);
        etPassword = getActivity().findViewById(R.id.password_register_edittext);
        etConfirmPassword = getActivity().findViewById(R.id.confirm_password_register_edittext);

        // if register, hide old password
        // if edit, hide email (as its primary key)
        if(!editMode)
            etOldPassword.setVisibility(View.GONE);
        else
            etEmail.setVisibility(View.GONE);

        Button btRegister = getActivity().findViewById(R.id.confirm_register_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerUser()){
                    if(editMode)
                        updateUserName();
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void fillForm(){
        etEmail.setText(student.getEmail());
        etFirstName.setText(student.getFirstName());
        etLastName.setText(student.getLastName());
    }

    private void updateUserName(){
        TextView userName = getActivity().findViewById(R.id.userName);
        userName.setText(String.format("%s %s", student.getFirstName(), student.getLastName()));
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
        String oldPass = "";
        if(editMode)
            oldPass = etOldPassword.getText().toString();
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
        }

        // Edit user profile
        if(editMode){
            if(oldPass.isEmpty()){
                if(!pass1.isEmpty() || !pass2.isEmpty()){
                    // Old password cannot be empty
                    etOldPassword.setError((getString(R.string.error_field_required)));
                    etOldPassword.requestFocus();
                    return false;
                }else{
                    // Save updated student
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    saveStudent();
                    return true;
                }
            }else{
                if(oldPass.equals(student.getPassword())) {
                    if (checkPassword(pass1, pass2)) {
                        // Save updated student
                        student.setFirstName(firstName);
                        student.setLastName(lastName);
                        student.setPassword(pass1);
                        saveStudent();
                        return true;
                    }
                }else {
                    // Old password cannot be empty
                    etOldPassword.setError((getString(R.string.error_incorrect_password)));
                    etOldPassword.requestFocus();
                    return false;
                }
            }
        }

        // Creation of a new user
        // Check if password matches and if user already exists and create it
        if(checkPassword(pass1, pass2) && !isUserAlreadyRegistered(email)){
            StudentEntity student = new StudentEntity();
            student.setEmail(email);
            student.setPassword(pass1);
            student.setFirstName(firstName);
            student.setLastName(lastName);

            new CreateStudent(getContext()).execute(student);

            return true;
        }else
            return false;
    }

    private boolean isUserAlreadyRegistered(String email){
        try {
            StudentEntity std = new GetStudent(getContext()).execute(email).get();
            if(std != null) {
                etEmail.setError((getString(R.string.error_email_already_exists)));
                etEmail.requestFocus();
                return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.d("REGISTER", "User already exists");
        }

        return false;
    }

    private boolean checkPassword(String pass1, String pass2){
        if(TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)){
            // Passwords cannot be empty
            etPassword.setError((getString(R.string.error_field_required)));
            etPassword.requestFocus();
            return false;
        }

        if(!pass1.equals(pass2)){
            // Check if pass 1 and 2 match
            etPassword.setError(getString(R.string.error_password_mismatch));
            etPassword.requestFocus();
            etPassword.setText("");
            etConfirmPassword.setText("");
            return false;
        }

        return true;
    }

    private void saveStudent(){
        new UpdateStudent(getContext()).execute(student);
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}