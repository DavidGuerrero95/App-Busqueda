package com.app.busqueda.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.busqueda.clients.ProyectosFeignClient;
import com.app.busqueda.models.Busqueda;
import com.app.busqueda.repository.BusquedaRepository;
import com.app.busqueda.responses.Proyectos;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BusquedaServices implements IBusquedaServices {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	BusquedaRepository bRepository;

	@Autowired
	ProyectosFeignClient pClient;

	@Override
	public List<Integer> buscarProyectos(String username, String busqueda) {

		bRepository.save(new Busqueda(username, busqueda));

		List<Proyectos> allProyectos = cbFactory.create("respuestas").run(() -> pClient.getProyectos(),
				e -> encontrarProyecto(e));

		List<String> nombres = new ArrayList<String>();
		List<List<String>> palabrasClave = new ArrayList<List<String>>();
		List<Integer> idProyectos = new ArrayList<Integer>();
		List<Integer> index = new ArrayList<Integer>();

		allProyectos.forEach(x -> {
			nombres.add(x.getNombre().toLowerCase());
			palabrasClave.add(x.getPalabrasClave());
			idProyectos.add(x.getCodigoProyecto());
		});

		nombres.forEach(x -> {
			if (x.contains(busqueda))
				index.add(idProyectos.get(nombres.indexOf(x)));
		});

		palabrasClave.forEach(x -> {
			x.forEach(n -> {
				if (n.toLowerCase().contains(busqueda)) {
					int i = palabrasClave.indexOf(x);
					if (!index.contains(i))
						index.add(idProyectos.get(i));
				}
			});
		});
		return index;
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	private List<Proyectos> encontrarProyecto(Throwable e) {
		log.error(e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Proyectos no disponible");
	}

}
