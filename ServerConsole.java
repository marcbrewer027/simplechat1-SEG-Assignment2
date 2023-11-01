import client.ChatClient;
import common.ChatIF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {

    final public static int DEFAULT_PORT = 5555;

    EchoServer server;

    /**
     * Constructs an instance of the ServerConsole UI.
     *
     * @param port The port to connect on.
     */
    public ServerConsole(int port)
    {
        server= new EchoServer(port);
    }

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept()
    {
        try
        {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true)
            {
                message = fromConsole.readLine();
                server.sendToAllClients(message); // **** This could be implemented to do more useful things like commands. MB
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        System.out.println("> " + message);
    }

    public static void main(String[] args)
    {
        int port = 0; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole sv = new ServerConsole(port);

        Thread consoleThread = new Thread(sv::accept); // **** async call to listen for console messages. MB
        consoleThread.start();

        try
        {
            sv.server.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() // **** Modified to close the server upon exit. MB
        {
            public void run()
            {
                try {
                    sv.server.close();
                } catch (IOException e) {
                    System.out.println("ERROR - Could not close!");
                }
            }
        });

    }

}
