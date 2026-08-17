package com.mipasarela.repository.mysql;

import com.mipasarela.config.DatabaseConnection;
import com.mipasarela.domain.Merchant;
import com.mipasarela.repository.MerchantRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class MerchantMySQLRepository implements MerchantRepository {

    @Override
    public Merchant save(Merchant merchant) {
        String sql = "INSERT INTO merchants (razon_social, nombre_fantasia, cuit, codigo_categoria, direccion, codigo_postal, email, telefono, api_key, secret_key) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, merchant.getSocialReason());
            stmt.setString(2, merchant.getFantasyName());
            stmt.setString(3, merchant.getCuit());
            stmt.setString(4, merchant.getCategoryCode());
            stmt.setString(5, merchant.getAddress());
            stmt.setString(6, merchant.getPostalCode());
            stmt.setString(7, merchant.getEmail());
            stmt.setString(8, merchant.getPhone());
            stmt.setString(9, merchant.getApiKey());
            stmt.setString(10, merchant.getSecretKey());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    merchant.setMerchantId(rs.getLong(1));
                }
            }
            return merchant;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el comercio en MySQL", e);
        }
    }

    @Override
    public Optional<Merchant> findById(Long id) {
        String sql = "SELECT * FROM merchants WHERE merchant_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMerchant(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar comercio por ID en MySQL", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Merchant> findByCuit(String cuit) {
        String sql = "SELECT * FROM merchants WHERE cuit = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cuit);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMerchant(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar comercio por CUIT en MySQL", e);
        }
        return Optional.empty();
    }

    private Merchant mapResultSetToMerchant(ResultSet rs) throws SQLException {
        return Merchant.builder()
                .merchantId(rs.getLong("merchant_id"))
                .socialReason(rs.getString("razon_social"))
                .fantasyName(rs.getString("nombre_fantasia"))
                .cuit(rs.getString("cuit"))
                .categoryCode(rs.getString("codigo_categoria"))
                .address(rs.getString("direccion"))
                .postalCode(rs.getString("codigo_postal"))
                .email(rs.getString("email"))
                .phone(rs.getString("telefono"))
                .apiKey(rs.getString("api_key"))
                .secretKey(rs.getString("secret_key"))
                .build();
    }
}