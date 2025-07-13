import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class Server extends JFrame{

    // to make port number and listen to it and accept th eclient request if any come
    ServerSocket server;
    //use for connection between client and server using serversocket
    Socket socket;
    //to read input that come from clients 
    BufferedReader br;
    // to write what ever we want to send teh client
    PrintWriter out;

    private JLabel head=new JLabel("Server");
    private JTextArea message=new JTextArea();
    private JTextField addMessage=new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Server(){

        try{
            server = new ServerSocket(7777);
            System.out.println("server is ready to accept connections");
            System.out.println("waiting...");
            socket = server.accept();
            System.out.println("Client connected");



            // read the data that come from that socket
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // to send the data to client
            out=new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

             startReading();
            // writeReading();
        }catch(Exception e){
           
            e.printStackTrace();
        }


    }
    private void handleEvents(){
        addMessage.addKeyListener(new KeyListener() {
            

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("key pressed :"+ e.getKeyCode());
                if(e.getKeyCode() == 10){
                    String contentToSend=addMessage.getText();
                    message.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    addMessage.setText("");
                }
            }
            
        });
    }
    private void createGUI(){
    this.setTitle("Chat App");
    this.setSize(400,500);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    head.setFont(font);
    message.setFont(font);
    addMessage.setFont(font);
    head.setHorizontalAlignment(SwingConstants.CENTER);
    head.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
    message.setEditable(false);

    this.setLayout(new BorderLayout());

    this.add(head,BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(message);
    this.add(jScrollPane,BorderLayout.CENTER);
    this.add(addMessage,BorderLayout.SOUTH);

    
    this.setVisible(true);
    }
        public void startReading(){
            //thread give data to read
            
            Runnable r1=()->{
                System.out.println("Reader start");
                try {
                while(true){
                        String msg = br.readLine();
                        if(msg.equals("quiet")){
                            System.out.println("Client closed the chat.");
                            JOptionPane.showMessageDialog(this, "Client closed the chat.");
                            addMessage.setEnabled(false);
                            socket.close();
                            break;
                        }
                        // System.out.println("Client :"+ msg);
                        message.append("Client :" +msg+"\n");
                        message.setCaretPosition(message.getDocument().getLength());

                        
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println("Connection closed.");
                    addMessage.setEnabled(false);

                }

            };
            new Thread(r1).start();
        }
        public void writeReading(){
            // thread give the data to socket
             Runnable r2=()->{
                System.out.println("Writing....");
                try {
                while (!socket.isClosed()) {
                    
                        BufferedReader br1= new BufferedReader(new InputStreamReader(System.in));
                        String content=br1.readLine();
                        out.println(content);
                        out.flush();
                        if(content.equals("quiet")){
                            socket.close();
                            break;
                        }
                        
                    }
                    } catch (Exception e) {
                    //    e.printStackTrace();
                    System.out.println("Connection closed.");
                    }
                
            };
            new Thread(r2).start();
        }


    public static void main(String[] args) {
        System.out.println("This is server going to start server make a chat app");
        new Server();
        
    }
}