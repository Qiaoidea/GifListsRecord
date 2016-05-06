package me.qiao.gifcard.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.qiao.gifcard.R;
import me.qiao.gifcard.api.GifDataApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GifDataApi.setupGlide(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,
                        Fragment.instantiate(this,CardListFragment.class.getName()))
                .commit();
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Bundle bundle = new Bundle();
        if (id == R.id.action_decoder) {
            bundle.putInt("type",0);
        }else if(id == R.id.action_movie0){
            bundle.putInt("type",1);
        }else if(id == R.id.action_movie1){
            bundle.putInt("type",2);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.container,
                Fragment.instantiate(this,GifListFragment.class.getName(),bundle))
                .commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().popBackStackImmediate())
            getSupportFragmentManager().popBackStack();
        else{
            super.onBackPressed();
        }
    }
}
