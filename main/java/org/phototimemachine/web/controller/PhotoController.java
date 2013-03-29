package org.phototimemachine.web.controller;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import org.phototimemachine.domain.*;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.*;
import org.phototimemachine.web.util.FileSystem;
import org.phototimemachine.web.util.Numerical;
import org.phototimemachine.web.util.image.CustomImageMagick;
import org.phototimemachine.web.util.tag.CustomTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RequestMapping("/photos")
@Controller
public class PhotoController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private FaveService faveService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AssortmentService assortmentService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadForm() {
        logger.info("Enter to upload page");
        return "photos/upload";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        AppUser appUser = userService.findAuthUser();
        if (appUser == null) return;

        Set<Tag> tags = new HashSet<Tag>();
        List<String> tagsList = CustomTag.getTagList(request.getParameter("tags"));
        for (String item: tagsList) {
            Tag tag = tagService.findOrCreate(item);
            tags.add(tag);
        }

        Photo photo = new Photo();
        photo.setName(request.getParameter("name"));
        photo.setAppUser(appUser);
        photo.setTags(tags);
        photo.setDay1(Numerical.getInt(request.getParameter("day")));
        photo.setMonth1(Numerical.getInt(request.getParameter("month")));
        photo.setYear1(Numerical.getInt(request.getParameter("year")));
        photo.setDay2(Numerical.getInt(request.getParameter("day2")));
        photo.setMonth2(Numerical.getInt(request.getParameter("month2")));
        photo.setYear2(Numerical.getInt(request.getParameter("year2")));
        photo.setLicense(Numerical.getByte(request.getParameter("license")));
        photo.setMarked(Numerical.getBool(request.getParameter("marked")));
        photo.setPrivacy(Numerical.getByte(request.getParameter("privacy")));

        photo.setAddress(request.getParameter("address"));
        photo.setLatitude(request.getParameter("latitude"));
        photo.setLongitude(request.getParameter("longitude"));

        if (photo.getAddress() != null && photo.getAddress().trim().length() > 0 &&
                (photo.getLatitude() == null || photo.getLatitude().trim().length() <= 0) &&
                (photo.getLongitude() == null || photo.getLongitude().trim().length() <= 0)) {
            Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(photo.getAddress()).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

            if (geocoderResponse.getResults().size() > 0) {
                GeocoderResult result = geocoderResponse.getResults().get(0);
                photo.setLatitude(result.getGeometry().getLocation().getLat().toString());
                photo.setLongitude(result.getGeometry().getLocation().getLng().toString());
            }
        }

        photo.setAuthor(request.getParameter("author"));
        photo.setDescription(request.getParameter("description"));
        photoService.save(photo);

        CustomImageMagick customImageMagick = new CustomImageMagick(file, photo.getId().toString());
        customImageMagick.setFormat();
        customImageMagick.setPath();
        customImageMagick.generate();

