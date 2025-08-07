package br.com.alura.literAlura.principal;

import br.com.alura.literAlura.model.*;
import br.com.alura.literAlura.repository.AutorRepository;
import br.com.alura.literAlura.repository.IdiomaRepository;
import br.com.alura.literAlura.repository.LivroRepository;
import br.com.alura.literAlura.service.ConsumoAPI;
import br.com.alura.literAlura.service.ConverteDados;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private LivroRepository livroRepository;
    private AutorRepository autorRepository;
    private IdiomaRepository idiomaRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository, IdiomaRepository idiomaRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.idiomaRepository = idiomaRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 9) {
            var menu = """
                    1 - Buscar Livro
                    2 - Listar Livros
                    3 - Listar Autores
                    4 - Listar Autores Vivos
                    5 - Listar Livros Por Idioma
                    6 - Top 10 Livros

                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarLivro();
                    break;
                case 2:
                    listarLivros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 6:
                    top10Livros();
                    break;
                case 9:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }



    private void cadastrarLivro() {
        System.out.println("Digite o nome do livro");
        var nomeLivro = scanner.nextLine();
        String nomeLivroCodificado = URLEncoder.encode(nomeLivro, StandardCharsets.UTF_8);
        var json = consumoAPI.obterDados(URL_BASE + nomeLivroCodificado);
        var resposta = conversor.obterDados(json, RespostaLivros.class);

        if (resposta.results().isEmpty() || resposta.results() == null) {
            System.out.println("Livro não encontrado.");
            return;
        }

        DadosLivros dados = resposta.results().get(0);

        if (dados.autor().isEmpty()) {
            System.out.println("Livro sem autor definido. Cancelando operação.");
            return;
        }

        DadosAutor dadosAutor = dados.autor().get(0);
        Optional<Autor> autorArmazenado = autorRepository.findByNome(dadosAutor.nome());

        Autor autor;
        if (autorArmazenado.isPresent()) {
            autor = autorArmazenado.get();
        } else {
            autor = new Autor();
            autor.setNome(dadosAutor.nome());
            autor.setAnoNascimento(dadosAutor.anoNascimento());
            autor.setAnoMorte(dadosAutor.anoMorte());
            autor = autorRepository.save(autor);
        }

        String abreviacaoIdioma = dados.idioma().isEmpty() ? "desconhecido" : dados.idioma().get(0);
        Idioma idioma = idiomaRepository.findByAbreviacao(abreviacaoIdioma);

        if (idioma == null) {
            idioma = new Idioma();
            idioma.setAbreviacao(abreviacaoIdioma);
            idioma = idiomaRepository.save(idioma);
        }

        Livro livro = new Livro();
        livro.setTitulo(dados.titulo());
        livro.setAutor(autor);
        livro.setIdioma(idioma);
        livro.setNumeroDownloads(dados.numeroDownloads());

        livroRepository.save(livro);

        System.out.println("Livro salvo com sucesso!");
    }

    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        livros.forEach(System.out::println);
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    private void listarAutoresVivos() {
        System.out.print("Digite o ano ");
        var ano = scanner.nextLine();

        List<Autor> autoresVivos = autorRepository.findByAutorVivo(ano);

        if (autoresVivos.size() == 0){
            System.out.println("Não há autores vivos no ano de " + ano);
        } else {
            System.out.println("Autores vivos em " + ano);
            autoresVivos.forEach(System.out::println);
        }

    }

    private void listarLivrosPorIdioma() {
        System.out.print("""
                Selecione o idioma desejado:
                Digite "en" para inglês e "pt" para português;
                """);
        String linguagem = scanner.nextLine().toLowerCase();

        List<Livro> livros = livroRepository.findByIdioma(linguagem);

        if (livros.isEmpty()) {
            System.out.println("Não há livros para o idioma " + linguagem);
        } else {
            System.out.println("\nLivros com o idioma " + linguagem);
            livros.forEach(System.out::println);
        }
    }

    private void top10Livros() {
        List<Livro> topLivro = livroRepository.findTop10ByOrderByNumeroDownloadsDesc();
        topLivro.forEach(l -> System.out.println(l.getTitulo() + " Número de downloads: " + l.getNumeroDownloads()));
    }
}
