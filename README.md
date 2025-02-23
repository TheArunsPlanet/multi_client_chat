# Multi-Client Chat Application

A simple chat application in Java that supports both dual-client and multi-client communication. Users can send and receive messages in real time.

## Features
- Two-client chat (Dualclient)
- Multi-client chat (multiple clients can connect to the server)
- Real-time message broadcasting

## Installation

1. **Clone the repo:**

   ```bash
   git clone https://github.com/TheArunsPlanet/multi_client_chat.git
   cd multi_client_chat
   ```

2. **Compile Java files:**

   ```bash
   javac dualclient/Dualclient.java
   javac dualclient/Dualclientserver.java
   javac multiclient/Client.java
   javac multiclient/Server.java
   ```

3. **Run the server:**

   ```bash
   java dualclient.Dualclientserver
   ```

   Or for multi-client:

   ```bash
   java multiclient.Server
   ```

4. **Run the client:**

   ```bash
   java dualclient.Dualclient
   ```

   Or for multi-client:

   ```bash
   java multiclient.Client
   ```

5. **Exit the chat:**

   Type `exit` to disconnect.

## Author

- **Arun Kumar** - [TheArunsPlanet](https://github.com/TheArunsPlanet)

This version is more concise and includes the basic steps for setting up and running the chat application. Let me know if you need further adjustments!
