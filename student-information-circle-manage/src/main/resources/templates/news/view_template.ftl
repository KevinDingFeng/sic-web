<#escape x as x?html>
<div class="am-tabs am-margin" data-am-tabs>
      <ul class="am-tabs-nav am-nav am-nav-tabs">
        <li class="am-active"><a href="#tab1">新闻信息</a></li>
      </ul>
	<article class="blog-main">
      <h3 class="am-article-title blog-title">
        <a href="#">${entity.title!}</a>
      </h3>
      <h4 class="am-article-meta blog-meta">${entity.creation}</h4>
      <div class="am-g blog-content">
        <div class="am-u-lg-7">
          <#noescape>${(entity.context!'')}</#noescape>
        </div>
      </div>
    </article>
    </div>
</#escape>