package persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Centraliza a criação e o encerramento da fábrica de EntityManager. */
public final class JpaUtil {

    private static final String PERSISTENCE_UNIT = "BibliotecaPU";
    private static EntityManagerFactory entityManagerFactory;

    private JpaUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            criarDiretorioDoBanco();
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return entityManagerFactory;
    }

    public static synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        entityManagerFactory = null;
    }

    private static void criarDiretorioDoBanco() {
        try {
            Files.createDirectories(Path.of("data"));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível criar a pasta 'data' para o banco de dados.", exception);
        }
    }
}
