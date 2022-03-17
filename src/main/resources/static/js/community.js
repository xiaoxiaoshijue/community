/**
 * 获取cookies
 */
function getCookie(cookieName){
    var cookies = document.cookie;
    var cookiesArray = cookies.split(";");
    for(var i = 0; i < cookiesArray.length; i++){
        var arr = cookiesArray[i].split("=");
        if(cookieName == arr[0]){
            return arr[1];
        }
    }
    return "";
}

/**
 * 提交回复
 */
/*回复问题*/
function post(){
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId,1,content);/*如果是回复问题，则传入问题id作为parentid*/
}

/**
 * 进行二级评论
 * @param e
 */
function comment(e){
    var commentId = e.getAttribute("data-id"); /*如果是进行二级评论 则传入对应的一级评论id作为parentid */
    var content = $("#input-"+commentId).val();
    comment2target(commentId,2,content);
}
function addLikeCount(e){
    var commentId = e.getAttribute("data-id");
    var token = getCookie("token");

    $.ajax({
        type:"POST",
        url:"/addCommentLikeCount",
        headers: {'token': token},
        contentType: 'application/json',
        data:JSON.stringify({
            "commentId":commentId
        }),
        success:function (response){
            if(response == 1){
                window.location.reload();
            }else {
                window.alert("登陆后才能点赞，请登陆后重试");
            }
        },
        dataType: "json"
    });
}
function comment2target(targetId,type,content){
    /*var questionId = $("#question_id").val();
    var content = $("#comment_content").val();*/
    if(!content){
        alert("回复消息不能为空!!!~");
        return
    }
    $.ajax({
        type:"POST",
        url:"/comment",
        contentType:'application/json',
        data: JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success:function (response){
            if(response.code == 200){
                console.log(this.data);
                window.location.reload();
                /*
                                $("#comment_section").hide();
                */
            }else {
                if(response.code == 2003){
                    var isAccept = confirm(response.message);
                    if(isAccept){
                       // window.open("https://github.com/login/oauth/authorize?client_id=68a1f60191a5e24b04e4&redirect_uri=http://39.106.54.82:8887/callback&scope=user&state=1")
                        window.localStorage.setItem("closable",true);
                    }
                }else {
                    alert(response.message);
                }
            }
            console.log(response);
        },
        dataType:"json"
    });
    console.log(targetId);
    console.log(content);
}
/** * 展开二级评论
 */
function collapseComments(this1,e){
    /*疑问  ？    mapper按照的是parentid 搜索问题 而视频中这里传入的是 问题的id*/
    var id = e;
/*
    var comments = $("#comment-"+id);
*/
    var subCommentContainer = $("#comment-" + id);
    //展开二级评论
    subCommentContainer.toggleClass("in");
    //this1.classList.toggle("active");
    //标记二级评论展开状态
    //e.setAttribute("data-collage");
    if(subCommentContainer.children().length != 1){
        /*comments.toggleClass("in");
        e.classList.toggle("active");*/
    }else {
        $.getJSON(
            "/comment/" + id,
            function (data){
            console.log(data);
            $.each(data.data.reverse(), function (index,comment) {
                var mediaLeftElement = $("<div/>", {
                    "class": "media-left"
                }).append($("<img/>", {
                    "class": "media-object img-rounded",
                    "src": comment.user.avatarUrl
                }));

                var mediaBodyElement = $("<div/>", {
                    "class": "media-body"
                }).append($("<h5/>", {
                    "class": "media-heading",
                    "html": comment.user.userName
                })).append($("<div/>", {
                    "html": comment.content
                })).append($("<div/>", {
                    "class": "menu"
                }).append($("<span/>", {
                    "class": "pull-right",
                    "html": moment(comment.gmtCreate).format('YYYY-MM-DD HH:mm')
                })));

                var mediaElement = $("<div/>", {
                    "class": "media"
                }).append(mediaLeftElement).append(mediaBodyElement);

                var commentElement = $("<div/>", {
                    "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                }).append(mediaElement);

                subCommentContainer.prepend(commentElement);
            });
        });
    }
}
function selectTag(that){
    /*var previous = $("#tag").val();
    if(previous.indexOf(value) === -1){
        if(previous){
            $('#tag').val(previous + "," + value);
        }else {
            $('#tag').val(value);
        }
    }*/
    var value = that.getAttribute("data-tag");
    var previous = $("#tag").val();
    var tags = previous.split(',');
    if(!previous){//如果标签没内容直接添加t
        $('#tag').val(value);
    }else {//如果标签有内容且不重复 拼接字符串后添加
        if(jQuery.inArray(value,tags) === -1){
            $('#tag').val(previous + "," + value);
        }
    }
}
function showSelectTag(){
    $("#select-tag").show();
}
