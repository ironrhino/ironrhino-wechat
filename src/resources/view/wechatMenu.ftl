<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>菜单编辑</title>
</head>
<body>
<@s.form id="menu-form" action="${actionBaseUrl}" method="post" class="ajax">
	<table class="datagrid nullable table table-condensed" data-maxrows="3">
		<thead>
			<tr>
				<th style="width:120px;">标题</th>
				<th style="width:100px;">${getText('type')}</th>
				<th style="width:200px;">${getText('value')}</th>
				<th>子菜单</th>
				<th class="manipulate"></th>
			</tr>
		</thead>
		<tbody>
			<#assign size = 0>
			<#if menu.button?? && menu.button?size gt 0>
				<#assign size = menu.button?size-1>
			</#if>
			<#list 0..size as index>
			<tr class="linkage">
				<td><@s.textfield theme="simple" name="menu.button[${index}].name" class="required" style="width:100px;" maxlength=8/></td>
				<td><@s.select theme="simple" name="menu.button[${index}].type" class="linkage_switch" style="width:80px;" list="@org.ironrhino.wechat.model.WechatButtonType@values()" listKey="name" listValue="displayName" headerKey="" headerValue="父菜单"/></td>
				<td>
				<@s.textfield theme="simple" name="menu.button[${index}].url" class="required linkage_component view" type="url"/>
				<@s.textfield theme="simple" name="menu.button[${index}].key" class="required linkage_component click scancode_push scancode_waitmsg pic_sysphoto pic_photo_or_album pic_weixin location_select"/>
				<@s.textfield theme="simple" name="menu.button[${index}].media_id" class="required linkage_component media_id view_limited"/>
				</td>
				<td>
					<table class="datagrid showonadd linkage_component linkage_default" data-maxrows="5">
						<thead>
							<tr>
								<th style="width:120px;">标题</th>
								<th style="width:100px;">${getText('type')}</th>
								<th style="width:200px;">${getText('value')}</th>
								<th class="manipulate"></th>
							</tr>
						</thead>
						<tbody>
							<#assign size = 0>
							<#if menu.button?? && menu.button[index]?? && menu.button[index].sub_button?? && menu.button[index].sub_button?size gt 0>
								<#assign size = menu.button[index].sub_button?size-1>
							</#if>
							<#list 0..size as index2>
							<tr class="linkage">
								<td><@s.textfield theme="simple" name="menu.button[${index}].sub_button[${index2}].name" class="required" style="width:100px;" maxlength=20/></td>
								<td><@s.select theme="simple" name="menu.button[${index}].sub_button[${index2}].type" class="linkage_switch required" style="width:80px;" list="@org.ironrhino.wechat.model.WechatButtonType@values()" listKey="name" listValue="displayName"/></td>
								<td>
									<@s.textfield theme="simple" name="menu.button[${index}].sub_button[${index2}].url" class="required linkage_component view" type="url"/>
									<@s.textfield theme="simple" name="menu.button[${index}].sub_button[${index2}].key" class="required linkage_component click scancode_push scancode_waitmsg pic_sysphoto pic_photo_or_album pic_weixin location_select"/>
									<@s.textfield theme="simple" name="menu.button[${index}].sub_button[${index2}].media_id" class="required linkage_component media_id view_limited"/>
								</td>
								<td class="manipulate"></td>
							</tr>
							</#list>
						</tbody>
					</table>
				</td>
				<td class="manipulate"></td>
			</tr>
			</#list>
		</tbody>
	</table>
	<div style="text-align:center;"><button type="submit" class="btn btn-primary" style="margin-top:10px;"><strong>${action.getText('confirm')}</strong></button></div>
</@s.form>
</body>
</html></#escape>