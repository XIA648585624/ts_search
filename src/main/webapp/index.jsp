<%@page isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; UTf-8" %>
<%
    String path = request.getContextPath();
    pageContext.setAttribute("path", path);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>作业3主页</title>
    <!--引入bootstrap的css样式-->
    <link rel="stylesheet" href="${path}/static/css/bootstrap.min.css">
    <!--引入jqgrid的css-->
    <link rel="stylesheet" href="${path}/static/css/ui.jqgrid-bootstrap.css">
    <!--引入jQuery-->
    <script src="${path}/static/js/jquery-3.4.1.min.js"></script>
    <!--引入jqgrid-->
    <script src="${path}/static/js/jquery.jqGrid.min.js"></script>
    <!--引入jqgird国际化-->
    <script src="${path}/static/js/grid.locale-cn.js"></script>
    <!--引入bootstrap的js-->
    <script src="${path}/static/js/bootstrap.min.js"></script>

    <script>
        $(function () {
            //表格初始化
            $('#userlist').jqGrid({
                url: "${path}/poem/findForPage",
                /*width:800,*/
                height: 300,
                autowidth: true,//自适应外部容器的宽度
                styleUI: "Bootstrap",//设置为bootstrap的风格
                datatype: "json",//数据类型为json
                mtype: "post",//设置请求方式，默认为get
                colNames: ["ID", "诗词名", "作家", "类型", "来源", "内容", "作者简介", "标签"],//列名称为数组
                colModel: [
                    {name: "id", search: false},
                    {name: "name", editable: true, search: true},
                    {name: "author", editable: true, search: true},
                    {name: "type", editable: true, search: true},
                    {name: "origin", editable: true, search: true},
                    {
                        name: "content", editable: true, search: true,
                    },
                    {name: "authordes", editable: true, search: true,},
                    {
                        name: "categoryid",
                        editable: true,
                        search: true,
                        formatter: function (value, options, rowObject) {
                            return rowObject.categoryname;
                        },
                        edittype: 'select',
                        editoptions: {dataUrl: "${path}/poem/findAllCatepory",}
                    },

                    /*{//配置操作列对象
                        formatter:function (value, options, rowObject) {
                            /!* console.log(value);
                             console.log(options);
                             console.log(rowObject);*!/
                            var id = rowObject.id;
                            return `<button class="btn btn-success" onclick="editRow('`+id+`');">修改</button>&nbsp;&nbsp;&nbsp;<button class="btn btn-danger" onclick="delRow('`+id+`');">删除</button>`;
                        },
                        search: false,
                        width:200,
                    },*/
                ],//列数组配置列对象
                pager: "#pager",//设置分页工具栏
                //注意：1，一旦设置分页工具栏之后再根据指定的url查询时自动向后台传递page(当前页默认为1),rows(当前每页显示的信息条数),
                rowNum: 10,//本页显示记录数
                rowList: [5, 10, 20],//生成可以指定显示每页展示多少条的下拉列表
                viewrecords: true,//是否显示总的信息条数
                caption: "唐诗宋词列表列表",//表格的标题
                cellEdit: true,//开启表格单元的编辑功能,配合colModel的列对象的editable属性来使用,同时要设置editurl才能编辑
                editurl: "${path}/poem/alter",//开启编辑时执行编辑操作的url路径  添加  修改  删除
                toolbar: [true, 'top'],
                /*gridComplete:function () {//给表格加入一个完成事件
                    $("#t_userlist").empty().append("<button class='btn btn-success' onclick='saveRow();'>添加员工</button>")
                }*/


            }).navGrid('#pager',//开启分页工具栏
                {add: true, edit: true, del: true, search: true, refresh: true},//开启编辑操作
                {closeAfterEdit: true, height: 600, width: 600, editCaption: "编辑诗词信息", reloadAfterSubmit: true},//配置对象:编辑面板
                {closeAfterAdd: true, height: 600, width: 600, addCaption: "添加诗词信息", reloadAfterSubmit: true},//配置对象:添加面板
                {},//删除的配置
                {
                    sopt: ['cn']//配置搜索条件
                },
            );


        })

        //添加
        function saveRow() {
            $("#userlist").jqGrid('editGridRow', "new", {
                height: 500,
                reloadAfterSubmit: true,
                closeAfterAdd: true,//添加后关闭窗口
            });
        }

        //修改
        function editRow(id) {
            $("#userlist").jqGrid('editGridRow', id, {
                height: 500,
                reloadAfterSubmit: true,
                closeAfterEdit: true,//修改后关闭窗口
            });

        }

        // 删除
        function delRow(id) {
            if (window.confirm("确定要删除此员工吗？")) {
                $.post("${path}/emp/alter", {id: id, oper: 'del'}, function (data) {
                    console.log(data);
                    $('#userlist').trigger('reloadGrid');//刷新表格页面
                })
            }
        }

        //发起建立索引的请求
        $(function () {
            $('#saveEs').click(function () {
                $.ajax({
                    url: '${path}/poem/saveEs',
                    method: 'post',
                    data: {},
                    datatype: 'json',
                    success: function (data) {
                        if (data != null) {
                            alert("重建成功！");
                        }
                    }
                })
            })
            $('#delEs').click(function () {

                $.ajax({
                    url: '${path}/poem/delEs',
                    method: 'post',
                    data: {},
                    datatype: 'json',
                    success: function (data) {
                        if (data != null) {
                            alert("所有索引均已删除！");
                        }
                    }
                })
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
                            var hot = $("<button class=\"btn btn-danger\" type=\"button\" style='margin-left: 5px' >\n" +
                                "                        " + data[i].value + " <span class=\"badge\" style=\"color: red\">" + data[i].score + "</span>\n" +
                                "                    </button>")
                            $('#hotSearchContent').append(hot);
                        } else {
                            var hot = $("<button class=\"btn btn-primary\" type=\"button\" style='margin-left: 5px'>\n" +
                                "                        " + data[i].value + " <span class=\"badge\">" + data[i].score + "</span>\n" +
                                "                    </button>")
                            $('#hotSearchContent').append(hot);
                        }

                    }
                }
            })
            /*热词管理*/
            //添加热词
            $('#subSearchString').click(function () {
                var searchString = $('#searchString').val();
                $.post('http://47.97.208.42:8080/dic/dic/add', {searchString: searchString}, function (data) {
                    $('#searchString').val("");
                    //添加成功！
                    var tag = $("<div class=\"col-sm-3\">\n" +
                        "            <div class=\"alert alert-info alert-dismissible\" role=\"alert\">\n" +
                        "                <button type=\"button\" class=\"close\" data-dismiss=\"alert\" name='" + searchString + "' aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\n" +
                        "                " + searchString + "\n" +
                        "            </div>\n" +
                        "        </div>");
                    $('#con').append(tag);
                });
            })
            //删除热词
            $('#con').on("click", ".close", function () {
                console.log($(this).attr('name'));
                var searchString = $(this).attr('name');
                $.get('http://47.97.208.42:8080/dic/dic/del', {searchString: searchString}, function (data) {
                    alert("删除热词成功！");
                });

            });
            //查询词典中所有热词
            $.ajax({
                url: 'http://47.97.208.42:8080/dic/dic/findAll',
                mehtod: 'get',//jsonp解决跨域问题只能以get请求方式访问远端数据
                data: {},
                dataType: 'json',//跨域访问将远端的数据形成js的函数形式调用
                success: function (data) {
                    console.log(data);
                    for (var i = 0; i < data.length; i++) {
                        if ("" != data[i]) {
                            var tag = $("<div class=\"col-sm-3\">\n" +
                                "            <div class=\"alert alert-info alert-dismissible\" role=\"alert\">\n" +
                                "                <button type=\"button\" class=\"close\" data-dismiss=\"alert\" name='" + data[i] + "' aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>\n" +
                                "                " + data[i] + "\n" +
                                "            </div>\n" +
                                "        </div>");
                            $('#con').append(tag);
                        }

                    }
                }
            })

        })
    </script>

