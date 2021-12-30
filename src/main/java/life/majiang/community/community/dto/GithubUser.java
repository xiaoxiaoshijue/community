package life.majiang.community.community.dto;

import lombok.Data;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
@Data
public class GithubUser {
    private String name;
    private Integer id;
    private String bio;
    private String avatarUrl;
}
