package jpa.basic.example;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn // JOINED 전략에서 dtype 컬럼 생성
//TODO: 상속 관계 매핑
// 상속 관계 매핑 애노테이션; 전략 설정 해줄 수 있다
// InheritanceType
// JOINED : 조인 전략; 슈퍼타입 - 서브타입 으로 테이블 생성

public class Item {
	
	@Id @GeneratedValue
	private Long id; 
	
	private String name;
	private int price;
	
}

// JPA의 기본전략은 싱글 테이블이라서 한 테이블에 컬럼이 다 들어가게 된다(구분을 위한 dtype 이라는 컬럼 자동 생성됨)
// persist 할때 하위 엔티티를 넣어주면 item까지 persist 됨

// 싱글 테이블 전략
// 장점: join 필요 없어 조회 성능이 빠름
// 단점: 값이 안들어오는 null인 컬럼이 존재하게 됨, 하위 엔티티 종류가 많아지면 복잡하고 테이블이 커져 오히려 성능 저하가 일어날 수 있다

// 조인 전략
// 장점: 테이블 정규화, 저장공간 효율적
// 단점: 조회시 join이 복잡하므로 성능 저하, 데이터 저장시 insert sql 2번 호출

// 결론: 단순하다면 싱글 테이블, 복잡해서 구분하기 쉬우려면 조인 전략 사용
