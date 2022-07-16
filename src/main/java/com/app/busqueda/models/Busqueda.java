package com.app.busqueda.models;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "busqueda")
@Data
@NoArgsConstructor
public class Busqueda {

	@Id
	@JsonIgnore
	private String id;

	@NotNull(message = "Username cannot be null")
	private String username;

	@NotNull(message = "Busqueda de pregunta be null")
	private String busqueda;

	public Busqueda(String username, String busqueda) {
		super();
		this.username = username;
		this.busqueda = busqueda;
	}

}
