BASEURL = "http://localhost:8888/"


function addTextsShow(){
    var a = document.getElementById("big")
    var sum = "<div class=\"addTextDiv\">\n" +

        "<div class=\"page-header\">\n" +
        "  <h1>Add Texts</h1>\n" +
        "</div>"+

        "        <div class=\"btn-group\" style=\"margin: auto\" role=\"group\" aria-label=\"...\">\n" +
        "            <button type=\"button\" class=\"btn btn-default\" onclick='addText()'>Add</button>\n" +
        "            <button type=\"button\" class=\"btn btn-default\">Reset</button>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\">Title</span>\n" +
        "            <input type=\"text\" class=\"form-control\" placeholder=\"Username\" aria-describedby=\"sizing-addon1\" id=\"title\">\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\">DataJson</span>\n" +
        "            <input type=\"text\" class=\"form-control\" id=\"djs\" placeholder=\"Username\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\">Content</span>\n" +
        "            <textarea style=\"width: 500px;height: 600px\" id=\"content\"></textarea>\n" +
        "        </div>\n" +
        "\n" +
        "    </div>"

    a.innerHTML = sum
}


function addPicsShow(){
    var a = document.getElementById("big")
    var sum = "<div class=\"addPicDiv\">\n" +

        "<div class=\"page-header\">\n" +
        "  <h1>Add Pics</h1>\n" +
        "</div>"+
        "        <div class=\"btn-group\" style=\"margin: auto\" role=\"group\" aria-label=\"...\">\n" +
        "            <button type=\"button\" class=\"btn btn-default\" onclick='addPic()'>Add</button>\n" +
        "            <button type=\"button\" class=\"btn btn-default\">Reset</button>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\">URL</span>\n" +
        "            <input type=\"text\" class=\"form-control\" id=\"url\" placeholder=\"URL\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\">Content</span>\n" +
        "            <input type=\"text\" class=\"form-control\" id=\"content\" placeholder=\"Content\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "    </div>"
    a.innerHTML = sum
}


function syStatusDivShow(){
    var a = document.getElementById("big");
    var sum = "<div class=\"panel panel-info\" style=\"width: 50%;margin: auto\">\n" +
        "<div class=\"page-header\">\n" +
        "  <h1>Status</h1>\n" +
        "</div>"+
        "        <div class=\"panel-heading\">可用的CPU核心数量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">可用的总内存容量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">已使用的内存容量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">剩余的内存容量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前倒排索引链数量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前正排索引链数量</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前文本总数</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前图片总数</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前redis连接状态</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "        <div class=\"panel-heading\">当前数据库连接状态</div>\n" +
        "        <div class=\"panel-body\">\n" +
        "            Panel content\n" +
        "        </div>\n" +
        "    </div>"

    a.innerHTML = sum
}


function addText(){
    let title = document.getElementById("title").value
    let content = document.getElementById("content").value
    let dataJson = document.getElementById("djs").value
    $.ajax({
        url:BASEURL+"index/addIndex.go",
        type:"post",
        dataType:'json',
        data:{
            title:title,
            content:content,
            dataJson:dataJson
        },
        success:function(result) {
            if (result.code === 4000) {
                alert("添加成功")
            }else{
                alert("添加失败，请稍后再试")
            }
        },
        error:function (){
            alert("服务器繁忙")
        }
    })
}


function addPic(){
    let content = document.getElementById("content").value
    let url = document.getElementById("url").value
    $.ajax({
        url:BASEURL+"index/addPic.go",
        type:"post",
        dataType:'json',
        data:{
            url:url,
            content:content
        },
        success:function(result) {
            if (result.code === 4000) {
                alert("添加成功")
            }else{
                alert("添加失败，请稍后再试")
            }
        },
        error:function (){
            alert("服务器繁忙")
        }
    })
}

$(function (){
    $("#addTexts").click(function (){
        addTextsShow()
    })

    $("#addPics").click(function (){
        addPicsShow()
    })

    $("#status").click(function (){
        syStatusDivShow()
    })
})