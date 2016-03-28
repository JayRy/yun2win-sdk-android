package y2w.model;

import y2w.manage.SessionMembers;
import y2w.entities.SessionMemberEntity;

/**
 * Created by maa2 on 2016/1/16.
 */
public class SessionMember {

    private SessionMemberEntity entity;
    private SessionMembers sessionMembers;

    public SessionMember(SessionMembers sessionMembers,SessionMemberEntity entity){
        this.sessionMembers = sessionMembers;
        this.entity = entity;

    }

    public SessionMemberEntity getEntity() {
        return entity;
    }

    public SessionMembers getSessionMembers() {
        return sessionMembers;
    }
}
