package com.demos.repository.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class User implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Long idUsuario;
	
	@Column(nullable = false, length = 60)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String apellidos;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, unique = true, length = 120)
	private String email;
	
	@Column(name = "fecha_login", nullable = false, updatable = false)
	private LocalDate fechaLogin;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rol", nullable = false)
	@ToString.Exclude
	private Role rol;


	public String getUsername() {
		return email;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(idUsuario, other.idUsuario);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idUsuario);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(rol.getNombreRol()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
    public boolean isAccountNonLocked() {
       return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
	
    @Override
    public boolean isEnabled() {
        return true;
    }
}
