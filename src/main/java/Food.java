import processing.core.PApplet;
import processing.core.PVector;

class Food {

    private final SnakeAI snakeAI;
    PVector pos;

    Food(SnakeAI snakeAI) {
        this.snakeAI = snakeAI;
        int x = 400 + snakeAI.SIZE + PApplet.floor(snakeAI.random(38)) * snakeAI.SIZE;
        int y = snakeAI.SIZE + PApplet.floor(snakeAI.random(38)) * snakeAI.SIZE;
        pos = new PVector(x, y);
    }

    public void show() {
        snakeAI.stroke(0);
        snakeAI.fill(255, 0, 0);
        snakeAI.rect(pos.x, pos.y, snakeAI.SIZE, snakeAI.SIZE);
    }

    public Food clone() {
        Food clone = new Food(snakeAI);
        clone.pos.x = pos.x;
        clone.pos.y = pos.y;

        return clone;
    }
}
