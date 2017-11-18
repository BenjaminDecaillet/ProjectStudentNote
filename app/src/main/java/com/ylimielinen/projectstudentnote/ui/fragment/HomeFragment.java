package com.ylimielinen.projectstudentnote.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetAllMarksOfStudent;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.HomeMarkAdapter;
import com.ylimielinen.projectstudentnote.ui.fragment.mark.EditMarkFragment;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Home fragment to display all notes of the student logged in
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "SubjectsFragment";

    private List<MarkEntity> marks;
    private RecyclerView recyclerView;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_main));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Add marks list
        recyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);

            // Get the logged user to get his subjects
            SharedPreferences settings = getContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
            String loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);
            marks = new GetAllMarksOfStudent(getContext()).execute(loggedInEmail).get();

            recyclerView.setAdapter(new HomeMarkAdapter(marks, getContext()));

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
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_main));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

}
