package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    @Query("SELECT l FROM Livro l JOIN l.idioma i WHERE i.abreviacao ILIKE %:linguagem")
    List<Livro> findByIdioma(String linguagem);

    List<Livro> findTop10ByOrderByNumeroDownloadsDesc();
}
