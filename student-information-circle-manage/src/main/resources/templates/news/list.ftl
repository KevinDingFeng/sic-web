<#escape x as x?html>
<@override name="headBranch">
</@override>

<@override name="centre">
<div class="am-cf admin-main">
<#include "/ftl/left.ftl">
<!-- content start -->
<#assign pageNum=page.number>

<div class="admin-content">
    <div class="admin-content-body">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf"><strong class="am-text-primary am-text-lg">新闻列表</strong> / <small>News List</small></div>
      </div>

      <hr>
      <div class="am-g">
        <div class="am-u-sm-12 am-u-md-6">
          <div class="am-btn-toolbar">
            <div class="am-btn-group am-btn-group-xs">
              <button type="button" class="am-btn am-btn-default" onclick="javascript:window.location.href='/sys/sys_new/form';"><span class="am-icon-plus"></span> 新增</button>
           <form method="post" class="am-form" action="/sys/sys_new/list" id="newsPageForm">
           	<input type="hidden" id="pageNum" name="page" value="${pageNum!}" />
           	<#if condition.verified??>
           		<input type="hidden" id="verified" name="verified" value="${condition.verified?string('true','flase')}" />
           		<#else>
           		<input type="hidden" id="verified" name="verified" />
           	</#if>
           	<#if condition.used??>
           		<input type="hidden" id="used" name="used" value="${condition.used?string('true','flase')}" />
           		<#else>
           		<input type="hidden" id="used" name="used"/>
           	</#if>
            <div class="btn-group btn-group-sm sbo-btn-group" >
	            <button type="button" class="btn btn-default sbo-active" onclick="allSearch()">全部</button>
	            <button type="button" class="btn btn-default <#if condition.verified?? && condition.verified >sbo-active</#if>" onclick="verifide(true)">通过</button>
	            <button type="button" class="btn btn-default <#if condition.verified?? && !condition.verified >sbo-active</#if>" onclick="verifide(false)">未通过</button>
	            <button type="button" class="btn btn-default <#if condition.used?? && condition.used?? >sbo-active</#if>" onclick="usedSearch(true)">发布</button>
	            <button type="button" class="btn btn-default <#if condition.used?? && !condition.used?? >sbo-active</#if>" onclick="usedSearch(false)">未发布</button>
	        </div>
	        <fieldset>
		    <div class="am-form-group">
		    <div class="am-alert am-alert-danger" id="my-alert" style="display: none">
			  <p>开始日期应小于结束日期！</p>
			</div>
			<div class="am-g">
			  <div class="am-u-sm-6">
			    <button type="button" class="am-btn am-btn-default am-margin-right" id="my-start">开始日期</button>
			    <span id="my-startDate">
			    	<#if condition.beginDate??>
			    		${condition.beginDate?string("yyyy-MM-dd")!}
			    	</#if>
			    </span>
			    <input type="hidden" id="begin_date" name="beginDate" value="${condition.beginDate!}"/>
			  </div>
			  <div class="am-u-sm-6">
			    <button type="button" class="am-btn am-btn-default am-margin-right" id="my-end">结束日期</button>
			    <span id="my-endDate">
			    	<#if condition.endDate??>
			    		${condition.endDate?string("yyyy-MM-dd")!}
			    	</#if>
			    </span>
			    <input type="hidden" id="end_date" name="endDate" value="${condition.endDate!}"/>
			  </div>
			</div>
			  <label for="doc-vld-name">ID：</label>
		      <input type="text" placeholder="ID" class="am-form-field" name="id" value="${condition.id!}"/>
		      <label for="doc-vld-name">提交者：</label>
		      <input type="text" placeholder="提交者" class="am-form-field" name="userName" value="${condition.userName!}"/>
		      <label for="doc-vld-name">标题：</label>
		      <input type="text" placeholder="标题" class="am-form-field" name="title" value="${condition.title!}"/>
		    </div>
		  </fieldset>
	        <div class="am-margin">
				<button type="button" class="am-btn am-btn-primary am-btn-xs" onclick="clearForm(this)">清除</button>
				<button type="submit" class="am-btn am-btn-primary am-btn-xs">搜索</button>
			</div>
	        </form>
            </div>
          </div>
        </div>
        <div class="am-u-sm-12 am-u-md-3">
          <div class="am-form-group">
            
          </div>
        </div>
        <div class="am-u-sm-12 am-u-md-3">
          
        </div>
      </div>

      <div class="am-g">
        <div class="am-u-sm-12">
            <table class="am-table am-table-striped am-table-hover table-main">
              <thead>
              <tr>
                <#--<th class="table-check"><input type="checkbox" /></th>-->
                <th class="table-id">ID</th>
                <th class="table-title">提交者</th>
                <th class="table-title">标题</th>
                <th class="table-date am-hide-sm-only">发布日期</th>
                <th class="table-set">操作</th>
              </tr>
              </thead>
              <tbody>
              <#list page.content as em>
              <tr>
                <#--<td><input type="checkbox" /></td>-->
                <td>${em.id!}</td>
                <td>${em.userName!}</td>
                <td>${em.title!}</td>
                <td>${em.creation}</td>
                <td>
                	<#-- 查看、编辑、删除 -->
                  <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-xs">
                      <a href="/sys/sys_new/view?id=${em.id}" target="_blank" class="am-btn am-btn-default am-btn-xs am-hide-sm-only"><span class="am-icon-archive"></span> 查看</a>
                      <a href="/sys/sys_new/form?id=${em.id}" class="am-btn am-btn-default am-btn-xs am-text-secondary"><span class="am-icon-pencil-square-o"></span> 编辑</a>
                      <a href="javascript:void(0);" onclick="removeNews(${em.id})" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only"><span class="am-icon-trash-o"></span> 删除</a>
                      <#if !em.verified>
                      	<a href="/new_verify/form?id=${em.id}" class="am-btn am-btn-default am-btn-xs am-text-secondary"><span class="am-icon-pencil-square-o"></span> 审核</a>
                      </#if>
                    </div>
                  </div>
                </td>
              </tr>
              <#else>
              <tr>
              	<td colspan="3" align="center">沒有符合的数据</td>
              </tr>
              </#list>
              </tbody>
            </table>
			
            <#include "/ftl/component.ftl">
            
            <hr />
        </div>
      </div>
    <script src="/assets/js/amazeui.min.js"></script>
	<script src="/assets/js/app.js"></script>
	<script type="text/javascript" src="/assets/information/list.js"></script>
  </div>
  </div>
