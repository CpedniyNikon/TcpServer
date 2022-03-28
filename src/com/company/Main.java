package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
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
        String msgFromClient;
        msgFromClient = bufferedReader.readLine();
        if (msgFromClient.equals("new client")) {
            clientSockets.add(new Pair(clientSocket, false));
            System.out.println("added new users in general chat");
            bufferedWriter.write("starting getting messages " + (clientSockets.size() - 1));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } else if (msgFromClient.startsWith("CommandFromUser")) {
            String finalMsgFromClient = msgFromClient.substring(16);
            if (finalMsgFromClient.startsWith("skip")) {
                int index = Integer.parseInt(finalMsgFromClient.substring(5));
                OutputStreamWriter finalOutputStreamWrite = new OutputStreamWriter(clientSockets.get(index).clientSocket.getOutputStream());
                BufferedWriter finalBufferedWriter = new BufferedWriter(finalOutputStreamWrite);
                finalBufferedWriter.write("");
                finalBufferedWriter.newLine();
                finalBufferedWriter.flush();
                System.out.println(index);
                clientSockets.get(index).hasQuit = true;
            }
            if (finalMsgFromClient.startsWith("start")) {
                int index = Integer.parseInt(finalMsgFromClient.substring(6));
                clientSockets.get(index).hasQuit = false;
            }
            System.out.println(msgFromClient);
            CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
        } else if (msgFromClient.startsWith("sendToEveryone")) {
            bufferedWriter.write("you wrote message");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String finalMsgFromClient = msgFromClient;
            finalMsgFromClient = finalMsgFromClient.substring(15);
            for (int i = 0; i < clientSockets.size(); i++) {
                if (!clientSockets.get(i).hasQuit) {
                    OutputStreamWriter finalOutputStreamWrite = new OutputStreamWriter(clientSockets.get(i).clientSocket.getOutputStream());
                    BufferedWriter finalBufferedWriter = new BufferedWriter(finalOutputStreamWrite);
                    finalBufferedWriter.write(finalMsgFromClient);
                    finalBufferedWriter.newLine();
                    finalBufferedWriter.flush();
                }
            }
            CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
        } else if (msgFromClient.equals("returnUserList")) {
            ArrayList<String> userList = new ArrayList<>();
            File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
            FileReader fileReader = new FileReader(logs);
            FileWriter fileWriter = new FileWriter(logs, true);
            BufferedReader reader = new BufferedReader(fileReader);
            // a - hui na
            System.out.println(msgFromClient);
            String line = reader.readLine();
            while (line != null) {
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == ' ') break;
                    name.append(line.charAt(i));
                }
                userList.add(name.toString());
                line = reader.readLine();
            }

            bufferedWriter.write(userList.size());

            for (int i = 0; i < userList.size(); i++) {
                bufferedWriter.write(userList.get(i));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
            fileReader.close();
            fileWriter.close();
            reader.close();

        } else if (msgFromClient.charAt(0) == 'l') {

            File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
            FileReader fileReader = new FileReader(logs);
            FileWriter fileWriter = new FileWriter(logs, true);
            BufferedReader reader = new BufferedReader(fileReader);

            String finalMsgFromClient = msgFromClient;
            finalMsgFromClient = finalMsgFromClient.substring(2);
            String line = reader.readLine();
            boolean flag = false;
            while (line != null) {
                if (line.equals(finalMsgFromClient)) {
                    flag = true;
                    System.out.println("You logged in");
                    bufferedWriter.write("You logged in\n");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                line = reader.readLine();
            }
            if (flag == false) {
                System.out.println("Error while logging");
                bufferedWriter.write("Error while logging\n");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
            fileReader.close();
            fileWriter.close();
            reader.close();

        } else if (msgFromClient.charAt(0) == 'r') {

            File logs = new File("C:\\Users\\carry\\Desktop\\Logs.txt");
            FileReader fileReader = new FileReader(logs);
            FileWriter fileWriter = new FileWriter(logs, true);
            BufferedReader reader = new BufferedReader(fileReader);

            String finalMsgFromClient = msgFromClient;
            finalMsgFromClient = finalMsgFromClient.substring(2);
            String line = reader.readLine();
            boolean flag = true;
            while (line != null) {
                if (line.equals(finalMsgFromClient)) {
                    flag = false;
                }
                line = reader.readLine();
            }
            if (flag) {
                fileWriter.write(finalMsgFromClient + '\n');
                System.out.println("You successfully registered");
                bufferedWriter.write("You successfully registered\n");
            } else {
                System.out.println("This user has already registered");
                bufferedWriter.write("This user has already registered\n");
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
            CloseSocket(clientSocket, inputStreamReader, bufferedReader, outputStreamWriter, bufferedWriter);
            fileReader.close();
            fileWriter.close();
            reader.close();
        }
    }

    private static class Pair {
        public Socket clientSocket;
        public boolean hasQuit;

        public Pair(Socket clientSocket, boolean hasQuit) {
            this.clientSocket = clientSocket;
            this.hasQuit = hasQuit;
        }
    }

    public static ArrayList<Pair> clientSockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;

        ServerSocket server = null;
        int index = 0;

        Socket clientSocket = null;
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
                index++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
