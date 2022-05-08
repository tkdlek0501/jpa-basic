package jpa.basic.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

// @Entity
@Getter
@Setter
public class Locker {
	
	@Id @GeneratedValue
	private Long id;
	
	private String name;
	
	@OneToOne(mappedBy = "locker")
	private Member member;
}
