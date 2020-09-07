package server;

import server.AuthService;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private server.Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(server.Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream()); // socket.getInputStream() - это чтение потока данных
            this.out = new DataOutputStream(socket.getOutputStream());

//            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream())); - можно
// использовать для потока данных, но вот в каких случаях?
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //цикл авторизации
                        while (true) {
                            String msg = in.readUTF(); // читаем сообщение от клиента
                            if (msg.startsWith("/auth")) { // /auth login pass
                                String[] tokens = msg.split(" "); //разбиваем данные
                                String nickname = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]); //и запрашиваем по логину и паролю
                                if (nickname != null) {
                                    nick = nickname;
                                    server.subscribe(ClientHandler.this); //подписываем на рассылку
                                    ClientHandler.this.sendMsg(nick + " in the Chat");
                                    break;
                                } else {
                                    ClientHandler.this.sendMsg("Check your Login/Password");
                                }
                            }
                        }
                        // цикл обзения с клиентом
                        while (true) {
                            String msg = in.readUTF(); // читаем
                            if (msg.equals("/end")) {
                                out.writeUTF("/server closed");
                                break;
                            }
                            server.broadCastMsg(msg);
                            System.out.println("Client: " + msg);
                            // out.writeUTF("echo " + message); // печатаем полученное сообщение
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(server.ClientHandler.this);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMsg (String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}