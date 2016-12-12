package com.steve.diary.diary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

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

public class ImageViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    File path;
    String fileDir2;
    int year, month, date;
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    ImageView imgView;
    String imagepath, imageEnc;

    //static Cipher cipher = null;
    //SecretKey desKey;

    Cipher cipher;
    SecretKeySpec skeySpec;
    GCMParameterSpec ivspec;

    public void onBackPressed() {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mainimage_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Image of the Day");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtnPressed(view);
            }
        });

        MainActivity ma = new MainActivity();
        path = ma.fileDir;
        fileDir2 = ma.fileDir2;
        year = MainActivity.getYear1();
        month = MainActivity.getMonth11();
        date = MainActivity.getDat1();

        //Log.d("IMAGEDATE",date+"-"+month+"-"+year);
        imgView = (ImageView) findViewById(R.id.imageView);

        //DESKeySpec dks = null;


        /*
        try {
            dks = new DESKeySpec(MainActivity.keyGot.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            desKey = skf.generateSecret(dks);
            cipher = Cipher.getInstance("DES");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        */

        byte[] mykey = new byte[0];
        try {
            mykey = MainActivity.keyGot.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            mykey = sha.digest(mykey);
            mykey = Arrays.copyOf(mykey, 16);
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);  // To use 256 bit keys, you need the "unlimited strength" encryption policy files from Sun.
            //byte[] key = Diary.userKey.getBytes();
            skeySpec = new SecretKeySpec(mykey, "AES");

            // build the initialization vector (randomly).
            //SecureRandom random = new SecureRandom();
            //byte iv[] = new byte[16];//generate random 16 byte IV AES is always 16bytes
            //random.nextBytes(iv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ivspec = new GCMParameterSpec(128,mykey);
            }

            // initialize the cipher for encrypt mode
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }


        final File x = new File(path.getAbsolutePath()+"/"+fileDir2+"/"+date+"-"+month+"-"+year);
        imageEnc = x.getAbsolutePath() + "/" + date+"-"+month+"-"+year + ".diarydata";


        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);

        if(!(new File((x.getAbsolutePath() + "/" + date+"-"+month+"-"+year + ".diarydata")).exists())){
           // x.mkdirs();
            //new File(imageEnc).mkdir();
            bar.setVisibility(View.GONE);
            new AlertDialog.Builder(this)
                    .setTitle("Do you want to add Image of the Day?")
                    .setMessage("No image found for the day. Click OK to add an image now.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            x.mkdirs();
                            //Log.d("Dialog", "YES PRESSED");
                            openImageChooser();


                        }})
                    .setNegativeButton(android.R.string.no, null).show()
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Intent intent = new Intent(ImageViewActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

        }
        else{


            class ImageASyncTask extends AsyncTask {

                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);


                @Override
                protected void onProgressUpdate(Object[] values) {
                    super.onProgressUpdate(values);
                    if (this.bar != null) {
                        bar.setProgress((Integer) values[0]);
                    }
                }

                @Override
                protected void onPostExecute(Object o) {
                    bar.setVisibility(View.GONE);
                    imgView.setImageBitmap((Bitmap)o);
                    new File(x.getAbsolutePath() + "/"  +"imageTemp.jpg").delete();
                }

                @Override
                protected Bitmap doInBackground(Object[] params) {
                    decrypt(imageEnc, x.getAbsolutePath() + "/"  +"imageTemp.jpg");
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                   // File x = new File(path.getAbsolutePath()+"/"+fileDir2+"/"+date+"-"+month+"-"+year);
                    Bitmap bitmap = BitmapFactory.decodeFile(x.getAbsolutePath() + "/"  +"imageTemp.jpg",bmOptions);
                    //Bitmap bmp = Bitmap.createBitmap(x.getAbsolutePath() + "/"  +"imageTemp.jpg");


                    double h = bitmap.getHeight();
                    double w = bitmap.getWidth();

                    //Log.d("HW:","Height "+h+" Width "+w);
                    double max = 3000;
                    double dh,dw;
                    if (w == h) {
                        dw = max;
                        dh = max;
                    } else if (w > h) {
                        dw = max;
                        dh = ( h /  w) * max;
                    } else {
                        dh = max;
                        dw = ( w / h) * max;
                    }
                    //Log.d("HW:","DHeight "+dh+" DWidth "+dw);
                    Bitmap sc = Bitmap.createScaledBitmap(bitmap,(int)dw ,(int)dh,true);
                    return sc;
                }
            }


            ImageASyncTask imageASyncTask = new ImageASyncTask();
            imageASyncTask.execute();



            //imgView.setImageURI(Uri.fromFile(new File(x.getAbsolutePath() +"/" +"imageTemp.jpg")));






        }


    }




    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }

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
        if(id == R.id.action_delete){
            deleteBtnPressed();

        }

        return super.onOptionsItemSelected(item);
    }



    protected void decrypt(String srcPath, String destPath) {
        File encryptedFile = new File(srcPath);
        File decryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for decryption
             */
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(encryptedFile);
            outStream = new FileOutputStream(decryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } //catch (BadPaddingException ex) {
        //System.out.println("BADPADDING");
        //  }
        catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            //Logger.getLogger(FileEncryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    protected void encrypt(String srcPath, String destPath) {
        File rawFile = new File(srcPath);
        File encryptedFile = new File(destPath);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for encryption
             */
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(rawFile);
            outStream = new FileOutputStream(encryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            //Logger.getLogger();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    imagepath = getPathFromURI(selectedImageUri);
                    //Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    imgView.setImageURI(selectedImageUri);
                    new AsyncTask(){

                        @Override
                        protected Object doInBackground(Object... params) {
                            encrypt(imagepath, imageEnc);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Toast.makeText(getApplicationContext(), "Image Encrypted Successfully", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }.execute();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void backBtnPressed(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteBtnPressed(){
        new File(imageEnc).delete();
        new File(path.getAbsolutePath()+"/"+fileDir2+"/"+date+"-"+month+"-"+year).delete();

        Toast.makeText(getApplicationContext(), "Image Deleted Successfully", Toast.LENGTH_SHORT)
                .show();



        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_calender) {
            Toast.makeText(getApplicationContext(), "Please first goto Home and then get calendar.", Toast.LENGTH_LONG)
                    .show();
        }else if(id == R.id.nav_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.nav_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
