package com.example.rplrus10.tangria_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class mainActivity extends AppCompatActivity {

    Button btnNext;
    EditText txtnama,txtalamat;
    id user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNext = findViewById(R.id.btn_next);
        txtnama = findViewById(R.id.edtNama);
        user = new id();
        txtalamat = findViewById(R.id.edtAlamat);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setName(txtnama.getText().toString());
                user.setAlamat(txtalamat.getText().toString());
                if (user.getName().equals("")|| user.getAlamat().equals("")){
                    Toast.makeText(getApplicationContext(),"Can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    new LoginProcess().execute();
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    public class LoginProcess extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            //kasih loading
            //spinner.setVisibility(View.VISIBLE);
        }
        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObject;

            try {
                String url = "http://192.168.6.88/kuesioner/android/input_name.php?nama="+user.getName()+"&&alamat="+user.getAlamat()+"";
                System.out.println("url ku " +url);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream inputStream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inputStream, "iso-8859-1"
                ), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                inputStream.close();
                String json = stringBuilder.toString();
                System.out.println("json error : "+json);
                jsonObject = new JSONObject(json);
            } catch (Exception e) {
                jsonObject = null;
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.d("hasil json ", "onPostExecute: "+jsonObject.toString());
           // spinner.setVisibility(View.INVISIBLE);
            if (jsonObject != null) {
                try {
                    JSONObject Result=jsonObject.getJSONObject("Result");
                    String sukses=Result.getString("Sukses");
                    Log.d("hasil sukses ", "onPostExecute: "+sukses);

                    if (sukses.equals("true")){
                        Intent intent = new Intent(mainActivity.this,tampilanPertanyaan.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"lebih teliti",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                    System.out.println("erornya "+ignored);
                }
            }else{
            }
        }
    }
}
