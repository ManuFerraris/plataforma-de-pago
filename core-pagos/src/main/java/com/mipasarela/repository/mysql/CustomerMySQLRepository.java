package com.mipasarela.repository.mysql;

import com.mipasarela.config.DatabaseConnection;
import com.mipasarela.domain.Customer;
import com.mipasarela.repository.CustomerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerMySQLRepository implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (numero_dni, categoria_consumidor, nombre, apellido, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getDniNumber());
            stmt.setString(2, customer.getCustomerCategory());
            stmt.setString(3, customer.getName());
            stmt.setString(4, customer.getLastName());
            stmt.setString(5, customer.getEmail());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setCustomerId(rs.getLong(1));
                }
            }
            return customer;

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cliente en MySQL", e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por ID en MySQL", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByNumeroDni(String numeroDni) {
        String sql = "SELECT * FROM customers WHERE numero_dni = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroDni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por DNI en MySQL", e);
        }
        return Optional.empty();
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return Customer.builder()
                .customerId(rs.getLong("customer_id"))
                .dniNumber(rs.getString("numero_dni"))
                .customerCategory(rs.getString("categoria_consumidor"))
                .name(rs.getString("nombre"))
                .lastName(rs.getString("apellido"))
                .email(rs.getString("email"))
                .build();
    }
}
