module com.example.lab5_team_log121 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lab5_team_log121 to javafx.fxml;
    exports com.example.lab5_team_log121;
}