import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JTextArea outputArea;
    private SwingWorker<String, Void> worker;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientGUI() {
        setTitle("Client Login Page");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create GUI components
        JPanel loginPanel = new JPanel(new GridLayout(4, 2));
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(signupButton);

        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Actions that run when the Login Button is clicked
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        if (loginToServer(username, password)) {
                            return "Login successful for user: " + username;
                        } else {
                            return "Login failed - invalid username or password.";
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            outputArea.append(get() + "\n");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

                worker.execute();
            }
        });

        // Actions that run when the Sign Up Button is clicked
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        if (signupToServer(username, password)) {
                            return "Signup successful for user: " + username;
                        } else {
                            return "Signup failed. Username already exists";
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            outputArea.append(get() + "\n");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

                worker.execute();
            }
        });
    }

    // Method for connecting to the server and handling login
private boolean loginToServer(String username, String password) {
    try {
        socket = new Socket("localhost", 3500);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("LOGIN");
        dos.writeUTF(username);
        dos.writeUTF(password);
        dos.flush();

        String response = dis.readUTF();

        if (response.equals("Login successful")) {
            // After a successful login, open the PostLoginGUI
            openPostLoginGUI();
            return true; // Indicate a successful login
        } else {
            return false; // Indicate a failed login
        }
    } catch (IOException e) {
        outputArea.append("Connection error: " + e.getMessage() + "\n");
        return false;
    } finally {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    // Method for connecting to the server and handling signup
    private boolean signupToServer(String username, String password) {
        try {
            socket = new Socket("127.0.0.1", 3500);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("SIGNUP");
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();

            String response = dis.readUTF();

            return response.equals("Signup successful");
        } catch (IOException e) {
            outputArea.append("Connection error: " + e.getMessage() + "\n");
            return false;
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to open the PostLoginGUI
    private void openPostLoginGUI() {
        // Close the current GUI window
        dispose();

        // Create and display the PostLoginGUI window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PostLoginGUI postLoginGUI = new PostLoginGUI();
                postLoginGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientGUI clientGUI = new ClientGUI();
                clientGUI.setVisible(true);
            }
        });
    }
}
