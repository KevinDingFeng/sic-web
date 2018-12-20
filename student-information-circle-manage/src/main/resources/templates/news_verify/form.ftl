<#escape x as x?html>
<@override name="headBranch">
  <#-- 添加富文本编辑器必须的 js 和 css 文件 -->
<style>
.modal-dialog{margin:52ps auto;}
#box{
      width: 100px;
      height: 100px;
      border: 2px solid #858585;
    }
    .imgshow{
      width: 100%;
      height: 100%;
    }
    #pox{
      width: 100px;
      height: 50px;
      overflow: hidden;
    }
</style>
</@override>

<@override name="centre">
<div class="am-cf admin-main">
<#include "/ftl/left.ftl">
<!-- content start -->
<div class="admin-content">
  <div class="admin-content-body">
    <div class="am-cf am-padding am-padding-bottom-0">
      <div class="am-fl am-cf">
        <strong class="am-text-primary am-text-lg">审核</strong> / <small>Audit News</small>
      </div>
    </div>

    <hr>
    <#include "/news/view_template.ftl">
    </div>
    
<form method="post" class="am-form" id="new_verify_form" action="/sys/new_verify/verify"
	id="editNewsForm">
	<input type="hidden" name="informationId" value="${entity.id}">
	<input type="hidden" id="verify_result" name="verifyResult" value="${entity.verifyResult!''}">
	<div class="am-g am-margin-top-sm">
		<div class="am-u-sm-12 am-u-md-2 am-text-right admin-form-text">说明</div>
		<div class="am-u-sm-12 am-u-md-10">
			<textarea rows="3" cols="20" name="resultRemark">
			</textarea>
		</div>
	</div>
	<div class="am-margin">
		<button type="button" onclick="verify('Pass')" class="am-btn am-btn-primary am-btn-xs">通过</button>
		<button type="button" onclick="verify('Fail')" class="am-btn am-btn-primary am-btn-xs">不通过</button>
	</div>
</form>
  </div>
<!-- content end -->
<script type="text/javascript">
	function verify(value_){
		$("#verify_result").val(value_);
		$("#new_verify_form").submit();
	}
</script>
</div>
</@override>
<@extends name="/ftl/base.ftl">
</@extends>
</#escape>
