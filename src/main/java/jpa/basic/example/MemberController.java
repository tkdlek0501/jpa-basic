package jpa.basic.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberRepository mr;
	
	@GetMapping("/test")
	public void createMember() {
//		mr.createMember();
//		mr.createParentWithChild();
//		mr.findMember();
	}
	
	@GetMapping("/jpqlTest")
	public void jpqlTest() {
		mr.type();
	}
	
	@GetMapping("/fetchJoinTest")
	public void fetchJoinTest() {
		mr.fetchJoinTest();
	}
	
	@GetMapping("/entity")
	public void entity() {
		mr.entity();
	} 
	
	@GetMapping("/namedQuery")
	public void namedQuery() {
		mr.namedQuery();
	}
	
	@GetMapping("/bulk")
	public void bulk() {
		mr.bulk();
	}
}
