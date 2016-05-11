package me.qiao.gifcard.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
    }

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x01;
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }else{
            addFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                addFragment();
            }
        }
    }

    private void addFragment(){
        Bundle bundle = new Bundle();
//        bundle.putInt("type",1);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,
                        Fragment.instantiate(this,CardListFragment.class.getName(),bundle))
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
        }else if(id == R.id.action_decoder0){
            bundle.putInt("type",3);
        }else if(id == R.id.action_decoder1){
            bundle.putInt("type",4);
        }else if(id == R.id.action_decoder2){
            bundle.putInt("type",5);
        }else if(id == R.id.action_animdrawable_decoder){
            bundle.putInt("type",6);
        }else if(id == R.id.action_glide) {
            bundle.putInt("type", -1);
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
