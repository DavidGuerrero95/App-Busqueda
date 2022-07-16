package com.app.busqueda.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.busqueda.responses.Proyectos;

@FeignClient(name = "app-proyectos")
public interface ProyectosFeignClient {

	@GetMapping("/proyectos/listar/")
	public List<Proyectos> getProyectos();

	@GetMapping("/proyectos/busqueda/obtener/")
	public List<Proyectos> busquedaObtener(List<Integer> codigos);
}
