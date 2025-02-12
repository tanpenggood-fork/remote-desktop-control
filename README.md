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

4. **Clipboard Support**
    * No speed limit file transfer.

## Screenshots

### Main Control Panel

![remote-desktop-control](z_launcher.png)

### Remote Connection Established

![remote-desktop-control](z_screen.png)

![remote-desktop-control](z_monitor.png)

### Settings Menu

![remote-desktop-control](z_screen_setting.png)
![remote-desktop-control](z_compress_setting.png)
![remote-desktop-control](z_clipboard.png)

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

3. Run the server: Export remote-desktop-control.sql to mysql's databases,Modify the application.properties configuration file with the database information and the
   netty.server.server and port configurations.
   ```bash
   RemoteServer.java
   ```

4. Run the client: Modify the RemoteClient.java parameters for serverIp and serverPort and clipboardServer addr
   ```bash
   RemoteClient.java
   ```
### Demo Video

[![Bilibili Video](https://i0.hdslb.com/bfs/archive/6f2f28a5ec06208ef1b7b78d134fa7b970c4e265.jpg@672w_378h_1c.avif)](https://www.bilibili.com/video/BV11qNCeNEoZ/)

### Future Plan

* http-based clipboard transmission (finish)
* internationalize  

### Q&A

* It is recommended that both the control end and the controlled end run the program with administrator privileges,
  otherwise, some programs on the controlled end may not be controllable due to lack of permissions.
* For the best control experience, it is recommended to set the input language preference on the control end to "
  English (United States)".