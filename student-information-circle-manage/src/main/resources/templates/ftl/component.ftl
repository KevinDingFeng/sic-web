<div class="am-cf">
              共 ${page.totalElements} 条记录
      <div class="am-fr">
        <ul class="am-pagination">
          <#if pageNum == 0>
          <li class="am-disabled"><a href="javascript:;">«</a></li>
          <#else>
          <li><a href="javascript:;" onclick="doPages(0)">«</a></li>
          </#if>
          
          <#if pageNum == 0>
          <li class="am-active"><a href="javascript:;">1</a></li>
          	<#if page.totalPages gt 1>
          	<li><a href="javascript:;" onclick="doPages(1)">2</a></li>
          	</#if>
          	<#if page.totalPages gt 2>
          	<li>...</li>
          	</#if>
          <#else>
            <#if pageNum gt 1>
          	<li>...</li>
          	</#if>
          <li><a href="javascript:;" onclick="doPages(${pageNum - 1})">${pageNum}</a></li>
		  <li class="am-active"><a href="javascript:;">${pageNum + 1}</a></li>                  	
          	<#if page.totalPages gt (pageNum + 1)>
          	<li><a href="javascript:;" onclick="doPages(${pageNum + 1})">${pageNum + 2}</a></li>
          	</#if>
          	<#if page.totalPages gt (pageNum + 2)>
          	<li>...</li>
          	</#if>
          </#if>
          <#if page.totalPages == (pageNum + 1)>
		  <li class="am-disabled"><a href="javascript:;">»</a></li>
          <#else>
          <li><a href="javascript:;" onclick="doPages(${pageNum + 1})">»</a></li>
          </#if>
       </ul>
    </div>
</div>
