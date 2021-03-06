package com.example.dell_pc.testmaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/**
 * Created by Dell - PC on 3/28/2017.
 */

public class Sender extends AsyncTask<Void,Void,String> {

    Context c;
    EditText nameET,phoneET,emailET;
    String urlAdd, name, phone, email, latitude, longitude;
    ProgressDialog progressDialog;

    public Sender(Context c, String urlAdd,String latitude,String longitude,EditText ... editTexts) {
        this.c = c;
        this.urlAdd = urlAdd;
        this.nameET = editTexts[0];
        this.phoneET = editTexts[1];
        this.emailET = editTexts[2];
        this.latitude = latitude;
        this.longitude = longitude;

        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
        email = emailET.getText().toString();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(c);
        progressDialog.setTitle("Send");
        progressDialog.setMessage("Sending .. Please Wait");
        progressDialog.show();


    }

    @Override
    protected String doInBackground(Void... voids) {
        return this.send();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        progressDialog.dismiss();

        if(s != null)
        {
            Toast.makeText(c,s,Toast.LENGTH_LONG).show();

            nameET.setText("");
            phoneET.setText("");
            emailET.setText("");
        }else
        {
            Toast.makeText(c,"Unsuccessful"+s,Toast.LENGTH_LONG).show();
        }
    }

    private String send()
    {
        HttpURLConnection connection = Connector.connect(urlAdd);

        if(connection == null)
        {
            return null;
        }

        try{
            OutputStream os = connection.getOutputStream();

            //write to data packer
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            bufferedWriter.write(new DataPacker(name,phone,email,latitude,longitude).pack());

            //release resources
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();

            //Success or not?
            int responseCode = connection.getResponseCode();

            if(responseCode == connection.HTTP_OK)
            {
                //get exact response
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();

                String line = null;

                while((line = bufferedReader.readLine()) != null)
                {
                    response.append(line);
                }

                //release resources
                bufferedReader.close();

                return response.toString();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

