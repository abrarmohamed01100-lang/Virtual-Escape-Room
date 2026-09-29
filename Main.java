import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

// Player class
class Player {
    private String name;
    private int score;
    private int tries;
    
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.tries = 3;
    }
    
    public void addScore(int points) { 
        score += points; 
    }
    
    public void loseTry() { 
        tries--; 
    }
    
    // lose points when take hint
    public void useClue() {
        if (score >= 5) score -= 5;
        else score = 0;
        System.out.println("Hint used! -5 points");
    }
    
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getTries() { return tries; }
    
    public void showStatus() {
        System.out.println("\n--- Your Status ---");
        System.out.println("Name: " + name + " | Score: " + score + " | Tries: " + tries);
    }
}

// abstract class for puzzles
abstract class Puzzle {
    protected String question, answer;
    protected int points;
    protected boolean solved = false;
    
    public Puzzle(String q, String a, int p) { 
        question=q; 
        answer=a; 
        points=p; 
    }
    
    public void displayQuestion() { 
        System.out.println("\n" + question); 
    }
    
    public int getPoints() { return points; }
    public boolean isSolved() { return solved; }
    public void setSolved() { solved = true; }
    
    // each puzzle checks answer in different way
    public abstract boolean check(String x);
}

class WordPuzzle extends Puzzle {
    public WordPuzzle(String q, String a, int p) { super(q,a,p); }
    @Override
    public boolean check(String x) { 
        return x.equalsIgnoreCase(answer); 
    }
}

class NumberPuzzle extends Puzzle {
    public NumberPuzzle(String q, String a, int p) { super(q,a,p); }
    @Override
    public boolean check(String x) { 
        return x.equals(answer); 
    }
}

// Clue class
class Clue {
    private String name, text;
    private boolean found;
    
    public Clue(String n, String t) { 
        name=n; 
        text=t; 
        found=false; 
    }
    
    public void find() { found=true; }
    public boolean isFound() { return found; }
    public void display() { 
        System.out.println("- " + name + ": " + text); 
    }
}

// Room class
class Room {
    private String name, text;
    private ArrayList<Clue> clues;
    
    public Room(String n, String t) { 
        name=n; 
        text=t; 
        clues=new ArrayList<>(); 
    }
    
    public void addClue(Clue c) { clues.add(c); }
    
    public void displayRoom() { 
        System.out.println("\n--- " + name + " ---\n" + text); 
    }
    
    // search for clues one by one
    public void search() {
        for (int i=0; i<clues.size(); i++) {
            if(!clues.get(i).isFound()){
                clues.get(i).find();
                System.out.println("\nYou found a clue!");
                clues.get(i).display();
                return;
            }
        }
        System.out.println("\nNothing new, search again.");
    }
    
    public void showClues() {
        System.out.println("\n--- Clues You Found ---");
        for (int i=0; i<clues.size(); i++) 
            if(clues.get(i).isFound()) 
                clues.get(i).display();
    }
    
    public boolean allFound() {
        for (int i=0; i<clues.size(); i++) 
            if (!clues.get(i).isFound()) {
                return false;}
        return true;
    }
}

// Game class - main logic
class Game {
    private Player p;
    private Room room;
    private ArrayList<Puzzle> puzzles;
    private int hintNumber=0;
    private Scanner input=new Scanner(System.in);
    private boolean escaped=false;
    
    public Game(Player p) {
        this.p=p;
        room=new Room("The Old Room","A dark room with a desk, painting and clock.");
        room.addClue(new Clue("Old Clock","Number 1"));
        room.addClue(new Clue("Painting","Number 2"));
        room.addClue(new Clue("Old Box","Number 3"));
        puzzles=new ArrayList<>();
        puzzles.add(new WordPuzzle("What is the opposite of 'on'?","off",40));
        puzzles.add(new NumberPuzzle("How many bits in 1 byte?","8",60));
    }
    
    public void start() {
        System.out.println("==============================");
        System.out.println(" VIRTUAL ESCAPE ROOM");
        System.out.println("==============================");
        System.out.println("Welcome : " + p.getName());
        room.displayRoom();
        play();
    }
    
