package server;

import server.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server {

    private Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        try {
            AuthService.connect();
            server = new ServerSocket(6666);
            System.out.println("Server is working...");
            while (true) {
                socket = server.accept();
                System.out.println("Client was added");
                subscribe(new server.ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
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


    public void subscribe(server.ClientHandler clientHandler) {
        clients.add((clientHandler));

    }


    public void unsubscribe(server.ClientHandler clientHandler) {
        clients.remove((clientHandler));
    }
}

