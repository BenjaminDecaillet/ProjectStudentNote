package com.ylimielinen.projectstudentnote.ui.fragment.subject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetSubjects;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.SubjectAdapter;
import com.ylimielinen.projectstudentnote.ui.fragment.mark.MarksFragment;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubjectsFragment extends Fragment {
    private static final String TAG = "SubjectsFragment";

    private List<SubjectEntity> subjects;
    private RecyclerView recyclerView;

    public SubjectsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subjects));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        // Add button action
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabSubjects);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditSubjectFragment esf = EditSubjectFragment.newInstance(null);

                // Create the fragment and display it
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, esf, "EditSubject")
                        .addToBackStack("EditSubject")
                        .commit();
            }
        });

        // Add subjects list
        recyclerView = (RecyclerView) view.findViewById(R.id.subjectsRecyclerView);
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);

            // Get the logged user to get his subjects
            SharedPreferences settings = getContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
            String loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);
            subjects = new GetSubjects(getContext()).execute(loggedInEmail).get();
            recyclerView.setAdapter(new SubjectAdapter(subjects));

            // On click listener
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    SubjectEntity subject = subjects.get(position);

                    MarksFragment mf = new MarksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("idSubject", subject.getIdSubject());
                    mf.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, mf, "ShowMarks")
                            .addToBackStack("marksStudentLoggedIn")
                            .commit();
                }

                // TODO: Ask to delete the subject
                @Override
                public void onLongClick(View view, int position) {
                    // on long click => open modification view
                    SubjectEntity subject = subjects.get(position);
                    EditSubjectFragment esf = EditSubjectFragment.newInstance(subject);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, esf, "EditSubject")
                            .addToBackStack("EditSubject")
                            .commit();
                }
            }));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subjects));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}
