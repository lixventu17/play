package com.play.controller;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class LeaderboardController {
    @FXML private TableView<UserScore> leaderboardTable;
    @FXML private TableColumn<UserScore, String> positionColumn;
    @FXML private TableColumn<UserScore, String> usernameColumn;
    @FXML private TableColumn<UserScore, Integer> scoreColumn;
    @FXML private Label currentUserLabel;
    @FXML private Button backButton;

	private String currentUser;

	public void initialize() {
        // Configura le colonne
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

    	UserSession session = UserSession.getInstance();
        currentUser = session.getUsername();
        
        // Recupera i dati totali per ogni utente
        Map<String, Integer> userTotal = fetchUserTotal();
        // Ordina map in ordine decrescente rispetto al punteggio
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(userTotal.entrySet());
        sortedList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        currentUserLabel.setText("Per entrare in classifica devi aver svolto almeno un esercizio!");
        currentUserLabel.setStyle("-fx-text-fill: red; -fx-font-size: 20px;");
        
        // Crea la lista degli oggetti UserScore
        ObservableList<UserScore> data = FXCollections.observableArrayList();
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (entry.getKey().equalsIgnoreCase(currentUser)) {
                setCurrentUserLabel(rank, entry.getValue());
            }
        	
            data.add(new UserScore(String.valueOf(rank), entry.getKey(), entry.getValue()));
            rank++;
        }
        leaderboardTable.setItems(data);

        leaderboardTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        leaderboardTable.getColumns().get(0).setMinWidth(200);
        leaderboardTable.getColumns().get(0).setPrefWidth(200);
        leaderboardTable.getColumns().get(0).setMaxWidth(200);
        leaderboardTable.getColumns().get(1).setMinWidth(500);
        leaderboardTable.getColumns().get(1).setPrefWidth(500);
        leaderboardTable.getColumns().get(1).setMaxWidth(500);
        leaderboardTable.getColumns().get(2).setMinWidth(300);
        leaderboardTable.getColumns().get(2).setPrefWidth(300);
        leaderboardTable.getColumns().get(2).setMaxWidth(300);
    }

    private Map<String, Integer> fetchUserTotal() {
    	Map<String, Integer> userTotal = new HashMap<>();
        try {
        	List<User> users = FileHandler.readUsers();
	    	for (User user : users) {
	        	List<String> userProgress = FileHandler.loadUserProgress(user.getUsername());
	            int[] highTotals1 = new int[9];
	            int[] highTotals2 = new int[9];
		        for (String progress : userProgress) {
		        	String[] parts = progress.split(";");
		        	if (parts[0].equalsIgnoreCase("exercise1")) {
		        		if (parts[1].equalsIgnoreCase("principiante")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals1[0] < Integer.parseInt(parts[6])) {
		        					highTotals1[0] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals1[1] < Integer.parseInt(parts[6])) {
		        					highTotals1[1] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals1[2] < Integer.parseInt(parts[6])) {
		        					highTotals1[2] = Integer.parseInt(parts[6]);
		        				}
		                	}
		        		}
		            	else if (parts[1].equalsIgnoreCase("intermedio")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals1[3] < Integer.parseInt(parts[6])) {
		        					highTotals1[3] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals1[4] < Integer.parseInt(parts[6])) {
		        					highTotals1[4] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals1[5] < Integer.parseInt(parts[6])) {
		        					highTotals1[5] = Integer.parseInt(parts[6]);
		        				}
		                	}
		            	}
		            	else if (parts[1].equalsIgnoreCase("esperto")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals1[6] < Integer.parseInt(parts[6])) {
		        					highTotals1[6] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals1[7] < Integer.parseInt(parts[6])) {
		        					highTotals1[7] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals1[8] < Integer.parseInt(parts[6])) {
		        					highTotals1[8] = Integer.parseInt(parts[6]);
		        				}
		                	}
		            	}
		        	}
		        	else if (parts[0].equalsIgnoreCase("exercise2")) {
		        		if (parts[1].equalsIgnoreCase("principiante")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals2[0] < Integer.parseInt(parts[6])) {
		        					highTotals2[0] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals2[1] < Integer.parseInt(parts[6])) {
		        					highTotals2[1] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals2[2] < Integer.parseInt(parts[6])) {
		        					highTotals2[2] = Integer.parseInt(parts[6]);
		        				}
		                	}
		        		}
		            	else if (parts[1].equalsIgnoreCase("intermedio")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals2[3] < Integer.parseInt(parts[6])) {
		        					highTotals2[3] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals2[4] < Integer.parseInt(parts[6])) {
		        					highTotals2[4] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals2[5] < Integer.parseInt(parts[6])) {
		        					highTotals2[5] = Integer.parseInt(parts[6]);
		        				}
		                	}
		            	}
		            	else if (parts[1].equalsIgnoreCase("esperto")) {
		        			if (Integer.parseInt(parts[2]) == 1) {
		        				if (highTotals2[6] < Integer.parseInt(parts[6])) {
		        					highTotals2[6] = Integer.parseInt(parts[6]);
		        				}
		        			}
		                	else if (Integer.parseInt(parts[2]) == 2) {
		        				if (highTotals2[7] < Integer.parseInt(parts[6])) {
		        					highTotals2[7] = Integer.parseInt(parts[6]);
		        				}
		                	}
		                	else if (Integer.parseInt(parts[2]) == 3) {
		        				if (highTotals2[8] < Integer.parseInt(parts[6])) {
		        					highTotals2[8] = Integer.parseInt(parts[6]);
		        				}
		                	}
		            	}
		        	}
		        }
	            int totalScore = 0;
	            for (int i = 0; i < highTotals1.length; i++) {
	            	totalScore += (highTotals1[i] + highTotals2[i]);
	            }
	            userTotal.put(user.getUsername(), totalScore);
	    	}
        } catch(IOException e) {
            e.printStackTrace();
        }
        return userTotal;
    }

    private void setCurrentUserLabel(int rank, int totalScore) {
        currentUserLabel.setText(rank + "     |     " + currentUser + "     |     " + totalScore);
        currentUserLabel.setStyle("-fx-font-size: 20px;");
    }
    
    @FXML
    private void handleBackToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/play/view/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/play/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Homepage");
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    // Classe helper per rappresentare un record di classifica
    public static class UserScore {
        private String position;
        private String username;
        private int score;
        
        public UserScore(String position, String username, int score) {
            this.position = position;
            this.username = username;
            this.score = score;
        }
        
        public String getPosition() {
            return position;
        }
        
        public String getUsername() {
            return username;
        }
        
        public int getScore() {
            return score;
        }
    }
}
