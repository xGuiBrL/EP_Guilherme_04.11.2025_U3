package com.example.ep_guilherme_04112025_u3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class fragment_map extends Fragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private Marker Marca;
    private LatLng baseLocation = new LatLng(-17.7080, -63.1250);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        miMapa = googleMap;
        Marca = miMapa.addMarker(new MarkerOptions().position(baseLocation).title("Ubicación actual"));
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(baseLocation, 15));
    }

    public void updateMarker(String movementType) {
        if (Marca == null) return;

        float color = BitmapDescriptorFactory.HUE_GREEN;
        String title = "Movimientos" +
                " Suave";

        if ("Moderado".equals(movementType)) {
            color = BitmapDescriptorFactory.HUE_YELLOW;
            title = "Movimiento Moderado";
        } else if ("Brusco".equals(movementType)) {
            color = BitmapDescriptorFactory.HUE_RED;
            title = "Movimiento Brusco";
        }

        Marca.setIcon(BitmapDescriptorFactory.defaultMarker(color));
        Marca.setTitle(title);
    }

    private void checkGeofence(LatLng location) {
        if (location.latitude < -17.8) {
            Toast.makeText(getContext(), "Zona de riesgo", Toast.LENGTH_SHORT).show();
        }
    }
}