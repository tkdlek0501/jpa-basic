package jpa.basic.example;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
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
	
}
