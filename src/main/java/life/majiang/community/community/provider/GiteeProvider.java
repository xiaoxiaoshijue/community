package life.majiang.community.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.GiteeAccessTokenDTO;
import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GiteeUser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GiteeProvider {
    @Value("${gitee.client.id}")
    private String giteeClientId;
    @Value("${gitee.client.secret}")
    private String giteeClientSecret;
    @Value("${gitee.redirect.uri}")
    private String giteeRedirectUri;
    public GiteeAccessTokenDTO getAccessToken(String code, String state){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(giteeRedirectUri);
        accessTokenDTO.setClient_id(giteeClientId);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(giteeClientSecret);
        accessTokenDTO.setGrant_type("authorization_code");
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        //1 . 拿到OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        //3 . 构建Request,将FormBody作为Post方法的参数传入
        Request request = new Request.Builder()
                .url("https://gitee.com/oauth/token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String accessToken_jsonString = response.body().string();
            GiteeAccessTokenDTO giteeAccessTokenDTO =  JSON.parseObject(accessToken_jsonString,GiteeAccessTokenDTO.class);
            return giteeAccessTokenDTO;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public GiteeUser getUser(String token){
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://gitee.com/api/v5/user?access_token=" + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                GiteeUser giteeUser = JSON.parseObject(s, GiteeUser.class);
                return giteeUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    public void login(GiteeUser giteeUser, GiteeAccessTokenDTO giteeAccessTokenDTO, String token) {

    }
}
