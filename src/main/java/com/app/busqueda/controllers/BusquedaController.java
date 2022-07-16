package com.app.busqueda.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.busqueda.clients.ProyectosFeignClient;
import com.app.busqueda.models.Busqueda;
import com.app.busqueda.repository.BusquedaRepository;
import com.app.busqueda.responses.Proyectos;
import com.app.busqueda.services.IBusquedaServices;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BusquedaController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	BusquedaRepository bRepository;

	@Autowired
	ProyectosFeignClient pClient;

	@Autowired
	IBusquedaServices bServices;

//  ****************************	BUSQUEDA	***********************************  //

	// ************ PROYECTOS ************** //

	// BUSCAR PROYECTOS
	@GetMapping("/busqueda/proyectos/buscar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> buscarProyectos(@RequestParam("username") String username,
			@RequestParam("busqueda") String busqueda) {
		List<Integer> index = bServices.buscarProyectos(username, busqueda.toLowerCase());
		if (!index.isEmpty()) {
			return cbFactory.create("respuestas").run(() -> pClient.busquedaObtener(index),
					e -> busquedaObtenerError(index, e));
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro proyectos con tu busqueda");
	}

	// ************ USUARIOS ************** //

	// BUSQUEDA DE USUARIOS
	@GetMapping("/busqueda/username/ver/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Busqueda> buscarBusquedasUsername(@PathVariable("username") String username) {
		if (bRepository.existsByUsername(username))
			return bRepository.findByUsername(username);
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene busquedas");
	}

	// MICROSERVICIO USUARIOS -> ELIMINAR USUARIO
	@DeleteMapping("/busqueda/username/eliminar/{username}")
	public Boolean eliminarBusquedasUsername(@PathVariable("username") String username) throws IOException {
		try {
			if (bRepository.existsByUsername(username)) {
				bRepository.deleteByUsername(username);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new IOException("error eliminar usuario en preguntas: " + e.getMessage());
		}
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	private List<Proyectos> busquedaObtenerError(List<Integer> codigos, Throwable e) {
		log.info(e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Proyectos no esta disponible");
	}

}
