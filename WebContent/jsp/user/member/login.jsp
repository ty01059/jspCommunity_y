<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="로그인" />
<%@ include file="../../part/head.jspf"%>

<div class="formBox flex flex-direction-column">

	<section class="title-bar con-min-width">
		<h1 class="con">${pageTitle}</h1>
	</section>

	<script>
		let DoLoginForm__submited = false;
		function DoLoginForm__submit(form) {
			if (DoLoginForm__submited) {
				alert('처리중입니다.');
				return;
			}

			form.loginId.value = form.loginId.value.trim();

			if (form.loginId.value.length == 0) {
				alert('로그인 아이디를 입력해주세요.');
				form.loginId.focus();

				return;
			}

			form.loginPw.value = form.loginPw.value.trim();

			if (form.loginPw.value.length == 0) {
				alert('로그인 비밀번호를 입력해주세요.');
				form.loginPw.focus();

				return;
			}

			form.submit();
			DoLoginForm__submited = true;
		}
	</script>
	<form action="doLogin" method="POST"
		onsubmit="DoLoginForm__submit(this); return false;">
		<div>
			<div>
				로그인 아이디 : <input name="loginId" type="text" maxlength="50"
					placeholder="로그인 아이디를 입력해주세요." />
			</div>
		</div>

		<hr />

		<div>
			<div>
				로그인 비번 : <input name="loginPw" type="password" maxlength="50"
					placeholder="로그인 비밀버호를 입력해주세요." />
			</div>
		</div>

		<hr />

		<div>
			<div>
				<input type="submit" value="로그인" />
				<button type="button" onclick="history.back();">뒤로가기</button>
			</div>
		</div>
	</form>
</div>
<%@ include file="../../part/foot.jspf"%>