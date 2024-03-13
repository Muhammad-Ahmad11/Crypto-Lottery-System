package com.fyp.cls.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.ProgressBar;

import com.fyp.cls.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadDataTask {
    private ExecutorService executorService;
    private Future<?> futureTask;
    private Dialog progressDialog;

    public void startTask(Context context) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog_layout);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
        // Set additional properties for the progress bar, if needed

        progressDialog.show();

        executorService = Executors.newSingleThreadExecutor();
        futureTask = executorService.submit(() -> {
            // Perform background task here
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // You can optionally use a Handler to update the UI on the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // Update UI here (e.g., show progress dialog)
        });
    }

    public void cancelTask() {
        if (futureTask != null && !futureTask.isDone()) {
            futureTask.cancel(true);
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
