package com.GetechnologiesMx.prueba.controller;

import com.GetechnologiesMx.prueba.dto.Message;
import com.GetechnologiesMx.prueba.dto.PersonaDto;
import com.GetechnologiesMx.prueba.models.Persona;
import com.GetechnologiesMx.prueba.services.Directorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/directorio")
@CrossOrigin(origins = "*")
public class DirectorioRestService {
    private final Logger log = LoggerFactory.getLogger(DirectorioRestService.class);

    @Autowired
    private Directorio directorio;

    @GetMapping
    public ResponseEntity<List<Persona>> listarPersonas(){
        return ResponseEntity.ok( this.directorio.findPersonas() );
    }
    @GetMapping("/find/{identificacion}")
    public ResponseEntity<Persona> listarPersonaPorIdentificacion(@PathVariable String identificacion){
        if (identificacion.isEmpty() || identificacion.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Persona p = this.directorio.findPersonaByIdentificacion(identificacion);
        return ResponseEntity.ok( p );
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Message> eliminar(@PathVariable String id){

        if (this.directorio.findPersonaByIdentificacion(id) == null){
            return new ResponseEntity<Message>( new Message("No hay registro con esa identificación..."), HttpStatus.BAD_REQUEST );
        }

        String mensaje = this.directorio.deletePersonaByIdentificacion(id);
        return new ResponseEntity<Message>( new Message(mensaje), HttpStatus.OK );

    }

    @PostMapping
    public ResponseEntity<Message> store(@RequestBody @Valid PersonaDto personaDto){
        if (personaDto.getNombre().isEmpty() || personaDto.getNombre().isBlank()){
            return new ResponseEntity<Message>( new Message("Debes ingresar un nombre"), HttpStatus.BAD_REQUEST );
        }
        if (personaDto.getApellidoPaterno().isEmpty() || personaDto.getApellidoPaterno().isBlank()){
            return new ResponseEntity<Message>( new Message("Debes ingresar el apellido paterno"), HttpStatus.BAD_REQUEST );
        }
        if (personaDto.getIdentificacion().isEmpty() || personaDto.getIdentificacion().isBlank()){
            return new ResponseEntity<Message>( new Message("La identificación es obligatoria"), HttpStatus.BAD_REQUEST );
        }

        if (this.directorio.existsByIdentificacion(personaDto.getIdentificacion()) ) {
            return new ResponseEntity<Message>( new Message("El número de identificación ya existe"), HttpStatus.BAD_REQUEST );
        }

        Persona p = this.directorio.storePersona(personaDto);
        if (p != null) {
            return new ResponseEntity<Message>( new Message("Registro exitoso"), HttpStatus.CREATED );
        } else {
            return new ResponseEntity<Message>( new Message("Registro fallido"), HttpStatus.BAD_REQUEST );
        }
    }
}
