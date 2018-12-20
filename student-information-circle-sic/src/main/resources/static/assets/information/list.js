//是否通过了审核
function verifide(value_){
	$("#verified").val(value_);
	$("#used").val(null);
	$("#pageNum").val(0);
	$("#newsPageForm").submit();
}

//是否已发布
function usedSearch(value_){
	$("#used").val(value_);
	$("#verified").val(null);
	$("#pageNum").val(0);
	$("#newsPageForm").submit();
}
//全部
function allSearch(){
	$("#used").val(null);
	$("#verified").val(null);
	$("#pageNum").val(0);
	$("#newsPageForm").submit();
}

function clearForm(this_){
	 var form = $(this_).parents("form");
	 $(form)[0].reset();
	 jQuery("input[type='hidden']").val("");
	 $('#news_id').val("");
	 $('#my-startDate').text("");
	 $('#my-endDate').text("");
}
