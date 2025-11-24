import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class FrameProduto extends JFrame {

    private JToolBar tbBotoes;
    private JButton btnIncluir, btnSalvar, btnExcluir, btnBuscar, btnProximo,
            btnAnterior, btnInicio, btnFinal, btnCancelar;

    private ResultSet dadosDoSelect;
    private Connection conexaoDados = null;

    private JTextField txtIdProduto, txtNomeProduto, txtPreco, txtDescricao,
            txtQtdeProduto, txtImagemProduto;
    private JComboBox<String> comboCategoria;
    private JTable tabProduto;

    public FrameProduto() {
        setTitle("Manutenção de Produtos - Da Roça");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tbBotoes = new JToolBar();
        tbBotoes.setLayout(new FlowLayout(FlowLayout.LEFT));
        btnInicio = new JButton("Início");
        btnAnterior = new JButton("Anterior");
        btnProximo = new JButton("Próximo");
        btnFinal = new JButton("Final");
        btnBuscar = new JButton("Buscar");
        btnIncluir = new JButton("Incluir");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");
        btnCancelar = new JButton("Cancelar");

        Dimension btnDim = new Dimension(90, 35);
        for (JButton b : new JButton[]{btnInicio, btnAnterior, btnProximo, btnFinal,
                btnBuscar, btnIncluir, btnSalvar, btnExcluir, btnCancelar}) {
            b.setPreferredSize(btnDim);
            b.setFocusPainted(false);
            tbBotoes.add(b);
        }
        tbBotoes.setRollover(true);

        Container cntForm = getContentPane();
        cntForm.setLayout(new BorderLayout());
        cntForm.add(tbBotoes, BorderLayout.NORTH);


        tabProduto = new JTable();
        JScrollPane barraRolagem = new JScrollPane(tabProduto);
        barraRolagem.setPreferredSize(new Dimension(450, 300));
        cntForm.add(barraRolagem, BorderLayout.WEST);

        // ---- Center: campos ----
        JPanel pnlCampos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        txtIdProduto = new JTextField();
        txtNomeProduto = new JTextField();
        txtPreco = new JTextField();
        txtDescricao = new JTextField();
        txtQtdeProduto = new JTextField();
        txtImagemProduto = new JTextField();
        comboCategoria = new JComboBox<>();

        int row = 0;
        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("ID Produto:"), c);
        c.gridx = 1; pnlCampos.add(txtIdProduto, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Nome Produto:"), c);
        c.gridx = 1; pnlCampos.add(txtNomeProduto, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Preço:"), c);
        c.gridx = 1; pnlCampos.add(txtPreco, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Descrição:"), c);
        c.gridx = 1; pnlCampos.add(txtDescricao, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Qtde Produto:"), c);
        c.gridx = 1; pnlCampos.add(txtQtdeProduto, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Imagem (caminho):"), c);
        c.gridx = 1; pnlCampos.add(txtImagemProduto, c); row++;

        c.gridx = 0; c.gridy = row; pnlCampos.add(new JLabel("Categoria:"), c);
        c.gridx = 1; pnlCampos.add(comboCategoria, c); row++;

        cntForm.add(pnlCampos, BorderLayout.CENTER);

        // ---- South: mensagem ----
        JPanel pnlMensagem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbMensagem = new JLabel("Mensagem:");
        pnlMensagem.add(lbMensagem);
        cntForm.add(pnlMensagem, BorderLayout.SOUTH);

        // Conexão e carregamento inicial
        try {
            conexaoDados = ConectaBD.getConnection();
            carregarComboCategorias();
            preencherDados(); // carrega ResultSet e tabela
            if (dadosDoSelect != null) exibirRegistro();
            testarBotoesSilencioso();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage());
        }

        // ---------- Ações dos botões ----------
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

        btnIncluir.addActionListener(e -> {
            String sql = "INSERT INTO DaRoca.Produto (nomeProduto, preco, descricao, QtdeProduto, imagemProduto, idCategoria) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                String nome = txtNomeProduto.getText().trim();
                String precoStr = txtPreco.getText().trim();
                String desc = txtDescricao.getText().trim();
                String qtdeStr = txtQtdeProduto.getText().trim();
                String img = txtImagemProduto.getText().trim();
                int idCat = obterIdCategoriaSelecionada();

                if (nome.isEmpty()) { JOptionPane.showMessageDialog(this, "Nome do produto obrigatório!"); return; }
                double preco = precoStr.isEmpty()? 0.0 : Double.parseDouble(precoStr);
                int qtde = qtdeStr.isEmpty()? 0 : Integer.parseInt(qtdeStr);

                ps.setString(1, nome);
                ps.setDouble(2, preco); // money aceita double
                ps.setString(3, desc);
                ps.setInt(4, qtde);
                ps.setString(5, img);
                if (idCat == -1) ps.setNull(6, Types.INTEGER); else ps.setInt(6, idCat);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Produto incluído com sucesso!");
                preencherDados();
                testarBotoesSilencioso();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro em número: " + ex.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao incluir: " + ex.getMessage());
            }
        });

        btnSalvar.addActionListener(e -> {
            String sql = "UPDATE DaRoca.Produto SET nomeProduto = ?, preco = ?, descricao = ?, QtdeProduto = ?, imagemProduto = ?, idCategoria = ? WHERE idProduto = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdProduto.getText().trim());
                String nome = txtNomeProduto.getText().trim();
                double preco = txtPreco.getText().trim().isEmpty()? 0.0 : Double.parseDouble(txtPreco.getText().trim());
                String desc = txtDescricao.getText().trim();
                int qtde = txtQtdeProduto.getText().trim().isEmpty()? 0 : Integer.parseInt(txtQtdeProduto.getText().trim());
                String img = txtImagemProduto.getText().trim();
                int idCat = obterIdCategoriaSelecionada();

                if (nome.isEmpty()) { JOptionPane.showMessageDialog(this, "Nome do produto obrigatório!"); return; }

                ps.setString(1, nome);
                ps.setDouble(2, preco);
                ps.setString(3, desc);
                ps.setInt(4, qtde);
                ps.setString(5, img);
                if (idCat == -1) ps.setNull(6, Types.INTEGER); else ps.setInt(6, idCat);
                ps.setInt(7, id);

                int rows = ps.executeUpdate();
                if (rows > 0) JOptionPane.showMessageDialog(this, "Atualização bem sucedida!");
                else JOptionPane.showMessageDialog(this, "ID não encontrado para atualização.");
                preencherDados();
                testarBotoesSilencioso();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido ou campo numérico inválido!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
            String sql = "DELETE FROM DaRoca.Produto WHERE idProduto = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdProduto.getText().trim());
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirma exclusão do produto ID " + id + "?", "Confirma",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                ps.setInt(1, id);
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

        btnBuscar.addActionListener(e -> {
            String sql = "SELECT p.*, c.nomeCategoria FROM DaRoca.Produto p LEFT JOIN DaRoca.Categoria c ON p.idCategoria = c.idCategoria WHERE p.idProduto = ?";
            try (PreparedStatement ps = conexaoDados.prepareStatement(sql)) {
                int id = Integer.parseInt(txtIdProduto.getText().trim());
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtNomeProduto.setText(rs.getString("nomeProduto"));
                        txtPreco.setText(rs.getString("preco"));
                        txtDescricao.setText(rs.getString("descricao"));
                        txtQtdeProduto.setText(rs.getString("QtdeProduto"));
                        txtImagemProduto.setText(rs.getString("imagemProduto"));
                        int idCat = rs.getInt("idCategoria");
                        selecionarCategoriaNoCombo(idCat);
                    } else {
                        JOptionPane.showMessageDialog(this, "Produto não encontrado!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage());
            }
            try { preencherDados(); } catch (Exception ignored) {}
        });

        btnCancelar.addActionListener(e -> {
            limparCampos();
        });

        // Sincroniza seleção na tabela com os campos (clique)
        tabProduto.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linha = tabProduto.getSelectedRow();
                if (linha >= 0) {
                    DefaultTableModel model = (DefaultTableModel) tabProduto.getModel();
                    txtIdProduto.setText(String.valueOf(model.getValueAt(linha, 0)));
                    txtNomeProduto.setText(String.valueOf(model.getValueAt(linha, 1)));
                    txtPreco.setText(String.valueOf(model.getValueAt(linha, 2)));
                    txtDescricao.setText(String.valueOf(model.getValueAt(linha, 3)));
                    txtQtdeProduto.setText(String.valueOf(model.getValueAt(linha, 4)));
                    txtImagemProduto.setText(String.valueOf(model.getValueAt(linha, 5)));
                    Object catIdObj = model.getValueAt(linha, 6);
                    int idCat = catIdObj == null ? -1 : Integer.parseInt(catIdObj.toString());
                    selecionarCategoriaNoCombo(idCat);
                }
            }
        });

        // Fecha conexão ao fechar a janela
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (conexaoDados != null && !conexaoDados.isClosed()) conexaoDados.close();
                } catch (SQLException ex) {
                    // ignora
                }
            }
        });
    } // fim construtor

    private void limparCampos() {
        txtIdProduto.setText("");
        txtNomeProduto.setText("");
        txtPreco.setText("");
        txtDescricao.setText("");
        txtQtdeProduto.setText("");
        txtImagemProduto.setText("");
        comboCategoria.setSelectedIndex(-1);
    }

    private int obterIdCategoriaSelecionada() {
        Object sel = comboCategoria.getSelectedItem();
        if (sel == null) return -1;
        String s = sel.toString();
        // formato esperado: "3 - Bebidas"
        String[] parts = s.split(" - ", 2);
        try {
            return Integer.parseInt(parts[0].trim());
        } catch (Exception ex) {
            return -1;
        }
    }

    private void carregarComboCategorias() {
        comboCategoria.removeAllItems();
        comboCategoria.addItem(""); // opção vazia
        String sql = "SELECT idCategoria, nomeCategoria FROM DaRoca.Categoria ORDER BY idCategoria";
        try (Statement st = conexaoDados.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String item = rs.getInt("idCategoria") + " - " + rs.getString("nomeCategoria");
                comboCategoria.addItem(item);
            }
            comboCategoria.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + ex.getMessage());
        }
    }

    private void preencherDados() {
        String sql = "SELECT p.idProduto, p.nomeProduto, p.preco, p.descricao, p.QtdeProduto, p.imagemProduto, p.idCategoria, c.nomeCategoria " +
                "FROM DaRoca.Produto p LEFT JOIN DaRoca.Categoria c ON p.idCategoria = c.idCategoria ORDER BY p.idProduto";
        try {
            Statement comandoSQL = conexaoDados.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            dadosDoSelect = comandoSQL.executeQuery(sql);

            // Preencher JTable com DefaultTableModel
            Vector<String> colNames = new Vector<>();
            colNames.add("ID");
            colNames.add("Nome");
            colNames.add("Preco");
            colNames.add("Descricao");
            colNames.add("Qtde");
            colNames.add("Imagem");
            colNames.add("idCategoria");
            // opcional: mostrar nomeCategoria também
            colNames.add("Categoria");

            Vector<Vector<Object>> rows = new Vector<>();
            while (dadosDoSelect.next()) {
                Vector<Object> row = new Vector<>();
                row.add(dadosDoSelect.getInt("idProduto"));
                row.add(dadosDoSelect.getString("nomeProduto"));
                row.add(dadosDoSelect.getString("preco"));
                row.add(dadosDoSelect.getString("descricao"));
                row.add(dadosDoSelect.getInt("QtdeProduto"));
                row.add(dadosDoSelect.getString("imagemProduto"));
                int idCat = dadosDoSelect.getInt("idCategoria");
                if (dadosDoSelect.wasNull()) row.add(null); else row.add(idCat);
                row.add(dadosDoSelect.getString("nomeCategoria"));
                rows.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(rows, colNames) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            tabProduto.setModel(model);
            // reposicionar cursor do ResultSet para antes do primeiro para navegação
            if (dadosDoSelect.getType() != ResultSet.TYPE_FORWARD_ONLY) {
                dadosDoSelect.beforeFirst();
                if (dadosDoSelect.next()) {
                    // posiciona no primeiro para exibição
                    exibirRegistro();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao preencher dados: " + ex.getMessage());
        }
    }

    private void exibirRegistro() throws SQLException {
        if (dadosDoSelect == null || dadosDoSelect.isAfterLast() || dadosDoSelect.isBeforeFirst()) return;
        txtIdProduto.setText(dadosDoSelect.getString("idProduto"));
        txtNomeProduto.setText(dadosDoSelect.getString("nomeProduto"));
        txtPreco.setText(dadosDoSelect.getString("preco"));
        txtDescricao.setText(dadosDoSelect.getString("descricao"));
        txtQtdeProduto.setText(dadosDoSelect.getString("QtdeProduto"));
        txtImagemProduto.setText(dadosDoSelect.getString("imagemProduto"));
        int idCat = dadosDoSelect.getInt("idCategoria");
        if (dadosDoSelect.wasNull()) selecionarCategoriaNoCombo(-1);
        else selecionarCategoriaNoCombo(idCat);
    }

    private void selecionarCategoriaNoCombo(int idCategoria) {
        if (idCategoria == -1) {
            comboCategoria.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            String item = comboCategoria.getItemAt(i);
            if (item == null) continue;
            String[] parts = item.split(" - ", 2);
            try {
                int id = Integer.parseInt(parts[0].trim());
                if (id == idCategoria) {
                    comboCategoria.setSelectedIndex(i);
                    return;
                }
            } catch (Exception ignored) {}
        }
        // se não encontrou, deixa em branco
        comboCategoria.setSelectedIndex(-1);
    }

    private void testarBotoesSilencioso() {
        try {
            if (dadosDoSelect != null && dadosDoSelect.first()) {
                exibirRegistro();
            }
        } catch (SQLException ex) {
            // ignora
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FrameProduto form = new FrameProduto();
                form.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao iniciar FrameProduto: " + e.getMessage());
            }
        });
    }
}
