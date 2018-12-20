//在input file内容改变的时候触发事件
$('#informationImg1').change(function() {
	//获取input file的files文件数组;
	//$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
	//这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
	var file = $('#informationImg1').get(0).files[0];
	//创建用来读取此文件的对象
	var reader = new FileReader();
	//使用该对象读取file文件
	reader.readAsDataURL(file);
	//读取文件成功后执行的方法函数
	reader.onload = function(e) {
		//读取成功后返回的一个参数e，整个的一个进度事件
		//console.log(e);
		//选择所要显示图片的img，要赋值给img的src就是e中target下result里面
		//的base64编码格式的地址
		$('#imgshow1').get(0).src = e.target.result;
	}
	//选择完图片清除删除标志
	$("#deleteImageFlag1").val("");
})

$('#informationImg2').change(function() {
	var file = $('#informationImg2').get(0).files[0];
	var reader = new FileReader();
	reader.readAsDataURL(file);
	reader.onload = function(e) {
		$('#imgshow2').get(0).src = e.target.result;
	}
	//选择完图片清除删除标志
	$("#deleteImageFlag2").val("");
})
$('#informationImg3').change(function() {
	var file = $('#informationImg3').get(0).files[0];
	var reader = new FileReader();
	reader.readAsDataURL(file);
	reader.onload = function(e) {
		$('#imgshow3').get(0).src = e.target.result;
	}
	//选择完图片清除删除标志
	$("#deleteImageFlag3").val("");
})

$(document).ready(function() {
    $("#editNewsForm").validate({
    	ignore: [],  // 验证hidden元素
    	 // 通过验证后执行
    	submitHandler: function(form) {
    		$("#new_submit_btn").val("正在提交,请等待...");
    		$("#new_submit_btn").attr('disabled',true);
    		form.submit();
    	},
    	onfocusout: function(element) { 
    		//$(element).valid(); 
    	},
    	rules: {
    		title: {
        		required : true,
        		maxlength : 255 
        	},
        	context : {
        		required : true
        	},
     },
    	// 自定义提示信息
        messages: {
        	title: {
        		required : "请输入标题",
        		maxlength : "控制在255字内"	
        	},
        	context: "请输入内容"
        }
    });
});

/*
 * 删除图片
 */
function deleteImg(index){
	$('#imgshow'+index).attr('src', '');
	$("#informationImg"+index).val("");
	//添加删除图片标志
	$("#deleteImageFlag"+index).val("1");
	
}

/**
 * 上传文章
 * 
 * @param formId
 * @param hiddenId
 *            放富文本内容的隐藏域id
 * @returns
 */
function publish(formId){
	var $form = $("#"+formId);
	var flag=false; 
	$("input[name='types']:checkbox").each(function(){
		var tt = $(this);
	  if($(this).prop('checked')){ 
	    flag=true; 
	  } 
	}) 
	if(!flag){ 
	  //alert('有被选中');
	  $("#types-title-error").show();
	  return;
	}
	// 富文本编辑器
	$("div.summernote", $form).each(
			function() {
				var $this = $(this);
				if (!$this.summernote('isEmpty')) {
					$("#summernoteContent").val($this.summernote('code'));
				}else{
					$("#summernoteContent").val('');
				}
			});
	$form.submit();
}

$("input[name='types']:checkbox").click(function(){
	 $("#types-title-error").hide();
})