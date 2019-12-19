package com.example.projectlocalizador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.projectlocalizador.Fragments.ConsultarListaActivos;
import com.example.projectlocalizador.Fragments.Consultar_Activo;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.projectlocalizador.Fragments.Registrar_Activo;
import com.example.projectlocalizador.Fragments.Solicitudes;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentBienvenida.OnFragmentInteractionListener,
        Consultar_Activo.OnFragmentInteractionListener, ConsultarListaActivos.OnFragmentInteractionListener, Registrar_Activo.OnFragmentInteractionListener,
        Solicitudes.OnFragmentInteractionListener
        {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentBienvenida()).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment miFragment=null;
        boolean fragmentSeleccionado=false;


        if (id == R.id.nav_home){
            miFragment = new Registrar_Activo();
            fragmentSeleccionado=true;
            //Intent intent = new Intent(this, Lector_QR.class);
           // startActivity(intent);
        }else if (id == R.id.nav_gallery) {
            miFragment = new Consultar_Activo();
            fragmentSeleccionado=true;
        }else if (id == R.id.nav_slideshow) {
            miFragment = new ConsultarListaActivos();
            fragmentSeleccionado=true;
        }else if (id == R.id.nav_tools) {
            miFragment =new Solicitudes();
            fragmentSeleccionado=true;
        }else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_send) {
            Intent intent = new Intent(this, ProxAlertActivity.class);
            startActivity(intent);
        }

        if (fragmentSeleccionado==true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,miFragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
