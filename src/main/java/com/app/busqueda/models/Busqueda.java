package com.app.busqueda.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "busqueda")
public class Busqueda {

	@Id
	private String id;

	@Indexed(unique = true)
	private String nombre;

	private List<Integer> palabrasClaveMuros;

	private List<String> palabrasClaveProyectos;

	public Busqueda() {
	}

	public Busqueda(String nombre, List<Integer> palabrasClaveMuros, List<String> palabrasClaveProyectos) {
		super();
		this.nombre = nombre;
		this.palabrasClaveMuros = palabrasClaveMuros;
		this.palabrasClaveProyectos = palabrasClaveProyectos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Integer> getPalabrasClaveMuros() {
		return palabrasClaveMuros;
	}

	public void setPalabrasClaveMuros(List<Integer> palabrasClaveMuros) {
		this.palabrasClaveMuros = palabrasClaveMuros;
	}

	public List<String> getPalabrasClaveProyectos() {
		return palabrasClaveProyectos;
	}

	public void setPalabrasClaveProyectos(List<String> palabrasClaveProyectos) {
		this.palabrasClaveProyectos = palabrasClaveProyectos;
	}

}
