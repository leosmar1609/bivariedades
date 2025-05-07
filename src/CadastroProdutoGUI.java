import DAO.lojadao;
import entity.produtos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class CadastroProdutoGUI extends JFrame {

    private JTextField txtNome, txtDescricao, txtPreco, txtEstoque, txtId;
    private JButton btnCadastrar, btnCancelar;
    private JPanel painelPrincipal, painelCampos;
    private Font oswaldFont;

    private final Color corFundo = new Color(33, 33, 33);
    private final Color corTexto = Color.WHITE;
    private final Color corCampo = new Color(50, 50, 50);
    private final Color corTurquesa = new Color(0, 206, 209);
    private final Color corErro = new Color(220, 53, 69);

    public CadastroProdutoGUI() {
        setTitle("Cadastrar Produto");
        setSize(460, 380);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));

        carregarFonteOswald();

        painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corFundo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        painelCampos = new JPanel(new GridLayout(5, 2, 10, 10));
        painelCampos.setOpaque(false);

        criarCampo("Id do Produto:", txtId = new JTextField(), true);
        criarCampo("Nome:", txtNome = new JTextField(), true);
        criarCampo("Descrição:", txtDescricao = new JTextField(), true);
        criarCampo("Preço:", txtPreco = new JTextField(), true);
        criarCampo("Estoque:", txtEstoque = new JTextField(), true);

        btnCadastrar = criarBotao("Cadastrar", corTurquesa, _ -> {
            try {
                Long id = Long.parseLong(txtId.getText());
                String nome = txtNome.getText();
                String descricao = txtDescricao.getText();
                double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
                int estoque = Integer.parseInt(txtEstoque.getText());

                produtos novoProduto = new produtos(id, nome, descricao, preco, estoque);
                new lojadao().inserirProduto(novoProduto);

                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar o produto: " + ex.getMessage());
            }
        });

        btnCancelar = criarBotao("Cancelar", corErro, _ -> dispose());

        JPanel painelBotoes = new JPanel();
        painelBotoes.setOpaque(false);
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnCancelar);

        painelPrincipal.add(painelCampos, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        setContentPane(painelPrincipal);
    }

    private void criarCampo(String titulo, JTextField campo, boolean editavel) {
        JLabel label = new JLabel(titulo);
        label.setFont(oswaldFont.deriveFont(Font.PLAIN, 16f));
        label.setForeground(corTexto);
        campo.setEditable(editavel);
        campo.setFont(oswaldFont.deriveFont(Font.PLAIN, 15f));
        campo.setBackground(corCampo);
        campo.setForeground(corTexto);
        campo.setCaretColor(corTexto);
        campo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        painelCampos.add(label);
        painelCampos.add(campo);
    }

    private JButton criarBotao(String texto, Color corFundo, AbstractAction action) {
        JButton botao = new JButton(texto);
        botao.setFont(oswaldFont.deriveFont(Font.BOLD, 15f));
        botao.setFocusPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.addActionListener(action);
        return botao;
    }

    private JButton criarBotao(String texto, Color corFundo, java.awt.event.ActionListener listener) {
        return criarBotao(texto, corFundo, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });
    }

    private void carregarFonteOswald() {
        try {
            oswaldFont = Font.createFont(Font.TRUETYPE_FONT, new File("fontes/Oswald-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(oswaldFont);
        } catch (FontFormatException | IOException e) {
            oswaldFont = new Font("Segoe UI", Font.PLAIN, 14);
            System.err.println("Erro ao carregar a fonte Oswald: " + e.getMessage());
        }
    }
}