        new Connector().create(photo);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model uiModel,
                         @RequestParam(value = "type", required = false) String type,
                         @RequestParam(value = "type_id", required = false) Long type_id) {
        Photo photo = photoService.findById(id);

        int variable = photo.getViews() != null ? photo.getViews().intValue() : 0;
        photo.setViews(++variable);
        photoService.save(photo);
        AppUser appUser = userService.findAuthUser();

        if (appUser == null || appUser.getUserId().intValue() != photo.getAppUser().getUserId().intValue())
            if (photo.getPrivacy() != 0)
                return "redirect:/";

        List<Photo> thumbs = new ArrayList<Photo>();
        if (type == null || (type.equals("user") && type_id != null && type_id.intValue() > 0))  {
            if (appUser == null || appUser.getUserId().intValue() != photo.getAppUser().getUserId().intValue())
                thumbs = photoService.findByUserPublic(photo.getAppUser().getUserId());
            else
                thumbs = photoService.findByUser(appUser.getUserId());
        } else if (type != null && type.equals("album") && type_id != null && type_id.intValue() > 0) {
            if (appUser != null && appUser.getUserId().intValue() != photo.getAppUser().getUserId().intValue())
                thumbs = photoService.findByAlbumPublic(type_id);
            else
                thumbs = photoService.findByAlbum(type_id);
        } else if (type != null && type.equals("fave") && type_id != null && type_id.intValue() > 0) {
            thumbs = photoService.findByFave(type_id);
        } else if (type != null && type.equals("assortment") && type_id != null && type_id.intValue() > 0) {
            thumbs = photoService.findByCollection(type_id);
        } else if (type != null && type.equals("group") && type_id != null && type_id.intValue() > 0) {
            thumbs = photoService.findAgreeGroup(type_id);
        }

        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", userService.isOwner(photo.getAppUser()));
        if (appUser != null && photo.getPrivacy() == 0)
            uiModel.addAttribute("fave", faveService.findByUserAndPhoto(appUser.getUserId(), photo.getId()));
        uiModel.addAttribute("user", appUser);
        uiModel.addAttribute("comments", commentService.findByPhotoId(photo.getId()));
        uiModel.addAttribute("faves", faveService.findByPhoto(photo.getId()));
        uiModel.addAttribute("auth", appUser);
        uiModel.addAttribute("thumbs", thumbs);
        uiModel.addAttribute("tags", tagService.findByPhoto(photo.getId()));
        if (type != null) uiModel.addAttribute("type", type);
        if (type_id != null && type_id.intValue() > 0) uiModel.addAttribute("type_id", type_id);

        // for represented jspx page
        uiModel.addAttribute("groups", groupService.findAllByPhoto(photo.getId()));
        uiModel.addAttribute("albums", albumService.findAllByPhoto(photo.getId()));
        uiModel.addAttribute("assortments", assortmentService.findAllByPhoto(photo.getId()));
        return "photos/detail";
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody String deletePhoto(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(id);

        if (photo.getAppUser().getUserId().intValue() == user.getUserId().intValue()) {
            List<Album> albums = albumService.findAllByPhoto(photo.getId());
            for (Album album: albums) {
                if (album.getThumbnail() == null) continue;
                if (album.getThumbnail().getId().intValue() == photo.getId().intValue()) {
                    album.setThumbnail(null);
                    albumService.save(album);
                }
            }

            List<Assortment> assortments = assortmentService.findAllByPhoto(photo.getId());
            for (Assortment assortment: assortments) {
                if (assortment.getThumbnail() == null) continue;
                if (assortment.getThumbnail().getId().intValue() == photo.getId().intValue()) {
                    assortment.setThumbnail(null);
                    assortmentService.save(assortment);
                }
            }

            new Connector().remove(photo);
            FileSystem.removePhoto(photo.getId());
            photoService.delete(photo);
        }
        return "";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/modal", method = RequestMethod.POST)
    public String modalPhoto(@PathVariable("id") Long id,
                             @RequestParam(value = "params", required = false) String params,
                             Model uiModel) {
        Photo photo = photoService.findById(id);
        if (photo != null) {
            uiModel.addAttribute("photo", photo);
            uiModel.addAttribute("user", photo.getAppUser());
            uiModel.addAttribute("params", params);
        }
        return "photo.modal";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/{id}/update/marked", method = RequestMethod.POST)
    public @ResponseBody String updateMarked(@PathVariable("id") Long id, @RequestParam("marked") String marked) {
        Boolean result = Numerical.getBool(marked);
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return "";
        photo.setMarked(result);
        photoService.save(photo);
        return result == Boolean.TRUE ? "Safe" : "Unsafe";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/license", method = RequestMethod.POST)
    public @ResponseBody String updateLicense(@PathVariable("id") Long id, @RequestParam("license") String license) {
        Byte result = Numerical.getByte(license);
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return photo.nameLicense();
        photo.setLicense(result);
        photoService.save(photo);
        return photo.nameLicense();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/privacy", method = RequestMethod.POST)
    public @ResponseBody String updatePrivacy(@PathVariable("id") Long id, @RequestParam("privacy") Byte privacy) {
        Photo photo = photoService.findById(id);
        if (userService.isOwner(photo.getAppUser())) {
            photo.setPrivacy(privacy);
            photoService.save(photo);
            new Connector().updatePhoto(id, "privacy", privacy);
        }
        return photo.namePrivacy();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/author", method = RequestMethod.POST)
    public String updateAuthor(@PathVariable("id") Long id, @RequestParam("author") String author, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return null;
        photo.setAuthor(author);
        photoService.save(photo);

        new Connector().updatePhoto(photo.getId(), "author", author);

        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);
        return "photo.author";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/name", method = RequestMethod.POST)
    public @ResponseBody String updateName(@PathVariable("id") Long id, @RequestParam("name") String name, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser()))
            return null;
        if (name != null && name.trim().length() > 0) {
            photo.setName(name);
            photoService.save(photo);
        }
        new Connector().updatePhoto(photo.getId(), "name", name);
        return "{\"name\": \""+photo.getName()+"\"}";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/description", method = RequestMethod.POST)
    public String updateDescription(@PathVariable("id") Long id,
                  @RequestParam("description") String description, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return null;
        photo.setDescription(description);
        photoService.save(photo);
        new Connector().updatePhoto(photo.getId(), "description", description);
        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);
        return "photo.description";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/date", method = RequestMethod.POST)
    public String updateDate(@PathVariable("id") Long id, Model uiModel,
        @RequestParam("year") String year, @RequestParam("month") String month, @RequestParam("day") String day,
        @RequestParam("year2") String year2, @RequestParam("month2") String month2, @RequestParam("day2") String day2) {
        Photo photo = photoService.findById(id);

        if (!userService.isOwner(photo.getAppUser())) return null;

        photo.setYear1(Numerical.getInt(year));
        photo.setYear2(Numerical.getInt(year2));
        photo.setMonth1(Numerical.getInt(month));
        photo.setMonth2(Numerical.getInt(month2));
        photo.setDay1(Numerical.getInt(day));
        photo.setDay2(Numerical.getInt(day2));
        photoService.save(photo);

        Connector con = new Connector();
        con.updatePhoto(photo.getId(), "year", photo.getYear1());
        con.updatePhoto(photo.getId(), "year2", photo.getYear2());
        con.updatePhoto(photo.getId(), "startDate", photo.startDate());
        con.updatePhoto(photo.getId(), "endDate", photo.endDate());
        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);
        return "photo.time";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/delete/time", method = RequestMethod.POST)
    public String deleteStartTime(@PathVariable("id") Long id, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return null;
        photo.setDay1(0);
        photo.setMonth1(0);
        photo.setYear1(0);
        photo.setDay2(0);
        photo.setMonth2(0);
        photo.setYear2(0);
        photoService.save(photo);
        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);

        Connector connector = new Connector();
        connector.updatePhoto(id, "startDate", null);
        connector.updatePhoto(id, "endDate", null);
        return "photo.time";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/place", method = RequestMethod.POST)
    public String updatePlace(@PathVariable("id") Long id, @RequestParam("address") String address,
            @RequestParam("lat") String lat, @RequestParam("lng") String lng, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return photo.getAddress();

        if (address.trim().length() > 0) {
            photo.setAddress(address);
            photo.setLatitude(lat);
            photo.setLongitude(lng);
        } else {
            photo.setAddress(null);
            photo.setLatitude(null);
            photo.setLongitude(null);
        }
        photoService.save(photo);

        Connector con = new Connector();
        con.updatePhoto(photo.getId(), "address", address);
        if (photo.getLatitude() != null)
            con.updatePhoto(photo.getId(), "loc", Arrays.<Float>asList(new Float(lng), new Float(lat)));
        else
            con.removeField(photo.getId(), "loc");

        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);
        return "photo.place";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/update/tags", method = RequestMethod.POST)
    public String updateTags(@PathVariable("id") Long id,
                 @RequestParam("tags") String tags, Model uiModel) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return null;

        Connector con = new Connector();
        List<String> tagsList = CustomTag.getTagList(tags);
        for (String item: tagsList) {
            Tag tag = tagService.findOrCreate(item);
            photo.getTags().add(tag);
            con.updateArrayPhoto(photo.getId(), "tags", item);
        }
        photoService.save(photo);
        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("owner", true);
        return "photo.tags";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "{id}/delete/tag/{tag_id}", method = RequestMethod.POST)
    public void deleteTag(@PathVariable("id") Long id, @PathVariable("tag_id") Long tag_id) {
        Photo photo = photoService.findById(id);
        if (!userService.isOwner(photo.getAppUser())) return;

        Tag tag = null;
        for (Tag item: photo.getTags()) {
            if (item.getId() == tag_id) {
                tag = item;
                break;
            }
        }

        if (tag != null) {
            photo.getTags().remove(tag);
            new Connector().dropFromArrayPhoto(photo.getId(), "tags", tag.getName());
            photoService.save(photo);
        }
    }
}
