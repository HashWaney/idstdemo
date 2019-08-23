package com.hash.greendao.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import java.util.Date;


/**
 * Created by HashWaney on 2019/8/23.
 */
@Entity(indexes = {@Index(value = "text, date DESC", unique = true)})
public class Note {
    @Id
    private Long id;
    private String text;
    private String comment;
    private java.util.Date date;
    @Convert(converter = NoteTypeConvert.class, columnType = String.class)
    private NoteType type;

    public Note(Long id) {
        this.id = id;
    }

    @Generated(hash = 600890876)
    public Note(Long id, String text, String comment, java.util.Date date,
            NoteType type) {
        this.id = id;
        this.text = text;
        this.comment = comment;
        this.date = date;
        this.type = type;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getDate() {
        return this.date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public NoteType getType() {
        return this.type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }
}
