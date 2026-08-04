package com.demos.repository.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ruta")
public class Route {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ruta")
	private Long idRuta;
	
	@Column(name = "direccion_origen", nullable = false)
	private String direccionOrigen;

	@Column(name = "direccion_destino", nullable = false)
	private String direccionDestino;
	
	@Column(name = "punto_origen", columnDefinition = "geometry(Point,4326)", nullable = false)
	private Point puntoOrigen;
	
	@Column(name = "punto_destino", columnDefinition = "geometry(Point,4326)", nullable = false)
	private Point puntoDestino;
	
	@Column(name = "linea_ruta", columnDefinition = "geometry(LineString,4326)", nullable = false)
	private LineString lineaRuta;
	
	private Double distancia;
	private Integer duracion;

	@Column(name = "fecha_busqueda")
	private LocalDateTime fechaBusqueda;
	
	private Boolean riesgo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	@ToString.Exclude
	private User usuario;
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return Objects.equals(idRuta, other.idRuta);
	}
	@Override
	public int hashCode() {
		return Objects.hash(idRuta);
	}
}
