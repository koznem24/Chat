import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    List<PrintWriter> clientOutputStreams;


    public static void main(String[] args) {
        new Server().go();
    } // Just to run the Server

    public void go(){
        clientOutputStreams = new ArrayList<>();
        try{
            ServerSocket serverSock = new ServerSocket("PORT NUMBER IS HERE");
            while(true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket){
            try{
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try{
                while((message = reader.readLine()) != null){
                    tellEveryone(message);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void tellEveryone(String message){
            clientOutputStreams.forEach((writer)->{
                try{
                    writer.println(message);
                    writer.flush();
                }catch(Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

}
