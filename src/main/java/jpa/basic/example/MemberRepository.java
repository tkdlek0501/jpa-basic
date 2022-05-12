package jpa.basic.example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class MemberRepository {
	
	private final EntityManager em;
	
	public Long createMember() {
		Member member = new Member();
		
		// member.setId(1L);
		member.setUsername("nameA");
		
		em.persist(member); // 기본키 생성 전략이 IDENTITY이면 이 시점에 쿼리를 던지고
		
		return member.getId(); // transaction 커밋 전에 id를 가져올 수 있다
	}
	
	public void createParentWithChild() {
		
		Child child1 = new Child();
		Child child2 = new Child();
		
		Parent parent = new Parent();
		parent.addChild(child1);
		parent.addChild(child2);
		
		em.persist(parent);
	}
	
	public void findMember() {
		
		List<Member> result = em.createQuery(
			"select m from Member m where m.username like '%kim%'", Member.class
		).getResultList();
		
		for(Member member : result) {
			System.out.println("member : " + member);
		}
		
		//동적쿼리를 위한 Criteria 사용 준비 -> 길어지면 작성 어렵지만 컴파일 시점에 오류를 잡을 수 있고 동적쿼리 짜기 쉽다
		// 실무에서 알아보기 어려워 유지보수 하기 쉽지 않다; 실용성이 없다
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Member> query = cb.createQuery(Member.class);
//		
//		Root<Member> m = query.from(Member.class);
//		
//		CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
//		List<Member> resultList = em.createQuery(cq).getResultList();
//	
//		for(Member cMember : resultList) {
//			System.out.println("criteria member : " + cMember);
//		}
		
		// 네이티브 SQL -> 이것보다 차라리 mybatis 이용하는 게 낫다
		//em.createNativeQuery("select member_id, city, street from member", Member.class).getResultList();
	}
	
	// TODO: 동적쿼리 처리는 QueryDSL 을 사용하자
	// www.querydsl.com
	// JPQL 사용법만 잘 익히면 QueryDSL 익히기 쉽다. JPQL이 먼저!
	
	public void jpqlTest() {
		Member member = new Member();
		member.setAge(10);
		member.setUsername("kim");
		em.persist(member);
		
		TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
//		TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
//		Query query3 = em.createQuery("select m.username, m.age from Member m");
		
		Member result = query.getSingleResult(); // 결과가 무조건 하나일 때; 없거나 여러개이면 exception 발생
		// Spring Data JPA -> try-catch로 잡아주는 처리를 해줌 
		System.out.println("result : " + result); 
		// getResultList(); // 결과가 없을 땐 빈 리스트 반환
	}
	
	public void paging() {
		for(int i=0;i<100;i++) {
			Member member = new Member();
			member.setAge(i);
			member.setUsername("member" + i);
			em.persist(member);
		}
		
		em.flush();
		em.clear();
		
		List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
			.setFirstResult(0)
			.setMaxResults(10)
			.getResultList();
		
		System.out.println("result.size = " + result.size());
	}
	
	public void join() {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);
		
		Member member = new Member();
		member.setAge(10);
		member.setUsername("member1");
		member.changeTeam(team);
		em.persist(member);
		
		// inner join
		String innerQuery = "select m from Member m inner join m.team t";
		List<Member> result1 = em.createQuery(innerQuery, Member.class)
				.getResultList();
		
		// left outer join
		String leftOuterQuery = "select m from Member m left join m.team t";
		List<Member> result2 = em.createQuery(leftOuterQuery, Member.class)
				.getResultList();
		
		// cross join
		String thetaQuery = "select m from Member m, Team t where m.username = t.name";
		List<Member> result3 = em.createQuery(thetaQuery, Member.class)
				.getResultList();
		
		// on 
		String onQuery = "select m, t from Member m left join m.team t on t.name";
		List<Member> result4 = em.createQuery(onQuery, Member.class)
				.getResultList();
		
		// 외부 조인
		String outQuery = "select m, t from Member m left join Team t on m.usernmae = t.name";
		
		
	}
}
