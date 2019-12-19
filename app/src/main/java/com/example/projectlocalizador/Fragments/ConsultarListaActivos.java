package com.example.projectlocalizador.Fragments;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectlocalizador.Adaptador.ActivosAdapter;
import com.example.projectlocalizador.Entidades.Activo;
import com.example.projectlocalizador.Entidades.ActivoEntidad;
import com.example.projectlocalizador.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsultarListaActivos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsultarListaActivos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultarListaActivos extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    RecyclerView recyclerActivos;
    ArrayList<ActivoEntidad> listaActivos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    public ConsultarListaActivos() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ConsultarListaActivos newInstance(String param1, String param2) {
        ConsultarListaActivos fragment = new ConsultarListaActivos();
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
        View vista= inflater.inflate(R.layout.fragment_consultar_lista_activos,container,false);

        listaActivos = new ArrayList<>();

        recyclerActivos = (RecyclerView) vista.findViewById(R.id.idRecycler);
        recyclerActivos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerActivos.setHasFixedSize(true);

        request= Volley.newRequestQueue(getContext());
        cargarWebService();


        return vista;
    }
    private void cargarWebService() {

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/ConsultarListaActivos.php";
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url ,null,this,this);
        request.add(jsonObjectRequest);
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
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"No se puede conectar" +error.toString(),Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR:", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        ActivoEntidad activo=null;

        JSONArray json=response.optJSONArray("activo_fijo");
        try {
            for (int i=0; i<json.length();i++){
                activo=new ActivoEntidad();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                activo.setDescripcion((jsonObject.optString("descripcion")));
                activo.setSerie((jsonObject.optString("serie")));
                activo.setModelo((jsonObject.optString("modelo")));
                activo.setMarca((jsonObject.optString("marca")));

                ////////////
                activo.setDato(jsonObject.optString("imagen"));

                listaActivos.add(activo);
            }
            ActivosAdapter adapter=new ActivosAdapter(listaActivos);
            recyclerActivos.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se pudo establecer la conexion", Toast.LENGTH_LONG).show();
        }
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
