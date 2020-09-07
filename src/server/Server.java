package server;

import server.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server {

    // Vector это как ArrayLisr, но только для многопоточности, так как он синхронизированный
    private Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null; // иницилизация локальной перемннной, так что пишу нолик
        Socket socket = null;
        try {
            AuthService.connect();
            // создали сервер и выюрали ему порт
            server = new ServerSocket(6666);
            System.out.println("Server is working...");
            while (true) {
                socket = server.accept(); // точка подлючения со строны сервера или розетка (информация - кто подключился с какого IP, port)
                System.out.println("Client was added");
                subscribe(new server.ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // закрываем розетку
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();  // закрываем сервер
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void broadCastMsg(String msg) {
        for (server.ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    //подписываем клиента, который вышел из сети
    public void subscribe(server.ClientHandler clientHandler) {
        clients.add((clientHandler));

    }

    //отписываем клиента, который вышел из сети
    public void unsubscribe(server.ClientHandler clientHandler) {
        clients.remove((clientHandler));
    }
}