    public void printMenu() {
        System.out.println("\n1. Solve Puzzle \n2. Search Room \n3. Take Hint \n4. My Status \n5. Show Clues \n6. Open Door \n7. Show All Scores \n8. Exit");
        System.out.print("Choose number: ");
    }
    
    public int getUserChoice() {
        try { 
            int c=input.nextInt(); 
            input.nextLine(); 
            return c; 
        }
        catch(Exception e){ 
            input.nextLine(); 
            System.out.println("Please enter a number."); 
            return -1; 
        }
    }
    
    public void play() {
        while(p.getTries()>0 && !escaped){
            printMenu();
            int x=getUserChoice();
            if(x==1) solveOnePuzzle();
            else if(x==2) room.search();
            else if(x==3) useHint();
            else if(x==4) p.showStatus();
            else if(x==5) room.showClues();
            else if(x==6) finalDoor();
            else if(x==7) showHighScores();
            else if(x==8){ System.out.println("Goodbye!"); break; }
            else System.out.println("Wrong choice!");
        }
        if(p.getTries()==0){ 
            System.out.println("\nGame Over! No tries left."); 
            saveScore(); 
        }
    }
    
    public void solveOnePuzzle() {
        for(Puzzle pz: puzzles) 
            if(!pz.isSolved()){
                pz.displayQuestion();
                System.out.print("Your answer: ");
                String ans=input.nextLine();
                if(pz.check(ans)){
                    System.out.println("Correct! +"+pz.getPoints());
                    p.addScore(pz.getPoints());
                    pz.setSolved();
                } else {
                    System.out.println("Wrong! -1 try");
                    p.loseTry();
                }
                return;
            }
        System.out.println("You solved all puzzles!");
    }
    
    public void useHint() {
        p.useClue(); 
        hintNumber++;
        if(hintNumber==1) System.out.println("Hint 1: First number is on the Clock.");
        else if(hintNumber==2) System.out.println("Hint 2: Second number is behind the Painting.");
        else if(hintNumber==3) System.out.println("Hint 3: Last number is inside the Box.");
        else System.out.println("No more hints.");
    }
    
    public void finalDoor() {
        if(!room.allFound()){
            System.out.println("\nYou need to find all clues first! Go search the room!");
            return;
        }
        System.out.println("\nYou found all clues! Your clues:"); 
        room.showClues();
        System.out.print("Enter the door code: ");
        String code=input.nextLine();
        if(code.equals("123")){
            System.out.println("\nCorrect code! You escaped!");
            escaped=true; 
            saveScore(); 
            finish();
        } else {
            System.out.println("\nWrong code! The code is hidden in the clues, go search again!");
            p.loseTry(); 
            System.out.println("Tries left: "+p.getTries());
        }
    }
    
    public void finish() {
        System.out.println("\n==================== FINAL RESULT ====================");
        System.out.println("Final Score: " + p.getScore());
        if(p.getScore()>=90) System.out.println("Rank: Perfect ESCAPER");
        else if(p.getScore()>=70) System.out.println("Rank: Good ESCAPER");
        else System.out.println("Rank: ESCAPER");
    }
    
    // save score to file
    public void saveScore() {
        try {
            FileWriter writer=new FileWriter("score.txt", true);
            writer.write("Player: " + p.getName() + " | Score: " + p.getScore() + " | Date: " + java.time.LocalDate.now() + "\n");
            writer.close();
           System.out.println("Score saved!");
        } catch(IOException e){
            System.out.println("Could not save score.");
        }
    }
    
    // read scores from file
    public void showHighScores() {
        System.out.println("\n--- All Scores ---");
        try {
            BufferedReader reader=new BufferedReader(new FileReader("score.txt"));
            String line;
            while((line=reader.readLine())!=null) System.out.println(line);
            reader.close();
        } catch(FileNotFoundException e){
            System.out.println("No scores yet, you are first!");
        } catch(IOException e){
            System.out.println("Error reading file");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter your name: ");
        String playerName=sc.nextLine();
        Player p = new Player(playerName);
        Game game=new Game(p);
        game.start();
  }
