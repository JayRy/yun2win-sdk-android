package y2w.model;

import java.util.List;

import y2w.manage.UserConversations;
import y2w.entities.MessageEntity;
import y2w.entities.UserConversationEntity;
import y2w.service.Back;

/**
 * Created by maa2 on 2016/1/16.
 */
public class UserConversation {


    private UserConversationEntity entity;
    private UserConversations userConversations;

    public UserConversation(UserConversations userConversations,UserConversationEntity entity) {
        this.userConversations = userConversations;
        this.entity = entity;
    }

    public UserConversationEntity getEntity() {
        return entity;
    }

    public UserConversations getUserConversations() {
        return userConversations;
    }

    public void getSession(Back.Result<Session> result) {
        userConversations.getUser().getSessions().getSessionByTargetId(entity.getTargetId(),entity.getType(),result);
    }

    public void syncMessages(Back.Result<List<MessageEntity>> result) {

    }

    public void getLastMessages(Back.Result<List<MessageEntity>> result) {

    }



}
