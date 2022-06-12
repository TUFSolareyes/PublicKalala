PAGECOUNT = 1
MAXPAGECOUNT = 1

TYPE = 1
SEARCHTEXT = ""
ISLOGIN = 0
WILLSTARTID = 0


BASEURL = "http://localhost:8888/"
BASEURL2 = "http://localhost:8082/"


let loginBodyStr = "<table style=\"border-spacing: 8px;border-collapse: separate\">\n" +
    "                        <tr>\n" +
    "                            <td>username:</td>\n" +
    "                            <td><input id=\"uname\"/></td>\n" +
    "                        </tr>\n" +
    "                        <tr>\n" +
    "                            <td>password:</td>\n" +
    "                            <td><input type=\"password\" id=\"pass\" /></td>\n" +
    "                        </tr>\n" +
    "                    </table>"


let starBodyStr = "<div class=\"starDiv\">\n" +
    "                        <div class=\"titleDiv\"></div>\n" +
    "                        <div class=\"summaryDiv\"></div>\n" +
    "                        <div><button>Del</button></div>\n" +
    "                    </div>"


let picStr = "<div class=\"row\" id=\"picDiv\">\n" +
    "                <div class=\"col-sm-6 col-md-4\">\n" +
    "                    <div class=\"thumbnail\">\n" +
    "                        <img src=\"...\" alt=\"...\">\n" +
    "                        <div class=\"caption\">\n" +
    "                            <h3>Thumbnail label</h3>\n" +
    "                            <p></p>\n" +
    "                        </div>\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "            </div>"



function save(key,value){
    if (typeof(Storage) !== "undefined") {
        localStorage.setItem(key,value)
    }
}


function searchStorage(key){
    return localStorage.getItem(key)
}


function init(){
    document.getElementById("webt").setAttribute("style","background-color:orange;pointer-events:none")
    if(!init1()){
        init2()
    }
}

function init1(){
    let loginOrRegBut = document.getElementById("loginSignUp")
    let myModalLabel = document.getElementById("myModalLabel")
    let modal_body = document.getElementById("modal_body")
    let reg = document.getElementById("reg")
    let ac = searchStorage("ac")
    let re = searchStorage("re")
    $.ajax({
        url:BASEURL+"loginJudge/isLogin",
        headers:{
            ac:ac
        },
        async:false,
        type:"get",
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                loginOrRegBut.innerText = "MyStars"
                myModalLabel.innerText = "Stars"
                if(ISLOGIN===0){
                    let login = document.getElementById("login")
                    login.innerText = "log out"
                    login.setAttribute("id","log_out")
                    reg.setAttribute("style","display:none")
                    ISLOGIN = 1
                }
            }else if(result.code===4007){
                if(ISLOGIN===1){
                    let login = document.getElementById("log_out")
                    login.innerText = "login"
                    login.setAttribute("id","login")
                    reg.setAttribute("style","")
                    ISLOGIN = 0
                }
            }
        },
        error:function (){
            ISLOGIN = 0
        }
    });
    if(isOk()){
        return true
    }else{
        return false
    }
}

function isOk(){
    if(ISLOGIN===1){
        return true
    }else{
        return false
    }
}


function init2(){
    let re = searchStorage("re")
    let loginOrRegBut = document.getElementById("loginSignUp")
    let myModalLabel = document.getElementById("myModalLabel")
    let modal_body = document.getElementById("modal_body")
    let reg = document.getElementById("reg")
    $.ajax({
        url:BASEURL+"loginJudge/isLogin",
        headers:{
            re:re
        },
        async:false,
        type:"get",
        dataType:'json',
        success:function(result,status,xhr){
            if(result.code===4000){
                var ac = xhr.getResponseHeader("ac")
                var re = xhr.getResponseHeader("re")
                save("ac",ac)
                save("re",re)
                loginOrRegBut.innerText = "MyStars"
                myModalLabel.innerText = "Stars"
                if(ISLOGIN===0){
                    let login = document.getElementById("login")
                    login.innerText = "log out"
                    login.setAttribute("id","log_out")
                    reg.setAttribute("style","display:none")
                    ISLOGIN = 1
                }
            }else if(result.code===4009){
                if(ISLOGIN===1){
                    let login = document.getElementById("log_out")
                    login.innerText = "login"
                    login.setAttribute("id","login")
                    reg.setAttribute("style","")
                    ISLOGIN = 0
                }
            }
        },
        error:function (){
        }
    });
    if(isOk()){
        return true
    }else{
        return false
    }
}

