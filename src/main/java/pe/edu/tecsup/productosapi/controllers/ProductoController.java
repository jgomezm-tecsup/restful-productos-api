package pe.edu.tecsup.productosapi.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

//@RequestMapping(path="", produces="application/json")
//@CrossOrigin(origins="*")

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.tecsup.productosapi.entities.Producto;
import pe.edu.tecsup.productosapi.services.ProductoService;


@RestController
public class ProductoController {

    private static final Logger logger 
    		= LoggerFactory.getLogger(ProductoController.class);

	@Value("${app.storage.path}")
	private String STORAGEPATH;
    
	@Autowired
	private ProductoService productoService;
	
	@GetMapping("/productos")
	public ResponseEntity<List<Producto>> listar() 
			throws Exception{
		
		logger.info("call listar");
		
		List<Producto> productos = productoService.findAll();
		productos.forEach(item -> logger.info("ITEM >> " + item));
		
		logger.info("productos: " + productos);
		
		return ResponseEntity.ok(productos);
	}
	
	@GetMapping("/productos/name/{name}")
	public ResponseEntity<List<Producto>> listarPorNombre(
			@PathVariable String name) throws Exception {
		
		logger.info("call productos");
		logger.info("search by name: " + name);
		
		List<Producto> productos 
					= productoService.findByName(name);
		logger.info("productos: " + productos);
		
		return ResponseEntity.ok(productos);
	}
	
    @GetMapping("/productos/id/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) 
    		throws Exception {
    	
    	logger.info("call obtener: " + id);

    	Optional<Producto> producto = productoService.findById(id);

    	if (producto.isPresent()) 
    		return ResponseEntity.ok(producto.get()); 
    	else
    		return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/productos/images/{filename:.+}")
	public ResponseEntity<Resource> files(@PathVariable String filename) throws Exception{
		logger.info("call images: " + filename);
		
		Path path = Paths.get(STORAGEPATH).resolve(filename);
		logger.info("Path: " + path);
		
		if(!Files.exists(path)) {
			return ResponseEntity.notFound().build();
		}
		
		Resource resource = new UrlResource(path.toUri());
		logger.info("Resource: " + resource);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+resource.getFilename()+"\"")
				.header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(STORAGEPATH).resolve(filename)))
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
				.body(resource);
	}



	@PostMapping("/productos")
	public  ResponseEntity<Producto> crear(
			@RequestBody Producto request ) throws Exception {
		
		logger.info("call crear :" + request);
		
		Producto producto = new Producto();
		producto.setNombre(request.getNombre());
		producto.setPrecio(request.getPrecio());
		producto.setDetalles(request.getDetalles());
		producto.setEstado("1");
		
		productoService.save(producto);
		
		return ResponseEntity.ok(producto);
	}

	@PutMapping("/productos/id/{id}") 
	public  ResponseEntity<Producto> actualizar(@PathVariable Long id,
			@RequestBody Producto request ) throws Exception {
		
		logger.info("call crear :" + request);
		
    	Optional<Producto> producto_encontrado
    		= productoService.findById(id);

    	if (producto_encontrado.isPresent()) {
    		
			Producto producto = producto_encontrado.get();
    		producto.setNombre(request.getNombre());
    		producto.setPrecio(request.getPrecio());
    		
    		productoService.save(producto);
    		
    		return ResponseEntity.ok(producto); 
    	
    	} else {
    	
    		return ResponseEntity.notFound().build();
    	
    	}
	}
	
	
	@DeleteMapping("/productos/id/{id}")
	public ResponseEntity<String> eliminar(@PathVariable Long id) 
			throws Exception {
		
		logger.info("call eliminar: " + id);
		
		try { 
		
			productoService.deleteById(id);
			
			return ResponseEntity.ok("Producto eliminado");
		
		} catch (EmptyResultDataAccessException e) {}
		
		return ResponseEntity.notFound().build();
	}

}
