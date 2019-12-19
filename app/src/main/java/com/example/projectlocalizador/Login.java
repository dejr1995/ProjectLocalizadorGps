package com.example.projectlocalizador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings.Secure;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String URL_LISTA_EQUIPO = "http://192.168.0.4/BD_GPS/spinner/ConsultarEquipoINH.php";
    String url = "http://192.168.0.4/BD_GPS/RegistrarEquipo.php";


    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    RequestQueue requestQueue;
    private Button btnsolicitarPermiso,btnactivarDispostivo;
    private TextView txtidequipo, txtEstado;

    EditText etUsuario, etContra;
    Button btnLogin,btnMapa;
    JSONArray ja;
    TextView txtcampo;
    Spinner comboperfiles;
    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        txtEstado= (TextView) findViewById(R.id.txtEstado);
        etUsuario = (EditText)findViewById(R.id.edtUsuario);
        etContra = (EditText)findViewById(R.id.edtPassword);
        txtcampo = (TextView) findViewById(R.id.edtPerfil);
        comboperfiles = (Spinner) findViewById(R.id.spinner);
        btnLogin = (Button) findViewById(R.id.btnConsulta);
        ////////////////////////////////////////////////////////////////////////////////////////
        comboperfiles.setOnItemSelectedListener(this);
        categories = new String[] {"selecciona","admin", "user"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboperfiles.setAdapter(dataAdapter);
        ///////////////////////////////////////////////////////////////////////////////////////

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConsultaPass();

            }
        });
        txtidequipo = (TextView) findViewById(R.id.txtidequipo);
        btnsolicitarPermiso = (Button) findViewById(R.id.btnsolicitarpermiso);
        btnactivarDispostivo = (Button) findViewById(R.id.btnactivardispositivo);
        btnsolicitarPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RegistrarEquipo();
            btnsolicitarPermiso.setEnabled(false);
            btnsolicitarPermiso.setText("SOLICITUD ENVIADA");

            }
        });
        btnactivarDispostivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProxAlertActivity.class);
                startActivity(intent);
            }
        });
       equipo();
    }
    private void estado(){
        if(txtEstado.getText().equals("HAB")){
            btnsolicitarPermiso.setEnabled(false);
            btnsolicitarPermiso.setVisibility(View.INVISIBLE);
            btnactivarDispostivo.setEnabled(true);
            btnactivarDispostivo.setVisibility(View.VISIBLE);
           // Toast.makeText(getApplicationContext(), "ESTA HABILITADO", Toast.LENGTH_SHORT).show();

        }else{

           Toast.makeText(getApplicationContext(), "Dispositivo no habilitado", Toast.LENGTH_SHORT).show();

        }
    }
    private void equipo(){

        String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        txtidequipo.setText(id);
        if(txtidequipo != null){
            ConsultarEquipo();
        }


    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void ConsultaPass() {

       // Log.i("url",""+URL);
        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/consultarusuario.php?user="+etUsuario.getText().toString()+"&perfil="+txtcampo.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest =  new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtcampo.setText(txtcampo.getText().toString());
                try {
                    ja = new JSONArray(response);
                    String contra = ja.getString(0);

                    if(contra.equals(etContra.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_SHORT).show();
                        verificar();
                    }else{
                        Toast.makeText(getApplicationContext(),"Datos incorrectos",Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(),"El usuario no existe en la base de datos",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);



    }
    public void verificar() {
        String valor = etUsuario.getText().toString();

        String seleccion = comboperfiles.getSelectedItem().toString();

        if(seleccion.equals("admin")){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else if(seleccion.equals("user")){
            if(valor.equals("jose")){
                Intent i = new Intent (getApplicationContext(), SeleccionarActivo.class);
                i.putExtra("codigo", etUsuario.getText()+"");
                startActivity(i);
            }else if(valor.equals("pedro")){
                Intent i = new Intent (getApplicationContext(), SeleccionarActivo2.class);
                i.putExtra("codigo", etUsuario.getText()+"");
                startActivity(i);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        if(pos == 0){
            txtcampo.setText("");
        }else if(pos > 0){
            String description = categories[pos];
            txtcampo.setText(description.split(" ")[0]);
        }else {
            comboperfiles.setSelection(0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void ConsultarEquipo() {

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/spinner/ConsultarINH.php?id_equipo="+txtidequipo.getText().toString();



        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EntidadEquipo miActivo=new EntidadEquipo();
                JSONArray json = response.optJSONArray("equipo");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);
                    miActivo.setEstado(jsonObject.optString("estado"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtEstado.setText(miActivo.getEstado());
                estado();


                //Toast.makeText(getApplicationContext(), "Consulta exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error de conexion"+error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);

    }
    private void RegistrarEquipo(){


        if(txtidequipo != null){

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(),"Solicitud enviada",Toast.LENGTH_SHORT).show();
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
                    parameters.put("id_equipo",txtidequipo.getText().toString());

                    return parameters;
                }
            };
            requestQueue.add(request);
        }
        }


}
