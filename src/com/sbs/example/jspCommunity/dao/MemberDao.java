package com.sbs.example.jspCommunity.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.jspCommunity.dto.Member;
import com.sbs.example.mysqlutil.MysqlUtil;
import com.sbs.example.mysqlutil.SecSql;

public class MemberDao {

	public List<Member> getForPrintMembers() {
		List<Member> members = new ArrayList<>();

		SecSql sql = new SecSql();
		sql.append("SELECT M.*");
		sql.append("FROM `member` AS M");
		sql.append("ORDER BY M.id DESC");

		List<Map<String, Object>> mapList = MysqlUtil.selectRows(sql);

		for (Map<String, Object> map : mapList) {
			members.add(new Member(map));
		}

		return members;
	}

	public Member getForPrintMember(String loginId) {
		SecSql sql = new SecSql();
		sql.append("SELECT M.*");
		sql.append("FROM `member` AS M");
		sql.append("WHERE M.loginId = ?", loginId);

		Map<String, Object> map = MysqlUtil.selectRow(sql);
		
		if (map.isEmpty()) {
			return null;
		}
		
		Member member = new Member(map);
		
		return member;
	}

	public int join(Member member) {
		SecSql sql = new SecSql();
		sql.append("INSERT INTO member");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", loginId = ?", member.getLoginId());
		sql.append(", loginPw = ?", member.getLoginPw());
		sql.append(", `name` = ?", member.getName());
		sql.append(", nickname = ?", member.getNickname());
		sql.append(", email = ?", member.getEmail());

		return MysqlUtil.insert(sql);
	}
}