package com.mipasarela.repository.mysql;

import com.mipasarela.config.DatabaseConnection;
import com.mipasarela.domain.Customer;
import com.mipasarela.domain.Token;
import com.mipasarela.repository.TokenRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class TokenMySQLRepository implements TokenRepository {

    @Override
    public Token save(Token token) {
        String sql = "INSERT INTO tokens (token_value, customer_id, last_four_digits, card_holder_name, expiration_month, expiration_year, brand, status, created_at) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, token.getTokenValue());
            stmt.setLong(2, token.getCustomer().getCustomerId());
            stmt.setString(3, token.getLastFourDigits());
            stmt.setString(4, token.getCardHolderName());
            stmt.setInt(5, token.getExpirationMonth());
            stmt.setInt(6, token.getExpirationYear());
            stmt.setString(7, token.getBrand());
            stmt.setString(8, token.getStatus());
            stmt.setTimestamp(9, Timestamp.valueOf(token.getCreatedAt()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    token.setTokenId(rs.getLong(1));
                }
            }
            return token;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el token en MySQL", e);
        }
    }

    @Override
    public Optional<Token> findById(Long id) {
        String sql = "SELECT tk.*, c.nombre, c.apellido, c.email FROM tokens tk " +
                "JOIN customers c ON tk.customer_id = c.customer_id " +
                "WHERE tk.token_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToToken(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar token por ID en MySQL", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) {
        if (tokenValue == null)
            return Optional.empty();

        String sql = "SELECT tk.*, c.nombre, c.apellido, c.email FROM tokens tk " +
                "JOIN customers c ON tk.customer_id = c.customer_id " +
                "WHERE tk.token_value = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tokenValue);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToToken(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar token por valor en MySQL", e);
        }
        return Optional.empty();
    }

    private Token mapResultSetToToken(ResultSet rs) throws SQLException {
        Customer customer = Customer.builder()
                .customerId(rs.getLong("customer_id"))
                .name(rs.getString("nombre"))
                .lastName(rs.getString("apellido"))
                .email(rs.getString("email"))
                .build();

        return Token.builder()
                .tokenId(rs.getLong("token_id"))
                .tokenValue(rs.getString("token_value"))
                .customer(customer)
                .lastFourDigits(rs.getString("last_four_digits"))
                .cardHolderName(rs.getString("card_holder_name"))
                .expirationMonth(rs.getInt("expiration_month"))
                .expirationYear(rs.getInt("expiration_year"))
                .brand(rs.getString("brand"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}