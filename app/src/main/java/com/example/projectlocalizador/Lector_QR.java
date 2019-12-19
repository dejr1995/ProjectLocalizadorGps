package com.example.projectlocalizador;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class Lector_QR extends AppCompatActivity  {

    private Button btnScanner;
    private TextView txtcodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector__qr);

        btnScanner = findViewById(R.id.btnScanner);
        txtcodigo = findViewById(R.id.txtcodigo);

        btnScanner.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
            if (result.getContents() != null){
                txtcodigo.setText("El código de barras es:\n" + result.getContents());
            }else{
                txtcodigo.setText("Error al escanear el código de barras");
            }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnScanner:
                    new IntentIntegrator(Lector_QR.this).initiateScan();
                    break;
            }
        }
    };
}
