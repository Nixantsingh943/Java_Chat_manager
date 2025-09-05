

---

# Java Chat Application (Client + Server)

This project is a **multi-user chat application** written in **Java**. It includes:

1. A **Chat Server** that manages client connections, user lists, broadcasts, and private messages.
2. A **Chat Client GUI** built with **Java Swing**, providing a graphical interface for sending and receiving messages in real time.

The project demonstrates how to use **Java networking (sockets)**, **multithreading**, and **Swing GUI programming** together to build a complete communication system.

---

## Features

### Client

* Built with **Java Swing** for an interactive graphical interface.
* Chat area with colored usernames for easy identification.
* Timestamps for every message.
* Sidebar showing the list of online users.
* Private messaging support.
* Handles system notifications (users joining/leaving).

### Server

* Accepts multiple client connections using threads.
* Broadcasts messages to all connected clients.
* Sends updated user lists to all clients.
* Supports private messages using special commands.
* Tracks user join/leave events.

---

## Technologies Used

* **Java SE** (Networking + Multithreading + Swing)
* **Socket Programming** for client-server communication
* **Swing GUI** for user-friendly interface
* **Collections Framework** for managing users and colors

---

## File Structure

```
chat-application/
│── ChatClientGUI.java   # Client GUI code
│── ChatServer.java      # Server code
│── README.md            # Project documentation
```

---

## How It Works

### Server Side

* The server listens on a port (default: `114114`).
* When a client connects, the server creates a new thread for handling communication.
* The server keeps track of all connected users and their usernames.
* Messages are broadcasted to all users, except private messages which are routed only to the intended recipient.
* The server regularly updates the `/users` list so clients can see who is online.

### Client Side

* When the user starts the client, they enter a username.
* The client connects to the server at the specified IP and port.
* Messages from the server are displayed in the chat window with timestamps.
* Usernames are assigned random colors for easy distinction.
* The user list on the right side updates dynamically as users join or leave.
* Special messages (system events, private messages) are highlighted in different colors.

---

## Requirements

* **Java Development Kit (JDK) 8 or later**
* Works on Windows, Linux, and macOS

---

## How to Run

### Step 1: Run the Server

1. Compile the server code:

   ```bash
   javac ChatServer.java
   ```
2. Start the server on the default port (`114114`):

   ```bash
   java ChatServer
   ```

### Step 2: Run the Client

1. Compile the client code:

   ```bash
   javac ChatClientGUI.java
   ```
2. Run the client:

   ```bash
   java ChatClientGUI
   ```
3. Enter your username when prompted.
4. The client will connect to the server running on `10.10.0.149:114114` (localhost).

   * To connect to a remote server, change the IP address in the `main` method of `ChatClientGUI.java`.

---

## Example Workflow

1. Start the server on one Computer.
2. Run multiple clients (each with different usernames).
3. Each client can:

   * Send public messages to everyone.
   * See who is online in the user list.
   * Send private messages using the supported format.
   * View system notifications when users join or leave.

---


