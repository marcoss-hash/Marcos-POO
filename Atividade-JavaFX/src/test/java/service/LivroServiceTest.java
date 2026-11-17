package service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Livro;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LivroServiceTest {

    private EntityManagerFactory entityManagerFactory;
    private LivroService livroService;

    @BeforeEach
    void configurar() {
        entityManagerFactory = Persistence.createEntityManagerFactory("BibliotecaTestPU");
        livroService = new LivroService(entityManagerFactory);
    }

    @AfterEach
    void encerrar() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @Test
    void deveCadastrarEListarLivro() {
        Livro salvo = livroService.salvar(new Livro("O Hobbit", "J. R. R. Tolkien", 1937, 310));

        List<Livro> livros = livroService.listarTodos();

        assertNotNull(salvo.getId());
        assertEquals(1, livros.size());
        assertEquals("O Hobbit", livros.get(0).getTitulo());
        assertEquals("J. R. R. Tolkien", livros.get(0).getAutor());
    }

    @Test
    void deveBuscarLivroPorTituloSemDiferenciarMaiusculas() {
        livroService.salvar(new Livro("Dom Casmurro", "Machado de Assis", 1899, 256));

        Optional<Livro> encontrado = livroService.buscarPorTitulo("dOm cAsMuRrO");

        assertTrue(encontrado.isPresent());
        assertEquals("Dom Casmurro", encontrado.get().getTitulo());
    }

    @Test
    void deveAtualizarLivroExistente() {
        Livro salvo = livroService.salvar(new Livro("Título antigo", "Autora", 2000, 100));
        Livro atualizado = new Livro("Título novo", "Autora", 2001, 150);
        atualizado.setId(salvo.getId());

        livroService.salvar(atualizado);

        Livro resultado = livroService.buscarPorId(salvo.getId()).orElseThrow();
        assertEquals("Título novo", resultado.getTitulo());
        assertEquals(2001, resultado.getAno());
        assertEquals(150, resultado.getPaginas());
    }

    @Test
    void deveExcluirLivroExistente() {
        Livro salvo = livroService.salvar(new Livro("Livro para excluir", "Autor", 2020, 120));

        livroService.excluir(salvo.getId());

        assertTrue(livroService.buscarPorId(salvo.getId()).isEmpty());
        assertTrue(livroService.listarTodos().isEmpty());
    }

    @Test
    void naoDeveSalvarLivroComTituloVazio() {
        Livro livro = new Livro("   ", "Autor", 2020, 100);

        assertThrows(IllegalArgumentException.class, () -> livroService.salvar(livro));
        assertTrue(livroService.listarTodos().isEmpty());
    }

    @Test
    void naoDeveSalvarLivroComAnoOuPaginasInvalidos() {
        Livro anoInvalido = new Livro("Livro", "Autor", 0, 100);
        Livro paginasInvalidas = new Livro("Livro", "Autor", 2020, 0);

        assertThrows(IllegalArgumentException.class, () -> livroService.salvar(anoInvalido));
        assertThrows(IllegalArgumentException.class, () -> livroService.salvar(paginasInvalidas));
        assertTrue(livroService.listarTodos().isEmpty());
    }

    @Test
    void deveRecusarAtualizacaoDeIdInexistente() {
        Livro livro = new Livro("Livro", "Autor", 2020, 100);
        livro.setId(999L);

        assertThrows(IllegalArgumentException.class, () -> livroService.salvar(livro));
    }
}
