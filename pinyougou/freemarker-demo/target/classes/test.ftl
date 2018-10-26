<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>模板技术</title>
    <#assign  linkman="张三"/>
    <#assign userinfo={"mobile":"120","address":"中粮"}/>
</head>
<body>
尊敬的xx,${username},Byebye;
署名:${username}
时间:${now}

<div>
    <h2>assign定义变量</h2>
    <div>
    ${linkman}你好!手机号:${userinfo.mobile},地址:${userinfo.address}
    </div>
</div>


<div>
    <h2>include指令</h2>
    <div>
         <#include "head.ftl">
    </div>
</div>

<#if success=true>
    你已通过实名认证
<#else >
    你未通过实名认证
</#if>


-----------商品价格表----- <br/>
<#list goodsList as goods>
    ${goods_index+1}商品名称:${goods.name}价格:${goods.price}<br>

</#list>
共 ${goodsList?size} 条记录

---------
<#assign text="{'bank':'工商银行','account':'112316446'}">

<#assign data=text?eval/>
开户行:${data.bank}账号:${data.account}

<br>
当前日期:${today?date}<br>
当前日期:${today?time}<br>
当前日期:${today?datetime}<br>
当前日期:${today?string("yyyy年MM月")}<br>

累计积分: ${point}

累计积分: ${point?c}

<br>
${aaa!'-'} <br>

<#if aaa??>
    aaa变量存在
<#else>
    aaa变量不存在
</#if>




</body>
</html>