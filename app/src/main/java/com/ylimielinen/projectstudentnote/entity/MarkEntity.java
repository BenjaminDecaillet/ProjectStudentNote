package com.ylimielinen.projectstudentnote.entity;

import android.support.annotation.NonNull;
import com.google.firebase.database.Exclude;
import com.ylimielinen.projectstudentnote.model.Mark;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by decai on 04.11.2017.
 * Entity of the mark with definition of the db
 */
public class MarkEntity implements Mark{
    @NonNull
    private String uid;
    private String name;
    private Double value;
    private String subject;
    private Double weighting;

    public MarkEntity() {
    }

    public MarkEntity(Mark mark) {
        uid = mark.getUid();
        name = mark.getName();
        value = mark.getValue();
        subject = mark.getSubject();
        weighting = mark.getWeighting();
    }

    @Exclude
    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    @Override
    public String getSubject() { return subject; }

    @Override
    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) { this.weighting = weighting; }

    public void setSubject(String subject) { this.subject = subject; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MarkEntity)) return false;
        MarkEntity o = (MarkEntity) obj;
        return o.getUid().equals(this.getUid());
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> res = new HashMap<>();

        res.put("name", name);
        res.put("value", value);
        res.put("weighting", weighting);
        res.put("subject", subject);

        return res;
    }
}