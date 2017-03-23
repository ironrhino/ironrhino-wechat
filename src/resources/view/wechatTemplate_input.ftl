<#ftl output_format='HTML'>
<!DOCTYPE html>
<html>
<head>
<title>添加消息模板</title>
</head>
<body>
<@s.form action="${actionBaseUrl}/save" method="post" class="ajax form-horizontal">
	<@s.textfield label="模板短ID" name="id" class="required"/>
	<@s.submit value=getText('save') class="btn-primary"/>
</@s.form>
</body>
</html>


