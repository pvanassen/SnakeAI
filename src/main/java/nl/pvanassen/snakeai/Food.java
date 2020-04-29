package nl.pvanassen.snakeai;

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

    private final SnakeAI snakeAI;
    private final int cnt;
    final PVector pos;

    Food(SnakeAI snakeAI, int cnt) {
        this.snakeAI = snakeAI;
        pos = locations.get(cnt);
        this.cnt = cnt;
    }

    public void show() {
        snakeAI.stroke(0);
        snakeAI.fill(255, 255, 255);
        snakeAI.text(cnt, pos.x - 10, pos.y - 10);
        snakeAI.fill(255, 0, 0);
        snakeAI.rect(pos.x, pos.y, snakeAI.SIZE, snakeAI.SIZE);
    }

    public Food clone() {
        Food clone = new Food(snakeAI, cnt);
        clone.pos.x = pos.x;
        clone.pos.y = pos.y;

        return clone;
    }
}
