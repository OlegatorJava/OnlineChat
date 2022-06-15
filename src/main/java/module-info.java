module ru.gb.onlinechat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;


    opens ru.gb.onlinechat to javafx.fxml;
    exports ru.gb.onlinechat;
    /*exports ru.gb.onlinechat;
    opens ru.gb.onlinechat to javafx.fxml;*/
}