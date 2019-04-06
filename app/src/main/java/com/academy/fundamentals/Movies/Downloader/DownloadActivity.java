package com.academy.fundamentals.Movies.Downloader;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.Movies.Services.ServiceController;
import com.academy.fundamentals.R;

public class DownloadActivity extends AppCompatActivity implements Constants {

    private static final String m_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private BroadcastReceiver m_broadcastReceiver;
    private ImageView m_downloadedPoster;
    private MovieDetails m_movieDetails;
    private String m_url;

    private void startDownloadService()
    {
        Intent downloadService = new Intent(this,DownloadServiceController.DownloadService.class);
        downloadService.putExtra(DOWNLOAD_URL, m_url);
        startService(downloadService);
    }

    private void finishActivity()
    {
        //TODO: FINISH ACTIVITY - GO BACK OR SOMETHING
    }

    private void requestWritePermission()
    {
        ActivityCompat.requestPermissions(this,
                new String[]{m_permission}, PERMISSIONS_REQUEST_CODE);
    }

    private void showExplainingRationaleDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage(PERMISSION_DIALOG_MESSAGE)
                .setTitle(PERMISSION_DIALOG_TITLE)
                .setPositiveButton(
                        PERMISSION_DIALOG_BUTTON_TEXT_GOT_IT,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestWritePermission();
                            }
                        })
                .setNegativeButton(
                        PERMISSION_DIALOG_BUTTON_TEXT_CANCEL,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishActivity();
                            }
                        })
                .create().show();
    }

    private void requestPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, m_permission)) {
            showExplainingRationaleDialog();
        } else {
            requestWritePermission();
        }
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, m_permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showImage(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        m_downloadedPoster.setImageBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        m_downloadedPoster = findViewById(R.id.iv_downloadedPoster);

        m_movieDetails = getIntent().getParcelableExtra(MOVIE_DETAILS_EXTRA);
        m_url = m_movieDetails.getBannerPosterPath();

        //Request permission if needed
        if (!isPermissionGranted()) {
            requestPermission();
        } else {
            startDownloadService();
        }

        m_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String filePath = intent.getStringExtra(MOVIE_POSTER_PATH_EXTRA);
                if (!TextUtils.isEmpty(filePath)) {
                    showImage(filePath);
                }

            }
        };



    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(m_broadcastReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(m_broadcastReceiver);
        super.onStop();
    }

    public static void startActivity(Context context, MovieDetails movie){
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra(MOVIE_DETAILS_EXTRA, movie);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                startDownloadService();
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                finishActivity();
            }
        }
    }





}

