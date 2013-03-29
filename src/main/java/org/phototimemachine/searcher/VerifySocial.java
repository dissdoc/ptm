package org.phototimemachine.searcher;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.phototimemachine.common.Verify;

/**
 * Created with IntelliJ IDEA.
 * User: dissdoc
 * Date: 21.03.13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class VerifySocial {

    private DB db;
    private DBCollection collection;
    private DBCursor cursor;

    public VerifySocial() {
        try {
            Mongo mongo = new Mongo("localhost", 27017);
            db = mongo.getDB("ptm");
            collection = db.getCollection("socials");
            cursor = null;
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void create(Verify verify) {
        try {
            BasicDBObject doc = new BasicDBObject();
            doc.put("email", verify.getEmail());
            doc.put("password", verify.getPassword());
            doc.put("confirm", verify.getConfirm());
            doc.put("uid", verify.getUid());
            doc.put("social", verify.getSocial());
            collection.insert(doc);
            verify.setId(doc.get("_id").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            verify = null;
        }
    }

    public Verify find(String id, String password) {
        try {
            DBObject doc = collection.findOne(new BasicDBObject("_id", new ObjectId(id))
                    .append("password", password)
                    .append("confirm", Boolean.FALSE));
            Verify verify = new Verify();
            verify.setId(id);
            verify.setPassword(password);
            verify.setEmail(doc.get("email").toString());
            verify.setUid(doc.get("uid").toString());
            verify.setConfirm(Boolean.FALSE);
            verify.setSocial(doc.get("social").toString());
            return verify;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public void update(String id) {
        try {
            collection.remove(new BasicDBObject("id", new ObjectId(id)));
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
