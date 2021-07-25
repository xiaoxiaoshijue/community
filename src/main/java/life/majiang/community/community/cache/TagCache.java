package life.majiang.community.community.cache;

import life.majiang.community.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache {
    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO tagDTO1 = new TagDTO();
        tagDTO1.setCategoryName("开发语言");
        tagDTO1.setTags(Arrays.asList("js","php","css","html","java","python"));
        TagDTO tagDTO2 = new TagDTO();
        tagDTO2.setCategoryName("平台架构");
        tagDTO2.setTags(Arrays.asList("spring","yii","koa","express"));
        tagDTOS.add(tagDTO1);
        tagDTOS.add(tagDTO2);
        return tagDTOS;
    }
    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOList = get();
        List<String> tagList = tagDTOList.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining());
        return invalid;
    }
}
