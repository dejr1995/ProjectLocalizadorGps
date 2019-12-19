package com.example.projectlocalizador.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectlocalizador.Entidades.EntidadEquipo;
import com.example.projectlocalizador.Entidades.UsuarioEntidad;
import com.example.projectlocalizador.R;
import com.example.projectlocalizador.ServiceHandler;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Registrar_Activo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Registrar_Activo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Registrar_Activo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //String insertUrl = "http://192.168.0.6/BD_GPS/RegistrarActivo.php";
    private String URL_LISTA_FRUTA = "http://192.168.0.4/BD_GPS/spinner/listarUsuario.php";
    private String URL_LISTA_EQUIPO = "http://192.168.0.4/BD_GPS/spinner/ConsultarUltimoEquipo.php";
    //////////////////////////////
    EditText edtDescripcion, edtSerie, edtModelo, edtMarca;
    Button btnRegistrar;
    private Button btnScanner;
    private TextView txtcodigo, txtUser, txtdispositivo;

    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    ListView listaResultado;
    private Spinner spinnerActivo,spinnerEquipo;

    private ArrayList<UsuarioEntidad> UsuarioList;
    private ArrayList<EntidadEquipo> EquipoList;
    ProgressDialog pDialog;
    /////////////////////////////

    public Registrar_Activo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Registrar_Activo.
     */
    // TODO: Rename and change types and number of parameters
    public static Registrar_Activo newInstance(String param1, String param2) {
        Registrar_Activo fragment = new Registrar_Activo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista=inflater.inflate(R.layout.fragment_registrar__activo,container,false);
        ////////////////////////////////////////
        txtcodigo = (EditText)vista.findViewById(R.id.edtCodigo);
        btnRegistrar = (Button) vista.findViewById(R.id.btnRegistrar);
        edtDescripcion =(EditText) vista.findViewById(R.id.edtDescripcion);
        edtSerie = (EditText) vista.findViewById(R.id.edtSerie);
        edtModelo = (EditText) vista.findViewById(R.id.edtModelo);
        edtMarca = (EditText) vista.findViewById(R.id.edtMarca);
        txtUser = (TextView) vista.findViewById(R.id.txtuser);
        txtdispositivo = (TextView)vista.findViewById(R.id.txtdispositivo);
        spinnerActivo = (Spinner) vista.findViewById(R.id.spinactivo);
        spinnerEquipo = (Spinner) vista.findViewById(R.id.spinEquipo);


        requestQueue = Volley.newRequestQueue(getContext());


        UsuarioList = new ArrayList<UsuarioEntidad>();
        EquipoList = new ArrayList<EntidadEquipo>();
       // spinnerActivo.setOnItemSelectedListener(this);
       // spinnerEquipo.setOnItemSelectedListener(this);

        new Getfrutas().execute();
        new Getfrutas2().execute();
        ///////////////////////////////////////
        btnScanner = vista.findViewById(R.id.btnScanner);

        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.forSupportFragment(Registrar_Activo.this).initiateScan();
            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarActivo();

            }
        });
        //////////////////////////////////////
        spinnerActivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtUser.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
        spinnerEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtdispositivo.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        return vista;
    }

    private void RegistrarActivo(){

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/RegistrarActivo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(),"Registro exitoso",Toast.LENGTH_SHORT).show();
                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();
                parameters.put("id_activo",txtcodigo.getText().toString());
                parameters.put("descripcion",edtDescripcion.getText().toString());
                parameters.put("serie",edtSerie.getText().toString());
                parameters.put("modelo",edtModelo.getText().toString());
                parameters.put("marca",edtMarca.getText().toString());
                parameters.put("id_user",txtUser.getText().toString());
                parameters.put("id_equipo",txtdispositivo.getText().toString());


                return parameters;
            }
        };
        requestQueue.add(request);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            txtcodigo.setText(scanContent);
        }else{

            Toast toast = Toast.makeText(getContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    ////////////////////////////////////////////
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();



        for (int i = 0; i < UsuarioList.size(); i++) {
            lables.add(UsuarioList.get(i).getUser_nombres());
        }


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, lables);


        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerActivo.setAdapter(spinnerAdapter);


    }



    private class Getfrutas extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_LISTA_FRUTA, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray UsuarioEntidad = jsonObj
                                .getJSONArray("usuario");

                        for (int i = 0; i < UsuarioEntidad.length(); i++) {
                            JSONObject catObj = (JSONObject) UsuarioEntidad.get(i);
                            UsuarioEntidad cat = new UsuarioEntidad(catObj.getString("user"),
                                    catObj.getString("user"));
                            UsuarioList.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "¿No ha recibido ningún dato desde el servidor!");
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }
    ///////////////////////////////////////////
    private void populateSpinner2() {
        List<String> lables = new ArrayList<String>();



        for (int i = 0; i < EquipoList.size(); i++) {
            lables.add(EquipoList.get(i).getId_equipo());
        }


        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, lables);


        spinnerAdapter2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerEquipo.setAdapter(spinnerAdapter2);


    }
    private class Getfrutas2 extends AsyncTask<Void, Void, Void> {
        

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Consultando Activos..");
            pDialog.setCancelable(false);
           pDialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_LISTA_EQUIPO, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray EntidadEquipo = jsonObj
                                .getJSONArray("equipo");

                        for (int i = 0; i < EntidadEquipo.length(); i++) {
                            JSONObject catObj = (JSONObject) EntidadEquipo.get(i);
                            EntidadEquipo cat = new EntidadEquipo(catObj.getString("id_equipo"),
                                    catObj.getString("id_equipo"));
                            EquipoList.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "¿No ha recibido ningún dato desde el servidor!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner2();
        }

    }
    ///////////////////////////////////////////
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}