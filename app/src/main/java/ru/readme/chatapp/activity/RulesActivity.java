package ru.readme.chatapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import ru.readme.chatapp.R;
import ru.readme.chatapp.fragment.RulesFragment;

public class  RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.base_layout);
        if (savedInstanceState == null) {
            setupRulesFragment();
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

    private void setupRulesFragment() {
        Bundle args = new Bundle();
        RulesFragment fragment = new RulesFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_base_layout_content, fragment, MainActivity.TAG_RULES)
                .addToBackStack(null)
                .commit();
    }

    public void reload(){
        setupRulesFragment();
    }

}
