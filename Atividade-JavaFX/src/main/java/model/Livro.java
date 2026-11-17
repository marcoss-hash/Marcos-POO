package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade Livro. Herda os dados comuns de um item do acervo.
 */
@Entity
@Table(name = "livros")
public class Livro extends ItemAcervo {

    @Column(name = "ano", nullable = false)
    private int ano;

    @Column(name = "paginas", nullable = false)
    private int paginas;

    /** Construtor vazio exigido pelo JPA. */
    public Livro() {
        super();
    }

    public Livro(String titulo, String autor, int ano, int paginas) {
        super(titulo, autor);
        this.ano = ano;
        this.paginas = paginas;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + '\'' +
                ", autor='" + getAutor() + '\'' +
                ", ano=" + ano +
                ", paginas=" + paginas +
                '}';
    }
}
