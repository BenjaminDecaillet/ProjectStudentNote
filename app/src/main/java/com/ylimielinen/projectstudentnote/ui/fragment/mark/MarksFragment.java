package com.ylimielinen.projectstudentnote.ui.fragment.mark;

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
import android.widget.TextView;

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
    private TextView tvMoy;

    public MarksFragment() {}

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
                EditMarkFragment emf = EditMarkFragment.newInstance(null);
                // Set subject id
                Bundle b = emf.getArguments();
                b.putLong("subjectId", subject);

                // Create the fragment and display it
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
            public void onClick(View view, int position) {}

            @Override
            public void onLongClick(View view, int position) {
                // on long click => open modification view
                MarkEntity mark = marks.get(position);
                EditMarkFragment emf = EditMarkFragment.newInstance(mark);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, emf, "EditMark")
                        .addToBackStack("EditMark")
                        .commit();
            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_marks));
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double moy=-1;
        double sum =0;
        double weight=0;
        if(marks.size()>0) {
            for (MarkEntity m : marks) {
                sum += m.getValue()*m.getWeighting();
                weight += m.getWeighting();
            }
            if(weight!=0){
                moy = sum / weight;
            }

        }
        tvMoy = (TextView) getActivity().findViewById(R.id.moySubj);
        if(moy==-1) {
            tvMoy.setText(getString(R.string.average)+" : -");
        } else
        {
            tvMoy.setText(getString(R.string.average)+" : " + moy);
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}