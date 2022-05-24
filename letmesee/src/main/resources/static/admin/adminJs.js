function addTextsShow(){
    var a = document.getElementById("big")
    var sum = "<div class=\"addTextDiv\">\n" +

        "<div class=\"page-header\">\n" +
        "  <h1>Add Texts</h1>\n" +
        "</div>"+

        "        <div class=\"btn-group\" style=\"margin: auto\" role=\"group\" aria-label=\"...\">\n" +
        "            <button type=\"button\" class=\"btn btn-default\">Add</button>\n" +
        "            <button type=\"button\" class=\"btn btn-default\">Reset</button>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\" id=\"title\">Title</span>\n" +
        "            <input type=\"text\" class=\"form-control\" placeholder=\"Username\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\" id=\"djs\">DataJson</span>\n" +
        "            <input type=\"text\" class=\"form-control\" placeholder=\"Username\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\" id=\"content\">Content</span>\n" +
        "            <textarea style=\"width: 500px;height: 600px\"></textarea>\n" +
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
        "            <button type=\"button\" class=\"btn btn-default\">Add</button>\n" +
        "            <button type=\"button\" class=\"btn btn-default\">Reset</button>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\" id=\"url\">URL</span>\n" +
        "            <input type=\"text\" class=\"form-control\" placeholder=\"URL\" aria-describedby=\"sizing-addon1\">\n" +
        "        </div>\n" +
        "        <div class=\"input-group input-group-lg\">\n" +
        "            <span class=\"input-group-addon\" id=\"content\">Content</span>\n" +
        "            <input type=\"text\" class=\"form-control\" placeholder=\"Content\" aria-describedby=\"sizing-addon1\">\n" +
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