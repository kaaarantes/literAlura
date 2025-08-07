package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdiomaRepository extends JpaRepository<Idioma, Long> {
    Idioma findByAbreviacao(String abreviacaoIdioma);
}
