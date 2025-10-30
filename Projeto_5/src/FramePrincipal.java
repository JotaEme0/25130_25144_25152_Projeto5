import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FramePrincipal extends JFrame {

    public FramePrincipal() {
        setTitle("Sistema Da Roça - Menu Principal");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ====== Título no topo ======
        JLabel lblTitulo = new JLabel("Bem-vindo ao Sistema Da Roça!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // ====== Painel central com botões ======
        JPanel pnlCentral = new JPanel();
        pnlCentral.setLayout(new GridLayout(2, 1, 20, 20));
        pnlCentral.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JButton btnCategorias = new JButton("Manutenção de Categorias");
        JButton btnProdutos = new JButton("Manutenção de Produtos");

        // estilizar botões
        Font fonteBotao = new Font("Arial", Font.PLAIN, 16);
        btnCategorias.setFont(fonteBotao);
        btnProdutos.setFont(fonteBotao);
        btnCategorias.setFocusPainted(false);
        btnProdutos.setFocusPainted(false);

        // adicionar os botões ao painel
        pnlCentral.add(btnCategorias);
        pnlCentral.add(btnProdutos);
        add(pnlCentral, BorderLayout.CENTER);

        // ====== Rodapé opcional ======
        JLabel lblRodape = new JLabel("Desenvolvido por Equipe Da Roça", SwingConstants.CENTER);
        lblRodape.setFont(new Font("Arial", Font.ITALIC, 12));
        lblRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblRodape, BorderLayout.SOUTH);

        // ====== Eventos dos botões ======
        btnCategorias.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FrameCategoria frmCat = new FrameCategoria();
                    frmCat.setLocationRelativeTo(null);
                    frmCat.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FramePrincipal.this,
                            "Erro ao abrir categorias: " + ex.getMessage());
                }
            }
        });

        btnProdutos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FrameProduto frmProd = new FrameProduto();
                    frmProd.setLocationRelativeTo(null);
                    frmProd.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FramePrincipal.this,
                            "Erro ao abrir produtos: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FramePrincipal tela = new FramePrincipal();
            tela.setVisible(true);
        });
    }
}
