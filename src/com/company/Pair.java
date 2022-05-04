package com.company;

import java.net.Socket;

public class Pair {
    public Socket clientSocket;
    public boolean hasQuit;

    public Pair(Socket clientSocket, boolean hasQuit) {
        this.clientSocket = clientSocket;
        this.hasQuit = hasQuit;
    }
}
