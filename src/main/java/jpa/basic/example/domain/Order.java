package jpa.basic.example.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDERS") // DB에는 보통 order가 예약어라서 s 붙여줌
@Getter
@Setter
public class Order {
	
	@Id @GeneratedValue
	@Column(name = "order_id")
	private Long id;
	
//	@Column(name = "member_id") 
//	private Long memberId;     // 객체 지향적이지 못한 DB 중심 설계, 외래키만 가져올 수 있기 때문에 탐색 불가
	private Member member;
	
	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
}
