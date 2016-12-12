package com.steve.diary.diary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String key;
    static int year1, dat1, month1;

    protected File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    protected String fileDir2 = "Diary";

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    Calendar instance = Calendar.getInstance();
    Date inputdate = instance.getTime();
    final int date = inputdate.getDate();
    final int month = inputdate.getMonth() + 1;
    final int year = inputdate.getYear() -100;

    private String dfilename = "test.rtf";
    private String efilename = date + "-" + month + "-" + year + ".diary";
    private File a = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+efilename);

    private String setupTemp = "setupTemp.rtf";
    private File settings = new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+"settings.data");

    static String passGot, keyGot;
    public static String nameGot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // ******************** Get Key ******************//

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

                nameGot = br.readLine();


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



        TextView welcome = (TextView) findViewById(R.id.welcomeText);
        welcome.setText("Welcome " + nameGot);

        int xmonth,xdate,xyear;
        xmonth = month;
        xdate = date;
        xyear = year+2000;

        String xsmonth;
        switch (xmonth){
            case 1:
                xsmonth = "January";
                break;
            case 2:
                xsmonth = "February";
                break;
            case 3:
                xsmonth = "March";
                break;
            case 4:
                xsmonth = "April";
                break;
            case 5:
                xsmonth = "May";
                break;
            case 6:
                xsmonth = "June";
                break;
            case 7:
                xsmonth = "July";
                break;
            case 8:
                xsmonth = "August";
                break;
            case 9:
                xsmonth = "September";
                break;
            case 10:
                xsmonth = "October";
                break;
            case 11:
                xsmonth = "November";
                break;
            case 12:
                xsmonth = "December";
                break;
            default:
                xsmonth = "Random";

        }


        TextView textDate = (TextView) findViewById(R.id.textDate);
        textDate.setText("Today is "+xsmonth+" "+xdate+", "+xyear);

        //Log.d("nameGot", nameGot);


        // **************** Check Logs ************************//


        // DatePicker dp1 = (DatePicker) findViewById(R.id.datePicker);


        //System.out.println(date + "-" + month + "-" + year + ".diary");
        //Log.d("Diary",date + "-" + month + "-" + year + ".diary");

        //System.out.println(inputdate);

        Calendar instance = Calendar.getInstance();
        Date date = instance.getTime();
        year1 = date.getYear() - 100;
        dat1 = date.getDate();
        month1 = date.getMonth() + 1;






        getDateLog(year1, month1, dat1);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.textViewName);
        name.setText(nameGot);







    }



    public String getNameGot(){
        return nameGot;
    }

    // ********** Date Picker *************//


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Please select a date", Toast.LENGTH_SHORT)
                .show();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog dpd = new DatePickerDialog(this, myDateListener, year1, month1, dat1);
            dpd.updateDate(year+2000, month-1, date);

            return  dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override

        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            year1 = arg1-2000;
            month1 = arg2+1;
            dat1 = arg3;
            getDateLog(arg1, arg2, arg3);
        }
    };

    private void getDateLog(int year, int month, int day) {

        EditText editText = (EditText) findViewById(R.id.textBox);
        String inputfilename = dat1 + "-" + month1 + "-" + year1 + ".diary";
        // System.out.println(dat1 + "-" + month1 + "-" + year1 + ".diary");
        //Log.d("Diary",dat1 + "-" + month1 + "-" + year1 + ".diary");
        //Log.d("KeyGotInMain",keyGot);



        //******************* Decrypt ******************//
        try {


            FileInputStream fis1 = new FileInputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+inputfilename);
            FileOutputStream fos1 = new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename);
            Encyption.decrypt(keyGot, fis1, fos1);
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


        //StringBuilder text = new StringBuilder();
        StringBuilder text = new StringBuilder();

        try{
            BufferedReader br = new BufferedReader(new FileReader(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename));
            String line;
            while((line=br.readLine())!=null){
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Spanned t = null;
        try {
            t = Html.fromHtml(text.toString());
            editText.setText(t);
        }catch(NullPointerException e){
            Log.e("ERROR::::","Null Pointer at editText.setText(text);");
        }





        new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename).delete();


        Calendar c = Calendar.getInstance();
        Date datee = c.getTime();
        int d = datee.getDate();
        int m = datee.getMonth()+1;
        int y = datee.getYear()-100;

        //Log.d("DateInput", dat1+"-"+month1+"-"+year1);
        //Log.d("DateActual", d+"-"+m+"-"+y);

        if((dat1!=d)||(month1!=m)||(year1!=y)){
            editText.setFocusable(false);
            //editText.setClickable(true);
            //editText.setLongClickable(false);
        }
        else{
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            //editText.requestFocus();
        }

    }

    public static int getDat1(){
        return dat1;
    }
    public static int getMonth11(){
        return month1;
    }
    public static int getYear1(){
        return year1;
    }

    // ************************************//



    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        if(id == R.id.action_save){
            click();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }
        else if (id == R.id.nav_calender) {
            setDate(this.getCurrentFocus());
        }
        else if (id == R.id.nav_image) {
            startActivity(new Intent(this, ImageViewActivity.class));
            finish();
        }
        else if(id == R.id.nav_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }
        else if(id==R.id.nav_rate){
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void click(){
        EditText editText = (EditText) findViewById(R.id.textBox);
        Spanned text1 = editText.getText();
        String text = Html.toHtml(text1);

        //************ Writing to file **************//
        if(!(new File(fileDir.getAbsolutePath()+"/"+fileDir2).exists())){
            new File(fileDir.getAbsolutePath()+"/"+fileDir2).mkdirs();
        }
        if(!(a.exists())){


            try {
                a.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        try {

            FileInputStream fis = new FileInputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename);
            FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+efilename);
            Encyption.encrypt(keyGot, fis, fos);
            fis.close();
            fos.close();

        } catch (Throwable e) {

        }

        new File(fileDir.getAbsolutePath()+"/"+fileDir2+"/"+dfilename).delete();


        Toast.makeText(getApplicationContext(), "Log Saved Successfully.", Toast.LENGTH_LONG)
                .show();
    }
}
