package org.example.hotel.core.repository;

import org.example.hotel.core.model.Guest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestRepository extends JdbcRepository<Guest> {

    @Override
    protected String tableName() { return "guests"; }

    @Override
    protected String insertSql() {
        return "INSERT INTO guests (name) VALUES (?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE guests SET name = ? WHERE id = ?";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Guest g) throws SQLException {
        ps.setString(1, g.getName());
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Guest g) throws SQLException {
        ps.setString(1, g.getName());
        ps.setInt(2, g.getId());
    }

    @Override
    protected Guest mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Guest guest = new Guest(name);
        guest.setId(id);
        return guest;
    }

    // Гість з усіма резерваціями — викликається після того як є ReservationRepository
    public Guest readWithReservations(int id, ReservationRepository reservationRepo) {
        Guest guest = read(id).orElseThrow();
        reservationRepo.findByGuestId(id, guest).forEach(guest::addReservation);
        return guest;
    }

    public List<Integer> findReservationIds(int guestId) {
        String sql = "SELECT id FROM reservations WHERE guest_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
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