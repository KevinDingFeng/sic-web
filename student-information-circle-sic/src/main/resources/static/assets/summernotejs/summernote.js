/*@author zhanping.yang
 * 渲染富文本编辑框
 */
$('.summernote').each(
	function() {
		var $this = $(this);
		var placeholder = $this.attr("placeholder") || '';
		var url = $this.attr("action") || '';
		$this.summernote({
			lang : 'zh-CN',
			placeholder : placeholder,
			height : 300,
			dialogsFade : true,// Add fade effect on
								// dialogs
			dialogsInBody : true,// Dialogs can be placed
									// in body, not in
			// summernote.
			disableDragAndDrop : false,// default false You
										// can disable drag
			// and drop
			callbacks : {
				onImageUpload : function(files) {
					var $files = $(files);
					$files.each(function() {
						var file = this;
						var data = new FormData();
						data.append("uploadFile", file);
						$.ajax({
							data : data,
							type : "POST",
							url : url,
							cache : false,
							contentType : false,
							processData : false,
							success : function(data) {
								// 文件不为空
								$this.summernote(
									'insertImage',
									data,
									function($image) {
									});
							},
							error : {}
						});
					});
				},
				onChange: function(contents, $editable) {
				      //console.log('onChange:', contents,$editable);
				    }
			}
		});
});

