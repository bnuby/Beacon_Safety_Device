package com.example.gibson.myapplication.Model;

public class Beacon {
  public String name;
  public String MAC;
  public double alert_distance;

  public Beacon(String name, String MAC, double alert_distance) {
    this.name = name;
    this.MAC = MAC;
    this.alert_distance = alert_distance;
  }
}
