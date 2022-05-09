package jpa.basic.example.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
public class Member {
	
	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String name;
	private String city;
	private String street;
	private String zipcode;
	
	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();
	// TODO: 1:다 관계
	// 연관관계가 양방향일 경우에 1 쪽에서 탐색이 필요하다면 1:다 를 써야하지만,
	// 단방향에서 1:다 관계는 지양하는 것이 좋다 
	// : DB입장에서는 어쨌든 fk를 가지고 있는 테이블(다 쪽)이 update가 돼야하므로
	// 다 쪽의 테이블을 건드리는 쿼리가 발생돼야 한다
	// 1:다 단방향 관계에서도 @JoinColumn을 꼭 사용해야 한다 그렇지 않으면 조인 테이블 방식을 사용 한다
	// 결론 : 다:1 연관관계만 매핑을 해주고 양방향 필요시에 1:다 를 추가해주는 설계를 해야 좋다
}
