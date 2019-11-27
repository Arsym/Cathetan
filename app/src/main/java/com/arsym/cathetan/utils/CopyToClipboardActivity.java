package com.arsym.cathetan.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.arsym.cathetan.R;

public class CopyToClipboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            copyTextToClipboard(uri.toString());
            Toast.makeText(this, R.string.toast_copy, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Pesan",text);
        clipboard.setPrimaryClip(clip);
    }
}
