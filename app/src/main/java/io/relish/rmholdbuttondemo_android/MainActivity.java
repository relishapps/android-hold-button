package io.relish.rmholdbuttondemo_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.relish.rmholdbutton.RMHoldButton;

public class MainActivity extends AppCompatActivity {

    RMHoldButton holdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holdButton = (RMHoldButton) findViewById(R.id.holdButton);
        holdButton.setmCallback(new RMHoldButton.RMHoldButtonProgressCallback() {
            @Override
            public void onError(int progress) {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int progress) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Long press has finished!");
                builder.setTitle("Complete!");
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
