package com.example.projectlocalizador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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
import com.example.projectlocalizador.Entidades.EntidadEquipo;
import com.example.projectlocalizador.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////



    Switch switchm;
    private Button botonGuardar;
    TextView latlng, direccion;
    private GoogleMap mMap;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    RequestQueue request;
    StringRequest stringRequest;
    JsonObjectRequest jsonObjectRequest;
    TextView CodObtenido;
    TextView txtlat, txtlon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        request = Volley.newRequestQueue(getApplicationContext());
        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlon = (TextView) findViewById(R.id.txtlon);
        CodObtenido = (TextView) findViewById(R.id.txtResultado);
        botonGuardar = (Button) findViewById(R.id.btnGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cargarWebServiceUpdate();

            }
        });
    }


    private void convertir(){

    }
    private void obtenermarcador(){
        try {
        if(txtlat != null && txtlon != null){
            Ubicaciones();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void obtenerCodigo(){
        String codigo = getIntent().getStringExtra("codigo");
        CodObtenido.setText(codigo);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        obtenerCodigo();
        obtenermarcador();

    }

    public void onclick(View view){

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void agregarMarcador(double lat, double lng){
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Mi Posicion Actual"));
        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcador(lat, lng);

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void Ubicaciones(){

        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/ConsultarUbicacionEquipo.php?id_equipo="+CodObtenido.getText().toString().trim();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EntidadEquipo miActivo=new EntidadEquipo();
                JSONArray json = response.optJSONArray("equipo");
                JSONObject jsonObject = null;

                try {
                    jsonObject = json.getJSONObject(0);

                    miActivo.setLatitud(jsonObject.optString("latitud"));
                    miActivo.setLongitud(jsonObject.optString("longitud"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtlat.setText(miActivo.getLatitud());
                txtlon.setText(miActivo.getLongitud());

                Double latitude=Double.parseDouble(txtlat.getText().toString());
                Double longitude=Double.parseDouble(txtlon.getText().toString());
                LatLng destino = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(destino).title("Mi Activo"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destino));



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



    ////////////////////////////////////////////////////////////////////////////////////////////////
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                actualizarUbicacion(location);

                String Text = "Lat = "+ location.getLatitude() + "\n Lng = " + location.getLongitude();

                //String lng="Longitud: " +location.getLongitude();
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> directions = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    //System.out.println(directions.get(0).getAddressLine(0)); imprimir en consola
                    //System.out.println(directions.get(0).getCountryName());
                    //System.out.println(directions.get(0).getLocality());
                    direccion.setText(directions.get(0).getAddressLine(0)); //imprimir en pantalla
                    String Text2 = "" +directions.get(0).getAddressLine(0);
                    direccion.setText(Text2);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                latlng.setText(Text);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

    private void miUbicacion() {


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        latlng = (TextView) findViewById(R.id.txtLatlng);
        direccion = (TextView) findViewById(R.id.txtDireccion);
        //longitud = (TextView) findViewById(R.id.txtlat);
        //latitud = (TexView) findViewById(R.id.txtlon)
;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,15000,0,locListener);

    }

    private void cargarWebServiceUpdate(){
        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/UpdateDireccionPersonal.php";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equalsIgnoreCase("actualiza")){
                    Toast.makeText(getApplicationContext(),"No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Se ha actualizado con exito",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error de conexion",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String id_activo=editCodigo.getText().toString();
                //String direccion=editDireccion.getText().toString();
                String coordenadas=latlng.getText().toString();


                Map<String,String> parametros=new HashMap<>();
                // parametros.put("id_activo",id_activo);
                //parametros.put("nombre",direccion);
                parametros.put("coordenadas",coordenadas);


                return parametros;
            }
        };
        request= Volley.newRequestQueue(getApplicationContext());
        request.add(stringRequest);
    }

}
