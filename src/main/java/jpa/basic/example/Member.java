package jpa.basic.example;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity // jpa가 관리하는 클래스라는 것을 인식 시킴
@Getter
@Setter
public class Member {
	
	@Id // pk라는 것을 인식 시킴
	private Long id;
	
	private String name;
	
}
