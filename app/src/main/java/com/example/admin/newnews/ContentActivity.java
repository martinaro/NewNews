package com.example.admin.newnews;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.admin.newnews.loadMadels.Entry;

public class ContentActivity extends AppCompatActivity {
    TextView textViewTitle, textViewContent, theLinkText;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Slide slide = new Slide();
            slide.setDuration(2000);
            getWindow().setEnterTransition(slide);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpGui();

        Intent data = getIntent();
        if (data.hasExtra(MainActivity.KEY)) {
            String myData = data.getStringExtra(MainActivity.KEY);
            if (myData != null) {
                textViewTitle.setText(myData);
            }
            textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        }
        if (data.hasExtra("key content")) {
            String myData = data.getStringExtra("key content");
            if (myData != null) {
                textViewContent.setText(myData);
            }
        }
        if (data.hasExtra("key link")) {
            final String myData = data.getStringExtra("key link");
            if (myData != null) {
                theLinkText.setText(myData);
            }
            theLinkText.setPaintFlags(theLinkText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            theLinkText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myData));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void setUpGui() {
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        theLinkText = (TextView) findViewById(R.id.theLinkText);
        textViewContent = (TextView) findViewById(R.id.textViewContent);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }

}
