package com.sbs.example.jspCommunity.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sbs.example.jspCommunity.container.Container;
import com.sbs.example.jspCommunity.controller.Controller;
import com.sbs.example.jspCommunity.dto.Article;
import com.sbs.example.jspCommunity.dto.Board;
import com.sbs.example.jspCommunity.dto.Member;
import com.sbs.example.jspCommunity.service.ArticleService;
import com.sbs.example.util.Util;

public class UserArticleController extends Controller {
	private ArticleService articleService;

	public UserArticleController() {
		articleService = Container.articleService;
	}

	public String showList(HttpServletRequest req, HttpServletResponse resp) {
		int boardId = Integer.parseInt(req.getParameter("boardId"));
		String searchKeyword = req.getParameter("searchKeyword");
		String searchKeywordType = req.getParameter("searchKeywordType");
		
		int itemsInAPage = 30;
		int page = Util.getAsInt(req.getParameter("page"), 1);
		int limitStart = (page - 1) * itemsInAPage;

		Board board = articleService.getBoardById(boardId);
		int totalCount = articleService.getArticlesCountByBoardId(boardId, searchKeywordType, searchKeyword);
		List<Article> articles = articleService.getForPrintArticlesByBoardId(boardId, limitStart, itemsInAPage, searchKeywordType, searchKeyword);

		req.setAttribute("board", board);
		int totalPage = (int) Math.ceil(totalCount / (double) itemsInAPage);

		int pageBoxSize = 10;

		// 현재 페이지 박스 시작, 끝 계산

		int previousPageBoxesCount = (page - 1) / pageBoxSize;
		int pageBoxStartPage = pageBoxSize * previousPageBoxesCount + 1;
		int pageBoxEndPage = pageBoxStartPage + pageBoxSize - 1;

		if (pageBoxEndPage > totalPage) {
			pageBoxEndPage = totalPage;
		}

		// 이전버튼 페이지 계산
		int pageBoxStartBeforePage = pageBoxStartPage - 1;
		if (pageBoxStartBeforePage < 1) {
			pageBoxStartBeforePage = 1;
		}

		// 다음버튼 페이지 계산
		int pageBoxEndAfterPage = pageBoxEndPage + 1;

		if (pageBoxEndAfterPage > totalPage) {
			pageBoxEndAfterPage = totalPage;
		}

		// 이전버튼 노출여부 계산
		boolean pageBoxStartBeforeBtnNeedToShow = pageBoxStartBeforePage != pageBoxStartPage;
		// 다음버튼 노출여부 계산
		boolean pageBoxEndAfterBtnNeedToShow = pageBoxEndAfterPage != pageBoxEndPage;

		req.setAttribute("totalCount", totalCount);
		req.setAttribute("articles", articles);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("page", page);

		req.setAttribute("pageBoxStartBeforeBtnNeedToShow", pageBoxStartBeforeBtnNeedToShow);
		req.setAttribute("pageBoxEndAfterBtnNeedToShow", pageBoxEndAfterBtnNeedToShow);
		req.setAttribute("pageBoxStartBeforePage", pageBoxStartBeforePage);
		req.setAttribute("pageBoxEndAfterPage", pageBoxEndAfterPage);
		req.setAttribute("pageBoxStartPage", pageBoxStartPage);
		req.setAttribute("pageBoxEndPage", pageBoxEndPage);
		return "user/article/list";
	}

	public String showDetail(HttpServletRequest req, HttpServletResponse resp) {
		int id = Util.getAsInt(req.getParameter("id"), 0);

		if (id == 0) {
			return msgAndBack(req, "게시물번호를 입력해주세요.");
		}

		Member loginedMember = (Member)req.getAttribute("loginedMember");
		Article article = articleService.getForPrintArticleById(id, loginedMember);

		if (article == null) {
			return msgAndBack(req, id + "번 게시물은 존재하지 않습니다.");
		}
		
		int count = article.getHitsCount() + 1;

		article.setHitsCount(count);
		articleService.modify(article);
		
		req.setAttribute("article", article);
		return "user/article/detail";
	}

