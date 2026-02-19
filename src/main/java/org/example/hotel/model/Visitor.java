package org.example.hotel.model;

import java.util.Objects;

public class Visitor {
    private String id;
    private String fullName;
    private String email;

    public Visitor(String id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasValidEmail() {
        return email != null && email.contains("@") && email.indexOf('@') > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visitor visitor)) return false;
        return Objects.equals(id, visitor.id) &&
                Objects.equals(fullName, visitor.fullName) &&
                Objects.equals(email, visitor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email);
    }
}
