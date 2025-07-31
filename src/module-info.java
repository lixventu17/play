module play {
	requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;
    requires activation;

    opens com.play to javafx.fxml;
    opens com.play.controller to javafx.fxml;
    opens com.play.model to javafx.base;
    opens com.play.util to javafx.base;

    exports com.play;
    exports com.play.controller;
    exports com.play.model;
    exports com.play.util;
}
