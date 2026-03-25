package com.botpanel.controller;

import com.botpanel.entity.Archivo;
import com.botpanel.repository.ArchivoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ArchivoController {

    @Value("${app.base-url:https://botpanel-production.up.railway.app}")
    private String baseUrl;

    private final ArchivoRepository archivoRepository;

    public ArchivoController(ArchivoRepository archivoRepository) {
        this.archivoRepository = archivoRepository;
    }

    @PostMapping("/subir")
    public ResponseEntity<Map<String, String>> subir(
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            String original = archivo.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String id = UUID.randomUUID().toString() + extension;

            Archivo entity = new Archivo();
            entity.setId(id);
            entity.setNombre(original);
            entity.setContentType(archivo.getContentType());
            entity.setDatos(archivo.getBytes());
            archivoRepository.save(entity);

            System.out.println("✅ Archivo guardado en BD: " + id);

            return ResponseEntity.ok(Map.of(
                "nombre", original,
                "url", baseUrl + "/api/archivos/" + id
            ));

        } catch (Exception e) {
            System.err.println("❌ Error guardando archivo: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> descargar(@PathVariable String id) {
        return archivoRepository.findById(id)
            .map(archivo -> ResponseEntity.ok()
                .header("Content-Type", archivo.getContentType())
                .header("Content-Disposition", "inline; filename=\"" + archivo.getNombre() + "\"")
                .body(archivo.getDatos()))
            .orElse(ResponseEntity.notFound().build());
    }
}
