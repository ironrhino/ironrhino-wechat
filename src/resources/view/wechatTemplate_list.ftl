<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>消息模板管理</title>
</head>
<body>
<#assign actionColumnButtons=r'<@btn action="delete" confirm=true/>'>
<#assign bottomButtons=r'<@btn view="input" label="添加"/> <@btn class="reload"/>'>
<#assign columns={"template_id":{"width":"350px","alias":"模板ID"},"title":{},"content":{"width":"250px"},"example":{"width":"400px","alias":"示例"}}>
<@richtable columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons rowid=r"${entity.template_id}"/>
</body>
</html></#escape>