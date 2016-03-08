package com.bluecoreservices.monitorgasolina.reportes;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluecoreservices.monitorgasolina.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class agregar_registro extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    public final static String PAGINA_DEBUG = "agregar_registro";

    private TextView stationName;
    private String stationId;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPlaces();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stationName = (TextView) findViewById(R.id.sucursal_gasolinera);
        stationId = "";
    }

    private void checkPlaces() {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            //intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(agregar_registro.this);
            PlacePicker.getLatLngBounds(intent);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) { 
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final CharSequence id = place.getId();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            //Log.e("Place.Name", name.toString());
            //Log.e("Place.getAddress", address.toString());


            stationName.setText(name.toString());
            stationId = id.toString();

            Log.e("Place.getId", stationId);

            /*mName.setText(name);
            mAddress.setText(address);
            mAttributions.setText(Html.fromHtml(attributions));*/

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getGasType() {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://app.bluecoreservices.com/webservices/getCategorias.php";

            String charset = "UTF-8";
            HttpURLConnection conn;
            DataOutputStream wr;
            StringBuilder result = new StringBuilder();
            URL urlObj;
            JSONObject jObj = null;
            StringBuilder sbParams;
            String paramsString;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(add_catego.this, "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String uname = idPaciente;

                sbParams = new StringBuilder();

                try {
                    sbParams.append("idPaciente").append("=").append(URLEncoder.encode(uname, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    urlObj = new URL(url);

                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", charset);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);

                    conn.connect();

                    paramsString = sbParams.toString();

                    wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(paramsString);
                    wr.flush();
                    wr.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //response from the server
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                conn.disconnect();

                String stringResult = result.toString().trim();

                try {
                    jObj = new JSONObject(stringResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return jObj;
            }


            @Override
            protected void onPostExecute(JSONObject result) {
                Log.i(PAGINA_DEBUG, result.toString());
                loadingDialog.dismiss();

                Spinner listaCatego = (Spinner)findViewById(R.id.spinner_catego);
                Integer listSize = listaCatego.getCount() -1;

                JSONArray categoriaLista = null;
                ArrayList<String> nombres = new ArrayList<String>();
                final ArrayList<String> ids = new ArrayList<String>();

                ArrayAdapter adapter = new ArrayAdapter(add_catego.this, android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                if (listSize > 0){
                    nombres.clear();
                    adapter.notifyDataSetChanged();
                }

                try {
                    categoriaLista = result.getJSONArray("categorias");


                    for (int i = 0; i < categoriaLista.length(); i++){

                        JSONObject categoriaElemento = categoriaLista.getJSONObject(i);

                        nombres.add(categoriaElemento.getString("nombre"));
                        ids.add(categoriaElemento.getString("id"));
                    }

                    listaCatego.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    listaCatego.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCatego = ids.get(position);
                            Log.i(PAGINA_DEBUG, selectedCatego);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute();
    }

}
