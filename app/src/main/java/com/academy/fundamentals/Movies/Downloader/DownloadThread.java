package com.academy.fundamentals.Movies.Downloader;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DownloadThread extends Thread implements Constants {

    public interface DownloadCallBack {
        void onProgressUpdate(int percent);
        void onDownloadFinished(String filePath);
        void onError(String error);
    }

    private         URL                 m_imageUrl;
    private final   String              m_imagePath;
    private final   DownloadCallBack    m_downloadCallBack;
    private         HttpURLConnection   m_connection;
    private         InputStream         m_inputStream;
    private         FileOutputStream    m_fileOutputStream;
    private         int                 m_progress;
    private         long                m_lastUpdateTime;

    public DownloadThread(String path, DownloadCallBack downloadCallBack) {
        m_imagePath         = path;
        m_downloadCallBack  = downloadCallBack;
        m_lastUpdateTime    = 0;
        m_progress          = 0;
    }

    private void updateProgress(int fileLength) throws IOException {
        if (m_lastUpdateTime == 0 || System.currentTimeMillis() > m_lastUpdateTime + 5) {
            int count = ((int) m_fileOutputStream.getChannel().size()) * 100 / fileLength;
            if (count > m_progress) {
                m_progress = count;
                m_lastUpdateTime = System.currentTimeMillis();
                m_downloadCallBack.onProgressUpdate(m_progress);
            }
        }
    }

    private void saveFile(File file, int fileLength) throws IOException
    {
        // Output stream (Saving file)
        m_fileOutputStream = new FileOutputStream(file.getPath());

        int next;
        byte[] data = new byte[1024];
        while ((next = m_inputStream.read(data)) != -1) {
            m_fileOutputStream.write(data, 0, next);
            updateProgress(fileLength);
        }
    }

    private int getFileSizeFromServer() throws IOException
    {
        m_imageUrl = new URL(m_imagePath);
        m_connection = (HttpURLConnection) m_imageUrl.openConnection();
        m_connection.connect();

        if (m_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            m_downloadCallBack.onError("Server returned HTTP response code: "
                    + m_connection.getResponseCode() + " - " + m_connection.getResponseMessage());
        }

        return m_connection.getContentLength();
    }

    private int DownloadFile() throws IOException
    {
        int fileLength = getFileSizeFromServer();

        // Input stream (Downloading file)
        m_inputStream = new BufferedInputStream(m_imageUrl.openStream(), 8192);

        return fileLength;
    }

    @NonNull
    private String createImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "FILE_" + timeStamp;
    }

    private File createFile() {
        File mediaStorageDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator);
        // Create the storage directory if it does not exist
        if (!mediaStorageDirectory.exists()) {
            if (!mediaStorageDirectory.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String imageName = createImageFileName() + ".jpg";
        return new File(mediaStorageDirectory.getPath() + File.separator + imageName);
    }

    @Override
    public void run() {
        int fileLength;
        File file = createFile();
        if (file == null) {
            m_downloadCallBack.onError(CREATE_FILE_FAILED);
            return;
        }

        try {
            fileLength = DownloadFile();
        } catch (IOException e) {
            m_downloadCallBack.onError(DOWNLOAD_FILE_FAILED);
            return;
        }

        try {
            saveFile(file, fileLength);
        } catch (IOException e) {
            m_downloadCallBack.onError(SAVE_FILE_FAILED);
            return;
        }

        m_downloadCallBack.onDownloadFinished(file.getPath());
    }


}
