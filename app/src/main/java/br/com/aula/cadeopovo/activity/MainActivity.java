package br.com.aula.cadeopovo.activity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.aula.cadeopovo.R;
import br.com.aula.cadeopovo.activity.entity.RegistroMovimentacao;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private LocationManager locationManager = null;

    List<RegistroMovimentacao> lstRegistroMovimentacaos = null;


    private TextView txtLatitude, txtLongitude,txtPosicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().addValueEventListener(localizacaoListner);


        if (user != null) {
            Toast.makeText(this, "Bem vindo usuário " + user.getEmail(), Toast.LENGTH_LONG).show();
            txtLatitude = (TextView) findViewById(R.id.txtLatitude);
            txtLongitude = (TextView) findViewById(R.id.txtLongitude);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            txtPosicoes = (TextView) findViewById(R.id.txtPosicoes);
            getLocalizacao();

        } else {
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logoutmenu:
                mAuth.signOut();
                Intent it = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
        }
        return true;
    }


    public void clica(View view) {
        getLocalizacao();
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLatitude.setText(String.valueOf(location.getLatitude()));
        txtLongitude.setText(String.valueOf(location.getLongitude()));
        RegistroMovimentacao registroMovimentacao =
                new RegistroMovimentacao(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getTime()
                );

        String chave = mDatabase.getReference().child("movimentacoes").push().getKey();

        Map<String, Object> movimentacaoUpdate = new HashMap<>();
        /*movimentacaoUpdate.put("/movimentacoes/"+mAuth.getUid()+"/"+chave,registroMovimentacao);

        mDatabase.getReference().updateChildren(movimentacaoUpdate);*/

        mDatabase.getReference().child("movimentacoes").child(mAuth.getUid()).setValue(registroMovimentacao);

       /* mDatabase.getReference("uid").setValue(mAuth.getUid());
        mDatabase.getReference("timestamp").setValue(location.getTime());
        mDatabase.getReference("latitude").setValue(location.getLatitude());
        mDatabase.getReference("longitude").setValue(location.getLongitude());*/

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);

    }



    ValueEventListener localizacaoListner = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            lstRegistroMovimentacaos = new ArrayList<>();


            for (DataSnapshot movSnapshot: dataSnapshot.child("movimentacoes").getChildren()) {

                RegistroMovimentacao registroMovimentacao = movSnapshot.getValue(RegistroMovimentacao.class);
                registroMovimentacao.setUid(movSnapshot.getKey());
                lstRegistroMovimentacaos.add(registroMovimentacao);
            }

            txtPosicoes.setText(String.valueOf(lstRegistroMovimentacaos.size()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
