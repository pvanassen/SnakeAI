import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

class Snake {

    private final SnakeAI snakeAI;
    int score = 1;
    int lifeLeft = 200;  //amount of moves the snake can make before it dies
    int lifetime = 0;  //amount of time the snake has been alive
    int xVel, yVel;
    int foodItterate = 0;  //itterator to run through the foodlist (used for replay)

    float fitness = 0;

    boolean dead = false;
    boolean replay = false;  //if this snake is a replay of best snake

    float[] vision;  //snakes vision
    float[] decision;  //snakes decision

    PVector head;

    ArrayList<PVector> body;  //snakes body
    ArrayList<Food> foodList;  //list of food positions (used to replay the best snake)

    Food food;
    NeuralNet brain;

    Snake(SnakeAI snakeAI) {
        this(snakeAI, snakeAI.hidden_layers);
    }

    Snake(SnakeAI snakeAI, int layers) {
        this.snakeAI = snakeAI;
        head = new PVector(800, snakeAI.height / 2);
        food = new Food(snakeAI);
        body = new ArrayList<PVector>();
        if (!snakeAI.humanPlaying) {
            vision = new float[24];
            decision = new float[4];
            foodList = new ArrayList<Food>();
            foodList.add(food.clone());
            brain = new NeuralNet(snakeAI, 24, snakeAI.hidden_nodes, 4, layers);
            body.add(new PVector(800, (snakeAI.height / 2) + snakeAI.SIZE));
            body.add(new PVector(800, (snakeAI.height / 2) + (2 * snakeAI.SIZE)));
            score += 2;
        }
    }

    Snake(SnakeAI snakeAI, ArrayList<Food> foods) {
        this.snakeAI = snakeAI;  //this constructor passes in a list of food positions so that a replay can replay the best snake
        replay = true;
        vision = new float[24];
        decision = new float[4];
        body = new ArrayList<PVector>();
        foodList = new ArrayList<Food>(foods.size());
        for (Food f : foods) {  //clone all the food positions in the foodlist
            foodList.add(f.clone());
        }
        food = foodList.get(foodItterate);
        foodItterate++;
        head = new PVector(800, snakeAI.height / 2);
        body.add(new PVector(800, (snakeAI.height / 2) + snakeAI.SIZE));
        body.add(new PVector(800, (snakeAI.height / 2) + (2 * snakeAI.SIZE)));
        score += 2;
    }

    public boolean bodyCollide(float x, float y) {  //check if a position collides with the snakes body
        for (int i = 0; i < body.size(); i++) {
            if (x == body.get(i).x && y == body.get(i).y) {
                return true;
            }
        }
        return false;
    }

    public boolean foodCollide(float x, float y) {  //check if a position collides with the food
        if (x == food.pos.x && y == food.pos.y) {
            return true;
        }
        return false;
    }

    public boolean wallCollide(float x, float y) {  //check if a position collides with the wall
        if (x >= snakeAI.width - (snakeAI.SIZE) || x < 400 + snakeAI.SIZE || y >= snakeAI.height - (snakeAI.SIZE) || y < snakeAI.SIZE) {
            return true;
        }
        return false;
    }

    public void show() {  //show the snake
        food.show();
        snakeAI.fill(255);
        snakeAI.stroke(0);
        for (int i = 0; i < body.size(); i++) {
            snakeAI.rect(body.get(i).x, body.get(i).y, snakeAI.SIZE, snakeAI.SIZE);
        }
        if (dead) {
            snakeAI.fill(150);
        } else {
            snakeAI.fill(255);
        }
        snakeAI.rect(head.x, head.y, snakeAI.SIZE, snakeAI.SIZE);
    }

    public void move() {  //move the snake
        if (!dead) {
            if (!snakeAI.humanPlaying && !snakeAI.modelLoaded) {
                lifetime++;
                lifeLeft--;
            }
            if (foodCollide(head.x, head.y)) {
                eat();
            }
            shiftBody();
            if (wallCollide(head.x, head.y)) {
                dead = true;
            } else if (bodyCollide(head.x, head.y)) {
                dead = true;
            } else if (lifeLeft <= 0 && !snakeAI.humanPlaying) {
                dead = true;
            }
        }
    }

    public void eat() {  //eat food
        int len = body.size() - 1;
        score++;
        if (!snakeAI.humanPlaying && !snakeAI.modelLoaded) {
            if (lifeLeft < 500) {
                if (lifeLeft > 400) {
                    lifeLeft = 500;
                } else {
                    lifeLeft += 100;
                }
            }
        }
        if (len >= 0) {
            body.add(new PVector(body.get(len).x, body.get(len).y));
        } else {
            body.add(new PVector(head.x, head.y));
        }
        if (!replay) {
            food = new Food(snakeAI);
            while (bodyCollide(food.pos.x, food.pos.y)) {
                food = new Food(snakeAI);
            }
            if (!snakeAI.humanPlaying) {
                foodList.add(food);
            }
        } else {  //if the snake is a replay, then we dont want to create new random foods, we want to see the positions the best snake had to collect
            food = foodList.get(foodItterate);
            foodItterate++;
        }
    }

    public void shiftBody() {  //shift the body to follow the head
        float tempx = head.x;
        float tempy = head.y;
        head.x += xVel;
        head.y += yVel;
        float temp2x;
        float temp2y;
        for (int i = 0; i < body.size(); i++) {
            temp2x = body.get(i).x;
            temp2y = body.get(i).y;
            body.get(i).x = tempx;
            body.get(i).y = tempy;
            tempx = temp2x;
            tempy = temp2y;
        }
    }

