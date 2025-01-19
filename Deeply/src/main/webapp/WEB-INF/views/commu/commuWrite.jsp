<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/uploadAdapter.js"></script>
<div class="page-main">
	<c:if test="${c_category==1}">
	
	</c:if>
	<c:if test="${c_category==1}">
	
	</c:if>
	<form:form modelAttribute="commuVO" action="write"
	  id="commu_register" enctype="multipart/form-data">
		<form:errors element="div" cssClass="error-color"/>
		<ul>
			<li>
				<form:input path="c_title" placeholder="제목(30자 이내)"/>
				<form:errors path="c_title" cssClass="error-color"/>
			</li>
			<li>
				<form:textarea path="c_content"/>
				<form:errors path="c_content" cssClass="error-color"/>
				<%-- CKEditor 셋팅 --%>
				<script>
					function MyCustomUploadAdapterPlugin(editor){
						editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
							return new UploadAdapter(loader);
						}
					}
					ClassicEditor
						.create(document.querySelector('#c_content'),{
							extraPlugins:[MyCustomUploadAdapterPlugin]
						})
						.then(editor => {
							window.editor = editor;
						})
						.catch(error => {
							console.error(error);
						});
				</script>
			</li>
			<li>
				<form:label path="upload">파일 첨부</form:label>
				<input type="file" name="upload" id="upload">
			</li>
		</ul> 
		<div class="align-center">
			<form:button>등록</form:button>
			<input type="button" value="이전으로"
			             onclick="location.href='list'">
		</div> 
	</form:form>
</div>