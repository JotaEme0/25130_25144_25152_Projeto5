import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FrameProduto extends JFrame {

    private JToolBar tbBotoes;
    private JButton btnIncluir, btnSalvar, btnExcluir, btnBuscar, btnProximo,
            btnAnterior, btnInicio, btnFinal, btnCancelar;

    private ResultSet dadosDoSelect;
    private Connection conexaoDados = null;

    private JTextField txtIdProduto, txtNomeProduto, txtPreco, txtDescricao,
            txtQtdeProduto, txtImagemProduto, txtIdCategoria;

    private JTable tabProduto;

    public FrameProduto() throws SQLException {
        setTitle("Manutenção de Produtos - Da Roça");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== BARRA DE BOTÕES =====
        tbBotoes = new JToolBar();
        tbBotoes.setLayout(new FlowLayout());
        btnInicio = new JButton("Início");
        btnAnterior = new JButton("Anterior");
        btnProximo = new JButton("Próximo");
        btnFinal = new JButton("Final");
        btnBuscar = new JButton("Buscar");
        btnIncluir = new JButton("Incluir");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnCancelar = new JButton("Cancelar");

        tbBotoes.add(btnInicio);
        tbBotoes.add(btnAnterior);
        tbBotoes.add(btnProximo);
        tbBotoes.add(btnFinal);
        tbBotoes.addSeparator();
        tbBotoes.add(btnBuscar);
        tbBotoes.addSeparator();
        tbBotoes.add(btnIncluir);
        tbBotoes.add(btnCancelar);
    }}
