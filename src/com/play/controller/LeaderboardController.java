package com.play.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.play.model.User;
import com.play.util.FileHandler;
import com.play.util.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Controller per la gestione della classifica degli utenti.
 * Estende BaseController per ereditare funzionalità comuni.
 */
public class LeaderboardController extends BaseController {
    @FXML private TableView<UserScore> leaderboardTable;
    @FXML private TableColumn<UserScore, String> positionColumn;
    @FXML private TableColumn<UserScore, String> usernameColumn;
    @FXML private TableColumn<UserScore, Integer> scoreColumn;
    @FXML private Label currentUserLabel;
    @FXML private Button backButton;

	private String currentUser;

    /**
     * Inizializza la vista della classifica.
     * Configura le colonne della tabella e carica i dati degli utenti.
     */
	public void initialize() {
        try {
            setupTableColumns();
            loadUserData();
        } catch (Exception e) {
            handleError("Errore di inizializzazione", "Impossibile caricare la classifica: " + e.getMessage());
        }
    }

    /**
     * Configura le colonne della tabella della classifica.
     */
    private void setupTableColumns() {
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Configura il cell factory per la colonna username per mostrare l'immagine del profilo
        usernameColumn.setCellFactory(column -> {
            TableCell<UserScore, String> cell = new TableCell<UserScore, String>() {
                private final HBox container = new HBox(10);
                private final ImageView profileImage = new ImageView();
                private final Label usernameLabel = new Label();

                {
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    profileImage.setFitHeight(20);
                    profileImage.setFitWidth(20);
                    profileImage.setPreserveRatio(true);
                    profileImage.setSmooth(true);
                    // Crea un cerchio come clip
                    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(10, 10, 10);
                    profileImage.setClip(clip);
                    container.getChildren().addAll(profileImage, usernameLabel);
                }

                @Override
                protected void updateItem(String username, boolean empty) {
                    super.updateItem(username, empty);
                    if (empty || username == null) {
                        setGraphic(null);
                    } else {
                        try {
                            String imagePath = getProfileImagePath(username);
                            // Carica l'immagine con dimensioni specificate
                            Image image = new Image(imagePath, 20, 20, true, true);
                            if (!image.isError()) {
                                profileImage.setImage(image);
                            }
                            usernameLabel.setText(username);
                            setGraphic(container);
                        } catch (Exception e) {
                            // Se c'è un errore nel caricamento dell'immagine, mostra solo il nome utente
                            usernameLabel.setText(username);
                            setGraphic(usernameLabel);
                        }
                    }
                }
            };
            return cell;
        });

        // Configura le dimensioni delle colonne
        leaderboardTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        configureColumnWidths();
    }

