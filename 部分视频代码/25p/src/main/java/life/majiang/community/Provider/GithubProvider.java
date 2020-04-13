package life.majiang.community.Provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUsr;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;


/*
OKHTTPClient 用来发送post请求，携带github授权获得的code去获取AccessToken
 */
@Component  // 这个注解是干嘛的？
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO  accessTokenDTO){

        MediaType mediaType= MediaType.get("application/json; charset=utf-8");

        // 这里用到了OKHTTP，需要去OKHTTP官网查看相关代码，导入依赖
        // 需要用alt enter导入类
        OkHttpClient client = new OkHttpClient();
        // 这里需要用到fastJason，通过依赖导入
        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO),mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try(Response response = client.newCall(request).execute()){
            //查看string的内容，即为access token
            String string = response.body().string();
            System.out.println(string);
            // 分离授权返回得到的字符串，从而获得accesstoken
            String access_token = string.split("&")[0].split("=")[1];
            System.out.println(access_token);
            return access_token;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 通过OKHttp向github发送请求 获得用户信息
     * @param access_token
     * @return
     */
    public GithubUsr getUUser(String access_token){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+access_token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUsr githubUsr = JSON.parseObject(string, GithubUsr.class);
            return githubUsr;
        } catch(IOException E){
            E.printStackTrace();
        }
        return  null;
    }


}
