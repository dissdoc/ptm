package org.phototimemachine.searcher;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.phototimemachine.common.Recommend;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.web.util.FormatDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kmuhov
 * Date: 22.03.13
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public class RecommendGenerator {

    private DB db;
    private DBCollection collection;
    private DBCursor cursor;

    public RecommendGenerator() {
        try {
            Mongo mongo = new Mongo("localhost", 27017);
            db = mongo.getDB("ptm");
            collection = db.getCollection("recommendations");
            cursor = null;
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void create(Object object, AppUser user, String name, String message, String annotation) {
        try {
            DBObject doc = new BasicDBObject();
            doc.put("user_id", user.getUserId());
            doc.put("user_name", user.fullName());
            doc.put("status", Boolean.FALSE);
            doc.put("name", name);
            doc.put("message", message);
            doc.put("ts", new Date());
            doc.put("annotation", annotation);

            if (object instanceof Photo) {
                Photo photo = (Photo) object;
                doc.put("type", "photo");
                doc.put("type_id", photo.getId());
                doc.put("author_id", photo.getAppUser().getUserId());
                doc.put("author_name", photo.getAppUser().fullName());
                doc.put("type_name", photo.getName());
            }

            collection.insert(doc);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public int newRecommends(AppUser user) {
        try {
            return collection.find(new BasicDBObject("author_id", user.getUserId()).append("status", Boolean.FALSE)).count();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public List<Recommend> recommendsLimit(AppUser user) {
        try {
            cursor = collection.find(new BasicDBObject("author_id", user.getUserId()).append("status", Boolean.FALSE))
                    .sort(new BasicDBObject("date", -1))
                    .limit(3);
            List<Recommend> list = new ArrayList<Recommend>();
            while(cursor.hasNext()) {
                DBObject doc = cursor.next();
                Recommend item = new Recommend();
                item.setId(doc.get("_id").toString());
                item.setAuthorId(Long.parseLong(doc.get("author_id").toString()));
                item.setAuthorName(doc.get("author_name").toString());
                item.setUserId(Long.parseLong(doc.get("user_id").toString()));
                item.setUserName(doc.get("user_name").toString());
                item.setType(doc.get("type").toString());
                item.setTypeId(Long.parseLong(doc.get("type_id").toString()));
                item.setName(doc.get("name").toString());
                item.setStatus(Boolean.parseBoolean(doc.get("status").toString()));
                if (doc.get("type_name") != null)
                    item.setTypeName(doc.get("type_name").toString());

                if (item.getName().equals("date")) {
                    String message = doc.get("message").toString();
                    String[] parts = message.split("-");

                    String[] start = parts[0].split("\\.");
                    String date = FormatDate.dt(Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2]));

                    String[] end = parts[1].split("\\.");
                    String date2 = FormatDate.dt(Integer.parseInt(end[0]), Integer.parseInt(end[1]), Integer.parseInt(end[2]));

                    item.setMessage(FormatDate.fulldt(date, date2));
                } else {
                    item.setMessage(doc.get("message").toString());
                }
                list.add(item);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Recommend> recommends(AppUser user) {
        try {
            cursor = collection.find(new BasicDBObject("author_id", user.getUserId()))
                    .sort(new BasicDBObject("date", -1));
            List<Recommend> list = new ArrayList<Recommend>();
            while (cursor.hasNext()) {
                DBObject doc = cursor.next();
                Recommend item = new Recommend();
                item.setId(doc.get("_id").toString());
                item.setAuthorId(Long.parseLong(doc.get("author_id").toString()));
                item.setAuthorName(doc.get("author_name").toString());
                item.setUserId(Long.parseLong(doc.get("user_id").toString()));
                item.setUserName(doc.get("user_name").toString());
                item.setType(doc.get("type").toString());
                item.setTypeId(Long.parseLong(doc.get("type_id").toString()));
                item.setStatus(Boolean.parseBoolean(doc.get("status").toString()));
                if (doc.get("annotation") != null)
                    item.setAnnotation(doc.get("annotation").toString());
                if (doc.get("type_name") != null)
                    item.setTypeName(doc.get("type_name").toString());
                item.setName(doc.get("name").toString());

                if (item.getName().equals("date")) {
                    String message = doc.get("message").toString();
                    String[] parts = message.split("-");

                    String[] start = parts[0].split("\\.");
                    String date = FormatDate.dt(Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2]));

                    String[] end = parts[1].split("\\.");
                    String date2 = FormatDate.dt(Integer.parseInt(end[0]), Integer.parseInt(end[1]), Integer.parseInt(end[2]));

                    item.setMessage(FormatDate.fulldt(date, date2));
                } else {
                    item.setMessage(doc.get("message").toString());
                }

                list.add(item);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Recommend findById(String id) {
        try {
            DBObject doc = collection.findOne(new BasicDBObject("_id", new ObjectId(id)));
            Recommend item = new Recommend();
            item.setId(doc.get("_id").toString());
            item.setAuthorId(Long.parseLong(doc.get("author_id").toString()));
            item.setAuthorName(doc.get("author_name").toString());
            item.setUserId(Long.parseLong(doc.get("user_id").toString()));
            item.setUserName(doc.get("user_name").toString());
            item.setType(doc.get("type").toString());
            item.setTypeId(Long.parseLong(doc.get("type_id").toString()));
            item.setStatus(Boolean.parseBoolean(doc.get("status").toString()));
            item.setName(doc.get("name").toString());
            item.setMessage(doc.get("message").toString());
            return item;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void delete(String id) {
        try {
            collection.remove(new BasicDBObject("_id", new ObjectId(id)));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void deleteById(String id, AppUser user) {
        try {
            collection.remove(new BasicDBObject("_id", new ObjectId(id)).append("author_id", user.getUserId()));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void updateStatus(String id, AppUser user) {
        try {
            collection.update(new BasicDBObject("_id", new ObjectId(id)).append("author_id", user.getUserId()),
                    new BasicDBObject("$set", new BasicDBObject("status", Boolean.TRUE)), false, true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
