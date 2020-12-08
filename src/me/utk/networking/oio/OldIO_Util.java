package me.utk.networking.oio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

abstract class OldIO_Util {
    private OldIO_Util() {
    }

    static void sendMessages(Socket socket, String... messages) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            for (String message : messages)
                out.writeUTF(message);
        } catch (IOException ignored) {
        }
    }

    // true iff success
    static boolean closeSocket(Socket socket) {
        try {
            socket.close();
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    static void closeSocketUntilSuccess(Socket socket) {
        while (!closeSocket(socket)) ;
    }

    // timeout timer count
    static int readMessages(Socket socket, OldIO_MessageBuilder builder, int timeoutCounter) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (true) {
                String line = in.readUTF();
                OldIO_MessageUtil type = builder.addLine(line);
                if (type == OldIO_MessageUtil.CONNECTION_CLOSED) {
                    timeoutCounter = Integer.MAX_VALUE;
                    break;
                }
                timeoutCounter = 0;
            }
        } catch (IOException e) {
            timeoutCounter++;
        }
        return timeoutCounter;
    }

    static String readLine(Socket socket) {
        try {
            return new DataInputStream(socket.getInputStream()).readUTF();
        } catch (IOException ignored) {
        }
        return null;
    }
}
