package org.phototimemachine.web.controller;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.phototimemachine.common.Verify;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Role;
import org.phototimemachine.searcher.VerifySocial;
import org.phototimemachine.service.RoleService;
import org.phototimemachine.service.UserService;
import org.phototimemachine.service.validator.email.CustomEmailSender;
import org.phototimemachine.service.validator.email.EmailCorrectValid;
import org.phototimemachine.service.validator.password.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kmuhov
 * Date: 19.03.13
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping("/social")
@Controller
public class SocialController {

    private static final Logger logger = LoggerFactory.getLogger(SocialController.class);

    private static final String fbScope = "email,offline_access,user_about_me,user_birthday,read_friendlists";
    private static final String fbId = "435252733195550";
    private static final String fbSecret = "e951d70095c56a35cef0db1f17697daa";
    private static final String fbHost = "http://www.phototimemachine.org/social/fb/connect";
    private static final String fbOAuth = "https://www.facebook.com/dialog/oauth";
    private static final String fbAccessToken = "https://graph.facebook.com/oauth/access_token";

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CustomEmailSender customEmailSender;

    private int status = -1;

    @RequestMapping(value = "/fb", params="signup", method = RequestMethod.GET)
    public void fbinit(HttpServletRequest request, HttpServletResponse response) {
        try {
            status = 0;
            response.sendRedirect(fbOAuth + "?client_id=" + fbId + "&redirect_uri=" + fbHost +
                    "&scope=" + fbScope);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @RequestMapping(value = "/fb", params="signin", method = RequestMethod.GET)
    public void fbsignin(HttpServletResponse response) {
        try {
            status = 1;
            response.sendRedirect(fbOAuth + "?client_id=" + fbId + "&redirect_uri=" + fbHost +
                    "&scope=" + fbScope);
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    @RequestMapping(value = "/fb", params="add", method = RequestMethod.GET)
    public void fbaddacc(HttpServletResponse response) {
        try {
            status = 2;
            response.sendRedirect(fbOAuth + "?client_id=" + fbId + "&redirect_uri=" + fbHost +
                    "&scope=" + fbScope);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @RequestMapping(value = "/fb/connect", params = "code", method = RequestMethod.GET)
    public String fbconnect(@RequestParam("code") String code,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        try {
            String url = fbAccessToken
                    + "?client_id=" + fbId
                    + "&redirect_uri=" + fbHost
                    + "&client_secret=" + fbSecret
                    + "&code=" + code;
            HttpGet get = new HttpGet(url);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(get);

            String ref = EntityUtils.toString(resp.getEntity());

            String token = "access_token=";
            String expires = "&expires";

            int startIndex = ref.indexOf(token);
            int endIndex = ref.indexOf(expires);

            String accessToken = "";
            if (startIndex != -1 && endIndex != -1) {
                accessToken = ref.substring(startIndex + token.length(), endIndex);
            }

            String dataUrl = "https://graph.facebook.com/me?access_token=" + accessToken;
            get = new HttpGet(dataUrl);
            resp = client.execute(get);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(EntityUtils.toString(resp.getEntity()));
            String email = json.get("email").toString();

            AppUser existUser = userService.findByEmail(email);
            if (status == 0) {
                if (existUser == null) {
                    AppUser user = new AppUser();
                    user.setEmail(email);
                    user.setAbout(json.get("bio").toString());
                    user.setFirstName(json.get("first_name").toString());
                    user.setLastName(json.get("last_name").toString());
                    user.setGender(json.get("gender").toString().equalsIgnoreCase("male") ? Boolean.FALSE : Boolean.TRUE);
                    user.setFblink(json.get("link").toString());

                    String birthday = json.get("birthday").toString();
                    if (birthday != null && !birthday.isEmpty()) {
                        String[] date = birthday.split("/");
                        user.setDay(Integer.valueOf(date[1]));
                        user.setMonth(Integer.valueOf(date[0]));
                        user.setYear(Integer.valueOf(date[2]));
                    }

                    String hometown = ((JSONObject)json.get("hometown")).get("name").toString();
                    if (hometown != null && hometown.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(hometown).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            user.setHomeTown(hometown);
                            user.sethLatitude(result.getGeometry().getLocation().getLat().toString());
                            user.sethLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    String current = ((JSONObject)json.get("location")).get("name").toString();
                    if (current != null && current.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(current).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            user.setCurrentCity(current);
                            user.setcLatitude(result.getGeometry().getLocation().getLat().toString());
                            user.setcLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    PasswordGenerator pg = new PasswordGenerator();
                    String newPassword = pg.generatePassword(8);
                    user.setPassword(newPassword);

                    Role role = roleService.findById("ROLE_USER");
                    user.setRole(role);

                    userService.save(user);
                    authenticateUser(user);
                    return "redirect:/user/profile";
                } else {
                    if (existUser.getAbout().isEmpty() && json.get("bio") != null) existUser.setAbout(json.get("bio").toString());

                    if (existUser.getGender() == null && json.get("gender") != null) {
                        String gender = json.get("gender").toString();
                        existUser.setGender(gender.equalsIgnoreCase("male") ? Boolean.FALSE : Boolean.TRUE);
                    }

                    if (json.get("link") != null)
                        existUser.setFblink(json.get("link").toString());

                    String birthday = json.get("birthday") != null ? json.get("birthday").toString() : null;
                    if (birthday != null && !birthday.isEmpty()) {
                        String[] date = birthday.split("/");

                        if (existUser.getDay() == null) existUser.setDay(Integer.valueOf(date[1]));
                        if (existUser.getMonth() == null) existUser.setMonth(Integer.valueOf(date[0]));
                        if (existUser.getYear() == null) existUser.setYear(Integer.valueOf(date[2]));
                    }

                    String hometown = json.get("hometown") != null ? ((JSONObject)json.get("hometown")).get("name").toString() : null;
                    if ((existUser.getHomeTown() == null || existUser.getHomeTown().length() <= 0) &&
                            hometown != null && hometown.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(hometown).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            existUser.setHomeTown(hometown);
                            existUser.sethLatitude(result.getGeometry().getLocation().getLat().toString());
                            existUser.sethLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    String current = json.get("location") != null ? ((JSONObject)json.get("location")).get("name").toString() : null;
                    if ((existUser.getHomeTown() == null || existUser.getHomeTown().length() <= 0) &&
                            current != null && current.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(current).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            existUser.setCurrentCity(current);
                            existUser.setcLatitude(result.getGeometry().getLocation().getLat().toString());
                            existUser.setcLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    userService.save(existUser);
                    authenticateUser(existUser);
                    return "redirect:/";
                }
            } else if (status == 1) {
                AppUser fbuser = userService.findByFBLink(json.get("link").toString());
                if (existUser == null && fbuser == null) {
                    AppUser user = new AppUser();
                    user.setEmail(email);
                    user.setAbout(json.get("bio").toString());
                    user.setFirstName(json.get("first_name").toString());
                    user.setLastName(json.get("last_name").toString());
                    user.setGender(json.get("gender").toString().equalsIgnoreCase("male") ? Boolean.FALSE : Boolean.TRUE);
                    user.setFblink(json.get("link").toString());

                    String birthday = json.get("birthday").toString();
                    if (birthday != null && !birthday.isEmpty()) {
                        String[] date = birthday.split("/");
                        user.setDay(Integer.valueOf(date[1]));
                        user.setMonth(Integer.valueOf(date[0]));
                        user.setYear(Integer.valueOf(date[2]));
                    }

                    String hometown = ((JSONObject)json.get("hometown")).get("name").toString();
                    if (hometown != null && hometown.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(hometown).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            user.setHomeTown(hometown);
                            user.sethLatitude(result.getGeometry().getLocation().getLat().toString());
                            user.sethLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    String current = ((JSONObject)json.get("location")).get("name").toString();
                    if (current != null && current.trim().length() > 0) {
                        Geocoder geocoder = new Geocoder();
                        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(current).getGeocoderRequest();
                        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

                        if (geocoderResponse.getResults().size() > 0) {
                            GeocoderResult result = geocoderResponse.getResults().get(0);
                            user.setCurrentCity(current);
                            user.setcLatitude(result.getGeometry().getLocation().getLat().toString());
                            user.setcLongitude(result.getGeometry().getLocation().getLng().toString());
                        }
                    }

                    PasswordGenerator pg = new PasswordGenerator();
                    String newPassword = pg.generatePassword(8);
                    user.setPassword(newPassword);

                    Role role = roleService.findById("ROLE_USER");
                    user.setRole(role);

                    userService.save(user);
                    authenticateUser(user);
                    return "redirect:/user/profile";
                } else if  (existUser != null && fbuser == null) {
                    Verify verify = new Verify();
                    verify.setEmail(email);
                    verify.setConfirm(Boolean.FALSE);
                    verify.setUid(json.get("link").toString());
                    verify.setSocial("fb");

                    PasswordGenerator pg = new PasswordGenerator("first", "last", email, existUser.getPassword());
                    verify.setPassword(pg.getPassword());

                    VerifySocial social = new VerifySocial();
                    social.create(verify);

                    customEmailSender.simpleSend(email, "Go to link for confirm your account http://www.phototimemachine.org/social/confirm?uid="
                            + verify.getId() + "&code=" + verify.getPassword());

                    return "send.email";
                } else {
                    authenticateUser(existUser);
                    return "redirect:/";
                }
            } else if (status == 2) {
                AppUser user = userService.findAuthUser();
                user.setFblink(json.get("link").toString());
                userService.save(user);
                return "redirect:/user/profile";
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        return "redirect:/";
    }

    @RequestMapping(value = "/callback", params = "error_reason", method = RequestMethod.GET)
    public String fbError(@RequestParam("error_reason") String errorReason,
                                      @RequestParam("error") String error,
                                      @RequestParam("error_description") String description,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        try {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, description);
            System.out.println(errorReason);
            System.out.println(error);
            System.out.println(description);
        } catch (Exception e) { e.printStackTrace(); }
        return "redirect:/users/register";
    }


    private static final String vkId = "3505676";
    private static final String vkSecret = "XxK3F7yx6pb3tZ5KWRUS";

    @RequestMapping(value = "/vk", params = "signup", method = RequestMethod.GET)
    private void vkinit(HttpServletResponse response) {
        try {
            status = 0;
            response.sendRedirect("http://oauth.vk.com/authorize?client_id="+vkId
                    +"&redirect_uri=http://www.phototimemachine.org/social/vk/connect&response_type=code");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @RequestMapping(value = "/vk", params="signin", method = RequestMethod.GET)
    private void vksignin(HttpServletResponse response) {
        try {
            status = 1;
            response.sendRedirect("http://oauth.vk.com/authorize?client_id="+vkId
                    +"&redirect_uri=http://www.phototimemachine.org/social/vk/connect&response_type=code");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @RequestMapping(value = "/vk", params = "add", method = RequestMethod.GET)
    private void vkaddacc(HttpServletResponse response) {
        try {
            status = 2;
            response.sendRedirect("http://oauth.vk.com/authorize?client_id="+vkId
                    +"&redirect_uri=http://www.phototimemachine.org/social/vk/connect&response_type=code");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private String firstName;
    private String lastName;
    private String uid;
    private String[] date = new String[] {"", "", ""};

    @RequestMapping(value = "/vk/connect", params = "code", method = RequestMethod.GET)
    private String vkconnect(@RequestParam("code") String code, Model uiModel) {
        try {
            String url = "https://oauth.vk.com/access_token?client_id="+vkId
                    +"&client_secret="+vkSecret
                    +"&code="+code+"&redirect_uri=http://www.phototimemachine.org/social/vk/connect";
            HttpGet get = new HttpGet(url);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(get);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(EntityUtils.toString(resp.getEntity()));

            url = "https://api.vk.com/method/users.get?uids="+ json.get("user_id")
                    +"&fields=uid,first_name,last_name,bdate" +
                    "&access_token=" + json.get("access_token");
            get = new HttpGet(url);
            resp = client.execute(get);

            json = (JSONObject) parser.parse(EntityUtils.toString(resp.getEntity()));
            JSONObject data = (JSONObject) ((JSONArray) json.get("response")).get(0);
            uid = data.get("uid").toString();
            AppUser appUser = userService.findByVKLink("http://vk.com/" + uid);

            if (status == 0) {
                if (appUser == null) {
                    firstName = data.get("first_name").toString();
                    lastName = data.get("last_name").toString();

                    String bday = data.get("bdate").toString();
                    if (bday != null && bday.trim().length() > 0) {
                        date = bday.split("\\.");
                    }
                    uiModel.addAttribute("username", firstName + " " + lastName);
                    return "vk.email";
                } else {
                    authenticateUser(appUser);
                    return "redirect:/";
                }
            } else if (status == 1) {
                if (appUser == null) {
                    firstName = data.get("first_name").toString();
                    lastName = data.get("last_name").toString();

                    String bday = data.get("bdate").toString();
                    if (bday != null && bday.trim().length() > 0) {
                        date = bday.split("\\.");
                    }
                    uiModel.addAttribute("username", firstName + " " + lastName);
                    return "vk.email";
                } else {
                    authenticateUser(appUser);
                    return "redirect:/";
                }
            } else if (status == 2) {
                AppUser user = userService.findAuthUser();
                user.setVklink("http://vk.com/" + uid);
                userService.save(user);
                return "redirect:/user/profile";
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return "redirect:/";
    }

    @RequestMapping(value = "/vk/connect/email", method = RequestMethod.POST)
    private String vkemail(HttpServletRequest request) {
        String email = request.getParameter("vkemail");
        EmailCorrectValid valid = new EmailCorrectValid();

        if (email != null && email.trim().length() > 0 && valid.validate(email)) {
            AppUser userByEmail = userService.findByEmail(email);
            if (userByEmail != null) {
                AppUser user = userService.findByVKLink("http://vk.com/"+uid);
                if (user != null) {
                    authenticateUser(user);
                    return "redirect:/user/profile";
                } else {
                    Verify verify = new Verify();
                    verify.setEmail(email);
                    verify.setConfirm(Boolean.FALSE);
                    verify.setUid("http://vk.com/" + uid);
                    verify.setSocial("vk");

                    PasswordGenerator pg = new PasswordGenerator("first", "last", email, userByEmail.getPassword());
                    verify.setPassword(pg.getPassword());

                    VerifySocial social = new VerifySocial();
                    social.create(verify);

                    customEmailSender.simpleSend(email, "Go to link for confirm your account http://www.phototimemachine.org/social/confirm?uid="
                            + verify.getId() + "&code=" + verify.getPassword());

                    return "send.email";
                }
            } else {
                AppUser user = new AppUser();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setVklink("http://vk.com/"+uid);
                if (date[0].length() > 0) user.setDay(Integer.valueOf(date[0]));
                if (date[1].length() > 0) user.setMonth(Integer.valueOf(date[1]));
                if (date[2].length() > 0) user.setYear(Integer.valueOf(date[2]));

                PasswordGenerator pg = new PasswordGenerator();
                String newPassword = pg.generatePassword(8);
                user.setPassword(newPassword);

                Role role = roleService.findById("ROLE_USER");
                user.setRole(role);

                userService.save(user);
                authenticateUser(user);
            }
        }

        return "redirect:/user/profile";
    }

    @RequestMapping(value = "/confirm", params = {"uid", "code"}, method = RequestMethod.GET)
    public String confirmSocial(@RequestParam("uid") String uid, @RequestParam("code") String code) {
        VerifySocial vs = new VerifySocial();
        Verify verify = vs.find(uid, code);
        vs.update(uid);

        AppUser user = userService.findAuthUser();
        if (user != null) {
            if (verify.getSocial().equals("vk")) user.setVklink(verify.getUid());
            else user.setFblink(verify.getUid());
            userService.save(user);
            vs.update(uid);
            return "redirect:/user/profile";
        }

        user = userService.findByEmail(verify.getEmail());
        if (user != null) {
            if (verify.getSocial().equals("vk")) user.setVklink(verify.getUid());
            else user.setFblink(verify.getUid());
            userService.save(user);
            authenticateUser(user);
            vs.update(uid);
            return "redirect:/user/profile";
        }

        return "redirect:/";
    }


    private void authenticateUser(AppUser user) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        GrantedAuthority granted = new SimpleGrantedAuthority(user.getRole().getRoleId());
        auths.add(granted);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    user.fullName(),
                    user.getPassword(),
                    auths
                )
        );
    }
}