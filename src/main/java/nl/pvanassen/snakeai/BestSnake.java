package nl.pvanassen.snakeai;

import com.google.common.collect.Lists;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BestSnake {

    private final Snake bestSnake;

    BestSnake(Snake snake) {
        bestSnake = snake;
        bestSnake.replay = true;
    }

    public boolean done() {  //check if all the snakes in the population are dead
        return bestSnake.dead;
    }

    public void update() {  //update all the snakes in the generation
        bestSnake.look();
        bestSnake.think();
        bestSnake.move();
    }

    public void show(PApplet parent) {  //show either the best snake or all the snakes
        bestSnake.show(parent);
        bestSnake.brain.show(parent, 0, 0, 360, 790, bestSnake.vision, bestSnake.decision);  //show the brain of the best snake
    }

    public int getScore() {
        return bestSnake.score;
    }

    public int getLifeLeft() {
        return bestSnake.lifeLeft;
    }

    public Snake clone() {
        return bestSnake.clone();
    }
}
