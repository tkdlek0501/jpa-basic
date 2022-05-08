package jpa.basic.example;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

// @Entity
@Getter
@Setter
public class Team {
	
	@Id @GeneratedValue
	@Column(name = "team_id")
	private Long id;
	private String name;
	
	@OneToMany(mappedBy = "team") // 어떤 이름으로 mapping이 되어있는지 mappedBy로 설정해줘야 한다; member는 Team 객체를 'team'이란 이름의 참조 필드로 가지고 있음 
	private List<Member> members = new ArrayList<>();
	
	// TODO: 양방향 연관관계
	// 단방향 설계가 더 알아보기는 쉽다. 양방향은 꼭 필요한 경우에 추가해주는 것이 좋음
	// TODO: mappedBy 의 개념
	// 어떤 이름으로 매핑(참조) 되었는지 설정
	// 객체와 테이블간에 연관관계를 맺는 차이를 풀기 위한 jpa의 기능,
	// 객체의 연관관계는 참조 형태로 회원 -> 팀, 팀 -> 회원의 2개의 관계(2개의 단방향을 양방향이라 부름)
	// 테이블의 연관관계는 FK를 이용해서 회원 <-> 팀 1개의 관계 (기본적으로 양방향)
	// TODO: 연관관계의 주인 개념
	// 연관 관계가 있는 두 객체 중 어떤 객체가 외래키를 관리하여 등록/ 수정할 지 정해줘야 한다
	// 주인이 아닌 쪽은 읽기만 가능
	// 외래키가 있는 테이블이 연관 관계의 주인이 돼야 한다
	// 연관관계의 주인을 기준으로 쿼리가 나가도록 설계해야 한다 (반대쪽은 읽기 전용이라 반영 안됨)
	// 그러나 양방향 연관관계라면 양쪽 다 값을 세팅해주는 것이 객체 지향적이다 -> 그래서 연관관계 편의 메서드를 만드는 것
	// 결론: 양방향 연관관계에서는 연관관계 주인인 쪽을 기준으로 로직 설계를 하되, 연관관계 편의 메서드도 주인 쪽(주인 쪽이 아니더라도 한 쪽에만)에 만들어 이용하는게 편하고 좋다
	// TODO: 양방향 매핑시 주의점
	// 무한 루프를 조심해야 한다 ex. toString()
}