<!-- content end -->
</div>
<script type="text/javascript">
    //翻页
    function doPages(n){
    	$("#pageNum").val(n);
    	$("#newsPageForm").submit();
    }
    //删除
    function removeNews(id){
    	if(confirm("确定删除此条新闻？")){
    		$.ajax({
			  type: 'DELETE',
			  url: "/sys/sys_new/remove?id=" + id,
			  data: {},
			  success: function(res){
			  	if(res == "success"){
					window.location.reload();
			  	}else{
			  		alert(res);
			  	}
			  }
			});
    	}else{
    		//console.log("选择了取消");
    	}
    }
    
    $(function() {
        var startDate = '${condition.beginDate!}';
        var endDate = '${condition.endDate!}';
        var $alert = $('#my-alert');
        $('#my-start').datepicker().
          on('changeDate.datepicker.amui', function(event) {
        	  console.log(event.date.valueOf())
            if (endDate && event.date.valueOf() > endDate.valueOf()) {
              $alert.find('p').text('开始日期应小于结束日期！').end().show();
            } else {
              $alert.hide();
              startDate = new Date(event.date);
              $('#my-startDate').text($('#my-start').data('date'));
              $('#begin_date').val($('#my-start').data('date'));
            }
        	  $(this).datepicker('close');
          });

        $('#my-end').datepicker().
          on('changeDate.datepicker.amui', function(event) {
        	  console.log($('#my-end').data('date'))
            if (startDate && event.date.valueOf() < startDate.valueOf()) {
              $alert.find('p').text('结束日期应大于开始日期！').end().show();
            } else {
              $alert.hide();
              endDate = new Date(event.date);
              $('#my-endDate').text($('#my-end').data('date'));
              $('#end_date').val($('#my-end').data('date'));
            }
            $(this).datepicker('close');
          });
      });
</script>
</@override>
<@extends name="/ftl/base.ftl">
</@extends>
</#escape>
