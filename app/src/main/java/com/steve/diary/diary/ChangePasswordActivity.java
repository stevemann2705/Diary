package com.steve.diary.diary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChangePasswordActivity extends AppCompatActivity {


    private String passGot=null;
    public String keyGot=null;

    public String getKeyGot(){
        return keyGot;
    }

    protected File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    protected String fileDir2 = "Diary";
    private String setupTemp = "setupTemp.rtf";
    private File settings = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"settings.data");


    EditText et;
    EditText et2;
    EditText et3;

    TextView tv;
    TextView tv1;

    Button cont;
    Button changePass;


    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");


        et = (EditText) findViewById(R.id.pent);
        et2 = (EditText) findViewById(R.id.newp);
        et3 = (EditText) findViewById(R.id.cp);
        tv = (TextView) findViewById(R.id.tnewp);
        tv1 = (TextView) findViewById(R.id.tcp);
        cont = (Button) findViewById(R.id.button);
        changePass = (Button) findViewById(R.id.bcp);

        et3.setVisibility(View.GONE);
        et2.setVisibility(View.GONE);

        tv.setVisibility(View.GONE);
        tv1.setVisibility(View.GONE);
        changePass.setVisibility(View.GONE);

        //changePass.setEnabled(false);

    }

    public void continueBtnClicked(View v){
        try {
            FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"Settings.data");
            FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp);
            Encyption.decrypt("SteveMann",fis1,fos1);
            fis1.close();
            fos1.close();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            //Logger.getLogger(Diary.class.getName()).log(Level.SEVERE, null, ex);
        }


        try {
            FileReader fr = new FileReader(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp);
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
        new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp).delete();




        String passEntered = et.getText().toString();

        if (passEntered.equals(passGot)){




            et.setFocusable(false);
            et.setClickable(true);

            et3.setVisibility(View.VISIBLE);
            et2.setVisibility(View.VISIBLE);

            tv.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            changePass.setVisibility(View.VISIBLE);

            cont.setEnabled(false);
            //changePass.setEnabled(true);

        }
        else {
            et.setError("Wrong Password");
            et.requestFocus();
        }
    }

    public void changepass(View v){


        String pass, cpass;
        pass = et2.getText().toString();
        cpass = et3.getText().toString();

        if(et2.getText().toString().length()<5){

            et2.setError(getString(R.string.error_passlength_short));
            et2.requestFocus();
            return;
            // cancel = true;

        }else{
            if(pass.equals(cpass)){

                if(!(new File(fileDir.getAbsolutePath()+"/"+fileDir2).exists())){
                    new File(fileDir.getAbsolutePath()+"/"+fileDir2).mkdirs();
                }


                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+setupTemp));

                    out.write(pass);
                    out.newLine();
                    out.append(keyGot);
                    out.newLine();
                    out.append(new MainActivity().getNameGot());
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


                Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            }
            else{
                et3.setError("Passwords Don't Match");
                et3.requestFocus();
                return;
            }
        }


        //Log.d("pass",password);
        //Log.d("key",key);


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }


}