function addStarCollection(){
    let name = prompt("请输入收藏夹名称:");
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            return
        }
    }
    let ac = searchStorage("ac")
    $.ajax({
        url:BASEURL+"loginJudge/createStarCollection.go",
        data:{
            cname:name
        },
        headers:{
            ac:ac
        },
        type:"post",
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                getStarCollections()
                setTimeout(function (){
                    alert("添加成功")
                },200)
            }
        },
        error:function (){

        }
    });
}


function getStarCollections(){
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            return
        }
    }
    let ac = searchStorage("ac")
    $.ajax({
        url:BASEURL+"loginJudge/getStarCollections.do",
        type:"get",
        headers:{
            ac:ac
        },
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                let a = document.getElementById("modal_body")
                let jsonArr = result.data
                let sum = ""
                for(let i=0;i<jsonArr.length;i++){
                    sum += ("<button class=\"btn btn-primary\" id="+"\""+jsonArr._id+"\""+" type=\"button\" onclick='getStarsFromCollection(this)'>\n" +
                        jsonArr[i].cname+"\n" +
                        " </button><hr/>")
                }

                sum += "<button type=\"button\" class=\"btn btn-default\" id='addStarCollection' onclick='addStarCollection()'>+</button>"
                a.innerHTML = sum
            }
        },
        error:function (){

        }
    });

}



function getStarsFromCollection(t){
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            t.click()
            return
        }
    }

    $.ajax({
        url:BASEURL+"loginJudge/getStarCollections.do",
        type:"get",
        headers:{
            ac:ac
        },
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                let a = document.getElementById("modal-content")
                let jsonArr = result.data
                let sum = "<h3>Collections</h3><hr/>"
                for(let i=0;i<jsonArr.length;i++){
                    sum += ("<button class=\"btn btn-primary\" type=\"button\" id='"+jsonArr[i]._id+"' onclick='addStarToCollection(this,TEMP_T)'>\n" +
                        jsonArr[i].cname+"\n" +
                        " </button><hr/>")
                }
                a.innerHTML = sum
            }
        },
        error:function (){

        }
    });

}


TEMP_TID = 0
TEMP_CID = 0
TEMP_T = null

function getStarCollection2(t){
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            t.click()
            return
        }
    }
    TEMP_TID = t.id
    TEMP_T = t
    let ac = searchStorage("ac")
    $.ajax({
        url:BASEURL+"loginJudge/getStarCollections.do",
        type:"get",
        headers:{
            ac:ac
        },
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                let a = document.getElementById("modal-content")
                let jsonArr = result.data
                let sum = "<h3>Collections</h3><hr/>"
                for(let i=0;i<jsonArr.length;i++){
                    sum += ("<button class=\"btn btn-primary\" type=\"button\" id='"+jsonArr[i]._id+"' onclick='addStarToCollection(this,TEMP_T)'>\n" +
                        jsonArr[i].cname+"\n" +
                        " </button><hr/>")
                }
                a.innerHTML = sum
            }
        },
        error:function (){

        }
    });
}



function addStarToCollection(t,tbtn){
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            return
        }
    }
    let ac = searchStorage("ac")
    TEMP_CID = t.id
    $.ajax({
        url:BASEURL+"loginJudge/addStarToCollection.go",
        type:"post",
        headers:{
            ac:ac
        },
        data:{
            cid:TEMP_CID,
            tid:TEMP_TID+""
        },
        dataType:'json',
        success:function(result){
            if(result.code===4000){
                alert("添加成功")
            }
            tbtn.click();
        },
        error:function (){

        }
    });
    TEMP_TID = 0
    TEMP_CID = 0
}


