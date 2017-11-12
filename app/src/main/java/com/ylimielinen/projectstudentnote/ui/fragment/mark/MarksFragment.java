package com.ylimielinen.projectstudentnote.ui.fragment.mark;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.mark.GetMarksOfStudentBySubject;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.MarkAdapter;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MarksFragment extends Fragment {
    private static final String TAG = "MarksFragment";

    private List<MarkEntity> marks;
    private RecyclerView recyclerView;
    private long subject;

    public MarksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_marks));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            subject = bundle.getLong("idSubject", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marks, container, false);

        // Add button action
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabMarks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add a new mark
                Snackbar.make(view, "Add a new mark", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                EditMarkFragment emf = EditMarkFragment.newInstance(null);
                Bundle b = emf.getArguments();
                b.putLong("subjectId", subject);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, emf, "EditMark")
                        .addToBackStack("EditMark")
                        .commit();
            }
        });

        // Add marks list
        recyclerView = (RecyclerView) view.findViewById(R.id.marksRecyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Get the logged user to get his subjects
        SharedPreferences settings = getContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);
        try {
            marks = new GetMarksOfStudentBySubject().execute(getContext(), subject, loggedInEmail).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            marks = new ArrayList<>();
        }


        recyclerView.setAdapter(new MarkAdapter(marks));

        // On click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MarkEntity mark = marks.get(position);
                Toast.makeText(getContext(), "Click on " + mark.getName(), Toast.LENGTH_SHORT).show();
                EditMarkFragment emf = EditMarkFragment.newInstance(mark);
                Bundle b = emf.getArguments();
                b.putLong("subjectId", mark.getSubject());

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, emf, "EditMark")
                        .addToBackStack("EditMark")
                        .commit();
            }

            // TODO: Ask to delete the subject
            @Override
            public void onLongClick(View view, int position) {
                MarkEntity mark = marks.get(position);
                Toast.makeText(getContext(), "Long click on " + mark.getName(), Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_marks));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

}
