package jpa.basic.example;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity // jpa가 관리하는 클래스라는 것을 인식 시킴
@Getter
@Setter
public class Member {
	
	@Id // pk라는 것을 인식 시킴
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성
	private Long id;
	
	// TODO: 기본키 생성 전략
	// 1. IDENTITY : 기본키 생성을 DB에 위임; MySQL의 경우에는 AUTO_INCREMENT
	// 2. SEQUENCE : DB sequence object를 사용, ORACLE / @SequenceGenerator 필요
	// 3. TABLE : 키 생성용 테이블 사용, 모든 DB에서 공용 / @TableGenerator 필요
	// 4. AUTO : 기본값, 자동 지정
	
	@Column(name = "name")
	private String username;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	// 양방향 연관관계시 편의 메서드 
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);// 반대쪽도 setting을 해준다		
	}
	
//	@OneToOne
//	@JoinColumn(name = "locker_id")
//	private Locker locker;
	
	private Integer age;
	
	@Enumerated(EnumType.STRING) // DB에는 Enum이 없으니 매핑을 위한 애노테이션 붙여줌
	private RoleType roleType;
	
	@Temporal(TemporalType.TIMESTAMP) // DB에서 TIMESTAMP = TIME + DATE
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	@Lob // DB에 varchar 를 넘어서는 용량을 넣기 위한 애노테이션
	private String description;

	@Override
	public String toString() {
		return "Member [id=" + id + ", username=" + username + ", age=" + age + ", roleType=" + roleType
				+ ", createDate=" + createDate + ", lastModifiedDate=" + lastModifiedDate + ", description="
				+ description + "]";
	}
	
}

// TODO: @Column 의 속성 (DDL은 애플리케이션 시작시 테이블 자동 생성을 의미)
// name : 필드와 매핑할 테이블의 컬럼 이름
// insertable, updatable : DB에 insert, update 반영할 컬럼인지 아닌지 설정
// nullable(DDL) : null 허용 하는지 false이면 not null
// unique(DDL) : 유니크 제약 조건 만들어 줌 but 이름이 랜덤하게 설정돼서 사용 x -> @Table 의 속성으로 대체
// length(DDL) : 문자 길이 제약조건, String 타입만 사용

// *@Enumerated의 EnumType는 반드시 STRING으로 설정해야 이름 그대로 DB에 들어감
// *@Temporal은 요즘 쓰지 않음 -> LocalDate or LocalDateTime 타입으로 대체 가능
