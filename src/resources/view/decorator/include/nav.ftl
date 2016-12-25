<ul class="nav">
	<li><a href="<@url value="/"/>" class="ajax view">${action.getText("index")}</a></li>
	<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
	<li><a href="<@url value="/user"/>" class="ajax view">${action.getText("user")}</a></li>
	<li><a href="<@url value="/autoreplyRule"/>" class="ajax view">自动回复规则</a></li>
	</@authorize>
</ul>