package life.majiang.community.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {


    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String s = JSON.toJSONString(accessTokenDTO);
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            System.out.println(string + "-------------------------------------");
            System.out.println("accessToken = " + string);
           /*  System.out.println(string);
            return string;*/
           String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/user")
                    .header("Authorization","token " +accessToken)
                    .build();
       // access_token="+accessToken)
            try {
                Response response = client.newCall(request).execute();
                String string = response.body().string();
                GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
                System.out.println("name = " + githubUser.getName());
                System.out.println("id = " + githubUser.getId());
                return githubUser;
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
    }
}
