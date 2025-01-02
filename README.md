[English](README.md) | [中文](README_zh.md)

### Remote Desktop Application

This project is a **Java** and **Netty** based remote desktop control application. Through this application, users can
connect and control remote devices in real-time. It is based on the client-server-client version of the core code
from [Dayon GitHub Repository](https://github.com/RetGal/Dayon). Special thanks to the Dayon project author.

### Features

1. **Real-time Remote Desktop Control**
    * Remotely control another device with minimal latency.

2. **Customizable Settings**
    * Configure screen capture intervals and enable/disable color mode to optimize performance.

3. **Cross-platform Support**
    * Developed using Java, compatible with most operating systems.

## Screenshots

### Main Control Panel

![remote-desktop-control](z_launcher.png)

### Remote Connection Established

![remote-desktop-control](z_screen.png)

### Settings Menu

![remote-desktop-control](z_screen_setting.png)

### Environment

* Java 8 or higher
* Maven for dependency management

### Build and Run

1. Clone the repository：
   ```bash
   git https://github.com/SpringStudent/remote-desktop-control
   cd remote-desktop-control
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the server: Modify the application.properties configuration file with the database information and the
   netty.server.server and port configurations.
   ```bash
   RemoteServer.java
   ```

4. Run the client: Modify the RemoteClient.java parameters for serverIp and serverPort.
   ```bash
   RemoteClient.java
   ```

### Future Plan

* http-based clipboard transmission (finish)
