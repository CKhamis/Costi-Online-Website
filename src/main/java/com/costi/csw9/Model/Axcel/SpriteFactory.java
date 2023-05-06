package com.costi.csw9.Model.Axcel;

import java.util.HashSet;
import java.util.Set;

public class SpriteFactory {

    public static HashSet<Sprite> createSprites() {
        HashSet<Sprite> sprites = new HashSet<>();

        Sprite sprite1 = new Sprite("test", "", "", "");
        sprites.add(sprite1);

        return sprites;
    }
}
