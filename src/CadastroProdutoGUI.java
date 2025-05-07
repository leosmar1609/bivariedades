import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import DAO.lojadao;
import entity.produtos;

public class CadastroProdutoGUI extends JFrame {

    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JTextField txtId;

    public CadastroProdutoGUI() {
        setTitle("Cadastro de Produtos");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setBackground(new Color(245, 245, 245));
        painel.setLayout(new GridBagLayout());
        setContentPane(painel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitulo = new JLabel("Cadastrar Novo Produto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(50, 50, 50));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 1;
        painel.add(new JLabel("ID:"), gbc);

        txtId = criarCampoTexto();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        painel.add(txtId, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(new JLabel("Nome:"), gbc);

        txtNome = criarCampoTexto();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        painel.add(txtNome, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 3;
        painel.add(new JLabel("Descrição:"), gbc);

        txtDescricao = criarCampoTexto();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        painel.add(txtDescricao, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 4;
        painel.add(new JLabel("Preço:"), gbc);

        txtPreco = criarCampoTexto();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        painel.add(txtPreco, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 5;
        painel.add(new JLabel("Estoque:"), gbc);

        txtEstoque = criarCampoTexto();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        painel.add(txtEstoque, gbc);

        JButton btnCadastrar = new JButton("Cadastrar Produto");
        estilizarBotao(btnCadastrar);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(btnCadastrar, gbc);

        btnCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarProduto();
            }
        });
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return campo;
    }

    private void estilizarBotao(JButton botao) {
        botao.setBackground(new Color(0, 153, 76));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void cadastrarProduto() {
        try {
            long id = Long.parseLong(txtId.getText());
            if (id <= 0) {
                throw new NumberFormatException("ID deve ser maior que zero.");
            }
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();
            String precoTexto = txtPreco.getText().replace(",", ".");
            float preco = Float.parseFloat(precoTexto);
            int estoque = Integer.parseInt(txtEstoque.getText());

            produtos produto = new produtos(id, nome, descricao, preco, estoque);
            produto.setId(id);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setEstoque(estoque);

            lojadao dao = new lojadao();
            dao.inserirProduto(produto);

            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            limparCampos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar produto: " + ex.getMessage());
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
    }

    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new CadastroProdutoGUI().setVisible(true);
    //     });
    // }
}
