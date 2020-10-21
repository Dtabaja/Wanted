package com.example.myapplication;


public class Player implements Comparable{
    private String name;
    private int score;
    private double longitude, latitude;

    // Constructor.
    public Player(String name, int score, double longitude, double latitude){
        this.name = name;
        this.score = score;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and setters.
    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }


    @Override
    public int compareTo(Object o) {
        Player player = (Player) o;
        return Integer.compare(player.getScore(), this.getScore());
    }
}
