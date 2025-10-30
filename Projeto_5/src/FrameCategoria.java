import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FrameCategoria extends JFrame {

    private JToolBar tbBotoes;
    private JButton btnIncluir, btnSalvar, btnExcluir, btnBuscar, btnProximo,
            btnAnterior, btnInicio, btnFinal, btnCancelar;

    private static ResultSet dadosDoSelect;
    private static Connection conexaoDados = null;

    private static JTextField txtIdCategoria, txtNomeCategoria;
    private static JTable tabCategoria;

    public FrameCategoria() throws SQLException {
        setTitle("Manutenção de Categorias - DaRoça");
        setSize(700, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        tbBotoes = new JToolBar();
        tbBotoes.setLayout(new FlowLayout(FlowLayout.LEFT));

        btnInicio = new JButton("Início");
        btnAnterior = new JButton("Anterior");
        btnProximo = new JButton("Próximo");
        btnFinal = new JButton("Final");
        btnBuscar = new JButton("Buscar");
        btnIncluir = new JButton("Incluir");
        btnSalvar = new JButton("Atualizar");
        btnExcluir = new JButton("Excluir");
        btnCancelar = new JButton("Cancelar");

        Dimension btnDim = new Dimension(90, 40);
        for (JButton b : new JButton[]{btnInicio, btnAnterior, btnProximo, btnFinal,
                btnBuscar, btnIncluir, btnSalvar, btnExcluir, btnCancelar}) {
            b.setPreferredSize(btnDim);
            b.setVerticalTextPosition(SwingConstants.BOTTOM);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setFocusPainted(false);
        }

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
        JPanel pnlMensagem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbMensagem = new JLabel("Mensagem:");
        pnlMensagem.add(lbMensagem);

        Container cntForm = getContentPane();
        cntForm.setLayout(new BorderLayout());
        cntForm.add(tbBotoes, BorderLayout.NORTH);
        cntForm.add(pnlGrade, BorderLayout.WEST);
        cntForm.add(pnlCampos, BorderLayout.CENTER);
        cntForm.add(pnlMensagem, BorderLayout.SOUTH);

        // tabela gráfica com colunas (apenas para visualização inicial)
        Object[][] dadosCategoria = {{0, ""}};
        String[] colunas = {"ID", "Nome Categoria"};
        tabCategoria = new JTable(dadosCategoria, colunas);
        JScrollPane barraRolagem = new JScrollPane(tabCategoria);
        barraRolagem.setPreferredSize(new Dimension(300, 150));
        pnlGrade.add(barraRolagem);

        // Campos: 2 linhas, 2 colunas
        pnlCampos.setLayout(new GridLayout(2, 2, 5, 5));
        txtIdCategoria = new JTextField();
        txtNomeCategoria = new JTextField();

        pnlCampos.add(new JLabel("ID Categoria:"));
        pnlCampos.add(txtIdCategoria);
        pnlCampos.add(new JLabel("Nome Categoria:"));
        pnlCampos.add(txtNomeCategoria);

        // Conexão e carregamento inicial
        try {
            conexaoDados = ConectaBD.getConnection();
            preencherDados();
            if (dadosDoSelect != null)
                exibirRegistro();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + e.getMessage());
        }

        // --- Navegação ---
        btnInicio.addActionListener(e -> {
            try {
                if (dadosDoSelect != null && dadosDoSelect.first()) exibirRegistro();
                else JOptionPane.showMessageDialog(this, "Sem registros!");
                testarBotoes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnAnterior.addActionListener(e -> {
            try {
                if (dadosDoSelect != null && dadosDoSelect.previous()) exibirRegistro();
                else JOptionPane.showMessageDialog(this, "Sem registros anteriores!");
                testarBotoes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnProximo.addActionListener(e -> {
            try {
                if (dadosDoSelect != null && dadosDoSelect.next()) exibirRegistro();
                else JOptionPane.showMessageDialog(this, "Sem registros à frente!");
                testarBotoes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnFinal.addActionListener(e -> {
            try {
                if (dadosDoSelect != null && dadosDoSelect.last()) exibirRegistro();
                else JOptionPane.showMessageDialog(this, "Sem registros!");
                testarBotoes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        // --- Incluir (INSERT) ---
        btnIncluir.addActionListener(e -> {
            String sql = "INSERT INTO DaRoca.Categoria (nomeCategoria) VALUES (?)";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                String nome = txtNomeCategoria.getText().trim();
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nome da categoria obrigatório!");
                    return;
                }
                ps.setString(1, nome);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Categoria incluída com sucesso!");
                preencherDados(); // recarrega ResultSet
                testarBotoesSilencioso();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao incluir: " + ex.getMessage());
            }
        });

        // --- Atualizar (UPDATE) ---
        btnSalvar.addActionListener(e -> {
            String sql = "UPDATE DaRoca.Categoria SET nomeCategoria = ? WHERE idCategoria = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdCategoria.getText().trim());
                String nome = txtNomeCategoria.getText().trim();
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nome da categoria obrigatório!");
                    return;
                }
                ps.setString(1, nome);
                ps.setInt(2, id);
                int rows = ps.executeUpdate();
                if (rows > 0) JOptionPane.showMessageDialog(this, "Atualização bem sucedida!");
                else JOptionPane.showMessageDialog(this, "ID não encontrado para atualização.");
                preencherDados();
                testarBotoesSilencioso();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
            }
        });

        // --- Excluir (DELETE) ---
        btnExcluir.addActionListener(e -> {
            String sql = "DELETE FROM DaRoca.Categoria WHERE idCategoria = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdCategoria.getText().trim());
                ps.setInt(1, id);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirma exclusão da categoria ID " + id + "?", "Confirma",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                int rows = ps.executeUpdate();
                if (rows > 0) JOptionPane.showMessageDialog(this, "Exclusão bem sucedida!");
                else JOptionPane.showMessageDialog(this, "ID não encontrado para exclusão.");
                preencherDados();
                testarBotoesSilencioso();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
            }
        });

        // --- Buscar por ID ---
        btnBuscar.addActionListener(e -> {
            String sql = "SELECT * FROM DaRoca.Categoria WHERE idCategoria = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdCategoria.getText().trim());
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtNomeCategoria.setText(rs.getString("nomeCategoria"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Categoria não encontrada!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage());
            }
        });

        // --- Cancelar (limpar campos) ---
        btnCancelar.addActionListener(e -> {
            txtIdCategoria.setText("");
            txtNomeCategoria.setText("");
        });
    } // fim construtor

    private void testarBotoesSilencioso() {
        try {
            if (dadosDoSelect != null && dadosDoSelect.first()) {
                exibirRegistro();
            }
        } catch (SQLException ex) {
            // ignora neste recarregamento silencioso
        }
    }

    private void testarBotoes() throws SQLException {
        btnInicio.setEnabled(true);
        btnAnterior.setEnabled(true);
        btnProximo.setEnabled(true);
        btnFinal.setEnabled(true);

        if (dadosDoSelect == null) {
            btnInicio.setEnabled(false);
            btnAnterior.setEnabled(false);
            btnProximo.setEnabled(false);
            btnFinal.setEnabled(false);
            return;
        }

        if (dadosDoSelect.isFirst() || dadosDoSelect.isBeforeFirst()) {
            btnInicio.setEnabled(false);
            btnAnterior.setEnabled(false);
        }

        if (dadosDoSelect.isLast() || dadosDoSelect.isAfterLast()) {
            btnProximo.setEnabled(false);
            btnFinal.setEnabled(false);
        }
    }

    private static void preencherDados() {
        String sql = "SELECT * FROM DaRoca.Categoria ORDER BY idCategoria";
        try {
            Statement comandoSQL = conexaoDados.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            dadosDoSelect = comandoSQL.executeQuery(sql);
            // não chamar exibirRegistro() aqui — let caller decide; mas manteremos para conveniência
            if (dadosDoSelect.first()) {
                exibirRegistro();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao preencher dados: " + ex.getMessage());
        }
    }

    private static void exibirRegistro() throws SQLException {
        if (dadosDoSelect == null) return;
        txtIdCategoria.setText(dadosDoSelect.getString("idCategoria"));
        txtNomeCategoria.setText(dadosDoSelect.getString("nomeCategoria"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FrameCategoria form = new FrameCategoria();
                form.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            if (conexaoDados != null && !conexaoDados.isClosed())
                                conexaoDados.close();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.exit(0);
                    }
                });
                form.pack();
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao iniciar formulário: " + e.getMessage());
            }
        });
    }
}
