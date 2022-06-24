package com.app.busqueda.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.busqueda.models.Proyectos;

@FeignClient(name = "app-proyectos")
public interface ProyectosFeignClient {

	@GetMapping("/proyectos/listar/")
	public List<Proyectos> getProyectos();

}
