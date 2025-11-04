package com.example.ac_sensor_mapas_30102025;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragmentCustom extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private GoogleMap googleMap;
    private Marker marker;
    private final LatLng SANTA_CRUZ = new LatLng(-17.7833, -63.1821);

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Prepare location request with slower update rate
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(3000); // 3 seconds minimum
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Handler for UI updates
        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        locationCallback = new LocationCallback() {
            private long lastCameraUpdate = 0;
            private static final long CAMERA_UPDATE_INTERVAL = 1000;

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                Location loc = locationResult.getLastLocation();
                if (loc != null) {
                    // Update marker position immediately
                    mainHandler.post(() -> updateMarkerPosition(loc));
                    
                    // Rate limit camera updates
                    long now = System.currentTimeMillis();
                    if (now - lastCameraUpdate >= CAMERA_UPDATE_INTERVAL) {
                        lastCameraUpdate = now;
                        mainHandler.post(() -> updateCamera(loc));
                    }
                }
            }
        };

        // Programmatically add SupportMapFragment as child
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_child_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_child_fragment, mapFragment).commitNowAllowingStateLoss();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        // initial marker at Santa Cruz
        marker = googleMap.addMarker(new MarkerOptions()
                .position(SANTA_CRUZ)
                .title("Santa Cruz")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SANTA_CRUZ, 15f));
        marker.showInfoWindow();

        checkLocationPermissionAndStart();
    }

    private void checkLocationPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException ignored) {}
            }
            startLocationUpdates();
        } else {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        // Try to get last known location immediately
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) updateLocationOnMap(location);
        });
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // Split location updates into two methods for better control
    private void updateMarkerPosition(Location loc) {
        if (googleMap == null) return;
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
        // Update or create marker
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Tú")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .flat(true)); // flat marker looks better when rotating
        } else {
            marker.setPosition(pos);
            float bearing = loc.hasBearing() ? loc.getBearing() : 0f;
            marker.setRotation(bearing);
        }
    }

    private void updateCamera(Location loc) {
        if (googleMap == null) return;
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)
                .zoom(18f)
                .bearing(loc.hasBearing() ? loc.getBearing() : 0f)
                .tilt(45f)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 
                1000, // 1 second duration
                null); // no callback needed
    }

    private void updateLocationOnMap(Location loc) {
        updateMarkerPosition(loc);
        updateCamera(loc);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissionAndStart();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        // resume updates if permission granted
        if (googleMap != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    /**
     * Update marker appearance based on movement state.
     */
    public void setMovementState(boolean moving) {
        if (googleMap == null || marker == null) return;
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            if (moving) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                marker.setTitle("Moviendo");
            } else {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker.setTitle("Estático");
            }
            // Only show info window if fragment is resumed to prevent window leaks
            if (isResumed()) {
                marker.showInfoWindow();
            }
        });
    }
}
