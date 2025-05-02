import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class PagamentoGUI extends JFrame {

    private JComboBox<String> cbFormaPagamento;
    private JTextField txtValorTotal, txtValorPago, txtTroco;
    private JButton btnFinalizar, btnCancelar;
    private double valorTotal;
    private boolean pagamentoConfirmado = false;
    private Font fonteOswald;

    public PagamentoGUI(double valorTotal) {
        this.valorTotal = valorTotal;
        carregarFonte();

        setTitle("Pagamento");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCampos(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void carregarFonte() {
        try {
            fonteOswald = Font.createFont(Font.TRUETYPE_FONT, new File("fontes/Oswald-Regular.ttf")).deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(fonteOswald);
        } catch (Exception e) {
            System.out.println("Fonte Oswald não carregada, usando padrão.");
            fonteOswald = new Font("Segoe UI", Font.PLAIN, 16);
        }
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel();
        JLabel lblTitulo = new JLabel("Finalizar Pagamento");
        lblTitulo.setFont(fonteOswald.deriveFont(Font.BOLD, 20f));
        painel.add(lblTitulo);
        return painel;
    }

    private JPanel criarPainelCampos() {
        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblForma = new JLabel("Forma de Pagamento:");
        cbFormaPagamento = new JComboBox<>(new String[]{"Dinheiro", "Cartão de Crédito", "Cartão de Débito", "Pix"});
        cbFormaPagamento.setFont(fonteOswald);

        JLabel lblValorTotal = new JLabel("Valor Total (R$):");
        txtValorTotal = new JTextField(String.format("%.2f", valorTotal));
        txtValorTotal.setEditable(false);

        JLabel lblValorPago = new JLabel("Valor Pago (R$):");
        txtValorPago = new JTextField();

        JLabel lblTroco = new JLabel("Troco (R$):");
        txtTroco = new JTextField();
        txtTroco.setEditable(false);

        Font camposFont = fonteOswald.deriveFont(15f);
        lblForma.setFont(fonteOswald);
        lblValorTotal.setFont(fonteOswald);
        lblValorPago.setFont(fonteOswald);
        lblTroco.setFont(fonteOswald);
        txtValorTotal.setFont(camposFont);
        txtValorPago.setFont(camposFont);
        txtTroco.setFont(camposFont);

        painel.add(lblForma);
        painel.add(cbFormaPagamento);
        painel.add(lblValorTotal);
        painel.add(txtValorTotal);
        painel.add(lblValorPago);
        painel.add(txtValorPago);
        painel.add(lblTroco);
        painel.add(txtTroco);

        cbFormaPagamento.addActionListener(_ -> {
            String forma = (String) cbFormaPagamento.getSelectedItem();
            boolean ehDinheiro = forma.equals("Dinheiro");
            txtValorPago.setEnabled(ehDinheiro);
            if (!ehDinheiro) {
                txtValorPago.setText("");
                txtTroco.setText("");
            }
        });

        txtValorPago.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calcularTroco();
            }
        });

        return painel;
    }

    private void calcularTroco() {
        try {
            double pago = Double.parseDouble(txtValorPago.getText().replace(",", "."));
            if (pago >= valorTotal) {
                double troco = pago - valorTotal;
                txtTroco.setText(String.format("%.2f", troco));
            } else {
                txtTroco.setText("Valor insuficiente");
            }
        } catch (NumberFormatException e) {
            txtTroco.setText("");
        }
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel();
        btnFinalizar = new JButton("Confirmar Pagamento");
        btnCancelar = new JButton("Cancelar");

        btnFinalizar.setFont(fonteOswald);
        btnCancelar.setFont(fonteOswald);

        btnFinalizar.setBackground(new Color(0, 153, 76));
        btnFinalizar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(204, 0, 0));
        btnCancelar.setForeground(Color.WHITE);

        btnFinalizar.setFocusPainted(false);
        btnCancelar.setFocusPainted(false);

        btnFinalizar.addActionListener(_ -> {
            String forma = (String) cbFormaPagamento.getSelectedItem();
            if (forma.equals("Dinheiro") && txtTroco.getText().equals("Valor insuficiente")) {
                JOptionPane.showMessageDialog(this, "O valor pago é menor que o valor total!", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                pagamentoConfirmado = true;
                JOptionPane.showMessageDialog(this, "Pagamento realizado com sucesso!", "Pagamento", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        btnCancelar.addActionListener((ActionEvent _) -> {
            int resposta = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar o pagamento?", "Cancelar Pagamento", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        painel.add(btnFinalizar);
        painel.add(btnCancelar);
        return painel;
    }

    public boolean pagamentoConfirmado() {
        return pagamentoConfirmado;
    }
}

    // Teste
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new PagamentoGUI(89.90));
//     }
// }
