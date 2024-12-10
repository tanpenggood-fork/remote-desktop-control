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
        //TODO 连接至远程服务器
        connectServer();
        setDeviceCode("abcdefg");
        setPassword("aoeiue");
    }

    @Override
    protected void openRemoteScreen(String remoteName) {
        this.screen = new RemoteScreen(remoteName,this);
        controll = new RemoteController();
        openSession();
        screen.launch();
    }

    @Override
    protected void closeRemoteScreen() {
        if(isController){
            super.closeRemoteScreen();
        }
        closeSession();
    }

    /**
     * 连接至server
     */
    private void connectServer(){

    }

    /**
     * 关闭会话
     */
    private void closeSession(){

    }

    /**
     * 打开会话
     */
    private void openSession(){

    }


    public static void main(String[] args) {
        new RemoteClient();
    }

}