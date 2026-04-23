package models;

import exceptions.InvalidUsernameException;
import exceptions.InvalidPasswordException;

import java.util.Scanner;

abstract public class User {
     protected String username;
     protected String password;
     public User(){}

    public User(String username, String password) {
         this.setUsername(username);
          this.setPassword(password);
     }

    public void setUsername(String username){
        this.username = username;
        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException("Username cannot be empty");
        }
        for(char c : username.toCharArray()){
            if(!Character.isLetter(c) && !Character.isDigit(c))
                throw new InvalidUsernameException("Username can not have special characters or spaces");
        }
    }

    public String getUsername() {
        return username;
    }

    //password setter, when called in main, password requirements
    //(1 letter 1 digit 1 special and at least 5 chars)
    //must appear to the user BEFORE writing the password

    public void setPassword(String password) throws InvalidPasswordException {
        validatePassword(password);
        this.password = password;
    }
    protected void validatePassword (String p){
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasLetter = false;

        if (p == null || p.isBlank()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }
        if (p.length() < 5){
            throw new InvalidPasswordException("Password must contain at least 5 characters");
        }

        // turn password to a character array and loops
        // through it checking the type of every character

        for(char c: p.toCharArray()){
            if(Character.isLetter(c)){
                hasLetter = true;
            }
            else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            else{
                hasSpecial = true;
            }
        }
        if(!hasDigit){
            throw  new InvalidPasswordException("Password must have at least one digit: 0 - 9");
        }
        if(!hasLetter){
            throw new InvalidPasswordException("Password must include at least one letter: a - z or A - Z");
        }

        // if neither a letter nor a digit, then it is a special character

        if(!hasSpecial){
            throw new InvalidPasswordException("Password must have at least one special character");
        }
    }
    public abstract boolean login(String username, String password);
    public abstract void showDashboard(Scanner sc);
    @Override
    public String toString() {
        return "Username: " + username;
    }
 }
