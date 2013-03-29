package org.phototimemachine.web.controller;

import org.phototimemachine.common.PhotoJson;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/search")
@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/photos", method = RequestMethod.POST)
    public @ResponseBody List<PhotoJson> photos(@RequestParam(value = "search_value", required = false) String searchValue,
                                                @RequestParam(value = "search_field", required = false) String searchField,
                                                @RequestParam(value = "ne_lng", required = false) String neLng,
                                                @RequestParam(value = "ne_lat", required = false) String neLat,
                                                @RequestParam(value = "sw_lng", required = false) String swLng,
                                                @RequestParam(value = "sw_lat", required = false) String swLat,
                                                @RequestParam(value = "minD", required = false) String minD,
                                                @RequestParam(value = "maxD", required = false) String maxD,
                                                @RequestParam(value = "sortby", required = false) Boolean sortby,
                                                @RequestParam(value = "owner_id", required = false) Long owner_id,
                                                @RequestParam(value = "page", required = false) String page,
                                                @RequestParam(value = "type_id", required = false) Long type_id) {
        Connector con = new Connector();
        AppUser user = userService.findAuthUser();
        boolean owner = user != null && owner_id != null && user.getUserId().intValue() == owner_id.intValue() ? true : false;
        con.find(sortby, page, type_id, owner, owner_id, searchValue, searchField, neLng, neLat, swLng, swLat, minD, maxD);
        return con.translateToJson();
    }
}
