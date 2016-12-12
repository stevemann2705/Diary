package com.steve.diary.diary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed() {


            if (doubleBackToExitPressedOnce) {
                //super.onBackPressed();
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);


    }
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    protected File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    protected String fileDir2 = "Diary";
    private String setupTemp = "setupTemp.rtf";
    private File settings = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"settings.data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_bar_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(!(settings.exists())){
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();

        }
        if(!(new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"AES").exists())) {
            try {
                FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "Settings.data");
                FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp1.rtf");
                Encyption.preDecrypt("SteveMann", fis1, fos1);
                fis1.close();
                fos1.close();
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp1.rtf");
                FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp2.rtf");
                Encyption.preDecrypt("SteveMann", fis1, fos1);
                fis1.close();
                fos1.close();
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp2.rtf");
                FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp);
                Encyption.preDecrypt("SteveMann", fis1, fos1);
                fis1.close();
                fos1.close();
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            }
            new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp2.rtf").delete();
            new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "setupTemp1.rtf").delete();
            new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "Settings.data").delete();

            try {

                FileInputStream fis = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp);
                FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "Settings.data");
                Encyption.encrypt("SteveMann", fis, fos);
                fis.close();
                fos.close();

            } catch (Throwable e) {

            }
            try {
                new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"AES").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp).delete();
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }





    /**
     * Callback received when a permissions request has been completed.
     */




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }

        // Reset errors.
        //mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        //String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        /*if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(password);
            mAuthTask.execute();
            Toast.makeText(getApplicationContext(), "ALL Encrypted Successfully", Toast.LENGTH_SHORT)
                    .show();
            mAuthTask.de();
        }
    }



    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }






    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private final String mPassword;
        private String passGot = null;
        public String keyGot = null;

        public String getKeyGot() {
            return keyGot;
        }

        protected File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        protected String fileDir2 = "Diary";
        private String setupTemp = "setupTemp.rtf";
        private File settings = new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "settings.data");
        Calendar instance = Calendar.getInstance();
        Date date = instance.getTime();
        int year1 = date.getYear() - 100;
        int dat1 = date.getDate();
        int month1 = date.getMonth() + 1;
        private String dfilename = "test.rtf";
        private String efilename = dat1 + "-" + month1 + "-" + year1 + ".diary";

        UserLoginTask(String password) {

            mPassword = password;

            try {
                FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "Settings.data");
                FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp);
                Encyption.decrypt("SteveMann", fis1, fos1);
                fis1.close();
                fos1.close();
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
            }


            try {
                FileReader fr = new FileReader(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp);
                BufferedReader br = new BufferedReader(fr);
                //Scanner scan = new Scanner(new FileReader(path+"\\"+"settingsTemp.txt"));
                try {
                    passGot = br.readLine();

                    keyGot = br.readLine();


                } catch (IOException ex) {
                    //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
                }

                br.close();
                fr.close();

            } catch (FileNotFoundException ex) {

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("PASS", passGot);
            Log.d("KEY", keyGot);
            new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + setupTemp).delete();


        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // Simulate network access.
            //Thread.sleep(2000);

            for (; month1 > 5; ) {
                if (!(new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "LOG").exists())) {
                    String inputfilename = dat1 + "-" + month1 + "-" + year1 + ".diary";
                    try {
                        FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + inputfilename);
                        FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "1.txt");
                        Encyption.preDecrypt(keyGot, fis1, fos1);
                        fis1.close();
                        fos1.close();
                        //labelStatus.setText("Opened Successfully.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (FileNotFoundException e) {
                        //labelStatus.setText("Error: File Not Found");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (Throwable e) {
                        //labelStatus.setText("Read Error.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    }
                    try {
                        FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "1.txt");
                        FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "2.txt");
                        Encyption.preDecrypt(keyGot, fis1, fos1);
                        fis1.close();
                        fos1.close();
                        //labelStatus.setText("Opened Successfully.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (FileNotFoundException e) {
                        //labelStatus.setText("Error: File Not Found");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (Throwable e) {
                        //labelStatus.setText("Read Error.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    }
                    try {
                        FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "2.txt");
                        FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dfilename);
                        Encyption.preDecrypt(keyGot, fis1, fos1);
                        fis1.close();
                        fos1.close();
                        //labelStatus.setText("Opened Successfully.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (FileNotFoundException e) {
                        //labelStatus.setText("Error: File Not Found");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    } catch (Throwable e) {
                        //labelStatus.setText("Read Error.");
                        //new JLabelCleaner(5, labelStatus).startCountdownFromNow();
                    }
                    new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "2.txt").delete();
                    new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + "1.txt").delete();
                    new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + inputfilename).delete();

                    try {

                        FileInputStream fis = new FileInputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dfilename);
                        FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dat1 + "-" + month1 + "-" + year1 + ".diary");
                        Encyption.encrypt(keyGot, fis, fos);
                        fis.close();
                        fos.close();

                    } catch (Throwable e) {

                    }
                    new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + dfilename).delete();





                    dat1--;
                    if (dat1 == 0) {
                        dat1 = 31;
                        month1--;
                    }
                }
            }







            try {
                new File(fileDir.getAbsolutePath() + "/" + fileDir2 + "/" + "LOG").createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }



            return true;
        }


            // TODO: register the new account here.



        @Override
        protected void onPostExecute(final Boolean success) {

            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        protected void de(){
            if(!(passGot.equals(null))) {
                if (mPassword.equals(passGot)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showProgress(false);
                    mAuthTask = null;
                    mPasswordView.setError(getString(R.string.error_incorrect_password));

                    mPasswordView.requestFocus();
                }
            }
        }
    }
}

