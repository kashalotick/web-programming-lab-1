package org.example.hotel.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    // Створюємо анонімний клас для тестування абстрактного Entity
    static class TestEntity extends Entity {
        public TestEntity() { super(); }
        public TestEntity(int id) { super(id); }
        @Override
        public java.io.Serializable toDTO() { return null; }
    }

    @BeforeEach
    void setUp() {
        Entity.clear();
    }

    @Test
    void shouldIncrementIdAutomatically() {
        TestEntity e1 = new TestEntity();
        TestEntity e2 = new TestEntity();

        assertEquals(0, e1.getId());
        assertEquals(1, e2.getId());
    }

    @Test
    void shouldRespectManualId() {
        TestEntity e = new TestEntity(100);
        assertEquals(100, e.getId());

        // Наступний має бути 101
        TestEntity next = new TestEntity();
        assertEquals(101, next.getId());
    }

    @Test
    void testStoreAndRead() {
        TestEntity e = new TestEntity();
        Entity.create(e);

        TestEntity found = Entity.read(TestEntity.class, e.getId());
        assertEquals(e, found);
    }
}