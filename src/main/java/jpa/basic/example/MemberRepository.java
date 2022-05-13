package jpa.basic.example;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.hibernate.mapping.Collection;
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
	
	
// =============================================================================	
	
	// TODO: fetch join
	public void fetchJoinTest() {
		Team teamA = new Team();
		teamA.setName("팀A");
		em.persist(teamA);
		
		Team teamB = new Team();
		teamB.setName("팀B");
		em.persist(teamB);
		
		Member member1 = new Member();
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);
		
		Member member2 = new Member();
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);
		
		Member member3 = new Member();
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);
		
		em.flush();
		em.clear();
		
		// fetch join 사용 전
//		String query = "select m from Member m";
//		
//		List<Member> result = em.createQuery(query, Member.class)
//				.getResultList();
//		
//		for(Member member : result) {
//			System.out.println("member : " + member.getUsername() + " , " + member.getTeam().getName());
//		}
		// 회원1, 팀A(SQL에서 조회)
		// 회원2, 팀A(1차 캐시에서 조회)
		// 회원3, 팀B(SQL에서 조회)
		// N + 1 문제 발생
		
		// fetch join 사용 후
		String query = "select m from Member m join fetch m.team";
		
		List<Member> result = em.createQuery(query, Member.class)
				.getResultList();
		
		for(Member member : result) {
			System.out.println("member : " + member.getUsername() + " , " + member.getTeam().getName());
		}
		// SQL에 inner join으로 한 번에 조회 후 영속성 컨텍스트에 한 번에 들어감
		// team객체는 프록시가 아니라 실제 객체
		
		// 주의점
		// 다:1 은 문제가 없지만
		// 1:다 관계에서 데이터가 중복돼서 가져올 수 있다 (team 입장에서 member join하면 같은 team이 중복되는 경우)
		// JPQL의 DISTINCT 를 사용해서 해결할 수 있다
		// 기능: 1. SQL의 distinct 추가(완전 중복되는 row를 하나로), 2. 같은 식별자 가진 엔티티 중복 제거
		// distinct 적용 전
		String teamQuery = "select t from Team t join fetch t.members";
		
		List<Team> teamResult = em.createQuery(teamQuery, Team.class)
				.getResultList();
		
		System.out.println("teamResult : " + teamResult.size()); // team이 2개인데 member는 3개라서 3
		for(Team team : teamResult) {
			System.out.println("team : " + team.getName() + " , members.size" + team.getMembers().size());
			for(Member member : team.getMembers()) {
				System.out.println("-> member = " + member);
			}
		}
		
		// distinct 적용 후 
		String distinctTeamQuery = "select distinct t from Team t join fetch t.members";
		
		List<Team> distinctTeamResult = em.createQuery(distinctTeamQuery, Team.class)
				.getResultList();
		
		System.out.println("distinctTeamResult.size : " + distinctTeamResult.size()); // 2
		for(Team team : distinctTeamResult) {
			System.out.println("team : " + team.getName() + " , members.size" + team.getMembers().size());
			for(Member member : team.getMembers()) {
				System.out.println("-> member = " + member);
			}
		}
		
		// TODO: fetch join 과 일반 join의 차이
		// 일반 join 은 실행시 연관된 엔티티를 함께 조회하지 않음
		// JPQL 은 결과 반환시 연관 관계 고려 x, 단지 select 절에 지정한 엔티티만 조회한다
		// 팀 엔티티만 조회하고, 회원 엔티티는 조회 x
		// fetch join 사용할 때만 연관된 엔티티도 함께 조회되는 것(즉시 로딩)
		// 요약: JPQL에서는 fetch join을 사용할 때만 연관된 엔티티를 함께 조회한다 (그냥 join은 연관 관계 고려 x)
		// 또한 fetch join은 즉시 로딩이다
		
		// TODO: fetch join의 특징과 한계
		// 1. fetch join 대상에는 별칭을 줄 수 없다
		// ex. fetch join t.members m  이렇게 별칭(as) 주고
		// where 절에 m.age > 10 이런 조건을 주는 것은 안된다
		// 왜냐하면 team으로 부터 member 탐색시 member 전체가 조회되는 게 아니라 일부만 조회돼서 탐색에 누락이 생겨버린다
		// 객체 그래프를 탐색한다는 것은 전체를 조회한다는 개념 따라서 일부만 가져오고 싶다면 fetch join이 아니라 아예 따로 쿼리를 날려야 한다 
		// 2. 둘 이상의 컬렉션은 fetch join 할 수 없다
		// 1:다 도 데이터 중복이 생기는데 둘 이상이면 예상하기 어려운 join이 돼버린다
		// 3. 컬렉션(1:다) fetch join 하면 페이징 API 쓸 수 없다 (1:1, 다:1 은 페이징 가능)
	}
	
	// 엔티티 직접 사용
	public void entity() {
		Team teamA = new Team();
		teamA.setName("팀A");
		em.persist(teamA);
		
		Team teamB = new Team();
		teamB.setName("팀B");
		em.persist(teamB);
		
		Member member1 = new Member();
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);
		
		Member member2 = new Member();
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);
		
		Member member3 = new Member();
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);
		
		em.flush();
		em.clear();
		
		String query = "select m from Member m where m = :member";
		
		Member result = em.createQuery(query, Member.class)
				.setParameter("member", member1)
				.getSingleResult();
		
		// sql) select m from member m where m.id = member.id
		
		System.out.println("findMember : " + result);
		
		String teamQuery = "select m from Member m where m.team = :team";
		
		List<Member> result2 = em.createQuery(teamQuery, Member.class)
				.setParameter("team", teamA)
				.getResultList();
		
		for (Member member : result2) {
			System.out.println("member : " + member);
		}
	}
	
	// namedQuery
	public void namedQuery() {
		Team teamA = new Team();
		teamA.setName("팀A");
		em.persist(teamA);
		
		Team teamB = new Team();
		teamB.setName("팀B");
		em.persist(teamB);
		
		Member member1 = new Member();
		member1.setUsername("회원1");
		member1.setTeam(teamA);
		em.persist(member1);
		
		Member member2 = new Member();
		member2.setUsername("회원2");
		member2.setTeam(teamA);
		em.persist(member2);
		
		Member member3 = new Member();
		member3.setUsername("회원3");
		member3.setTeam(teamB);
		em.persist(member3);
		
		em.flush();
		em.clear();
		
		// Member 클래스에 정의한 namedQuery 문을 가져와서 쓸 수 있음
		// spring data jpa 에서는 @Query를 사용해서 메서드 위에 선언 가능
		em.createNamedQuery("Member.findByUsername", Member.class)
			.setParameter("username", "회원1")
			.getResultList();
	}
	
	// bulk 연산
	public void bulk() {
		Team teamA = new Team();
		teamA.setName("팀A");
		em.persist(teamA);
		
		Team teamB = new Team();
		teamB.setName("팀B");
		em.persist(teamB);
		
		Member member1 = new Member();
		member1.setUsername("회원1");
		member1.setAge(0);
		member1.setTeam(teamA);
		em.persist(member1);
		
		Member member2 = new Member();
		member2.setUsername("회원2");
		member2.setAge(0);
		member2.setTeam(teamA);
		em.persist(member2);
		
		Member member3 = new Member();
		member3.setUsername("회원3");
		member3.setAge(0);
		member3.setTeam(teamB);
		em.persist(member3);
		
//		em.flush();
//		em.clear();
		
		// flush  자동 호출
		int resultCount = em.createQuery("update Member m set m.age = 20")
			.executeUpdate(); //DB에는 반영 되지만 영속성 컨텍스트에는 반영되지 않음
		
		System.out.println("resultCount : " + resultCount);
		
		Member findMember1 = em.find(Member.class, member1.getId());
		
		System.out.println("before member1.getAge() : " + findMember1.getAge());
		
		em.clear(); // 영속성 컨텍스트 초기화
		
		Member findMember2 = em.find(Member.class, member1.getId());
		System.out.println("after member1.getAge() : " + findMember2.getAge());
	}
}