    public Snake cloneForReplay() {  //clone a version of the snake that will be used for a replay
        Snake clone = new Snake(snakeAI, foodList);
        clone.brain = brain.clone();
        return clone;
    }

    public Snake clone() {  //clone the snake
        Snake clone = new Snake(snakeAI, snakeAI.hidden_layers);
        clone.brain = brain.clone();
        return clone;
    }

    public Snake crossover(Snake parent) {  //crossover the snake with another snake
        Snake child = new Snake(snakeAI, snakeAI.hidden_layers);
        child.brain = brain.crossover(parent.brain);
        return child;
    }

    public void mutate() {  //mutate the snakes brain
        brain.mutate(snakeAI.mutationRate);
    }

    public void calculateFitness() {  //calculate the fitness of the snake
        if (score < 10) {
            fitness = PApplet.floor(lifetime * lifetime) * PApplet.pow(2, score);
        } else {
            fitness = PApplet.floor(lifetime * lifetime);
            fitness *= PApplet.pow(2, 10);
            fitness *= (score - 9);
        }
    }

    public void look() {  //look in all 8 directions and check for food, body and wall
        vision = new float[24];
        float[] temp = lookInDirection(new PVector(-snakeAI.SIZE, 0));
        vision[0] = temp[0];
        vision[1] = temp[1];
        vision[2] = temp[2];
        temp = lookInDirection(new PVector(-snakeAI.SIZE, -snakeAI.SIZE));
        vision[3] = temp[0];
        vision[4] = temp[1];
        vision[5] = temp[2];
        temp = lookInDirection(new PVector(0, -snakeAI.SIZE));
        vision[6] = temp[0];
        vision[7] = temp[1];
        vision[8] = temp[2];
        temp = lookInDirection(new PVector(snakeAI.SIZE, -snakeAI.SIZE));
        vision[9] = temp[0];
        vision[10] = temp[1];
        vision[11] = temp[2];
        temp = lookInDirection(new PVector(snakeAI.SIZE, 0));
        vision[12] = temp[0];
        vision[13] = temp[1];
        vision[14] = temp[2];
        temp = lookInDirection(new PVector(snakeAI.SIZE, snakeAI.SIZE));
        vision[15] = temp[0];
        vision[16] = temp[1];
        vision[17] = temp[2];
        temp = lookInDirection(new PVector(0, snakeAI.SIZE));
        vision[18] = temp[0];
        vision[19] = temp[1];
        vision[20] = temp[2];
        temp = lookInDirection(new PVector(-snakeAI.SIZE, snakeAI.SIZE));
        vision[21] = temp[0];
        vision[22] = temp[1];
        vision[23] = temp[2];
    }

    public float[] lookInDirection(PVector direction) {  //look in a direction and check for food, body and wall
        float look[] = new float[3];
        PVector pos = new PVector(head.x, head.y);
        float distance = 0;
        boolean foodFound = false;
        boolean bodyFound = false;
        pos.add(direction);
        distance += 1;
        while (!wallCollide(pos.x, pos.y)) {
            if (!foodFound && foodCollide(pos.x, pos.y)) {
                foodFound = true;
                look[0] = 1;
            }
            if (!bodyFound && bodyCollide(pos.x, pos.y)) {
                bodyFound = true;
                look[1] = 1;
            }
            if (replay && snakeAI.seeVision) {
                snakeAI.stroke(0, 255, 0);
                snakeAI.point(pos.x, pos.y);
                if (foodFound) {
                    snakeAI.noStroke();
                    snakeAI.fill(255, 255, 51);
                    snakeAI.ellipseMode(PConstants.CENTER);
                    snakeAI.ellipse(pos.x, pos.y, 5, 5);
                }
                if (bodyFound) {
                    snakeAI.noStroke();
                    snakeAI.fill(102, 0, 102);
                    snakeAI.ellipseMode(PConstants.CENTER);
                    snakeAI.ellipse(pos.x, pos.y, 5, 5);
                }
            }
            pos.add(direction);
            distance += 1;
        }
        if (replay && snakeAI.seeVision) {
            snakeAI.noStroke();
            snakeAI.fill(0, 255, 0);
            snakeAI.ellipseMode(PConstants.CENTER);
            snakeAI.ellipse(pos.x, pos.y, 5, 5);
        }
        look[2] = 1 / distance;
        return look;
    }

    public void think() {  //think about what direction to move
        decision = brain.output(vision);
        int maxIndex = 0;
        float max = 0;
        for (int i = 0; i < decision.length; i++) {
            if (decision[i] > max) {
                max = decision[i];
                maxIndex = i;
            }
        }

        switch (maxIndex) {
            case 0:
                moveUp();
                break;
            case 1:
                moveDown();
                break;
            case 2:
                moveLeft();
                break;
            case 3:
                moveRight();
                break;
        }
    }

    public void moveUp() {
        if (yVel != snakeAI.SIZE) {
            xVel = 0;
            yVel = -snakeAI.SIZE;
        }
    }

    public void moveDown() {
        if (yVel != -snakeAI.SIZE) {
            xVel = 0;
            yVel = snakeAI.SIZE;
        }
    }

    public void moveLeft() {
        if (xVel != snakeAI.SIZE) {
            xVel = -snakeAI.SIZE;
            yVel = 0;
        }
    }

    public void moveRight() {
        if (xVel != -snakeAI.SIZE) {
            xVel = snakeAI.SIZE;
            yVel = 0;
        }
    }
}
