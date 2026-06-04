module com.example.project30 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    opens database to java.sql;
    //icone

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.controlsfx.controls;


    opens com.example.project30 to javafx.fxml;
    exports com.example.project30;
}