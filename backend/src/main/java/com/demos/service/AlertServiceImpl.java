package com.demos.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demos.model.dto.AlertDTO;
import com.demos.repository.dao.AlertRepository;
import com.demos.repository.entity.Alert;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AlertServiceImpl implements AlertService {

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    private AlertRepository alertRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public AlertDTO obtenerAlertaMeteo(Double lat, Double lng) {
        
        try {

            String url = "https://api.openweathermap.org/data/2.5/weather" +
                        "?lat=" + lat +
                        "&lon=" + lng +
                        "&appid=" + apiKey +
                        "&units=metric";
            
            // Lamada a la API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Parsear respuesta
            JsonNode root = objectMapper.readTree(response.getBody());

            // Obtener lluvia (si hay)
            double mmLluvia = 0.0;

            if (root.has("rain") && root.get("rain").has("1h")) {
                mmLluvia = root.get("rain").get("1h").asDouble();
            } 

            // Buscar nivel de alerta según mm lluvia
            BigDecimal umbralBdLluvia = BigDecimal.valueOf(mmLluvia);

            Alert alerta = alertRepository.findByMmLluvia(umbralBdLluvia).orElse(null);

            if (alerta == null) {
                return null;
            }

            return AlertDTO.convertToDTO(alerta, umbralBdLluvia);

        } catch (Exception ex) {
            System.err.println("Error al obtener clima" + ex.getMessage());
            return null;
        }
    } 
}
