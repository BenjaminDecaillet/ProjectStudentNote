package com.ylimielinen.projectstudentnote.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.entity.SubjectEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by decai on 12.11.2017.
 * Adapter for the mark display in the home page
 */

public class HomeMarkAdapter extends RecyclerView.Adapter {
    List<MarkEntity> marks;
    Context context;
    public HomeMarkAdapter(List<MarkEntity> marks, Context context) {
        this.marks = marks;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_mark, parent, false);
        return new HomeMarkAdapter.HomeMarkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarkEntity mark = marks.get(position);
        HomeMarkAdapter.HomeMarkViewHolder mvh = (HomeMarkAdapter.HomeMarkViewHolder)holder;

        SubjectEntity subject = null;
        /*try {
            subject = new GetSubject(context).execute(mark.getSubject()).get();
        } catch (InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }*/

        mvh.subjectName.setText(subject.getName().toString());
        mvh.markValue.setText(mark.getValue().toString());
        mvh.markName.setText(mark.getName());
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    public class HomeMarkViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public TextView markName;
        public TextView markValue;

        public HomeMarkViewHolder(View view){
            super(view);
            this.subjectName = (TextView) view.findViewById(R.id.markHomeSubject);
            this.markName = (TextView) view.findViewById(R.id.markHomeName);
            this.markValue = (TextView) view.findViewById(R.id.markHomeValue);
        }
    }
}
