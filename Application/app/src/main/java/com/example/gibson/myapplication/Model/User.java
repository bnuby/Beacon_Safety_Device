package com.example.gibson.myapplication.Model;

public class User {
  public String name;
  public String username;
  public String password;
  public String email;
  public String callerID;

  public User(String name, String username, String password, String email, String callerID) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.email = email;
    this.callerID = callerID;
  }
}
