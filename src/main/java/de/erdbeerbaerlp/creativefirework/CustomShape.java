package de.erdbeerbaerlp.creativefirework;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum CustomShape{
	SMALL_BALL(0, "small_ball"),
    LARGE_BALL(1, "large_ball"),
    STAR(2, "star"),
    CREEPER(3, "creeper"),
    BURST(4, "burst"),
	TEXT(5, "text");
	
    private static final CustomShape[] shapes = Arrays.stream(values()).sorted(Comparator.comparingInt((shape) -> {
       return shape.ID;
    })).toArray((shapeID) -> {
       return new CustomShape[shapeID];
    });
    private final int ID;
    private final String name;

    private CustomShape(int ID, String name) {
       this.ID = ID;
       this.name = name;
    }

    public int getID() {
       return this.ID;
    }

    @OnlyIn(Dist.CLIENT)
    public String getName() {
       return this.name;
    }

    @OnlyIn(Dist.CLIENT)
    public static CustomShape getShape(int index) {
       return index >= 0 && index < shapes.length ? shapes[index] : SMALL_BALL;
    }
}
