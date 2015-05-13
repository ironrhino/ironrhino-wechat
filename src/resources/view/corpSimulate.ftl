﻿<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${action.getText('simulate')}</title>
</head>
<body>
	<@s.form id="test-form" method="post" class="ajax view form-horizontal">
		<#if result?has_content>
		<div class="alert alert-info">
		${result}
		</div>
		</#if>
		<@s.textarea theme="simple" name="text" class="required container" cssStyle="height:300px;"/>
		<button type="submit" class="btn btn-block btn-primary" style="margin-top:10px;"><strong>${action.getText('confirm')}</strong></button>
	</@s.form>
</body>
</html></#escape>