	public String showWrite(HttpServletRequest req, HttpServletResponse resp) {
		int boardId = Util.getAsInt(req.getParameter("boardId"), 0);

		if (boardId == 0) {
			return msgAndBack(req, "게시판번호를 입력해주세요.");
		}

		Board board = articleService.getBoardById(boardId);
		
		if (board == null) {
			return msgAndBack(req, boardId + "번 게시판은 존재하지 않습니다.");
		}
		
		req.setAttribute("board", board);

		return "user/article/write";
	}

	public String doWrite(HttpServletRequest req, HttpServletResponse resp) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		int boardId = Util.getAsInt(req.getParameter("boardId"), 0);

		if (boardId == 0) {
			return msgAndBack(req, "게시판번호를 입력해주세요.");
		}

		Board board = articleService.getBoardById(boardId);

		if (board == null) {
			return msgAndBack(req, boardId + "번 게시판은 존재하지 않습니다.");
		}
		
		String title = req.getParameter("title");
		
		if (Util.isEmpty(title)) {
			return msgAndBack(req, "제목을 입력해주세요.");
		}
		
		String body = req.getParameter("body");
		
		if (Util.isEmpty(body)) {
			return msgAndBack(req, "내용을 입력해주세요.");
		}
		
		Article article = new Article();
		article.setMemberId(loginedMemberId);
		article.setBoardId(boardId);
		article.setTitle(title);
		article.setBody(body);

		int newArticleId = articleService.write(article);

		return msgAndReplace(req, newArticleId + "번 게시물이 생성되었습니다.", String.format("detail?id=%d", newArticleId));
	}

	public String showModify(HttpServletRequest req, HttpServletResponse resp) {

		int id = Util.getAsInt(req.getParameter("id"), 0);

		if (id == 0) {
			return msgAndBack(req, "번호를 입력해주세요.");
		}
		
		Article article = articleService.getForPrintArticleById(id);

		if (article == null) {
			return msgAndBack(req, id + "번 게시물은 존재하지 않습니다.");
		}
		
		int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		
		if (article.getMemberId() != loginedMemberId) {
			return msgAndBack(req, id + "번 게시물에 대한 권한이 없습니다.");
		}
		
		Board board = articleService.getBoardById(article.getBoardId());

		req.setAttribute("board", board);
		req.setAttribute("article", article);
		return "user/article/modify";
	}

	public String doModify(HttpServletRequest req, HttpServletResponse resp) {

		int id = Util.getAsInt(req.getParameter("id"), 0);

		if (id == 0) {
			return msgAndBack(req, "번호를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticleById(id);

		if (article == null) {
			return msgAndBack(req, id + "번 게시물은 존재하지 않습니다.");
		}

		int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		
		if (article.getMemberId() != loginedMemberId) {
			return msgAndBack(req, id + "번 게시물에 대한 권한이 없습니다.");
		}

		String title = req.getParameter("title");
		
		if (Util.isEmpty(title)) {
			return msgAndBack(req, "제목을 입력해주세요.");
		}

		String body = req.getParameter("body");

		if (Util.isEmpty(body)) {
			return msgAndBack(req, "내용을 입력해주세요.");
		}
		
		article.setTitle(title);
		article.setBody(body);

		articleService.modify(article);

		return msgAndReplace(req, id + "번 게시물이 수정되었습니다.", String.format("detail?id=%d", id));
	}

	public String doDelete(HttpServletRequest req, HttpServletResponse resp) {

		int id = Util.getAsInt(req.getParameter("id"), 0);

		if (id == 0) {
			return msgAndBack(req, "번호를 입력해주세요.");
		}
		
		Article article = articleService.getForPrintArticleById(id);

		if (article == null) {
			return msgAndBack(req, id + "번 게시물은 존재하지 않습니다.");
		}
		
		int loginedMemberId = (int)req.getAttribute("loginedMemberId");

		if (article.getMemberId() != loginedMemberId) {
			return msgAndBack(req, id + "번 게시물에 대한 권한이 없습니다.");
		}

		articleService.delete(id);

		int boardId = article.getBoardId();

		return msgAndReplace(req, id + "번 게시물이 삭제되었습니다.", String.format("list?boardId=%d", boardId));
	}
}