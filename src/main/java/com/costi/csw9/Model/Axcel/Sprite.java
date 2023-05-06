package com.costi.csw9.Model.Axcel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Sprite {
    private String name;
    private String description;
    private String hint;
    private String location;

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        Sprite other = (Sprite) obj;
        return Objects.equals(name, other.name);
    }
}
