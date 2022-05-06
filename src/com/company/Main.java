package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    private static void newClient(Socket clientSocket, BufferedWriter bufferedWriter) throws IOException {
        clientSockets.add(new Pair(clientSocket, false));
        System.out.println("added new users in general chat");
        bufferedWriter.write("starting getting messages " + (clientSockets.size() - 1));
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private static void login(Socket clientSocket,
                              InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                              OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter,
                              String msgFromClient) throws IOException {
        File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
        FileReader fileReader = new FileReader(logs);
        FileWriter fileWriter = new FileWriter(logs, true);
        BufferedReader reader = new BufferedReader(fileReader);

        String finalMsgFromClient = msgFromClient;
        finalMsgFromClient = finalMsgFromClient.substring(2);
        String line = reader.readLine();
        boolean flag = false;
        while (line != null) {
            String[] words = line.split(" ");
            if (finalMsgFromClient.equals(words[0] + " " + words[1])) {
                flag = true;
                System.out.println("You logged in");
                bufferedWriter.write("You logged in " + words[2] + "\n");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            line = reader.readLine();
        }
        if (!flag) {
            System.out.println("Error while logging");
            bufferedWriter.write("Error while logging");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }

        CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
        fileReader.close();
        fileWriter.close();
        reader.close();
    }

    private static void isThisUserRegistered(Socket clientSocket,
                                             InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                                             OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter,
                                             String msgFromClient) throws IOException {
        File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
        FileReader fileReader = new FileReader(logs);
        BufferedReader reader = new BufferedReader(fileReader);
        String finalMsgFromClient = msgFromClient;
        finalMsgFromClient = finalMsgFromClient.substring(2);
        String line = reader.readLine();
        boolean flag = true;
        while (line != null) {
            String[] words = line.split(" ");
            if (finalMsgFromClient.equals(words[0] + " " + words[1])) {
                flag = false;
            }
            line = reader.readLine();
        }
        if (flag) {
            System.out.println("You Can Register");
            bufferedWriter.write("You Can Register\n");
        } else {
            System.out.println("This user has already registered");
            bufferedWriter.write("This user has already registered\n");
        }
        bufferedWriter.newLine();
        bufferedWriter.flush();
        CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
        fileReader.close();
        reader.close();
    }

    private static void stop(String finalMsgFromClient) throws IOException {
        int index = Integer.parseInt(finalMsgFromClient.substring(5));
        OutputStreamWriter finalOutputStreamWrite = new OutputStreamWriter(clientSockets.get(index).clientSocket.getOutputStream());
        BufferedWriter finalBufferedWriter = new BufferedWriter(finalOutputStreamWrite);
        finalBufferedWriter.write("");
        finalBufferedWriter.newLine();
        finalBufferedWriter.flush();
        System.out.println(index);
        clientSockets.get(index).hasQuit = true;
    }

    private static void setNickName(String msgFromClient) throws IOException {
        File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
        FileWriter fileWriter = new FileWriter(logs, true);
        fileWriter.write(msgFromClient.substring(15) + '\n');
        fileWriter.close();
    }

    private static void start(String finalMsgFromClient) {
        int index = Integer.parseInt(finalMsgFromClient.substring(6));
        clientSockets.get(index).hasQuit = false;
    }

    private static void commandHandler(Socket clientSocket,
                                       InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                                       OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter,
                                       String msgFromClient) throws IOException {
        String finalMsgFromClient = msgFromClient.substring(16);
        if (finalMsgFromClient.startsWith("stop")) {
            stop(finalMsgFromClient);
        }
        if (finalMsgFromClient.startsWith("start")) {
            start(finalMsgFromClient);
        }
        CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
    }

    private static void sendToEveryone(Socket clientSocket,
                                       InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                                       OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter,
                                       String msgFromClient) throws IOException {
        bufferedWriter.write("you wrote message");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        String[] words = msgFromClient.split(" ");
        for (int i = 0; i < clientSockets.size(); i++) {
            if (i == Integer.parseInt(words[1])) continue;
            if (!clientSockets.get(i).hasQuit) {
                OutputStreamWriter finalOutputStreamWrite = new OutputStreamWriter(clientSockets.get(i).clientSocket.getOutputStream());
                BufferedWriter finalBufferedWriter = new BufferedWriter(finalOutputStreamWrite);
                finalBufferedWriter.write(words[2] + words[3]);
                finalBufferedWriter.newLine();
                finalBufferedWriter.flush();
            }
        }
        System.out.println(words[0] + " " + words[1] + " " + words[2] + " " + words[3]);
        CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
    }

    private static void CloseSocket(Socket clientSocket,
                                    InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                                    OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter) throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (outputStreamWriter != null) {

            outputStreamWriter.close();
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
    }


    public static void ClientSession(Socket clientSocket,
                                     InputStreamReader inputStreamReader, BufferedReader bufferedReader,
                                     OutputStreamWriter outputStreamWriter, BufferedWriter bufferedWriter
    ) throws IOException, InterruptedException {
        String msgFromClient = bufferedReader.readLine();
        if (msgFromClient.equals("new client")) {
            newClient(clientSocket, bufferedWriter);
        } else if (msgFromClient.startsWith("CommandFromUser")) {
            commandHandler(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter, msgFromClient);
        } else if (msgFromClient.startsWith("sendToEveryone")) {
            sendToEveryone(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter, msgFromClient);
        } else if (msgFromClient.startsWith("changeNickName")) {
            setNickName(msgFromClient);
        } else if (msgFromClient.startsWith("l")) {
            login(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter, msgFromClient);
        } else if (msgFromClient.startsWith("r")) {
            isThisUserRegistered(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter, msgFromClient);
        }
    }


    public static ArrayList<Pair> clientSockets = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;

        OutputStreamWriter outputStreamWriter;
        BufferedWriter bufferedWriter;

        ServerSocket server;
        Socket clientSocket;
        server = new ServerSocket(4000);
        do {
            try {
                clientSocket = server.accept();
                System.out.println("new socket connected");

                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                Socket finalClientSocket = clientSocket;
                InputStreamReader finalInputStreamReader = inputStreamReader;
                BufferedReader finalBufferedReader = bufferedReader;
                OutputStreamWriter finalOutputStreamWriter = outputStreamWriter;
                BufferedWriter finalBufferedWriter = bufferedWriter;

                Thread sessionThread = new Thread(() -> {
                    try {
                        ClientSession(finalClientSocket,
                                finalInputStreamReader, finalBufferedReader,
                                finalOutputStreamWriter, finalBufferedWriter
                        );
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                sessionThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}