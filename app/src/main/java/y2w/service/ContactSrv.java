package y2w.service;

import com.yun2win.imlib.IMClient;
import java.util.List;

import y2w.base.ClientFactory;
import y2w.entities.ContactEntity;
import y2w.model.Contact;

/**
 * Created by maa2 on 2016/1/19.
 */
public class ContactSrv {

    private static ContactSrv contactSrv = null;
    public static ContactSrv getInstance(){
        if(contactSrv == null){
            contactSrv = new ContactSrv();
        }
        return contactSrv;
    }

    public void getContacts(String token, String updateAt,int limit,String userId,Back.Result<List<ContactEntity>> result){
        ClientFactory.getInstance().getContacts(token, updateAt,limit, userId, result);
    }

    public void contactAdd(String token, String userId ,String otherId, String email, String name, String avatarUrl, Back.Result<ContactEntity> result){
        ClientFactory.getInstance().contactAdd(token, userId, otherId, email, name, avatarUrl, result);
    }

    public void contactDelete(String token, String userId,String id, Back.Callback callback){
        ClientFactory.getInstance().contactDelete(token, userId, id, callback);
    }

    public void contactUpdate(String token, Contact contact, Back.Callback callback){
        ClientFactory.getInstance().contactUpdate(token, contact.getEntity().getMyId(),contact.getEntity().getUserId(),
                contact.getEntity().getId(), contact.getEntity().getName(),
                 contact.getEntity().getName(), contact.getEntity().getAvatarUrl(),
                contact.getEntity().getAvatarUrl(), callback);

    }

    public void contactSearch(String token, String keyword,Back.Result<List<ContactEntity>> result){
        ClientFactory.getInstance().contactSearch(token, keyword,result);
    }

}
