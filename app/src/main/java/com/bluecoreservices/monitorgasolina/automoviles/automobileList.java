package com.bluecoreservices.monitorgasolina.automoviles;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link automobileList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link automobileList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class automobileList extends Fragment {
    public final static String PAGINA_DEBUG = "Lista autos Frag";




    public automobileList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_automobile_list, container, false);
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
                Log.i(PAGINA_DEBUG, result.toString());
                loadingDialog.dismiss();

                /*Spinner listaCatego = (Spinner)findViewById(R.id.tipo_gasolina);
                Integer listSize = listaCatego.getCount() -1;

                JSONArray categoriaLista = null;
                ArrayList<String> nombres = new ArrayList<String>();
                final ArrayList<String> ids = new ArrayList<String>();

                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, nombres);
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
