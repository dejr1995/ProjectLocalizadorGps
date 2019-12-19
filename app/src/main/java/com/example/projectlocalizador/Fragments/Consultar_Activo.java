package com.example.projectlocalizador.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.projectlocalizador.Entidades.ActivoEntidad;
import com.example.projectlocalizador.MapsActivity;
import com.example.projectlocalizador.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Consultar_Activo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Consultar_Activo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Consultar_Activo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText editCodigo,editDireccion,editCoordenadas,editDescripcion,editSerie,editModelo,editMarca;
    Button btnConsultarActivo, btnUpdateActivo;
    ImageButton btnimgclean,btnimg2;
    ImageView imagen;
    TextView txtequipo;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    public Consultar_Activo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Consultar_Activo.
     */
    // TODO: Rename and change types and number of parameters
    public static Consultar_Activo newInstance(String param1, String param2) {
        Consultar_Activo fragment = new Consultar_Activo();
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

        View vista=inflater.inflate(R.layout.fragment_consultar__activo,container,false);
        editCodigo= (EditText) vista.findViewById(R.id.editCodigo);
        editDireccion= (EditText) vista.findViewById(R.id.editDireccion);
        editCoordenadas= (EditText) vista.findViewById(R.id.editCoordenadas);
        editDescripcion= (EditText) vista.findViewById(R.id.editDescripcion);
        editSerie= (EditText) vista.findViewById(R.id.editSerie);
        editModelo= (EditText) vista.findViewById(R.id.editModelo);
        editMarca= (EditText) vista.findViewById(R.id.editMarca);
        txtequipo= (TextView) vista.findViewById(R.id.txtequipo);





        btnConsultarActivo= (Button) vista.findViewById(R.id.btnConsultarActivo);
        btnUpdateActivo= (Button) vista.findViewById(R.id.btnUpdateActivo);
        btnimgclean= (ImageButton) vista.findViewById(R.id.btnimg1);
        imagen= (ImageView) vista.findViewById(R.id.imagenId) ;
        btnimg2= (ImageButton) vista.findViewById(R.id.btnimg2);

        /////////////////////////////////////////////////////////////////////////////////////////////
        btnimg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getActivity(), MapsActivity.class);
                i.putExtra("codigo", txtequipo.getText()+"");
                startActivity(i);
            }
        });
        request = Volley.newRequestQueue(getContext());
        btnimgclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clean();
            }
        });
        btnUpdateActivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebServiceUpdate();
            }
        });
        btnConsultarActivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebServiceConsultarActivo();
                btnimg2.setVisibility(View.VISIBLE);
            }
        });

        return vista;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void cargarWebServiceConsultarActivo() {

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/ConsultarActivos.php?id_activo="+editCodigo.getText();



        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ActivoEntidad miActivo=new ActivoEntidad();
                JSONArray json = response.optJSONArray("activo_fijo");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);
                    miActivo.setDireccion(jsonObject.optString("direccion"));
                    miActivo.setCoordenadas(jsonObject.optString("coordenadas"));
                    miActivo.setDescripcion(jsonObject.optString("descripcion"));
                    miActivo.setSerie(jsonObject.optString("serie"));
                    miActivo.setModelo(jsonObject.optString("modelo"));
                    miActivo.setMarca(jsonObject.optString("marca"));

                    miActivo.setDato(jsonObject.optString("imagen"));

                    miActivo.setId_equipo(jsonObject.optString("id_equipo"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editDireccion.setText(miActivo.getDireccion());
                editCoordenadas.setText(miActivo.getCoordenadas());
                editDescripcion.setText(miActivo.getDescripcion());
                editSerie.setText(miActivo.getSerie());
                editModelo.setText(miActivo.getModelo());
                editMarca.setText(miActivo.getMarca());


                if (miActivo.getImagen() !=null){
                    imagen.setImageBitmap(miActivo.getImagen());
                }else{
                    imagen.setImageResource(R.drawable.img_base);
                }

                txtequipo.setText(miActivo.getId_equipo());
                Toast.makeText(getContext(), "Consulta exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error de conexion"+error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        request.add(jsonObjectRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void cargarWebServiceUpdate(){
        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/UpdateActivo.php";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("actualiza")){
                    Toast.makeText(getContext(),"No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Se ha actualizado con exito",Toast.LENGTH_SHORT).show();
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
                String id_activo=editCodigo.getText().toString();
                //String direccion=editDireccion.getText().toString();
                //String coordenadas=editCoordenadas.getText().toString();
                String descripcion=editDescripcion.getText().toString();
                String serie=editSerie.getText().toString();
                String modelo=editModelo.getText().toString();
                String marca=editMarca.getText().toString();


                Map<String,String> parametros=new HashMap<>();
                parametros.put("id_activo",id_activo);
                //parametros.put("direccion",direccion);
                //parametros.put("coordenadas",coordenadas);
                parametros.put("descripcion",descripcion);
                parametros.put("serie",serie);
                parametros.put("modelo",modelo);
                parametros.put("marca",marca);

                return parametros;
            }
        };
        request= Volley.newRequestQueue(getContext());
        request.add(stringRequest);
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
    private void clean(){
        editCodigo.setText("");
        editDireccion.setText("");
        editCoordenadas.setText("");
        editDescripcion.setText("");
        editSerie.setText("");
        editModelo.setText("");
        editMarca.setText("");
        imagen.setImageResource(R.drawable.img_base);
    }
}