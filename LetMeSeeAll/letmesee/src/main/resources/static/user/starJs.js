BASEURL = "http://localhost:8888/"
CID = "..."

function searchStorage(key){
    return localStorage.getItem(key)
}


function getCid(){
    let url = window.location.href
    CID = url.split("?")[1].split("=")[1]
}

function init1(){
    let ac = searchStorage("ac")
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
                if(ISLOGIN===0){
                    ISLOGIN = 1
                }
            }else if(result.code===4007){
                if(ISLOGIN===1){
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
                if(ISLOGIN===0){
                    ISLOGIN = 1
                }
            }else if(result.code===4009){
                if(ISLOGIN===1){
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

PAGECOUNT = 1

function getStarFromCollection(per,next){
    if(!init1()){
        if(!init2()){
            alert("用户没登录")
            return
        }
    }
    let ac = searchStorage("ac")
    $.ajax({
        url:BASEURL+"/loginJudge/getStarCollections.do",
        headers:{
            ac:ac
        },
        data:{
            cid:CID,
            pageCountStr:PAGECOUNT-per+next,
            limitCountStr:"10"
        },
        type:"get",
        dataType:"json",
        success:function (result){
            if(result.code===4000){
                let b = document.getElementById("littleDiv")
                let jsonArr = result.data
                let sum = ""
                for(let i=0;i<jsonArr.length;i++){
                    let textInfo = jsonArr[i]
                    sum += "<button type=\"button\" class=\"btn btn-primary btn-lg btn-block\" id=\""+textInfo._id+"\""+">"+textInfo.t+"</button>"
                }
                b.innerHTML = sum
                PAGECOUNT = PAGECOUNT-per+next
            }

        },
        error:function (){

        }
    })
}