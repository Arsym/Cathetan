package com.arsym.cathetan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arsym.cathetan.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;


public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        overridePendingTransition(R.anim.slidein, R.anim.slideout);

        Button whatsapp = findViewById(R.id.btn_whatsapp);
        Button instagram = findViewById(R.id.btn_instagram);
        Button telegram = findViewById(R.id.btn_telegram);
        Button facebook = findViewById(R.id.btn_facebook);

        SlidrInterface slidr = Slidr.attach(this);
        slidr.unlock();

        ImageButton back = (ImageButton) findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=+6283842736366&text=Hai Ilham, Saya ingin berkenalan";
                Intent wa = new Intent(Intent.ACTION_VIEW);
                wa.setData(Uri.parse(url));
                startActivity(wa);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/ilham.arsyam29");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");
                try {
                    startActivity(insta);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/_u/ilham.arsyam29")));
                }
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent tele = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/arsym29"));
                            tele.setPackage("org.telegram.messenger");
                    startActivity(tele);
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Aplikasi Telegram tidak terpasang", Toast.LENGTH_LONG).show();
                }
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("fb://profile/100002669004189");
                Intent fb = new Intent(Intent.ACTION_VIEW, uri);
                fb.setPackage("com.facebook.katana");
                try {
                    startActivity(fb);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.facebook.com/IlhamArsyam29")));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slideclosein, R.anim.sidecloseout);
    }

}
