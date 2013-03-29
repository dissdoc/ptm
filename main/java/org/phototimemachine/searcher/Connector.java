package org.phototimemachine.searcher;

import com.mongodb.*;
import org.phototimemachine.common.PhotoJson;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.domain.Tag;
import org.phototimemachine.web.util.tag.CustomTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Connector {

    private DB db;
    private DBCollection collection;
    private DBCursor cursor;

    public Connector() {
        try {
            Mongo mongo = new Mongo("localhost", 27017);
            db = mongo.getDB("ptm");
            collection = db.getCollection("search");
            cursor = null;
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void find(Boolean sortBy, String page, Long type_id, boolean owner, Long owner_id,
                     String searchValue, String searchField, String neLng, String neLat,
                     String swLng, String swLat, String minD, String maxD) {
        try {
            BasicDBObject _Q = new BasicDBObject();
            List<BasicDBObject> array = new ArrayList<BasicDBObject>();

            if (searchValue != null &&
                    searchField != null &&
                    searchValue.trim().length() > 0 &&
                    searchField.trim().length() > 0) {
                if (searchValue.equals("1")) {
                    String[] elements = searchField.replaceAll(",", " ").split("\\s+");
                    String q = "";
                    for (String item : elements) q += "(?=.*?\\b(?:" + item + ")\\b)";
                    Pattern Q = Pattern.compile(q.substring(0, q.length()), Pattern.CASE_INSENSITIVE);

                    _Q.append("$or", Arrays.asList(
                            new BasicDBObject("owner.name", Q),
                            new BasicDBObject("name", Q),
                            new BasicDBObject("description", Q),
                            new BasicDBObject("author", Q),
                            new BasicDBObject("address", Q),
                            new BasicDBObject("tags", new BasicDBObject("$all", Arrays.asList(elements)))));
                } else if (searchValue.equals("2")) {
                    List<String> tags = CustomTag.getTagList(searchField);
                    _Q.append("tags", new BasicDBObject("$all",tags));
                }
            }

            if (neLng != null && neLng.trim().length() > 0 &&
                neLat != null && neLat.trim().length() > 0 &&
                swLng != null && swLng.trim().length() > 0 &&
                swLat != null && swLat.trim().length() > 0) {
                Double nelng = new Double(neLng);
                Double nelat = new Double(neLat);
                Double swlng = new Double(swLng);
                Double swlat = new Double(swLat);

                _Q.append("address", new BasicDBObject("$exists", true).append("$ne", ""));
                _Q.append("loc",
                      new BasicDBObject("$within",
                              new BasicDBObject("$box", Arrays.asList(
                                      Arrays.asList(swlng, swlat),
                                      Arrays.asList(nelng, nelat)
                              ))));
            }

            if (minD != null && maxD != null && minD.trim().length() > 0 && maxD.trim().length() > 0) {
                Long mind = new Long(minD);
                Long maxd = new Long(maxD);

                array.add(new BasicDBObject("year", new BasicDBObject("$gte", mind)));
                array.add(new BasicDBObject("year", new BasicDBObject("$lte", maxd)));
                array.add(new BasicDBObject("year2", new BasicDBObject("$gte", mind)));
                array.add(new BasicDBObject("year2", new BasicDBObject("$lte", maxd)));
            }

            if (page != null && page.equals("main")) {
                int id = owner_id != null ? owner_id.intValue() : 0;

                array.add(
                    new BasicDBObject("$or", Arrays.asList(
                        new BasicDBObject("owner.id", id),
                        new BasicDBObject("privacy", 0)))
                );
            } else if (page != null && page.equals("fave")) {
                if (owner_id != null && owner_id.intValue() > 0) {
                    List<Long> arr = new ArrayList<Long>();
                    arr.add(owner_id);
                    _Q.append("fave", new BasicDBObject("$in", arr));
                }
            } else if (page != null && page.equals("assortment")) {
                List<Long> arr = new ArrayList<Long>();
                arr.add(type_id);
                _Q.append("assortment", new BasicDBObject("$in", arr));
            } else if (page != null && page.equals("album")) {
                List<Long> arr = new ArrayList<Long>();
                arr.add(type_id);
                if (!owner) {
                    _Q.append("album", new BasicDBObject("$in", arr)).append("privacy", 0);
                } else {
                    _Q.append("album", new BasicDBObject("$in", arr));
                }
            } else if (page != null && page.equals("group")) {
                System.out.println(owner + " => " + type_id);
                List<Long> arr = new ArrayList<Long>();
                arr.add(type_id);
                if (!owner) {
                    _Q.append("group", new BasicDBObject("$in", arr)).append("privacy", 0);
                } else {
                    _Q.append("group", new BasicDBObject("$in", arr));
                }
            } else {
                if (owner_id != null && owner_id.intValue() > 0) {
                    _Q.append("owner.id", owner_id);
                }

                if (!owner) {
                    _Q.append("privacy", 0);
                }
            }

            _Q.append("$and", array);

            BasicDBObject sorted = new BasicDBObject();
            if (sortBy == null || sortBy == Boolean.FALSE) {
                sorted.append("created", new Integer(-1));
            } else {
                sorted.append("year", new Integer(-1)).append("year2", new Integer(-1));
            }

            cursor = collection.find(_Q).sort(sorted);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public List<PhotoJson> translateToJson() {
        try {
            List<PhotoJson> photos = new ArrayList<PhotoJson>();
            while(cursor.hasNext()) {
                DBObject item = cursor.next();
                PhotoJson json = new PhotoJson();
                json.setId((Long) item.get("_id"));
                json.setAuthor((String) item.get("author"));
                json.setName((String) item.get("name"));
                json.setAddress((String) item.get("address"));

                if (item.get("loc") != null) {
                    List<Double> loc = (List<Double>) item.get("loc");
                    json.setLongitude(loc.get(0).toString());
                    json.setLatitude(loc.get(1).toString());
                }

                DBObject owner = (DBObject) item.get("owner");
                json.setUserId((Long) owner.get("id"));
                json.setUser((String) owner.get("name"));
                json.setFullDate((String)item.get("startDate"), (String)item.get("endDate"));
                photos.add(json);
            }
            return photos;
        } catch (Exception ex) { ex.printStackTrace(); return null; }
    }

    public void updatePhoto(Long id, String name, Object value) {
        try {
            BasicDBObject doc = new BasicDBObject().append("_id", id);

            BasicDBObject up = new BasicDBObject().append("$set",
                    new BasicDBObject().append(name, value));

            collection.update(doc, up, false, true);
        } catch (Exception ex) { }
    }

    public void removeField(Long id, String name) {
        try {
            collection.update(new BasicDBObject("_id", id), new BasicDBObject("$unset", new BasicDBObject(name, 1)));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void addToModel(String model, Long id, Long value) {
        try {
            BasicDBObject doc = new BasicDBObject("_id", id);
            BasicDBObject up = new BasicDBObject("$addToSet", new BasicDBObject(model, value));
            collection.update(doc, up, false, true);
        } catch (Exception ex) { }
    }

    public void removeFromModel(String model, Long id, Long value) {
        try {
            BasicDBObject doc = new BasicDBObject("_id", id);
            BasicDBObject up = new BasicDBObject("$pull", new BasicDBObject(model, value));
            collection.update(doc, up, false, true);
        } catch (Exception ex) { }
    }

    public void updateArrayPhoto(Long id, String name, String value) {
        try {
            BasicDBObject doc = new BasicDBObject().append("_id", id);
            BasicDBObject up = new BasicDBObject().append("$addToSet",
                    new BasicDBObject().append(name, value));
            collection.update(doc, up, false, true);
        } catch (Exception ex) { }
    }

    public void dropFromArrayPhoto(Long id, String name, String value) {
        try {
            BasicDBObject doc = new BasicDBObject().append("_id", id);
            BasicDBObject up = new BasicDBObject().append("$pop",
                    new BasicDBObject().append(name, value));
            collection.update(doc, up, false, true);
        } catch (Exception ex) { }
    }

    public void remove(Photo photo) {
        try {
            BasicDBObject doc = new BasicDBObject("_id", photo.getId());
            collection.remove(doc);
        } catch (Exception ex) { }
    }

    public void create(Photo photo) {
        try {
            BasicDBObject doc = new BasicDBObject();
            doc.put("_id", photo.getId());
            doc.put("name", photo.getName());
            doc.put("author", photo.getAuthor());
            doc.put("description", photo.getDescription());
            doc.put("address", photo.getAddress());
            doc.put("privacy", photo.getPrivacy());

            if (photo.getLatitude() != null && photo.getLatitude().length() > 0)
                doc.put("loc", Arrays.<Float>asList(new Float(photo.getLongitude()), new Float(photo.getLatitude())));

            doc.put("year", photo.getYear1());
            doc.put("startDate", photo.startDate());
            doc.put("year2", photo.getYear2());
            doc.put("endDate", photo.endDate());
            doc.put("created", new Date());

            BasicDBObject owner = new BasicDBObject();
            owner.put("id", photo.getAppUser().getUserId());
            owner.put("name", photo.getAppUser().fullName());
            doc.put("owner", owner);

            ArrayList<String> tags = new ArrayList<String>();
            for (Tag tag: photo.getTags())
                tags.add(tag.getName());
            doc.put("tags", tags);

            collection.insert(doc);
        } catch (Exception ex) { }
    }
}
