package com.demos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.demos.model.dto.CoordinateDTO;
import com.demos.model.dto.RouteMapboxDTO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;



@Service
public class MapboxServiceImpl implements MapboxService {

    @Value("${mapbox.api.key}")
    private String mapboxApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public CoordinateDTO geocodificar(String direccion) {
        try {
            
            String url = UriComponentsBuilder
                .fromUriString("https://api.mapbox.com/geocoding/v5/mapbox.places/{query}.json")
                .queryParam("access_token", mapboxApiKey)
                .queryParam("limit", 5)
                .queryParam("language", "es")
                .queryParam("types", "poi,address,place")
                .queryParam("country", "ES")
                .buildAndExpand(direccion)
                .toUriString();         

            // LLamar a la API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);    

            // Parsear respuesta JSON
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode features = root.get("features");

            if (features == null || !features.isArray() || features.size() == 0) {
                throw new RuntimeException("No se encontraron coordenadas para: " + direccion + " | body=" + response.getBody());
            }

            // Obtener coordenadas del primer resultado
            JsonNode coordinates = features.get(0).get("geometry").get("coordinates");
            double lng = coordinates.get(0).asDouble();
            double lat = coordinates.get(1).asDouble();

            return CoordinateDTO.builder()
                .lat(lat)
                .lng(lng)
                .build();
                
        } catch (Exception ex) {
            throw new RuntimeException("Error al geocodificar la dirección. " + ex.getMessage(), ex);
        }
    }

    
    @Override
    public RouteMapboxDTO calcularRuta(CoordinateDTO origen, CoordinateDTO destino) {

        try {
            // Construir URL de Mapbox Directions API
            String url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                origen.getLng() + "," + origen.getLat() + ";" +
                destino.getLng() + "," + destino.getLat() +
                "?access_token=" + mapboxApiKey + "&geometries=geojson&overview=simplified";

            // LLamar a la API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);  
            
            // Parsear respuesta JSON
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode routes = root.get("routes");

            if (routes == null || routes.isEmpty()) {
                throw new RuntimeException("No se encontró ninguna ruta");
            }

            JsonNode route = routes.get(0);

            // Obtener distancia (en metros) y duración (en segundos)
            double distanciaM = route.get("distance").asDouble();
            int duracionS = route.get("duration").asInt();

            // Convertir distancia a km
            double distanciaKm = distanciaM / 1000;
            int duracionMin = duracionS / 60;

            // Obtener coordenadas de la ruta en geometría
            JsonNode geometry = route.get("geometry");
            JsonNode coordinates = geometry.get("coordinates");

            List<CoordinateDTO> coordenadasRuta = new ArrayList<>();
            for (JsonNode c : coordinates) {
                double lng = c.get(0).asDouble();
                double lat = c.get(1).asDouble();

                coordenadasRuta.add(CoordinateDTO.builder()
                    .lat(lat)
                    .lng(lng)
                    .build());   
            }

            return RouteMapboxDTO.builder()
                .listaCoordenadas(coordenadasRuta)
                .distancia(distanciaKm)
                .duracion(duracionMin)
                .build();

        } catch (Exception ex) {
            throw new RuntimeException("Error al calcular la ruta. " + ex.getMessage());
        }
    }


    @Override
    public List<RouteMapboxDTO> calcularRutasAlternativas(CoordinateDTO origen, CoordinateDTO destino) {
        
        try {
            // Url Mapbox con rutas alternativas
            String url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                origen.getLng() + "," + origen.getLat() + ";" +
                destino.getLng() + "," + destino.getLat() +
                "?alternatives=true" + 
                "&access_token=" + mapboxApiKey + "&geometries=geojson&overview=simplified";

            System.out.println("URL alternativas: " + url);

            // LLamar a la API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);  
            
            // Parsear respuesta JSON
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode routes = root.get("routes");

            if (routes == null || routes.isEmpty()) {
                throw new RuntimeException("No se encontró ninguna ruta");
            }
            
            List<RouteMapboxDTO> listaRutasAlternativas = new ArrayList<>();

            for (JsonNode rutaAlternativa : routes) {

                double distanciaM = rutaAlternativa.get("distance").asDouble();
                int duracionS = rutaAlternativa.get("duration").asInt();
                double distanciaKm = distanciaM / 1000;
                int duracionMin = duracionS / 60;

                // Obtener coordenadas de la ruta en geometría
                JsonNode geometry = rutaAlternativa.get("geometry");
                JsonNode coordinates = geometry.get("coordinates");

                List<CoordinateDTO> coordenadasRuta = new ArrayList<>();

                for (JsonNode c : coordinates) {

                    double lng = c.get(0).asDouble();
                    double lat = c.get(1).asDouble();

                    coordenadasRuta.add(CoordinateDTO.builder()
                        .lat(lat)
                        .lng(lng)
                        .build());   
                }

                listaRutasAlternativas.add(RouteMapboxDTO.builder()
                    .listaCoordenadas(coordenadasRuta)
                    .distancia(distanciaKm)
                    .duracion(duracionMin)
                    .build());
            }

            System.out.println("✅ Se encontraron " + listaRutasAlternativas.size() + " rutas alternativas"); //

            return listaRutasAlternativas;
        
        } catch (Exception ex) {
            throw new RuntimeException("Error al calcular rutas alternativas: " + ex.getMessage());
        }
    }



    


    

    
    
}
