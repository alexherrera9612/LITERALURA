package com.aluracursos.literalura.model;

import jakarta.persistence.*;

/**
 * Representa un libro en el sistema.
 */
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @Enumerated(EnumType.STRING)
    private Idiomas idioma;

    private Integer numeroDescargas;

    /**
     * Constructor por defecto.
     */
    public Libro() {
    }

    /**
     * Constructor que inicializa un libro con datos desde un objeto DatosLibros.
     *
     * @param datosLibros objeto con datos del libro
     */
    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        this.autor = new Autor(datosLibros.autores().get(0));
        this.idioma = datosLibros.idiomas().get(0);
        this.numeroDescargas = datosLibros.numeroDescargas() != null ? datosLibros.numeroDescargas() : 0;
    }

    /**
     * Constructor completo que inicializa todos los campos.
     *
     * @param id              identificador del libro
     * @param titulo          título del libro
     * @param autor           autor del libro
     * @param idioma          idioma del libro
     * @param numeroDescargas número de descargas del libro
     */
    public Libro(Long id, String titulo, Autor autor, Idiomas idioma, Integer numeroDescargas) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTitulo: ").append(titulo).append("\n")
                .append("Autor: ").append(autor.toString()).append("\n")
                .append("Idiomas: ").append(idioma.toString()).append("\n")
                .append("Número de descargas: ").append(numeroDescargas)
                .append("\n\n────────────────────────────────────────────────────────────────────────────────────────────────────────");
        return sb.toString();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idiomas getIdioma() {
        return idioma;
    }

    public void setIdioma(Idiomas idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
