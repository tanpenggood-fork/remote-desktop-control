package io.github.springstudent.dekstop.client;
/**
 *
 * @author ZhouNing
 * @date 2024/12/6
 */
public class RemoteClient extends RemoteFrame {

    private RemoteControll controll;

    private RemoteScreen screen;

    public RemoteClient(){
        connect();
        setDeviceCode("abcdefg");
        setPassword("aoeiue");
    }

    @Override
    protected void launchRemoteScreen(String remoteName) {
        this.screen = new RemoteScreen(remoteName);
        screen.launch();
    }

    private void connect(){

    }

    public static void main(String[] args) {
        new RemoteClient();
    }

}