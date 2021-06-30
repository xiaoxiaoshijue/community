package life.majiang.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer totalPage;  //总页数
    private Integer Page;        //当前页码
    private List<Integer> pages = new ArrayList<>(); //显示在页面上的页表的集合

    /**
     *
     * @param totalCount   总记录数
     * @param page         当前页数
     * @param size         每页的最大记录数
     */
    public void setPagination(Integer totalCount, Integer page, Integer size) {
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        Page = page;

        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if(page - i > 0){
                pages.add(0,page - i);
            }
            if(page + i <= totalPage){
                pages.add(page + i);
            }
        }

        //是否显示上一页
        if(page == 1){
            showPrevious = false;
        }else {
            showPrevious = true;
        }
        //是否显示下一页
        if(page == totalPage){
            showNext = false;
        }else {
            showNext = true;
        }
        //是否展示第一页
        if(pages.contains(1)){
            showFirstPage = false;
        }else {
            showFirstPage = true;
        }
        //是否显示最后一页
        if(pages.contains(totalPage)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }
}
