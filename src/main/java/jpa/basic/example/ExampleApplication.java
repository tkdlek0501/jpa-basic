package jpa.basic.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ExampleApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

}

// TODO: 영속성 관리 설명
// 영속성 컨텍스트 : 엔티티를 영구 저장하는 환경 ; em의 persist함수로 영속화 시킬 수 있음
// 장점 : 1차캐시, 동일성 보장, 쓰기 지연, 변경 감지, 지연로딩

// 1차 캐시 
// 애플리케이션과 DB사이에 1차 캐시 공간을 만들어서 거쳐가도록 해서 DB에 접근하는 횟수를 줄일 수 있음
// 속도상 이점은 아주 찰나이기 때문에 그렇게 큰 이점을 얻을 수 있는 것은 아니다

// 동일성 보장
// 자바의 컬렉션과 비슷하게 '==' 비교를 하면 true가 나온다, 물론 같은 transaction 안에서 
// 이는 1차 캐시를 이용함으로써 가능하다 (new 하는 것이 아니기 때문)

// 쓰기 지연 (버퍼링)
// 실제 SQL 쿼리가 나가는 시점은 transaction이 커밋 되는 시점이다
// ex. persist로 1차 캐시에 저장되는 시점에 insert SQL을 만들어 쓰기 지연 SQL 저장소라는 공간에도 쌓아 놓는다
// 이 SQL들은 transaction 커밋 시점에 flush 되면서 DB에 나가게 된다

// 변경 감지 (dirty checking)
// 엔티티를 수정시 이용할 수 있는 기능
// transaction 안에서는 말 그대로 엔티티의 변경을 감지 한다 (마치 자바 컬렉션 다루듯이 값을 변경하면 바로 반영됨)
// 1. transaction 커밋 시점에 내부적으로 flush() 함수를 호출 
// 2. 엔티티와 스냅샷을 비교 (스냅샷 : 1차 캐시에 최초로 영속성 컨텍스르에 들어온 엔티티)
// 3. 변경을 감지하면 update SQL을 쓰기 지연 SQL 저장소에 저장

// =================================================================

// * flush 발생
// 영속성 컨텍스트의 변경 내용을 DB에 반영하는 것
// 영속성 컨텍스트를 비우는 게 아니라 변경 내용을 DB에 동기화하는 것
// 1. transaction 커밋 시점에 flush 발생되고 이때 변경 감지 (em.flush() 로 직접 호출 가능, JPQL 실행시에도 flush 됨)
// 2. 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
// 3. 쌓여있는 쓰기 지연 SQL들을 DB에 전송 

// * 준영속 상태
// 영속 상태의 엔티티가 영속성 컨텍스트에서 분리된 상태 
// 영속성 컨텍스트가 제공하는 기능 사용 못함
// em.detach(entity), em.clear, em.close 로 준영속 상태로 만듦

// + em.find는?
// 1차 캐시에 있다면 1차 캐시에서 가져오지만,
// 없다면 조회하는 쿼리가 DB에 바로 나간다 (transaction 커밋 시점에 나가는 게 아님)
// 조회해서 가져와 1차 캐시에 담겨진다 (스냅샷)
