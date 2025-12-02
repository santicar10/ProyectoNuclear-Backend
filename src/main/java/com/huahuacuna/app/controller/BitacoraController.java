package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.BitacoraDTO;
import com.huahuacuna.app.model.Bitacora;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.BitacoraService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/bitacoras")
public class BitacoraController {

    @Autowired
    private BitacoraService bitacoraService;


    @PostMapping("/admin")
    public ResponseEntity<Bitacora> crear(@RequestBody BitacoraDTO dto,
                                          HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(403).build();
        }
        Bitacora nueva = bitacoraService.crearBitacoraPorAdmin(dto);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Bitacora> actualizar(@PathVariable Integer id,
                                               @RequestBody BitacoraDTO dto,
                                               HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(403).build();
        }

        Bitacora actualizada = bitacoraService.actualizar(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id,
                                         HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(403).build();
        }

        bitacoraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Bitacora>> listarTodas(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(bitacoraService.listarTodas());
    }


    @GetMapping("/padrino/{idApadrinamiento}")
    public ResponseEntity<List<Bitacora>> listarPorPadrino(
            @PathVariable Integer idApadrinamiento,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || usuario.getRol() != Usuario.Rol.padrino) {
            return ResponseEntity.status(403).build();
        }

        Integer idUsuario = usuario.getIdUsuario();

        List<Bitacora> lista = bitacoraService.listarPorPadrino(idApadrinamiento, idUsuario);
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/padrino/pdf/{idBitacora}")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Integer idBitacora,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || usuario.getRol() != Usuario.Rol.padrino) {
            return ResponseEntity.status(403).build();
        }

        Integer idUsuario = usuario.getIdUsuario();

        Bitacora bitacora = bitacoraService.listarTodas().stream()
                .filter(b -> b.getIdBitacora().equals(idBitacora))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bitácora no encontrada"));

        if (!bitacora.getApadrinamiento().getPadrino().getIdUsuario().equals(idUsuario)) {
            return ResponseEntity.status(403).build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(pdf, baos);
            pdf.open();

            String contenido = bitacoraService.generarTextoBitacora(bitacora);
            pdf.add(new com.itextpdf.text.Paragraph(contenido));

            pdf.close();

            byte[] bytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=bitacora_" + idBitacora + ".pdf")
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}


