package nl.pvanassen.snakeai;

import com.google.common.collect.Lists;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Population {

    private final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);

    private final SnakeAI snakeAI;
    List<Snake> snakes;
    Snake bestSnake;

    int bestSnakeScore = 0;
    int gen = 0;
    int samebest = 0;

    float bestFitness = 0;
    float fitnessSum = 0;

    Population(SnakeAI snakeAI, int size) {
        this.snakeAI = snakeAI;
        snakes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            snakes.add(new Snake(snakeAI));
        }
        bestSnake = snakes.get(0).clone();
        bestSnake.replay = true;
    }

    public boolean done() {  //check if all the snakes in the population are dead
        for (Snake snake : snakes) {
            if (!snake.dead) {
                return false;
            }
        }
        if (!bestSnake.dead) {
            return false;
        }
        return true;
    }

    public void update() {  //update all the snakes in the generation
        List<Snake> livingSnakes = Stream.concat(snakes.stream(), Stream.of(bestSnake))
                .filter(snake -> !snake.dead)
                .collect(Collectors.toList());
        int partitionSize = (int)Math.ceil(livingSnakes.size() / (float)Runtime.getRuntime().availableProcessors());
        Lists.partition(livingSnakes, partitionSize)
                .stream()
                .map(Task::new)
                .forEach(pool::submit);

        if (!pool.awaitQuiescence(1, TimeUnit.SECONDS)) {
            throw new IllegalArgumentException("Pool not done: " + pool);
        }
    }

    static class Task implements Runnable {
        private final List<Snake> snakes;

        Task(List<Snake> snakes) {
            this.snakes = snakes;
        }

        @Override
        public void run() {
            for (Snake snake : snakes) {
                snake.look();
                snake.think();
                snake.move();
            }
        }
    }

    public void show(PApplet parent) {  //show either the best snake or all the snakes
        if (SnakeAI.replayBest) {
            bestSnake.show(parent);
            bestSnake.brain.show(parent, 0, 0, 360, 790, bestSnake.vision, bestSnake.decision);  //show the brain of the best snake
        } else {
            for (Snake snake : snakes) {
                snake.show(parent);
            }
        }
    }

    public void setBestSnake() {  //set the best snake of the generation
        float max = 0;
        int maxIndex = 0;
        for (int i = 0; i < snakes.size(); i++) {
            if (snakes.get(i).fitness > max) {
                max = snakes.get(i).fitness;
                maxIndex = i;
            }
        }
        if (max > bestFitness) {
            bestFitness = max;
            bestSnake = snakes.get(maxIndex).cloneForReplay();
            bestSnakeScore = snakes.get(maxIndex).score;
            //samebest = 0;
            //mutationRate = defaultMutation;
        } else {
            bestSnake = bestSnake.cloneForReplay();
     /*
     samebest++;
     if(samebest > 2) {  //if the best snake has remained the same for more than 3 generations, raise the mutation rate
        mutationRate *= 2;
        samebest = 0;
     }*/
        }
    }

    public Snake selectParent() {  //selects a random number in range of the fitnesssum and if a snake falls in that range then select it
        float rand = snakeAI.random(fitnessSum);
        float summation = 0;
        for (Snake snake : snakes) {
            summation += snake.fitness;
            if (summation > rand) {
                return snake;
            }
        }
        return snakes.get(0);
    }

    public void naturalSelection() {
        List<Snake> newSnakes = new ArrayList<>(snakes.size());

        setBestSnake();
        calculateFitnessSum();

        newSnakes.add(bestSnake.clone());  //add the best snake of the prior generation into the new generation
        for (int i = 1; i < snakes.size(); i++) {
            Snake child = selectParent().crossover(selectParent());
            child.mutate();
            newSnakes.add(child);
        }
        snakes.clear();
        snakes.addAll(newSnakes);
        snakeAI.evolution.add(bestSnakeScore);
        gen += 1;
    }
//
//    public void mutate() {
//        for (Snake snake : snakes) {
//            snake.mutate();
//        }
//    }

    public void calculateFitness() {  //calculate the fitnesses for each snake
        for (Snake snake : snakes) {
            snake.calculateFitness();
        }
    }

    public void calculateFitnessSum() {  //calculate the sum of all the snakes fitnesses
        fitnessSum = 0;
        for (Snake snake : snakes) {
            fitnessSum += snake.fitness;
        }
    }
}
