package com.app.busqueda.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.busqueda.clients.ProyectosFeignClient;
import com.app.busqueda.clients.RecomendacionesFeignClient;
import com.app.busqueda.models.Busqueda;
import com.app.busqueda.models.Proyectos;
import com.app.busqueda.repository.BusquedaRepository;

@RestController
public class BusquedaController {

	private final Logger logger = LoggerFactory.getLogger(BusquedaController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	BusquedaRepository bRepository;

	@Autowired
	ProyectosFeignClient pClient;

	@Autowired
	RecomendacionesFeignClient rClient;

	@PostMapping("/busqueda/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void crearBusqueda() {
		Busqueda bus = new Busqueda();
		bus.setNombre("busqueda");
		bus.setPalabrasClaveProyectos(new ArrayList<String>());
		bus.setPalabrasClaveMuros(new ArrayList<Integer>());
		bRepository.save(bus);
	}

	@PutMapping("/busqueda/proyecto/editar/")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean editarProyecto(@RequestParam("nombre") String nombre) throws IOException {
		try {
			if (bRepository.findAll().isEmpty()) {
				crearBusqueda();
			}
			Busqueda busqueda = bRepository.findByNombre("busqueda");
			List<String> proyectos = busqueda.getPalabrasClaveProyectos();
			proyectos.add(nombre.toLowerCase());
			busqueda.setPalabrasClaveProyectos(proyectos);
			bRepository.save(busqueda);
			return true;
		} catch (Exception e) {
			throw new IOException("Error edicion proyecto, busqueda: " + e.getMessage());
		}
	}

	@PutMapping("/busqueda/muro/editar/")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean editarMuro(@RequestParam("nombre") Integer nombre) throws IOException {
		try {
			Busqueda busqueda = bRepository.findByNombre("busqueda");
			List<Integer> muro = busqueda.getPalabrasClaveMuros();
			muro.add(nombre);
			busqueda.setPalabrasClaveMuros(muro);
			bRepository.save(busqueda);
			return true;
		} catch (Exception e) {
			throw new IOException("Error edicion muro, busqueda: " + e.getMessage());
		}
	}

	@GetMapping("/busqueda/proyectos/buscar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> buscarProyectos(@RequestParam("username") String username,
			@RequestParam("busqueda") String busqueda) {
		List<String> palabras = bRepository.findByNombre("busqueda").getPalabrasClaveProyectos();
		List<Integer> resultado = new ArrayList<Integer>();

		List<Proyectos> allProyectos = pClient.getProyectos();
		List<Proyectos> proyectos = new ArrayList<Proyectos>();
		if (cbFactory.create("busqueda").run(() -> rClient.editarBusqueda(username, busqueda), e -> errorConexion(e))) {
			logger.info("Creacion Correcta");
		}
		for (int i = 0; i < palabras.size(); i++) {
			if (palabras.get(i).toLowerCase().contains(busqueda.toLowerCase())) {
				resultado.add(palabras.indexOf(palabras.get(i)));
			}
		}
		if (resultado.isEmpty()) {
			return allProyectos;
		} else {
			for (int i = 0; i < resultado.size(); i++) {
				proyectos.add(allProyectos.get(resultado.get(i)));
			}
			return proyectos;
		}
	}

	@PutMapping("/busqueda/proyecto/eliminar/")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarProyecto(@RequestParam("nombre") String nombre) throws IOException {
		try {
			Busqueda busqueda = bRepository.findByNombre("busqueda");
			List<String> listaProyectos = busqueda.getPalabrasClaveProyectos();
			listaProyectos.remove(nombre.toLowerCase());
			busqueda.setPalabrasClaveProyectos(listaProyectos);
			bRepository.save(busqueda);
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar proyecto, busqueda: " + e.getMessage());
		}
	}

	@PutMapping("/busqueda/muro/eliminar/")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarMuro(@RequestParam List<Integer> listaMuro) throws IOException {
		try {
			Busqueda busqueda = bRepository.findByNombre("busqueda");
			busqueda.setPalabrasClaveMuros(listaMuro);
			bRepository.save(busqueda);
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar muro, busqueda: " + e.getMessage());
		}

	}

	public Boolean errorConexion(Throwable e) {
		logger.info(e.getMessage());
		return false;
	}
}
