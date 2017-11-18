package com.ylimielinen.projectstudentnote.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.subject.GetMarks;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by decai on 11.11.2017.
 * Adapter for the subject display in the subject list
 */

public class SubjectAdapter extends RecyclerView.Adapter {
    List<SubjectEntity> subjects;
    private Context context;
    public SubjectAdapter(List<SubjectEntity> subjects) {
        this.subjects = subjects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject, parent, false);
        context = parent.getContext();
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        List<MarkEntity> marks = new ArrayList<>();
        SubjectEntity subject = subjects.get(position);
        SubjectViewHolder sbvh = ((SubjectViewHolder)holder);
        try {
            marks = new GetMarks(context).execute(subject.getIdSubject()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        sbvh.subjectName.setText(subject.getName());
        sbvh.subjectDescription.setText(subject.getDescription());

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

        if(moy!=-1) {
            sbvh.subjectAverage.setText(""+moy);
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public TextView subjectDescription;
        public TextView subjectAverage;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            this.subjectName = (TextView) itemView.findViewById(R.id.subjectName);
            this.subjectDescription = (TextView) itemView.findViewById(R.id.subjectDescription);
            this.subjectAverage = (TextView) itemView.findViewById(R.id.subjectAverage);
        }
    }
}