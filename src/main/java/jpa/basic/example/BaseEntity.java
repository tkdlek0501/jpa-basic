package jpa.basic.example;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

// TODO: mapped superclass
// 공통되는 필드를 상속받아 사용하기 위해 (Team 클래스에서 상속)
// 상속관계 매핑 x, 엔티티 x, 조회 x, 추상 클래스를 권장

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
	
	private String createBy; // 누가 생성했는지
	private LocalDateTime createdDate; // 생성 시간
	private String lastModifiedBy; // 마지막 수정한 사람
	private LocalDateTime lastModifiedDate; // 수정 시간
	
}
