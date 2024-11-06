import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ClientGUI {
    public OutputStream outputStream;
    public InputStream inputStream;
    public String username;
    public String name;

    private Client client;
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel menuPanel;
    private JPanel sendMsg;
    private JPanel changePass;
    private JPanel adminPanel;
    private JPanel changeMail;
    private JPanel changeUsername;

    // Components for Login
    private JTextField usernameField;
    private JPasswordField passwordField;

    //change password
    private JPasswordField newpasswordField;

    //change email
    private JTextField newEmailField;

    //change username
    private JTextField newUsernameField;

    //admin
    TextArea adminShow;

    // Components for Register
    private JTextField regNameField, regSurnameField, regAgeField, regMailField, regUsernameField;
    private JPasswordField regPasswordField;

    public ClientGUI() throws IOException {
        Socket socket = new Socket("localhost", 8080);
        client = new Client(8080, "localhost");

        File folder_Users = new File("USERS");//kreirame folder users
        File usernames = new File(folder_Users + "\\Usernames.txt");

        if(folder_Users.mkdir()) {//kreirame folder
            System.out.print("");
        }else{
            System.out.print("");
        }
        if(usernames.createNewFile()) {//go dodavame usernames vo foldero
            System.out.print("");
        }else{
            System.out.print("");
        }

        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();

        // Setup the frame
        frame = new JFrame("Client Application");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        menuPanel = createMenuPanel();
        sendMsg = createMessagePanel();
        changePass = createChangePass();
        changeMail = createChangeMail();
        changeUsername = createChangeUsername();
        adminPanel = createAdminPanel();


        // Add panels to the CardLayout container
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registerPanel, "Register");
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(sendMsg, "Message");
        mainPanel.add(changePass, "ChangePass");
        mainPanel.add(changeMail, "ChangeMail");
        mainPanel.add(changeUsername, "ChangeUsername");
        mainPanel.add(adminPanel, "AdminPanel");

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);

        //DA POCNI VO CENTAR
        frame.setLocationRelativeTo(null);

        // Start with the login screen
        cardLayout.show(mainPanel, "Login");
    }

    // Panel for login
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3,2));
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameField.setFont(new Font("Serif", Font.PLAIN, 18));
        usernameField.setBackground(Color.gray);
        usernameField.setForeground(Color.white);

        passwordField.setFont(new Font("Serif", Font.PLAIN, 18));
        passwordField.setBackground(Color.gray);
        passwordField.setForeground(Color.white);


        usernameField.addActionListener(e -> {
            passwordField.requestFocus();
        });

        passwordField.addActionListener(e -> {
            try {
                handleLogin();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton loginButton = new JButton("LOGIN");
        JButton registerButton = new JButton("REGISTER");

        loginButton.setFont(new Font("Serif", Font.BOLD, 25));
        registerButton.setFont(new Font("Serif", Font.BOLD, 25));

        loginButton.setBackground(Color.lightGray);
        registerButton.setBackground(Color.ORANGE);
        registerButton.setBorder(BorderFactory.createLineBorder(Color.black,1));

        loginButton.addActionListener(e -> {
            try {
                handleLogin();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        registerButton.addActionListener(e -> showRegisterPanel());

        panel.add(new JLabel("Username:"));
        usernameField.setFont(new Font("Serif", Font.PLAIN, 34));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField.setFont(new Font("Serif", Font.PLAIN, 24));

        panel.add(passwordField);


        panel.add(loginButton);
        panel.add(registerButton);
        return panel;
    }

    // Panel for registration
    private JPanel createRegisterPanel() {

        JPanel panel = new JPanel(new GridLayout(7, 1));
        regNameField = new JTextField();
        regSurnameField = new JTextField();
        regAgeField = new JTextField();
        regMailField = new JTextField();
        regUsernameField = new JTextField();
        regPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.gray);
        JButton backButton = new JButton("Back to Login");
        backButton.setBackground(Color.gray);

        registerButton.addActionListener(e -> {
            try {
                handleRegister();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        backButton.addActionListener(e -> showMainPanel());

        panel.add(new JLabel("Name:"));
        panel.add(regNameField);
        regNameField.addActionListener(e -> {
            regSurnameField.requestFocus();
        });

        panel.add(new JLabel("Surname:"));
        panel.add(regSurnameField);
        regSurnameField.addActionListener(e -> {
            regAgeField.requestFocus();
        });
        panel.add(new JLabel("Age:"));
        panel.add(regAgeField);
        regAgeField.addActionListener(e -> {
            regMailField.requestFocus();
        });

        panel.add(new JLabel("Mail:"));
        panel.add(regMailField);
        regMailField.addActionListener(e -> {
            regUsernameField.requestFocus();
        });
        panel.add(new JLabel("Username:"));
        panel.add(regUsernameField);
        regUsernameField.addActionListener(e -> {
            regPasswordField.requestFocus();
        });
        panel.add(new JLabel("Password:"));
        panel.add(regPasswordField);
        regSurnameField.addActionListener(e -> {
            try {
                handleRegister();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        regNameField.setFont(new Font("Serif", Font.PLAIN, 24));
        regSurnameField.setFont(new Font("Serif", Font.PLAIN, 24));
        regAgeField.setFont(new Font("Serif", Font.PLAIN, 24));
        regMailField.setFont(new Font("Serif", Font.PLAIN, 24));
        regPasswordField.setFont(new Font("Serif", Font.PLAIN, 24));
        regUsernameField.setFont(new Font("Serif", Font.PLAIN, 24));
        panel.add(registerButton);
        panel.add(backButton);
        return panel;
    }

    private void showChangePaswwordPanel() {
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "ChangePass");
    }

    private JPanel createChangePass() throws IOException {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        panel.add(new JLabel("New password:"));
        newpasswordField = new JPasswordField();
        newpasswordField.setFont(new Font("Serif",Font.PLAIN, 25));

        panel.add(newpasswordField);

        JButton changePass = new JButton("Change Password");
        JButton goBack = new JButton("Go back");

        changePass.setFont(new Font("Serif", Font.BOLD, 18));
        goBack.setFont(new Font("Serif", Font.BOLD, 18));

        changePass.setBackground(Color.lightGray);
        goBack.setBackground(Color.ORANGE);

        changePass.addActionListener(e -> {
            try {
                // Retrieve the password when the button is clicked
                String newPass = new String(newpasswordField.getPassword());

                Client.User newUser = Client.getUserInfo(username);
                newUser.setPassword(newPass);
                newUser.writeUser();

                JOptionPane.showMessageDialog(frame, "Changed Password Successfully!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        goBack.addActionListener(e -> showMenuPanel());

        panel.add(changePass);
        panel.add(goBack);
        return panel;

    }

    private JPanel createChangeMail() throws IOException {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("New mail:"));
        newEmailField = new JTextField();
        newEmailField.setFont(new Font("Serif",Font.PLAIN, 25));
        panel.add(newEmailField);

        JButton changeEmail = new JButton("Change Email");

        JButton goBack = new JButton("Go back");

        changeEmail.setFont(new Font("Serif", Font.BOLD, 18));
        goBack.setFont(new Font("Serif", Font.BOLD, 18));

        changeEmail.setBackground(Color.lightGray);
        goBack.setBackground(Color.ORANGE);

        changeEmail.addActionListener(e -> {
            try {
                // Retrieve the password when the button is clicked
                String newMail = newEmailField.getText();

                Client.User newUser = Client.getUserInfo(username);
                newUser.setEmail(newMail);
                newUser.writeUser();

                JOptionPane.showMessageDialog(frame, "Changed Email Successfully!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        goBack.addActionListener(e -> showMenuPanel());

        panel.add(changeEmail);
        panel.add(goBack);
        return panel;

    }

    private JPanel createChangeUsername() throws IOException {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("New Username:"));
        newUsernameField = new JTextField();
        newUsernameField.setFont(new Font("Serif",Font.PLAIN, 25));
        panel.add(newUsernameField);

        JButton change_username = new JButton("Change Username");
        JButton goBack = new JButton("Go back");

        change_username.setFont(new Font("Serif", Font.BOLD, 18));
        goBack.setFont(new Font("Serif", Font.BOLD, 18));

        change_username.setBackground(Color.lightGray);
        goBack.setBackground(Color.ORANGE);

        change_username.addActionListener(e -> {
            try {
                // Retrieve the password when the button is clicked
                String newUsername = newUsernameField.getText();

                Client.User newUser = Client.getUserInfo(username);
                String oldUsername = newUser.getNickname();
                File oldUserinfo = new File("USERS\\" + oldUsername + ".txt");
                if(oldUserinfo.delete()) {
                    System.out.print("");
                }else{
                    System.out.print("");
                }

                newUser.setNickname(newUsername);
                newUser.writeUser();
                Client.deleteUser(username,newUsername);

                JOptionPane.showMessageDialog(frame, "Changed Username Successfully!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        goBack.addActionListener(e -> showLoginPanel());

        panel.add(change_username);
        panel.add(goBack);
        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create a JTextPane for messages
        JTextPane messagePane = new JTextPane();
        messagePane.setEditable(false); // Make it non-editable
        JScrollPane scrollPane = new JScrollPane(messagePane); // Add scrolling functionality

        // Panel for message input section (TextField and Button)
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField msgs = new JTextField();
        JButton sendMsgs = new JButton("Send Messages");
        sendMsgs.setBackground(Color.orange);

        // Add components to input panel
        inputPanel.add(msgs, BorderLayout.CENTER);
        inputPanel.add(sendMsgs, BorderLayout.EAST);

        // Add scroll pane and input panel to the main panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // Send message on button click or Enter key
        sendMsgs.addActionListener(e -> sendMessage(msgs, messagePane));
        msgs.addActionListener(e -> sendMessage(msgs, messagePane)); // Allow Enter key to send

        // Separate thread to listen for messages from the server (broadcasted messages)
        new Thread(() -> {
            try {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
                while (scanner.hasNextLine()) {
                    String serverMsg = scanner.nextLine();
                    if (!serverMsg.contains(username)) {
                        // Display server message in JTextPane with gray background
                        appendMessage(messagePane, "[" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "] " + serverMsg + "\n", Color.GRAY, Color.BLACK);
                    }
                }
                scanner.close();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to read message from server", ex);
            }
        }).start();

        JButton logOUT = new JButton("LogOUT");
        logOUT.addActionListener(e -> showMenuPanel());
        panel.add(logOUT, BorderLayout.NORTH);
        logOUT.setBackground(Color.orange);
        logOUT.setForeground(Color.white);

        return panel;
    }


    // Method to send message
    private void sendMessage(JTextField msgs, JTextPane messagePane) {
        String msg = msgs.getText().trim();
        if (!msg.isEmpty()) {
            try {
                // Send the message to the server
                outputStream.write((username + " : " + msg + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // Display the message in the JTextPane (client-side display) with orange background
                appendMessage(messagePane, "[" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "] You: " + msg + "\n", Color.ORANGE, Color.BLACK);
                msgs.setText(""); // Clear the text field
            } catch (IOException ex) {
                throw new RuntimeException("Failed to send message", ex);
            }
        }
    }

    // Method to append messages to JTextPane with specified background and foreground colors
    private void appendMessage(JTextPane messagePane, String message, Color backgroundColor, Color foregroundColor) {
        try {
            // Set the text and styles for the message
            StyledDocument doc = messagePane.getStyledDocument();

            // Create style for background
            Style style = messagePane.addStyle("Style", null);
            StyleConstants.setBackground(style, backgroundColor);
            StyleConstants.setForeground(style, foregroundColor);

            // Append the message
            doc.insertString(doc.getLength(), message, style);

            // Scroll to the bottom of the JTextPane
            messagePane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private void showMainPanel() {
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Client application");
        cardLayout.show(mainPanel, "Login");
    }

    private void showMessagePanel() {
        frame.setSize(400, 600); // Set a larger size for the message panel
        frame.setLocationRelativeTo(null); // Center the window
        cardLayout.show(mainPanel, "Message");
    }
    private void showRegisterPanel() {
        frame.setSize(400, 600); // Set a larger size for the message panel
        frame.setLocationRelativeTo(null); // Center the window
        cardLayout.show(mainPanel, "Register");
    }
    private void showMenuPanel() {
        frame.setSize(400, 300); // Set a larger size for the message panel
        frame.setLocationRelativeTo(null); // Center the window
        frame.setTitle("Client application");
        cardLayout.show(mainPanel, "Menu");
    }
    private void showLoginPanel() {
        frame.setSize(400, 300); // Set a larger size for the message panel
        frame.setLocationRelativeTo(null); // Center the window
        frame.setTitle("Client application");
        cardLayout.show(mainPanel, "Login");
    }
    private void showAdminPanel() {
        frame.setSize(600, 600); // Set a larger size for the message panel
        frame.setLocationRelativeTo(null); // Center the window
        frame.setTitle("ADMIN");
        cardLayout.show(mainPanel, "AdminPanel");
    }

    private JPanel createAdminPanel() throws IOException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(600,600);

        JTextPane messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setBackground(Color.black);

        JScrollPane scrollPane = new JScrollPane(messagePane);
        panel.add(scrollPane,BorderLayout.CENTER);


        File us = new File("USERS\\Usernames.txt");
        if(us.exists()) {
            BufferedReader readusers = new BufferedReader(new FileReader(us));
            int counter =0;
            String msg;
            String poraka = "";
            while((msg = readusers.readLine()) != null){
                counter++;
                for(int i=0;i<counter;i++) {
                    File whole = new File("USERS\\" + msg + ".txt");
                    if (whole.exists()) {
                        BufferedReader readwhole = new BufferedReader(new FileReader(whole));
                        String usrname = readwhole.readLine();
                        String name = readwhole.readLine();
                        String surname = readwhole.readLine();
                        String age = readwhole.readLine();
                        String email = readwhole.readLine();
                        String pass = readwhole.readLine();
                        poraka = "Name:"+ name + "\nSurname:"+surname + "\nAge:"+ age + "\nEmail:"+ email+ "\nUsername:" + usrname + "\nPassword:"+ pass +"\n";
                    }
                }
                appendMessage(messagePane,counter + ": " + msg + "\n" + poraka + "\n",Color.BLACK,Color.white);
            }
        }else{
            System.out.println("File does not exist");
        }

        return panel;
    }


    // Panel for the main menu after login
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(6,1));

        JButton changeUsername = new JButton("Change Username");
        changeUsername.addActionListener(e -> cardLayout.show(mainPanel, "ChangeUsername"));
        changeUsername.setBackground(Color.lightGray);
        panel.add(changeUsername);

        JButton changePassword = new JButton("Change Password");
        changePassword.addActionListener(e -> cardLayout.show(mainPanel, "ChangePass"));
        changePassword.setBackground(Color.lightGray);
        panel.add(changePassword);

        JButton changeEmail = new JButton("Change Email");
        changeEmail.addActionListener(e -> cardLayout.show(mainPanel, "ChangeMail"));
        panel.add(changeEmail);
        changeEmail.setBackground(Color.lightGray);

        JButton sendMessages = new JButton("Send Messages");
        sendMessages.addActionListener(e -> showMessagePanel());
        panel.add(sendMessages);
        sendMessages.setBackground(Color.lightGray);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> showLoginPanel());
        logoutButton.setBackground(Color.lightGray);
        panel.add(logoutButton);



        JButton deleteACC = new JButton("DELETE ACC");
        deleteACC.addActionListener(e -> {
            try {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    Client.deleteUser(username,"");
                    File oldUserinfo = new File("USERS\\" + username + ".txt");
                    if(oldUserinfo.delete()) {
                        System.out.print("");
                    }else{
                        System.out.print("");
                    }
                    cardLayout.show(mainPanel, "Login");
                } else if (response == JOptionPane.NO_OPTION) {
                    showMenuPanel();
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteACC.setBackground(Color.RED);
        panel.add(deleteACC);

        return panel;
    }


    // Handle login
    private void handleLogin() throws IOException {
        String ime = usernameField.getText();
        String password = new String(passwordField.getPassword());

        usernameField.setText("");
        passwordField.setText("");

        username = ime;
        if(Client.checkAdmin(ime,password)){
            showAdminPanel();
        }
        else if (Client.checkLogin(ime, password)) {
            JOptionPane.showMessageDialog(frame, "Login successful!");
            frame.setTitle(username.toUpperCase());
            cardLayout.show(mainPanel, "Menu");
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            cardLayout.show(mainPanel, "Login");
        }
    }

    // Handle registration
    private void handleRegister() throws IOException {
        String name = regNameField.getText();
        regNameField.setText("");
        String surname = regSurnameField.getText();
        regSurnameField.setText("");
        Integer age = Integer.valueOf(regAgeField.getText());
        regAgeField.setText("");
        String email = regMailField.getText();
        regMailField.setText("");
        String username = regUsernameField.getText();
        regUsernameField.setText("");
        String password = new String(regPasswordField.getPassword());
        regPasswordField.setText("");

        Client.RegisterUser(name,surname,username,password,email,age);
            JOptionPane.showMessageDialog(frame, "Registration successful!");
            showLoginPanel();

        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ClientGUI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
