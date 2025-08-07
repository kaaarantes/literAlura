package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String name);

    @Query("SELECT a FROM Autor a WHERE a.anoMorte > :ano AND a.anoNascimento <= :ano")
    List<Autor> findByAutorVivo(String ano);

    Optional<Autor> findByNomeIgnoreCase(String nomeAutor);
}
