import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FrameProduto extends JFrame {

    private JToolBar tbBotoes;
    private JButton btnIncluir, btnSalvar, btnExcluir, btnBuscar, btnProximo,
            btnAnterior, btnInicio, btnFinal, btnCancelar;

    private static ResultSet dadosDoSelect;
    private static Connection conexaoDados = null;

    private static JTextField txtIdProduto, txtNomeProduto, txtPreco, txtDescricao,
            txtQtdeProduto, txtImagemProduto, txtIdCategoria;

    private static JTable tabProduto;

    public FrameProduto() throws SQLException {
        setTitle("Manutenção de Produtos - Da Roça");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // fecha só o frame, não o app todo
        setLocationRelativeTo(null); // centraliza

        // ===== TOOLBAR =====
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
        tbBotoes.add(btnSalvar);
        tbBotoes.add(btnExcluir);
        tbBotoes.add(btnCancelar);
        tbBotoes.setRollover(true);

        // ===== PAINÉIS =====
        JPanel pnlGrade = new JPanel();
        JPanel pnlCampos = new JPanel();
        JPanel pnlMensagem = new JPanel();

        JLabel lbMensagem = new JLabel("Mensagem:");
        pnlMensagem.add(lbMensagem);
        pnlMensagem.setLayout(new FlowLayout(FlowLayout.LEFT));

        Container cntForm = getContentPane();
        cntForm.setLayout(new BorderLayout());
        cntForm.add(tbBotoes, BorderLayout.NORTH);
        cntForm.add(pnlGrade, BorderLayout.WEST);
        cntForm.add(pnlCampos, BorderLayout.CENTER);
        cntForm.add(pnlMensagem, BorderLayout.SOUTH);

        // ===== TABELA =====
        Object[][] dadosProduto = {{0, "", "", "", "", "", ""}};
        String[] colunas = {"ID", "Nome", "Preço", "Descrição", "Qtd", "Imagem", "Categoria"};
        tabProduto = new JTable(dadosProduto, colunas); // <-- corrigido (sem redeclarar)
        JScrollPane barraRolagem = new JScrollPane(tabProduto);
        pnlGrade.add(barraRolagem);

        // ===== CAMPOS =====
        pnlCampos.setLayout(new GridLayout(7, 2, 5, 5));

        txtIdProduto = new JTextField();
        txtNomeProduto = new JTextField();
        txtPreco = new JTextField();
        txtDescricao = new JTextField();
        txtQtdeProduto = new JTextField();
        txtImagemProduto = new JTextField();
        txtIdCategoria = new JTextField();

        pnlCampos.add(new JLabel("ID Produto:"));
        pnlCampos.add(txtIdProduto);
        pnlCampos.add(new JLabel("Nome Produto:"));
        pnlCampos.add(txtNomeProduto);
        pnlCampos.add(new JLabel("Preço:"));
        pnlCampos.add(txtPreco);
        pnlCampos.add(new JLabel("Descrição:"));
        pnlCampos.add(txtDescricao);
        pnlCampos.add(new JLabel("Quantidade:"));
        pnlCampos.add(txtQtdeProduto);
        pnlCampos.add(new JLabel("Imagem:"));
        pnlCampos.add(txtImagemProduto);
        pnlCampos.add(new JLabel("ID Categoria:"));
        pnlCampos.add(txtIdCategoria);

        // ===== CONEXÃO =====
        try {
            conexaoDados = ConectaBD.getConnection();
            preencherDados();
            if (dadosDoSelect != null) {
                exibirRegistro();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
        }

        // ===== BOTÕES =====
        btnIncluir.addActionListener(e -> incluirProduto());
        btnProximo.addActionListener(e -> proximoRegistro());
        btnAnterior.addActionListener(e -> registroAnterior());
        btnInicio.addActionListener(e -> primeiroRegistro());
        btnFinal.addActionListener(e -> ultimoRegistro());
        btnExcluir.addActionListener(e -> excluirProduto());
    }

    // ====== MÉTODOS AUXILIARES ======

    private void incluirProduto() {
        String sql = "INSERT INTO DaRoca.Produto (nomeProduto, preco, descricao, QtdeProduto, ImagemProduto, idCategoria) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement comandoSQL = conexaoDados.prepareStatement(sql);
            comandoSQL.setString(1, txtNomeProduto.getText());
            comandoSQL.setBigDecimal(2, new java.math.BigDecimal(txtPreco.getText()));
            comandoSQL.setString(3, txtDescricao.getText());
            comandoSQL.setInt(4, Integer.parseInt(txtQtdeProduto.getText()));
            comandoSQL.setString(5, txtImagemProduto.getText());
            comandoSQL.setInt(6, Integer.parseInt(txtIdCategoria.getText()));
            comandoSQL.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produto incluído com sucesso!");
            preencherDados();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao incluir produto: " + ex.getMessage());
        }
    }

    private void excluirProduto() {
        if (txtIdProduto.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o produto?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM DaRoca.Produto WHERE idProduto = ?";
                PreparedStatement comandoSQL = conexaoDados.prepareStatement(sql);
                comandoSQL.setInt(1, Integer.parseInt(txtIdProduto.getText()));
                comandoSQL.executeUpdate();
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
                preencherDados();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir produto: " + ex.getMessage());
            }
        }
    }

    private void preencherDados() {
        String sql = "SELECT * FROM DaRoca.Produto ORDER BY idProduto";
        try {
            Statement comandoSQL = conexaoDados.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            dadosDoSelect = comandoSQL.executeQuery(sql);
            if (dadosDoSelect.first()) exibirRegistro();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void exibirRegistro() throws SQLException {
        txtIdProduto.setText(dadosDoSelect.getString("idProduto"));
        txtNomeProduto.setText(dadosDoSelect.getString("nomeProduto"));
        txtPreco.setText(dadosDoSelect.getString("preco"));
        txtDescricao.setText(dadosDoSelect.getString("descricao"));
        txtQtdeProduto.setText(dadosDoSelect.getString("QtdeProduto"));
        txtImagemProduto.setText(dadosDoSelect.getString("ImagemProduto"));
        txtIdCategoria.setText(dadosDoSelect.getString("idCategoria"));
    }

    private void proximoRegistro() {
        try {
            if (!dadosDoSelect.isLast()) {
                dadosDoSelect.next();
                exibirRegistro();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registroAnterior() {
        try {
            if (!dadosDoSelect.isFirst()) {
                dadosDoSelect.previous();
                exibirRegistro();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void primeiroRegistro() {
        try {
            dadosDoSelect.first();
            exibirRegistro();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ultimoRegistro() {
        try {
            dadosDoSelect.last();
            exibirRegistro();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FrameProduto form = new FrameProduto();
                form.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao abrir tela: " + e.getMessage());
            }
        });
    }
}
