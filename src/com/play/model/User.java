package com.play.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePicturePath;
    private boolean autoSendEnabled;
    
    private Map<String, Integer> exerciseProgress;

    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicturePath = null;
        this.autoSendEnabled = false;
        this.exerciseProgress = new HashMap<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getExerciseProgress(String exerciseId) {
        return exerciseProgress.getOrDefault(exerciseId, 0);
    }
    public void setExerciseProgress(String exerciseId, int progress) {
        exerciseProgress.put(exerciseId, progress);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }
    
    public boolean isAutoSendEnabled() { return autoSendEnabled; }
    public void setAutoSendEnabled(boolean autoSendEnabled) { this.autoSendEnabled = autoSendEnabled; }
    
    @Override
    public String toString() {
        return username + ":" + password + ":" + firstName + ":" + lastName + ":" + email + ":" + phoneNumber;
    }
}
