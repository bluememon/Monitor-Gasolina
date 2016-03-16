package com.bluecoreservices.monitorgasolina.automoviles;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bluecoreservices.monitorgasolina.Main2Activity;
import com.bluecoreservices.monitorgasolina.R;

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

public class automobileList extends Fragment {
    public final static String PAGINA_DEBUG = "Lista autos Frag";

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;


    public automobileList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_automobile_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.automobile_list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

       return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LoadAutomobiles();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void LoadAutomobiles() {

        class LoginAsync  extends AsyncTask<String, Void, JSONObject> {
            private Dialog loadingDialog;
            private final String url = "http://mg.bluecoreservices.com/api/vehicle.php";

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
                loadingDialog = ProgressDialog.show(getContext(), "Please wait", "Loading...");
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                String idUser = "1";

                sbParams = new StringBuilder();

                try {
                    sbParams.append("accion").append("=").append(URLEncoder.encode("list", charset)).append("&");
                    sbParams.append("userId").append("=").append(URLEncoder.encode(idUser, charset));
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
                loadingDialog.dismiss();

                //Integer listSize = mAdapter.getCount() -1;

                JSONArray listaVehiculos = null;
                try {
                    listaVehiculos = result.getJSONArray("VehicleLists");

                    ArrayList<String> brands = new ArrayList<String>();
                    ArrayList<String> models = new ArrayList<String>();
                    ArrayList<String> years = new ArrayList<String>();
                    final ArrayList<String> ids = new ArrayList<String>();

                    if (listaVehiculos.length() > 0){
                        /*brands.clear();
                        mAdapter.notifyDataSetChanged();*/
                    }

                    for (int i = 0; i < listaVehiculos.length(); i++){

                        JSONObject categoriaVehiculo = listaVehiculos.getJSONObject(i);

                        brands.add(categoriaVehiculo.getString("brand"));
                        models.add(categoriaVehiculo.getString("model"));
                        years.add(categoriaVehiculo.getString("year"));
                        ids.add(categoriaVehiculo.getString("id"));
                    }

                    Log.i(PAGINA_DEBUG, brands.toString());
                    Log.i(PAGINA_DEBUG, models.toString());
                    Log.i(PAGINA_DEBUG, years.toString());
                    Log.i(PAGINA_DEBUG, ids.toString());

                    ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.automobile_list_element);



                }catch (JSONException e) {
                    e.printStackTrace();
                }


                /*

                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                if (listSize > 0){
                    nombres.clear();
                    adapter.notifyDataSetChanged();
                }*/

                /*try {
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
                            selectedGasType = ids.get(position);
                            Log.i(PAGINA_DEBUG, selectedGasType);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }
        LoginAsync la = new LoginAsync();
        la.execute();
    }
}
