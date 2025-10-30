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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        tbBotoes = new JToolBar();
        tbBotoes.setLayout(new FlowLayout());

        // Criação dos botões (pode reaproveitar o código do FrameDepto)
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

        Object[][] dadosProduto = {{0, "", "", "", "", "", ""}};
        String[] colunas = {"ID", "Nome", "Preço", "Descrição", "Qtd", "Imagem", "Categoria"};
        JTable tabProduto = new JTable(dadosProduto, colunas);
        JScrollPane barraRolagem = new JScrollPane(tabProduto);
        pnlGrade.add(barraRolagem);

        // 7 linhas e 2 colunas
        pnlCampos.setLayout(new GridLayout(7, 2));
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

        try {
            conexaoDados = ConectaBD.getConnection();
            preencherDados();
            if (dadosDoSelect != null)
                exibirRegistro();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Eventos dos botões (mesma lógica do FrameDepto)
        btnIncluir.addActionListener(e -> {
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
                JOptionPane.showMessageDialog(null, "Produto incluído com sucesso!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    private static void preencherDados() {
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

    private static void exibirRegistro() throws SQLException {
        txtIdProduto.setText(dadosDoSelect.getString("idProduto"));
        txtNomeProduto.setText(dadosDoSelect.getString("nomeProduto"));
        txtPreco.setText(dadosDoSelect.getString("preco"));
        txtDescricao.setText(dadosDoSelect.getString("descricao"));
        txtQtdeProduto.setText(dadosDoSelect.getString("QtdeProduto"));
        txtImagemProduto.setText(dadosDoSelect.getString("ImagemProduto"));
        txtIdCategoria.setText(dadosDoSelect.getString("idCategoria"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FrameProduto form = new FrameProduto();
                form.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            conexaoDados.close();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.exit(0);
                    }
                });
                form.pack();
                form.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
