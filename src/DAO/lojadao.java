package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import conexao.Conexao;
import entity.produtos;

public class lojadao {
    
    // Método para inserir um produto
    public void inserirProduto(produtos produto) {
        String sql = "INSERT INTO produtos (id, nome, descricao, preco, estoque) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, produto.getId());    
            pstmt.setString(2, produto.getNome());
            pstmt.setString(3, produto.getDescricao());
            pstmt.setDouble(4, produto.getPreco());
            pstmt.setInt(5, produto.getEstoque());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar um produto
    public void atualizarProduto(produtos produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, estoque = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getEstoque());
            pstmt.setLong(5, produto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para deletar um produto
    public void deletarProduto(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar um produto por ID
    public produtos buscarProduto(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        produtos produto = null;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                produto = new produtos(rs.getLong("id"), rs.getString("nome"), rs.getString("descricao"),
                        rs.getDouble("preco"), rs.getInt("estoque"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produto;
    }

    // Método para listar todos os produtos
    public List<produtos> listarProdutos() {
        List<produtos> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                produtos produto = new produtos(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque")
                );
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    // Método para buscar produtos por nome
    public List<produtos> buscarProdutoPorNome(String nome) {
        List<produtos> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE nome LIKE ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nome + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produtos produto = new produtos(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque")
                );
                produtos.add(produto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    // Método para verificar se há estoque suficiente
    public boolean verificarEstoque(Long idProduto, int quantidade) {
        String sql = "SELECT estoque FROM produtos WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idProduto);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int estoque = rs.getInt("estoque");
                return estoque >= quantidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para registrar a venda
    public void registrarVenda(Long idProduto, int quantidade, double precoUnitario) {
        // Verifica se há estoque suficiente
        if (!verificarEstoque(idProduto, quantidade)) {
            System.out.println("Estoque insuficiente para realizar a venda.");
            return;
        }
    
        // Calcula o lucro (20% do valor total da venda)
        double lucro = precoUnitario * quantidade;
    
        // Registra a venda
        String sql = "INSERT INTO vendas (id_produto, quantidade, preco_unitario, data_venda, lucro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idProduto);
            pstmt.setInt(2, quantidade);
            pstmt.setDouble(3, precoUnitario);
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Data atual
            pstmt.setDouble(5, lucro);
            pstmt.executeUpdate();
    
            // Atualiza o estoque do produto
            atualizarEstoque(idProduto, quantidade);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    // Método para atualizar o estoque após a venda
    private void atualizarEstoque(Long idProduto, int quantidadeVendida) {
        String sql = "UPDATE produtos SET estoque = estoque - ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantidadeVendida);
            pstmt.setLong(2, idProduto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para calcular o lucro diário
    public double calcularLucroDiario() {
        String sql = "SELECT SUM(lucro) AS lucro_diario FROM vendas WHERE data_venda = CURDATE()";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("lucro_diario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Método para calcular o lucro semanal
    public double calcularLucroSemanal() {
        String sql = "SELECT SUM(lucro) AS lucro_semanal FROM vendas WHERE data_venda BETWEEN CURDATE() - INTERVAL 7 DAY AND CURDATE()";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("lucro_semanal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Método para calcular o lucro mensal
    public double calcularLucroMensal() {
        String sql = "SELECT SUM(lucro) AS lucro_mensal FROM vendas WHERE YEAR(data_venda) = YEAR(CURDATE()) AND MONTH(data_venda) = MONTH(CURDATE())";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("lucro_mensal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Método para listar todas as vendas
    public List<Map<String, Object>> listarVendas() {
        List<Map<String, Object>> vendasList = new ArrayList<>();
        String sql = "SELECT v.id, p.nome, v.quantidade, v.preco_unitario, v.data_venda, v.lucro" +
                     "FROM vendas v " +
                     "JOIN produtos p ON v.id_produto = p.id";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> venda = new HashMap<>();
                venda.put("id", rs.getInt("id"));
                venda.put("nome_produto", rs.getString("nome"));
                venda.put("quantidade", rs.getInt("quantidade"));
                venda.put("preco_unitario", rs.getDouble("preco_unitario"));
                venda.put("data_venda", rs.getDate("data_venda"));
                venda.put("lucro", rs.getDouble("lucro"));
                vendasList.add(venda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendasList;
    }
    public produtos buscarProdutoPorNomeUnico(String nome) {
        String sql = "SELECT * FROM produtos WHERE nome = ?";
        produtos produto = null;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                produto = new produtos(rs.getLong("id"), rs.getString("nome"), rs.getString("descricao"),
                        rs.getDouble("preco"), rs.getInt("estoque"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produto;
    }

    public List<produtos> buscarTodosProdutos() {
        List<produtos> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        try (
            Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produtos p = new produtos();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getDouble("preco"));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public produtos buscarProdutoPorId(Long id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new produtos(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Map<String, Double> buscarLucrosUltimos6Meses() {
        Map<String, Double> lucros = new LinkedHashMap<>();
    
        String sql = "SELECT " +
                     "DATE_FORMAT(data_venda, '%m/%Y') AS mes_formatado, " +
                     "SUM(lucro) AS total_lucro " +
                     "FROM vendas " +
                     "WHERE data_venda >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-01') " +
                     "GROUP BY mes_formatado " +
                     "ORDER BY STR_TO_DATE(mes_formatado, '%m/%Y') DESC";
    
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                String mes = rs.getString("mes_formatado");
                double lucro = rs.getDouble("total_lucro");
                lucros.put(mes, lucro);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return lucros;
    }
    
    
}