</head>
<body>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <!---->
        <div class="navbar-header">
            <a class="navbar-brand" href="#">唐诗后台管理系统V1.0</a>
        </div>

        <!--导航内容-->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav navbar-left" id="nav">
                <li class="li-ite">
                    <button class="btn btn-danger" id="delEs" type="button">清空所有es文档</button>
                </li>
                <li class="li-ite ">
                    <button class="btn btn-primary" id="saveEs" type="button">基于基础数据量重建ES索引库</button>
                </li>
            </ul>

        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav><!--导航栏结束-->

<!--网页content-->
<div class="container-fluid" id="contenter">
    <div class="row">
        <!--创建表格-->
        <table id="userlist"></table>

        <!--分页工具栏-->
        <div id="pager"></div>
    </div>


    <div class="row" style="margin-top: 30px">
        <%--热搜榜--%>
        <div class="col-sm-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">全网热搜榜</h3>
                </div>
                <div class="panel-body" id="hotSearchContent">
                    <%--热搜榜内容--%>
                </div>
            </div>
        </div>
        <%--词典添加--%>
        <div class="col-sm-6">
            <!--词典管理-->
            <div class="col-sm-12">
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="searchString" placeholder="请输入要添加的热词"/>
                </div>
                <div class="col-sm-2">
                    <button type="button" id="subSearchString" class="btn btn-primary">添加远程词典</button>
                </div>
                </form>
            </div>
            <!--添加的热词显示-->
            <div class="col-sm-12" id="con" style="margin-top: 20px;">

            </div>
        </div>
    </div>
</div>
</body>
</html>
