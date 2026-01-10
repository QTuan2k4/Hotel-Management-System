package com.hms.room.repository;

import com.hms.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByCode(String code);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Room r WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.code) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR r.status = :status) " +
           "AND (:type IS NULL OR :type = '' OR r.type = :type) " +
           "AND (:minPrice IS NULL OR r.pricePerNight >= :minPrice) " +
           "AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice)")
    java.util.List<Room> searchRooms(@org.springframework.data.repository.query.Param("query") String query, 
                                     @org.springframework.data.repository.query.Param("status") String status, 
                                     @org.springframework.data.repository.query.Param("type") String type,
                                     @org.springframework.data.repository.query.Param("minPrice") java.math.BigDecimal minPrice,
                                     @org.springframework.data.repository.query.Param("maxPrice") java.math.BigDecimal maxPrice);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r.type FROM Room r")
    java.util.List<String> findDistinctTypes();
}
