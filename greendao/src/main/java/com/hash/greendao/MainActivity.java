package com.hash.greendao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hash.greendao.adapter.NotesAdapter;
import com.hash.greendao.db.DaoSession;
import com.hash.greendao.db.NoteDao;
import com.hash.greendao.model.Note;
import com.hash.greendao.model.NoteType;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView;
    private RecyclerView recyclerView;
    private Button addNoteButton;
    private NotesAdapter notesAdapter;
    private RxDao<Note, Long> noteDao;
    private RxQuery<Note> notesQuery;
    private String TAG =this.getClass().getName();


    private String[] personNameArr ={"Jame","Hash","GitHub","Stack","Thread"};
    private String[] personIdArr ={"299931","124414","122441","124413","124141"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        Log.e(TAG,"onCreate");
        // get the Rx variant of the note DAO
        DaoSession daoSession = GreenDaoApp.getInstance().getDaoSession();
        noteDao = daoSession.getNoteDao().rx();

        // query all notes, sorted a-z by their text
        notesQuery = daoSession.getNoteDao().queryBuilder().orderAsc(NoteDao.Properties.Text).rx();
        updateNotes();


    }

    private void updateNotes() {
        notesQuery.list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Note>>() {
                    @Override
                    public void call(List<Note> notes) {
                        notesAdapter.setNotes(notes);
                    }
                });
    }

    private void initUi() {
        editText =findViewById(R.id.editTextNote);
        textView =findViewById(R.id.textViewNoteInstructions);
        recyclerView=findViewById(R.id.recyclerViewNotes);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notesAdapter = new NotesAdapter(noteClickListener);
        recyclerView.setAdapter(notesAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);

        editText = (EditText) findViewById(R.id.editTextNote);
        //noinspection ConstantConditions
        RxTextView.editorActions(editText).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer actionId) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            addNote();
                        }
                    }
                });
        RxTextView.afterTextChangeEvents(editText).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        boolean enable = textViewAfterTextChangeEvent.editable().length() > 0;
                        addNoteButton.setEnabled(enable);
                    }
                });
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());


        Note note = new Note(null, personIdArr[new Random().nextInt(personIdArr.length-1)],noteText, comment,
                new Date(),personNameArr[new Random().nextInt(personNameArr.length-1)], NoteType.TEXT);
        noteDao.insert(note)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Note>() {
                    @Override
                    public void call(Note note) {
                        Log.e(TAG, "Inserted new note, ID: " + note.getId());
                        updateNotes();
                    }
                });
    }


    public void onAddButtonClick(View view)
    {
        addNote();


    }

    NotesAdapter.NoteClickListener noteClickListener = new NotesAdapter.NoteClickListener() {
        @Override
        public void onNoteClick(int position) {
            Note note = notesAdapter.getNote(position);
            final Long noteId = note.getId();
            noteDao.deleteByKey(noteId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Log.e(TAG, "Deleted note, ID: " + noteId);
                            updateNotes();
                        }
                    });
        }
    };
}
