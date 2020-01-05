<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; UTf-8" %>
<%
    String path = request.getContextPath();
    pageContext.setAttribute("path", path);
%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>查询诗词主页</title>
</head>
<!--引入bootstrap的css样式-->
<link rel="stylesheet" href="${path}/static/css/bootstrap.min.css">
<!--引入jQuery-->
<script src="${path}/static/js/jquery-3.4.1.min.js"></script>
<!--引入bootstrap的js-->
<script src="${path}/static/js/bootstrap.min.js"></script>

<script>
    $(function () {
        $('#submit').click(function () {
            $('#esForm').submit();
        })
        /*热词排行榜*/
        $.ajax({
            url: '${path}/poem/findAllHotSearchString',
            method: 'post',
            data: {},
            dataType: 'json',
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].score >= 20) {//红色热搜词
                        var hot = $("<p style=\"color: red\" >\n" +
                            "                        " + data[i].value + " <span class=\"badge\" style=\"color: red\">" + data[i].score + "</span>\n" +
                            "                    </p>")
                        $('#hotSearchContent').append(hot);
                    } else {
                        var hot = $("<p style='color: cornflowerblue;'>\n" +
                            "                        " + data[i].value + " <span class=\"badge\">" + data[i].score + "</span>\n" +
                            "                    </p>")
                        $('#hotSearchContent').append(hot);
                    }

                }
            }
        })

    })
</script>
<body>
<div class="row">
    <div class="col-sm-8 col-sm-offset-2" style="text-align: center"><h1>唐诗-宋词检索系统</h1></div>
    <div class="col-sm-offset-2 col-sm-7">
        <form class="form-horizontal" id="esForm" method="post" action="${path}/es/search">
            <div class="form-group">
                <label class="sr-only" for="exampleInputAmount">请输入关键词</label>
                <div class="input-group">
                    <div class="input-group-addon">检索唐诗宋词</div>
                    <input type="text" name="searchString" class="form-control" id="exampleInputAmount"
                           value="${searchString}" placeholder="请输入关键词">
                    <input type="hidden" name="pageNum" value="1"/>
                    <input type="hidden" name="pageSize" value="10"/>
                </div>
            </div>

        </form>
    </div>
    <div class="col-sm-2" style="margin-left: 0;">
        <button type="submit" id="submit" class="btn btn-primary">检索</button>
    </div>
</div>
<div class="row">
    <div class="col-sm-2">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title" style="text-align: center">全网热搜榜</h3>
            </div>
            <div class="panel-body" style="text-align: center" id="hotSearchContent">
                <%--热搜榜内容--%>

            </div>
        </div>
    </div>
    <div class=" col-sm-10" id="contentHtml">
        <p>
            <c:if test="${tPoems!=null}">
                <c:if test="${pageNum==1}">首页</c:if>
                <c:if test="${pageNum==1}">上一页</c:if>
                <c:if test="${pageNum!=1}"><a
                        href="${path}/es/search?pageNum=1&pageSize=${pageSize}&searchString=${searchString}">首页</a></c:if>
                <c:if test="${pageNum!=1}"><a
                        href="${path}/es/search?pageNum=${pageNum-1}&pageSize=${pageSize}&searchString=${searchString}">上一页</a></c:if>
                共${count}条记录，${pageNum}/${pages}页
                <c:if test="${pageNum!=pages}"><a
                        href="${path}/es/search?pageNum=${pageNum+1}&pageSize=${pageSize}&searchString=${searchString}">下一页</a></c:if>
                <c:if test="${pageNum!=pages}"><a
                        href="${path}/es/search?pageNum=${pages}&pageSize=${pageSize}&searchString=${searchString}">尾页</a></c:if>

            </c:if>
        </p>
        <c:forEach items="${tPoems}" var="p">
            <ul>
                <li style="color: cornflowerblue"><a target="_blank" href="${p.href}">${p.name}</a></li>
                <li>${p.author}.${p.type}</li>
                <li>${p.content}</li>
                <li>${p.authordes}</li>
            </ul>
        </c:forEach>

    </div>
</div>

</body>
</html>