<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>Login Page</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="format-detection" content="telephone=no">
  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp" />
  <link rel="alternate icon" type="image/png" href="/assets/i/favicon.png">
  <link rel="stylesheet" href="/assets/css/amazeui.min.css"/>
  <script src="/assets/js/jquery-2.1.4.min.js"></script>
  <style>
    .header {
      text-align: center;
    }
    .header h1 {
      font-size: 200%;
      color: #333;
      margin-top: 30px;
    }
    .header p {
      font-size: 14px;
    }
  </style>
</head>
<body>
<div class="header">
  <div class="am-g">
    <h1>学讯圈</h1>
    <p>manage platform - 管理平台</p>
  </div>
  <hr />
</div>
<div class="am-g">
  <div class="am-u-lg-6 am-u-md-8 am-u-sm-centered">
    <h3>登录</h3>
    <hr>
    <#--
    <div class="am-btn-group">
      <a href="#" class="am-btn am-btn-secondary am-btn-sm"><i class="am-icon-github am-icon-sm"></i> Github</a>
      <a href="#" class="am-btn am-btn-success am-btn-sm"><i class="am-icon-google-plus-square am-icon-sm"></i> Google+</a>
      <a href="#" class="am-btn am-btn-primary am-btn-sm"><i class="am-icon-stack-overflow am-icon-sm"></i> stackOverflow</a>
    </div>
    -->
    <br>
    <br>

    <form method="post" class="am-form" action="/login" id="loginForm">
      <label for="account">用户名:</label>
      <input type="text" name="account" id="account" value="${(entity.account)!}">
      <br>
      <label for="password">密码:</label>
      <input type="password" name="" id="passwordText" value="">
      <input type="hidden" name="password" id="password" />
      <br>
      <label for="remember-me">
        ${errMessage!}
      </label>
      <br />
      <div class="am-cf">
        <input type="button" onclick="encrypt()" name="" value="登 录" class="am-btn am-btn-primary am-btn-sm am-fl">
        <#--<input type="submit" name="" value="忘记密码 ^_^? " class="am-btn am-btn-default am-btn-sm am-fr">-->
      </div>
    </form>
    <script>
    function encrypt(){
    	var account = $("#account").val();
    	var passwordText = $("#passwordText").val();
    	if(account && passwordText){
    		$.ajax({
			  type: 'GET',
			  url: "/encrypt",
			  data: {"account": account, "password": passwordText},
			  success: function(res){
			  	console.log(res);
			  	if(res.code == 200){
			  		$("#password").val(res.data);
			  		doSubmit();
			  	}else{
			  		alert(res.data);
			  	}
			  }
			});
    	}else{
    		console.log("asldkfj");
    	}
    	
    	
    	/*$.ajax({
		  type: 'POST',
		  url: url,
		  data: data,
		  success: success,
		  dataType: dataType
		});*/
    }
    function doSubmit(){
    	$("#loginForm").submit();
    }
    </script>
    <hr>
    <p>© 2014 AllMobilize, Inc. Licensed under MIT license.</p>
  </div>
</div>
</body>
</html>
