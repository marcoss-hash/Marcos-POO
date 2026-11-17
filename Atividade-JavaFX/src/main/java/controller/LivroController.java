package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Livro;
import service.LivroService;

import java.util.Optional;

/** Controller da tela de cadastro e gerenciamento de livros. */
public class LivroController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtAno;
    @FXML private TextField txtPaginas;
    @FXML private TextField txtBusca;

    @FXML private TableView<Livro> tabelaLivros;
    @FXML private TableColumn<Livro, Long> colId;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colAutor;
    @FXML private TableColumn<Livro, Integer> colAno;
    @FXML private TableColumn<Livro, Integer> colPaginas;
    @FXML private Label lblStatus;

    private final LivroService livroService = new LivroService();
    private final ObservableList<Livro> livrosData = FXCollections.observableArrayList();
    private Long livroEmEdicaoId;

    @FXML
    private void initialize() {
        configurarColunas();
        tabelaLivros.setItems(livrosData);
        carregarDadosTabela();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colPaginas.setCellValueFactory(new PropertyValueFactory<>("paginas"));
    }

    @FXML
    private void salvarLivro() {
        try {
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            int ano = Integer.parseInt(txtAno.getText().trim());
            int paginas = Integer.parseInt(txtPaginas.getText().trim());

            boolean estavaEditando = livroEmEdicaoId != null;
            Livro livro = new Livro(titulo, autor, ano, paginas);
            livro.setId(livroEmEdicaoId);
            livroService.salvar(livro);

            carregarDadosTabela();
            limparFormulario();
            atualizarStatus(estavaEditando
                    ? "Livro atualizado com sucesso."
                    : "Livro cadastrado com sucesso.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Os dados do livro foram salvos.");
        } catch (NumberFormatException exception) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos",
                    "Os campos Ano e Páginas devem conter números inteiros.");
        } catch (IllegalArgumentException exception) {
            mostrarAlerta(Alert.AlertType.WARNING, "Verifique os dados", exception.getMessage());
        } catch (Exception exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar",
                    mensagemDoErro(exception));
        }
    }

    @FXML
    private void editarSelecionado() {
        Livro selecionado = tabelaLivros.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum livro selecionado",
                    "Selecione um livro na tabela para editar.");
            return;
        }

        livroEmEdicaoId = selecionado.getId();
        txtTitulo.setText(selecionado.getTitulo());
        txtAutor.setText(selecionado.getAutor());
        txtAno.setText(String.valueOf(selecionado.getAno()));
        txtPaginas.setText(String.valueOf(selecionado.getPaginas()));
        atualizarStatus("Editando o livro de ID " + livroEmEdicaoId + ".");
    }

    @FXML
    private void excluirSelecionado() {
        Livro selecionado = tabelaLivros.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum livro selecionado",
                    "Selecione um livro na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir '" + selecionado.getTitulo() + "'?");
        confirmacao.setContentText("Esta operação não poderá ser desfeita.");
        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            try {
                livroService.excluir(selecionado.getId());
                carregarDadosTabela();
                limparFormulario();
                atualizarStatus("Livro excluído com sucesso.");
            } catch (Exception exception) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao excluir", mensagemDoErro(exception));
            }
        }
    }

    @FXML
    private void novoLivro() {
        limparFormulario();
        atualizarStatus("Pronto para cadastrar um novo livro.");
    }

    @FXML
    private void buscarPorTitulo() {
        String titulo = txtBusca.getText();
        if (titulo == null || titulo.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Busca vazia",
                    "Digite o título que deseja procurar.");
            return;
        }

        Optional<Livro> encontrado = livroService.buscarPorTitulo(titulo);
        if (encontrado.isPresent()) {
            livrosData.setAll(encontrado.get());
            atualizarStatus("Livro encontrado: " + encontrado.get().getTitulo() + ".");
        } else {
            livrosData.clear();
            atualizarStatus("Nenhum livro encontrado com o título informado.");
        }
    }

    @FXML
    private void mostrarTodos() {
        carregarDadosTabela();
        atualizarStatus("Exibindo todos os livros cadastrados.");
    }

    private void carregarDadosTabela() {
        try {
            livrosData.setAll(livroService.listarTodos());
        } catch (Exception exception) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao carregar o acervo",
                    mensagemDoErro(exception));
        }
    }

    private void limparFormulario() {
        txtTitulo.clear();
        txtAutor.clear();
        txtAno.clear();
        txtPaginas.clear();
        livroEmEdicaoId = null;
        tabelaLivros.getSelectionModel().clearSelection();
        txtTitulo.requestFocus();
    }

    private void atualizarStatus(String mensagem) {
        if (lblStatus != null) {
            lblStatus.setText(mensagem);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private String mensagemDoErro(Exception exception) {
        String mensagem = exception.getMessage();
        return mensagem == null || mensagem.isBlank()
                ? "Ocorreu um erro inesperado. Consulte o console para mais detalhes."
                : mensagem;
    }
}
