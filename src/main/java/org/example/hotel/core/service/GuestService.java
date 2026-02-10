package org.example.hotel.core.service;

import org.example.hotel.core.model.Guest;
import org.example.hotel.core.repository.impl.InMemoryRepository;

public class GuestService {
    private final InMemoryRepository<Guest> repository = new InMemoryRepository<>();

}
