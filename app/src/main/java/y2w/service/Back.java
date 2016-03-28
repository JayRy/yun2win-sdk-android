package y2w.service;

/**
 * Created by maa2 on 2016/1/30.
 */
public class Back {

    public abstract static class Callback{
        public  abstract void onSuccess();
        public  abstract void onError(ErrorCode errorCode);
    }

    public abstract static class ConnectCallback{
        public  abstract void onSuccess();
        public  abstract void onError(ErrorCode errorCode);
        public  abstract void onTokenIncorrect();
    }

    public abstract static class Result<T> extends Object{
        public  abstract void onSuccess(T t);
        public  abstract void onError(ErrorCode Code,String error);
    }

}
