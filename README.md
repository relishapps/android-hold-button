# RMHoldButton

## Installation

How to install RMHoldButton for Android

1) Download RMHoldButton
2) In your Android Studio project, go to File -> New -> Import Module
3) Navigate to the RMHoldButton directory you downloaded, and click OK
4) In your app Gradle settings, add `compile project(':rmholdbutton’)`
5) In your layout xml add `<io.relish.rmholdbutton.RMHoldButton>`
6) Configure it using it’s custom properties;
```app:backgroundColor="#fff"
app:text="Hold me!"
app:animationDuration="2000"
app:cornerRadius="10”```
7) In you activity’s .java file, give the button callback method;
```        holdButton = (RMHoldButton) findViewById(R.id.holdButton);
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
        });```

For a full demo on how to use it, check out the Demo project.