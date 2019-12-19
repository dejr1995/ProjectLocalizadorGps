package com.example.projectlocalizador.Entidades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectlocalizador.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Prueba2 extends AppCompatActivity {

    public static final String TAG = "MENSAJES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba2);


        String token = FirebaseInstanceId.getInstance().getToken();

        Log.i("Token:", token);

    }
}
