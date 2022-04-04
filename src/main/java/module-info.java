module ru.gb.onlinechat {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.onlinechat to javafx.fxml;
    exports ru.gb.onlinechat;
}