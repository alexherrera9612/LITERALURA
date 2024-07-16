package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.*;
import com.aluracursos.literalura.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado;
    private ConsumoAPI consumoAPI;
    private ConvierteDatos conversor;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private static final String MENU = """
            \nElija el número de la opción deseada:\n
            1 - Buscar libro por título
            2 - Listar libros registrados
            3 - Listar autores registrados
            4 - Listar autores vivos en un determinado año
            5 - Listar libros por idioma
            6 - Listar libros por título
            7 - Listar autores por nombre
            8 - Buscar los 5 libros más descargados
            9 - Mostrar estadísticas de la base de datos
            0 - Salir
            """;

    // Inyección de dependencias
    @Autowired
    private LibrosRepository librosRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private EstadisticasService estadisticasService;

    // Inicialización de componentes necesarios
    @PostConstruct
    public void init() {
        teclado = new Scanner(System.in);
        consumoAPI = new ConsumoAPI();
        conversor = new ConvierteDatos();
    }

    // MENÚ CON EL QUE VA A INTERACTUAR EL USUARIO
    public void muestraMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println(MENU);
            try {
                opcion = Integer.parseInt(teclado.nextLine());
                ejecutarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
            }
        }
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> buscarLibroPorTitulo();
            case 2 -> listarLibrosRegistrados();
            case 3 -> listarAutoresRegistrados();
            case 4 -> listarAutoresVivosEnDeterminadoAnio();
            case 5 -> listarLibrosPorIdioma();
            case 6 -> listarLibrosPorTitulo();
            case 7 -> listarAutoresPorNombre();
            case 8 -> buscarTop5LibrosDescargados();
            case 9 -> mostrarEstadisticas();
            case 0 -> System.out.println("\n\nLa aplicación se está cerrando...\n\n");
            default -> System.out.println("Opción inválida.");
        }
    }

    // MÉTODO PARA BUSCAR UN LIBRO EN LA API (POR TÍTULO O PARTE DE ÉL)
    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        String tituloLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+").toLowerCase());
        libroService.buscarLibroPorTitulo(tituloLibro, json);
    }

    // MÉTODO PARA LISTAR LOS LIBROS REGISTRADOS EN LA BASE DE DATOS
    private void listarLibrosRegistrados() {
        List<Libro> libros = librosRepository.findAll();
        libros.forEach(System.out::println);
    }

    // MÉTODO PARA LISTAR LOS AUTORES REGISTRADOS EN LA BASE DE DATOS
    private void listarAutoresRegistrados() {
        List<String> sortedAutores = autorService.listarAutoresRegistrados();
        System.out.println("\nLISTADO DE AUTORES REGISTRADOS:\n──────────────────────────────");
        sortedAutores.forEach(System.out::println);
    }

    // MÉTODO PARA LISTAR AUTORES REGISTRADOS EN LA BASE DE DATOS VIVOS EN UN DETERMINADO AÑO
    private void listarAutoresVivosEnDeterminadoAnio() {
        System.out.println("En esta opción podrá buscar autores vivos en un determinado año." +
                "\n¿Autores vivos en qué año desea encontrar?");
        int anio = Integer.parseInt(teclado.nextLine());
        autorService.listarAutoresVivosEnAnio(anio);
    }

    // MÉTODO PARA LISTAR LIBROS DE ACUERDO AL IDIOMA
    private void listarLibrosPorIdioma() {
        System.out.println("En esta opción podrá buscar libros escritos en un determinado idioma. \n" +
                "¿En qué idioma desea buscar?");
        String idiomaStr = teclado.nextLine().toLowerCase();
        idiomaStr = LibroService.eliminarTildes(idiomaStr);
        if ("español".equalsIgnoreCase(idiomaStr)) {
            idiomaStr = "CASTELLANO";
        }
        try {
            Idiomas idioma = Idiomas.valueOf(idiomaStr.toUpperCase());
            libroService.listarLibrosPorIdioma(idioma);
        } catch (IllegalArgumentException e) {
            System.out.println("El idioma ingresado no es válido.");
        }
    }

    // MÉTODO PARA LISTAR LIBROS DE ACUERDO A SU TÍTULO
    private void listarLibrosPorTitulo() {
        System.out.println("En esta opción podrá buscar libros registrados en la base de datos. \n" +
                "¿Qué título desea buscar?");
        String titulo = teclado.nextLine();
        List<Libro> libro = librosRepository.findByTituloContainingIgnoreCase(titulo);
        if (libro.isEmpty()) {
            System.out.println("No se encontraron libros con el título: " + titulo);
        } else {
            System.out.println("\nLIBRO ENCONTRADO:\n────────────────");
            libro.forEach(System.out::println);
        }
    }

    // MÉTODO PARA LISTAR AUTORES DE ACUERDO AL NOMBRE
    private void listarAutoresPorNombre() {
        System.out.println("En esta opción podrá buscar autores registrados en la base de datos. \n" +
                "¿El nombre o apellido de qué autor desea buscar?");
        String nombreAutor = teclado.nextLine();
        List<Autor> autores = autorService.listarAutoresPorNombre(nombreAutor);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con el nombre: " + nombreAutor);
        } else {
            System.out.println("\nAUTOR ENCONTRADO:\n────────────────");
            autores.forEach(System.out::println);
        }
    }

    // MÉTODO PARA BUSCAR LOS 5 LIBROS MÁS DESCARGADOS DE LA BASE DE DATOS
    private void buscarTop5LibrosDescargados() {
        List<Libro> libros = librosRepository.findAll();
        List<Libro> top5Libros = libroService.obtenerTop5LibrosMasDescargados(libros);
        System.out.println("\nTop 5, libros más descargados.\nCANTIDAD     TÍTULOS\nVECES        MÁS DESCARGADOS\n────────     ───────────");
        top5Libros.forEach(libro -> System.out.println(libro.getNumeroDescargas() + "        " + libro.getTitulo().toUpperCase()));
    }

    // MÉTODO PARA MOSTRAR ESTADÍSTICAS DE LA BASE DE DATOS
    private void mostrarEstadisticas() {
        estadisticasService.mostrarEstadisticas();
    }
}
