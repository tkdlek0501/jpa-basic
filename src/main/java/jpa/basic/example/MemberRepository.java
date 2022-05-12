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
	
	// TODO: JPQL 정리
	// 아래 모든 내용
	
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
	
	public void subQuery() {
		// ex. 나이가 평균보다 많은 회원
		// select m from Member m where m.age > (select avg(m2.age) from Member m2)
		
		// ex. 한 건이라도 주문한 고객
		// select m from Member m where (select count(o) from Order o where m = o.member) > 0
	
		// ex. 팀 A 소속인 회원 (exists : 서브쿼리에 결과가 존재하면 참)
		// select m from Member m where exists (select t from m.team t where t.name = '팀A')
		
		// ex. 전체 상품 각각의 재고보다 주문량이 많은 주문들(ALL : 모두 만족하면 참)
		// select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)
	
		// 어떤 팀이든 팀에 소속된 회원(ANY : 조건을 하나라도 만족하면 참)
		// select m from Member m where m.team = ANY (select t from Team t)
	
		// JPA에서는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
		// SELECT 절도 가능 (하이버네이트에서 지원)
		// FROM 절 서브 쿼리는 불가능 -> join 으로 풀 수 있으면 풀어서 해결해야 한다 -> 안되면 쿼리 두 번으로 처리 / 애플리케이션으로 끌어와서 처리 -> 안되면 native 쓰든지 아니면 mybatis 등 
	}
	
	// 타입
	public void type() {
		Team team = new Team();
		team.setName("teamA");
		em.persist(team);
		
		Member member = new Member();
		member.setAge(10);
		member.setUsername("member1");
		member.changeTeam(team);
		em.persist(member);
		
		// 여러 타입 조회
		// 1. Query 타입으로 조회
//		Query query = em.createQuery("select m.username, 'HELLO', TRUE from Member m"
//				+ "where m.type = :userType"
//			);
//		query.setParameter("userType", RoleType.ADMIN);
		
		// List<Object[]> result 
//		List<Object[]> result = query.getResultList();
//		for(Object[] objects : result) {
//			System.out.println("objects = " + objects[0]);
//		}
		
		// 2. Object[] 타입으로 조회
		List resultList = em.createQuery("select m.username, m.age from Member m").getResultList();
		
		Object o = resultList.get(0);
		Object[] resultOb = (Object[]) o;
		System.out.println("result0 = " + resultOb[0]); // member1
		System.out.println("result1 = " + resultOb[1]); // 10
		
		List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m").getResultList();
		Object[] resultOb2 = resultList2.get(0);
		System.out.println("result0 = " + resultOb2[0]); // member1
		System.out.println("result1 = " + resultOb2[1]); // 10
		
		// 3. new 명령어로 조회 ; 가장 깔끔하다 -> 단순 값을 DTO로 바로 조회
		//List<MeberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from MemberDTO m ", MemberDTO.class).getResultList();
		List<MemberDTO> resultList3 = em.createQuery("select new jpa.basic.example.MemberDTO(m.username, m.age) from Member m ", MemberDTO.class).getResultList();
		
		MemberDTO memberDTO = resultList3.get(0);
		System.out.println("memberDTO0 = " + memberDTO.getUsername()); // member1
		System.out.println("memberDTO1 = " + memberDTO.getAge()); // 10

//		주의점: 엔티티가 아니기 때문에 new 키워드를 사용해 생성자를 사용하듯이 해야한다
//		패키지 경로를 다 적어줘야 한다(queryDSL에서는 import가능)
//		DTO에 생성자 만들어 줘야한다 (타입 순서 맞게)
		
	}
	
	// 조건식
	public void caseTest() {
		String query = "select" + 
							"	case when m.age <= 10 then '학생요금'" +
							"	when m.age >= 60 then '경로요금'" +
							"	else '일반요금'" +
							"	end" +
						"	from Member m";
		List<String> result = em.createQuery(query, String.class).getResultList();
		
		query = "select coalease(m.username, '이름 없는 회원') from Member m";
		result = em.createQuery(query, String.class).getResultList();
	}
	
	// JPQL 기본 함수
	public void fnTest() {
		String query = "select concat('a', 'b') from Member m";
		// "substring(m.username, 2, 3)"
		// "locate('de', 'abcdefg')"
		// trim, lower, upper, length, abs, sqrt, mod, 
		// size, index (JPA 용도)
	}
}
