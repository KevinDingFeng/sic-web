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
        <strong class="am-text-primary am-text-lg">预览</strong> / <small>View News</small>
      </div>
    </div>

    <hr>
    <#include "view_template.ftl">
    </div>
  </div>
<!-- content end -->
</div>
</@override>
<@extends name="/ftl/base.ftl">
</@extends>
</#escape>
