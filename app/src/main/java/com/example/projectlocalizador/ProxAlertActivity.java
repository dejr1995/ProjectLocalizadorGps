package com.example.projectlocalizador;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;

public class ProxAlertActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1; // in Milliseconds

    private static final long POINT_RADIUS = 1000; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;

    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

    private static final String PROX_ALERT_INTENT =
            "com.example.projectlocalizador.ProximityAlert";

    private static final NumberFormat nf = new DecimalFormat("##.########");

    private LocationManager locationManager;

    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button findCoordinatesButton;
    private Button savePointButton;


    TextView txtID;

    TextView latlng, direccion;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prox_alert);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /////////////////////////////////////
        time time = new time();
        time.execute();

        //////////////////////
        time2 time2 = new time2();
        time2.execute();
        ////////////////////////////////////

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                new MyLocationListener()
        );


        txtID = (TextView) findViewById(R.id.txtId);
        latlng = (TextView) findViewById(R.id.txtLatlng);
        latitudeEditText = (EditText) findViewById(R.id.point_latitude);
        longitudeEditText = (EditText) findViewById(R.id.point_longitude);
        findCoordinatesButton = (Button) findViewById(R.id.find_coordinates_button);
        savePointButton = (Button) findViewById(R.id.save_point_button);

        findCoordinatesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                populateCoordinatesFromLastKnownLocation();
            }
        });

        savePointButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProximityAlertPoint();


            }
        });

    }

    private void checkSMSStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para enviar SMS!");
        }
    }

    private void EnviarSMS() {
        checkSMSStatePermission();
        String phone = "938484632";
        String text = "Se ha detectado movimiento del objeto";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, text, null, null);
    }

    private void saveProximityAlertPoint() {

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            Toast.makeText(this, "No last known location. Aborting...",
                    Toast.LENGTH_LONG).show();
            return;
        }
        saveCoordinatesInPreferences((float) location.getLatitude(),
                (float) location.getLongitude());
        addProximityAlert(location.getLatitude(), location.getLongitude());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void obtenerID(){
        String id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        txtID.setText(id);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void addProximityAlert(double latitude, double longitude) {


        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected

        );

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);



    }


    private void populateCoordinatesFromLastKnownLocation() {
        ProxAlertActivity.time time = new ProxAlertActivity.time();
        time.execute();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                new MyLocationListener()
        );
        if (location != null) {
            latitudeEditText.setText(nf.format(location.getLatitude()));
            longitudeEditText.setText(nf.format(location.getLongitude()));
            latlng.setText(nf.format(location.getLatitude()) + "\n" + (location.getLongitude()));


        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void agregarMarcador(double lat, double lng) {
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
    private void ObtenerEquipo(){

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void cargarWebServiceUpdate() {
        ProxAlertActivity.time2 time = new ProxAlertActivity.time2();
        time.execute();
        String ip=getString(R.string.ip);
        String url=ip+"/BD_GPS/UpdateCoordenadas.php";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("actualiza")) {
                    Toast.makeText(getApplicationContext(), "Fallo al enviar coordenadas", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(),"Se ha actualizado con exito",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id_activo=txtID.getText().toString();
                //String direccion=txtdirec.getText().toString();
                String coordenadas = latlng.getText().toString();
                String latitud = latitudeEditText.getText().toString();
                String longitud = longitudeEditText.getText().toString();

                Map<String, String> parametros = new HashMap<>();
                 parametros.put("id_equipo",id_activo);
                //parametros.put("direccion",direccion);
                parametros.put("coordenadas", coordenadas);
                parametros.put("latitud", latitud);
                parametros.put("longitud", longitud);

                return parametros;
            }
        };
        request = Volley.newRequestQueue(getApplicationContext());
        request.add(stringRequest);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void saveCoordinatesInPreferences(float latitude, float longitude) {
        SharedPreferences prefs =
                this.getSharedPreferences(getClass().getSimpleName(),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
        prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
        prefsEditor.commit();
    }

    private Location retrievelocationFromPreferences() {
        SharedPreferences prefs =
                this.getSharedPreferences(getClass().getSimpleName(),
                        Context.MODE_PRIVATE);
        Location location = new Location("POINT_LOCATION");
        location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
        location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
        return location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        obtenerID();
        //mMap.setMyLocationEnabled(true);
        populateCoordinatesFromLastKnownLocation();
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "HOLA ME PRESIONASTE", Toast.LENGTH_SHORT).show();
    }


    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            //Location pointLocation = retrievelocationFromPreferences();
            //float distance = location.distanceTo(pointLocation);
           // Toast.makeText(ProxAlertActivity.this,
             //       "Objeto fuera de su lugar - Distancia aproximada:" + distance, Toast.LENGTH_SHORT).show();

            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            registerReceiver(new ProximityIntentReceiver(), filter);
            //EnviarSMS();


        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
        }

        public void onProviderEnabled(String s) {
        }
    }





    //////////////////////////////////////////////////////////////////////////////////////////////
    //tiempo
    public void hilo(){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public class time extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            for(int i=1; i<2; i++){
                hilo();

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            populateCoordinatesFromLastKnownLocation();

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void hilo2(){
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public class time2 extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            for(int i=1; i<2; i++){
                hilo2();

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            cargarWebServiceUpdate();
            Toast.makeText(getApplicationContext(),"Enviando coordenadas...",Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
