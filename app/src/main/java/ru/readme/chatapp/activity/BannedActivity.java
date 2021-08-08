package ru.readme.chatapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import ru.readme.chatapp.R;
import ru.readme.chatapp.fragment.BannedFragment;

public class  BannedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.base_layout);
        if (savedInstanceState == null) {
            setupBannedFragment();
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
    protected void onDestroy() {
        finishActivity(1);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void setupBannedFragment() {
        Bundle args = new Bundle();
        BannedFragment fragment = new BannedFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_base_layout_content, fragment, MainActivity.TAG_BANNED)
                .addToBackStack(null)
                .commit();
    }

    public void reload(){
        setupBannedFragment();
    }

}
