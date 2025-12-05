# Distributed P2P Chat System (Java)

## Overview
Simple peer-to-peer chat node using Java sockets. Each node:
- Listens for incoming TCP connections
- Can connect to other peers
- Sends and receives messages
- Maintains a local chat history file

## Files
src/
  Node.java           - main program (start server, CLI)
  PeerList.java       - manage connected peers
  MessageHandler.java - per-connection receiver thread
  Utils.java          - history saving helpers

## Compile
From project root:

```bash
javac -d out src/*.java
```

This compiles classes into the `out` folder.

## Run
Open multiple terminals to run several nodes locally.

Terminal 1:
```bash
java -cp out Node Alice 5000
```

Terminal 2:
```bash
java -cp out Node Bob 5001
```

In Bob's terminal, connect to Alice:
```
/connect 127.0.0.1 5000
```

Commands:
- `/connect <ip> <port>`  connect to a peer
- `/peers`                list connected peers
- `/history`              print local history filename
- `/quit`                 exit
- typing text             broadcast to all connected peers

History files: `history_<username>.txt`
