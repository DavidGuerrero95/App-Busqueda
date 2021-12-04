package com.app.busqueda.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "app-recomendacion")
public interface RecomendacionesFeignClient {
	
	@PutMapping("/recomendaciones/editarBusqueda/{nombre}")
	public Boolean editarBusqueda(@PathVariable String nombre, @RequestParam String busqueda);

}
