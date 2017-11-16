package com.ylimielinen.projectstudentnote.ui.fragment.subject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.subject.CreateSubject;
import com.ylimielinen.projectstudentnote.db.async.subject.DeleteSubject;
import com.ylimielinen.projectstudentnote.db.async.subject.GetSubject;
import com.ylimielinen.projectstudentnote.db.async.subject.UpdateSubject;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;

import java.util.concurrent.ExecutionException;

public class EditSubjectFragment extends Fragment {
    private final String TAG = "EditMarkFragment";
    private static final String ARG_PARAM1 = "subjectId";

    private String student;
    private boolean editMode;
    private Long subjectId;

    private SubjectEntity subject;
    private EditText etName;
    private EditText etDescription;
    private Button deleteBtn;

    public EditSubjectFragment() {
    }

    public static EditSubjectFragment newInstance(SubjectEntity subject) {
        EditSubjectFragment fragment = new EditSubjectFragment();
        Bundle args = new Bundle();
        if(subject != null)
            args.putLong(ARG_PARAM1, subject.getIdSubject());
        else
            args.putLong(ARG_PARAM1, -1L);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get logged in student
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        student = settings.getString(MainActivity.PREFS_USER, null);

        if(getArguments() != null){
            subjectId = getArguments().getLong(ARG_PARAM1);

            if(subjectId == -1L){
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subject_create));
                editMode = false;
            }else{
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subject_edit));
                // Get subject
                try {
                    subject = new GetSubject(getContext()).execute(subjectId).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                editMode = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_subject, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
        if(editMode)
            fillForm();
    }

    private void fillForm() {
        // Fill the form with existing values
        etName.setText(subject.getName());
        etDescription.setText(subject.getDescription());
    }

    private void initForm() {
        // Get GUI elements for the form
        etName = (EditText) getActivity().findViewById(R.id.subjectNameEditText);
        etDescription = (EditText) getActivity().findViewById(R.id.subjectDescriptionEditText);
        final Button saveBtn = (Button) getActivity().findViewById(R.id.saveSubject);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveChanges(etName.getText().toString(), etDescription.getText().toString())) {
                    ((MainActivity) getActivity()).dismissKeyboard();
                    getActivity().onBackPressed();
                }
            }
        });

        deleteBtn = (Button) getActivity().findViewById(R.id.deleteSubject);
        if(!editMode)
            deleteBtn.setVisibility(View.GONE);
        else{
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(getString(R.string.delete));
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(getString(R.string.dialog_delete_subject));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteSubject(getContext()).execute(subject);
                            getActivity().onBackPressed();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }
            });
        }

    }

    private boolean saveChanges(String name, String description){
        if(description.equals("")) {
            etDescription.setError(getString(R.string.error_value_empty));
            etDescription.requestFocus();
            return false;
        }

        if(name.equals("")) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return false;
        }

        if(editMode){
            subject.setName(name);
            subject.setDescription(description);
            new UpdateSubject(getContext()).execute(subject);
        }else{
            SubjectEntity newSubject = new SubjectEntity();
            newSubject.setName(name);
            newSubject.setDescription(description);
            newSubject.setStudent(student);
            new CreateSubject(getContext()).execute(newSubject);
        }

        return true;
    }
}