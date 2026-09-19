# Dictionary Project

A simple Java client/server dictionary application. The server loads word definitions from `dictionary.json`, and the Swing client connects to the server to search for words.

## Requirements

- Java 17 or newer
- IntelliJ IDEA

The project uses Gson, included in `lib/gson-2.10.1.jar`.

## Run in IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Run `server.DictionaryServer`.
   - Program arguments:
     ```text
     3000 dictionary.json
     ```
   - Working directory: the project root.
3. Run `client.ClientGUI`.
4. Enter a word and click **Search**.

Always start the server before the client.

## Run from a terminal

From the project root, compile the source files:

```bash
javac -cp "lib/gson-2.10.1.jar" -d out/production/DictionaryProject src/server/*.java src/client/*.java
```

Start the server:

```bash
java -cp "out/production/DictionaryProject:lib/gson-2.10.1.jar" server.DictionaryServer 3000 dictionary.json
```

In another terminal, start the client:

```bash
java -cp "out/production/DictionaryProject:lib/gson-2.10.1.jar" client.ClientGUI
```
<img width="1920" height="1080" alt="Screenshot From 2026-09-19 05-34-11" src="https://github.com/user-attachments/assets/240e5440-0aef-42db-8283-764a4d48d3e7" />
