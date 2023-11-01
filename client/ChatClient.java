// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import ocsf.client.AbstractClient;

import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles command functions.
   *
   * @param command The command message from the client.
   */
  public void handleCommand(String command) throws IOException { // **** function to handle commands
    String[] commandParts = command.split(" ");
      switch (commandParts[0]) {
        case "#quit":
          clientUI.display("quitting...");
          quit();
          break;
        case "#logoff":
          clientUI.display("logging off...");
          closeConnection();
          break;
        case "#sethost":
          if (!isConnected()) {
            this.setHost(commandParts[1]);
            clientUI.display("new host set!");
          } else {
            throw new IllegalStateException();
          }
          break;
        case "#setport":
          if (!isConnected()) {
            clientUI.display("new port set!");
            this.setPort(Integer.parseInt(commandParts[1]));
          } else {
            throw new IllegalStateException();
          }
          break;
        case "#login":
          clientUI.display("Logging in...");
          openConnection();
          break;
        case "#gethost":
          clientUI.display(getHost());
          break;
        case "#getport":
          clientUI.display(String.valueOf(getPort()));
          break;
        default:
          throw new IllegalArgumentException();
      }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if (message.startsWith("#")) { // **** Looking for command symbol. MB
        handleCommand(message);
      } else {
        sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
    catch(IllegalArgumentException e) {
      clientUI.display("Unknown command!");
    } catch(IllegalStateException e) {
      clientUI.display("Must be disconnected to change settings!");
    }
  }

  @Override
  protected void connectionClosed() { // **** Wait for quit() method MB
    super.connectionClosed();
    clientUI.display("Connection to server closed.  Terminating client.");
  }

  @Override
  protected void connectionException(Exception exception) { // **** Listening for server disconnection MB
    super.connectionException(exception);
    quit();
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
