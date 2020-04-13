package life.majiang.community.controller;

import life.majiang.community.Provider.GithubProvider;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUsr;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 *用来产生实例对象 调用githubprovider
 */

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientID;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired(required = false)
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientID);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUsr githubUsr = githubProvider.getUUser(accessToken);
        System.out.println(githubUsr);
/**
 * user 为数据库上创建的用户（从github上获取放入的）
 */

        if(githubUsr!=null && githubUsr.getId()!=null){
            // 创建用户实例
            User user = new User();
            // token 为用户唯一的标识码，存入cookie中，当浏览器重新刷新时，
            // 通过cookie携带的token，判断该用户是否为数据库中存在的用户，从而达到不需要重复登录的目的。
            String token = UUID.randomUUID().toString();
            user.setAccount_id(String.valueOf(githubUsr.getId()));
            user.setName(githubUsr.getName());
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(user.getGmt_create());
            user.setToken(token);
            user.setAvatar_url(githubUsr.getAvatar_url());
            System.out.println(userMapper==null);
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }
}
