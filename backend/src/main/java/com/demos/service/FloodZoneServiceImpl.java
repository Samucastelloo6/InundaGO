package com.demos.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public  class FloodZoneServiceImpl implements FloodZoneService{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String getAllFloodZonesGeoJson() {

        
            String sql = """
            SELECT json_build_object(
              'type', 'FeatureCollection',
              'features', COALESCE(json_agg(
                json_build_object(
                  'type', 'Feature',
                  'geometry', ST_AsGeoJSON(geom)::json,
                  'properties', json_build_object('id', id)
                )
              ), '[]'::json)
            )::text
            FROM zonas_inundacion
        """;

        return (String) entityManager.createNativeQuery(sql).getSingleResult();
    }
    
}
