package org.example.hotel.core.repository;

import org.example.hotel.core.model.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JdbcRepository<T extends Entity> implements IRepository<T> {

    // Конкретний клас реалізує маппінг
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract String tableName();
    protected abstract void bindInsert(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void bindUpdate(PreparedStatement ps, T entity) throws SQLException;
    protected abstract String insertSql();
    protected abstract String updateSql();

    @Override
    public void create(T entity) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     insertSql(), Statement.RETURN_GENERATED_KEYS)) {
            bindInsert(ps, entity);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RepositoryException("create failed", e);
        }
    }

    @Override
    public Optional<T> read(int id) {
        String sql = "SELECT * FROM " + tableName() + " WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("read failed", e);
        }
    }

    @Override
    public List<T> readAll() {
        String sql = "SELECT * FROM " + tableName();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<T> result = new ArrayList<>();
            while (rs.next()) result.add(mapRow(rs));
            return result;
        } catch (SQLException e) {
            throw new RepositoryException("readAll failed", e);
        }
    }

    @Override
    public void update(T entity) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql())) {
            bindUpdate(ps, entity);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("update failed", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("delete failed", e);
        }
    }
}