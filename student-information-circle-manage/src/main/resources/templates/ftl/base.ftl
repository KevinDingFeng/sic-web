<@override name="title">manage platform - 管理平台</@override>
<@override name="headBase">
<meta name="keywords" content="管理平台" />
<meta name="description" content="管理平台" />
<meta name="viewport" content="width=device-width, initial-scale=1">
 <meta name="renderer" content="webkit">
 <meta http-equiv="Cache-Control" content="no-siteapp" />
  <link rel="icon" type="image/png" href="/assets/i/favicon.png">
  <link rel="apple-touch-icon-precomposed" href="/assets/i/app-icon72x72@2x.png">
  <meta name="apple-mobile-web-app-title" content="Amaze UI" />
  <link rel="stylesheet" href="/assets/css/amazeui.min.css"/>
  <link rel="stylesheet" href="/assets/css/admin.css">

<script src="/assets/js/jquery-2.1.4.min.js"></script>
<script src="/assets/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="/assets/bootstrap-3.3.7-dist/css/bootstrap.min.css">

<@block name="headBranch"></@block>
</@override>

<@override name="body">
    <#include "header.ftl">
	<@block name="centre">
		<#include "footer.ftl">
	</@block>
	<@block name="script"></@block>
</@override>
<@extends name="core/base.ftl"></@extends>