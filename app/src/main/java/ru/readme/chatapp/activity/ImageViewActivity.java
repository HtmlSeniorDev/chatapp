package ru.readme.chatapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ru.readme.chatapp.R;

public class ImageViewActivity extends AppCompatActivity {

    private ImageViewTouch ivImage;
    private String url;
    private String description;
    private TextView tvDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_prevewer);
        if (savedInstanceState != null && savedInstanceState.containsKey("url")) {
            url = savedInstanceState.getString("url");
        } else if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
        } else {
            url = "";
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("description")) {
            description = savedInstanceState.getString("description");
        } else if (getIntent().hasExtra("description")) {
            description = getIntent().getStringExtra("description");
        } else {
            description = "";
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivImage = (ImageViewTouch) findViewById(R.id.ivt_image_preview_image);
        tvDescription = (TextView) findViewById(R.id.tv_image_previewer_description);
        if (description != null && description.length() > 0) {
            tvDescription.setText(description);
        }
        Picasso.with(this).load(Uri.parse(url)).into(ivImage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
        outState.putString("description", description);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        finishActivity(1);
        super.onDestroy();
    }

}
