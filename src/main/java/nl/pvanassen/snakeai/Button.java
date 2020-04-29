package nl.pvanassen.snakeai;

import processing.core.PConstants;

class Button {
    private final SnakeAI snakeAI;
    float X, Y, W, H;
    String text;

    Button(SnakeAI snakeAI, float x, float y, float w, float h, String t) {
        this.snakeAI = snakeAI;
        X = x;
        Y = y;
        W = w;
        H = h;
        text = t;
    }

    public boolean collide(float x, float y) {
        if (x >= X - W / 2 && x <= X + W / 2 && y >= Y - H / 2 && y <= Y + H / 2) {
            return true;
        }
        return false;
    }

    public void show() {
        snakeAI.fill(255);
        snakeAI.stroke(0);
        snakeAI.rectMode(PConstants.CENTER);
        snakeAI.rect(X, Y, W, H);
        snakeAI.textSize(22);
        snakeAI.textAlign(PConstants.CENTER, PConstants.CENTER);
        snakeAI.fill(0);
        snakeAI.noStroke();
        snakeAI.text(text, X, Y - 3);
    }
}
