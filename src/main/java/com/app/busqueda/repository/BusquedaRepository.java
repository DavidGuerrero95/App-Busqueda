package com.app.busqueda.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.busqueda.models.Busqueda;

public interface BusquedaRepository extends MongoRepository<Busqueda, String> {

	@RestResource(path = "find-username")
	public List<Busqueda> findByUsername(@Param("username") String username);
	
	@RestResource(path = "exists-username")
	public Boolean existsByUsername(@Param("username") String username);
	
	@RestResource(path = "delete-username")
	public void deleteByUsername(@Param("username") String username);

}