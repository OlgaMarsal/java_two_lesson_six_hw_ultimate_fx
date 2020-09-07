package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField tfMessage;

    @FXML
    TextArea textArea;

    @FXML
    HBox topPanel;

    @FXML
    HBox botPanel;

    @FXML
    TextField tfLogin;

    @FXML
    PasswordField pfPassword;


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP = "Localhost";
    final int PORT = 6666;

    boolean isAuthorized;

    //для удобства, сетторы хорошо работают с полями
    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            topPanel.setVisible(true);
            topPanel.setManaged(true);
            botPanel.setVisible(false);
            botPanel.setManaged(false);
        } else {
            topPanel.setVisible(false);
            topPanel.setManaged(false);
            botPanel.setVisible(true);
            botPanel.setManaged(true);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            // В этом потоке слушаем сервер
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authOK")) {
                                setAuthorized(true);
                                break; //выхожим из цикла авторизации
                            } else {
                                textArea.appendText(str + "\n");
                            }
                        }
                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/server closed")) break; //для закрытия сокетов на стороне клиента
                            textArea.appendText(str + "\n");
                        }
                    } catch (IOException e) {
                        System.out.println("Mori help");
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("Mori help 2");
                        }
                        Controller.this.setAuthorized(false);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Mori help 3");
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(tfMessage.getText());
            tfMessage.clear();
            tfMessage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toAuth() {

        try {
            out.writeUTF("/auth " + tfLogin.getText() + " " + pfPassword.getText());
            tfLogin.clear();
            pfPassword.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
