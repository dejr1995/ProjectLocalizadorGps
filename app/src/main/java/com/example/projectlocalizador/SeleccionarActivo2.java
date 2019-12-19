package com.example.projectlocalizador;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectlocalizador.Entidades.ActivoEntidad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SeleccionarActivo2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener{



    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;


    private TextView txtlogin, txtspiner, txtnombre;
    private Button btnbuscarubicacion;
    private EditText txtAgregar;
    private Spinner spinnerActivo;
    // array para listar las frutas
    private ArrayList<ActivoEntidad> ActivoList;
    ProgressDialog pDialog;


    private String URL_LISTA_1 = "http://192.168.0.4/BD_GPS/spinner/usuarioonelista.php";
    private String URL_LISTA_2 = "http://192.168.0.4/BD_GPS/spinner/usuariotwolista.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_activo);


        spinnerActivo = (Spinner) findViewById(R.id.spinactivo);

        btnbuscarubicacion = (Button) findViewById(R.id.btnbuscar);
        btnbuscarubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getApplication(), MapsActivity.class);
                i.putExtra("codigo", txtnombre.getText()+"");
                startActivity(i);


            }
        });

        request = Volley.newRequestQueue(getApplicationContext());

        txtnombre = (TextView) findViewById(R.id.txtnombre);
        txtlogin = (TextView) findViewById(R.id.txtlogin);
        txtspiner = (TextView) findViewById(R.id.txtspinner);

        ActivoList = new ArrayList<ActivoEntidad>();
        spinnerActivo.setOnItemSelectedListener(this);

        new Getfrutas().execute();


    }


    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();



        for (int i = 0; i < ActivoList.size(); i++) {
            lables.add(ActivoList.get(i).getId_activo());

        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, lables);


        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerActivo.setAdapter(spinnerAdapter);
        obtenerCodigo();
    }

    private class Getfrutas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SeleccionarActivo2.this);
            pDialog.setMessage("Consultando Activos..");
            pDialog.setCancelable(false);
            pDialog.show();
        }



        @Override
        protected Void doInBackground(Void... arg0) {


            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_LISTA_2, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);


            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray ActivoEntidad = jsonObj
                                .getJSONArray("activo_fijo");

                        for (int i = 0; i < ActivoEntidad.length(); i++) {
                            JSONObject catObj = (JSONObject) ActivoEntidad.get(i);
                            ActivoEntidad cat = new ActivoEntidad(catObj.getString("id_activo"),
                                    catObj.getString("nombre_activo"));
                            ActivoList.add(cat);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Seleccionado" ,
                Toast.LENGTH_LONG).show();

        txtspiner.setText(parent.getItemAtPosition(position).toString());
        cargarWebServiceConsultarActivo();




    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private void obtenerCodigo(){
        String codigo = getIntent().getStringExtra("codigo");
        txtlogin.setText(codigo);

    }

    private void cargarWebServiceConsultarActivo() {

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/ConsultarActivos.php?id_activo="+txtspiner.getText();



        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ActivoEntidad miActivo=new ActivoEntidad();
                JSONArray json = response.optJSONArray("activo_fijo");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);

                    miActivo.setId_equipo(jsonObject.optString("id_equipo"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtnombre.setText(miActivo.getId_equipo());
                Toast.makeText(getApplicationContext(), "Consulta exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error de conexion"+error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        request.add(jsonObjectRequest);
    }
}
