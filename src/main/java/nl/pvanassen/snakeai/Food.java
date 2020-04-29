package nl.pvanassen.snakeai;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Food {

    private static final List<PVector> locations = new LinkedList<>();
    static {
        Random random = new Random();
        for (int i=0;i!=1024;i++) {
            int x = 400 + SnakeAI.SIZE + random.nextInt(38) * SnakeAI.SIZE;
            int y = SnakeAI.SIZE + random.nextInt(38) * SnakeAI.SIZE;
            locations.add(new PVector(x, y));
        }
    }

    private final int cnt;
    final PVector pos;

    Food(int cnt) {
        pos = locations.get(cnt);
        this.cnt = cnt;
    }

    public void show(PApplet parent) {
        parent.stroke(0);
        parent.fill(255, 255, 255);
        parent.text(cnt, pos.x - 10, pos.y - 10);
        parent.fill(255, 0, 0);
        parent.rect(pos.x, pos.y, SnakeAI.SIZE, SnakeAI.SIZE);
    }

    public Food clone() {
        Food clone = new Food(cnt);
        clone.pos.x = pos.x;
        clone.pos.y = pos.y;

        return clone;
    }
}
