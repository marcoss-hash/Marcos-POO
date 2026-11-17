package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import persistence.JpaUtil;
import model.Livro;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Camada de serviço responsável pelas operações de persistência dos livros.
 * A interface JavaFX não acessa o banco diretamente: ela chama esta classe.
 */
public class LivroService {

    private final EntityManagerFactory entityManagerFactory;

    /** Construtor utilizado pela aplicação. */
    public LivroService() {
        this(JpaUtil.getEntityManagerFactory());
    }

    /** Construtor com fábrica externa, útil também para os testes automatizados. */
    public LivroService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Objects.requireNonNull(
                entityManagerFactory, "A EntityManagerFactory não pode ser nula.");
    }

    /** Cadastra um livro novo ou atualiza um livro que já possui ID. */
    public Livro salvar(Livro livro) {
        validarLivro(livro);

        livro.setTitulo(livro.getTitulo().trim());
        livro.setAutor(livro.getAutor().trim());

        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Livro resultado;
            if (livro.getId() == null) {
                em.persist(livro);
                resultado = livro;
            } else {
                Livro existente = em.find(Livro.class, livro.getId());
                if (existente == null) {
                    throw new IllegalArgumentException(
                            "Não existe livro cadastrado com o ID " + livro.getId() + ".");
                }
                resultado = em.merge(livro);
            }

            transaction.commit();
            return resultado;
        } catch (RuntimeException exception) {
            rollbackSeNecessario(transaction);
            throw exception;
        } finally {
            em.close();
        }
    }

    /** Retorna todos os livros, ordenados pelo ID de cadastro. */
    public List<Livro> listarTodos() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Livro l ORDER BY l.id", Livro.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Procura um livro pelo título, ignorando diferenças entre maiúsculas e minúsculas. */
    public Optional<Livro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            List<Livro> encontrados = em.createQuery(
                            "SELECT l FROM Livro l WHERE LOWER(l.titulo) = LOWER(:titulo)", Livro.class)
                    .setParameter("titulo", titulo.trim())
                    .setMaxResults(1)
                    .getResultList();
            return encontrados.stream().findFirst();
        } finally {
            em.close();
        }
    }

    /** Busca um livro pelo ID. */
    public Optional<Livro> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Livro.class, id));
        } finally {
            em.close();
        }
    }

    /** Exclui um livro. Lança exceção caso o ID não corresponda a um registro. */
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um livro antes de excluir.");
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Livro livro = em.find(Livro.class, id);
            if (livro == null) {
                throw new IllegalArgumentException(
                        "Não existe livro cadastrado com o ID " + id + ".");
            }
            em.remove(livro);
            transaction.commit();
        } catch (RuntimeException exception) {
            rollbackSeNecessario(transaction);
            throw exception;
        } finally {
            em.close();
        }
    }

    private void validarLivro(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Os dados do livro não foram informados.");
        }
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título do livro.");
        }
        if (livro.getAutor() == null || livro.getAutor().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do autor.");
        }
        if (livro.getAno() <= 0) {
            throw new IllegalArgumentException("O ano deve ser um número inteiro positivo.");
        }
        if (livro.getPaginas() <= 0) {
            throw new IllegalArgumentException("A quantidade de páginas deve ser maior que zero.");
        }
    }

    private void rollbackSeNecessario(EntityTransaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}
