import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread{

   ServerSocket hi;
   Socket client;
   DataInputStream br;
   DataOutputStream dos;

   // HashMap to store user information (username, password)
   private HashMap<String, String> userDatabase = new HashMap<>();

   public int clients_max = 10; // Maximum allowed clients
   public int current_connected_clients = 0; // counter for current connected clients

   public static void main(String argv[]) throws Exception {
     new Server();
   }
   public Server() throws Exception {
       try {
           hi = new ServerSocket(3500);
           System.out.println("Server Listening on port 3500....");

           // Load the user database from a file
           loadUserDatabase();
           this.run();
       } catch (IOException e) {
           // Handle exceptions related to server socket creation
           e.printStackTrace();
       }
  }

  @SuppressWarnings("unused")
  public void run() {
    while (true) {
      try {
        if (current_connected_clients < clients_max){
          System.out.println("Waiting for connections.");
          Socket client = hi.accept();
          System.out.println("Accepted a connection from: " + client.getRemoteSocketAddress());
          System.out.println("New Client connected");
          current_connected_clients++;
          new Connect(client).start();

        } else {
          Socket server_Busy = hi.accept();
          DataOutputStream dos_busy = new DataOutputStream(server_Busy.getOutputStream());
          dos_busy.writeBytes("Server is busy. Try again later.\n");
          server_Busy.close();
          System.out.println("Server is busy.");
        }

      } catch (IOException e) {
          e.printStackTrace();
      }
    }
  }
  @SuppressWarnings("unchecked")
  // method for loading the user credentials from the txt file as a hashmap
  private void loadUserDatabase() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userDatabaseCredentials.txt"))) {
            userDatabase = (HashMap<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //initialize a file if it doesn't exist on the server
            userDatabase = new HashMap<>();
        }
    }
    // method for saving the user credentials to the txt file as a hashmap
    private void saveUserDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userDatabaseCredentials.txt"))) {
            oos.writeObject(userDatabase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  class Connect extends Thread {
    private Socket client_Socket = null;

    public Connect(Socket clientSocket) {
      this.client_Socket = clientSocket;
    } 

    public void run() {
      try {
          DataInputStream dis = new DataInputStream(client_Socket.getInputStream());
          DataOutputStream dos = new DataOutputStream(client_Socket.getOutputStream());

          String operation = dis.readUTF(); // Read the client's operation

          if (operation.equals("LOGIN")) {
              // Handle login operation
              String username = dis.readUTF();
              String password = dis.readUTF();

              if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                  dos.writeUTF("Login successful");
              } else {
                  dos.writeUTF("Login failed");
              }
          } else if (operation.equals("SIGNUP")) {
              // Handle signup operation
              String username = dis.readUTF();
              String password = dis.readUTF();

              if (!userDatabase.containsKey(username)) {
                  userDatabase.put(username, password);
                  dos.writeUTF("Signup successful");
                  saveUserDatabase();
              } else {
                  dos.writeUTF("Signup failed (user already exists)");
              }
          } else {
              dos.writeUTF("Invalid operation");
          }

          dis.close();
          dos.close();
          client_Socket.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  
  }
}