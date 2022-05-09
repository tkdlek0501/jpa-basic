package jpa.basic.example.domain;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
	
	private String createBy; // 누가 생성했는지
	private LocalDateTime createdDate; // 생성 시간
	private String lastModifiedBy; // 마지막 수정한 사람
	private LocalDateTime lastModifiedDate; // 수정 시간
	
}
