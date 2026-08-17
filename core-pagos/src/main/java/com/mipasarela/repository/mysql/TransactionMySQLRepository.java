package com.mipasarela.repository.mysql;

import com.mipasarela.config.DatabaseConnection;
import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Merchant;
import com.mipasarela.domain.Token;
import com.mipasarela.domain.Transaction;
import com.mipasarela.domain.TransactionStatus;
import com.mipasarela.repository.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class TransactionMySQLRepository implements TransactionRepository {

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (merchant_id, customer_id, token_id, amount, currency, status, status_reason, idempotency_key, created_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // try-with-resources: Garantiza (atomicidad)
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, transaction.getMerchant().getMerchantId());
            stmt.setLong(2, transaction.getCustomer().getCustomerId());
            stmt.setLong(3, transaction.getToken().getTokenId());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getCurrency());
            stmt.setString(6, transaction.getStatus().name());
            stmt.setString(7, transaction.getStatusReason());
            stmt.setString(8, transaction.getIdempotencyKey());
            stmt.setTimestamp(9, Timestamp.valueOf(transaction.getCreatedAt()));

            stmt.executeUpdate();

            // Recupero la PK auto-incrementada generada por MySQL
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getLong(1));
                }
            }

            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la transacción en MySQL", e);
        }
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank())
            return Optional.empty();

        String sql = "SELECT t.*, m.razon_social, c.nombre, c.apellido, tk.brand, tk.last_four_digits " +
                "FROM transactions t " +
                "JOIN merchants m ON t.merchant_id = m.merchant_id " +
                "JOIN customers c ON t.customer_id = c.customer_id " +
                "JOIN tokens tk ON t.token_id = tk.token_id " +
                "WHERE t.idempotency_key = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idempotencyKey);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar transacción por clave de idempotencia en MySQL", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT t.*, m.razon_social, c.nombre, c.apellido, tk.brand, tk.last_four_digits " +
                "FROM transactions t " +
                "JOIN merchants m ON t.merchant_id = m.merchant_id " +
                "JOIN customers c ON t.customer_id = c.customer_id " +
                "JOIN tokens tk ON t.token_id = tk.token_id " +
                "WHERE t.transaction_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar transacción por ID en MySQL", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Transaction> findByMerchantId(Long merchantId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, m.razon_social, c.nombre, c.apellido, tk.brand, tk.last_four_digits " +
                "FROM transactions t " +
                "JOIN merchants m ON t.merchant_id = m.merchant_id " +
                "JOIN customers c ON t.customer_id = c.customer_id " +
                "JOIN tokens tk ON t.token_id = tk.token_id " +
                "WHERE t.merchant_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, merchantId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar transacciones por comercio en MySQL", e);
        }

        return transactions;
    }

    // Convierte una fila del ResultSet a un objeto Java Transaction.
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Merchant merchant = Merchant.builder()
                .merchantId(rs.getLong("merchant_id"))
                .socialReason(rs.getString("razon_social"))
                .build();

        Customer customer = Customer.builder()
                .customerId(rs.getLong("customer_id"))
                .name(rs.getString("nombre"))
                .lastName(rs.getString("apellido"))
                .build();

        Token token = Token.builder()
                .tokenId(rs.getLong("token_id"))
                .brand(rs.getString("brand"))
                .lastFourDigits(rs.getString("last_four_digits"))
                .build();

        return Transaction.builder()
                .transactionId(rs.getLong("transaction_id"))
                .merchant(merchant)
                .customer(customer)
                .token(token)
                .amount(rs.getBigDecimal("amount"))
                .currency(rs.getString("currency"))
                .status(TransactionStatus.valueOf(rs.getString("status")))
                .statusReason(rs.getString("status_reason"))
                .idempotencyKey(rs.getString("idempotency_key"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}