function search(per,next){
    if(per==0&&next==0){
        PAGECOUNT = 1
    }
    var a = document.getElementById("sText")
    var b = document.getElementById("big")
    var c = document.getElementById("sTime")
    var d = document.getElementById("sum")
    var fi = document.getElementById("fi")
    var rs = document.getElementById("rs")

    b.innerHTML = ""
    var sTexta = a.value
    var fia = fi.value
    SEARCHTEXT = a.value

    //获取搜索结果
    if(TYPE===1){
        $.ajax({
            url:BASEURL+"index/search.do",
            data:{
                text: sTexta,
                fwords: fia,
                pageCountStr:(PAGECOUNT-per+next),
                limitCountStr:"10"
            },
            type:"get",
            dataType:'json',
            success:function(result){
                var jsonArr = result.data
                var sum = ""
                for(var i=0;i<jsonArr.length;i++){
                    let id = jsonArr[i]._id
                    console.log(id)
                    var willHtml = "<div class=\"textDiv\">\n" +
                        "            <div class=\"titleDiv\">\n" +
                        "                <a href=\""+BASEURL+"index/getText.do?textId="+jsonArr[i]._id+"\">"+jsonArr[i].t+"</a>\n" +
                        "            </div>\n" +
                        "            <div class=\"summaryDiv\">"+jsonArr[i].su+"..."+"</div>\n" +
                        "            <div><button id='"+jsonArr[i]._id+"' name='starButton' data-toggle='modal' data-target='#myStarCollections' onclick='getStarCollection2(this)'>❤</button></div>"+
                        "        </div>"
                    sum += willHtml
                }
                if(jsonArr.length===0){
                    sum += "<span style='margin: auto'>暂无搜索结果</span>"
                    b.innerHTML = sum
                    PAGECOUNT = 1
                    MAXPAGECOUNT = 1
                    return
                }
                sum += "<div class=\"pageCountDiv\">\n" +
                    "                <nav aria-label=\"...\">\n" +
                    "                    <ul class=\"pager\">\n" +
                    "                        <li><a onclick=\"per()\" style=\"user-select: none\">Previous</a></li>\n" +
                    "                        <li><a onclick=\"next()\" style=\"user-select: none\">Next</a></li>\n" +
                    "                    </ul>\n" +
                    "                </nav>\n" +
                    "                <span id=\"pageCount\"></span>\n" +
                    "            </div>"
                b.innerHTML = sum
                var str = result.msg+""
                var m = str.split(",")
                c.innerHTML = m[0]
                d.innerHTML = m[1]
                if(jsonArr.length>0){
                    MAXPAGECOUNT = m[2]
                }else{
                    MAXPAGECOUNT = 1
                }
                PAGECOUNT = PAGECOUNT-per+next
                document.getElementById("pageCount").innerHTML = "当前页码: " +PAGECOUNT
            },
            error:function (){
                alert("服务当前繁忙！")
            }
        });



        //获取相关搜索
        $.ajax({
            url:BASEURL+"index/getRelatedSearch.do",
            data:{
                text: sTexta
            },
            type:"get",
            dataType:'json',
            success:function(result){
                var jsonArr = result.data
                var sum = ""
                if(jsonArr.length>0){
                    sum = "<h3>相关搜索:</h3>\n"
                    for(var i=0;i<jsonArr.length;i++){
                        sum += "<button type=\"button\" class=\"btn btn-default\" onclick='searchByRe(this)' value='"+jsonArr[i]+"'>"+jsonArr[i]+"</button>"
                    }
                    rs.innerHTML = sum
                }else{
                    rs.innerHTML = ""
                }
            },
            error:function (){
                alert("服务当前繁忙！")
            }
        });
    }else if(TYPE===2){
        $.ajax({
            url:BASEURL+"index/searchPic.do",
            data:{
                text: sTexta,
                pageCountStr:(PAGECOUNT-per+next),
                limitCountStr:"9"
            },
            type:"get",
            dataType:'json',
            success:function(result){
               rs.innerHTML = ""
               var big = document.getElementById("big")
               var sum = ""
               var jsonArr = result.data
               if(jsonArr.length===0){
                   sum += "<span style='margin: auto'>暂无搜索结果</span>"
                   b.innerHTML = sum
                   PAGECOUNT = 1
                   MAXPAGECOUNT = 1
                   return
               }
               sum = "<div class=\"row\" id=\"picDiv\">\n" +
                    "                \n"
               for(var i=0;i<jsonArr.length;i++){
                   sum += "<div class=\"col-sm-6 col-md-4\">\n" +
                       "                <div class=\"thumbnail\" style='background-color: #fbfbfb;border-radius: 16px'>\n" +
                       "                    <a target='_blank' href='"+BASEURL2+jsonArr[i].url+"'><img src=\""+BASEURL2+jsonArr[i].url+"\" alt=\"...\"  style='CURSOR: hand;width: 300px;height: 200px;border-radius: 12px'></a>\n" +
                       "                    <div class=\"caption\" style='height: 100px'>\n" +
                       "                        <p style='color: #000000'>"+jsonArr[i].content+"</p>\n" +
                       "                    </div>\n" +
                       "                </div>\n" +
                       "            </div>"
               }
               sum += "</div>"
               sum += "<div class=\"pageCountDiv\">\n" +
                   "                <nav aria-label=\"...\">\n" +
                   "                    <ul class=\"pager\">\n" +
                   "                        <li><a onclick=\"per()\" style=\"user-select: none\">Previous</a></li>\n" +
                   "                        <li><a onclick=\"next()\" style=\"user-select: none\">Next</a></li>\n" +
                   "                    </ul>\n" +
                   "                </nav>\n" +
                   "                <span id=\"pageCount\"></span>\n" +
                   "            </div>"
               big.innerHTML = sum
               var str = result.msg
                var m = str.split(",")
                c.innerHTML = m[0]
                d.innerHTML = m[1]
                if(jsonArr.length>0){
                    MAXPAGECOUNT = m[2]
                }else{
                    MAXPAGECOUNT = 1
                }
                PAGECOUNT = PAGECOUNT-per+next
                document.getElementById("pageCount").innerHTML = "当前页码: " +PAGECOUNT
            },
            error:function (){
                alert("服务当前繁忙！")
            }
        });
    }


}


