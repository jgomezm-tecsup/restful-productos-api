package pe.edu.tecsup.productosapi.services;

import java.util.List;
import java.util.Optional;

import pe.edu.tecsup.productosapi.entities.Producto;

public interface ProductoService {

	public List<Producto> findAll();

	public List<Producto> findByName(String name);

	public Optional<Producto> findById(Long id);

	public void save(Producto producto);

	public void deleteById(Long id);

}
