package com.ylimielinen.projectstudentnote.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 * Adapter for the mark display in a subject
 */

public class MarkAdapter extends RecyclerView.Adapter {
    List<MarkEntity> marks;

    public MarkAdapter(List<MarkEntity> marks) {
        this.marks = marks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mark, parent, false);
        return new MarkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarkEntity mark = marks.get(position);
        MarkViewHolder mvh = (MarkViewHolder)holder;
        mvh.markValue.setText(mark.getValue().toString());
        mvh.markName.setText(mark.getName());
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    /*
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_mark,parent, false);
            }

            MarkViewHolder viewHolder = (MarkViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new MarkViewHolder();
                viewHolder.markName = (TextView) convertView.findViewById(R.id.markName);
                viewHolder.markValue = (TextView) convertView.findViewById(R.id.markValue);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<SubjectEntity> subjects
            MarkEntity mark = getItem(position);

            viewHolder.markName.setText(mark.getName());
            viewHolder.markValue.setText(String.valueOf(mark.getValue()));

            return convertView;
        }
    */
    public class MarkViewHolder extends RecyclerView.ViewHolder {
        public TextView markName;
        public TextView markValue;

        public MarkViewHolder(View view){
            super(view);
            this.markName = (TextView) view.findViewById(R.id.markName);
            this.markValue = (TextView) view.findViewById(R.id.markValue);
        }
    }
}