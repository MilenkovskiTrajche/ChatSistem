import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client extends Thread {
    private int port;
    private String ipAdress;
    public User u;
    public OutputStream outputStream;
    public Scanner scanner;

    public Client(int port, String ipAdress) {
        this.port = port;
        this.ipAdress = ipAdress;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ipAdress, port);
            outputStream = socket.getOutputStream();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkUsernamesExist(String us) throws IOException {
        BufferedReader readUsernames = new BufferedReader(new FileReader("USERS\\Usernames.txt"));
        String usr;
        while ((usr = readUsernames.readLine()) != null) {
            if (usr.equals(us)) {
                readUsernames.close();
                return true;
            }
        }
        readUsernames.close();
        return false;
    }

    public static User getUserInfo(String usrname) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("USERS\\"+usrname + ".txt"));
        String username = reader.readLine(); // First line: username
        String name = reader.readLine();     // Second line: name
        String surname = reader.readLine();  // Third line: surname
        String age = reader.readLine();
        String email = reader.readLine();
        String password = reader.readLine();
        reader.close();

        return new User(name, surname, username, password, email, Integer.parseInt(age));
    }

    public static void ChangePassword(String usrname,String newPassword) throws IOException {
        User newUser = getUserInfo(usrname);
        newUser.setPassword(newPassword);
        newUser.writeUser();
        System.out.println(usrname + " " + newPassword);
    }

    public static boolean checkAdmin(String usrname,String psw){
        if(usrname.equals("admin") && psw.equals("admin")){
            return true;
        }

        return false;
    }

    public static boolean checkLogin(String usrname,String psw) throws IOException {
        if (checkUsernamesExist(usrname)) {
            User user = getUserInfo(usrname);

                if(user.getPassword().equals(psw)){
                    return true;
                }else{
                    System.out.print("Wrong password\n");
                }
        }else{
            System.out.print("Username does not exist.\n");
        }
        return false;
    }

    public static void deleteUser(String username,String newUsername) throws IOException {
        // Original file containing usernames
        File users = new File("USERS\\Usernames.txt");
        // Temporary file where valid usernames will be written
        File tempFile = new File("USERS\\TempUsernames.txt");

        // Ensure the temporary file is created
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }

        // Readers and writers for the original and temp files
        BufferedReader reader = new BufferedReader(new FileReader(users));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));

        String currentUser;
        boolean isUserDeleted = false;

        // Read through each line (username) in the original file
        while ((currentUser = reader.readLine()) != null) {
            // Only write usernames that are not equal to the one to delete
            if (!currentUser.equals(username)) {
                writer.write(currentUser + System.lineSeparator());
            } else {
                writer.write(newUsername + "\n");
                isUserDeleted = true;  // Mark that the user was found and deleted
            }
        }

        reader.close();
        writer.close();

        // If the user was successfully deleted, proceed with replacing the original file
        if (isUserDeleted) {
            // Delete the original file
            if (!users.delete()) {
                System.out.println("Failed to delete the original file.");
                return;
            }
            // Rename the temp file to the original file name
            if (!tempFile.renameTo(users)) {
                System.out.println("Failed to rename the temp file.");
            } else {
                System.out.println("User '" + username + "' deleted successfully.");
            }
        } else {
            
            // If the username was not found, delete the temp file
            tempFile.delete();
            System.out.println("User '" + username + "' not found.");
        }
    }



    public static void RegisterUser(String name,String surname,String nickname,String password,String email,int age) throws IOException {
        User newUser = new User(name, surname, nickname, password,email,age);
        BufferedWriter writer = new BufferedWriter(new FileWriter("USERS\\Usernames.txt",true));
        writer.write(nickname + "\n");
        writer.flush();
        //go zapisuvame noviot user vo .txt
        newUser.writeUser();
        System.out.print("\nSuccessfully user created!\n");

        newUser.print();
        System.out.print("\n");
    }



    public static class User {
        private String name;
        private String surname;
        private String nickname;
        private String password;
        private String email;
        private int age;

        public User(String name, String surname, String nickname, String password, String email, int age) {
            this.name = name;
            this.surname = surname;
            this.nickname = nickname;
            this.password = password;
            this.email = email;
            this.age = age;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public  void writeUser() throws IOException {
            String pathname = "USERS\\";
            File users = new File(pathname + nickname + ".txt");
            if(users.delete()){
                System.out.print("");
            }else{
                System.out.println("");
            }
            File unew = new File(pathname + nickname + ".txt");

            if(unew.createNewFile()) {
                System.out.print("");
            }else {
                System.out.println("");
            }
            BufferedWriter writeUsers = new BufferedWriter(new FileWriter(unew,true));
            //go zapisuvame noviot user
            writeUsers.write(nickname + "\n" + name + "\n" + surname + "\n" + age + "\n" + email + "\n" + password + "\n");
            writeUsers.close();
        }

        public void print(){
            System.out.println("USER:"+ nickname+"\nName:" + this.name + "\nSurname:" + this.surname + "\nAge:" + this.age + "\nEmail:" + this.email);
        }
    }
}
