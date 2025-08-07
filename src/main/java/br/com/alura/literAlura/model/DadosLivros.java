package br.com.alura.literAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivros(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DadosAutor> autor,
        @JsonAlias("languages") List<String>  idioma,
        @JsonAlias("download_count") Integer numeroDownloads) {}

