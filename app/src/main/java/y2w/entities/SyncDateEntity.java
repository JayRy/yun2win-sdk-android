package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by maa2 on 2016/1/28.
 */
@DatabaseTable (tableName = "SyncDateEntity")
public class SyncDateEntity implements Serializable{

    @DatabaseField
    private String userId;
    @DatabaseField
    private String contactDate;
    @DatabaseField
    private String userConversationDate;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactDate() {
        return contactDate;
    }

    public void setContactDate(String contactDate) {
        this.contactDate = contactDate;
    }

    public String getUserConversationDate() {
        return userConversationDate;
    }

    public void setUserConversationDate(String userConversationDate) {
        this.userConversationDate = userConversationDate;
    }
}
