package com.arsym.cathetan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.arsym.cathetan.R;
import com.arsym.cathetan.utils.AppUtils;
import com.arsym.cathetan.utils.CopyToClipboardActivity;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;
import com.thebluealliance.spectrum.SpectrumPalette;
import com.thebluealliance.spectrum.internal.SelectedColorChangedEvent;

import java.util.Date;


public class EditorActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.arsym.cathetan.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.arsym.cathetan.EXTRA_TITLE";
    public static final String EXTRA_NOTE = "com.arsym.cathetan.EXTRA_NOTE";

    private EditText et_title;
    private EditText et_note;
    private androidx.appcompat.widget.Toolbar mToolbar;

    Menu actionmenu;
    int color, id;
    boolean edited = false;
    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .scrimStartAlpha(0f)
                .scrimEndAlpha(0f)
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {

                    }

                    @Override
                    public void onSlideChange(float percent) {
//                        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
                    }

                    @Override
                    public void onSlideOpened() {

                    }

                    @Override
                    public void onSlideClosed() {
                        onBackPressed();
                    }
                })
                .build();
        SlidrInterface slidr = Slidr.attach(this, config);
        slidr.unlock();

        et_title = findViewById(R.id.et_title);
        et_note = findViewById(R.id.et_note);

        SpectrumPalette palette = findViewById(R.id.palette);
        palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int clr) {
                color = clr;
            }
        });

        palette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != 0) {
                    edited = true;
                    Log.v("CLICKED", "BISMILLAHIRAHMANIRAHIM");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Intent intent = getIntent();
        id = getIntent().getIntExtra(EXTRA_ID, 0);

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle(R.string.edit_note);
            et_title.setText(intent.getStringExtra(EXTRA_TITLE));
            et_note.setText(intent.getStringExtra(EXTRA_NOTE));
            palette.setSelectedColor(intent.getIntExtra("color", color));
        } else {
            setTitle(R.string.create_note);
            palette.setSelectedColor(getResources().getColor(R.color.white));
        }

        TextWatcher textTrigger = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edited = true;
            }
        };

        et_title.addTextChangedListener(textTrigger);
        et_note.addTextChangedListener(textTrigger);

    }


    private void saveNote() {
        String title = et_title.getText().toString();
        String note = et_note.getText().toString();
        int color = this.color;
        Date timeCreate = AppUtils.getCurrentDateTime();

        if (title.trim().isEmpty() && note.trim().isEmpty()) {
            Toast.makeText(this, R.string.please_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (!edited) {
             Toast.makeText(this, R.string.not_changed, Toast.LENGTH_SHORT).show();
             return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_NOTE, note);
        data.putExtra("color", color);
        data.putExtra("time", timeCreate);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.addnote_menu, menu);
        actionmenu = menu;

        if (id != 0) {
            actionmenu.findItem(R.id.share).setVisible(true);
            actionmenu.findItem(R.id.save_note).setVisible(true);
        } else {
            actionmenu.findItem(R.id.save_note).setVisible(true);
            actionmenu.findItem(R.id.share).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            case R.id.share:
                shareNote();
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareNote() {
        String title = et_title.getText().toString();
        String note = et_note.getText().toString();
        String share = title + "\n\n" + note;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, share);
        intent.setType("text/plain");
//        startActivity(Intent.createChooser(intent, "Bagikan ke"));

        Intent clipboardIntent = new Intent(this, CopyToClipboardActivity.class);
        clipboardIntent.setData(Uri.parse(share));

        Intent chooserIntent = Intent.createChooser(intent, getString(R.string.share_to));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { clipboardIntent });
        startActivity(chooserIntent);

    }

    @Override
    public void onBackPressed() {
        String title = et_title.getText().toString();
        String note = et_note.getText().toString();

        if (id != 0) {
            if (edited) {
                saveNote();
                edited = false;
            } else {
                finish();
            }
        } else {
            if (!title.trim().isEmpty() || !note.trim().isEmpty()) {
                saveNote();
            } else {
                finish();
            }
        }

    }

}
