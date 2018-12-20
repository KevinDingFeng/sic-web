<#escape x as x?html>
<@override name="headBranch">
<link rel="stylesheet" type="text/css" href="/assets/css/style.css" />
<script src="/assets/jquery_validation/jquery.validate.min.js"></script>
<script src="/assets/jquery_validation/additional-methods.min.js"></script>
<script src="/assets/jquery_validation/localization/messages_zh.min.js"></script>
  <#-- 添加富文本编辑器必须的 js 和 css 文件 -->
	<link rel="stylesheet" href="/assets/summernote/summernote.css" >
	<script type="text/javascript" src="/assets/summernote/summernote.min.js"></script>
	<script type="text/javascript" src="/assets/summernote/lang/summernote-zh-CN.js"></script>
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
    .btn_sty{
    	border:none;
    	background-color:#fff;
    }
    .inpt_cc{
    	border:none
    }
    .btn_cc{
    	position:relative;
    }
    .cc_sty{
    	position:absolute;
    	top: 6px;
    	left: 119px;
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
        <strong class="am-text-primary am-text-lg">编辑</strong> / <small>Edit News</small>
      </div>
    </div>

    <hr>
    <div class="am-tabs am-margin" data-am-tabs>
      <ul class="am-tabs-nav am-nav am-nav-tabs">
        <li class="am-active"><a href="#tab1">新闻信息</a></li>
      </ul>
      <#if entity.id??>
    <form method="post" class="am-form" action="/sys/sys_new/update" id="editNewsForm" enctype="multipart/form-data">
      <input type="hidden" name="id" value="${entity.id}" />
      <#else>
	<form method="post" class="am-form" action="/sys/sys_new/create" id="editNewsForm" enctype="multipart/form-data">
      </#if>
      
      <#if entity.imgs??>
      	<#assign imgsList=entity.imgs?split(',')>
	      	<#if imgsList[0] != " ">
	      		<#assign imgshow1 ='/f/'+imgsList[0]>
	      		<#else>
	      		<#assign imgshow1 =''>
	      	</#if>
	      	<#if imgsList[1] != " ">
	      		<#assign imgshow2 ='/f/'+imgsList[1]>
	      		<#else>
	      		<#assign imgshow2 =''>
	      	</#if>
	      	<#if imgsList[2] != " ">
	      		<#assign imgshow3 ='/f/'+imgsList[2]>
	      		<#else>
	      		<#assign imgshow3 =''>
	      	</#if>
      	<#else>
      	<#assign imgshow1 =''>
      	<#assign imgshow2 =''>
      	<#assign imgshow3 =''>
      </#if>
      	<div class="am-tabs-bd">
        	<div class="am-tab-panel am-fade am-in am-active" id="tab1">
        	<div class="am-u-sm-4 am-u-md-3 am-text-center btn_cc">
        	<div class="am-g am-margin-top" >
	        		<div id="box">
					    <img class="imgshow" id="imgshow1" src="${imgshow1}" alt=""/>
					  </div>
	        		<div class="img_bor" id="pox">
						<input type="file" id="informationImg1" class="form-control photo_bj create-form-control inpt_cc" name="informationImg1" accept="image/*">
						<div class="cc_sty">
							<button  onclick="deleteImg('1')" class="btn_sty">
								<i class="am-icon-remove"></i>
							</button>
						</div>
						<input type="hidden" id="deleteImageFlag1" name="deleteImageFlag1" >
					</div>
				</div>
        	</div>
        	<div class="am-u-sm-4 am-u-md-3 am-text-center btn_cc">
        	<div class="am-g am-margin-top">
	        		<div id="box">
					    <img class="imgshow" id="imgshow2" src="${imgshow2}" alt=""/>
					  </div>
	        		<div class="img_bor" id="pox">
						<input type="file" id="informationImg2" class="form-control photo_bj create-form-control inpt_cc" name="informationImg2" accept="image/*">
						<div class="cc_sty">
							<button type="button" onclick="deleteImg('2')"class="btn_sty">
								<i class="am-icon-remove"></i>
							</button>
						</div>
						<input type="hidden" id="deleteImageFlag2" name="deleteImageFlag2" >
					</div>
				</div>
        	</div>
        	<div class="am-u-sm-4 am-u-md-6 am-text-center btn_cc">
	        	<div class="am-g am-margin-top">
		        		<div id="box">
						    <img class="imgshow" id="imgshow3" src="${imgshow3}" alt=""/>
						  </div>
		        		<div class="img_bor" id="pox">
							<input type="file" id="informationImg3" class="form-control photo_bj create-form-control inpt_cc" name="informationImg3" accept="image/*">
							<div class="cc_sty">
								<button type="button" onclick="deleteImg('3')"class="btn_sty">
									<i class="am-icon-remove"></i>
								</button>
							</div>
							<input type="hidden" id="deleteImageFlag1" name="deleteImageFlag3" >
						</div>
					</div>
	        	</div>
			</div>
			<div class="am-g">
				<div class="am-u-sm-4 am-u-md-1 am-text-right">类型</div>
				<div class="am-u-sm-8 am-u-md-11">
					<#list types as type>
						<#assign typeCheck =''>
						<#if entity.types??>
							<#list entity.types as typeDb>
								<#if type.id == typeDb.id>
									<#assign typeCheck ='checked'>
								</#if>
							</#list>
						</#if>
						<label class="am-checkbox-inline">
					    	<input type="checkbox" name="types" value="${type.id}" data-am-ucheck ${typeCheck}> ${type.name}
					    </label>
					</#list>
					<div id="types-title-error" style="display:none">
						<label>请选择类型</label>
					</div>
          		</div>
			</div>
        	<div class="am-g am-margin-top-sm">
          		<div class="am-u-sm-4 am-u-md-1 am-text-right">标题</div>
          		<div class="am-u-sm-8 am-u-md-4">
            		<input type="text" class="am-input-sm" name="title" value="${entity.title!}" maxlength='255' required >
          		</div>
          		<div class="am-hide-sm-only am-u-md-6">*必填</div>
        	</div>
        	<div class="am-g am-margin-top-sm">
          		<div class="am-u-sm-12 am-u-md-1 am-text-right admin-form-text">内容</div>
          		<div class="am-u-sm-12 am-u-md-11">
            		<div class="summernote" name="content" placeholder="内容" action="/sys/sys_new/upload_file"></div>
					<input type="hidden" id="summernoteContent" name="context" required="required" value="${(entity.context!'')}" />
				</div>
          	</div>
	        <div class="am-margin tl_c">
				<button type="button" id="new_submit_btn" onclick="publish('editNewsForm')" class="am-btn am-btn-primary am-btn-xs">提交保存</button>
			</div>
		</div>
	</form>
    </div>
    </div>
    
	<script type="text/javascript" src="/assets/summernotejs/summernote.js"></script>
	<script type="text/javascript" src="/assets/information/form.js"></script>
    <script>
		if($("#summernoteContent").val()){
			$('.summernote').summernote('code', $("#summernoteContent").val());
		}
    </script>
  </div>
<!-- content end -->
</div>
</@override>
<@extends name="/ftl/base.ftl">
</@extends>
</#escape>