    /**
     * Configura le larghezze delle colonne della tabella.
     */
    private void configureColumnWidths() {
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

    /**
     * Carica i dati degli utenti e popola la classifica.
     */
    private void loadUserData() {
        UserSession session = UserSession.getInstance();
        currentUser = session.getUsername();

        Map<String, Integer> userTotal = fetchUserTotal();
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(userTotal.entrySet());
        sortedList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

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
    }

    /**
     * Recupera i punteggi totali degli utenti.
     * @return Mappa con username e punteggio totale
     */
    private Map<String, Integer> fetchUserTotal() {
    	Map<String, Integer> userTotal = new HashMap<>();
        try {
        	List<User> users = FileHandler.readUsers();
	    	for (User user : users) {
                int totalScore = calculateUserTotalScore(user);
                userTotal.put(user.getUsername(), totalScore);
            }
        } catch (IOException e) {
            handleError("Errore di lettura", "Impossibile leggere i dati degli utenti: " + e.getMessage());
        }
        return userTotal;
    }

    /**
     * Calcola il punteggio totale di un utente.
     * @param user L'utente di cui calcolare il punteggio
     * @return Il punteggio totale
     */
    private int calculateUserTotalScore(User user) {
	        	List<String> userProgress = FileHandler.loadUserProgress(user.getUsername());
	            int[] highTotals1 = new int[9];
	            int[] highTotals2 = new int[9];
        
		        for (String progress : userProgress) {
            processProgressEntry(progress, highTotals1, highTotals2);
        }
        
        return calculateTotalScore(highTotals1, highTotals2);
    }

    /**
     * Processa una singola entry di progresso.
     */
    private void processProgressEntry(String progress, int[] highTotals1, int[] highTotals2) {
        String[] parts = progress.split(";");
        if (parts.length < 7) return;

        String exerciseId = parts[0];
        String level = parts[1];
        int levelIndex = Integer.parseInt(parts[2]) - 1;
        int score = Integer.parseInt(parts[6]);

        if (exerciseId.equalsIgnoreCase("exercise1")) {
            updateHighScore(highTotals1, level, levelIndex, score);
        } else if (exerciseId.equalsIgnoreCase("exercise2")) {
            updateHighScore(highTotals2, level, levelIndex, score);
        }
    }

    /**
     * Aggiorna il punteggio più alto per un livello specifico.
     */
    private void updateHighScore(int[] highTotals, String level, int levelIndex, int score) {
        int baseIndex = getBaseIndex(level);
        if (baseIndex >= 0 && levelIndex >= 0 && levelIndex < 3) {
            int index = baseIndex + levelIndex;
            if (highTotals[index] < score) {
                highTotals[index] = score;
            }
        }
    }

    /**
     * Ottiene l'indice base per il livello specificato.
     */
    private int getBaseIndex(String level) {
        switch (level.toLowerCase()) {
            case "principiante": return 0;
            case "intermedio": return 3;
            case "esperto": return 6;
            default: return -1;
        }
    }

    /**
     * Calcola il punteggio totale dai punteggi dei due esercizi.
     */
    private int calculateTotalScore(int[] highTotals1, int[] highTotals2) {
        int totalScore = 0;
        for (int i = 0; i < highTotals1.length; i++) {
            totalScore += (highTotals1[i] + highTotals2[i]);
        }
        return totalScore;
    }

    /**
     * Ottiene il percorso dell'immagine del profilo per un utente.
     * @param username L'username dell'utente
     * @return Il percorso dell'immagine del profilo
     */
    private String getProfileImagePath(String username) {
        try {
            List<User> users = FileHandler.readUsers();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    String customPath = user.getProfilePicturePath();
                    if (customPath != null && !customPath.isEmpty()) {
                        // Prova prima nella cartella resources
                        File imageFile = new File("resources/com/play/images/profiles/" + customPath);
                        if (imageFile.exists() && imageFile.canRead()) {
                            return imageFile.toURI().toString();
                        }
                        // Se non la trova in resources, prova nella cartella bin
                        imageFile = new File("bin/com/play/images/profiles/" + customPath);
                        if (imageFile.exists() && imageFile.canRead()) {
                            return imageFile.toURI().toString();
                        }
                    }
                }
            }
        } catch (IOException e) {
            handleError("Errore di lettura", "Impossibile leggere i dati degli utenti: " + e.getMessage());
        }
        return getClass().getResource("/com/play/images/user.png").toExternalForm();
    }

    /**
     * Imposta l'etichetta dell'utente corrente.
     */
    private void setCurrentUserLabel(int rank, int totalScore) {
        HBox userBox = new HBox(10);
        userBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.setStyle("-fx-font-size: 20px;");

        ImageView profileImage = new ImageView();
        try {
            String imagePath = getProfileImagePath(currentUser);
            
            // Carica l'immagine con dimensioni specificate
            Image image = new Image(imagePath, 20, 20, true, true);
            if (image.isError()) {
                System.err.println("Errore nel caricamento dell'immagine: " + imagePath);
                throw new Exception("Errore nel caricamento dell'immagine");
            }
            
            profileImage.setImage(image);
            profileImage.setFitHeight(20);
            profileImage.setFitWidth(20);
            profileImage.setPreserveRatio(true);
            profileImage.setSmooth(true);

            // Crea un cerchio come clip
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(10, 10, 10);
            profileImage.setClip(clip);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/com/play/images/user.png"), 20, 20, true, true);
                profileImage.setImage(defaultImage);
                // Applica lo stesso clip anche all'immagine di default
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(10, 10, 10);
                profileImage.setClip(clip);
            } catch (Exception ex) {
                System.err.println("Errore nel caricamento dell'immagine di default: " + ex.getMessage());
            }
        }

        Label usernameLabel = new Label(currentUser);
        usernameLabel.setStyle("-fx-font-size: 20px;");

        Label scoreLabel = new Label(String.valueOf(totalScore));
        scoreLabel.setStyle("-fx-font-size: 20px;");

        userBox.getChildren().addAll(
            rankLabel,
            new Label("|"),
            profileImage,
            usernameLabel,
            new Label("|"),
            scoreLabel
        );

        currentUserLabel.setGraphic(userBox);
    }

    /**
     * Gestisce il click sul pulsante di ritorno alla homepage.
     */
    @FXML
    private void handleBackToHomepage() {
        navigateToPage("/com/play/view/homepage.fxml", backButton, "Homepage");
    }

    /**
     * Classe interna per rappresentare un record di classifica.
     */
    public static class UserScore {
        private final String position;
        private final String username;
        private final int score;

        public UserScore(String position, String username, int score) {
            this.position = position;
            this.username = username;
            this.score = score;
        }

        public String getPosition() { return position; }
        public String getUsername() { return username; }
        public int getScore() { return score; }
    }
}
