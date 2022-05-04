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
		
		member.setId(1L);
		member.setName("nameA");
		
		em.persist(member);
		
		return member.getId(); 
	}
	
}
