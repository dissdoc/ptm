package org.phototimemachine.web.controller;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import org.phototimemachine.common.Recommend;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.domain.Tag;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.searcher.RecommendGenerator;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.TagService;
import org.phototimemachine.service.UserService;
import org.phototimemachine.web.util.FormatDate;
import org.phototimemachine.web.util.Numerical;
import org.phototimemachine.web.util.tag.CustomTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kmuhov
 * Date: 22.03.13
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping("/recommend")
@Controller
public class RecommendController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/author", method = RequestMethod.POST)
    public @ResponseBody void author(@RequestParam("photo_id") Long photo_id,
                                     @RequestParam("author") String author,
                                     @RequestParam("annotation") String annotation) {
        if (author == null || author.trim().length() <= 0) return;
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(photo_id);
        new RecommendGenerator().create(photo, user, "author", author, annotation);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/date", method = RequestMethod.POST)
    public @ResponseBody void period(@RequestParam("photo_id") Long photo_id,
                         @RequestParam("year") String year,
                         @RequestParam("month") String month,
                         @RequestParam("day") String day,
                         @RequestParam("year2") String year2,
                         @RequestParam("month2") String month2,
                         @RequestParam("day2") String day2,
                         @RequestParam("annotation") String annotation) {
        if ((year == null || year.trim().length() <= 0) &&
            (month == null || month.trim().length() <= 0) &&
            (day == null || day.trim().length() <= 0) &&
            (year2 == null || year2.trim().length() <= 0) &&
            (month2 == null || month2.trim().length() <= 0) &&
            (day2 == null || day2.trim().length() <= 0)) return;
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(photo_id);
        String format = String.format("%s.%s.%s-%s.%s.%s",
                Numerical.getInt(day),
                Numerical.getInt(month),
                Numerical.getInt(year),
                Numerical.getInt(day2),
                Numerical.getInt(month2),
                Numerical.getInt(year2));
        new RecommendGenerator().create(photo, user, "date", format, annotation);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/place", method = RequestMethod.POST)
    public @ResponseBody void place(@RequestParam("photo_id") Long photo_id,
                                    @RequestParam("place") String place,
                                    @RequestParam("annotation") String annotation) {
        if (place == null || place.trim().length() <= 0) return;
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(photo_id);
        new RecommendGenerator().create(photo, user, "place", place, annotation);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    public @ResponseBody void tags(@RequestParam("photo_id") Long photo_id,
                                   @RequestParam("tags") String tags) {
        if (tags == null || tags.trim().length() <= 0) return;
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(photo_id);
        RecommendGenerator rg = new RecommendGenerator();

        List<String> tagsList = CustomTag.getTagList(tags);
        for (String tag: tagsList)
            rg.create(photo, user, "tag", tag, "");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/status", method = RequestMethod.POST)
    public @ResponseBody void status(@RequestParam("id") String id) {
        AppUser user = userService.findAuthUser();
        new RecommendGenerator().updateStatus(id, user);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public @ResponseBody void accept(@RequestParam("id") String id) {
        RecommendGenerator rg = new RecommendGenerator();
        Recommend recommend = rg.findById(id);
        Photo photo = photoService.findById(recommend.getTypeId());
        if (!userService.isOwner(photo.getAppUser())) return;

        if (recommend.getName().equals("author")) {
            photo.setAuthor(recommend.getMessage());
            new Connector().updatePhoto(photo.getId(), "author", recommend.getMessage());
        } else if (recommend.getName().equals("about")) {
            photo.setDescription(recommend.getMessage());
            new Connector().updatePhoto(photo.getId(), "description", recommend.getMessage());
        } else if (recommend.getName().equals("date")) {
            String[] parts = recommend.getMessage().split("-");
            String[] start = parts[0].split("\\.");
            String[] end = parts[1].split("\\.");

            photo.setYear1(Numerical.getInt(start[2]));
            photo.setYear2(Numerical.getInt(end[2]));
            photo.setMonth1(Numerical.getInt(start[1]));
            photo.setMonth2(Numerical.getInt(end[1]));
            photo.setDay1(Numerical.getInt(start[0]));
            photo.setDay2(Numerical.getInt(end[0]));

            Connector con = new Connector();
            con.updatePhoto(photo.getId(), "year", photo.getYear1());
            con.updatePhoto(photo.getId(), "year2", photo.getYear2());
            con.updatePhoto(photo.getId(), "startDate", photo.startDate());
            con.updatePhoto(photo.getId(), "endDate", photo.endDate());
        } else if (recommend.getName().equals("tag")) {
            Tag tag = tagService.findOrCreate(recommend.getMessage());
            photo.getTags().add(tag);
            new Connector().updateArrayPhoto(photo.getId(), "tags", recommend.getMessage());
        } else if (recommend.getName().equals("place")) {
            Geocoder geocoder = new Geocoder();
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(recommend.getMessage()).getGeocoderRequest();
            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
            if (geocoderResponse.getResults().size() > 0) {
                GeocoderResult result = geocoderResponse.getResults().get(0);
                String lat = result.getGeometry().getLocation().getLat().toString();
                String lng = result.getGeometry().getLocation().getLng().toString();
                photo.setAddress(recommend.getMessage());
                photo.setLatitude(lat);
                photo.setLongitude(lng);
                Connector con = new Connector();
                con.updatePhoto(photo.getId(), "address", photo.getAddress());
                con.updatePhoto(photo.getId(), "latitude", new Float(lat));
                con.updatePhoto(photo.getId(), "longitude", new Float(lng));
            }
        }
        rg.delete(id);
        photoService.save(photo);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public @ResponseBody void reject(@RequestParam("id") String id) {
        AppUser user = userService.findAuthUser();
        RecommendGenerator rg = new RecommendGenerator();
        Recommend recommend = rg.findById(id);
        if (recommend == null) return;
        if (user.getUserId().intValue() != recommend.getAuthorId().intValue()) return;
        rg.delete(id);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/", method= RequestMethod.POST)
    public String recommends(Model uiModel) {
        AppUser user = userService.findAuthUser();
        RecommendGenerator rg = new RecommendGenerator();
        uiModel.addAttribute("recommends", rg.recommendsLimit(user));
        return "recommend.list";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/archive", method = RequestMethod.GET)
    public String archive(Model uiModel) {
        AppUser user = userService.findAuthUser();
        RecommendGenerator rg = new RecommendGenerator();
        uiModel.addAttribute("recommends", rg.recommends(user));
        return "recommend.archive";
    }
}
