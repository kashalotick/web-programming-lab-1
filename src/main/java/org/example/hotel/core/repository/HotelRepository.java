package org.example.hotel.core.repository;

import org.example.hotel.core.model.Hotel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HotelRepository extends JdbcRepository<Hotel> {

    @Override
    protected String tableName() {
        return "hotels";
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO hotels (name) VALUES (?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE hotels SET name = ? WHERE id = ?";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Hotel h) throws SQLException {
        ps.setString(1, h.getName());
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Hotel h) throws SQLException {
        ps.setString(1, h.getName());
        ps.setInt(2, h.getId());
    }

    @Override
    protected Hotel mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Hotel hotel = new Hotel(name);
        hotel.setId(id);
        // кімнати НЕ завантажуємо тут
        return hotel;
    }

    public Optional<Hotel> readWithRooms(int id, RoomRepository roomRepository) {
        return read(id).map(hotel -> {
            roomRepository.findByHotelId(hotel.getId(), hotel).forEach(hotel::addRoom);
            return hotel;
        });
    }

    public List<Hotel> readAllWithRooms(RoomRepository roomRepository) {
        return readAll().stream().map(hotel -> {
            roomRepository.findByHotelId(hotel.getId(), hotel).forEach(hotel::addRoom);
            return hotel;
        }).toList();
    }
}