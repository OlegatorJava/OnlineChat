module ru.gb.onlinechat {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.onlinechat to javafx.fxml;
    exports ru.gb.onlinechat;
    /*exports ru.gb.onlinechat;
    opens ru.gb.onlinechat to javafx.fxml;*/
}