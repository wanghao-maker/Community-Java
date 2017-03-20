package me.cmnt.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ListModel;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.sun.xml.internal.rngom.parse.host.Base;

import me.cmnt.model.Member;
import me.cmnt.model.User;
import me.cmnt.service.BaseServiceI;

@ParentPackage("basePackage")
@Action(value = "member")
@Namespace("/")
@Results({
	@Result(name = "user_list", location = "/jsp/common/member/member.jsp"),
	@Result(name = "user_app_list", location = "/jsp/common/member/member_application.jsp")
})
public class MemberAction extends BaseAction {
	
	@Autowired
	private BaseServiceI memberService;
	@Autowired
	private BaseServiceI userService;
	private List<Member> memberList;
	private List<User> userList;
	private String uid;
	private Member member;
	private User user;
	public List<Member> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<Member> memberList) {
		this.memberList = memberList;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	@Override
	public List<Member> queryByEntType(int entType) {
		List<Member> listObj = new ArrayList<Member>();
		try {
			List<Object> listObjects = memberService.query(member, entType);
			for (Object cmnt : listObjects) {
				if (cmnt != null && cmnt instanceof Member) {
					listObj.add((Member) cmnt);
				}
			}
			return listObj;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 查询该社团所有成员
	 * @return
	 */
	public String selectMemberByCommunityId() {
		ActionContext actionContext = ActionContext.getContext();	//获得Struts容器
		Map<String,Object> session = actionContext.getSession();	//获得Session容器
		member = (Member) session.get("current_member");
		member.setMember_status(2);
		memberList = queryByEntType(5);
		userList = new ArrayList<User>();
		for (Member member_local : memberList) {
			user = new User();
			user.setId(member_local.getUser_id());
			List<Object> user_list = userService.query(user, 1);
			if (user_list != null && !user_list.isEmpty()) {
				userList.add((User) user_list.get(0));
			}
		}
		return "user_list";
	}
	
	/**
	 * 获得申请成员列表
	 * @return
	 */
	public String selectApplicationMember() {
		ActionContext actionContext = ActionContext.getContext();	//获得Struts容器
		Map<String,Object> session = actionContext.getSession();	//获得Session容器
		member = (Member) session.get("current_member");
		member.setMember_status(1);
		memberList = queryByEntType(5);
		userList = new ArrayList<User>();
		for (Member member_local : memberList) {
			user = new User();
			user.setId(member_local.getUser_id());
			List<Object> user_list = userService.query(user, 1);
			if (user_list != null && !user_list.isEmpty()) {
				userList.add((User) user_list.get(0));
			}
		}
		return "user_app_list";
	}
	
	public String confirm() {
		member = new Member();
		member.setUser_id(Integer.valueOf(uid));
		member = queryByEntType(2).get(0);
		if (member != null) {
			member.setMember_status(2);
			memberService.update(member);
			return ajaxForwardSuccess("状态已更改！");
		}
		return ajaxForwardError("修改失败，请重试");
	}
	
}
