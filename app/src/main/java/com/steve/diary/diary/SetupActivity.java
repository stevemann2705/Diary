package com.steve.diary.diary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SetupActivity extends AppCompatActivity {

    String password, key, name;

    protected File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    protected String fileDir2 = "Diary";
    private String setupTemp = "setupTemp.rtf";
    private File settings = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"settings.data");
    private File settingsTemp = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsetup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_calender) {
            setDate(this.getCurrentFocus());
        }else if(id==R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
        }*/
        if(id == R.id.action_setup_ok){
            setup();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setup(){
        TextView pass = (TextView) findViewById(R.id.textPass);
        TextView keyView = (TextView) findViewById(R.id.textKey);
        TextView nameView = (TextView) findViewById(R.id.textName);

        if(keyView.getText().toString().length()<8){

                keyView.setError(getString(R.string.error_keylength_short));
                keyView.requestFocus();
                return;
               // cancel = true;

        }

        if(pass.getText().toString().length()<5){

            pass.setError(getString(R.string.error_passlength_short));
            pass.requestFocus();
            return;
            // cancel = true;

        }

        password = pass.getText().toString();
        key = keyView.getText().toString();
        name = nameView.getText().toString();

        //Log.d("pass",password);
        //Log.d("key",key);

        if(!(new File(fileDir.getAbsolutePath()+"/"+fileDir2).exists())){
            new File(fileDir.getAbsolutePath()+"/"+fileDir2).mkdirs();
        }
        /*if(!(settings.exists())){


            try {
                settings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        /*try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp));
            outputStreamWriter.write(password + '\n' + key + '\n' + name);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp));

            out.write(password);
            out.newLine();
            out.append(key);
            out.newLine();
            out.append(name);
            out.close();
        }catch(Exception e){

        }

        /*try {
            BufferedWriter out = new BufferedWriter(new FileWriter(String.valueOf(new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp))));

            out.write(password);

            out.newLine();
            out.append(key);
            out.close();
        }catch(Exception e){

        }*/

        try {

            FileInputStream fis = new FileInputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp);
            FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"Settings.data");
            Encyption.encrypt("SteveMann", fis, fos);
            fis.close();
            fos.close();

        } catch (Throwable e) {

        }

        new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp).delete();


        //Toast.makeText(getApplicationContext(),"For security reasons, we need to restart the app. This is a natural process. Just start the app again.\nThank You for using Personal Diary. (made by Steve Mann)",Toast.LENGTH_LONG);

       /* new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Closing Activity").setMessage("re you sure you want to close this activity").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("No",null).show();*/

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
