package org.example.hotel.core.repository;

import org.example.hotel.core.model.Hotel;
import org.example.hotel.core.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository extends JdbcRepository<Room> {

    @Override
    protected String tableName() { return "rooms"; }

    @Override
    protected String insertSql() {
        return "INSERT INTO rooms (hotel_id, local_id, type, price) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE rooms SET price = ? WHERE id = ?";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Room r) throws SQLException {
        ps.setInt(1, r.getHotel().getId());
        ps.setInt(2, r.getLocalId());
        ps.setString(3, r.getType());
        ps.setInt(4, r.getPrice());
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Room r) throws SQLException {
        ps.setInt(1, r.getPrice());  // type і localId — immutable
        ps.setInt(2, r.getId());
    }

    @Override
    protected Room mapRow(ResultSet rs) throws SQLException {
        // Без hotel тут не обійтись — кидаємо exception щоб не викликали напряму
        throw new UnsupportedOperationException("Use mapRow(ResultSet, Hotel)");
    }

    public Room mapRow(ResultSet rs, Hotel hotel) throws SQLException {
        Room room = new Room(
                hotel,
                rs.getInt("local_id"),
                rs.getString("type"),
                rs.getInt("price")
        );
        room.setId(rs.getInt("id"));
        return room;
    }

    public List<Room> findByHotelId(int hotelId, Hotel hotel) {
        String sql = "SELECT * FROM rooms WHERE hotel_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Room> result = new ArrayList<>();
                while (rs.next()) result.add(mapRow(rs, hotel));
                return result;
            }
        } catch (SQLException e) {
            throw new RepositoryException("findByHotelId failed", e);
        }
    }

    public Optional<Room> findById(int id, Hotel hotel) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs, hotel));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("findById failed", e);
        }
    }
    public List<Integer> findReservationIds(int roomId) {
        String sql = "SELECT id FROM reservations WHERE room_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getInt("id"));
                return ids;
            }
        } catch (SQLException e) {
            throw new RepositoryException("findReservationIds failed", e);
        }
    }
}