package life.majiang.community.community.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import life.majiang.community.community.exception.ErrorCodeEnum;
import life.majiang.community.community.exception.CustomizeException;
import life.majiang.community.community.model.Users;

import java.util.Calendar;
import java.util.Date;

public class JwtUtil {
    /**
     签发对象：这个用户的id
     签发时间：现在
     有效时间：30分钟
     载荷内容：暂时设计为：这个人的名字，这个人的昵称
     加密密钥：这个人的id加上一串字符串
     */
    public static String createToken(Users users) {

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE,120);
        Date expiresDate = nowTime.getTime();

        return JWT.create().withAudience(users.getUserName())   //签发对象
                .withIssuedAt(new Date())    //发行时间
                .withExpiresAt(expiresDate)  //有效时间
                .withClaim("userId", users.getUserId())    //载荷，随便写几个都可以
                .withClaim("avatarUrl", users.getAvatarUrl())
                .withClaim("createTime", users.getCreateTime())
                .withClaim("email", users.getEmail())
                .withClaim("mobile", users.getMobile())
                .withClaim("realName", users.getRealName())
                .withClaim("userInfo", users.getUserInfo())
                .withClaim("token", users.getToken())
                .withClaim("sex", users.getSex())           //载荷，随便写几个都可以
                .sign(Algorithm.HMAC256(users.getUserName()+"xiaoxushequ"));   //加密
    }

    /**
     * 检验合法性，其中secret参数就应该传入的是用户的id
     * @param token
     */
    public static void verifyToken(String token, String secret){
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret+"xiaoxushequ")).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            //效验失败
            //这里抛出的异常是我自定义的一个异常，你也可以写成别的
            throw new CustomizeException(ErrorCodeEnum.ILLEGAL_TOKEN);
        }
    }

    /**
     * 获取签发对象
     */
    public static String getAudience(String token){
        return JWT.decode(token).getAudience().get(0);
    }

    public static Users getUsersByJWT(String token, String secret) {
        Users users = new Users();
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret + "xiaoxushequ")).build().verify(token);
            users.setUserId(decodedJWT.getClaim("userId").asLong());
            users.setAvatarUrl(decodedJWT.getClaim("avatarUrl").asString());
            users.setCreateTime(decodedJWT.getClaim("createTime").asLong());
            users.setEmail(decodedJWT.getClaim("email").asString());
            users.setMobile(decodedJWT.getClaim("mobile").asString());
            users.setRealName(decodedJWT.getClaim("realName").asString());
            users.setUserInfo(decodedJWT.getClaim("userInfo").asString());
            users.setToken(decodedJWT.getClaim("token").asString());
            users.setSex(decodedJWT.getClaim("sex").asString());
            users.setUserName(decodedJWT.getAudience().get(0));
            return users;
        } catch (JWTDecodeException j) {
            //这里是token解析失败
            throw new CustomizeException(ErrorCodeEnum.TOKEN_ANALYSIS_FAILED);
        } catch (TokenExpiredException e){
            throw new CustomizeException(ErrorCodeEnum.TOKEN_EXPIRE);
        }
    }


    /**
     * 通过载荷名字获取载荷的值
     */
    public static Claim getClaimByName(String token, String name){
        return JWT.decode(token).getClaim(name);
    }

}
