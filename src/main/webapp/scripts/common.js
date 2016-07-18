$.ajaxSetup({
    contentType: "application/json; charset=utf-8"
});

/*jQuery.qtlPost = function(param,isShow) {
    if(isShow!=false){
        $.showLoading();
    }
    var successFn = param.success;
    var _default = {
        type : "post",
        timeout : 20000,
        contentType : "application/json; charset=utf-8",
        dataType : "json",
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            $.hideLoading();
            if (textStatus == 'timeout') {
                console.log("访问服务器超时");
            } else{
                console.log("访问服务器出错");
            }
        },
        success: function(reData){
            $.hideLoading();
            if(ErrorCode.SessionTimeOut==reData.errorCode){
                //TODO i18n
                $.qtlAlert("会话超时，请重新登陆");
                window.location.href = "/user/login.htm";
            }else if(ErrorCode.Unauthorized==reData.errorCode){
                $.qtlAlert(reData.message);
                console.log("没有权限访问:"+this.url);
                return;
            }
            if(successFn){
                successFn(reData);
            }
        }
    }
    delete param.success;
    $.extend(_default, param);
    $.ajax(_default);
}

*//**
 * get请求
 *//*
jQuery.qtlGet = function(param) {
    $.showLoading();
    var successFn = param.success;
    var _default = {
        type : "get",
        timeout : 20000,
        contentType : "application/json; charset=utf-8",
        dataType : "json",
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            $.hideLoading();
            if (textStatus == 'timeout') {
                console.log("访问服务器超时");
            } else{
                console.log("访问服务器出错");
            }
        },
        success: function(reData){
            $.hideLoading();
            if(ErrorCode.SessionTimeOut==reData.errorCode){
                $.qtlAlert("会话超时，请重新登陆");
                window.location.href = "/user/login.htm"
            }else if(ErrorCode.Unauthorized==reData.errorCode){
                $.qtlAlert(reData.message);
                console.log("没有权限访问:"+this.url);
                return;
            }

            if(successFn){
                successFn(reData);
            }
        }
    }
    delete param.success;
    $.extend(_default, param);
    $.ajax(_default);
}


jQuery.isZhCN = function(){
    var language_zh_cn = "zh-cn";
    var currentLang = navigator.language;
    if(!currentLang){
        currentLang = navigator.browserLanguage;
    }
    if(currentLang.toLowerCase() == language_zh_cn){
       return true;
    }else{
        return false;
    }
}

$(function(){
    loadProperties();
    $(".qtl-grid-searchfield").initQtlCodeSelect();
})


function loadProperties() {
    var language;
    if($.isZhCN()){
        language = 'zh';
    }else{
        language = 'en';
    }
    jQuery.i18n.properties({//加载资浏览器语言对应的资源文件
        name : 'strings', //资源文件名称
        path : '/i18n/', //资源文件路径
        mode : 'map', //用Map的方式使用资源文件中的值
        language : language,
        callback : function() {//加载成功后设置显示内容
            $('.qtl-i18n').each(function() {
                $(this).text($.i18n.prop($(this).text()));
            });
            if(typeof page_init === 'function'){
                page_init();
            }
        }
    });
}

*//**
 * 将form转成json对象
 * @returns {{}}
 *//*
jQuery.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

jQuery.redStar = function(){
    return $("<span class='red-star'>*</span>");
}*/
