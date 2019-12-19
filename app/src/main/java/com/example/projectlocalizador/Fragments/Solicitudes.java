package com.example.projectlocalizador.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
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
 * {@link Solicitudes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Solicitudes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Solicitudes extends Fragment implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String URL_LISTA_EQUIPO = "http://192.168.0.6/BD_GPS/spinner/ConsultarEquipoINH.php";
    private Spinner spinnerEquipo;
    private ArrayList<EntidadEquipo> EquipoList;
    ProgressDialog pDialog;
    RequestQueue requestQueue;

    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    Button btnhabilitar, btneliminar;
    EditText txttexto;
    TextView txtEquipo, Estado;

    public Solicitudes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Solicitudes.
     */
    // TODO: Rename and change types and number of parameters
    public static Solicitudes newInstance(String param1, String param2) {
        Solicitudes fragment = new Solicitudes();
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
        View vista=inflater.inflate(R.layout.fragment_solicitudes,container,false);

        Estado = (TextView) vista.findViewById(R.id.txtEstado);

        txtEquipo = (TextView) vista.findViewById(R.id.txtEquipo);
        requestQueue = Volley.newRequestQueue(getContext());
        spinnerEquipo = (Spinner) vista.findViewById(R.id.spinequipo);
        btnhabilitar = (Button) vista.findViewById(R.id.btnhabilitar);
        btneliminar = (Button) vista.findViewById(R.id.btneliminar);
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webServiceEliminar();
            }
        });

        btnhabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            cargarWebServiceUpdate();
            spinnerEquipo.setAdapter(null);
            }
        });

        EquipoList = new ArrayList<EntidadEquipo>();
        spinnerEquipo.setOnItemSelectedListener(this);
        new Getfrutas().execute();
        return vista;
    }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        txtEquipo.setText(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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


    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();



        for (int i = 0; i < EquipoList.size(); i++) {
            lables.add(EquipoList.get(i).getId_equipo());
        }


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, lables);


        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerEquipo.setAdapter(spinnerAdapter);


    }

    private class Getfrutas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
            populateSpinner();
        }

    }

    //////////////////////////////////////////////////////
    private void cargarWebServiceUpdate(){
        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/spinner/UpdateEstadoDispositivo.php";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("actualiza")){
                    Toast.makeText(getContext(),"No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Operacion exitosa",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error de conexion",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id_equipo=txtEquipo.getText().toString();
                String estado=Estado.getText().toString();


                Map<String,String> parametros=new HashMap<>();
                parametros.put("id_equipo",id_equipo);
                parametros.put("estado",estado);

                return parametros;
            }
        };
        requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void webServiceEliminar() {
        pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Cargando...");
        pDialog.show();

        String ip=getString(R.string.ip);

        String url=ip+"/BD_GPS/DeleteDispositivo.php?id_equipo="+txtEquipo.getText().toString();

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.hide();

                if (response.trim().equalsIgnoreCase("elimina")){

                    Toast.makeText(getContext(),"Solicitud Eliminada",Toast.LENGTH_SHORT).show();
                }else{
                    if (response.trim().equalsIgnoreCase("noExiste")){
                        Toast.makeText(getContext(),"No existe en la base de datos",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }else{
                        Toast.makeText(getContext(),"No se ha Eliminado ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });
        requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }



}
