package org.example.hotel.core.repository;

import org.example.hotel.core.model.Guest;
import org.example.hotel.core.model.Hotel;
import org.example.hotel.core.model.Reservation;
import org.example.hotel.core.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ReservationRepository extends JdbcRepository<Reservation> {

    @Override
    protected String tableName() { return "reservations"; }

    @Override
    protected String insertSql() {
        return """
            INSERT INTO reservations (guest_id, room_id, check_in, check_out, grand_total)
            VALUES (?, ?, ?, ?, ?)
        """;
    }

    @Override
    protected String updateSql() {
        // Резервація immutable — update не потрібен, але інтерфейс вимагає
        throw new UnsupportedOperationException("Reservations are immutable");
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Reservation r) throws SQLException {
        ps.setInt(1, r.getGuest().getId());
        ps.setInt(2, r.getRoom().getId());
        ps.setDate(3, Date.valueOf(r.getCheckIn()));
        ps.setDate(4, Date.valueOf(r.getCheckOut()));
        ps.setInt(5, r.getGrandTotal());
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Reservation r) throws SQLException {
        throw new UnsupportedOperationException("Reservations are immutable");
    }

    @Override
    protected Reservation mapRow(ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("Use mapRow(ResultSet, Room, Guest)");
    }

    public Reservation mapRow(ResultSet rs, Room room, Guest guest) throws SQLException {
        Reservation reservation = new Reservation(
                guest,
                room,
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getInt("grand_total")
        );
        reservation.setId(rs.getInt("id"));
        return reservation;
    }

    public List<Reservation> findByGuestId(int guestId, Guest guest) {
        String sql = """
            SELECT r.*, rm.local_id, rm.type, rm.price, rm.hotel_id
            FROM reservations r
            JOIN rooms rm ON rm.id = r.room_id
            WHERE r.guest_id = ?
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Reservation> result = new ArrayList<>();
                while (rs.next()) {
                    // Room без hotel — потрібно окремо дістати якщо треба повний об'єкт
                    // Для простоти — створюємо Room з даних JOIN
                    Room room = buildRoomFromJoin(rs, guest);
                    result.add(mapRow(rs, room, guest));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RepositoryException("findByGuestId failed", e);
        }
    }

    public Optional<Reservation> read(int id, HotelRepository hotelRepo,
                                      RoomRepository roomRepo, GuestRepository guestRepo) {
        String sql = """
        SELECT r.*, rm.local_id, rm.type, rm.price, rm.hotel_id
        FROM reservations r
        JOIN rooms rm ON rm.id = r.room_id
        WHERE r.id = ?
    """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                int guestId = rs.getInt("guest_id");
                int hotelId = rs.getInt("hotel_id");

                Guest guest = guestRepo.read(guestId)
                        .orElseThrow(() -> new RepositoryException("Guest not found", null));
                Hotel hotel = hotelRepo.read(hotelId)
                        .orElseThrow(() -> new RepositoryException("Hotel not found", null));
                Room room = buildRoomFromJoin(rs, hotel);

                return Optional.of(mapRow(rs, room, guest));
            }
        } catch (SQLException e) {
            throw new RepositoryException("read reservation failed", e);
        }
    }

    public List<Reservation> findByRoomId(int roomId, Room room, Guest guest) {
        String sql = "SELECT * FROM reservations WHERE room_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Reservation> result = new ArrayList<>();
                while (rs.next()) result.add(mapRow(rs, room, guest));
                return result;
            }
        } catch (SQLException e) {
            throw new RepositoryException("findByRoomId failed", e);
        }
    }

    // Скасування — видаляє з БД
    public void cancel(int reservationId) {
        delete(reservationId);
    }

    private Room buildRoomFromJoin(ResultSet rs, Hotel hotel) throws SQLException {
        Room room = new Room(
                hotel,
                rs.getInt("local_id"),
                rs.getString("type"),
                rs.getInt("price")
        );
        room.setId(rs.getInt("room_id"));
        return room;
    }

    private Room buildRoomFromJoin(ResultSet rs, Guest guest) throws SQLException {
        // Спрощена кімната без повного hotel об'єкта — для відображення резервацій гостя
        Hotel placeholder = new Hotel("");
        placeholder.setId(rs.getInt("hotel_id"));
        Room room = new Room(
                placeholder,
                rs.getInt("local_id"),
                rs.getString("type"),
                rs.getInt("price")
        );
        room.setId(rs.getInt("room_id"));
        return room;
    }
}