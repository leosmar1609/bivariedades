import DAO.lojadao;
import entity.produtos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class SistemaLojaGUI extends JFrame {
    private JTextField txtBuscaNome, txtBuscaId, txtQuantidade;
    private JList<String> listaProdutos;
    private DefaultListModel<String> listModel;
    private JTable tabelaCarrinho;
    private JLabel lblTotal, lblLucroDiario, lblLucroSemanal, lblLucroMensal;
    private JButton btnAdicionarCarrinho, btnFinalizarVenda, btnRemoverCarrinho;
    private lojadao lojaDAO;
    private double totalCarrinho = 0.0;

    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    public SistemaLojaGUI() {
        setTitle("Bia Variedades");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        lojaDAO = new lojadao();

        setLayout(new BorderLayout());

        JPanel menuLateral = new JPanel(new GridLayout(3, 1, 10, 10));
        menuLateral.setBorder(BorderFactory.createEmptyBorder(20, 10, 500, 10));
        JButton btnFinanceiro = new JButton("Financeiro");
        JButton btnProdutos = new JButton("Produtos");
        JButton btnVendas = new JButton("Venda");

        menuLateral.add(btnFinanceiro);
        menuLateral.add(btnProdutos);
        menuLateral.add(btnVendas);

        add(menuLateral, BorderLayout.WEST);

        painelPrincipal = new JPanel();
        cardLayout = new CardLayout();
        painelPrincipal.setLayout(cardLayout);

        JPanel telaVendas = construirTelaVendas();
        painelPrincipal.add(telaVendas, "venda");

        painelPrincipal.add(construirTelaFinanceiro(), "financeiro");
        
        painelPrincipal.add(construirTelaProdutos(), "produtos");

        add(painelPrincipal, BorderLayout.CENTER);

        btnVendas.addActionListener(e -> cardLayout.show(painelPrincipal, "venda"));
        btnFinanceiro.addActionListener(e -> cardLayout.show(painelPrincipal, "financeiro"));
        btnProdutos.addActionListener(e -> cardLayout.show(painelPrincipal, "produtos"));

        cardLayout.show(painelPrincipal, "venda");
    }

    private JPanel construirTelaVendas() {
        JPanel painelVendas = new JPanel(new BorderLayout(10, 10));

        JPanel painelPesquisa = new JPanel(new GridLayout(2, 1, 5, 5));
        painelPesquisa.setBorder(BorderFactory.createTitledBorder("Buscar Produto"));

        JPanel linhaBuscaNome = new JPanel(new BorderLayout(5, 5));
        txtBuscaNome = new JTextField();
        txtBuscaNome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscaNome.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                buscarProdutosPorNome();
            }
        });
        linhaBuscaNome.add(new JLabel("Nome:"), BorderLayout.WEST);
        linhaBuscaNome.add(txtBuscaNome, BorderLayout.CENTER);

        JPanel linhaBuscaId = new JPanel(new BorderLayout(5, 5));
        txtBuscaId = new JTextField();
        txtBuscaId.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscaId.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                buscarProdutoPorId(Long.parseLong(txtBuscaId.getText()));
            }
        });
        linhaBuscaId.add(new JLabel("Código (ID):"), BorderLayout.WEST);
        linhaBuscaId.add(txtBuscaId, BorderLayout.CENTER);

        painelPesquisa.add(linhaBuscaNome);
        painelPesquisa.add(linhaBuscaId);

        listModel = new DefaultListModel<>();
        listaProdutos = new JList<>(listModel);
        listaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollProdutos = new JScrollPane(listaProdutos);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));

        String[] colunas = {"Produto", "Preço", "Qtd", "Subtotal"};
        tabelaCarrinho = new JTable(new DefaultTableModel(new Object[][] {}, colunas));
        tabelaCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaCarrinho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        txtQuantidade = new JTextField("0", 5);
        txtQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        btnAdicionarCarrinho = new JButton("Adicionar");
        btnAdicionarCarrinho.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdicionarCarrinho.addActionListener(e -> adicionarProdutoCarrinho());
        btnAdicionarCarrinho.setBackground(new Color(0, 123, 255));
        btnAdicionarCarrinho.setForeground(Color.WHITE);

        btnFinalizarVenda = new JButton("Finalizar Venda");
        btnFinalizarVenda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFinalizarVenda.addActionListener(e -> finalizarVenda());
        btnFinalizarVenda.setBackground(new Color(40, 167, 69));
        btnFinalizarVenda.setForeground(Color.WHITE);

        btnRemoverCarrinho = new JButton("Remover Selecionado");
        btnRemoverCarrinho.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRemoverCarrinho.setBackground(new Color(220, 53, 69));
        btnRemoverCarrinho.setForeground(Color.WHITE);
        btnRemoverCarrinho.addActionListener(e -> removerItemCarrinho());

        painelAcoes.add(btnRemoverCarrinho);


        painelAcoes.add(new JLabel("Quantidade:"));
        painelAcoes.add(txtQuantidade);
        painelAcoes.add(btnAdicionarCarrinho);
        painelAcoes.add(btnFinalizarVenda);

        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        painelTotal.add(lblTotal);

        JPanel painelEsquerda = new JPanel(new BorderLayout(5, 5));
        painelEsquerda.add(painelPesquisa, BorderLayout.NORTH);
        painelEsquerda.add(scrollProdutos, BorderLayout.CENTER);

        JPanel painelDireita = new JPanel(new BorderLayout(5, 5));
        painelDireita.add(scrollCarrinho, BorderLayout.CENTER);
        painelDireita.add(painelTotal, BorderLayout.SOUTH);

        painelVendas.add(painelEsquerda, BorderLayout.WEST);
        painelVendas.add(painelDireita, BorderLayout.CENTER);
        painelVendas.add(painelAcoes, BorderLayout.SOUTH);

        buscarProdutosPorNome();

        return painelVendas;
    }

    private JPanel construirTelaProdutos() {
        JPanel painelProdutos = new JPanel(new BorderLayout(10, 10));
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Lista de Produtos"));
    
        JTextArea areaProdutos = new JTextArea();
        areaProdutos.setEditable(false);
        areaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollArea = new JScrollPane(areaProdutos);
        painelProdutos.add(scrollArea, BorderLayout.CENTER);
    
        JTextField txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBuscar.setBackground(new Color(0, 123, 255));
        btnBuscar.setForeground(Color.WHITE);
    
        JPanel painelBusca = new JPanel();
        painelBusca.add(new JLabel("Buscar por Nome ou ID:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelProdutos.add(painelBusca, BorderLayout.NORTH);
    
        Runnable atualizarListaProdutos = () -> {
            areaProdutos.setText("Lista de Produtos Disponíveis:\n\n");
            List<produtos> lista = lojaDAO.listarProdutos();
            for (produtos produto : lista) {
                areaProdutos.append(" ID: " + produto.getId() + "\n");
                areaProdutos.append(" Nome: " + produto.getNome() + "\n");
                areaProdutos.append(" Descrição: " + produto.getDescricao() + "\n");
                areaProdutos.append(" Preço: R$ " + String.format("%.2f", produto.getPreco()) + "\n");
                areaProdutos.append(" Estoque: " + produto.getEstoque() + " unidades\n");
                areaProdutos.append("_________________________________________\n\n");
            }
        };
    
        atualizarListaProdutos.run();
    
        btnBuscar.addActionListener(e -> {
            String termo = txtBusca.getText().trim().toLowerCase();
            areaProdutos.setText("Resultado da Busca:\n\n");
        
            if (termo.isEmpty()) {
                atualizarListaProdutos.run();
                return;
            }
        
            try {
                Long idBuscado = Long.parseLong(termo); 
                produtos produto = lojaDAO.buscarProdutoPorId(idBuscado);
                if (produto != null) {
                    areaProdutos.append(" ID: " + produto.getId() + "\n");
                    areaProdutos.append(" Nome: " + produto.getNome() + "\n");
                    areaProdutos.append(" Descrição: " + produto.getDescricao() + "\n");
                    areaProdutos.append(" Preço: R$ " + String.format("%.2f", produto.getPreco()) + "\n");
                    areaProdutos.append(" Estoque: " + produto.getEstoque() + " unidades\n");
                    areaProdutos.append("_________________________________________\n\n");
                } else {
                    areaProdutos.append("Produto não encontrado.\n");
                }
            } catch (NumberFormatException ex) {
                List<produtos> lista = lojaDAO.listarProdutos();
                for (produtos produto : lista) {
                    if (produto.getNome().toLowerCase().contains(termo)) {
                        areaProdutos.append(" ID: " + produto.getId() + "\n");
                        areaProdutos.append(" Nome: " + produto.getNome() + "\n");
                        areaProdutos.append(" Descrição: " + produto.getDescricao() + "\n");
                        areaProdutos.append(" Preço: R$ " + String.format("%.2f", produto.getPreco()) + "\n");
                        areaProdutos.append(" Estoque: " + produto.getEstoque() + " unidades\n");
                        areaProdutos.append("_________________________________________\n\n");
                    }
                }
            }
        });
        
    
        JButton btnRegistrarProduto = new JButton("Registrar Produto");
        btnRegistrarProduto.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegistrarProduto.setBackground(new Color(0, 123, 255));
        btnRegistrarProduto.setForeground(Color.WHITE);
        btnRegistrarProduto.setPreferredSize(new Dimension(200, 40));
        btnRegistrarProduto.addActionListener(e -> {
            CadastroProdutoGUI cadastroProduto = new CadastroProdutoGUI();
            cadastroProduto.setVisible(true);
        });
    
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAtualizar.setBackground(new Color(0, 123, 255));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setPreferredSize(new Dimension(200, 40));
        btnAtualizar.addActionListener(e -> atualizarListaProdutos.run());

        JButton btnAtualizarProduto = new JButton("Atualizar Produto");
        btnAtualizarProduto.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAtualizarProduto.setBackground(new Color(255, 193, 7));
        btnAtualizarProduto.setForeground(Color.BLACK);
        btnAtualizarProduto.setPreferredSize(new Dimension(200, 40));
        btnAtualizarProduto.addActionListener(e -> {
            String termo = txtBusca.getText().trim();
        
            if (!termo.isEmpty()) {
                try {
                    Long idBuscado = Long.parseLong(termo);
                    List<produtos> lista = lojaDAO.listarProdutos();
                    for (produtos produto : lista) {
                        if (produto.getId() == idBuscado) {
                            atualizarProdutoGUI atualizarProduto = new atualizarProdutoGUI(produto.getId());
                            atualizarProduto.setVisible(true);
                            return;
                        }
                    }
                } catch (NumberFormatException ex) {
                    List<produtos> lista = lojaDAO.listarProdutos();
                    for (produtos produto : lista) {
                        if (produto.getNome().equalsIgnoreCase(termo)) {
                            atualizarProdutoGUI atualizarProduto = new atualizarProdutoGUI(produto.getId());
                            atualizarProduto.setVisible(true);
                            return;
                        }
                    }
                }
            }
        
            atualizarProdutoGUI atualizarProduto = new atualizarProdutoGUI();
            atualizarProduto.setVisible(true);
        });

    
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnRegistrarProduto);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnAtualizarProduto);
    
        painelProdutos.add(painelBotoes, BorderLayout.SOUTH);
    
        return painelProdutos;
    }
    
    private JPanel construirTelaFinanceiro() {
        JPanel painelFinanceiro = new JPanel(new GridBagLayout());
        painelFinanceiro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelFinanceiro.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);

        lojaDAO = new lojadao();

        JLabel titulo = new JLabel("Resumo Financeiro", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(33, 37, 41));
        painelFinanceiro.add(titulo, gbc);

        JPanel painelLucros = new JPanel();
        painelLucros.setLayout(new BoxLayout(painelLucros, BoxLayout.Y_AXIS));
        painelLucros.setBackground(new Color(245, 245, 245));
        painelLucros.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        lblLucroDiario = new JLabel("", SwingConstants.CENTER);
        lblLucroSemanal = new JLabel("", SwingConstants.CENTER);
        lblLucroMensal = new JLabel("", SwingConstants.CENTER);

        JLabel[] labels = { lblLucroDiario, lblLucroSemanal, lblLucroMensal };
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            label.setForeground(new Color(60, 60, 60));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            painelLucros.add(label);
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        painelFinanceiro.add(painelLucros, gbc);

        JLabel expliq = new JLabel("<html><div>*Os lucros diários são referentes ao dia atual, enquanto os lucros<br/> semanais e mensais são referentes à semana e mês atuais, respectivamente.<br/><br/>*Lucros diários: renovados todos os dias 00:00<br/><br/>*Lucros semanais: renovados a cada 7 dias<br/><br/>*Lucros mensais: renovados todo dia 1º</div></html>");
        expliq.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        expliq.setAlignmentX(Component.CENTER_ALIGNMENT);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 20, 0); 
        painelFinanceiro.add(expliq, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelBotoes.setBackground(new Color(245, 245, 245));

        JButton btnAtualizar = new JButton(" Atualizar");
        JButton btnHistorico = new JButton(" Ver Histórico de Lucros");

        btnAtualizar.setFocusPainted(false);
        btnHistorico.setFocusPainted(false);
        btnAtualizar.setBackground(new Color(0, 123, 255));
        btnHistorico.setBackground(new Color(40, 167, 69));
        btnAtualizar.setForeground(Color.WHITE);
        btnHistorico.setForeground(Color.WHITE);
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHistorico.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAtualizar.addActionListener(e -> carregarLucros());
        btnHistorico.addActionListener(e -> mostrarHistoricoLucros());

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnHistorico);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        painelFinanceiro.add(painelBotoes, gbc);

        carregarLucros();

        return painelFinanceiro;
    }       
    
    private void carregarLucros() {
        double lucroDiario = lojaDAO.calcularLucroDiario();
        double lucroSemanal = lojaDAO.calcularLucroSemanal();
        double lucroMensal = lojaDAO.calcularLucroMensal();
    
        lblLucroDiario.setText(String.format("Lucro Diário: R$ %.2f", lucroDiario));
        lblLucroSemanal.setText(String.format("Lucro Semanal: R$ %.2f", lucroSemanal));
        lblLucroMensal.setText(String.format("Lucro Mensal: R$ %.2f", lucroMensal));
    }

    private void mostrarHistoricoLucros() {
        Map<String, Double> historico = lojaDAO.buscarLucrosUltimos6Meses();
    
        StringBuilder texto = new StringBuilder("Histórico dos últimos 6 meses:\n\n");
        for (Map.Entry<String, Double> entry : historico.entrySet()) {
            texto.append(String.format("%s - R$ %.2f\n", entry.getKey(), entry.getValue()));
        }
    
        JTextArea textArea = new JTextArea(texto.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 200));
    
        JOptionPane.showMessageDialog(this, scrollPane, "Histórico de Lucros", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarProdutosPorNome() {
        String nome = txtBuscaNome.getText().trim();
        listModel.clear();
        List<produtos> produtosList = nome.isEmpty() ? lojaDAO.buscarTodosProdutos() : lojaDAO.buscarProdutoPorNome(nome);
        for (produtos p : produtosList) {
            listModel.addElement(p.getNome());
        }
    }

    private void buscarProdutoPorId(Long id) {
        listModel.clear();
    
        try {
            produtos produto = lojaDAO.buscarProdutoPorId(id);
            if (produto != null) {
                listModel.addElement(produto.getNome());
            } else {
                
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar o produto: " + e.getMessage());
        }
    }
    

    private void adicionarProdutoCarrinho() {
        String nomeSelecionado = listaProdutos.getSelectedValue();
        if (nomeSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na lista.");
            return;
        }
    
        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite uma quantidade válida.");
            return;
        }
    
        String nomeProduto = nomeSelecionado.split(" - ")[0]; 
        produtos produto = lojaDAO.buscarProdutoPorNomeUnico(nomeProduto);
    
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado.");
            return;
        }
    
        if (quantidade > produto.getEstoque()) {
            JOptionPane.showMessageDialog(this, "Produto sem estoque suficiente.");
            return;
        }
    
        double subtotal = produto.getPreco() * quantidade;
        totalCarrinho += subtotal;
    
        DefaultTableModel model = (DefaultTableModel) tabelaCarrinho.getModel();
        model.addRow(new Object[]{
            produto.getNome(),
            produto.getPreco(),     
            quantidade,
            subtotal
        });

        lblTotal.setText(String.format("Total: R$ %.2f", totalCarrinho));
        
    }

    private void removerItemCarrinho() {
        int linhaSelecionada = tabelaCarrinho.getSelectedRow();
        if (linhaSelecionada != -1) {
            DefaultTableModel modelo = (DefaultTableModel) tabelaCarrinho.getModel();
    
            String subtotalStr = modelo.getValueAt(linhaSelecionada, 3).toString().replace("R$", "").replace(",", ".");
            double subtotal = Double.parseDouble(subtotalStr);
            totalCarrinho -= subtotal;
    
            modelo.removeRow(linhaSelecionada);
    
            lblTotal.setText(String.format("Total: R$ %.2f", totalCarrinho));
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um item do carrinho para remover.", "Nenhum item selecionado", JOptionPane.WARNING_MESSAGE);
        }
    }
    

    private void finalizarVenda() {
        if (totalCarrinho == 0.0) {
            JOptionPane.showMessageDialog(this, "Carrinho vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        int confirmar = JOptionPane.showConfirmDialog(this, "Deseja finalizar a venda?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            PagamentoGUI pagamento = new PagamentoGUI(totalCarrinho);
    
            pagamento.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (pagamento.pagamentoConfirmado()) {
                        DefaultTableModel modeloTabela = (DefaultTableModel) tabelaCarrinho.getModel();
    
                        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
                            String nomeProduto = (String) modeloTabela.getValueAt(i, 0);
                            double precoUnitario = (double) modeloTabela.getValueAt(i, 1);
                            int quantidade = (int) modeloTabela.getValueAt(i, 2);
    
                            produtos produto = lojaDAO.buscarProdutoPorNomeUnico(nomeProduto);
                            lojaDAO.registrarVenda(produto.getId(), quantidade, precoUnitario);
                        }
    
                        JOptionPane.showMessageDialog(null, "Venda concluída!\nTotal: R$ " + String.format("%.2f", totalCarrinho));
                        limparCarrinho();
                    }
                }
            });
        }
    }
    

    private void atualizarTotal() {
        lblTotal.setText("Total: R$ " + String.format("%.2f", totalCarrinho));
    }

    private void limparCarrinho() {
        DefaultTableModel modeloTabela = (DefaultTableModel) tabelaCarrinho.getModel();
        modeloTabela.setRowCount(0);
        totalCarrinho = 0;
        atualizarTotal();
        txtQuantidade.setText("1");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaLojaGUI().setVisible(true));
    }
}