function searchByRe(t){
    let w = t.value
    document.getElementById("sText").value = w
    search(0,0)
}

function largerPic(url){
    window.open(url)
}



function changeType(){
    PAGECOUNT = 1
    MAXPAGECOUNT = 1
    if(TYPE===1){
        TYPE = 2
        document.getElementById("webt").setAttribute("style","background-color:#5798c1;")
        document.getElementById("pict").setAttribute("style","background-color:orange;pointer-events:none")
        search(0,0)
    }else{
        TYPE = 1
        document.getElementById("webt").setAttribute("style","background-color:orange;pointer-events:none")
        document.getElementById("pict").setAttribute("style","background-color:#5798c1;")
        search(0,0)
    }
}

function per(){
    if(PAGECOUNT==1){
        alert("前面没有了...")
        return ;
    }
    search(1,0)
}

function next(){
    if(PAGECOUNT==MAXPAGECOUNT){
        alert("后面没有了...")
        return ;
    }
    search(0,1)
}


function login(){
    var username = document.getElementById("uname").value
    var pass = document.getElementById("pass").value
    $.ajax({
        url:BASEURL+"loginJudge/login.do",
        data:{
            userName:username,
            pass:pass
        },
        type:"get",
        dataType:'json',
        success:function(result,status,xhr){
            var code = result.code
            var ac = xhr.getResponseHeader("ac")
            var re = xhr.getResponseHeader("re")
            if(code===4000){
                alert("登录成功！")
                save("ac",ac)
                save("re",re)
                window.location.reload()
            }
        },
        error:function (){
            alert("服务当前繁忙！")
        }
    });
}


function reg(){
    var username = document.getElementById("uname").value
    var pass = document.getElementById("pass").value
    var but = document.getElementById("reg")
    but.setAttribute("style","disabled:true")
    but.innerText = "..."
    $.ajax({
        url:BASEURL+"/loginJudge/reg.go",
        data:{
            userName:username,
            pass:pass
        },
        type:"get",
        dataType:'json',
        success:function(result){
            var code = result.code
            if(code===4000){
                alert("注册成功！")
            }else{
                alert("注册失败！")
            }
            but.setAttribute("style","disabled:false")
            but.innerText = "See"
        },
        error:function (){
            alert("服务当前繁忙！")
            but.setAttribute("style","disabled:false")
            but.innerText = "See"
        }
    });
}


function star(tid){

}


function exitMe(){

    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            window.location.reload()
        }
    }

    let ac = searchStorage("ac")
    $.ajax({
        url:BASEURL+"/loginJudge/exitMe.do",
        headers:{
            ac:ac
        },
        type:"get",
        dataType:"json",
        success:function (result){
            if(result.code===4000){
                alert("注销成功")
                window.location.reload()
            }else{
                window.location.reload()
            }
        },
        error:function (){

        }
    })
}

//
// "<h3>相关搜索:</h3>\n" +
// "            <div class=\"rightBigDiv\">\n" +
// "                <a href=\"#\">大海大海</a>\n" +
// "            </div>"




//绑定调用
$(function (){
    $("#reg").click(function (){
        reg();
    })

    $("#login").click(function (){
        login();
    })

    $("#loginSignUp").click(function (){
        if(ISLOGIN===1){
            getStarCollections()
        }
    })

    $("#addStarCollection").click(function (){
        addStarCollection()
    })

    $("#log_out").click(function(){
        exitMe()
    })
})



