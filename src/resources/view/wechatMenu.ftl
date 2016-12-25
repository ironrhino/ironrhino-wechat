<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>菜单编辑</title>
</head>
<body>
	<@s.form id="menu-form" method="post" class="ajax view form-horizontal">
		<@s.textarea theme="simple" name="json" class="required container" cssStyle="height:500px;"/>
		<button type="submit" class="btn btn-block btn-primary" style="margin-top:10px;"><strong>${action.getText('confirm')}</strong></button>
	</@s.form>
</body>
</html></#escape>