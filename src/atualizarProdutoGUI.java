import DAO.lojadao;
import entity.produtos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class atualizarProdutoGUI extends JFrame {

    private JTextField txtId, txtNome, txtDescricao, txtPreco, txtEstoque;
    private JButton btnAtualizar, btnExcluir, btnCancelar;
    private JPanel painelPrincipal, painelCampos;
    private Font oswaldFont;

    public atualizarProdutoGUI() {
        this(null);
    }

    public atualizarProdutoGUI(Long idProduto) {
        lojadao dao = new lojadao();

        setTitle("Atualizar Produto");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        carregarFonteOswald();

        painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        painelCampos = new JPanel(new GridLayout(5, 2, 10, 10));

        JLabel lblId = new JLabel("ID do Produto:");
        estilizarLabel(lblId);
        txtId = new JTextField();
        txtId.setEditable(false);
        estilizarCampo(txtId);
        painelCampos.add(lblId);
        painelCampos.add(txtId);

        JLabel lblNome = new JLabel("Nome:");
        estilizarLabel(lblNome);
        txtNome = new JTextField();
        estilizarCampo(txtNome);
        painelCampos.add(lblNome);
        painelCampos.add(txtNome);

        JLabel lblDescricao = new JLabel("Descrição:");
        estilizarLabel(lblDescricao);
        txtDescricao = new JTextField();
        estilizarCampo(txtDescricao);
        painelCampos.add(lblDescricao);
        painelCampos.add(txtDescricao);

        JLabel lblPreco = new JLabel("Preço:");
        estilizarLabel(lblPreco);
        txtPreco = new JTextField();
        estilizarCampo(txtPreco);
        painelCampos.add(lblPreco);
        painelCampos.add(txtPreco);

        JLabel lblEstoque = new JLabel("Estoque:");
        estilizarLabel(lblEstoque);
        txtEstoque = new JTextField();
        estilizarCampo(txtEstoque);
        painelCampos.add(lblEstoque);
        painelCampos.add(txtEstoque);

        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(new Color(40, 167, 69));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFont(oswaldFont.deriveFont(Font.BOLD, 16f));
        btnAtualizar.addActionListener((ActionEvent _) -> {
            try {
                long id = Long.parseLong(txtId.getText());
                String nome = txtNome.getText();
                String descricao = txtDescricao.getText();
                double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
                int estoque = Integer.parseInt(txtEstoque.getText());

                produtos produtoAtualizado = new produtos(id, nome, descricao, preco, estoque);
                dao.atualizarProduto(produtoAtualizado);

                JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar o produto: " + ex.getMessage());
            }
        });

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(oswaldFont.deriveFont(Font.BOLD, 16f));
        btnCancelar.addActionListener((ActionEvent _) -> dispose());

        btnExcluir = new JButton("Excluir Produto");
        btnExcluir.setBackground(new Color(220, 53, 69));
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFont(oswaldFont.deriveFont(Font.BOLD, 16f));
        btnExcluir.addActionListener((ActionEvent _) -> {
            int resposta = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este produto?", "Excluir Produto", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                try {
                    long id = Long.parseLong(txtId.getText());
                    dao.deletarProduto(id);
                    JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir o produto: " + ex.getMessage());
                }
            }
        });
        btnExcluir.setVisible(idProduto != null);

        JPanel painelBotao = new JPanel();
        painelBotao.setOpaque(false);
        painelBotao.add(btnAtualizar);
        painelBotao.add(btnCancelar);
        painelBotao.add(btnExcluir);

        painelPrincipal.add(painelCampos, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);

        if (idProduto != null) {
            produtos p = dao.buscarProdutoPorId(idProduto);
            if (p != null) {
                txtId.setText(String.valueOf(p.getId()));  // Aqui você define o ID no campo
                txtNome.setText(p.getNome());
                txtDescricao.setText(p.getDescricao());
                txtPreco.setText(String.format("%.2f", p.getPreco()));
                txtEstoque.setText(String.valueOf(p.getEstoque()));
            } else {
                JOptionPane.showMessageDialog(this, "Produto não encontrado!");
            }
        }
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

    private void estilizarLabel(JLabel label) {
        label.setFont(oswaldFont.deriveFont(Font.PLAIN, 16f));
        label.setForeground(new Color(33, 37, 41));
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(oswaldFont.deriveFont(Font.PLAIN, 15f));
        campo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        campo.setBackground(new Color(245, 245, 245));
    }
}
