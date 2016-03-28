package y2w.service;

import com.yun2win.imlib.IMClient;

/**
 * Created by maa2 on 2016/3/10.
 */
public class IMBridge {

    private IMClient.onConnectionStatusChanged changed = new IMClient.onConnectionStatusChanged() {
        @Override
        public void onChanged(int status, int error) {

        }
    };

    class ConnectPara{
        String token;
        String uid;
        IMClient.onConnectionStatusChanged connectionStatusChanged;
        IMClient.OnMessageReceiveListener messageReceiveListener;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public IMClient.onConnectionStatusChanged getConnectionStatusChanged() {
            return connectionStatusChanged;
        }

        public void setConnectionStatusChanged(IMClient.onConnectionStatusChanged connectionStatusChanged) {
            this.connectionStatusChanged = connectionStatusChanged;
        }

        public IMClient.OnMessageReceiveListener getMessageReceiveListener() {
            return messageReceiveListener;
        }

        public void setMessageReceiveListener(IMClient.OnMessageReceiveListener messageReceiveListener) {
            this.messageReceiveListener = messageReceiveListener;
        }
    }



    private ConnectPara create(){
        ConnectPara para = new ConnectPara();
        para.setToken("");
        para.setToken("");
        para.setConnectionStatusChanged(changed);

        return  para;
    }


    public void connect(ConnectPara para){

    }
}
