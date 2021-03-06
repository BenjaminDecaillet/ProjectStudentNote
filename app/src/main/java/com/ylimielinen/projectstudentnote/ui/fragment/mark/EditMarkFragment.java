package com.ylimielinen.projectstudentnote.ui.fragment.mark;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.mark.CreateMark;
import com.ylimielinen.projectstudentnote.db.async.mark.DeleteMark;
import com.ylimielinen.projectstudentnote.db.async.mark.GetMark;
import com.ylimielinen.projectstudentnote.db.async.mark.UpdateMark;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;

import java.util.concurrent.ExecutionException;

public class EditMarkFragment extends Fragment {
    private final String TAG = "EditMarkFragment";
    private static final String ARG_PARAM1 = "markId";
    private static final String ARG_PARAM2 = "subjectId";

    private String student;
    private boolean editMode;
    private Long subjectId;

    private MarkEntity mark;
    private EditText etName;
    private EditText etValue;
    private EditText etWeighting;

    public EditMarkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mark mark.
     * @return A new instance of fragment EditMarkFragment.
     */
    public static EditMarkFragment newInstance(MarkEntity mark) {
        EditMarkFragment fragment = new EditMarkFragment();
        Bundle args = new Bundle();
        if(mark != null)
            args.putLong(ARG_PARAM1, mark.getIdMark());
        else
            args.putLong(ARG_PARAM1, -1L);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define there is an option menu
        setHasOptionsMenu(true);

        // Get logged in student
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        student = settings.getString(MainActivity.PREFS_USER, null);

        if(getArguments() != null){
            Long markId = getArguments().getLong(ARG_PARAM1);
            subjectId = getArguments().getLong(ARG_PARAM2);

            if(markId == -1L){
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_mark_create));
                editMode = false;
            }else{
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_mark_edit));
                // Get mark
                try {
                    mark = new GetMark(getContext()).execute(markId).get();
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
        return inflater.inflate(R.layout.fragment_edit_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
        if(editMode)
            fillForm();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(editMode)
            inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteButton:
                performDeleteWithConfirmation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillForm() {
        // Fill the form with existing values
        etName.setText(mark.getName());
        etValue.setText(mark.getValue().toString());
        etWeighting.setText(mark.getWeighting().toString());
    }

    private void initForm() {
        // Get GUI elements for the form
        etName = (EditText) getActivity().findViewById(R.id.markNameEditText);
        etValue = (EditText) getActivity().findViewById(R.id.markValueEditText);
        etWeighting = (EditText) getActivity().findViewById(R.id.markWeightingEditText);
        final Button saveBtn = (Button) getActivity().findViewById(R.id.saveMark);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveChanges(etName.getText().toString(), etValue.getText().toString(), etWeighting.getText().toString())) {
                    ((MainActivity) getActivity()).dismissKeyboard();
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void performDeleteWithConfirmation(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getString(R.string.dialog_delete_mark));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteMark(getContext()).execute(mark);
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

    private boolean saveChanges(String name, String value, String weighting){
        if(name.equals("")) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return false;
        }

        if(value.equals("")) {
            etValue.setError(getString(R.string.error_value_empty));
            etValue.requestFocus();
            return false;
        }

        // Get mark value and weighting
        double markValue = Double.parseDouble(value);
        double markWeighting = (weighting.equals("")) ? 1.0 : Double.parseDouble(weighting);

        if(editMode){
            mark.setName(name);
            mark.setValue(markValue);
            mark.setWeighting(markWeighting);
            new UpdateMark(getContext()).execute(mark);
        }else{
            MarkEntity newMark = new MarkEntity();
            newMark.setValue(markValue);
            newMark.setName(name);
            newMark.setWeighting(markWeighting);
            newMark.setStudent(student);
            newMark.setSubject(subjectId);
            new CreateMark(getContext()).execute(newMark);
        }

        return true;
    }
}