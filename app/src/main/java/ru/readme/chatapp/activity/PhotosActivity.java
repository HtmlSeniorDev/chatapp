package ru.readme.chatapp.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import ru.readme.chatapp.R;
import ru.readme.chatapp.fragment.PhotosFragment;
import ru.readme.chatapp.util.SettingsHelper;

public class PhotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.base_layout);
        if (savedInstanceState == null) {
            String id;
            if(getIntent().hasExtra("id")){
                id = getIntent().getStringExtra("id");
            }else{
                id= new SettingsHelper(this).getUserId();
            }
            setupPhotosFragment(id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void setupPhotosFragment(String id) {
        Bundle args = new Bundle();
        args.putString("user", id);
        args.putSerializable("folder", null);
        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_base_layout_content, fragment, MainActivity.TAG_CHAT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        finishActivity(1);
        super.onDestroy();
    }
}
