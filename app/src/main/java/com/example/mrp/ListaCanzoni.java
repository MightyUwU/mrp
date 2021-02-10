package com.example.mrp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ListaCanzoni extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    public final String myLogTAG = "PERMISSION";
    public final String explain = "This app needs to access files in the storage.";
    static ArrayList<MusicFiles> musicFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, explain);
        setContentView(R.layout.activity_lista_canzoni);


    }

    private void initViewPager()
    {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    public static ArrayList<MusicFiles> getAllAudio(Context context)
    {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection =
                {
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA, //for path
                        MediaStore.Audio.Media.ARTIST
                };
        Cursor cursor = context.getContentResolver().query(uri, projection,null,null,null);
        if(cursor != null)
        {
            Log.e("sono entrato", "ci sono");
            while(cursor.moveToNext())
            {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);

                Log.e("path: "+path, " Album: "+album);

                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

    public void checkPermission(final String myPermission, final int MY_PERMISSIONS_REQUEST, final String explanation) {

        if (ContextCompat.checkSelfPermission(this, myPermission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, myPermission)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(explanation);
                builder.setMessage(explanation);
                builder.setCancelable(true);
                builder.setNeutralButton("Chiudi", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                // Request permission
                                ActivityCompat.requestPermissions(ListaCanzoni.this,
                                        new String[]{myPermission},
                                        MY_PERMISSIONS_REQUEST);

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{myPermission},
                        MY_PERMISSIONS_REQUEST);

            }


        }else{
            Toast myToast = Toast.makeText(this, myPermission + " was already OK", Toast.LENGTH_LONG);
            myToast.show();
            Log.d(myLogTAG, myPermission + " was already OK");
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast myToast = Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG);
                    myToast.show();
                    Log.d(myLogTAG, "Permission granted!");
                    musicFiles = getAllAudio(this);
                    initViewPager();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast myToast = Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG);
                    myToast.show();
                    Log.d(myLogTAG, "Permission denied!");

                }
                return;
            }
        }
    }
}