package com.aluracursos.literalura.service;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.model.Idiomas;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private static final String RECUENTO_TOTAL_LIBROS = "    * Recuento total de libros cargados: ";
    private static final String RECUENTO_TOTAL_AUTORES = "    * Recuento total de autores cargados: ";
    private static final String RECUENTO_LIBROS_POR_IDIOMA = "    * Recuento de libros por idioma:";
    private static final String ESTADISTICAS_DESCARGAS_LIBROS = "    * Datos estadísticos de descargas: ";

    @Autowired
    private LibrosRepository librosRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void mostrarEstadisticas() {
        System.out.println("La base de datos de LiterAlura contiene la siguiente información:\n");
        mostrarRecuentoTotalLibros();
        mostrarRecuentoTotalAutores();
        mostrarRecuentoLibrosPorIdioma();
        mostrarEstadisticasDescargasLibros();
    }

    private void mostrarRecuentoTotalLibros() {
        long recuentoTotal = librosRepository.count();
        System.out.println(RECUENTO_TOTAL_LIBROS + recuentoTotal);
    }

    private void mostrarRecuentoTotalAutores() {
        List<Autor> autores = autorRepository.findAllWithoutDuplicates();
        long recuentoTotal = autores.size();
        System.out.println(RECUENTO_TOTAL_AUTORES + recuentoTotal);
    }

    private void mostrarRecuentoLibrosPorIdioma() {
        System.out.println(RECUENTO_LIBROS_POR_IDIOMA);

        Map<Idiomas, Long> recuentoPorIdioma = librosRepository.findAll().stream()
                .collect(Collectors.groupingBy(Libro::getIdioma, Collectors.counting()));

        recuentoPorIdioma.forEach((idioma, recuento) ->
                System.out.println("        - " + idioma.toString().toLowerCase() + ": " + recuento));
    }

    private void mostrarEstadisticasDescargasLibros() {
        System.out.println(ESTADISTICAS_DESCARGAS_LIBROS);

        DoubleSummaryStatistics estadisticasDescargas = librosRepository.findAll().stream()
                .mapToDouble(Libro::getNumeroDescargas)
                .filter(descargas -> descargas > 0)
                .summaryStatistics();

        System.out.println("        - Cantidad máxima: " + estadisticasDescargas.getMax());
        System.out.println("        - Cantidad mínima: " + estadisticasDescargas.getMin());
        System.out.println("        - Cantidad media: " + estadisticasDescargas.getAverage());
    }
}
