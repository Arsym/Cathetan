package com.arsym.cathetan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arsym.cathetan.roomdb.Note;
import com.arsym.cathetan.adapter.NoteAdapter;
import com.arsym.cathetan.NoteViewModel;
import com.arsym.cathetan.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    private TextView emptyView;
    private NoteViewModel noteViewModel;
    private long backPressedTime;
    private Toast backToast;
    private boolean zero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton btnAddNote = findViewById(R.id.btn_addNote);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        emptyView = findViewById(R.id.empty_view);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            private void checkEmpty() {
                emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                zero = adapter.getItemCount() == 0;
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
            ColorDrawable background = new ColorDrawable(Color.RED);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Note note = adapter.getNoteAt(position);

                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
//                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                Snackbar snackbar = Snackbar
                        .make(recyclerView, R.string.snackbar_text, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        noteViewModel.restoreItem(note, position);
                        recyclerView.smoothScrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(getResources().getColor(R.color.orange));
                snackbar.setDuration(4000);
                snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 4;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra(EditorActivity.EXTRA_ID, note.getId());
                intent.putExtra(EditorActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(EditorActivity.EXTRA_NOTE, note.getDescription());
                intent.putExtra("color", note.getColor());

                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EditorActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(EditorActivity.EXTRA_NOTE);
            int color = data.getIntExtra("color", 0);
            Date timeCreate = (Date) data.getSerializableExtra("time");

            Note note = new Note(title, desc, color, timeCreate);
            noteViewModel.insert(note);

            Toast.makeText(this, R.string.added_success, Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(EditorActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, R.string.changed_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(EditorActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(EditorActivity.EXTRA_NOTE);

            int color = data.getIntExtra("color", 0);
            Date timeCreate = (Date) data.getSerializableExtra("time");

            Note note = new Note(title, desc, color, timeCreate);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this, R.string.changed_success, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                if (zero) {
                    Toast.makeText(this, R.string.note_empty, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle(R.string.alert_confirm);
                    alertDialog.setMessage(R.string.alert_delete);
                    alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            noteViewModel.deleteAllNotes();
                            Toast.makeText(MainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.confirm_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }


}
