package com.demos.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demos.service.FloodZoneService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
public class FloodZonesRestController {

    @Autowired
    private FloodZoneService floodZoneService;

    public FloodZonesRestController (FloodZoneService floodZoneService){
        this.floodZoneService = floodZoneService;
    }
    
    @GetMapping("/flood-zones")
    public ResponseEntity<String> getFloodZones() {
        return ResponseEntity.ok(floodZoneService.getAllFloodZonesGeoJson());
    }
    

    
}
