package com.example.hebronexpo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    EditText vName, vEmail, vMobile, vJob;
    Button Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vName = findViewById(R.id.vName);
        vEmail = findViewById(R.id.vEmail);
        vMobile = findViewById(R.id.vMobile);
        vJob = findViewById(R.id.vJob);
        Save = findViewById(R.id.save);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  SendData();

                if (vName.getText().toString().isEmpty() ||
                        vMobile.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "الرحاء تعبئة الاسم و رقم الهاتف..", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت..", Toast.LENGTH_LONG).show();
                    return;
                }

                String IP = "http://api.alamana.ps";
                String url = IP + "/upload/AddUser";

                SendData sendReg = new SendData(url);
                sendReg.execute();

            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class SendData extends AsyncTask<String, String, String> {

        private String urlString;

        public SendData(String urlString) {
            this.urlString = urlString;
        }
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("جاري إرسال البيانات ...");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String res = Send(urlString);
            return res;
        }


        public String Send(String urlString) {
            HttpPost httppost = new HttpPost(urlString);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("Name", vName.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Phone", vMobile.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Email", vEmail.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Company", vJob.getText().toString()));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            HttpResponse httpresponse = null;
            try {
                httpresponse = httpclient.execute(httppost, httpContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String resp = null;
            try {
                if (httpresponse != null)
                    resp = EntityUtils.toString(httpresponse.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s!= null  && s.contains("success".trim())) {
                Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();


                vEmail.setText("");
                vMobile.setText("");
                vJob.setText("");
                vName.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }


}
