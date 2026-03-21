package com.botpanel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ArchivoController {

    @Value("${archivos.directorio:uploads}")
    private String directorio;

    // POST /api/archivos/subir — recibe el archivo y lo guarda en disco
    @PostMapping("/subir")
    public ResponseEntity<Map<String, String>> subir(
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            // Crea la carpeta uploads si no existe
            Path dirPath = Paths.get(directorio);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Genera un nombre único para evitar colisiones
            String original = archivo.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf("."));
            String nombreUnico = UUID.randomUUID().toString() + extension;

            // Guarda el archivo en disco
            Path rutaArchivo = dirPath.resolve(nombreUnico);
            Files.write(rutaArchivo, archivo.getBytes());

            System.out.println("✅ Archivo guardado: " + nombreUnico);

            // Devuelve el nombre original y la URL pública
            return ResponseEntity.ok(Map.of(
                "nombre", original,
                "url",    "/api/archivos/" + nombreUnico
            ));

        } catch (IOException e) {
            System.err.println("❌ Error guardando archivo: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    // GET /api/archivos/{nombre} — devuelve el archivo para que Twilio lo descargue
    @GetMapping("/{nombre}")
    public ResponseEntity<byte[]> descargar(@PathVariable String nombre) {
        try {
            Path rutaArchivo = Paths.get(directorio).resolve(nombre);

            if (!Files.exists(rutaArchivo)) {
                return ResponseEntity.notFound().build();
            }

            byte[] contenido = Files.readAllBytes(rutaArchivo);

            // Detecta el tipo de archivo
            String contentType = "application/octet-stream";
            if (nombre.endsWith(".pdf"))  contentType = "application/pdf";
            if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) contentType = "image/jpeg";
            if (nombre.endsWith(".png"))  contentType = "image/png";

            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", "inline; filename=\"" + nombre + "\"")
                .body(contenido);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}