package com.app.viewgroup.asynctask;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private Button mDownloadBtn;
    private TextView mDownloadState, mdownloadProgress;
    private ProgressBar mProgressBar;
    private ImageView mImageView;


    String url = "http://www.abc.net.au/cm/lb/9663056/data/self-portrait-of-a-female-celebes-crested-macaque-data.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadBtn = findViewById(R.id.downloadBtn);
        mDownloadState = findViewById(R.id.state_text);
        mdownloadProgress = findViewById(R.id.download_percents);
        mProgressBar = findViewById(R.id.progressBar);
        mImageView = findViewById(R.id.imageView);


        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
                downloadAsyncTask.execute(url);
            }
        });
    }


    class DownloadAsyncTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setMax(100);
            mProgressBar.setProgress(0);
            mDownloadState.setText(getString(R.string.download_started));


        }


        @Override
        protected String doInBackground(String... params) {

            String path = params[0];
            int fileLength = 0;

            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                fileLength = urlConnection.getContentLength();
                File new_folder = new File("storage/emulated");
                if (!new_folder.exists()) {

                    new_folder.mkdir();
                }
                File input_file = new File(new_folder, "image.jpg");

                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

                byte[] data = new byte[1024];
                int count = 0;
                int total = 0;
                OutputStream outputStream = new FileOutputStream(input_file);
                count = inputStream.read(data);


                while (count != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                    int progress = (int) total * 100 / fileLength;
                    publishProgress(progress);
                }
                inputStream.close();
                outputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
            mdownloadProgress.setText(values[0]);
        }

        @Override
        protected void onPostExecute(String text) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mDownloadState.setText(getString(R.string.download_completed));
            String drawablePath="download/image.jpg";
            mImageView.setImageDrawable(Drawable.createFromPath(drawablePath));
        }
    }
}

